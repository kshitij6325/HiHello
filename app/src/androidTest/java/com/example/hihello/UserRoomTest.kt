package com.example.hihello

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.auth.data.User
import com.example.auth.app.room.UserDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class UserRoomTest {

    lateinit var userDao: UserDao

    private val user = User(
        userName = "harshit3344",
        mobileNumber = 9953319605,
        profileUrl = "https://google.com.jpeg",
    )

    private val user2 = User(
        userName = "kshitij",
        mobileNumber = 9953819605,
        profileUrl = "https://google.com.jpeg",
    )

    @Before
    fun initDatabaseAndDao() {
        val dataBase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()

        userDao = dataBase.getUserDao()
    }


    @Test
    fun addUser_success() = runTest {
        val user1Id = userDao.insertUser(user = user)
        val user2Id = userDao.insertUser(user = user2)
        assert(user1Id == 1L && user2Id == 2L)
    }

    @Test
    fun addUser_duplicateUser_success() = runTest {
        val user1Id = userDao.insertUser(user = user)

        var userRes = userDao.getUser(user.userName)
        assert(userRes != null && userRes.userName == user.userName)

        val user2Id = userDao.insertUser(user = user.copy(mobileNumber = 8888888888))
        userRes = userDao.getUser(user.userName)

        val userAllRes = userDao.getAllUser()

        assert(userRes != null && userRes.mobileNumber == 8888888888)
        assert(user1Id == 1L && user2Id == 2L)
        assert(userAllRes.size == 1)
    }

    @Test
    fun getUser_success() = runTest {
        val userInsertId = userDao.insertUser(user = user)
        val userRes = userDao.getUser(user.userName)
        assert(userInsertId == 1L && userRes != null && userRes.userName == user.userName)
    }

    @Test
    fun getUser_notExits_fail() = runTest {
        val userRes = userDao.getUser(user.userName)
        assert(userRes == null)
    }

    @Test
    fun updateUser_success() = runTest {
        val userInsertId = userDao.insertUser(user = user)
        assert(userInsertId == 1L)

        var userRes = userDao.getUser(user.userName)
        assert(userRes != null && userRes.userName == user.userName)

        val updateRes = userDao.updateUser(user.copy(mobileNumber = 9999999999))
        userRes = userDao.getUser(user.userName)
        assert(updateRes == 1 && userRes != null && userRes.mobileNumber == 9999999999)
    }

    @Test
    fun deleteUser() = runTest {
        val userInsertId = userDao.insertUser(user = user)
        assert(userInsertId == 1L)

        val userDelRes = userDao.deleteUser(user.userName)
        assert(userDelRes == 1)

        val user = userDao.getUser(user.userName)
        assert(user == null)

    }
}