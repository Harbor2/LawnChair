package com.example.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.negative_screen.R
import com.example.negative_screen.databinding.ActivityAboutUsBinding
import com.example.negative_screen.model.TAG
import com.wyz.emlibrary.em.EMManager
import com.wyz.emlibrary.util.immersiveWindowC

class AboutUsActivity : AppCompatActivity() {

    companion object {
        fun startActivity(context: Context) {
            Log.d(TAG, "点击跳转关于页面")
            context.startActivity(Intent(context, AboutUsActivity::class.java))
        }
    }

    private lateinit var binding: ActivityAboutUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        immersiveWindowC(binding.root, false, binding.containerNavi)

        initView()
        initListener()
    }

    private fun initView() {
        EMManager.from(binding.btnPrivacy)
            .setCorner(10f)
            .setBorderWidth(1f)
            .setBorderColor(R.color.btn_main_color)
        EMManager.from(binding.btnUninstall)
            .setCorner(10f)
            .setBorderWidth(1f)
            .setBorderColor(R.color.btn_main_color)
    }

    private fun initListener() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.btnPrivacy.setOnClickListener {

        }
        binding.btnUninstall.setOnClickListener {

        }
    }
}
