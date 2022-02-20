package com.example.auth.repo

import com.example.auth.User
import com.example.pojo.Result

interface UserRepository {
    suspend fun createRemoteUser(user: User): Result<User>

    suspend fun createLoggedInUser(user: User): Result<User>

    suspend fun createLocalUser(user: User): Result<Boolean>

    suspend fun getRemoteUser(userName: String): Result<User>

    suspend fun getUserByMobile(mobile: Long): Result<User>

    suspend fun getAllLocalUsers(): Result<List<User>>

    suspend fun deleteLocalUser(): Result<Boolean>

    suspend fun getLocalUser(userId: String): Result<User>

    suspend fun getLoggedInUser(): Result<User>

    suspend fun isNewUser(user: User): Result<Boolean>

    suspend fun updateLoggedInUser(user: User): Result<User>

    suspend fun updateLocalUser(user: User): Result<User>

    suspend fun createUserIfNotExists(user: String): Result<User>
}

