package com.example.auth.data.datasource

import com.example.auth.NoSuchUserException
import com.example.auth.data.User
import com.example.auth.UserCreationException
import com.example.auth.app.room.UserDao
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
            dao.getUser(userId)?.let {
                Result.Success(it)
            } ?: Result.Failure(NoSuchUserException())
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun createUser(user: User): Result<User> = withContext(Dispatchers.IO) {
        return@withContext try {
            if (dao.insertUser(user) != -1L) Result.Success(user) else Result.Failure(
                UserCreationException()
            )
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
            dao.deleteUser(userId).run { Result.Success(true) }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun getAllUsers(): Result<List<User>> = withContext(Dispatchers.IO) {
        return@withContext try {
            dao.getAllUser().run { Result.Success(this) }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }


}