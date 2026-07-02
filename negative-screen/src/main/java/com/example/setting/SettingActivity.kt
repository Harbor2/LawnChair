package com.example.setting

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.camera2.CameraManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.observe
import com.example.callback.NegativeModuleCallbackManager
import com.example.negative_screen.R
import com.example.negative_screen.databinding.ActivitySettingBinding
import com.example.util.FlashUtil
import com.example.util.NetworkUtils
import com.wyz.emlibrary.util.immersiveWindowC

class SettingActivity : AppCompatActivity() {

    private val wifiLiveData = MutableLiveData(false)
    private val flashLiveData = MutableLiveData(false)
    private val mobileDataLiveData = MutableLiveData(false)
    private val hotspotLiveData = MutableLiveData(false)
    private val bluetoothLiveData = MutableLiveData(false)
    private val locationLiveData = MutableLiveData(false)
    private val flyLiveData = MutableLiveData(false)

    private val wifiReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent?) {
            updateWifiStatus()
            updateHotspotStatus()
        }
    }

    private val torchCallback = object : CameraManager.TorchCallback() {
        override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
            flashLiveData.value = enabled
        }

        override fun onTorchModeUnavailable(cameraId: String) {}
    }

    private val mobileDataCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
            val cellular = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            mobileDataLiveData.postValue(cellular)
        }
    }

    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent?) {
            updateBluetoothStatus()
        }
    }

    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent?) {
            updateLocationStatus()
        }
    }

    private val airplaneReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent?) {
            updateAirplaneStatus()
        }
    }

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
        immersiveWindowC(binding.root, false)

        initView()
        initData()
        initListener()
    }

    private fun initView() {
        binding.tvTitle.text = String.format("%s Launcher", getString(R.string.app_name))
        wifiLiveData.observe(this) { value ->
            binding.sbtnWifi.updateSwitchStatus(value)
        }
        flashLiveData.observe(this) { value ->
            binding.sbtnFlash.updateSwitchStatus(value)
        }
        mobileDataLiveData.observe(this) { value ->
            binding.sbtnMobileData.updateSwitchStatus(value)
        }
        hotspotLiveData.observe(this) { value ->
            binding.sbtnHotspot.updateSwitchStatus(value)
        }
        bluetoothLiveData.observe(this) { value ->
            binding.sbtnBluetooth.updateSwitchStatus(value)
        }
        locationLiveData.observe(this) { value ->
            binding.sbtnLocation.updateSwitchStatus(value)
        }
        flyLiveData.observe(this) { value ->
            binding.sbtnFlight.updateSwitchStatus(value)
        }
    }

    private fun initData() {
        updateWifiStatus()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(wifiReceiver, IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION), RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(wifiReceiver, IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION))
        }

        val cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        cameraManager.registerTorchCallback(torchCallback, Handler(Looper.getMainLooper()))

        updateMobileDateStatus()
        val cm = getSystemService(ConnectivityManager::class.java)
        cm.registerDefaultNetworkCallback(mobileDataCallback)

        updateBluetoothStatus()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(bluetoothReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED), RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(bluetoothReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
        }

        updateLocationStatus()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(locationReceiver, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION), RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(locationReceiver, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))
        }

        updateAirplaneStatus()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(airplaneReceiver, IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED), RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(airplaneReceiver, IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED))
        }
    }

    private fun initListener() {
        binding.btnTopSetting.setOnClickListener {
            NegativeModuleCallbackManager.preferenceJumpCallback?.onJumpPreference(this)
        }
        binding.sbtnHelpCenter.setOnClickListener {
            QAActivity.startActivity(this, true)
        }
        binding.sbtnAboutUs.setOnClickListener {
            AboutUsActivity.startActivity(this)
        }
        binding.btnSystemSetting.setOnClickListener {
            val intent = Intent(android.provider.Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        }

        binding.sbtnWifi.setOnClickListener {
            NetworkUtils.openWifiSwitch(this)
        }
        binding.sbtnFlash.setOnClickListener {
            if (!flashLiveData.value!!) {
                FlashUtil.turnOnFlash(false)
            } else {
                FlashUtil.turnOffFlash(false)
            }
            flashLiveData.value = !flashLiveData.value!!
        }
        binding.sbtnMobileData.setOnClickListener {
            NetworkUtils.openMobileDataSwitch(this)
        }
        binding.sbtnHotspot.setOnClickListener {
            NetworkUtils.openHotspotSwitch(this)
        }
        binding.sbtnBluetooth.setOnClickListener {
            NetworkUtils.openBlueToothSwitch(this)
        }
        binding.sbtnLocation.setOnClickListener {
            NetworkUtils.openLocationSwitch(this)
        }
        binding.sbtnFlight.setOnClickListener {
            NetworkUtils.openAirplaneModeSwitch(this)
        }
    }

    private fun updateWifiStatus() {
        wifiLiveData.value = NetworkUtils.isWifiEnable(this)
    }

    private fun updateHotspotStatus() {
        hotspotLiveData.value = NetworkUtils.isHotspotEnable(this)
    }

    private fun updateMobileDateStatus() {
        mobileDataLiveData.value = NetworkUtils.isMobileDateEnable(this)
    }

    private fun updateBluetoothStatus() {
        bluetoothLiveData.value = NetworkUtils.isBluetoothEnable(this)
    }

    private fun updateLocationStatus() {
        locationLiveData.value = NetworkUtils.isLocationEnable(this)
    }

    private fun updateAirplaneStatus() {
        flyLiveData.value = NetworkUtils.isAirplaneModeOn(this)
    }
}
