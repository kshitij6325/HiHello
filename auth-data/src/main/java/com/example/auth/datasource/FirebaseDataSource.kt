package com.example.auth.datasource

import android.app.Activity
import com.example.pojo.Result
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.UserInfo
import java.lang.Exception

sealed class PhoneVerification {
    object Success : PhoneVerification()
    class CodeSent(val id: String) : PhoneVerification()
}

interface FirebaseDataSource {
    suspend fun getAppSecret(): Result<String>

    suspend fun getFirebaseToken(): Result<String>

    suspend fun createAnonymousUser(): Result<Boolean> {
        return Result.Success(true)
    }

    suspend fun verifyPhoneNumber(
        phoneNumber: String, activity: Activity
    ): Result<PhoneVerification>

    suspend fun verifyOtp(
        activity: Activity,
        otp: String,
        id: String,
    ): Result<Boolean>
}