package com.example.auth.data

import android.app.Activity
import android.net.Uri
import com.example.auth.datasource.FirebaseDataSource
import com.example.auth.datasource.PhoneVerification
import com.example.pojo.Result
import com.google.firebase.auth.UserInfo
import java.lang.Exception

class FakeFirebaseDataSource : FirebaseDataSource {

    var fail = false

    var firebaseToken = "token"
    var secret = "Secret"
    var verificationId = "verificationId"

    override suspend fun getAppSecret(): Result<String> {
        return if (!fail) Result.Success(secret) else Result.Failure(Exception("Error fetching secret"))
    }

    override suspend fun getFirebaseToken(): Result<String> {
        return if (!fail) Result.Success(firebaseToken) else Result.Failure(Exception("Error fetching token"))
    }

    override suspend fun verifyPhoneNumber(
        phoneNumber: String,
        activity: Activity
    ): Result<PhoneVerification> {
        return if (!fail) Result.Success(PhoneVerification.CodeSent(verificationId)) else Result.Failure(
            Exception("Error verifying phone number")
        )
    }

    override suspend fun verifyOtp(activity: Activity, otp: String, id: String): Result<Boolean> {
        return if (!fail) Result.Success(true) else Result.Failure(
            Exception("Error verifying OTP")
        )
    }
}