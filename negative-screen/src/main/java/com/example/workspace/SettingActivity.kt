package com.example.workspace

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.negative_screen.databinding.ActivityNegativeGuideBinding
import com.example.negative_screen.databinding.ActivitySettingBinding
import com.example.util.immersiveWindow

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
        immersiveWindow(binding.rootView, false)

        initView()
        initListener()
    }

    private fun initView() {

    }

    private fun initListener() {
        binding.root.setOnClickListener {
            finish()
        }
    }
}
