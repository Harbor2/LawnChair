package com.example.setting

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.negative_screen.R
import com.example.negative_screen.databinding.LayoutCustomSwitchButtonBinding
import com.wyz.emlibrary.em.EMManager

class SwitchButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: LayoutCustomSwitchButtonBinding
    private var mSwitchStatus = false

    init {
        binding = LayoutCustomSwitchButtonBinding.inflate(LayoutInflater.from(context), this, true)
        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.SwitchButton, 0, 0)
        val icon = attributes.getResourceId(R.styleable.SwitchButton_sbIcon, -1)
        val title = attributes.getString(R.styleable.SwitchButton_sbTitle) ?: ""
        val select = attributes.getBoolean(R.styleable.SwitchButton_sbSelect, false)

        if (icon != -1) {
            binding.btnIcon.setImageResource(icon)
        }

        if (title.isNotEmpty()) {
            binding.btnTitle.text = title
        }

        updateSwitchStatus(select)
    }

    fun updateSwitchStatus(status: Boolean) {
        if (mSwitchStatus != status) {
            mSwitchStatus = status
        }

        EMManager.from(binding.btnBg)
            .setCorner(24f)
            .setBackGroundColor(if (mSwitchStatus) R.color.btn_main_color else R.color.white_10)
        binding.btnIcon.alpha = if (mSwitchStatus) 1f else 0.3f
        binding.btnTitle.alpha = if (mSwitchStatus) 1f else 0.3f
    }

}
