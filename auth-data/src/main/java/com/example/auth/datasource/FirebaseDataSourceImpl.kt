package com.example.auth.datasource

import android.app.Activity
import com.example.pojo.Result
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class FirebaseDataSourceImpl @Inject constructor() : FirebaseDataSource {

    override suspend fun getAppSecret(): Result<String> = withContext(Dispatchers.IO) {
        return@withContext suspendCancellableCoroutine { continuation ->
            FirebaseDatabase.getInstance().getReference("app_secret").get()
                .addOnSuccessListener {
                    continuation.resume(Result.Success(it.getValue(String::class.java) as String))
                }
                .addOnFailureListener {
                    continuation.resume(Result.Failure(it))
                }
        }
    }

    override suspend fun getFirebaseToken(): Result<String> = withContext(Dispatchers.IO) {
        return@withContext suspendCancellableCoroutine { continuation ->
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener {
                    continuation.resume(Result.Success(it))
                }
                .addOnFailureListener {
                    continuation.resume(Result.Failure(it))
                }
        }
    }

    override suspend fun verifyPhoneNumber(
        phoneNumber: String,
        activity: Activity
    ): Result<PhoneVerification> = withContext(Dispatchers.IO) {
        return@withContext suspendCancellableCoroutine { continutation ->
            val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        continutation.resume(Result.Success(PhoneVerification.Success))
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        continutation.resume(Result.Failure(e))
                    }

                    override fun onCodeSent(
                        verificationId: String,
                        token: PhoneAuthProvider.ForceResendingToken
                    ) {
                        continutation.resume(
                            Result.Success(
                                PhoneVerification.CodeSent(
                                    verificationId
                                )
                            )
                        )
                    }
                }).build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }

    override suspend fun verifyOtp(
        activity: Activity,
        otp: String,
        id: String,
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext suspendCancellableCoroutine { cont ->
            val pro = PhoneAuthProvider.getCredential(id, otp)
            FirebaseAuth.getInstance().signInWithCredential(pro)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        task.result?.user?.let { cont.resume(Result.Success(true)) }
                    } else {
                        task.exception?.let { cont.resume(Result.Failure(it)) }
                    }
                }
        }
    }
}