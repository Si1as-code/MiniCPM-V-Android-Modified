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
    fun modelManagerToolbarStartsBelowStatusBar() {
        val toolbarBelowStatusBar = AtomicBoolean(false)
        val verified = CountDownLatch(1)

        ActivityScenario.launch(ModelManagerActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val toolbar = activity.findViewById<View>(R.id.toolbar)
                toolbar.post {
                    val insets = ViewCompat.getRootWindowInsets(activity.window.decorView)
                    val statusBarHeight = insets
                        ?.getInsets(WindowInsetsCompat.Type.statusBars())
                        ?.top ?: 0
                    val location = IntArray(2)
                    toolbar.getLocationOnScreen(location)
                    toolbarBelowStatusBar.set(
                        statusBarHeight > 0 && location[1] >= statusBarHeight
                    )
                    verified.countDown()
                }
            }

            assertTrue(verified.await(5, TimeUnit.SECONDS))
            assertTrue(
                "The model manager toolbar must start below the status bar",
                toolbarBelowStatusBar.get()
            )
        }
    }

    @Test
    fun chatScreenStartsBelowVisibleStatusBarAndHasPendingImagePanel() {
        val statusBarVisible = AtomicBoolean(false)
        val verified = CountDownLatch(1)

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val pendingPanel = activity.findViewById<View>(R.id.pending_image_panel)
                val settingsButton = activity.findViewById<View>(R.id.btn_settings)
                val title = activity.findViewById<View>(R.id.tv_title)
                val cameraButton = activity.findViewById<View>(R.id.btn_camera)
                val sendButton = activity.findViewById<View>(R.id.btn_send)
                val preprocessingStatus = activity.findViewById<View>(R.id.tv_pending_image_status)

                assertNotNull(pendingPanel)
                assertNotNull(settingsButton)
                assertNotNull(title)
                assertNotNull(cameraButton)
                assertNotNull(sendButton)
                assertNotNull(preprocessingStatus)
                assertTrue(pendingPanel.visibility == View.GONE)

                val parent = cameraButton.parent as ViewGroup
                assertTrue(parent.indexOfChild(cameraButton) < parent.indexOfChild(sendButton))

                val settingsLocation = IntArray(2)
                val titleLocation = IntArray(2)
                settingsButton.getLocationOnScreen(settingsLocation)
                title.getLocationOnScreen(titleLocation)
                assertTrue(
                    "The unified settings entry must be left of the title",
                    settingsLocation[0] < titleLocation[0]
                )

                activity.window.decorView.post {
                    val insets = ViewCompat.getRootWindowInsets(activity.window.decorView)
                    statusBarVisible.set(
                        insets != null &&
                            insets.isVisible(WindowInsetsCompat.Type.statusBars())
                    )
                    verified.countDown()
                }
            }

            assertTrue(verified.await(5, TimeUnit.SECONDS))
            assertTrue("The Android status bar must remain visible", statusBarVisible.get())
        }
    }
}
