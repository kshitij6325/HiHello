package com.example.chat_feature.chatuser

import com.example.auth.User
import com.example.chat_data.Chat
import com.example.media_data.MediaSource
import java.io.File

data class ChatUserUI(
    val error: String? = null,
    val chatList: List<ChatUI> = listOf(),
    val currentUser: User? = null,
    val chatSentSuccess: Boolean = false,
    val mediaSource: MediaSource.File? = null
)

sealed class ChatUI(val type: Int) {
    class DateItem(val date: String) : ChatUI(1)
    sealed class ChatItem(val chat: Chat, type: Int) : ChatUI(type) {
        class ChatItemSent(chat: Chat) : ChatItem(chat, 2)
        class ChatItemReceived(chat: Chat) : ChatItem(chat, 3)
    }
}