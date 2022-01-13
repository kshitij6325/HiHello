package com.example.auth.room

import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.OnConflictStrategy.REPLACE
import com.example.auth.User

@Dao
interface UserDao {

    @Query("Select * from users")
    suspend fun getAllUser(): List<User>


    @Query("Select * from users where user_name=:userId")
    suspend fun getUser(userId: String): User?

    @Query("Select * from users where mobile_number=:mobileNumber")
    suspend fun getUserByIdAndMobile(mobileNumber: Long?): User?

    @Insert(onConflict = REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User): Int

    @Query("delete from users where user_name == :userId")
    suspend fun deleteUser(userId: String): Int
}