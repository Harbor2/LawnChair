package com.example.negative_screen

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.FrameLayout
import com.example.negative_screen.databinding.LayoutNegativeViewBinding
import com.example.util.FlashUtil
import com.example.util.LauncherUtil
import com.wyz.emlibrary.TAG
import com.wyz.emlibrary.util.EMUtil
import kotlin.math.abs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NegativeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    interface DragCallback {
        fun onDrag(progress: Float)
        fun onRelease(progress: Float)
    }
    var overlayProgress = 0f
    var dragCallback: DragCallback? = null
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private var downX = 0f
    private var downY = 0f
    private var dragging = false
    private var binding: LayoutNegativeViewBinding
    private var mScope: CoroutineScope? = null

    /**
     * 当前闪光灯模式
     */
    var mFlashTypeSelected = FlashUtil.FLASH_NORMAL

    /**
     * 当前闪光灯是否打开
     */
    var mFlashIsOpen = false

    init {
        Log.w(TAG, "NegativeView init")
        binding = LayoutNegativeViewBinding.inflate(LayoutInflater.from(context), this, true)
        initListener()
    }

    private fun initListener() {
        binding.btnFlashTypeNormal.setOnClickListener {
            changeFlashType(FlashUtil.FLASH_NORMAL)
        }
        binding.btnFlashTypeSos.setOnClickListener {
            changeFlashType(FlashUtil.FLASH_SOS)
        }
        binding.btnFlashTypeFlicker.setOnClickListener {
            changeFlashType(FlashUtil.FLASH_FLICKER)
        }

        binding.btnFlashOpen.setOnClickListener {
            processFlash()
        }

        binding.btnAmbientLight.setOnClickListener {
            ScreenLightActivity.startActivity(context)
        }
        binding.btnMarquee.setOnClickListener {
            MarqueeOptionsActivity.startActivity(context)
        }
    }

    private fun changeFlashType(type: String) {
        if (mFlashIsOpen) return
        mFlashTypeSelected = type
        when(type) {
            FlashUtil.FLASH_NORMAL -> {
                binding.ivFlashType.setImageResource(R.drawable.iv_negative_flash_type_1)
            }
            FlashUtil.FLASH_SOS -> {
                binding.ivFlashType.setImageResource(R.drawable.iv_negative_flash_type_2)
            }
            FlashUtil.FLASH_FLICKER -> {
                binding.ivFlashType.setImageResource(R.drawable.iv_negative_flash_type_3)
            }
        }
    }

    private fun processFlash() {
        if (!LauncherUtil.checkCameraPermission(context)) {
            mScope?.launch {
                EMUtil.showToast(context, context.getString(R.string.toast_camera_permission))
                delay(800L)
                // camera权限请求
                LauncherUtil.openAppSettings(context)
            }
            return
        }

        when (mFlashTypeSelected) {
            FlashUtil.FLASH_NORMAL -> {
                if (mFlashIsOpen) {
                    mFlashIsOpen = false
                    FlashUtil.turnOffFlash(false)
                    binding.ivFlashStatus.setImageResource(R.drawable.iv_negative_flash_off)
                } else {
                    mFlashIsOpen = true
                    FlashUtil.turnOnFlash(false)
                    binding.ivFlashStatus.setImageResource(R.drawable.iv_negative_flash_open)
                }
            }
            else -> {
                if (mFlashIsOpen) {
                    mFlashIsOpen = false
                    FlashUtil.stopFlickerFlash()
                    binding.ivFlashStatus.setImageResource(R.drawable.iv_negative_flash_off)
                } else {
                    mFlashIsOpen = true
                    FlashUtil.startFlickerFlash(mFlashTypeSelected)
                    binding.ivFlashStatus.setImageResource(if (mFlashTypeSelected == FlashUtil.FLASH_SOS) R.drawable.iv_negative_flash_sos else R.drawable.iv_negative_flash_flicker)
                }
            }
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

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.w(TAG, "NegativeView onAttachedToWindow")
        mScope?.cancel()
        mScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.w(TAG, "NegativeView onDetachedFromWindow")
        mScope?.cancel()
        mScope = null
    }
}
