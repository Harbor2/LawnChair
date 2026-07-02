package com.example.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.negative_screen.R
import com.example.negative_screen.databinding.ActivityQuestionAnswerBinding
import com.example.util.FeedbackUtils
import com.example.util.LauncherUtil
import com.wyz.emlibrary.em.EMManager
import com.wyz.emlibrary.util.EMMapUtil
import com.wyz.emlibrary.util.EMUtil
import com.wyz.emlibrary.util.immersiveWindowC
import android.graphics.Typeface
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.wyz.emlibrary.util.isNotNullOrEmpty
import org.json.JSONObject

class QAActivity : AppCompatActivity() {

    companion object {
        var isUnstall = false
        var mNaviTitle = ""
        fun startActivity(context: Context, uninstall: Boolean = false, naviTitle: String = "") {
            isUnstall = uninstall
            mNaviTitle = naviTitle
            context.startActivity(Intent(context, QAActivity::class.java))
        }
    }

    private lateinit var binding: ActivityQuestionAnswerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionAnswerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        immersiveWindowC(binding.root, false, binding.containerNavi)

        initView()
        initData()
        initListener()
    }

    private fun initView() {
        EMManager.from(binding.btnContactUs)
            .setCorner(22f)
            .setBackGroundColor(R.color.btn_main_color)
    }

    private fun initData() {
        binding.llBottom.isVisible = !isUnstall
        fillContainer(if (isUnstall) "Uninstall" else "Help_Center")
    }

    private fun fillContainer(path: String) {
        binding.llContainers.removeAllViews()

        val rootMap = LauncherUtil.parseJsonToMapWithJSONObject()
        val helpCenterMap = EMMapUtil.optMap(rootMap, null, "Application", path) ?: return

        val naviTitle = EMMapUtil.optString(helpCenterMap, "", "naviTitle")
        val title = EMMapUtil.optString(helpCenterMap, "", "title")
        val desc = EMMapUtil.optString(helpCenterMap, "", "desc")

        binding.tvTitle.text = mNaviTitle.ifEmpty { naviTitle }
        if (title.isNotEmpty()) {
            TextView(this).apply {
                text = title
                textSize = 20f
                typeface = ResourcesCompat.getFont(context, R.font.product_sans_b)
                setTextColor(EMUtil.getColor(R.color.text_main_color))
                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                params.setMargins(
                    0,
                    EMUtil.dp2Px(32),
                    0,
                    0
                )
                layoutParams = params
                binding.llContainers.addView(this)
            }
        }

        if (desc.isNotEmpty()) {
            TextView(this).apply {
                text = desc
                textSize = 14f
                typeface = ResourcesCompat.getFont(context, R.font.product_sans_r)
                setTextColor(EMUtil.getColor(R.color.text_main_color_30))
                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                params.setMargins(
                    0,
                    EMUtil.dp2Px(10),
                    0,
                    0
                )
                layoutParams = params
                binding.llContainers.addView(this)
            }
        }

        val ques = EMMapUtil.optList(helpCenterMap, null, "ques") ?: return

        View(this).apply {
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, EMUtil.dp2Px(32))
            layoutParams = params
            binding.llContainers.addView(this)
        }

        for (i in ques.indices) {
            val queJsonObject = ques[i] as? JSONObject ?: continue

            val action = queJsonObject.optString("action").toIntOrNull() ?: -1
            val queTitle = queJsonObject.optString("que") ?: ""
            val queAnsTitle = queJsonObject.optString("ansTitle") ?: ""
            val queAns = queJsonObject.optString("ans") ?: ""

            TitleArrowItem(this).apply {
                updateView(queTitle)
                setOnClickListener {
                    when(action) {
                        3 -> {
                            // 跳转卸载
                            startActivity(this@QAActivity, true, getString(R.string.help_center))
                        }
                        4 -> {
                            // 隐私政策
                            BrowseActivity.startPrivacyPolicy(this@QAActivity)
                        }
                        else -> {
                            QADetailActivity.startActivity(this@QAActivity, naviTitle, action, queAnsTitle.ifEmpty { queTitle }, queAns)
                        }
                    }
                }
                binding.llContainers.addView(this)
            }
        }
    }

    private fun initListener() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.btnContactUs.setOnClickListener {
            FeedbackUtils.feedback(this)
        }
    }
}
