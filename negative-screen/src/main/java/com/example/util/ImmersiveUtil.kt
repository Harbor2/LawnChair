package com.example.util

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

fun Activity.immersiveWindow(rootView: View, isDarkMode: Boolean, vararg views: View?) {
    try {
        window.decorView.systemUiVisibility = if (isDarkMode) {
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        } else {
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.BLACK

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            views.forEach { viewItem ->
                viewItem?.let {
                    val params = it.layoutParams as ViewGroup.MarginLayoutParams
                    params.topMargin = systemInsets.top
                }
            }

            view.setPadding(
                view.paddingLeft,
                0,
                view.paddingRight,
                systemInsets.bottom
            )
            WindowInsetsCompat.CONSUMED
        }
        // 让系统回调生效
        rootView.requestApplyInsets()
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}
