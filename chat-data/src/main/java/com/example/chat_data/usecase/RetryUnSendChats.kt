package com.example.chat_data.usecase

import com.example.auth.repo.FirebaseDataRepository
import com.example.auth.repo.UserRepository
import com.example.chat_data.repo.ChatRepository
import com.example.media_data.MediaRepository
import com.example.media_data.MediaSource
import com.example.pojo.BaseUseCase
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetryUnSendChats @Inject constructor(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    private val firebaseDataRepository: FirebaseDataRepository,
    private val mediaRepository: MediaRepository
) : BaseUseCase<List<String>>() {

    suspend operator fun invoke() {
        val listOfUnSuccessFullChats = mutableListOf<String>()
        chatRepository.getAllUnSendChats().onSuccess {
            for (chat in it) {
                userRepository.createUserIfNotExists(chat.userId).map { user ->
                    firebaseDataRepository.getAppSecret().map { appSecret ->
                        userRepository.getLoggedInUser().map { self ->
                            if (chat.media != null && chat.media.url == null) {
                                mediaRepository.uploadMedia(
                                    MediaSource.File(File(chat.media.localPath), chat.media.type),
                                    "${chat.chatId}_${self.userName}_${user.userName}",

                                    ) {}.map { mediaSrc ->
                                    chatRepository.sendChat(
                                        user,
                                        appSecret,
                                        chat.copy(
                                            userId = self.userName,
                                            media = chat.media.copy(
                                                localPath = null,
                                                url = mediaSrc.url
                                            )
                                        )
                                    ).map {
                                        chatRepository.updateChat(
                                            chat.copy(
                                                chatId = chat.chatId,
                                                media = chat.media.copy(url = mediaSrc.url),
                                                success = true
                                            )
                                        ).onFailure {
                                            listOfUnSuccessFullChats.add(chat.chatId.toString())
                                        }
                                    }
                                }
                            } else {
                                chatRepository.sendChat(
                                    user,
                                    appSecret,
                                    chat.copy(userId = self.userName)
                                ).map {
                                    chatRepository.updateChatSuccess(chat.chatId.toString(), true)
                                        .onFailure {
                                            listOfUnSuccessFullChats.add(chat.chatId.toString())
                                        }
                                }
                            }
                        }
                    }
                }.catch {
                    listOfUnSuccessFullChats.add(chat.chatId.toString())
                }
            }
            onSuccess?.invoke(listOfUnSuccessFullChats)
        }
    }
}