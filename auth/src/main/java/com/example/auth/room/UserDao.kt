package com.example.auth.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.auth.User

@Dao
interface UserDao {

    @Query("Select * from users where user_name=:userId")
    suspend fun getUser(userId: String): User

    @Query("Select * from users where mobile_number=:mobileNumber")
    suspend fun getUserByIdAndMobile(mobileNumber: Long?): User?

    @Insert
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)
}