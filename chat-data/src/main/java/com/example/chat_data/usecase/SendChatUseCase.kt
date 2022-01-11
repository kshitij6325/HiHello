package com.example.chat_data.usecase

import com.example.auth.NoSuchUserException
import com.example.auth.User
import com.example.auth.repo.UserRepository
import com.example.chat_data.Chat
import com.example.chat_data.datasource.ChatType
import com.example.chat_data.repo.ChatRepository
import com.example.pojo.BaseUseCase
import com.example.pojo.Result
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class SendChatUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository
) : BaseUseCase<Boolean>() {

    suspend operator fun invoke(message: String, userName: String) {
        val chat = Chat(
            message = message,
            type = ChatType.SENT,
            timeStamp = System.currentTimeMillis(),
            userId = userName
        )
        val userExistsRes = userRepository.getLocalUser(userName)
        when {
            userExistsRes is Result.Success -> sendChat(chat, userExistsRes.data)
            userExistsRes is Result.Failure && userExistsRes.exception is NoSuchUserException -> createUserAndSendChat(
                chat,
                userName
            )
            userExistsRes is Result.Failure -> onFailure?.invoke(userExistsRes.exception)
        }
    }

    private suspend fun createUserAndSendChat(chat: Chat, userName: String) {
        userRepository.getRemoteUser(userName).map { user ->
            userRepository.createLocalUser(user).onSuccess {
                sendChat(chat, user)
            }.onFailure { onFailure?.invoke(it) }
        }.catch {
            onFailure?.invoke(it)
        }
    }

    private suspend fun sendChat(chat: Chat, user: User) {
        chatRepository.addChat(chat).map { chatId ->
            userRepository.getAppSecret().map {
                chatRepository.sendChat(user, it, chat).map {
                    chatRepository.updateChatSuccess(chatId.toString(), true).onSuccess {
                    }.onFailure { onFailure?.invoke(it) }
                }
            }
        }.catch { onFailure?.invoke(it) }
    }


}