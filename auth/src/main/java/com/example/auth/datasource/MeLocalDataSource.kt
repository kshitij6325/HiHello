package com.example.auth.datasource

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.auth.NoSuchUserException
import com.example.auth.User
import com.example.pojo.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.Exception

class MeLocalDataSource(private val sharedPref: SharedPreferences) : UserDataSource {

    private val loggedInUserKey = "logged_in_user_info"

    override suspend fun getUser(userId: String): Result<User> = withContext(Dispatchers.IO) {
        return@withContext try {
            sharedPref.getString(loggedInUserKey, null)
                ?.run { Result.Success(Json.decodeFromString(this)) }
                ?: Result.Failure(NoSuchUserException())
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun createUser(user: User): Result<User> = withContext(Dispatchers.IO) {
        return@withContext try {
            val stringifyUser = Json.encodeToString(user)
            sharedPref.edit {
                putString(loggedInUserKey, stringifyUser)
            }
            Result.Success(user)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun updateUser(user: User): Result<User> = createUser(user)

    override suspend fun getUserByMobile(mobile: Long) = getUser("")
}
