package com.example.auth.datasource

import com.example.auth.NoSuchUserException
import com.example.auth.User
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import com.example.pojo.Result
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.coroutines.resume

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UserFirebaseDataSourceType

@Singleton
class UserFirebaseDataSource @Inject constructor() : UserDataSource {

    private val firebaseInstanceRef by lazy {
        FirebaseDatabase.getInstance().getReference("user")
    }

    override suspend fun getUser(userId: String): Result<User> =
        withContext(Dispatchers.IO) {
            return@withContext suspendCancellableCoroutine<Result<User>> {
                firebaseInstanceRef.child(userId).get().run {
                    addOnSuccessListener { snapshot ->
                        val res = snapshot.getValue(User::class.java)
                        if (res != null) {
                            it.resume(Result.Success(res))
                        } else it.resume(Result.Failure(NoSuchUserException()))
                    }

                    addOnFailureListener { exception ->
                        it.resume(Result.Failure(exception))
                    }
                }
            }
        }

    override suspend fun createUser(user: User): Result<User> =
        withContext(Dispatchers.IO) {
            return@withContext suspendCancellableCoroutine<Result<User>> { continuation ->
                firebaseInstanceRef.child(user.userName).setValue(user).addOnSuccessListener {
                    continuation.resume(Result.Success(user))
                }.addOnFailureListener {
                    continuation.resume(Result.Failure(it))
                }
            }
        }

    override suspend fun updateUser(user: User): Result<User> = createUser(user)


    override suspend fun getUserByMobile(mobile: Long): Result<User> =
        withContext(Dispatchers.IO) {
            return@withContext suspendCancellableCoroutine<Result<User>> {
                firebaseInstanceRef.orderByChild("mobileNumber").equalTo(mobile.toDouble()).get()
                    .run {
                        addOnSuccessListener { snapshot ->
                            val res = snapshot.getValue(User::class.java)
                            if (res != null) {
                                it.resume(Result.Success(res))
                            } else it.resume(Result.Failure(NoSuchUserException()))
                        }

                        addOnFailureListener { exception ->
                            it.resume(Result.Failure(exception))
                        }
                    }
            }
        }

    override suspend fun deleteUser(userId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext suspendCancellableCoroutine<Result<Boolean>> {
            firebaseInstanceRef.child(userId).removeValue()
                .run {
                    addOnSuccessListener { _ ->
                        it.resume(Result.Success(true))
                    }

                    addOnFailureListener { exception ->
                        it.resume(Result.Failure(exception))
                    }
                }
        }
    }
}