package com.example.chat_feature

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.usecase.GetUserLoggedInUseCase
import com.example.basefeature.update
import com.example.chat_data.usecase.GetAllChatsUseCase
import com.example.chat_feature.chathome.ChatHomeUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatHomeViewModel @Inject constructor(
    private val getAllChatsUseCase: GetAllChatsUseCase,
    private val getLoggedInUseCase: GetUserLoggedInUseCase
) : ViewModel() {

    private val _chatHomeUiState = MutableLiveData(ChatHomeUI())
    val chatHomeUiStateLiveData: LiveData<ChatHomeUI> = _chatHomeUiState


    fun fetchWelcomeMessage() = viewModelScope.launch {
        getLoggedInUseCase.apply {
            onSuccess = { user ->
                _chatHomeUiState.update {
                    it?.copy(welcomeString = "Welcome, ${user?.userName}")
                }

            }
        }.invoke()
    }

    fun fetchUserChats() = viewModelScope.launch {
        getAllChatsUseCase.get().collect { list ->
            _chatHomeUiState.update {
                it?.copy(loading = false, userChatList = list)
            }
        }
    }

}