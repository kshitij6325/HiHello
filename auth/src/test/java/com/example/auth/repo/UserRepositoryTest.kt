package com.example.auth.repo

import com.example.auth.NoSuchUserException
import com.example.auth.User
import com.example.auth.UserAlreadyExitsException
import com.example.auth.WrongPasswordException
import com.example.auth.datasource.FirebaseDataSource
import com.example.auth.datasource.IFirebaseDataSource
import com.example.auth.datasource.UserDataSource
import com.example.auth.usecase.SignUpUseCase
import com.example.pojo.Result
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test


class UserRepositoryTest {

    private val myUser = User(
        userName = "kshitij6325",
        mobileNumber = 9953319602,
        profileUrl = "https://google.com.jpeg",
        password = "abcd"
    )

    private val user1 = User(
        userName = "harshit3344",
        mobileNumber = 9953319605,
        profileUrl = "https://google.com.jpeg",
        password = "abcd"
    )

    private val user2 = User(
        userName = "beena345",
        mobileNumber = 9953219602,
        profileUrl = "https://google.com.jpeg",
        password = "abcd"
    )

    private val user3 = User(
        userName = "surendra6677",
        mobileNumber = 9151319602,
        profileUrl = "https://google.com.jpeg",
        password = "abcd"
    )

    private val remoteUserList = listOf(user1, user2, user3)
    private val localUserList = listOf<User>()

    private lateinit var userFirebaseDataSource: UserDataSource
    private lateinit var meDataSource: UserDataSource
    private lateinit var userRoomDataSource: UserDataSource
    private lateinit var firebaseDataSource: IFirebaseDataSource

    private lateinit var repo: UserRepository

    @Before
    @After
    fun initRepo() {
        userFirebaseDataSource = FakeUserDataSource(remoteUserList.toMutableList())
        meDataSource = FakeMeDataSource()
        userRoomDataSource = FakeUserDataSource(localUserList.toMutableList())
        firebaseDataSource = FakeFirebaseDataSource()
        repo = UserRepository(
            userFirebaseDataSource = userFirebaseDataSource,
            meDataSource = meDataSource,
            userRoomDataSource = userRoomDataSource,
            firebaseDataSource = firebaseDataSource
        )
    }

    @Test
    fun givenRepo_fetchRemoteUser() = runBlocking {
        val resCreate = repo.getRemoteUser(user1.userName)
        assert(resCreate is Result.Success && resCreate.data.userName == user1.userName)
    }

    @Test
    fun givenRepo_fetchRemoteUserError() = runBlocking {
        val resCreate = repo.getRemoteUser("userId12345")
        assert(resCreate is Result.Failure && resCreate.exception is NoSuchUserException)
    }

    @Test
    fun givenRepo_testLoggedInUser() = runBlocking {
        repo.crateLoggedInUser(myUser)
        val loggedInUser = repo.getLoggedInUser()
        assert(loggedInUser is Result.Success && loggedInUser.data.userName == myUser.userName)
    }

    @Test
    fun givenRepo_testLoggedInUserError() = runBlocking {
        val loggedInUser = repo.getLoggedInUser()
        assert(loggedInUser is Result.Failure && loggedInUser.exception is NoSuchUserException)
    }

    @Test
    fun givenRepo_testUserExists() = runBlocking {
        val user = repo.isNewUser(myUser)
        assert(user is Result.Failure && user.exception is UserAlreadyExitsException)
    }

    @Test
    fun givenRepo_testLogout() = runBlocking {
        val resCreate = repo.crateLoggedInUser(myUser)
        val resLogout = repo.deleteLocalUser()
        assert(resCreate is Result.Success && resLogout is Result.Success)
    }

}