package com.example.chat_feature

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import androidx.work.*
import com.example.auth.User
import com.example.auth.repo.UserRepository
import com.example.auth.usecase.GetLoggedInUserUseCase
import com.example.basefeature.getFile
import com.example.chat_data.datasource.ChatType
import com.example.chat_data.usecase.GetAllChatsUseCase
import com.example.chat_data.usecase.GetAllUserChatUseCase
import com.example.chat_data.usecase.SendChatUseCase
import com.example.chat_feature.chathome.ChatHomeUI
import com.example.chat_feature.chathome.NewChatBsUI
import com.example.chat_feature.chatuser.ChatUI
import com.example.chat_feature.chatuser.ChatUserUI
import com.example.chat_feature.work.RetryFailedChatsWorker
import com.example.chat_feature.work.SyncUserWorker
import com.example.media_data.MediaSource
import com.example.media_data.MediaType
import com.example.pojo.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatHomeViewModel @Inject constructor(
    application: Application,
    private val getAllChatsUseCase: GetAllChatsUseCase,
    private val getLoggedInUserUseCase: GetLoggedInUserUseCase,
    private val sendChatUseCase: SendChatUseCase,
    private val getAllUserChatUseCase: GetAllUserChatUseCase,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _chatHomeUiState = MutableStateFlow(ChatHomeUI())
    val chatHomeUiStateLiveData = _chatHomeUiState.asStateFlow()

    private val _newChatUiState = MutableStateFlow(NewChatBsUI())
    val newChatUiStateLiveData = _newChatUiState.asStateFlow()

    private val _chatUserUiState = MutableStateFlow(ChatUserUI())
    val chatUserUiStateLiveData = _chatUserUiState.asStateFlow()

    private var offset = 0
    private var isLoading = false
    private var isFullChatLoaded = false
    private var isInitialChatLoaded = false
    private val paginationPreFetchThreshold = 5

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

    suspend fun initUserChats(scope: CoroutineScope, userName: String) {
        val userRes = userRepository.getLocalUser(userName)
        if (userRes is Result.Success) {
            _chatUserUiState.update {
                it.copy(currentUser = userRes.data)
            }
        }
        subscribeToLatestUserChat(userName).launchIn(scope)
        fetchMoreUserChat(userName)
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

    suspend fun fetchMore(dy: Int, firstItemPos: Int) {
        if (dy <= 0 && firstItemPos <= paginationPreFetchThreshold) {
            fetchMoreUserChat(_chatUserUiState.value.currentUser?.userName ?: "")
        }
    }

    private suspend fun fetchMoreUserChat(userName: String) {
        if (isLoading || isFullChatLoaded) {
            return
        }
        isLoading = true
        when (val res = getAllUserChatUseCase.getUserChat(userId = userName, offset)) {
            is Result.Success -> {
                val newChatList = mutableListOf<ChatUI>()
                var oldChat = _chatUserUiState.value.chatList.filterIsInstance<ChatUI.ChatItem>()
                    .firstOrNull()?.chat

                // reached the top of conversation
                if (res.data.isEmpty() && oldChat != null && !isFullChatLoaded) {
                    newChatList.add(0, ChatUI.DateItem(oldChat.date.getDateString()))
                    isFullChatLoaded = true
                }

                for (newChat in res.data) {
                    if (oldChat != null && !newChat.date.isSameDay(oldChat.date)) {
                        newChatList.add(0, ChatUI.DateItem(oldChat.date.getDateString()))
                    }
                    val chatToAdd =
                        if (newChat.type == ChatType.SENT) ChatUI.ChatItem.ChatItemSent(
                            newChat
                        ) else ChatUI.ChatItem.ChatItemReceived(
                            newChat
                        )
                    newChatList.add(0, chatToAdd)
                    oldChat = newChat

                }
                _chatUserUiState.update {
                    it.copy(chatList = newChatList + it.chatList)
                }
                offset += 10
                Log.e("NEXT OFFSET IS::", offset.toString())
                isLoading = false
            }
            else -> {
            }
        }
    }

    private fun subscribeToLatestUserChat(userName: String) =
        getAllUserChatUseCase.getLatestChatFlow(userName)
            .map { newChat ->
                val newChatList = mutableListOf<ChatUI>()

                val oldChat = _chatUserUiState.value.chatList.filterIsInstance<ChatUI.ChatItem>()
                    .lastOrNull()?.chat

                if (oldChat != null && !newChat.date.isSameDay(oldChat.date)
                ) {
                    newChatList.add(ChatUI.DateItem(newChat.date.getDateString()))
                }
                val chatToAdd = if (newChat.type == ChatType.SENT) ChatUI.ChatItem.ChatItemSent(
                    newChat
                ) else ChatUI.ChatItem.ChatItemReceived(
                    newChat
                )
                newChatList.add(chatToAdd)
                return@map newChatList
            }.onEach { list ->
                if (isInitialChatLoaded) {
                    val chatNew = list.filterIsInstance<ChatUI.ChatItem>()
                    _chatUserUiState.update {
                        it.copy(
                            chatList = it.chatList + list,
                            newChatAdded = chatNew[0].chat.chatId
                        )
                    }
                    offset += list.filterIsInstance<ChatUI.ChatItem>().size
                }
                isInitialChatLoaded = true
            }


    fun sendChat(userName: String, message: String) = viewModelScope.launch {
        val mediaSrc = _chatUserUiState.value.mediaSource
        removeAttachment()
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
        }.invoke(message, userName, mediaSrc)
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

    fun removeAttachment() {
        _chatUserUiState.update {
            it.copy(mediaSource = null)
        }
    }

    suspend fun createAndSetImageFile(uri: Uri, resolver: ContentResolver) {
        val file = uri.getFile(resolver)
        _chatUserUiState.update {
            it.copy(mediaSource = MediaSource.File(file, MediaType.IMAGE))
        }
    }
}