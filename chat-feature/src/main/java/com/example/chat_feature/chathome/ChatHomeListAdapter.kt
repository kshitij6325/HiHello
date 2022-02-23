package com.example.chat_feature.chathome

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.auth.data.User
import com.example.chat_data.data.Chat
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
        return ChatHomeViewHolder(binding) {
            onItemClick(list[it].first)
        }
    }

    override fun onBindViewHolder(holder: ChatHomeViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size

}