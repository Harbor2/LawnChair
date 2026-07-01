package com.example.negative_screen

import android.animation.ValueAnimator
import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.WindowManager
import android.widget.FrameLayout
import android.util.Log
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.doOnEnd
import com.example.model.TAG
import com.google.android.libraries.launcherclient.ILauncherOverlay
import com.google.android.libraries.launcherclient.ILauncherOverlayCallback

class NegativeScreenService : Service() {

    private var mOverlayView: NegativeView? = null

    private var mWindowManager: WindowManager? = null

    private var mCallback: ILauncherOverlayCallback? = null

    private var mCurrentLayoutParams: WindowManager.LayoutParams? = null

    private val mainHandler = Handler(Looper.getMainLooper())

    private var currentProgress = 0f

    private var isOpened = false

    private var snapAnimator: ValueAnimator? = null

    private var dragOpenThreshold = 0.3f
    private var dragCloseThreshold = 0.7f

    private val binder = object : ILauncherOverlay.Stub() {

        override fun startScroll() {
            snapAnimator?.cancel()
        }

        override fun onScroll(progress: Float) {
            currentProgress = progress.coerceIn(0f, 1f)
            mOverlayView?.overlayProgress = currentProgress
            mainHandler.post {
                val width = resources.displayMetrics.widthPixels.toFloat()
                mOverlayView?.translationX = -width * (1f - currentProgress)
            }
            try {
                mCallback?.overlayScrollChanged(progress)
            } catch (_: Exception) {
            }
        }

        override fun endScroll() {
            Log.d(TAG, "endScroll progress=$currentProgress")
            val targetOpen = currentProgress >= dragOpenThreshold
            mainHandler.post {
                animateToState(targetOpen)
            }
        }

        override fun windowAttached(
            lp: WindowManager.LayoutParams?,
            cb: ILauncherOverlayCallback?,
            flags: Int
        ) {
            Log.d(TAG, "windowAttached flags=$flags")

            mainHandler.post {
                if (lp == null) {
                    return@post
                }

                mCallback = cb
                val localLp = WindowManager.LayoutParams()
                localLp.copyFrom(lp)
                localLp.type = WindowManager.LayoutParams.TYPE_APPLICATION
                // 不允许触摸
                localLp.flags = localLp.flags or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                mCurrentLayoutParams = localLp

                try {
                    if (mOverlayView?.parent == null) {
                        mWindowManager?.addView(mOverlayView, localLp)
                    }
                    cb?.overlayStatusChanged(1)
                } catch (_: Exception) {
                }
            }
        }

        override fun windowAttached2(
            bundle: Bundle?,
            cb: ILauncherOverlayCallback?
        ) {
            bundle?.classLoader = WindowManager.LayoutParams::class.java.classLoader
            val lp = bundle?.getParcelable<WindowManager.LayoutParams>(
                    "layout_params"
                )
            windowAttached(lp, cb, 0)
        }

        override fun windowDetached(isChangingConfigurations: Boolean) {
            mainHandler.post {
                snapAnimator?.cancel()
                try {
                    mOverlayView?.let {
                        if (it.parent != null) {
                            mWindowManager?.removeView(it)
                        }
                    }
                } catch (_: Exception) {
                }
            }
        }

        override fun openOverlay(flags: Int) {
            mainHandler.post {
                animateToState(true)
            }
        }

        override fun closeOverlay(flags: Int) {
            mainHandler.post {
                animateToState(false)
            }
        }

        override fun hasOverlayContent() = true
        override fun getVoiceSearchLanguage() = "en-US"
        override fun isVoiceDetectionRunning() = false
        override fun unusedMethod() {}
        override fun setActivityState(flags: Int) {}
        override fun startSearch(data: ByteArray?, bundle: Bundle?) = false
        override fun onPause() {}
        override fun onResume() {}
        override fun requestVoiceDetection(start: Boolean) {}
    }

    override fun onCreate() {
        super.onCreate()
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        mOverlayView = NegativeView(this)

        mOverlayView?.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )

        mOverlayView?.translationX =
            -resources.displayMetrics.widthPixels.toFloat()

        registerDragCallback()
    }

    private fun registerDragCallback() {

        mOverlayView?.dragCallback = object : NegativeView.DragCallback {

                override fun onDrag(progress: Float) {
                    currentProgress = progress
                    mOverlayView?.overlayProgress = progress

                    val width = resources.displayMetrics.widthPixels.toFloat()
                    mOverlayView?.translationX = -width * (1f - progress)
                    try {
                        mCallback?.overlayScrollChanged(progress)
                    } catch (_: Exception) {
                    }
                }

                override fun onRelease(progress: Float) {
                    currentProgress = progress
                    val targetOpen = progress >= dragCloseThreshold
                    animateToState(targetOpen)
                }
            }
    }

    private fun setTouchable(touchable: Boolean) {
        Log.d(TAG, "setTouchable： touchable=${touchable}")
        val lp = mCurrentLayoutParams ?: return
        val wm = mWindowManager ?: return
        val view = mOverlayView ?: return

        val oldFlags = lp.flags

        if (touchable) {
            lp.flags = lp.flags and WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE.inv()
            lp.flags = lp.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv()
        } else {
            lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        }

        if (oldFlags != lp.flags && view.parent != null) {
            try {
                wm.updateViewLayout(view, lp)
            } catch (e: Exception) {
                Log.e(TAG, "updateViewLayout failed", e)
            }
        }
    }

    private fun animateToState(open: Boolean) {
        Log.d(TAG, "animateToState： open=${open}")
        val view = mOverlayView ?: return
        if (!open) {
            setTouchable(false)
        }

        snapAnimator?.cancel()
        val width = resources.displayMetrics.widthPixels.toFloat()
        val startProgress = currentProgress
        val targetProgress = if (open) 1f else 0f

        snapAnimator = ValueAnimator.ofFloat(startProgress, targetProgress).apply {
                duration = 250
                interpolator = DecelerateInterpolator()
                addUpdateListener {
                    val progress = it.animatedValue as Float
                    currentProgress = progress
                    mOverlayView?.overlayProgress = progress
                    view.translationX = -width * (1f - progress)
                    try {
                        mCallback?.overlayScrollChanged(progress)
                    } catch (_: Exception) {
                    }
                }

                doOnEnd {
                    isOpened = open
                    currentProgress = if (open) 1f else 0f
                    mOverlayView?.overlayProgress = currentProgress

                    if (open) {
                        setTouchable(true)
                    } else {
                        setTouchable(false)
                    }
                    try {
                        mCallback?.overlayScrollChanged(currentProgress)
                    } catch (_: Exception) {
                    }
                }
                start()
            }
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "onBind")
        return binder
    }
}
