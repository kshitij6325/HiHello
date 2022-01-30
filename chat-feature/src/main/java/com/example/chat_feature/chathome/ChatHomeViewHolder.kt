package com.example.chat_feature.chathome

import com.bumptech.glide.Glide
import com.example.auth.User
import com.example.basefeature.BaseViewHolder
import com.example.basefeature.visibleIf
import com.example.chat_data.Chat
import com.example.chat_data.datasource.ChatType
import com.example.chat_feature.R
import com.example.chat_feature.databinding.ItemChatHomeBinding

class ChatHomeViewHolder(
    private val binding: ItemChatHomeBinding,
    onItemClick: ((Int) -> Unit)
) : BaseViewHolder<Pair<User, Chat>>(binding, onItemClick) {

    override fun onBind(data: Pair<User, Chat>) {
        val chat = data.second
        binding.tvChat.text = chat.message
        Glide.with(binding.ivAvatar).load(data.first.profileUrl).into(binding.ivAvatar)
        binding.ivAvatar
        binding.tvUsername.text = data.first.userName
        binding.ivMessageState.visibleIf(chat.type == ChatType.SENT)
        binding.ivMessageState.setImageResource(if (chat.success) R.drawable.ic_outline_done_24 else R.drawable.ic_baseline_access_time_24)
    }
}