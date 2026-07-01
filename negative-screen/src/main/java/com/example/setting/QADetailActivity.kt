package com.example.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.negative_screen.R
import com.example.negative_screen.databinding.ActivityQuestionAnswerBinding
import com.example.util.FeedbackUtils
import com.example.util.LauncherUtil
import com.wyz.emlibrary.em.EMManager
import com.wyz.emlibrary.util.EMMapUtil
import com.wyz.emlibrary.util.EMUtil
import com.wyz.emlibrary.util.immersiveWindowC
import android.graphics.Typeface
import android.view.View
import com.example.negative_screen.databinding.ActivityDetailQuestionAnswerBinding
import org.json.JSONObject

class QADetailActivity : AppCompatActivity() {

    companion object {
        private var mAction: Int = -1
        private var mQue: String = ""
        private var mAns: String = ""

        fun startActivity(
            context: Context,
            action: Int,
            que: String,
            ans: String
        ) {
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
        if (mQue.isNotEmpty()) {
            TextView(this).apply {
                text = mQue
                textSize = 20f
                setTypeface(typeface, Typeface.BOLD)
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
                setTypeface(typeface, Typeface.NORMAL)
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
                binding.btnAction.isVisible = true
            }
            2-> {
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
    }
}
