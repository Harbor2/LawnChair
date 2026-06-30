package com.example.setting

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.negative_screen.databinding.LayoutTitleArrowItemBinding

class TitleArrowItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: LayoutTitleArrowItemBinding

    init {
        binding = LayoutTitleArrowItemBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun updateView(title: String) {
        binding.tvTitle.text = title
    }
}
