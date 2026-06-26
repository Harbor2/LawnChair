package com.example.workspace

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.negative_screen.databinding.ActivityLauncherSelectBinding
import com.example.util.immersiveWindow

class LauncherSelectActivity : AppCompatActivity() {

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, LauncherSelectActivity::class.java))
        }
    }

    private lateinit var binding: ActivityLauncherSelectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherSelectBinding.inflate(layoutInflater)
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
