package com.example.negative_screen.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.FrameLayout
import android.widget.Toast
import com.example.negative_screen.databinding.LayoutNegativeViewBinding
import kotlin.math.abs

class NegativeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var overlayProgress = 0f

    interface DragCallback {
        fun onDrag(progress: Float)
        fun onRelease(progress: Float)
    }

    var dragCallback: DragCallback? = null

    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop

    private var downX = 0f
    private var downY = 0f

    private var dragging = false

    private var binding: LayoutNegativeViewBinding
        = LayoutNegativeViewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        initListener()
    }

    private fun initListener() {
        binding.centerView.setOnClickListener {
            Toast.makeText(context, "hhh", Toast.LENGTH_SHORT).show()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {

        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = ev.rawX
                downY = ev.rawY
                dragging = false
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = ev.rawX - downX
                val dy = ev.rawY - downY

                val isHorizontal = abs(dx) > abs(dy)
                val canClose = overlayProgress >= 0.98f

                // 左滑才触发关闭
                if (!dragging && canClose && isHorizontal && dx < -touchSlop) {
                    dragging = true
                }

                if (dragging) {
                    val width = width.toFloat()
                    // 左滑关闭模型
                    val progress = (1f + dx / width).coerceIn(0f, 1f)
                    dragCallback?.onDrag(progress)
                    return true
                }
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                if (dragging) {
                    val width = width.toFloat()
                    val dx = ev.rawX - downX
                    val progress = (1f + dx / width).coerceIn(0f, 1f)
                    dragCallback?.onRelease(progress)
                    dragging = false
                    return true
                }
            }
        }

        return super.dispatchTouchEvent(ev)
    }
}
