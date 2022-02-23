package com.example.auth.domain

import android.app.Activity
import com.example.auth.*
import com.example.auth.data.User
import com.example.auth.data.datasource.PhoneVerification
import com.example.auth.data.datasource.State
import com.example.auth.data.repo.FirebaseDataRepository
import com.example.auth.data.repo.UserRepository
import com.example.pojo.BaseUseCase
import com.example.pojo.Result
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class LoginUseCase @Inject constructor(
    private val userRepositoryImpl: UserRepository,
    private val firebaseDataRepository: FirebaseDataRepository,

    ) : BaseUseCase<State>() {

    private var phoneAuthId: String? = null

    suspend operator fun invoke(
        user: User,
        activity: Activity,
        otp: String? = null,
    ) {

        val phoneNumber = user.mobileNumber ?: 0

        if (!validateMobileNumber(phoneNumber)) {
            onFailure?.invoke(InvalidPhoneNumberException())
            return
        }
        firebaseDataRepository.createAnonymousUser().map {
            val userRes =
                if (user.userName.isNotEmpty()) Result.Success(user) else userRepositoryImpl.getUserByMobile(
                    phoneNumber
                )
            userRes.onSuccess { userRemote ->
                if (otp != null) {
                    phoneAuthId?.let {
                        firebaseDataRepository.verifyOtp(activity, otp, it).map {
                            loginUser(userRemote)
                        }.onFailure { onFailure?.invoke(it) }
                    }
                } else {
                    firebaseDataRepository.verifyPhoneNumber(
                        "+91$phoneNumber",
                        activity
                    ).onSuccess {
                        when (it) {
                            is PhoneVerification.CodeSent -> {
                                this.phoneAuthId = it.id
                                onSuccess?.invoke(State.Otp(userRemote))
                            }
                            is PhoneVerification.Success -> loginUser(userRemote)
                        }
                    }.onFailure { onFailure?.invoke(it) }
                }
            }.onFailure {
                onFailure?.invoke(it)
            }
        }.onFailure { onFailure?.invoke(it) }

    }

    private fun validateMobileNumber(phone: Long): Boolean {
        val regex = Regex("^[6-9]\\d{9}\$")
        return regex.matches(phone.toString())
    }

    private suspend fun loginUser(remoteUser: User) =
        firebaseDataRepository.getFirebaseToken().map {
            userRepositoryImpl.createRemoteUser(remoteUser.copy(fcmToken = it))
                .map { user ->
                    userRepositoryImpl.createLoggedInUser(user).onSuccess { updatedUser ->
                        onSuccess?.invoke(State.Complete(updatedUser))

                    }
                }
        }
}
