package com.example.minicpm_v_demo

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class PendingImageAttachment(
    val requestId: Long,
    val thumbnail: Bitmap,
    val imageInfo: String
)

sealed interface PendingImageUiState {
    data object Empty : PendingImageUiState
    data class LoadingPreview(val requestId: Long) : PendingImageUiState
    data class Preprocessing(
        val attachment: PendingImageAttachment
    ) : PendingImageUiState
    data class Ready(
        val attachment: PendingImageAttachment,
        val progressPercent: Int = 100
    ) : PendingImageUiState
    data object Clearing : PendingImageUiState
}

sealed interface PendingImageEvent {
    data class Error(val message: String) : PendingImageEvent
}

class PendingImageViewModel(application: Application) : AndroidViewModel(application) {

    private val appContext = application.applicationContext
    private val contentResolver = application.contentResolver
    private val engine by lazy { LlamaEngine.getInstance(appContext) }
    private val stateLock = Any()

    private val _uiState = MutableStateFlow<PendingImageUiState>(
        PendingImageUiState.Empty
    )
    val uiState: StateFlow<PendingImageUiState> = _uiState.asStateFlow()

    private val eventChannel = Channel<PendingImageEvent>(Channel.BUFFERED)
    val events: Flow<PendingImageEvent> = eventChannel.receiveAsFlow()

    private var nextRequestId = 1L
    private var activeRequestId: Long? = null
    private var processingJob: Job? = null

    fun controls(
        modelReady: Boolean,
        engineBusy: Boolean,
        videoProcessing: Boolean,
        hasText: Boolean
    ): ChatInputControls {
        val state = _uiState.value
        if (
            !modelReady ||
            engineBusy ||
            videoProcessing ||
            state is PendingImageUiState.Clearing
        ) {
            return ChatInputControls(
                textEnabled = false,
                sendEnabled = false,
                mediaEnabled = false,
                modelSettingsEnabled = false
            )
        }

        val isPreprocessing =
            state is PendingImageUiState.LoadingPreview ||
                state is PendingImageUiState.Preprocessing
        val hasPendingImage = isPreprocessing || state is PendingImageUiState.Ready
        return ChatInputControls(
            textEnabled = true,
            sendEnabled = hasText && !isPreprocessing,
            mediaEnabled = !hasPendingImage,
            modelSettingsEnabled = !hasPendingImage
        )
    }

    /**
     * Starts preprocessing and assumes ownership of [cameraCacheFile], if supplied.
     * The camera file is deleted after all decoder streams have closed, including
     * cancellation and failure paths.
     */
    fun start(uri: Uri, cameraCacheFile: File? = null): Boolean {
        val request = synchronized(stateLock) {
            if (
                processingJob != null ||
                _uiState.value !is PendingImageUiState.Empty
            ) {
                null
            } else {
                val requestId = nextRequestId++
                activeRequestId = requestId
                _uiState.value = PendingImageUiState.LoadingPreview(requestId)
                val job = viewModelScope.launch(
                    context = Dispatchers.IO,
                    start = CoroutineStart.LAZY
                ) {
                    preprocess(requestId, uri, cameraCacheFile)
                }
                processingJob = job
                requestId to job
            }
        }
        if (request == null) {
            deleteCameraCacheFile(cameraCacheFile)
            return false
        }

        val (_, job) = request
        job.start()
        return true
    }

    fun consumeReady(): PendingImageAttachment? =
        synchronized(stateLock) {
            val ready = _uiState.value as? PendingImageUiState.Ready
                ?: return@synchronized null
            activeRequestId = null
            processingJob = null
            _uiState.value = PendingImageUiState.Empty
            ready.attachment
        }

    /**
     * Cancels and joins preprocessing before returning. Callers can safely invoke
     * LlamaEngine.clearContext()/unloadModel() afterwards without racing a native
     * image prefill that was already in flight.
     */
    suspend fun cancelAndClear() {
        val job = synchronized(stateLock) {
            activeRequestId = null
            val current = processingJob
            _uiState.value = if (current == null) {
                PendingImageUiState.Empty
            } else {
                PendingImageUiState.Clearing
            }
            current
        }

        job?.cancelAndJoin()

        synchronized(stateLock) {
            if (processingJob === job) {
                processingJob = null
            }
            activeRequestId = null
            _uiState.value = PendingImageUiState.Empty
        }
    }

    /**
     * Synchronous local reset for a caller that has already reset or unloaded the
     * engine. It must not be used as a replacement for [cancelAndClear] while a
     * native prefill can still be running.
     */
    fun clearLocalAfterEngineReset() {
        synchronized(stateLock) {
            activeRequestId = null
            processingJob?.cancel()
            processingJob = null
            _uiState.value = PendingImageUiState.Empty
        }
    }

    private suspend fun preprocess(
        requestId: Long,
        uri: Uri,
        cameraCacheFile: File?
    ) {
        var modelBitmap: Bitmap? = null
        var encodedFile: File? = null
        try {
            validateSourceLength(uri)
            val metadata = readMetadata(uri)
            ensureCurrent(requestId)

            val thumbnail = decodeOrientedBitmap(
                uri = uri,
                metadata = metadata,
                maxDimension = THUMBNAIL_MAX_DIMENSION,
                maxPixelCount = THUMBNAIL_MAX_PIXEL_COUNT
            )
            ensureCurrent(requestId)

            val displayWidth = if (metadata.transform.rotationDegrees % 180 == 0) {
                metadata.width
            } else {
                metadata.height
            }
            val displayHeight = if (metadata.transform.rotationDegrees % 180 == 0) {
                metadata.height
            } else {
                metadata.width
            }
            val previewAttachment = PendingImageAttachment(
                requestId = requestId,
                thumbnail = thumbnail,
                imageInfo = "$displayWidth x $displayHeight"
            )
            publishIfCurrent(
                requestId,
                PendingImageUiState.Preprocessing(previewAttachment)
            )

            modelBitmap = decodeOrientedBitmap(
                uri = uri,
                metadata = metadata,
                maxDimension = ImageDecodePolicy.MAX_DIMENSION,
                maxPixelCount = ImageDecodePolicy.MAX_PIXEL_COUNT
            )
            ensureCurrent(requestId)
            check(ImageDecodePolicy.isPixelCountAllowed(
                width = modelBitmap.width,
                height = modelBitmap.height
            )) {
                appContext.getString(R.string.error_image_too_large)
            }

            encodedFile = encodeToPrivateCache(modelBitmap)
            val encodedSize = encodedFile.length()
            if (!ImageDecodePolicy.isSourceLengthAllowed(encodedSize)) {
                val errorResource = if (
                    encodedSize > ImageDecodePolicy.MAX_SOURCE_BYTES
                ) {
                    R.string.error_image_too_large
                } else {
                    R.string.error_decode_image
                }
                throw IOException(appContext.getString(errorResource))
            }
            val preparedAttachment = previewAttachment.copy(
                imageInfo = "${modelBitmap.width} x ${modelBitmap.height} " +
                    "(${encodedSize / 1024} KB)"
            )
            publishIfCurrent(
                requestId,
                PendingImageUiState.Preprocessing(preparedAttachment)
            )

            modelBitmap.recycle()
            modelBitmap = null
            val encodedBytes = encodedFile.readBytes()
            ensureCurrent(requestId)
            engine.prefillImage(encodedBytes)
            ensureCurrent(requestId)

            synchronized(stateLock) {
                if (activeRequestId == requestId) {
                    processingJob = null
                    _uiState.value = PendingImageUiState.Ready(preparedAttachment)
                }
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: OutOfMemoryError) {
            failRequest(
                requestId,
                appContext.getString(R.string.error_decode_image)
            )
        } catch (error: Exception) {
            failRequest(
                requestId,
                error.localizedMessage
                    ?: appContext.getString(R.string.error_decode_image)
            )
        } finally {
            modelBitmap?.takeUnless { it.isRecycled }?.recycle()
            encodedFile?.let(::deletePreparedCacheFile)
            deleteCameraCacheFile(cameraCacheFile)
        }
    }

    private fun validateSourceLength(uri: Uri) {
        val sourceLength = contentResolver.openAssetFileDescriptor(uri, "r")?.use {
            it.length
        } ?: -1L
        if (!ImageDecodePolicy.isSourceLengthAllowed(sourceLength)) {
            val errorResource = if (
                sourceLength > ImageDecodePolicy.MAX_SOURCE_BYTES
            ) {
                R.string.error_image_too_large
            } else {
                R.string.error_read_image
            }
            throw IOException(appContext.getString(errorResource))
        }
    }

    private fun readMetadata(uri: Uri): ImageMetadata {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        contentResolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input, null, bounds)
        } ?: throw IOException(appContext.getString(R.string.error_read_image))
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            throw IOException(appContext.getString(R.string.error_decode_image))
        }

        val orientation = try {
            contentResolver.openInputStream(uri)?.use { input ->
                ExifInterface(input).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            } ?: ExifInterface.ORIENTATION_NORMAL
        } catch (_: IOException) {
            ExifInterface.ORIENTATION_NORMAL
        }
        return ImageMetadata(
            width = bounds.outWidth,
            height = bounds.outHeight,
            transform = ExifOrientationPolicy.transformFor(orientation)
        )
    }

    private fun decodeOrientedBitmap(
        uri: Uri,
        metadata: ImageMetadata,
        maxDimension: Int,
        maxPixelCount: Long
    ): Bitmap {
        val options = BitmapFactory.Options().apply {
            inSampleSize = ImageDecodePolicy.sampleSizeFor(
                width = metadata.width,
                height = metadata.height,
                maxDimension = maxDimension,
                maxPixelCount = maxPixelCount
            )
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        val decoded = contentResolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input, null, options)
        } ?: throw IOException(appContext.getString(R.string.error_decode_image))
        return applyExifTransform(decoded, metadata.transform)
    }

    private fun applyExifTransform(
        bitmap: Bitmap,
        transform: ExifOrientationTransform
    ): Bitmap {
        if (
            transform.rotationDegrees == 0 &&
            !transform.mirrorHorizontal
        ) {
            return bitmap
        }

        val matrix = Matrix().apply {
            if (transform.rotationDegrees != 0) {
                postRotate(transform.rotationDegrees.toFloat())
            }
            if (transform.mirrorHorizontal) {
                postScale(-1f, 1f)
            }
        }
        return try {
            Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            ).also { transformed ->
                if (transformed !== bitmap) {
                    bitmap.recycle()
                }
            }
        } catch (error: Exception) {
            bitmap.recycle()
            throw error
        }
    }

    private fun encodeToPrivateCache(bitmap: Bitmap): File {
        val cacheDirectory = File(
            appContext.cacheDir,
            PREPARED_CACHE_DIRECTORY
        )
        if (!cacheDirectory.exists() && !cacheDirectory.mkdirs()) {
            throw IOException(appContext.getString(R.string.error_decode_image))
        }
        val canonicalDirectory = cacheDirectory.canonicalFile
        val format = if (bitmap.hasAlpha()) {
            Bitmap.CompressFormat.PNG
        } else {
            Bitmap.CompressFormat.JPEG
        }
        val suffix = if (format == Bitmap.CompressFormat.PNG) ".png" else ".jpg"
        val outputFile = File.createTempFile(
            PREPARED_FILE_PREFIX,
            suffix,
            canonicalDirectory
        )
        check(outputFile.canonicalFile.parentFile == canonicalDirectory) {
            appContext.getString(R.string.error_decode_image)
        }

        try {
            FileOutputStream(outputFile).use { output ->
                if (!bitmap.compress(format, JPEG_QUALITY, output)) {
                    throw IOException(appContext.getString(R.string.error_decode_image))
                }
            }
            return outputFile
        } catch (error: Exception) {
            outputFile.delete()
            throw error
        }
    }

    private fun deletePreparedCacheFile(file: File) {
        try {
            val cacheDirectory = File(
                appContext.cacheDir,
                PREPARED_CACHE_DIRECTORY
            ).canonicalFile
            val target = file.canonicalFile
            if (target.parentFile == cacheDirectory && target.isFile) {
                target.delete()
            }
        } catch (_: IOException) {
            // Best-effort cleanup in the app-private cache.
        }
    }

    private fun deleteCameraCacheFile(file: File?) {
        if (file == null) return
        try {
            val cameraDirectory = File(
                appContext.cacheDir,
                CAMERA_CACHE_DIRECTORY
            ).canonicalFile
            val target = file.canonicalFile
            if (target.parentFile == cameraDirectory && target.isFile) {
                target.delete()
            }
        } catch (_: IOException) {
            // Best-effort cleanup; never delete outside cache/camera.
        }
    }

    private fun failRequest(requestId: Long, message: String) {
        synchronized(stateLock) {
            if (activeRequestId == requestId) {
                activeRequestId = null
                processingJob = null
                _uiState.value = PendingImageUiState.Empty
                eventChannel.trySend(PendingImageEvent.Error(message))
            }
        }
    }

    private suspend fun ensureCurrent(requestId: Long) {
        currentCoroutineContext().ensureActive()
        if (!isCurrent(requestId)) {
            throw CancellationException("Pending image request was replaced")
        }
    }

    private fun publishIfCurrent(
        requestId: Long,
        state: PendingImageUiState
    ) {
        synchronized(stateLock) {
            if (activeRequestId == requestId) {
                _uiState.value = state
            }
        }
    }

    private fun isCurrent(requestId: Long): Boolean =
        synchronized(stateLock) {
            activeRequestId == requestId
        }

    private data class ImageMetadata(
        val width: Int,
        val height: Int,
        val transform: ExifOrientationTransform
    )

    companion object {
        const val THUMBNAIL_MAX_DIMENSION = 512
        const val THUMBNAIL_MAX_PIXEL_COUNT = 512L * 512L

        private const val PREPARED_CACHE_DIRECTORY = "pending-images"
        private const val PREPARED_FILE_PREFIX = "prepared-"
        private const val CAMERA_CACHE_DIRECTORY = "camera"
        private const val JPEG_QUALITY = 95
    }
}
