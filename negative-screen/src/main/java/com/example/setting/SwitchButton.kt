package com.example.setting

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.negative_screen.R
import com.example.negative_screen.databinding.LayoutCustomSwitchButtonBinding

class SwitchButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: LayoutCustomSwitchButtonBinding
    private var mSwitchStatus = false
    var icon: Int = -1
    var unIcon: Int = -1

    init {
        binding = LayoutCustomSwitchButtonBinding.inflate(LayoutInflater.from(context), this, true)
        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.SwitchButton, 0, 0)
        icon = attributes.getResourceId(R.styleable.SwitchButton_sbIcon, -1)
        unIcon = attributes.getResourceId(R.styleable.SwitchButton_sbUnIcon, -1)
        val title = attributes.getString(R.styleable.SwitchButton_sbTitle) ?: ""
        val select = attributes.getBoolean(R.styleable.SwitchButton_sbSelect, false)

        if (title.isNotEmpty()) {
            binding.btnTitle.text = title
        }

        updateSwitchStatus(select)
    }

    fun updateSwitchStatus(status: Boolean) {
        if (mSwitchStatus != status) {
            mSwitchStatus = status
        }

        if (icon != -1 && unIcon != -1) {
            binding.btnIcon.setImageResource(if (status) icon else unIcon)
        }
        binding.btnTitle.alpha = if (status) 1f else 0.3f
    }

}
