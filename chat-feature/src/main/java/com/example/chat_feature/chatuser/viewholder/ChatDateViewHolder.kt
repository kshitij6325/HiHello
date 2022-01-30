package com.example.chat_feature.chatuser.viewholder

import androidx.core.content.ContextCompat
import com.example.basefeature.BaseViewHolder
import com.example.basefeature.tintBackground
import com.example.chat_feature.R
import com.example.chat_feature.chatuser.ChatUI
import com.example.chat_feature.databinding.ItemDateBinding

class ChatDateViewHolder(private val binding: ItemDateBinding) :
    BaseViewHolder<ChatUI.DateItem>(binding) {

    init {
        binding.tvDate.tintBackground(
            ContextCompat.getColor(
                binding.root.context,
                R.color.white_variant
            )
        )
    }

    override fun onBind(data: ChatUI.DateItem) {
        binding.tvDate.text = data.date
    }
}

