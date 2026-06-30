package com.example.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.example.negative_screen.R
import com.example.negative_screen.databinding.ActivityWebBinding

class BrowseActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_KEY_TITLE = "EXTRA_KEY_TITLE"
        private const val EXTRA_KEY_URL = "EXTRA_KEY_URL"

        fun startPrivacyPolicy(context: Context) {
            val intent = Intent(context, BrowseActivity::class.java)
            intent.putExtra(EXTRA_KEY_TITLE, context.getString(R.string.text_privacy_policy))
            intent.putExtra(EXTRA_KEY_URL, "https://sites.google.com/view/jfcu-policy/")
            context.startActivity(intent)
        }

        fun startTermOfService(context: Context) {
            val intent = Intent(context, BrowseActivity::class.java)
            intent.putExtra(EXTRA_KEY_TITLE, context.getString(R.string.text_terms_of_service))
            intent.putExtra(EXTRA_KEY_URL, "https://sites.google.com/view/jfcu-terms/")
            context.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityWebBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        binding.webView.setBackgroundColor(getColor(R.color.white))

        WindowCompat.getInsetsController(window, binding.root).isAppearanceLightStatusBars = true
        val title = intent?.getStringExtra(EXTRA_KEY_TITLE) ?: ""
        val url = intent?.getStringExtra(EXTRA_KEY_URL) ?: ""

        binding.webView.loadUrl(url)
        binding.titleLabel.text = title

        binding.backView.setOnClickListener {
            finish()
        }
    }
}
