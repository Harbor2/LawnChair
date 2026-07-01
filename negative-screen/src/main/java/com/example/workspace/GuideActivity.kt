package com.example.workspace

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.negative_screen.databinding.ActivityGuideBinding
import com.example.util.LauncherUtil
import com.wyz.emlibrary.em.Direction
import com.wyz.emlibrary.em.EMManager
import com.wyz.emlibrary.util.immersiveWindowC
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GuideActivity : AppCompatActivity() {

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, GuideActivity::class.java))
        }
    }

    private lateinit var binding: ActivityGuideBinding
    private val cameraPermLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuideBinding.inflate(layoutInflater)
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

        startAnim()
        checkPerm()
    }

    private fun initListener() {
        binding.btnContinue.setOnClickListener {
            startActivity(Intent(Settings.ACTION_HOME_SETTINGS))

            binding.root.postDelayed({
                LauncherSelectActivity.startActivity(this)
                finish()
            }, 800)
        }
    }

    private fun startAnim() {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.1f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.1f)

        ObjectAnimator.ofPropertyValuesHolder(binding.btnContinue, scaleX, scaleY).apply {
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            start()
        }
    }

    private fun checkPerm() {
        // 权限判断
        if (!LauncherUtil.checkCameraPermission(this)) {
            lifecycleScope.launch {
                delay(200)
                cameraPermLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }
    }
}
