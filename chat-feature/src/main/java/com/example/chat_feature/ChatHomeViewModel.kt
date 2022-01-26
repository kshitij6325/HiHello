package com.example.chat_feature

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import androidx.work.*
import com.example.auth.usecase.GetUserLoggedInUseCase
import com.example.basefeature.update
import com.example.chat_data.Chat
import com.example.chat_data.usecase.GetAllChatsUseCase
import com.example.chat_data.usecase.GetAllUserChatUseCase
import com.example.chat_data.usecase.SendChatUseCase
import com.example.chat_feature.chathome.ChatHomeUI
import com.example.chat_feature.chathome.NewChatBsUI
import com.example.chat_feature.chatuser.ChatUserUI
import com.example.chat_feature.work.RetryFailedChatsWorker
import com.example.chat_feature.work.SyncUserWorker
import com.example.media_data.MediaSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import kotlin.io.path.outputStream

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

    private var flow: Flow<List<Chat>>? = null

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
                it?.copy(
                    userSyncing = true,
                    userSyncSuccess = null
                )
            }
            beginWith(syncUserWorkRequest).then(retrySendChats).enqueue()
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

    suspend fun subscribeToUserChat(userName: String) {
        getAllUserChatUseCase.get(userName).collect { list ->
            _chatUserUiState.update {
                it?.copy(chatList = list)
            }
        }
    }


    fun sendChat(userName: String, message: String) = viewModelScope.launch {
        val mediaSrc = _chatUserUiState.value?.mediaSource
        removeAttachment()
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
        }.invoke(message, userName, mediaSrc)
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

    fun removeAttachment() {
        _chatUserUiState.update {
            it?.copy(mediaSource = null)
        }
    }

    suspend fun createAndSetImageFile(uri: Uri, resolver: ContentResolver) =
        withContext(Dispatchers.IO) {
            val file = kotlin.io.path.createTempFile("file", "png")
            val inputStream = resolver.openInputStream(uri)

            inputStream.use { input ->
                file.outputStream().use { output ->
                    input?.copyTo(output)
                }
            }
            _chatUserUiState.postValue(
                _chatUserUiState.value?.copy(
                    mediaSource = MediaSource.File.ImageFile(
                        file.toFile()
                    )
                )
            )
        }
}