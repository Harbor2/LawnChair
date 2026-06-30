package com.example.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.negative_screen.R
import com.example.negative_screen.databinding.ActivityQuestionAnswerBinding
import com.example.util.FeedbackUtils
import com.wyz.emlibrary.em.EMManager
import com.wyz.emlibrary.util.immersiveWindowC

class QAActivity : AppCompatActivity() {

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, QAActivity::class.java))
        }
    }

    private lateinit var binding: ActivityQuestionAnswerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionAnswerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        immersiveWindowC(binding.root, false, binding.containerNavi)

        initView()
        initListener()
    }

    private fun initView() {
        EMManager.from(binding.btnContactUs)
            .setCorner(10f)
            .setBackGroundColor(R.color.btn_main_color)
    }

    private fun initListener() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.btnMore.setOnClickListener {

        }
        binding.btnContactUs.setOnClickListener {
            FeedbackUtils.feedback(this)
        }
    }
}
