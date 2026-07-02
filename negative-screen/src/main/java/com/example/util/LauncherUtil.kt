package com.example.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.example.NegativeContext
import com.wyz.emlibrary.util.EMUtil
import java.io.IOException
import java.util.jar.Manifest
import org.json.JSONObject

object LauncherUtil {
    /**
     * 是否默认launcher
     */
    fun isDefaultLauncher(context: Context): Boolean {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        val resolveInfo = context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return resolveInfo?.activityInfo?.packageName == context.packageName
    }

    fun setDefaultLauncher(context: Context) {
        val intent = Intent(Settings.ACTION_HOME_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun jumpSystemSetting(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun parseJsonToMapWithJSONObject(): Map<String, Any>? {
        val jsonString = readJsonFromAssets()
        return try {
            val jsonObject = JSONObject(jsonString)
            jsonObjectToMap(jsonObject)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun readJsonFromAssets(): String {
        return try {
            val inputStream = NegativeContext.context.assets.open("config_app.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8) // 转换为字符串
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }

    private fun jsonObjectToMap(jsonObject: JSONObject): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        jsonObject.keys().forEach { key ->
            val value = jsonObject.get(key)
            map[key] = when (value) {
                is JSONObject -> jsonObjectToMap(value)
                is org.json.JSONArray -> List(value.length()) { value.get(it) }
                else -> value
            }
        }
        return map
    }


    fun checkCameraPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    /**
     * 10  50  130
     */
    fun mapTextSize(progress: Int): Float {
        val pxSize = if (progress >= 100) {
            50f + 80 * (progress - 100) / 100f
        } else {
            10f + 40f * progress / 100f
        }
        return EMUtil.dp2px(pxSize)
    }

    /**
     * 0 35 70
     */
    fun mapTextSpeed(progress: Int): Int {
        val result =  if (progress >= 100) {
            35 + 35 * (progress - 100) / 100
        } else {
            35 * progress / 100
        }
        return 70 - result
    }

    fun hideSoftKeyboard(view: EditText, context: Context) {
        view.clearFocus()
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}
