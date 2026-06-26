package com.example.workspace

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.example.negative_screen.databinding.ActivityGuideBinding
import com.example.util.immersiveWindow

class GuideActivity : AppCompatActivity() {

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, GuideActivity::class.java))
        }
    }

    private lateinit var binding: ActivityGuideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        immersiveWindow(binding.rootView, false)

        initView()
        initListener()
    }

    private fun initView() {

    }

    private fun initListener() {
        binding.buttonConfirm.setOnClickListener {
            startActivity(Intent(Settings.ACTION_HOME_SETTINGS))

            binding.root.postDelayed({
                LauncherSelectActivity.startActivity(this)
                finish()
            }, 1000)
        }
    }

}
