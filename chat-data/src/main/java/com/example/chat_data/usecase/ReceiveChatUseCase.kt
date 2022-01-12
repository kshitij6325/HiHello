package com.example.chat_data.usecase

import com.example.auth.repo.UserRepository
import com.example.chat_data.Chat
import com.example.chat_data.repo.ChatRepository
import com.example.pojo.BaseUseCase
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@ViewModelScoped
class ReceiveChatUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository
) : BaseUseCase<Chat>() {

    suspend operator fun invoke(jsonChatString: String) {
        val chat = Json.decodeFromString<Chat>(jsonChatString)
        userRepository.createUserIfNotExists(chat.userId).onSuccess {
            saveChat(chat)
        }.onFailure { onFailure?.invoke(it) }
    }

    private suspend fun saveChat(chat: Chat) {
        chatRepository.addChat(chat).onSuccess {
            onSuccess?.invoke(chat)
        }.onFailure { onFailure?.invoke(it) }
    }
}