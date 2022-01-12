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
        val listOfUnsuccessFullChats = mutableListOf<String>()
        chatRepository.getAllUnSendChats().onSuccess {
            it.forEach { chat ->
                firebaseDataRepository.getAppSecret().map { appSecret ->
                    userRepository.getLoggedInUser().map { self ->
                        userRepository.getLocalUser(chat.userId).map { user ->
                            chatRepository.sendChat(
                                user, appSecret, chat.copy(userId = self.userName)
                            ).onFailure { listOfUnsuccessFullChats.add(chat.userId) }
                        }
                    }
                }
            }
            onSuccess?.invoke(listOfUnsuccessFullChats)
        }
    }
}