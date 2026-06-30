package com.example.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.negative_screen.databinding.ActivitySettingBinding
import com.wyz.emlibrary.util.immersiveWindowC

class SettingActivity : AppCompatActivity() {

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, SettingActivity::class.java))
        }
    }

    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        immersiveWindowC(binding.root, false)

        initView()
        initListener()
    }

    private fun initView() {

    }

    private fun initListener() {
        binding.sbtnHelpCenter.setOnClickListener {
            QAActivity.startActivity(this, true)
        }
        binding.sbtnAboutUs.setOnClickListener {
            AboutUsActivity.startActivity(this)
        }
        binding.btnSystemSetting.setOnClickListener {
            val intent = Intent(android.provider.Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        }
    }
}
