package com.example.chat_feature.chatuser.viewholder

import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.Glide
import com.example.basefeature.*
import com.example.chat_feature.R
import com.example.chat_feature.chatuser.ChatUI
import com.example.chat_feature.databinding.ItemChatBinding
import com.example.media_data.MediaType
import java.io.File

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

