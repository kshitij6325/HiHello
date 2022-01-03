package com.example.auth.usecase

import com.example.auth.InvalidPhoneNumberException
import com.example.auth.User
import com.example.auth.UserAlreadyExitsException
import com.example.auth.repo.UserRepository
import com.example.pojo.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignUpUseCase @Inject constructor(private val userRepository: UserRepository) {

    suspend operator fun invoke(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        when {
            !validateMobileNumber(user) -> runOnMain {
                onFailure(InvalidPhoneNumberException())
            }
            isUserAlreadyExists(user) -> runOnMain {
                onFailure(UserAlreadyExitsException())
            }
            else -> when (val res = userRepository.signUpUser(user)) {
                is Result.Failure -> runOnMain {
                    onFailure(res.exception)
                }
                is Result.Success -> runOnMain { onSuccess() }
            }
        }
    }

    private fun validateMobileNumber(user: User): Boolean {
        val regex = Regex("^[6-9]\\d{9}\$")
        return regex.matches(user.mobileNumber.toString())
    }

    private suspend fun isUserAlreadyExists(user: User) =
        userRepository.getRemoteUserByPhone(user) is Result.Success || userRepository.getRemoteUser(
            user
        ) is Result.Success

    private suspend fun runOnMain(task: () -> Unit) = withContext(Dispatchers.Main) {
        task()
    }

}