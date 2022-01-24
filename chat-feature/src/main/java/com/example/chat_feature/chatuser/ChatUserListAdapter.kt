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
import com.example.auth.User
import com.example.basefeature.getDimen
import com.example.basefeature.gone
import com.example.basefeature.setTint
import com.example.basefeature.tintBackground
import com.example.chat_data.Chat
import com.example.chat_data.datasource.ChatType
import com.example.chat_feature.R
import com.example.chat_feature.databinding.ItemChatBinding
import com.example.chat_feature.databinding.ItemChatHomeBinding

class ChatUserListAdapter :
    ListAdapter<Chat, ChatUserViewHolder>(ChatUserListDiffUtils()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatUserViewHolder {
        val binding =
            ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val dp16 = parent.getDimen(R.dimen.dp_16)
        val dp50 = parent.getDimen(R.dimen.dp_50)

        when (ChatType.fromInt(viewType)) {
            ChatType.SENT -> {
                binding.clContainer.updateLayoutParams {
                    (this as ViewGroup.MarginLayoutParams).run {
                        marginStart = dp50
                        marginEnd = dp16
                    }
                    (this as FrameLayout.LayoutParams).gravity = Gravity.END
                }
                binding.clContainer.tintBackground(
                    ContextCompat.getColor(
                        parent.context,
                        R.color.blue
                    )
                )
                binding.tvMessage.setTextColor(
                    ContextCompat.getColor(
                        parent.context,
                        R.color.white
                    )
                )
                binding.ivMessageState.setTint(R.color.white)
            }
            ChatType.RECEIVED -> {
                binding.clContainer.updateLayoutParams {
                    (this as ViewGroup.MarginLayoutParams).run {
                        marginStart = dp16
                        marginEnd = dp50
                    }
                    (this as FrameLayout.LayoutParams).gravity = Gravity.START
                }
                binding.clContainer.tintBackground(
                    ContextCompat.getColor(
                        parent.context,
                        R.color.light_blue
                    )
                )
                binding.tvMessage.setTextColor(
                    ContextCompat.getColor(
                        parent.context,
                        R.color.black
                    )
                )
                binding.ivMessageState.gone()
            }
        }

        return ChatUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatUserViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getItemCount(): Int = currentList.size

    override fun getItemViewType(position: Int) = currentList[position].type.ordinal

}

class ChatUserViewHolder(private val binding: ItemChatBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(data: Chat) {
        binding.tvMessage.text = data.message
        binding.ivMessageState.setImageResource(if (data.success) R.drawable.ic_outline_done_24 else R.drawable.ic_baseline_access_time_24)
    }
}

class ChatUserListDiffUtils : DiffUtil.ItemCallback<Chat>() {
    override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
        return oldItem.chatId == newItem.chatId
    }

    override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
        return oldItem == newItem
    }

}