package com.example.chat_feature.chatuser

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.marginLeft
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.example.auth.User
import com.example.basefeature.tintBackground
import com.example.chat_data.Chat
import com.example.chat_data.datasource.ChatType
import com.example.chat_feature.databinding.ItemChatBinding
import com.example.chat_feature.databinding.ItemChatHomeBinding

class ChatUserListAdapter : RecyclerView.Adapter<ChatUserViewHolder>() {

    var list: List<Chat> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatUserViewHolder {
        val binding =
            ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        when (ChatType.fromInt(viewType)) {
            ChatType.SENT -> {
                binding.tvMessage.updateLayoutParams {
                    (this as ViewGroup.MarginLayoutParams).marginStart = 32
                    (this as FrameLayout.LayoutParams).gravity = Gravity.END
                }
                binding.tvMessage.tintBackground(
                    ContextCompat.getColor(
                        parent.context,
                        android.R.color.holo_blue_bright
                    )
                )
            }
            ChatType.RECEIVED -> {
                binding.tvMessage.updateLayoutParams {
                    (this as ViewGroup.MarginLayoutParams).marginEnd = 32
                    (this as FrameLayout.LayoutParams).gravity = Gravity.START
                }
                binding.tvMessage.tintBackground(
                    ContextCompat.getColor(
                        parent.context,
                        android.R.color.holo_green_light
                    )
                )
            }
        }

        return ChatUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatUserViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int) = list[position].type.ordinal

}

class ChatUserViewHolder(private val binding: ItemChatBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(data: Chat) {
        binding.tvMessage.text = data.message
    }
}