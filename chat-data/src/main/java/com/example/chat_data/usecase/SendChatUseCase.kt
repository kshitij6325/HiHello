package com.example.chat_data.usecase

import com.example.auth.User
import com.example.auth.repo.FirebaseDataRepository
import com.example.auth.repo.UserRepository
import com.example.chat_data.Chat
import com.example.chat_data.datasource.ChatType
import com.example.chat_data.repo.ChatRepository
import com.example.pojo.BaseUseCase
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class SendChatUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    private val firebaseDataRepository: FirebaseDataRepository
) : BaseUseCase<Boolean>() {

    suspend operator fun invoke(message: String, userName: String) {
        val chat = Chat(
            message = message,
            type = ChatType.SENT,
            timeStamp = System.currentTimeMillis(),
            userId = userName
        )
        userRepository.createUserIfNotExists(userName).onSuccess {
            sendChat(chat, it)
        }.onFailure { onFailure?.invoke(it) }
    }

    private suspend fun sendChat(chat: Chat, user: User) {
        chatRepository.addChat(chat).map { chatId ->
            firebaseDataRepository.getAppSecret().map { appSecret ->
                userRepository.getLoggedInUser().map { self ->
                    chatRepository.sendChat(user, appSecret, chat.copy(userId = self.userName))
                        .map {
                            chatRepository.updateChatSuccess(chatId.toString(), true).onSuccess {
                                onSuccess?.invoke(it)
                            }.onFailure { onFailure?.invoke(it) }
                        }
                }
            }

        }.catch { onFailure?.invoke(it) }
    }


}