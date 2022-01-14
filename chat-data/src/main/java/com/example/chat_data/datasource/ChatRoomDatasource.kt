package com.example.chat_data.datasource

import androidx.lifecycle.LiveData
import com.example.chat_data.Chat
import com.example.chat_data.room.ChatDao
import com.example.pojo.Result
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRoomDatasource @Inject constructor(private val chatDao: ChatDao) : ChatDatasource {

    override suspend fun addChats(chat: Chat): Result<Long> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                Result.Success(chatDao.addChat(chat))
            } catch (ex: Exception) {
                Result.Failure(ex)
            }
        }

    override suspend fun getAllUserChat(userId: String): Result<List<Chat>> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                Result.Success(chatDao.getAllUserChats(userId))
            } catch (ex: Exception) {
                Result.Failure(ex)
            }
        }

    override suspend fun getChat(chatId: String): Result<Chat> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                Result.Success(chatDao.getChat(chatId))
            } catch (ex: Exception) {
                Result.Failure(ex)
            }

        }

    override suspend fun updateChat(chat: Chat): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext try {
            chatDao.updateChat(chat)
            Result.Success(true)
        } catch (ex: Exception) {
            Result.Failure(ex)
        }
    }

    override suspend fun updateChatSuccessState(chatId: String, success: Boolean): Result<Boolean> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                chatDao.updateChatSuccessState(chatId, success)
                Result.Success(true)
            } catch (ex: Exception) {
                Result.Failure(ex)
            }
        }

    override suspend fun deleteChat(chatId: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                chatDao.deleteChats(chatId)
                Result.Success(true)
            } catch (ex: Exception) {
                Result.Failure(ex)
            }
        }

    override suspend fun deleteAllUserChat(userId: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                chatDao.deleteUserChats(userId)
                Result.Success(true)
            } catch (ex: Exception) {
                Result.Failure(ex)
            }
        }

    override suspend fun getAllUnSendChat(): Result<List<Chat>> = withContext(Dispatchers.IO) {
        return@withContext try {
            Result.Success(chatDao.getAllUnSendChats())
        } catch (ex: Exception) {
            Result.Failure(ex)
        }
    }

    override fun getAllUserChatLiveData(userId: String): LiveData<List<Chat>> {
        return chatDao.getAllUserChatLiveData()
    }
}
