package com.example.negative_screen

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.example.negative_screen.databinding.ActivityMarqueeTextBinding
import com.example.util.LauncherUtil
import com.wyz.emlibrary.util.EMUtil
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

class MarqueeTextActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMarqueeTextBinding
    private val mScope = MainScope()
    private val naviObserve = MutableLiveData(false)
    private var isStartedMarquee = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarqueeTextBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.statusBarColor = Color.TRANSPARENT

        naviObserve.observe(this) { value ->
            binding.ivBack.visibility = if (value) View.VISIBLE else View.GONE
        }
        initData()
        initListener()
    }

    private fun initData() {
        // 此activity为横屏需要获取高度使用
        val screenWidth = EMUtil.getScreenH(this)
        val rateTextSize = LauncherUtil.mapTextSize(sizeProgress) * screenWidth / EMUtil.dp2px(156f)

        binding.colorView.setBackgroundColor(backGroundColor)
        binding.colorView.alpha = backGroundAlpha

        with(binding.tvMarquee) {
            setText(inputStr)
            setTextColor(textColor)
            setTextAlpha(textAlpha)
            setScrollDirection(if (isDirectionLeft) 0 else 1)
            setTextSize(rateTextSize)
            setScrollSpeed(LauncherUtil.mapTextSpeed(speedProgress))
            startScroll()
            isStartedMarquee = true
        }
    }

    private fun initListener() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.colorView.setOnClickListener {
            naviObserve.value = !naviObserve.value!!
        }
    }

    override fun onResume() {
        super.onResume()
        if (isStartedMarquee) {
            binding.tvMarquee.stopScroll()
        }
        binding.tvMarquee.startScroll()
        isStartedMarquee = true
    }

    override fun onPause() {
        super.onPause()
        binding.tvMarquee.stopScroll()
        isStartedMarquee = false
    }

    override fun onDestroy() {
        super.onDestroy()
        mScope.cancel()
    }

    companion object {
        private var inputStr = "Text Led"
        private var backGroundColor = Color.BLACK
        private var backGroundAlpha = 1f
        private var textColor = Color.WHITE
        private var textAlpha = 1f
        private var isDirectionLeft = true
        private var sizeProgress = 100
        private var speedProgress = 100

        fun startActivity(
            context: Context,
            inputStr: String = "Text Led",
            backGroundColor: Int = Color.BLACK,
            backGroundAlpha: Float = 1f,
            textColor: Int = Color.WHITE,
            textAlpha: Float = 1f,
            isDirectionLeft: Boolean = true,
            sizeProgress: Int = 100,
            speedProgress: Int = 100
        ) {
            this.inputStr = inputStr
            this.backGroundColor = backGroundColor
            this.backGroundAlpha = backGroundAlpha
            this.textColor = textColor
            this.textAlpha = textAlpha
            this.isDirectionLeft = isDirectionLeft
            this.sizeProgress = sizeProgress
            this.speedProgress = speedProgress
            context.startActivity(Intent(context, MarqueeTextActivity::class.java))
        }
    }
}
