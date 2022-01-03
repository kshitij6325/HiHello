package com.example.auth.usecase

import com.example.auth.InvalidPhoneNumberException
import com.example.auth.NoSuchUserException
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

    suspend operator fun invoke(
        user: User,
        onSuccess: suspend (User) -> Unit,
        onFailure: suspend (Exception) -> Unit
    ) {
        when {
            !validateMobileNumber(user) -> onFailure(InvalidPhoneNumberException())

            else -> {
                if (!userAlreadyExists(user, userRepository::getRemoteUser, onFailure) &&
                    !userAlreadyExists(user, userRepository::getRemoteUserByPhone, onFailure))
                    userRepository.getFirebaseToken().map {
                        userRepository.signUpUser(user.copy(fcmToken = it)).also { successRes ->
                            successRes.onSuccess(onSuccess)
                        }
                    }.catch {
                        onFailure(it)
                    }
            }

        }
    }

    private suspend fun userAlreadyExists(
        user: User,
        evaluator: suspend (User) -> Result<User>,
        onFailure: suspend (Exception) -> Unit
    ): Boolean {
        when (val res = evaluator(user)) {
            is Result.Failure -> if (res.exception !is NoSuchUserException) {
                onFailure(res.exception)
                return true
            }
            else -> {
                onFailure(UserAlreadyExitsException())
                return true
            }
        }
        return false
    }

    private fun validateMobileNumber(user: User): Boolean {
        val regex = Regex("^[6-9]\\d{9}\$")
        return regex.matches(user.mobileNumber.toString())
    }

    private suspend fun runOnMain(task: () -> Unit) = withContext(Dispatchers.Main) {
        task()
    }

}