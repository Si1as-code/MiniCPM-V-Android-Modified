package com.example.minicpm_v_demo

import android.content.Context
import android.os.SystemClock
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.io.File
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.ceil

@RunWith(AndroidJUnit4::class)
class LlamaCheckpointInstrumentedTest {
    @Test
    fun restoringCheckpointReproducesPositionHistoryAndNextToken() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val engine = readyEngine(context)

        engine.clearContext()
        engine.replayHistoryMessage(ModelHistoryRole.USER, "Remember that the office code is blue seven.")
        engine.replayHistoryMessage(ModelHistoryRole.ASSISTANT, "I will remember it.")
        val stable = engine.nativeContextDebugSnapshot()

        val saveTimesMs = mutableListOf<Double>()
        val restoreTimesMs = mutableListOf<Double>()
        var measuredSizeBytes = 0L
        repeat(20) {
            val saveStart = SystemClock.elapsedRealtimeNanos()
            val checkpoint = engine.beginEphemeralTurn()
            saveTimesMs += (SystemClock.elapsedRealtimeNanos() - saveStart) / 1_000_000.0
            measuredSizeBytes = checkpoint.sizeBytes

            val restoreStart = SystemClock.elapsedRealtimeNanos()
            engine.restoreEphemeralTurn(checkpoint)
            restoreTimesMs += (SystemClock.elapsedRealtimeNanos() - restoreStart) / 1_000_000.0
        }
        val saveP50 = percentile(saveTimesMs, 0.50)
        val saveP95 = percentile(saveTimesMs, 0.95)
        val restoreP50 = percentile(restoreTimesMs, 0.50)
        val restoreP95 = percentile(restoreTimesMs, 0.95)
        println(
            "CHECKPOINT_BENCHMARK sizeBytes=$measuredSizeBytes " +
                "saveP50Ms=$saveP50 saveP95Ms=$saveP95 " +
                "restoreP50Ms=$restoreP50 restoreP95Ms=$restoreP95"
        )
        assertTrue("Checkpoint save P95 exceeded 500 ms: $saveP95", saveP95 < 500.0)
        assertTrue("Checkpoint restore P95 exceeded 500 ms: $restoreP95", restoreP95 < 500.0)
        assertEquals(stable, engine.nativeContextDebugSnapshot())

        val firstCheckpoint = engine.beginEphemeralTurn()
        assertTrue(firstCheckpoint.sizeBytes in 1..(256L * 1024L * 1024L))
        val firstToken = engine.sendUserPrompt("What is two plus two?", predictLength = 8)
            .take(1)
            .toList()
            .single()
        assertTrue(engine.nativeContextDebugSnapshot().currentPosition > stable.currentPosition)
        engine.restoreEphemeralTurn(firstCheckpoint)
        assertEquals(stable, engine.nativeContextDebugSnapshot())

        val secondCheckpoint = engine.beginEphemeralTurn()
        val repeatedFirstToken = engine.sendUserPrompt("What is two plus two?", predictLength = 8)
            .take(1)
            .toList()
            .single()
        engine.restoreEphemeralTurn(secondCheckpoint)

        assertEquals(firstToken, repeatedFirstToken)
        assertEquals(stable, engine.nativeContextDebugSnapshot())
    }

    private suspend fun readyEngine(context: Context): LlamaEngine {
        val engine = LlamaEngine.getInstance(context)
        val initializedState = withTimeout(30_000) {
            engine.state.first { state: LlamaState ->
                state is LlamaState.Initialized || state is LlamaState.ModelReady || state is LlamaState.Error
            }
        }
        check(initializedState !is LlamaState.Error) { "Native initialization failed" }
        if (initializedState is LlamaState.Initialized) {
            val model = File(LlamaEngine.modelPath(context))
            check(model.isFile) { "Production model is not installed: ${model.absolutePath}" }
            val mmproj = LlamaEngine.mmprojPath(context)?.let(::File)?.takeIf(File::isFile)
            withTimeout(180_000) {
                engine.loadModel(model.absolutePath, mmproj?.absolutePath)
            }
        }
        return engine
    }

    private fun percentile(values: List<Double>, fraction: Double): Double {
        val sorted = values.sorted()
        val index = (ceil(sorted.size * fraction).toInt() - 1).coerceIn(sorted.indices)
        return sorted[index]
    }
}
