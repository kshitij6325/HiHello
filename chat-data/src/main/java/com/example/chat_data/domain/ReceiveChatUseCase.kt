package com.example.chat_data.domain

import com.example.auth.data.repo.UserRepository
import com.example.chat_data.data.Chat
import com.example.chat_data.data.datasource.ChatType
import com.example.chat_data.data.repo.ChatRepository
import com.example.pojo.BaseUseCase
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalSerializationApi
@Singleton
class ReceiveChatUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository
) : BaseUseCase<Chat>() {

    suspend operator fun invoke(jsonChatString: String) {
        val chat = Json.decodeFromString<Chat>(jsonChatString)
        userRepository.createUserIfNotExists(chat.userId).onSuccess {
            saveChat(chat.copy(chatId = 0, type = ChatType.RECEIVED, success = true))
        }.onFailure { onFailure?.invoke(it) }
    }

    private suspend fun saveChat(chat: Chat) {
        chatRepository.addChat(chat).onSuccess {
            onSuccess?.invoke(chat)
        }.onFailure { onFailure?.invoke(it) }
    }
}