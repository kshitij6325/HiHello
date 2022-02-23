package com.example.chat_data.domain

import androidx.lifecycle.LiveData
import com.example.chat_data.data.Chat
import com.example.chat_data.data.repo.ChatRepository
import com.example.pojo.BaseUseCase
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GetAllUserChatUseCase @Inject constructor(private val chatRepository: ChatRepository) :
    BaseUseCase<LiveData<List<Chat>>>() {

    fun getLatestChatFlow(userId: String) = chatRepository.getLatestUserChatFlow(userId)

    suspend fun getUserChat(userId: String, limit: Int, offset: Int) =
        chatRepository.getAllUserChat(userId, limit, offset)
}