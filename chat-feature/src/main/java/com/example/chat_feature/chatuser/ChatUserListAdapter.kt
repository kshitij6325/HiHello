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
            1 -> ChatDateViewHolder(
                ItemDateBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            ) as BaseViewHolder<ChatUI>
            2 -> ChatSentViewHolder(
                ItemChatBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            ) as BaseViewHolder<ChatUI>
            3 -> ChatReceivedVieHolder(
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

abstract class BaseViewHolder<in D : ChatUI>(binding: ViewBinding) :
    RecyclerView.ViewHolder(binding.root) {
    abstract fun onBind(data: D)
}

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
        binding.tvDate.setTextWithVisibility(data.date)
    }
}

class ChatReceivedVieHolder(private val binding: ItemChatBinding) :
    BaseViewHolder<ChatUI.ChatItem>(binding) {
    private val dp16 = binding.root.getDimen(R.dimen.dp_16)
    private val dp50 = binding.root.getDimen(R.dimen.dp_50)


    init {
        binding.clContainer.updateLayoutParams {
            (this as ViewGroup.MarginLayoutParams).run {
                marginStart = dp16
                marginEnd = dp50
            }
            (this as FrameLayout.LayoutParams).gravity = Gravity.START
        }

        binding.clContainer.tintBackground(
            ContextCompat.getColor(
                binding.root.context,
                R.color.light_blue
            )
        )
        binding.tvMessage.setTextColor(
            ContextCompat.getColor(
                binding.root.context,
                R.color.black
            )
        )
        binding.ivMessageState.gone()
    }

    override fun onBind(data: ChatUI.ChatItem) {
        val data = data.chat
        binding.tvMessage.setTextWithVisibility(data.message)

        binding.ivMessageState.setImageResource(if (data.success) R.drawable.ic_outline_done_24 else R.drawable.ic_baseline_access_time_24)

        binding.tvTime.text = data.date.getTimeString()

        binding.ivMedia.visibleIf(data.media != null)
        if (data.media?.type == MediaType.IMAGE) {
            Glide.with(binding.root.context).apply {
                (data.media?.url?.let { load(it) }
                    ?: data.media?.localPath?.let { load(File(it)) })?.into(binding.ivMedia)
            }
        }

    }
}

class ChatSentViewHolder(private val binding: ItemChatBinding) :
    BaseViewHolder<ChatUI.ChatItem>(binding) {

    private val dp16 = binding.root.getDimen(R.dimen.dp_16)
    private val dp50 = binding.root.getDimen(R.dimen.dp_50)

    init {
        binding.clContainer.updateLayoutParams {
            (this as ViewGroup.MarginLayoutParams).run {
                marginStart = dp50
                marginEnd = dp16
            }
            (this as FrameLayout.LayoutParams).gravity = Gravity.END
        }
        binding.clContainer.tintBackground(
            ContextCompat.getColor(
                binding.root.context,
                R.color.blue
            )
        )
        binding.tvTime.setTextColor(
            ContextCompat.getColor(
                binding.root.context,
                R.color.white_variant
            )
        )
        binding.tvMessage.setTextColor(
            ContextCompat.getColor(
                binding.root.context,
                R.color.white
            )
        )
        binding.ivMessageState.setTint(R.color.white)
    }

    override fun onBind(data: ChatUI.ChatItem) {
        val data = data.chat
        binding.tvMessage.setTextWithVisibility(data.message)

        binding.ivMessageState.setImageResource(if (data.success) R.drawable.ic_outline_done_24 else R.drawable.ic_baseline_access_time_24)

        binding.tvTime.text = data.date.getTimeString()

        binding.ivMedia.visibleIf(data.media != null)
        if (data.media?.type == MediaType.IMAGE) {
            Glide.with(binding.root.context).apply {
                (data.media?.url?.let { load(it) }
                    ?: data.media?.localPath?.let { load(File(it)) })?.into(binding.ivMedia)
            }
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