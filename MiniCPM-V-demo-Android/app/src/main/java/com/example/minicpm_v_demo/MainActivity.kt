package com.example.minicpm_v_demo

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import io.noties.markwon.Markwon
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class MainActivity : StatusBarVisibleActivity() {

    private val pendingImageViewModel: PendingImageViewModel by viewModels()

    private lateinit var recyclerChat: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var etInput: TextInputEditText
    private lateinit var btnSend: ImageButton
    private lateinit var btnImage: ImageButton
    private lateinit var btnCamera: ImageButton
    private lateinit var btnSettings: ImageButton
    private lateinit var cardInputBar: View
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var tvTitle: TextView
    private lateinit var pendingImagePanel: View
    private lateinit var ivPendingImage: ImageView
    private lateinit var pendingImageScrim: View
    private lateinit var progressPendingImage: CircularProgressIndicator
    private lateinit var tvPendingImageStatus: TextView
    private lateinit var tvPendingImageInfo: TextView

    private lateinit var engine: LlamaEngine
    private var generationJob: Job? = null
    private var localGuardJob: Job? = null
    private var videoProcessingJob: Job? = null
    private var isModelReady = false
    private var isProcessingVideo = false
    private var isSubmitting = false
    private var isClearing = false
    private var hasAutoLoaded = false
    private var loadedModelId: String? = null
    private var messageIdCounter = 1L
    private val messages = mutableListOf<ChatMessage>()
    private var createdWithLocale: String? = null
    private var isLocaleRestart = false
    private var currentEngineState: LlamaState = LlamaState.Uninitialized
    private var pendingCameraUri: Uri? = null
    private var pendingCameraFile: File? = null
    private val originalImageCache by lazy {
        ImageSourceCache(
            File(cacheDir, PendingImageViewModel.SOURCE_CACHE_DIRECTORY),
            ImageDecodePolicy.MAX_SOURCE_BYTES
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createdWithLocale = LocaleManager.currentLanguage(this).tag
        restorePendingCameraCapture(savedInstanceState)

        // If the selected model is a TTS model, redirect to TtsActivity immediately.
        // The chat interface is only meaningful for LLM/VLM models.
        if (shouldRedirectToTts()) {
            startActivity(Intent(this, TtsActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        // Edge-to-edge: pad the root content for status/nav bars and the IME
        // so the bottom input bar follows the soft keyboard up. Without this,
        // targetSdk=35+ draws content behind the IME and the input bar gets
        // covered.
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val rootContent = findViewById<View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(rootContent) { v, insets ->
            val sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            v.updatePadding(
                left = sysBars.left,
                top = sysBars.top,
                right = sysBars.right,
                bottom = maxOf(sysBars.bottom, ime.bottom)
            )
            insets
        }

        LlamaEngine.migrateLegacyLayoutIfNeeded(applicationContext)

        initViews()
        setupRecyclerView()
        setupClickListeners()
        observePendingImage()
        initEngine()
    }

    private fun initViews() {
        recyclerChat = findViewById(R.id.recycler_chat)
        etInput = findViewById(R.id.et_input)
        btnSend = findViewById(R.id.btn_send)
        btnImage = findViewById(R.id.btn_image)
        btnCamera = findViewById(R.id.btn_camera)
        btnSettings = findViewById(R.id.btn_settings)
        cardInputBar = findViewById(R.id.card_input_bar)
        appBarLayout = findViewById(R.id.appBarLayout)
        tvTitle = findViewById(R.id.tv_title)
        pendingImagePanel = findViewById(R.id.pending_image_panel)
        ivPendingImage = findViewById(R.id.iv_pending_image)
        pendingImageScrim = findViewById(R.id.pending_image_scrim)
        progressPendingImage = findViewById(R.id.progress_pending_image)
        tvPendingImageStatus = findViewById(R.id.tv_pending_image_status)
        tvPendingImageInfo = findViewById(R.id.tv_pending_image_info)
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(Markwon.create(this))
        chatAdapter.setOnStopClick {
            if (localGuardJob?.isActive == true) {
                localGuardJob?.cancel()
            } else {
                engine.cancelGeneration()
            }
        }
        chatAdapter.setOnImageClick(::openOriginalImage)
        chatAdapter.setOnWelcomeAction(::handleWelcomeAction)

        recyclerChat.layoutManager = LinearLayoutManager(this)
        recyclerChat.adapter = chatAdapter

        cardInputBar.viewTreeObserver.addOnGlobalLayoutListener {
            recyclerChat.setPadding(
                recyclerChat.paddingLeft,
                recyclerChat.paddingTop,
                recyclerChat.paddingRight,
                cardInputBar.height
            )
        }

        val selectedModel = LlamaEngine.getSelectedModel(applicationContext)
        messages.add(
            ChatMessage.WelcomeCard(
                isTextOnly = selectedModel.isTextOnly,
                hasVisualContext = false
            )
        )
        chatAdapter.submitList(messages.toList())
    }

    private fun handleWelcomeAction(action: WelcomeAction) {
        when (action) {
            is WelcomeAction.SendPrompt -> {
                if (isModelReady && !isProcessingVideo) {
                    etInput.setText(action.prompt)
                    handleUserInput()
                } else if (!isModelReady) {
                    Toast.makeText(
                        this,
                        R.string.toast_load_model_first,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(this, R.string.toast_wait_video, Toast.LENGTH_SHORT).show()
                }
            }
            WelcomeAction.PickMedia -> startVisualInput {
                getMedia.launch(arrayOf("image/*", "video/*"))
            }
            WelcomeAction.TakePhoto -> startVisualInput(::launchCameraCapture)
        }
    }

    private fun startVisualInput(action: () -> Unit) {
        when {
            !isModelReady || currentEngineState !is LlamaState.ModelReady ->
                Toast.makeText(this, R.string.toast_load_model_first, Toast.LENGTH_SHORT).show()
            isProcessingVideo ->
                Toast.makeText(this, R.string.toast_wait_video, Toast.LENGTH_SHORT).show()
            pendingImageViewModel.uiState.value !is PendingImageUiState.Empty ->
                Toast.makeText(
                    this,
                    R.string.toast_wait_image_preprocessing,
                    Toast.LENGTH_SHORT
                ).show()
            !engine.isVisionSupported ->
                Toast.makeText(this, R.string.toast_load_model_first, Toast.LENGTH_SHORT).show()
            else -> action()
        }
    }

    private fun setupClickListeners() {
        // Pick image OR video.  iOS demo's HXPhotoPicker exposes both
        // photo and video in a single picker; on Android we ask SAF
        // for either MIME, so the user gets the same "pick anything
        // viewable" affordance with no extra "video" button.  Video is
        // only fed to the model if the loaded model is V-4.6 (gated in
        // [handleSelectedMedia] / [LlamaEngine.isVideoUnderstandingSupported]).
        btnImage.setOnClickListener { getMedia.launch(arrayOf("image/*", "video/*")) }
        btnCamera.setOnClickListener { launchCameraCapture() }
        btnSend.setOnClickListener { handleUserInput() }
        btnSettings.setOnClickListener { showChatSettingsDialog() }
        ivPendingImage.setOnClickListener {
            val token = when (val state = pendingImageViewModel.uiState.value) {
                is PendingImageUiState.Preprocessing ->
                    state.attachment.originalImageToken
                is PendingImageUiState.Ready ->
                    state.attachment.originalImageToken
                else -> null
            }
            token?.let(::openOriginalImage)
        }

        etInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                collapseAppBar()
                scrollToBottom()
            }
        }
        etInput.doAfterTextChanged { refreshInputControls() }
    }

    private fun observePendingImage() {
        lifecycleScope.launch {
            pendingImageViewModel.uiState.collect { state ->
                renderPendingImage(state)
                refreshInputControls()
            }
        }
        lifecycleScope.launch {
            pendingImageViewModel.events.collect { event ->
                when (event) {
                    is PendingImageEvent.Error -> Toast.makeText(
                        this@MainActivity,
                        getString(R.string.toast_image_failed, event.message),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun collapseAppBar() {
        appBarLayout.setExpanded(false, true)
    }

    private fun scrollToBottom() {
        recyclerChat.post {
            val adapterCount = chatAdapter.itemCount
            if (adapterCount == 0) return@post
            val layoutManager = recyclerChat.layoutManager as? LinearLayoutManager ?: return@post
            val lastView = layoutManager.findViewByPosition(adapterCount - 1)
            if (lastView != null) {
                val offset = recyclerChat.height - recyclerChat.paddingBottom - lastView.height
                layoutManager.scrollToPositionWithOffset(adapterCount - 1, offset.coerceAtMost(0))
            } else {
                recyclerChat.scrollToPosition(adapterCount - 1)
            }
        }
    }

    private fun showClearChatDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.clear_chat)
            .setMessage(R.string.clear_chat_confirm)
            .setPositiveButton(R.string.confirm) { _, _ ->
                clearChat()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showChatSettingsDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_chat_settings, null, false)
        val rowModelManagement = view.findViewById<View>(R.id.row_model_management)
        val rowImageSlice = view.findViewById<View>(R.id.row_image_slice)
        val rowClearChat = view.findViewById<View>(R.id.row_clear_chat)
        val selectedModel = LlamaEngine.getSelectedModel(applicationContext)

        view.findViewById<TextView>(R.id.tv_settings_model_summary).text =
            getString(R.string.settings_model_summary, selectedModel.displayName)
        view.findViewById<TextView>(R.id.tv_settings_slice_summary).text =
            getString(
                R.string.settings_slice_summary,
                LlamaEngine.getImageMaxSliceNums(this)
            )

        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.chat_settings)
            .setView(view)
            .setNegativeButton(android.R.string.cancel, null)
            .create()

        val modelManagementEnabled = isModelManagerSafe()
        val imageSliceEnabled = canChangeImageSlices()
        val clearChatEnabled = canClearCurrentChat()
        rowImageSlice.visibility = if (
            ::engine.isInitialized && engine.isVisionSupported
        ) {
            View.VISIBLE
        } else {
            View.GONE
        }
        rowModelManagement.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, ModelManagerActivity::class.java))
        }
        rowImageSlice.setOnClickListener {
            dialog.dismiss()
            showImageSliceDialog()
        }
        rowClearChat.setOnClickListener {
            dialog.dismiss()
            showClearChatDialog()
        }
        setSettingsRowEnabled(rowModelManagement, modelManagementEnabled)
        setSettingsRowEnabled(rowImageSlice, imageSliceEnabled)
        setSettingsRowEnabled(rowClearChat, clearChatEnabled)
        dialog.show()
    }

    private fun setSettingsRowEnabled(row: View, enabled: Boolean) {
        row.isEnabled = enabled
        row.isClickable = enabled
        row.alpha = if (enabled) 1f else 0.38f
    }

    /**
     * Pops up the slice-cap picker.  The slider drives a live preview of
     * the selected value; only on dialog "confirm" do we persist + push
     * the value to native.  Cancel = no-op.
     *
     * Live update path is cheap (no mmproj reload), but we still gate it
     * behind a confirm step so users don't accidentally regenerate cached
     * embeddings while dragging the knob.
     */
    private fun showImageSliceDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_image_slice, null, false)
        val slider = view.findViewById<com.google.android.material.slider.Slider>(R.id.slider_image_slice)
        val tvValue = view.findViewById<android.widget.TextView>(R.id.tv_image_slice_value)

        val initial = LlamaEngine.getImageMaxSliceNums(this)
        slider.value = initial.toFloat()
        tvValue.text = initial.toString()
        slider.addOnChangeListener { _, value, _ -> tvValue.text = value.toInt().toString() }

        AlertDialog.Builder(this)
            .setTitle(R.string.image_slice_dialog_title)
            .setView(view)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val chosen = slider.value.toInt()
                lifecycleScope.launch {
                    engine.setImageMaxSliceNums(chosen)
                    val msgRes = if (engine.isVisionSupported) {
                        R.string.image_slice_apply_toast
                    } else {
                        R.string.image_slice_pending_toast
                    }
                    Toast.makeText(this@MainActivity, getString(msgRes, chosen), Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun clearChatUI() {
        pendingImageViewModel.clearLocalAfterEngineReset()
        releaseMessageOriginals()
        messages.clear()
        val selectedModel = LlamaEngine.getSelectedModel(applicationContext)
        messages.add(
            ChatMessage.WelcomeCard(
                isTextOnly = selectedModel.isTextOnly,
                hasVisualContext = false
            )
        )
        messageIdCounter = 1L
        chatAdapter.submitList(messages.toList())
    }

    private fun openOriginalImage(token: String) {
        startActivity(OriginalImageViewerActivity.intent(this, token))
    }

    private fun releaseMessageOriginals() {
        messages.asSequence()
            .filterIsInstance<ChatMessage.UserMessage>()
            .mapNotNull { it.originalImageToken }
            .distinct()
            .forEach(originalImageCache::deleteToken)
    }

    private fun clearChat() {
        if (isClearing) return
        isClearing = true
        refreshInputControls()

        lifecycleScope.launch {
            try {
                pendingImageViewModel.cancelAndClear()
                videoProcessingJob?.cancelAndJoin()
                videoProcessingJob = null
                withContext(Dispatchers.IO) {
                    engine.clearContext()
                }
                clearChatUI()
                Toast.makeText(
                    this@MainActivity,
                    R.string.clear_chat_toast,
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Log.e(TAG, "Error clearing context", e)
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.toast_clear_chat_failed, e.message),
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                isClearing = false
                refreshInputControls()
            }
        }
    }

    private fun initEngine() {
        lifecycleScope.launch(Dispatchers.Default) {
            engine = LlamaEngine.getInstance(applicationContext)
            withContext(Dispatchers.Main) {
                observeEngineState()
                observeVisualContext()
            }
        }
    }

    private fun observeEngineState() {
        lifecycleScope.launch {
            engine.state.collect { state ->
                currentEngineState = state
                when (state) {
                    is LlamaState.Uninitialized,
                    is LlamaState.Initializing -> {
                        isModelReady = false
                    }
                    is LlamaState.Initialized -> {
                        isModelReady = false
                        if (!hasAutoLoaded) {
                            hasAutoLoaded = true
                            loadDefaultModel()
                        }
                    }
                    is LlamaState.LoadingModel -> {
                        isModelReady = false
                    }
                    is LlamaState.ModelReady -> {
                        isModelReady = true
                        loadedModelId = LlamaEngine.getSelectedModel(applicationContext).id
                        updateUIForModelType()
                    }
                    is LlamaState.ProcessingSystemPrompt,
                    is LlamaState.ProcessingUserPrompt,
                    is LlamaState.Generating -> {
                        isModelReady = true
                    }
                    is LlamaState.PrefillingImage -> {
                        isModelReady = true
                    }
                    is LlamaState.UnloadingModel -> {
                        isModelReady = false
                    }
                    is LlamaState.Error -> {
                        isModelReady = false
                    }
                }
                refreshInputControls()
            }
        }
    }

    private fun observeVisualContext() {
        lifecycleScope.launch {
            engine.hasVisualContext.collect {
                refreshWelcomeCard(
                    LlamaEngine.getSelectedModel(applicationContext).isTextOnly
                )
            }
        }
    }

    private fun refreshInputControls() {
        if (!::etInput.isInitialized) return

        val engineBusy = isSubmitting || isClearing || when (currentEngineState) {
            is LlamaState.ModelReady,
            is LlamaState.PrefillingImage -> false
            else -> true
        }
        val controls = pendingImageViewModel.controls(
            modelReady = isModelReady,
            engineBusy = engineBusy,
            videoProcessing = isProcessingVideo,
            hasText = etInput.text?.toString()?.isNotBlank() == true
        )
        val visionSupported = ::engine.isInitialized && engine.isVisionSupported
        val modelManagerSafe = isModelManagerSafe()
        val clearChatSafe = canClearCurrentChat()

        etInput.isEnabled = controls.textEnabled
        btnSend.isEnabled = controls.sendEnabled
        btnImage.isEnabled = controls.mediaEnabled && visionSupported
        btnCamera.isEnabled = controls.mediaEnabled && visionSupported
        btnSettings.isEnabled = modelManagerSafe || clearChatSafe
    }

    private fun isModelManagerSafe(): Boolean {
        val hasPendingImage =
            pendingImageViewModel.uiState.value !is PendingImageUiState.Empty
        return !hasPendingImage && !isSubmitting && !isClearing &&
            !isProcessingVideo &&
            when (currentEngineState) {
                is LlamaState.LoadingModel,
                is LlamaState.ProcessingSystemPrompt,
                is LlamaState.ProcessingUserPrompt,
                is LlamaState.PrefillingImage,
                is LlamaState.Generating,
                is LlamaState.UnloadingModel -> false
                else -> true
            }
    }

    private fun canChangeImageSlices(): Boolean =
        ::engine.isInitialized && engine.isVisionSupported && isModelManagerSafe()

    private fun canClearCurrentChat(): Boolean =
        isModelReady && !isSubmitting && !isClearing &&
            (currentEngineState is LlamaState.ModelReady ||
                currentEngineState is LlamaState.PrefillingImage)

    private fun shouldRedirectToTts(): Boolean {
        val model = LlamaEngine.getSelectedModel(applicationContext)
        return model.isTts
    }

    private fun updateUIForModelType() {
        val model = LlamaEngine.getSelectedModel(applicationContext)
        val isVision = engine.isVisionSupported

        tvTitle.setText(if (isVision) R.string.app_title else R.string.app_title_text)
        btnImage.visibility = if (isVision) View.VISIBLE else View.GONE
        btnCamera.visibility = if (isVision) View.VISIBLE else View.GONE
        refreshWelcomeCard(model.isTextOnly)
        refreshInputControls()
    }

    private fun refreshWelcomeCard(isTextOnly: Boolean) {
        val welcomeIndex = messages.indexOfFirst { it is ChatMessage.WelcomeCard }
        if (welcomeIndex >= 0) {
            messages[welcomeIndex] = ChatMessage.WelcomeCard(
                isTextOnly = isTextOnly,
                hasVisualContext = ::engine.isInitialized && engine.hasVisualContext.value
            )
            chatAdapter.submitList(messages.toList())
        }
    }

    private fun loadDefaultModel() {
        val ctx = applicationContext
        val model = LlamaEngine.getSelectedModel(ctx)
        val ggufFile = File(LlamaEngine.modelPath(ctx))
        val mmprojPathStr = LlamaEngine.mmprojPath(ctx)
        val mmprojFile = mmprojPathStr?.let { File(it) }

        val ggufMissing = !ggufFile.exists()
        val mmprojMissing = !model.isTextOnly && (mmprojFile == null || !mmprojFile.exists())

        if (ggufMissing || mmprojMissing) {
            if (ModelDownloadPromptPolicy.shouldPrompt(
                    ggufMissing = ggufMissing,
                    mmprojMissing = mmprojMissing,
                    downloadRunning = ModelDownloadController.isRunning
                )
            ) {
                promptDownloadModels(
                    ggufMissing = ggufMissing,
                    mmprojMissing = mmprojMissing
                )
            }
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val mmprojArg = if (mmprojFile != null && mmprojFile.exists()) mmprojFile.absolutePath else null
                engine.loadModel(ggufFile.absolutePath, mmprojArg)
                loadedModelId = model.id
            } catch (e: Exception) {
                Log.e(TAG, "Error loading model", e)
                engine.resetToInitialized()
                hasAutoLoaded = false
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, getString(R.string.toast_model_load_failed, e.message), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun promptDownloadModels(ggufMissing: Boolean, mmprojMissing: Boolean) {
        val message = when {
            ggufMissing && mmprojMissing ->
                getString(R.string.download_prompt_all_missing)
            mmprojMissing ->
                getString(R.string.download_prompt_mmproj_missing)
            else ->
                getString(R.string.download_prompt_incomplete)
        }
        AlertDialog.Builder(this)
            .setTitle(R.string.download_prompt_title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.go_download) { _, _ ->
                startActivity(Intent(this, ModelManagerActivity::class.java))
            }
            .setNegativeButton(R.string.later) { _, _ ->
                Toast.makeText(
                    this,
                    R.string.download_prompt_hint,
                    Toast.LENGTH_LONG
                ).show()
            }
            .show()
    }

    private val getMedia = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { handleSelectedMedia(it) }
    }

    private val takePicture = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { captured ->
        val uri = pendingCameraUri
        val file = pendingCameraFile
        pendingCameraUri = null
        pendingCameraFile = null
        if (captured && uri != null && file != null) {
            handleSelectedImage(uri, file)
        } else {
            deleteCameraCacheFile(file)
        }
    }

    private fun handleSelectedMedia(uri: Uri) {
        if (!isModelReady || currentEngineState !is LlamaState.ModelReady) {
            Toast.makeText(this, R.string.toast_load_model_first, Toast.LENGTH_SHORT).show()
            return
        }
        if (pendingImageViewModel.uiState.value !is PendingImageUiState.Empty) {
            Toast.makeText(this, R.string.toast_wait_image_preprocessing, Toast.LENGTH_SHORT).show()
            return
        }
        val mime = contentResolver.getType(uri).orEmpty()
        when {
            mime.startsWith("video/") -> handleSelectedVideo(uri)
            mime.startsWith("image/") || mime.isEmpty() -> handleSelectedImage(uri)
            else -> {
                Toast.makeText(this, getString(R.string.toast_unsupported_file, mime), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun launchCameraCapture() {
        if (!isModelReady || currentEngineState !is LlamaState.ModelReady) {
            Toast.makeText(this, R.string.toast_load_model_first, Toast.LENGTH_SHORT).show()
            return
        }
        if (pendingImageViewModel.uiState.value !is PendingImageUiState.Empty) {
            Toast.makeText(this, R.string.toast_wait_image_preprocessing, Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val cameraDir = File(cacheDir, CAMERA_CACHE_DIRECTORY)
            if (!cameraDir.exists() && !cameraDir.mkdirs()) {
                throw IOException(getString(R.string.error_create_camera_file))
            }
            val captureFile = File.createTempFile("capture-", ".jpg", cameraDir)
            val captureUri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                captureFile
            )
            pendingCameraFile = captureFile
            pendingCameraUri = captureUri
            takePicture.launch(captureUri)
        } catch (e: ActivityNotFoundException) {
            Log.e(TAG, "No camera app can handle image capture", e)
            clearPendingCameraCapture()
            Toast.makeText(
                this,
                getString(R.string.toast_camera_failed, e.localizedMessage ?: "No camera app"),
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            Log.e(TAG, "Unable to create camera capture", e)
            clearPendingCameraCapture()
            Toast.makeText(
                this,
                getString(
                    R.string.toast_camera_failed,
                    e.localizedMessage ?: getString(R.string.error_create_camera_file)
                ),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun handleSelectedImage(uri: Uri, cameraCacheFile: File? = null) {
        if (!pendingImageViewModel.start(uri, cameraCacheFile)) {
            Toast.makeText(this, R.string.toast_wait_image_preprocessing, Toast.LENGTH_SHORT).show()
            return
        }
        renderPendingImage(pendingImageViewModel.uiState.value)
        refreshInputControls()
    }

    private fun renderPendingImage(
        state: PendingImageUiState = pendingImageViewModel.uiState.value
    ) {
        if (state is PendingImageUiState.Empty) {
            pendingImagePanel.visibility = View.GONE
            ivPendingImage.setImageDrawable(null)
            return
        }

        pendingImagePanel.visibility = View.VISIBLE
        val attachment = when (state) {
            is PendingImageUiState.Preprocessing -> state.attachment
            is PendingImageUiState.Ready -> state.attachment
            else -> null
        }
        if (attachment == null) {
            ivPendingImage.setImageDrawable(null)
            tvPendingImageInfo.setText(R.string.image_preprocessing)
        } else {
            ivPendingImage.setImageBitmap(attachment.thumbnail)
            tvPendingImageInfo.text = attachment.imageInfo
        }

        when (state) {
            is PendingImageUiState.LoadingPreview,
            is PendingImageUiState.Preprocessing,
            PendingImageUiState.Clearing -> {
                pendingImageScrim.visibility = View.VISIBLE
                progressPendingImage.visibility = View.VISIBLE
                progressPendingImage.isIndeterminate = true
                tvPendingImageStatus.setText(R.string.image_preprocessing_wait)
            }
            is PendingImageUiState.Ready -> {
                pendingImageScrim.visibility = View.GONE
                progressPendingImage.visibility = View.GONE
                tvPendingImageStatus.setText(R.string.image_ready_view_original)
            }
            PendingImageUiState.Empty -> Unit
        }
        ivPendingImage.isClickable = attachment != null
    }

    private fun restorePendingCameraCapture(savedInstanceState: Bundle?) {
        val uriText = savedInstanceState?.getString(STATE_CAMERA_URI) ?: return
        val savedFileName = savedInstanceState.getString(STATE_CAMERA_FILE_NAME) ?: return
        if (savedFileName != File(savedFileName).name) return

        val restoredUri = Uri.parse(uriText)
        if (
            restoredUri.scheme != "content" ||
            restoredUri.authority != "${packageName}.fileprovider"
        ) {
            return
        }
        val restoredFile = File(File(cacheDir, CAMERA_CACHE_DIRECTORY), savedFileName)
        if (!restoredFile.isFile) return

        pendingCameraUri = restoredUri
        pendingCameraFile = restoredFile
    }

    private fun clearPendingCameraCapture() {
        deleteCameraCacheFile(pendingCameraFile)
        pendingCameraUri = null
        pendingCameraFile = null
    }

    private fun deleteCameraCacheFile(file: File?) {
        if (file == null) return
        try {
            val cameraDir = File(cacheDir, CAMERA_CACHE_DIRECTORY).canonicalFile
            val target = file.canonicalFile
            if (target.parentFile == cameraDir && target.isFile && !target.delete()) {
                Log.w(TAG, "Unable to delete camera cache file: ${target.name}")
            }
        } catch (e: IOException) {
            Log.w(TAG, "Unable to resolve camera cache file", e)
        }
    }

    /**
     * Video-understanding pipeline (iOS-equivalent
     * MBHomeViewController+CaptureVideo.processVideoFrame):
     * extract up to 64 uniformly-sampled frames off the IO dispatcher,
     * append a single chat cell with the first frame as thumbnail,
     * then hand the frames to [LlamaEngine.prefillVideoFrames] which
     * loops `prefillImage(...)` under a temporary slice=1 cap.
     *
     * Gated to MiniCPM-V-4.6 because that's where iOS enables the
     * feature and where the native nCtx bump to 8192 takes effect
     * (see prepare() in llama_jni.cpp).
     */
    private fun handleSelectedVideo(uri: Uri) {
        if (!engine.isVideoUnderstandingSupported) {
            Toast.makeText(this,
                R.string.video_only_v46,
                Toast.LENGTH_LONG).show()
            return
        }

        isProcessingVideo = true
        val msgId = messageIdCounter++
        refreshInputControls()
        videoProcessingJob = lifecycleScope.launch(Dispatchers.IO) {
            val startNs = System.nanoTime()
            var completed = false
            var failure: Exception? = null
            try {
                val extracted = VideoFrameExtractor.extract(applicationContext, uri)
                val info = VideoFrameExtractor.formatVideoInfo(applicationContext, extracted)
                Log.i(TAG, "Video info: $info")

                withContext(Dispatchers.Main) {
                    val videoMessage = ChatMessage.UserMessage(
                        id = msgId,
                        text = "",
                        imageBitmap = extracted.thumbnail,
                        imageInfo = info,
                        isPrefilling = true,
                        isVideo = true
                    )
                    messages.add(videoMessage)
                    chatAdapter.submitList(messages.toList()) {
                        scrollToBottom()
                    }
                }

                engine.prefillVideoFrames(extracted.frames) { current, total ->
                    withContext(Dispatchers.Main) {
                        val index = messages.indexOfFirst { it.id == msgId }
                        if (index >= 0) {
                            val cur = messages[index] as ChatMessage.UserMessage
                            messages[index] = cur.copy(
                                imageInfo = getString(R.string.video_processing_progress, info, current, total)
                            )
                            chatAdapter.submitList(messages.toList())
                        }
                    }
                }

                val elapsedMs = (System.nanoTime() - startNs) / 1_000_000
                withContext(Dispatchers.Main) {
                    val index = messages.indexOfFirst { it.id == msgId }
                    if (index >= 0) {
                        val cur = messages[index] as ChatMessage.UserMessage
                        messages[index] = cur.copy(
                            imageInfo = getString(R.string.video_preprocessing_done, info, elapsedMs / 1000.0),
                            isPrefilling = false
                        )
                        chatAdapter.submitList(messages.toList())
                    }
                }
                completed = true
            } catch (e: CancellationException) {
                Log.i(TAG, "Video preprocessing was cancelled")
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Error processing video", e)
                failure = e
            } finally {
                withContext(NonCancellable + Dispatchers.Main) {
                    isProcessingVideo = false
                    if (!completed) {
                        val index = messages.indexOfFirst { it.id == msgId }
                        if (index >= 0) {
                            messages.removeAt(index)
                            chatAdapter.submitList(messages.toList())
                        }
                    }
                    videoProcessingJob = null
                    refreshInputControls()
                    failure?.let { error ->
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.toast_video_failed, error.message),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun handleUserInput() {
        val userMsg = etInput.text.toString().trim()
        if (userMsg.isEmpty()) {
            Toast.makeText(this, R.string.toast_empty_input, Toast.LENGTH_SHORT).show()
            return
        }
        val pendingState = pendingImageViewModel.uiState.value
        if (
            pendingState is PendingImageUiState.LoadingPreview ||
            pendingState is PendingImageUiState.Preprocessing ||
            pendingState is PendingImageUiState.Clearing
        ) {
            Toast.makeText(this, R.string.toast_wait_image_preprocessing, Toast.LENGTH_SHORT).show()
            return
        }
        if (
            !isModelReady ||
            currentEngineState !is LlamaState.ModelReady ||
            isSubmitting ||
            isClearing
        ) {
            Toast.makeText(this, R.string.toast_load_model_first, Toast.LENGTH_SHORT).show()
            return
        }

        val dispatchPlan = LocalGuardReplyPolicy.plan(engine.evaluateVisualPrompt(userMsg))
        if (dispatchPlan.destination == PromptDestination.LOCAL_ONLY) {
            showLocalGuardReply(userMsg, requireNotNull(dispatchPlan.localReplyKind))
            return
        }

        val attachment = if (pendingState is PendingImageUiState.Ready) {
            pendingImageViewModel.consumeReady().also { consumed ->
                if (consumed == null) {
                    Log.e(TAG, "Ready pending image could not be consumed")
                }
            }
        } else {
            null
        }
        if (pendingState is PendingImageUiState.Ready && attachment == null) {
            Toast.makeText(
                this,
                getString(R.string.toast_image_failed, getString(R.string.error_read_image)),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        etInput.clearFocus()
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(etInput.windowToken, 0)

        etInput.text = null
        isSubmitting = true
        refreshInputControls()

        collapseAppBar()

        val msgId = messageIdCounter++
        val userMessage = ChatMessage.UserMessage(
            id = msgId,
            text = userMsg,
            imageBitmap = attachment?.thumbnail,
            imageInfo = attachment?.imageInfo,
            originalImageToken = attachment?.originalImageToken
        )
        renderPendingImage(pendingImageViewModel.uiState.value)
        messages.add(userMessage)
        chatAdapter.submitList(messages.toList()) {
            scrollToBottom()
        }

        val aiMsgId = messageIdCounter++
        val aiMessage = ChatMessage.AiMessage(id = aiMsgId, text = "", isGenerating = true)
        messages.add(aiMessage)
        chatAdapter.setActiveAiMessage(aiMsgId)
        chatAdapter.submitList(messages.toList()) {
            scrollToBottom()
        }

        val generationHadVisualContext = engine.hasVisualContext.value
        generationJob = lifecycleScope.launch(Dispatchers.Default) {
            val fullResponse = StringBuilder()
            try {
                engine.sendUserPrompt(userMsg)
                    .collect { token ->
                        fullResponse.append(token)
                        if (generationHadVisualContext) {
                            withContext(Dispatchers.Main) {
                                val currentText = fullResponse.toString()
                                val index = messages.indexOfFirst { it.id == aiMsgId }
                                if (index >= 0) {
                                    messages[index] = ChatMessage.AiMessage(
                                        id = aiMsgId,
                                        text = currentText,
                                        isGenerating = true
                                    )
                                }
                                chatAdapter.updateStreamingText(aiMsgId, currentText)
                                scrollToBottom()
                            }
                        }
                    }
            } catch (e: CancellationException) {
                Log.i(TAG, "Text generation was cancelled")
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Text generation failed", e)
            } finally {
                withContext(NonCancellable + Dispatchers.Main) {
                    val index = messages.indexOfFirst { it.id == aiMsgId }
                    val candidateResponse = fullResponse.toString()
                    val responseDecision = engine.evaluateVisualResponse(
                        response = candidateResponse,
                        hadVisualContext = generationHadVisualContext
                    )
                    val displayedResponse = when (responseDecision) {
                        VisualResponseDecision.ALLOW -> candidateResponse
                        VisualResponseDecision.BLOCK_VISUAL_ASSERTION,
                        VisualResponseDecision.BLOCK_UNCERTAIN_ASSERTION -> {
                            Log.w(
                                TAG,
                                "Generated response hidden by visual grounding guard: " +
                                    responseDecision.name
                            )
                            getString(R.string.response_blocked_no_visual_context)
                        }
                    }
                    if (index >= 0) {
                        val current = messages[index] as? ChatMessage.AiMessage
                        messages[index] = (current ?: aiMessage).copy(
                            text = displayedResponse,
                            isGenerating = false
                        )
                    }
                    chatAdapter.setGeneratingDone(aiMsgId)
                    chatAdapter.clearActiveAiMessage()
                    chatAdapter.submitList(messages.toList())
                    isSubmitting = false
                    generationJob = null
                    refreshInputControls()
                    if (index >= 0) {
                        scrollToBottom()
                    }
                }
            }
        }
    }

    private fun showLocalGuardReply(userMessageText: String, kind: LocalGuardReplyKind) {
        val replyText = getString(
            when (kind) {
                LocalGuardReplyKind.NO_VISUAL_CONTEXT ->
                    R.string.response_blocked_no_visual_context
                LocalGuardReplyKind.UNCERTAIN_VISUAL_REQUEST ->
                    R.string.response_uncertain_visual_request
            }
        )

        etInput.clearFocus()
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(etInput.windowToken, 0)
        etInput.text = null
        isSubmitting = true
        refreshInputControls()
        collapseAppBar()

        val userMessage = ChatMessage.UserMessage(
            id = messageIdCounter++,
            text = userMessageText
        )
        val aiMessageId = messageIdCounter++
        val aiMessage = ChatMessage.AiMessage(
            id = aiMessageId,
            text = "",
            isGenerating = true
        )
        messages.add(userMessage)
        messages.add(aiMessage)
        chatAdapter.setActiveAiMessage(aiMessageId)
        chatAdapter.submitList(messages.toList()) {
            scrollToBottom()
        }

        localGuardJob = lifecycleScope.launch {
            var displayedText = ""
            try {
                for (frame in LocalResponseStreamer.frames(replyText)) {
                    displayedText = frame
                    val index = messages.indexOfFirst { it.id == aiMessageId }
                    if (index >= 0) {
                        messages[index] = aiMessage.copy(text = frame)
                    }
                    chatAdapter.updateStreamingText(aiMessageId, frame)
                    scrollToBottom()
                    delay(LOCAL_GUARD_FRAME_DELAY_MS)
                }
            } finally {
                val index = messages.indexOfFirst { it.id == aiMessageId }
                if (index >= 0) {
                    messages[index] = aiMessage.copy(
                        text = displayedText,
                        isGenerating = false
                    )
                }
                chatAdapter.setGeneratingDone(aiMessageId)
                chatAdapter.clearActiveAiMessage()
                chatAdapter.submitList(messages.toList())
                isSubmitting = false
                localGuardJob = null
                refreshInputControls()
                if (index >= 0) {
                    scrollToBottom()
                }
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is TextInputEditText) {
                val barRect = android.graphics.Rect()
                cardInputBar.getGlobalVisibleRect(barRect)
                if (!barRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onResume() {
        super.onResume()
        val currentTag = LocaleManager.currentLanguage(this).tag
        if (createdWithLocale != null && createdWithLocale != currentTag) {
            isLocaleRestart = true
            LocaleManager.recreateSeamlessly(this)
            return
        }
        // Re-check: if the model was switched to a TTS model while this
        // activity was in the background, redirect to TtsActivity.
        if (shouldRedirectToTts()) {
            startActivity(Intent(this, TtsActivity::class.java))
            finish()
            return
        }
        if (!::engine.isInitialized) return
        val selectedId = LlamaEngine.getSelectedModel(applicationContext).id

        if (loadedModelId != null && loadedModelId != selectedId) {
            loadedModelId = null
            hasAutoLoaded = false
            reloadAfterModelSwitch()
        } else if (LlamaEngine.consumeModelSwitched(applicationContext)) {
            loadedModelId = selectedId
            clearChatUI()
            updateUIForModelType()
        }
    }

    private fun reloadAfterModelSwitch() {
        if (isClearing) return
        isClearing = true
        isModelReady = false
        refreshInputControls()
        lifecycleScope.launch {
            try {
                pendingImageViewModel.cancelAndClear()
                withContext(Dispatchers.IO) {
                    if (engine.state.value is LlamaState.ModelReady) {
                        engine.unloadModel()
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error unloading during model switch", e)
            } finally {
                isClearing = false
                clearChatUI()
                loadDefaultModel()
                refreshInputControls()
            }
        }
    }

    override fun onStop() {
        generationJob?.cancel()
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        pendingCameraUri?.let { outState.putString(STATE_CAMERA_URI, it.toString()) }
        pendingCameraFile?.let { outState.putString(STATE_CAMERA_FILE_NAME, it.name) }
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        if (isFinishing) {
            clearPendingCameraCapture()
            if (!isLocaleRestart) {
                releaseMessageOriginals()
            }
        }
        if (isFinishing && !isLocaleRestart && ::engine.isInitialized) {
            engine.destroy()
        }
        super.onDestroy()
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val CAMERA_CACHE_DIRECTORY = "camera"
        private const val STATE_CAMERA_URI = "pending_camera_uri"
        private const val STATE_CAMERA_FILE_NAME = "pending_camera_file_name"
        private const val LOCAL_GUARD_FRAME_DELAY_MS = 24L
    }
}
