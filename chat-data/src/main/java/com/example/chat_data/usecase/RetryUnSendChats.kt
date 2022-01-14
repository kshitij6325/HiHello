package com.example.chat_data.usecase

import com.example.auth.repo.FirebaseDataRepository
import com.example.auth.repo.UserRepository
import com.example.chat_data.repo.ChatRepository
import com.example.pojo.BaseUseCase
import javax.inject.Inject

class RetryUnSendChats @Inject constructor(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    private val firebaseDataRepository: FirebaseDataRepository
) : BaseUseCase<List<String>>() {

    suspend operator fun invoke() {
        val listOfUnSuccessFullChats = mutableListOf<String>()
        chatRepository.getAllUnSendChats().onSuccess {
            for (chat in it) {
                userRepository.createUserIfNotExists(chat.userId).map { user ->
                    firebaseDataRepository.getAppSecret().map { appSecret ->
                        userRepository.getLoggedInUser().map { self ->
                            chatRepository.sendChat(
                                user,
                                appSecret,
                                chat.copy(userId = self.userName)
                            ).map {
                                chatRepository.updateChatSuccess(
                                    chat.chatId.toString(),
                                    true
                                )
                            }.onFailure { listOfUnSuccessFullChats.add(chat.userId) }
                        }
                    }
                }.catch { listOfUnSuccessFullChats.add(chat.chatId.toString()) }
            }
            onSuccess?.invoke(listOfUnSuccessFullChats)
        }
    }
}