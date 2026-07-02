package com.example.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.negative_screen.R
import com.example.util.FeedbackUtils
import com.example.util.LauncherUtil
import com.wyz.emlibrary.em.EMManager
import com.wyz.emlibrary.util.EMUtil
import com.wyz.emlibrary.util.immersiveWindowC
import androidx.core.content.res.ResourcesCompat
import com.example.negative_screen.databinding.ActivityDetailQuestionAnswerBinding

class QADetailActivity : AppCompatActivity() {

    companion object {
        private var mNaviTitle: String = ""
        private var mAction: Int = -1
        private var mQue: String = ""
        private var mAns: String = ""

        fun startActivity(
            context: Context,
            naviTitle: String,
            action: Int,
            que: String,
            ans: String
        ) {
            mNaviTitle = naviTitle
            mAction = action
            mQue = que
            mAns = ans
            context.startActivity(Intent(context, QADetailActivity::class.java))
        }
    }

    private lateinit var binding: ActivityDetailQuestionAnswerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailQuestionAnswerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        immersiveWindowC(binding.root, false, binding.containerNavi)

        initView()
        initData()
        initListener()
    }

    private fun initView() {
        EMManager.from(binding.btnAction)
            .setCorner(22f)
            .setBackGroundColor(R.color.btn_main_color)
    }

    private fun initData() {
        binding.tvTitle.text = mNaviTitle
        if (mQue.isNotEmpty()) {
            TextView(this).apply {
                text = mQue
                textSize = 20f
                typeface = ResourcesCompat.getFont(context, R.font.product_sans_b)
                setTextColor(EMUtil.getColor(R.color.text_main_color))
                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                params.setMargins(
                    0,
                    EMUtil.dp2Px(32),
                    0,
                    0
                )
                layoutParams = params
                binding.llContainers.addView(this)
            }
        }

        if (mAns.isNotEmpty()) {
            TextView(this).apply {
                text = mAns
                textSize = 14f
                typeface = ResourcesCompat.getFont(context, R.font.product_sans_r)
                setTextColor(EMUtil.getColor(R.color.text_main_color_30))
                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                params.setMargins(
                    0,
                    EMUtil.dp2Px(10),
                    0,
                    0
                )
                layoutParams = params
                binding.llContainers.addView(this)
            }
        }

        when(mAction) {
            1 -> {
                // 切换Home应用
                binding.btnAction.text = getString(R.string.change_default_launcher)
                binding.btnAction.isVisible = true
            }
            2-> {
                // contact us
                binding.btnAction.text = getString(R.string.contact_us)
                binding.btnAction.isVisible = true
            }
            5 -> {
                // uninstall
                binding.btnAction.text = getString(R.string.uninstall)
                binding.btnAction.isVisible = true
            }
            else -> {
                binding.btnAction.isVisible = false
            }
        }
    }

    private fun initListener() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.btnAction.setOnClickListener {
            when(mAction) {
                1 -> {
                    // 切换Home应用
                    LauncherUtil.setDefaultLauncher(this)
                }
                2-> {
                    // contact us
                    FeedbackUtils.feedback(this)
                }
                5 -> {
                    // uninstall
                    LauncherUtil.jumpSystemSetting(this)
                }
                else -> {}
            }
        }
    }
}
