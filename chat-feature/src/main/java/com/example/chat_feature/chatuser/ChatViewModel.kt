package com.example.chat_feature.chatuser

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import androidx.work.*
import com.example.auth.data.repo.UserRepository
import com.example.basefeature.getFile
import com.example.chat_data.data.datasource.ChatType
import com.example.chat_data.domain.GetAllUserChatUseCase
import com.example.chat_data.domain.SendChatUseCase
import com.example.media_data.MediaSource
import com.example.media_data.MediaType
import com.example.pojo.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val PAGINATION_ITEM_THRESHOLD = 5
private const val PAGE_SIZE = 10

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendChatUseCase: SendChatUseCase,
    private val getAllUserChatUseCase: GetAllUserChatUseCase,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _chatUserUiState = MutableStateFlow(ChatUserUI())
    val chatUserUiStateLiveData = _chatUserUiState.asStateFlow()

    private var offset = 0
    private var isLoading = false
    private var isFullChatLoaded = false
    private var canSubscribeToNewChatFlow = false
    private val paginationPreFetchThreshold = PAGINATION_ITEM_THRESHOLD

    suspend fun init(scope: CoroutineScope, userName: String) {
        val userRes = userRepository.getLocalUser(userName)
        if (userRes is Result.Success) {
            _chatUserUiState.update {
                it.copy(currentUser = userRes.data)
            }
        }
        subscribeToLatestUserChat(userName).launchIn(scope)
        fetchMoreUserChat(userName)
    }

    suspend fun onScroll(dy: Int, firstItemPos: Int, canScrollDown: Boolean) {
        _chatUserUiState.update {
            it.copy(scrollToLatestChat = !canScrollDown)
        }
        if (dy <= 0 && firstItemPos <= paginationPreFetchThreshold) {
            fetchMoreUserChat(_chatUserUiState.value.currentUser?.userName ?: "")
        }
    }

    private suspend fun fetchMoreUserChat(userName: String) {
        if (isLoading || isFullChatLoaded) {
            return
        }
        isLoading = true
        when (val res = getAllUserChatUseCase.getUserChat(userId = userName, PAGE_SIZE, offset)) {
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
                offset += PAGE_SIZE
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

                if (oldChat != null && !newChat.date.isSameDay(oldChat.date)) {
                    newChatList.add(ChatUI.DateItem(newChat.date.getDateString()))
                }
                val chatToAdd = if (newChat.type == ChatType.SENT) ChatUI.ChatItem.ChatItemSent(
                    newChat
                ) else ChatUI.ChatItem.ChatItemReceived(newChat)
                newChatList.add(chatToAdd)
                return@map newChatList
            }.onEach { list ->
                if (canSubscribeToNewChatFlow) {
                    _chatUserUiState.update {
                        it.copy(
                            chatList = it.chatList + list,
                            clearEditText = it.clearEditText + 1,
                        )
                    }
                    offset += list.filterIsInstance<ChatUI.ChatItem>().size
                }
                canSubscribeToNewChatFlow = true
            }


    fun sendChat(userName: String, message: String) = viewModelScope.launch {
        val mediaSrc = _chatUserUiState.value.mediaSource
        removeAttachment()
        sendChatUseCase.invoke(message, userName, mediaSrc)
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