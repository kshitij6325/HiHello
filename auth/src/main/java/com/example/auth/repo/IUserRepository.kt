package com.example.auth.repo

import com.example.auth.User
import com.example.pojo.Result

interface IUserRepository {
    suspend fun createRemoteUser(user: User): Result<User>

    suspend fun crateLoggedInUser(user: User): Result<User>

    suspend fun getRemoteUser(userName: String): Result<User>

    suspend fun getAppSecret(): Result<String>

    suspend fun getFirebaseToken(): Result<String>

    suspend fun deleteLocalUser(): Result<Boolean>

    suspend fun getLocalUser(userId: String): Result<User>

    suspend fun getLoggedInUser(): Result<User>

    suspend fun isNewUser(user: User): Result<Boolean>

    suspend fun updateLoggedInUser(user: User): Result<User>
}

