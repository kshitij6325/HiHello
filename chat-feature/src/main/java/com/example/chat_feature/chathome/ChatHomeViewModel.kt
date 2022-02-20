package com.example.chat_feature.chathome

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import androidx.work.*
import com.example.auth.usecase.GetLoggedInUserUseCase
import com.example.chat_data.usecase.GetAllChatsUseCase
import com.example.chat_data.usecase.SendChatUseCase
import com.example.chat_feature.work.RetryFailedChatsWorker
import com.example.chat_feature.work.SyncUserWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatHomeViewModel @Inject constructor(
    application: Application,
    private val getAllChatsUseCase: GetAllChatsUseCase,
    private val getLoggedInUserUseCase: GetLoggedInUserUseCase,
    private val sendChatUseCase: SendChatUseCase,
) : ViewModel() {

    private val _chatHomeUiState = MutableStateFlow(ChatHomeUI())
    val chatHomeUiStateLiveData = _chatHomeUiState.asStateFlow()

    private val _newChatUiState = MutableStateFlow(NewChatBsUI())
    val newChatUiStateLiveData = _newChatUiState.asStateFlow()

    private val syncUserWorkRequest by lazy {
        OneTimeWorkRequestBuilder<SyncUserWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).build()
    }

    private val retrySendChats by lazy {
        OneTimeWorkRequestBuilder<RetryFailedChatsWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).build()
    }

    init {
        syncUsersAndRetryChats(application.applicationContext)
        fetchUserChats()
        fetchWelcomeMessage()
    }

    private fun syncUsersAndRetryChats(context: Context) {
        with(WorkManager.getInstance(context)) {
            _chatHomeUiState.update {
                it.copy(
                    userSyncing = true,
                    userSyncSuccess = null
                )
            }
            beginWith(syncUserWorkRequest).then(retrySendChats).enqueue()
            getWorkInfoByIdLiveData(syncUserWorkRequest.id)
                .observeForever { wf ->
                    _chatHomeUiState.update {
                        it.copy(
                            userSyncing = false,
                            userSyncSuccess = wf?.state == WorkInfo.State.SUCCEEDED
                        )
                    }
                }

        }
    }

    fun sendChat(userName: String, message: String) = viewModelScope.launch {
        sendChatUseCase.apply {
            onSuccess = {
                _newChatUiState.update {
                    it.copy(isSuccess = true)
                }
            }

            onFailure = { ex ->
                _newChatUiState.update {
                    it.copy(error = ex.message)
                }
            }
        }.invoke(message, userName)
    }


    private fun fetchWelcomeMessage() = viewModelScope.launch {
        getLoggedInUserUseCase.apply {
            onSuccess = { user ->
                _chatHomeUiState.update {
                    it.copy(
                        welcomeString = "Welcome, ${user?.userName}",
                        userAvatar = user?.profileUrl
                    )
                }

            }
        }.invoke()
    }

    private fun fetchUserChats() = viewModelScope.launch {
        getAllChatsUseCase.get().collect { list ->
            _chatHomeUiState.update {
                it.copy(loading = false, userChatList = list)
            }
        }
    }
}