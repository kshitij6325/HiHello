package com.example.auth.datasource

import com.example.auth.NoSuchUserException
import com.example.auth.User
import com.example.auth.room.UserDao
import com.example.pojo.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UserRoomDataSourceType

@Singleton
class UserRoomDataSource @Inject constructor(private val dao: UserDao) : UserDataSource {

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

    override suspend fun getUserByMobile(mobile: Long): Result<User> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                dao.getUserByIdAndMobile(mobile)?.run { Result.Success(this) } ?: Result.Failure(
                    NoSuchUserException()
                )

            } catch (e: Exception) {
                Result.Failure(e)
            }
        }


}