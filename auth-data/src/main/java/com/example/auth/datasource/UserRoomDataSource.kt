package com.example.auth.datasource

import android.util.Log
import com.example.auth.NoSuchUserException
import com.example.auth.User
import com.example.auth.room.UserDao
import com.example.pojo.Result
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UserRoomDataSourceType

@ViewModelScoped
class UserRoomDataSource @Inject constructor(private val dao: UserDao) : UserDataSource {

    override suspend fun getUser(userId: String): Result<User> = withContext(Dispatchers.IO) {
        return@withContext try {
            dao.getUser(userId)?.let {
                Result.Success(it)
            } ?: Result.Failure(NoSuchUserException())
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

    override suspend fun deleteUser(userId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext try {
            dao.deleteUser(User(userName = userId)).run { Result.Success(true) }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }


}