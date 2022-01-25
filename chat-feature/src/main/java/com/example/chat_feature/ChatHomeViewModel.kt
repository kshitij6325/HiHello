package com.example.chat_feature

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.auth.usecase.GetUserLoggedInUseCase
import com.example.auth.usecase.SyncUsersUseCase
import com.example.basefeature.showToast
import com.example.basefeature.update
import com.example.chat_data.usecase.GetAllChatsUseCase
import com.example.chat_data.usecase.GetAllUserChatUseCase
import com.example.chat_data.usecase.SendChatUseCase
import com.example.chat_feature.chathome.ChatHomeUI
import com.example.chat_feature.chathome.NewChatBsUI
import com.example.chat_feature.chatuser.ChatUserUI
import com.example.chat_feature.work.SyncUserWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatHomeViewModel @Inject constructor(
    application: Application,
    private val getAllChatsUseCase: GetAllChatsUseCase,
    private val getLoggedInUseCase: GetUserLoggedInUseCase,
    private val sendChatUseCase: SendChatUseCase,
    private val getAllUserChatUseCase: GetAllUserChatUseCase,
) : ViewModel() {

    private val _chatHomeUiState = MutableLiveData(ChatHomeUI())
    val chatHomeUiStateLiveData: LiveData<ChatHomeUI> = _chatHomeUiState

    private val _newChatUiState = MutableLiveData(NewChatBsUI())
    val newChatUiStateLiveData: LiveData<NewChatBsUI> = _newChatUiState

    private val _chatUserUiState = MutableLiveData(ChatUserUI())
    val chatUserUiStateLiveData: LiveData<ChatUserUI> = _chatUserUiState

    private val syncUserWorkRequest by lazy {
        OneTimeWorkRequestBuilder<SyncUserWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).build()
    }

    init {
        syncUsers(application.applicationContext)
        fetchUserChats()
        fetchWelcomeMessage()
    }

    private fun syncUsers(context: Context) {
        with(WorkManager.getInstance(context)) {
            _chatHomeUiState.update {
                it?.copy(
                    userSyncing = true,
                    userSyncSuccess = null
                )
            }
            enqueue(syncUserWorkRequest)
            getWorkInfoByIdLiveData(syncUserWorkRequest.id)
                .observeForever { wf ->
                    _chatHomeUiState.update {
                        it?.copy(
                            userSyncing = false,
                            userSyncSuccess = wf?.state == WorkInfo.State.SUCCEEDED
                        )
                    }
                }

        }
    }

    fun subscribeToUserChat(userName: String) {
        getAllUserChatUseCase.get(userName).observeForever { list ->
            _chatUserUiState.update {
                it?.copy(chatList = list)
            }
        }
    }


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


    private fun fetchWelcomeMessage() = viewModelScope.launch {
        getLoggedInUseCase.apply {
            onSuccess = { user ->
                _chatHomeUiState.update {
                    it?.copy(welcomeString = "Welcome, ${user?.userName}")
                }

            }
        }.invoke()
    }

    private fun fetchUserChats() = viewModelScope.launch {
        getAllChatsUseCase.get().collect { list ->
            _chatHomeUiState.update {
                it?.copy(loading = false, userChatList = list)
            }
        }
    }

}