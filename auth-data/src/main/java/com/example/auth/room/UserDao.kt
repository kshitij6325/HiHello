package com.example.auth.room

import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import com.example.auth.User

@Dao
interface UserDao {

    @Query("Select * from users where user_name=:userId")
    suspend fun getUser(userId: String): User?

    @Query("Select * from users where mobile_number=:mobileNumber")
    suspend fun getUserByIdAndMobile(mobileNumber: Long?): User?

    @Insert(onConflict = IGNORE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(vararg users: User)
}