package com.example.auth.datasource

import com.example.auth.User
import com.example.auth.room.UserDao
import com.example.pojo.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class UserRoomDataSource(private val dao: UserDao) : UserDataSource {

    override suspend fun getUser(userId: String): Result<User> = withContext(Dispatchers.IO) {
        return@withContext try {
            Result.Success(dao.getUser(userId))
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun createUser(user: User): Result<User> = withContext(Dispatchers.IO) {
        return@withContext try {
            dao.insertUser(user)
            Result.Success(user)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun updateUser(user: User): Result<User> = withContext(Dispatchers.IO) {
        return@withContext try {
            dao.updateUser(user)
            Result.Success(user)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
}