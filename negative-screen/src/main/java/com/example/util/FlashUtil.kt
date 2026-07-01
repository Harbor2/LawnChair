package com.example.util

import android.content.Context
import android.hardware.camera2.CameraManager
import android.util.Log
import com.example.NegativeContext
import com.example.negative_screen.model.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

object FlashUtil {
    /**
     * 闪光灯模式
     */
    const val FLASH_NORMAL = "FLASH_NORMAL"
    const val FLASH_SOS = "FLASH_SOS"
    const val FLASH_FLICKER = "FLASH_FLICKER"

    private var mScope: CoroutineScope? = null
    private var flashJob: Job? = null

    /**
     * 闪烁播放
     */
    private var isStartFlicker = false

    // 获取或初始化协程作用域
    private fun getScope(): CoroutineScope {
        if (mScope == null) {
            mScope = CoroutineScope(Dispatchers.IO)
        }
        return mScope!!
    }

    /**
     * 闪烁闪光灯
     */
    fun startFlickerFlash(type: String) {
        stopFlickerFlash()
        var durationOn = 1000L
        var durationOff = 1000L
        when (type) {
            FLASH_FLICKER -> {
                durationOn = 250L
                durationOff = 250L
            }
            FLASH_SOS -> {
                durationOn = 100L
                durationOff = 100L
            }
        }
        val longPair = Pair(durationOn, durationOff)

        isStartFlicker = true
        // 获取协程作用域
        val scope = getScope()
        // 启动一个新的协程任务来控制闪光灯
        flashJob = scope.launch {
            while (isActive) {
                // 打开闪光灯
                turnOnFlash()
                delay(longPair.first)

                // 关闭闪光灯
                turnOffFlash()
                delay(longPair.second)
            }
        }
    }

    /**
     * 停止闪光灯，取消闪光灯控制协程
     */
    fun stopFlickerFlash() {
        isStartFlicker = false
        flashJob?.cancel() // 取消当前闪光灯任务
        turnOffFlash(false)
    }

    /**
     * 打开闪光灯
     */
    fun turnOnFlash(auto: Boolean = true) {
        if (!isStartFlicker && auto) {
            // 自动 & 结束
            return
        }
        try {
            val cameraManager = NegativeContext.context
                .getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = cameraManager.cameraIdList[0]
            cameraManager.setTorchMode(cameraId, true)
        } catch (e: Exception) {
            Log.e(TAG, "SoundFlashUtil: turnOnFlashLight()发生异常 ${e.message}")
        }
    }

    /**
     * 关闭闪光灯
     */
    fun turnOffFlash(auto: Boolean = true) {
        if (!isStartFlicker && auto) {
            // 自动 & 结束
            return
        }
        try {
            val cameraManager = NegativeContext.context
                .getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = cameraManager.cameraIdList[0]
            cameraManager.setTorchMode(cameraId, false)
        } catch (e: Exception) {
            Log.e(TAG, "SoundFlashUtil: turnOffFlashLight()发生异常 ${e.message}")
        }
    }

}
