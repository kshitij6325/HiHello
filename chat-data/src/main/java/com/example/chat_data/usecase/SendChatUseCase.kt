package com.example.chat_data.usecase

import android.graphics.Bitmap
import android.media.MediaDataSource
import com.example.auth.User
import com.example.auth.repo.FirebaseDataRepository
import com.example.auth.repo.UserRepository
import com.example.chat_data.Chat
import com.example.chat_data.datasource.ChatMedia
import com.example.chat_data.datasource.ChatType
import com.example.chat_data.repo.ChatRepository
import com.example.media_data.MediaRepository
import com.example.media_data.MediaSource
import com.example.media_data.MediaType
import com.example.pojo.BaseUseCase
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class SendChatUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    private val firebaseDataRepository: FirebaseDataRepository,
    private val mediaDataRepository: MediaRepository
) : BaseUseCase<Boolean>() {

    suspend operator fun invoke(
        message: String,
        userName: String,
        mediaSource: MediaSource.File? = null,
    ) {
        val chat = Chat(
            message = message,
            type = ChatType.SENT,
            timeStamp = System.currentTimeMillis(),
            userId = userName,
            media = mediaSource?.let {
                ChatMedia(
                    localPath = mediaSource.file.absolutePath,
                    type = mediaSource.mediaType
                )
            }
        )
        userRepository.createUserIfNotExists(userName).onSuccess { user ->
            sendChat(chat, user, mediaSource)

        }.onFailure { onFailure?.invoke(it) }
    }

    private suspend fun sendChat(chat: Chat, user: User, mediaSource: MediaSource.File?) {
        if (chat.message.isNullOrEmpty() && chat.media == null) {
            return
        }
        chatRepository.addChat(chat).map { chatId ->
            firebaseDataRepository.getAppSecret().map { appSecret ->
                userRepository.getLoggedInUser().map { self ->
                    if (mediaSource != null) mediaDataRepository.uploadMedia(
                        mediaSource,
                        "${chatId}_${self.userName}_${user.userName}",

                        ) {}.map { mediaSrc ->
                        chatRepository.sendChat(
                            user,
                            appSecret,
                            chat.copy(
                                userId = self.userName,
                                media = chat.media?.copy(localPath = null, url = mediaSrc.url)
                            )
                        ).map {
                            chatRepository.updateChat(
                                chat.copy(
                                    chatId = chatId,
                                    media = chat.media?.copy(url = mediaSrc.url),
                                    success = true
                                )
                            ).onSuccess {
                                onSuccess?.invoke(it)
                            }.onFailure {
                                onFailure?.invoke(it)
                            }
                        }
                    } else chatRepository.sendChat(
                        user,
                        appSecret,
                        chat.copy(userId = self.userName)
                    ).map {
                        chatRepository.updateChatSuccess(chatId.toString(), true)
                            .onSuccess {
                                onSuccess?.invoke(it)
                            }.onFailure {
                                onFailure?.invoke(it)
                            }
                    }
                }

            }

        }.catch {
            onFailure?.invoke(it)
        }
    }

}