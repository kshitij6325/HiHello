package com.example.auth.usecase

import com.example.auth.User
import com.example.auth.repo.UserRepository
import com.example.pojo.BaseUseCase
import com.example.pojo.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncUsersUseCase @Inject constructor(private val userRepository: UserRepository) :
    BaseUseCase<Int>() {

    suspend fun invoke(vararg userIds: String?): Result<Int> {
        return if (userIds.isNullOrEmpty()) {
            userRepository.getAllLocalUsers().map { list ->
                Result.Success(updateUsers(userList = list.toList()))
            }
        } else {
            userRepository.getAllLocalUsers().map { list ->
                Result.Success(updateUsers(userList = list.toList()))
            }
        }
    }

    private suspend fun updateUsers(userList: List<User>): Int {
        var failureCount = 0
        for (user in userList) {
            userRepository.getRemoteUser(user.userName).map {
                userRepository.updateLocalUser(it)
            }.catch {
                failureCount++
            }
        }
        return failureCount
    }
}