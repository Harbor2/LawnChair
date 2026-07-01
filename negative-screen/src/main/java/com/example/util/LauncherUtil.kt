package com.example.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.NegativeContext
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

}
