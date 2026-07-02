package com.example.util

import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.util.Log

object NetworkUtils {

    private const val TAG = "NETWORK_UTILS"

    fun isWifiEnable(context: Context): Boolean {
        return try {
            val wifiManager = context.applicationContext
                .getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifiManager.isWifiEnabled
        } catch (e: Exception) {
            Log.d(TAG, "isWifiEnable(), e = $e")
            false
        }
    }

    fun openWifiSwitch(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.d(TAG, "openWifiSwitch(), e = $e")
        }
    }

    fun isMobileDateEnable(context: Context): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true
        } catch (e: Exception) {
            Log.d(TAG, "isMobileDateEnable(), e = $e")
            false
        }
    }

    fun openMobileDataSwitch(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS)
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.d(TAG, "openMobileDataSwitch(), e = $e")

            try {
                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                context.startActivity(intent)
            } catch (e: Exception) {
                Log.d(TAG, "openMobileDataSwitch(), e = $e")
            }
        }
    }

    fun isHotspotEnable(context: Context): Boolean {
        return try {
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val method = WifiManager::class.java.getDeclaredMethod("getWifiApState")
            method.isAccessible = true

            val state = method.invoke(wifiManager) as Int
            state == 13 // WIFI_AP_STATE_ENABLED
        } catch (e: Exception) {
            Log.d(TAG, "isHotspotEnable(), e = $e")
            false
        }
    }

    fun openHotspotSwitch(context: Context) {
        try {
            val intent = Intent("android.settings.TETHER_SETTINGS")
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.d(TAG, "openMobileDataSwitch(), e = $e")

            try {
                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                context.startActivity(intent)
            } catch (e: Exception) {
                Log.d(TAG, "openMobileDataSwitch(), e = $e")
            }
        }
    }

    fun isBluetoothEnable(context: Context): Boolean {
        return try {
            val bluetoothManager = context.getSystemService(
                Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothManager.adapter?.isEnabled == true
        } catch (e: Exception) {
            Log.d(TAG, "isBluetoothEnable(), e = $e")
            false
        }
    }

    fun openBlueToothSwitch(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.d(TAG, "openBlueToothSwitch(), e = $e")
        }
    }

    fun isAirplaneModeOn(context: Context): Boolean {
        return try {
            return Settings.Global.getInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) != 0
        } catch (e: Exception) {
            Log.d(TAG, "isAirplaneModeOn(), e = $e")
            false
        }
    }

    fun openAirplaneModeSwitch(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS)
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.d(TAG, "openAirplaneModeSwitch(), e = $e")

            try {
                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                context.startActivity(intent)
            } catch (e: Exception) {
                Log.d(TAG, "openAirplaneModeSwitch(), e = $e")
            }
        }
    }

    fun isLocationEnable(context: Context): Boolean {
        return try {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                lm.isLocationEnabled
            } else {
                lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            }
        } catch (e: Exception) {
            false
        }
    }

    fun openLocationSwitch(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context.startActivity(intent)
        } catch (e: Exception) {
        }
    }
}
