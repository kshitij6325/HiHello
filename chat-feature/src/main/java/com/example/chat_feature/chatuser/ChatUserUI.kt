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
    val mediaSource: MediaSource.File? = null,
    val scrollToLatestChat: Boolean = false,
    val clearEditText: Int = 0,
)

sealed class ChatUI(val type: Int) {
    class DateItem(val date: String) : ChatUI(DATE)
    sealed class ChatItem(val chat: Chat, type: Int) : ChatUI(type) {
        class ChatItemSent(chat: Chat) : ChatItem(chat, CHAT_SENT)
        class ChatItemReceived(chat: Chat) : ChatItem(chat, CHAT_RECEIVED)
    }

    companion object {
        const val DATE = 1
        const val CHAT_SENT = 2
        const val CHAT_RECEIVED = 3;
    }

}