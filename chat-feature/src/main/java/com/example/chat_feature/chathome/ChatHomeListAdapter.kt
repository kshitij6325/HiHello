package com.example.chat_feature.chathome

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.auth.User
import com.example.chat_data.Chat
import com.example.chat_feature.databinding.ItemChatHomeBinding

class ChatHomeListAdapter : RecyclerView.Adapter<ChatHomeViewHolder>() {

    var list: List<Pair<User, Chat>> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHomeViewHolder {
        val binding =
            ItemChatHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatHomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatHomeViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

}

class ChatHomeViewHolder(private val binding: ItemChatHomeBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(data: Pair<User, Chat>) {
        binding.tvChat.text = data.second.message
        binding.tvUsername.text = data.first.userName
    }
}