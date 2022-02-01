package com.example.auth.repo

import android.app.Activity
import com.example.auth.datasource.FirebaseDataSource
import com.example.pojo.Result
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseDataRepository @Inject constructor(private val firebaseDataSource: FirebaseDataSource) {

    private var appSecret: Result<String>? = null

    suspend fun getAppSecret(cached: Boolean = true): Result<String> {
        return when {
            !cached || appSecret is Result.Failure || appSecret == null -> firebaseDataSource.getAppSecret()
                .also {
                    appSecret = it
                }
            else -> appSecret!!
        }
    }

    suspend fun verifyPhoneNumber(
        phoneNumber: String,
        activity: Activity
    ) = firebaseDataSource.verifyPhoneNumber(phoneNumber, activity)

    suspend fun verifyOtp(
        activity: Activity,
        otp: String,
        id: String
    ) = firebaseDataSource.verifyOtp(activity, otp, id)

    suspend fun getFirebaseToken(): Result<String> = firebaseDataSource.getFirebaseToken()
}