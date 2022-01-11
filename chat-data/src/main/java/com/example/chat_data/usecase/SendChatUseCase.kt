package com.example.chat_data.usecase

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
    private val chatRepository: ChatRepository
) : BaseUseCase<Boolean>() {

    suspend operator fun invoke(message: String, userName: String) {
        userRepository.getRemoteUser(userName).map { user ->
            userRepository.crateLocalUser(user).map {
                val chat = Chat(
                    message = message,
                    type = ChatType.SENT,
                    timeStamp = System.currentTimeMillis(),
                    userId = userName
                )
                chatRepository.addChat(chat).map { chatId ->
                    userRepository.getAppSecret().map {
                        chatRepository.sendChat(user, it, chat).map {
                            chatRepository.updateChatSuccess(chatId.toString(), true).onSuccess {
                                onSuccess?.invoke(true)
                            }.onFailure { onFailure?.invoke(it) }
                        }
                    }
                }
            }
        }.catch {
            onFailure?.invoke(it)
        }
    }


}