package com.example.chat_data.usecase

import androidx.lifecycle.LiveData
import com.example.chat_data.Chat
import com.example.chat_data.repo.ChatRepository
import com.example.pojo.BaseUseCase
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ViewModelScoped
class GetAllUserChatUseCase @Inject constructor(private val chatRepository: ChatRepository) :
    BaseUseCase<LiveData<List<Chat>>>() {

    fun get(userId: String) = chatRepository.getAllUserChatLiveData(userId)
}