package com.example.negative_screen

import android.animation.ArgbEvaluator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.example.negative_screen.databinding.ActivityScreenLightBinding
import com.wyz.emlibrary.em.EMManager
import com.wyz.emlibrary.util.EMUtil
import com.wyz.emlibrary.util.immersiveWindowC
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlin.math.min

class ScreenLightActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScreenLightBinding
    private val mScope = MainScope()
    private var naviObserve = MutableLiveData(false)

    private var screenPlayJob: Job? = null

    private val screenColors = intArrayOf(
        EMUtil.getColor("#FF0000"),
        EMUtil.getColor("#FF6B00"),
        EMUtil.getColor("#FFA800"),
        EMUtil.getColor("#EBFF00"),
        EMUtil.getColor("#61FF00"),
        EMUtil.getColor("#00FF47"),
        EMUtil.getColor("#00FFE0"),
        EMUtil.getColor("#0075FF"),
        EMUtil.getColor("#0500FF"),
        EMUtil.getColor("#CC00FF"),
        EMUtil.getColor("#FF0000")
    )

    private val transColors = intArrayOf(
        Color.TRANSPARENT,
        Color.WHITE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScreenLightBinding.inflate(layoutInflater)
        setContentView(binding.root)
        immersiveWindowC(binding.root, false, binding.containerNavi)

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.statusBarColor = Color.TRANSPARENT

        initView()
        initData()
        initListener()
    }

    private fun initView() {
        naviObserve.observe(this) { value ->
            binding.containerNavi.visibility = if (value) View.VISIBLE else View.GONE
            binding.bottomOption.visibility = if (value) View.VISIBLE else View.GONE
        }
        setActivityBrightness()
    }

    private fun initData() {
        val colorGradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, screenColors
        ).apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = EMUtil.dp2px(4f)
        }
        val transGradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, transColors
        ).apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = EMUtil.dp2px(4f)
        }
        binding.seekbarColor.progressDrawable = colorGradientDrawable
        binding.seekbarTrans.progressDrawable = transGradientDrawable

        // 未传入色值信息
        naviObserve.value = true
        updateScreenColor(screenColors[0])
        updateScreenTrans(1f)
    }

    private fun initListener() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.colorView.setOnClickListener {
            naviObserve.value =!naviObserve.value!!
        }

        binding.seekbarColor.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (seekBar == null) return
                val selectColor =  getGradientColor(progress * 1f / seekBar.max, screenColors)
                updateScreenColor(selectColor)
            }
        })

        binding.seekbarTrans.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (seekBar == null) return
                val selectAlpha = progress * 1f / 100
                updateScreenTrans(selectAlpha)
            }
        })
    }

    private fun setActivityBrightness() {
        val layoutParams = window.attributes
        layoutParams.screenBrightness = 1f
        window.attributes = layoutParams
    }

    private fun stopPlayScreenColor() {
        screenPlayJob?.cancel()
    }

    private fun updateScreenColor(@ColorInt color: Int) {
        EMManager.from(binding.colorView)
            .setBackGroundRealColor(color)
    }

    private fun updateScreenTrans(alpha: Float) {
        binding.colorView.alpha = alpha
    }

    /**
     * 计算给定进度比例(fraction)的颜色值
     */
    private fun getGradientColor(fraction: Float, colors: IntArray): Int {
        val evaluator = ArgbEvaluator()
        val index = (fraction * (colors.size - 1)).toInt()
        val startColor = colors[index]
        val endColor = colors[min(index + 1, colors.size - 1)]
        val localFraction = (fraction * (colors.size - 1)) % 1
        return evaluator.evaluate(localFraction, startColor, endColor) as Int
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPlayScreenColor()
        mScope.cancel()
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, ScreenLightActivity::class.java)
            if (context !is android.app.Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}
