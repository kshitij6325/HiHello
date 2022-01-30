package com.example.chat_feature.chatuser

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.marginLeft
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.auth.User
import com.example.basefeature.*
import com.example.chat_data.Chat
import com.example.chat_data.datasource.ChatType
import com.example.chat_feature.R
import com.example.chat_feature.chatuser.ChatUI.Companion.CHAT_RECEIVED
import com.example.chat_feature.chatuser.ChatUI.Companion.CHAT_SENT
import com.example.chat_feature.chatuser.ChatUI.Companion.DATE
import com.example.chat_feature.chatuser.viewholder.ChatDateViewHolder
import com.example.chat_feature.chatuser.viewholder.ChatReceivedVieHolder
import com.example.chat_feature.chatuser.viewholder.ChatSentViewHolder
import com.example.chat_feature.databinding.ItemChatBinding
import com.example.chat_feature.databinding.ItemChatHomeBinding
import com.example.chat_feature.databinding.ItemDateBinding
import com.example.media_data.MediaType
import java.io.File

class ChatUserListAdapter :
    ListAdapter<ChatUI, BaseViewHolder<ChatUI>>(ChatUserListDiffUtils()) {

    override fun getItemCount(): Int = currentList.size

    override fun getItemViewType(position: Int) = currentList[position].type

    override fun onBindViewHolder(holder: BaseViewHolder<ChatUI>, position: Int) {
        holder.onBind(currentList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ChatUI> {
        return when (viewType) {
            DATE -> ChatDateViewHolder(
                ItemDateBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            ) as BaseViewHolder<ChatUI>
            CHAT_SENT -> ChatSentViewHolder(
                ItemChatBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            ) as BaseViewHolder<ChatUI>
            CHAT_RECEIVED -> ChatReceivedVieHolder(
                ItemChatBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            ) as BaseViewHolder<ChatUI>
            else -> ChatDateViewHolder(
                ItemDateBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            ) as BaseViewHolder<ChatUI>
        }
    }
}


class ChatUserListDiffUtils : DiffUtil.ItemCallback<ChatUI>() {
    override fun areItemsTheSame(oldItem: ChatUI, newItem: ChatUI): Boolean {
        return when {
            (oldItem is ChatUI.ChatItem) && (newItem is ChatUI.ChatItem) -> oldItem.chat.chatId == newItem.chat.chatId
            oldItem is ChatUI.DateItem && newItem is ChatUI.DateItem -> oldItem.date == (newItem).date
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: ChatUI, newItem: ChatUI): Boolean {
        return when {
            (oldItem is ChatUI.ChatItem) && (newItem is ChatUI.ChatItem) -> oldItem.chat == newItem.chat
            oldItem is ChatUI.DateItem && newItem is ChatUI.DateItem -> oldItem.date == newItem.date
            else -> false
        }
    }

}