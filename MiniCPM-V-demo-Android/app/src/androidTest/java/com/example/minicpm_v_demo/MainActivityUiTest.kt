package com.example.minicpm_v_demo

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

@RunWith(AndroidJUnit4::class)
class MainActivityUiTest {

    @Test
    fun chatScreenStartsWithHiddenStatusBarAndPendingImagePanel() {
        val statusBarHidden = AtomicBoolean(false)
        val verified = CountDownLatch(1)

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val pendingPanel = activity.findViewById<View>(R.id.pending_image_panel)
                val cameraButton = activity.findViewById<View>(R.id.btn_camera)
                val sendButton = activity.findViewById<View>(R.id.btn_send)

                assertNotNull(pendingPanel)
                assertNotNull(cameraButton)
                assertNotNull(sendButton)
                assertTrue(pendingPanel.visibility == View.GONE)

                val parent = cameraButton.parent as ViewGroup
                assertTrue(parent.indexOfChild(cameraButton) < parent.indexOfChild(sendButton))

                activity.window.decorView.post {
                    val insets = ViewCompat.getRootWindowInsets(activity.window.decorView)
                    statusBarHidden.set(
                        insets != null &&
                            !insets.isVisible(WindowInsetsCompat.Type.statusBars())
                    )
                    verified.countDown()
                }
            }

            assertTrue(verified.await(5, TimeUnit.SECONDS))
            assertTrue("The Android status bar must be hidden", statusBarHidden.get())
        }
    }
}
