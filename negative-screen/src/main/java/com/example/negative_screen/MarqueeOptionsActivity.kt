package com.example.negative_screen

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.example.negative_screen.databinding.ActivityMarqueeOptionsBinding
import com.example.util.LauncherUtil
import com.wyz.emlibrary.em.EMManager
import com.wyz.emlibrary.util.EMUtil
import com.wyz.emlibrary.util.immersiveWindowC
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlin.math.min

class MarqueeOptionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMarqueeOptionsBinding
    private val directionObserve = MutableLiveData(true)
    private val mScope = MainScope()
    private var isStartedMarquee = false

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

    private var mInputStr = "Text Led"
    private var mSelectLeftDirection = true
    private var mSizeProgress = 100
    private var mSpeedProgress = 100
    private var mTextColorInt = EMUtil.getColor("#FFFFFF")
    private var mTextTrans = 1f
    private var mBgColorInt = EMUtil.getColor("#000000")
    private var mBgTrans = 1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarqueeOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        immersiveWindowC(binding.root, false, binding.containerNavi)

        initView()
        initData()
        initListener()
    }

    private fun initView() {
        EMManager.from(binding.marqueeBorder)
            .setBorderWidth(1f)
            .setCorner(24f)
            .setBorderColor(R.color.white_30)
        EMManager.from(binding.containerInput)
            .setCorner(20f)
            .setBackGroundColor(R.color.white_10)
        EMManager.from(binding.tvPlay)
            .setCorner(22f)
            .setBackGroundColor(R.color.btn_main_color)
        EMManager.from(binding.bgSeekbarSize)
            .setCorner(4f)
            .setBackGroundColor(R.color.white_15)
        EMManager.from(binding.bgSeekbarSpeed)
            .setCorner(4f)
            .setBackGroundColor(R.color.white_15)

        directionObserve.observe(this) { value ->
            mSelectLeftDirection = value
            binding.btnLeft.setTextColor(EMUtil.getColor(if (value) R.color.btn_main_color else R.color.white_54))
            binding.btnRight.setTextColor(EMUtil.getColor(if (value) R.color.white_54 else R.color.btn_main_color))
            EMManager.from(binding.btnLeft)
                .setCorner(6f)
                .setBorderWidth(1f)
                .setBorderColor(if (value) R.color.btn_main_color else R.color.white_54)
            EMManager.from(binding.btnRight)
                .setCorner(6f)
                .setBorderWidth(1f)
                .setBorderColor(if (value) R.color.white_54 else R.color.btn_main_color)
        }
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
        binding.seekbarTextColor.progressDrawable = colorGradientDrawable
        binding.seekbarTextTrans.progressDrawable = transGradientDrawable

        // 背景
        binding.colorView.setBackgroundColor(mBgColorInt)
        binding.colorView.alpha = mBgTrans

        binding.editInput.setText(mInputStr)

        binding.tvSize.text = String.format("%d%%", mSizeProgress)
        binding.seekbarSize.progress = mSizeProgress
        binding.tvSpeed.text = String.format("%d%%", mSpeedProgress)
        binding.seekbarSpeed.progress = mSpeedProgress
        binding.seekbarTextColor.progress = 0
        binding.seekbarTextTrans.progress = 100

        binding.tvMarquee.post {
            binding.tvMarquee.setText(mInputStr)
            binding.tvMarquee.setTextColor(mTextColorInt)
            binding.tvMarquee.setTextAlpha(mTextTrans)
            binding.tvMarquee.setTextSize(LauncherUtil.mapTextSize(mSizeProgress))
            binding.tvMarquee.setScrollDirection(if (mSelectLeftDirection) 0 else 1)
            binding.tvMarquee.setScrollSpeed(LauncherUtil.mapTextSpeed(mSpeedProgress))
            binding.tvMarquee.startScroll()
            isStartedMarquee = true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.scrollView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    LauncherUtil.hideSoftKeyboard(binding.editInput, this@MarqueeOptionsActivity)
                }
            }
            false
        }
        binding.btnLeft.setOnClickListener {
            directionObserve.value = true
            binding.tvMarquee.setScrollDirection(0)
        }
        binding.btnRight.setOnClickListener {
            directionObserve.value = false
            binding.tvMarquee.setScrollDirection(1)
        }
        binding.tvPlay.setOnClickListener {
            MarqueeTextActivity.startActivity(
                this,
                mInputStr,
                mBgColorInt,
                mBgTrans,
                mTextColorInt,
                mTextTrans,
                mSelectLeftDirection,
                mSizeProgress,
                mSpeedProgress
            )
        }
        binding.editInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val result = s.toString().ifEmpty { "Text Led" }
                binding.tvMarquee.setText(result)
                mInputStr = result
            }
        })
        binding.editInput.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    LauncherUtil.hideSoftKeyboard(binding.editInput, this@MarqueeOptionsActivity)
                    return true
                }
                return false
            }
        })
        binding.seekbarSize.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (!fromUser) {
                    return
                }
                mSizeProgress = progress
                binding.tvSize.text = String.format("%d%%", progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val progress = seekBar?.progress ?: 100
                binding.tvMarquee.setTextSize(LauncherUtil.mapTextSize(progress))
            }
        })
        binding.seekbarSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (!fromUser) {
                    return
                }
                mSpeedProgress = progress
                binding.tvSpeed.text = String.format("%d%%", progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val progress = seekBar?.progress?: 100
                binding.tvMarquee.setScrollSpeed(LauncherUtil.mapTextSpeed(progress))
            }
        })
        binding.seekbarTextColor.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val fraction = (seekBar?.progress ?: 100) / 100f
                mTextColorInt = getGradientColor(fraction, screenColors)
                binding.tvMarquee.setTextColor(mTextColorInt)
            }
        })
        binding.seekbarTextTrans.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mTextTrans = (seekBar?.progress?: 100) / 100f
                binding.tvMarquee.setTextAlpha(mTextTrans)
            }
        })
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
        mScope.cancel()
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, MarqueeOptionsActivity::class.java)
            if (context !is android.app.Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}
