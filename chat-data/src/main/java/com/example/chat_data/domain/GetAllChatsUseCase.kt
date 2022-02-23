package com.example.chat_data.domain

import com.example.auth.data.User
import com.example.auth.data.repo.UserRepository
import com.example.chat_data.data.Chat
import com.example.chat_data.data.repo.ChatRepository
import com.example.pojo.BaseUseCase
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.map
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