package com.example.chat_feature.chathome

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.auth.User
import com.example.basefeature.visibleIf
import com.example.chat_data.Chat
import com.example.chat_data.datasource.ChatType
import com.example.chat_feature.R
import com.example.chat_feature.databinding.ItemChatHomeBinding

class ChatHomeListAdapter(private val onItemClick: ((User) -> Unit)) :
    RecyclerView.Adapter<ChatHomeViewHolder>() {

    var list: List<Pair<User, Chat>> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHomeViewHolder {
        val binding =
            ItemChatHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatHomeViewHolder(binding).also { holder ->
            binding.root.setOnClickListener {
                onItemClick.invoke(list[holder.adapterPosition].first)
            }
        }
    }

    override fun onBindViewHolder(holder: ChatHomeViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

}

class ChatHomeViewHolder(private val binding: ItemChatHomeBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(data: Pair<User, Chat>) {
        val chat = data.second
        binding.tvChat.text = chat.message
        binding.tvUsername.text = data.first.userName
        binding.ivMessageState.visibleIf(chat.type == ChatType.SENT)
        binding.ivMessageState.setImageResource(if (chat.success) R.drawable.ic_outline_done_24 else R.drawable.ic_baseline_access_time_24)
    }
}