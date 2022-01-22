package com.example.chat_data.usecase

import com.example.auth.User
import com.example.auth.repo.UserRepository
import com.example.chat_data.Chat
import com.example.chat_data.repo.ChatRepository
import com.example.pojo.BaseUseCase
import dagger.hilt.android.scopes.ViewModelScoped
import java.lang.Exception
import javax.inject.Inject

@ViewModelScoped
class GetAllChatsUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository
) : BaseUseCase<List<Pair<User, Chat>>>() {

    suspend operator fun invoke() {
        val resList = mutableListOf<Pair<User, Chat>>()
        val errorList = mutableListOf<String>()
        userRepository.getAllLocalUsers().onSuccess { userList ->
            for (user in userList) {
                chatRepository.getAllUserChat(user.userName, 1).onSuccess {
                    if (it.isNotEmpty()) {
                        resList.add(user to it[0])
                    }
                }.onFailure {
                    errorList.add(user.userName)
                }
            }
            onSuccess?.invoke(resList)
        }.onFailure {
            onFailure?.invoke(Exception("Error loading user chats"))
        }
    }
}