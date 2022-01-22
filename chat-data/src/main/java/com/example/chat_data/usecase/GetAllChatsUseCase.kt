package com.example.chat_data.usecase

import com.example.auth.User
import com.example.auth.repo.UserRepository
import com.example.chat_data.Chat
import com.example.chat_data.repo.ChatRepository
import com.example.pojo.BaseUseCase
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.map
import java.lang.Exception
import javax.inject.Inject

@ViewModelScoped
class GetAllChatsUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository
) : BaseUseCase<List<Pair<User, Chat>>>() {

    fun get() = chatRepository.getAllUserChat().map {
        val mutableList = mutableListOf<Pair<User, Chat>>()
        for ((user, chatList) in it) {
            if (chatList.isNotEmpty())
                mutableList.add(user to chatList[0])
        }
        return@map mutableList
    }
}