package com.example.chat_feature

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.basefeature.update
import com.example.chat_data.usecase.SendChatUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatUserViewModel @Inject constructor(private val sendChatUseCase: SendChatUseCase) :
    ViewModel() {

    private val _newChatUiState = MutableLiveData(NewChatBsUI())
    val newChatUiStateLiveData: LiveData<NewChatBsUI> = _newChatUiState


    fun sendChat(userName: String, message: String) = viewModelScope.launch {
        sendChatUseCase.apply {
            onSuccess = {
                _newChatUiState.update {
                    it?.copy(isSuccess = true)
                }
            }

            onFailure = { ex ->
                _newChatUiState.update {
                    it?.copy(error = ex.message)
                }
            }
        }.invoke(message, userName)
    }

}