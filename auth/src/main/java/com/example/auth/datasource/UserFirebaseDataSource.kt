package com.example.auth.datasource

import com.example.auth.NoSuchUserException
import com.example.auth.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import com.example.pojo.Result
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.lang.Exception

class UserFirebaseDataSource : UserDataSource {

    private val firebaseInstanceRef by lazy {
        FirebaseDatabase.getInstance().getReference("user")
    }

    override suspend fun getUser(userId: String): Result<User> =
        withContext(Dispatchers.IO) {
            return@withContext suspendCancellableCoroutine<Result<User>> {
                firebaseInstanceRef.child(userId).run {
                    addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val res = snapshot.getValue(User::class.java)
                            if (res != null) {
                                it.resume(Result.Success(res)) {}
                            } else it.resume(Result.Failure(NoSuchUserException())) {}
                            removeEventListener(this)

                        }

                        override fun onCancelled(error: DatabaseError) {
                            it.resume(Result.Failure(error.toException())) {}
                        }

                    })
                }
            }
        }

    override suspend fun createUser(user: User): Result<User> =
        withContext(Dispatchers.IO) {
            return@withContext suspendCancellableCoroutine<Result<User>> { continuation ->
                firebaseInstanceRef.child(user.userName).setValue(user).addOnSuccessListener {
                    continuation.resume(Result.Success(user)) {}
                }.addOnFailureListener {
                    continuation.resume(Result.Failure(it)) {}
                }
            }
        }

    override suspend fun updateUser(user: User): Result<User> =
        withContext(Dispatchers.IO) {
            return@withContext createUser(user)
        }

    override suspend fun getUserByMobile(mobile: Long): Result<User> =
        withContext(Dispatchers.IO) {
            return@withContext suspendCancellableCoroutine<Result<User>> {
                firebaseInstanceRef.orderByChild("mobileNumber").equalTo(mobile.toDouble()).run {
                    addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val res = snapshot.getValue(User::class.java)
                            if (res != null) {
                                it.resume(Result.Success(res)) {}
                            } else it.resume(Result.Failure(NoSuchUserException())) {}
                            removeEventListener(this)

                        }

                        override fun onCancelled(error: DatabaseError) {
                            it.resume(Result.Failure(error.toException())) {}
                        }

                    })
                }
            }
        }


}