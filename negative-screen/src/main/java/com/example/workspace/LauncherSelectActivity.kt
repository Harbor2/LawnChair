package com.example.workspace

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.negative_screen.databinding.ActivityLauncherSelectBinding
import com.wyz.emlibrary.em.Direction
import com.wyz.emlibrary.em.EMManager
import com.wyz.emlibrary.util.immersiveWindowC

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
        immersiveWindowC(binding.root, false)

        initView()
        initListener()
    }

    private fun initView() {
        EMManager.from(binding.containerTips)
            .setCorner(16f)
            .setGradientColor(arrayOf("#FFE5BA", "#FFEED3"), Direction.TOP)

        EMManager.from(binding.containerTips2)
            .setCorner(8f)
            .setShadow("#40000000", 2f, 1f, 2f)
            .setBackGroundColor("#FAF9F4")

    }

    private fun initListener() {
        binding.root.setOnClickListener {
            finish()
        }
    }

}
