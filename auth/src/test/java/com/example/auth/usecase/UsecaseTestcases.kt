package com.example.auth.usecase

import com.example.auth.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class UsecaseTestcases {

    private lateinit var isUserLoggedInUseCase: IsUserLoggedInUseCase
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var logoutUseCase: LogoutUseCase
    private lateinit var signUpUseCase: SignUpUseCase

    @Before
    @After
    fun init() {
        val repo = FakeDataProvider.initDataAndRepo()
        isUserLoggedInUseCase = IsUserLoggedInUseCase(repo)
        loginUseCase = LoginUseCase(repo)
        signUpUseCase = SignUpUseCase(repo)
        logoutUseCase = LogoutUseCase(repo)
    }


    @Test
    fun loggInUser_IfUserNotExists() = runBlocking {
        loginUseCase.apply {
            onSuccess = {
                assert(false)
            }
            onFailure = {
                assert(it is NoSuchUserException)
            }

        }.invoke("Rajdeep", "112233")
    }

    @Test
    fun loggInUser_IfUserExists_wrongPassword() = runBlocking {
        loginUseCase.apply {
            onSuccess = {
                assert(false)
            }
            onFailure = {
                assert(it is WrongPasswordException)
            }

        }.invoke(FakeDataProvider.user1.userName, "iuiuiuiuiui")
    }

    @Test
    fun checkIsLoggedIn_withoutLogin() = runBlocking {
        isUserLoggedInUseCase.apply {
            onSuccess = {
                assert(!it)
            }
            onFailure = {
                assert(false)
            }
        }.invoke()
    }

    @Test
    fun checkIsLoggedIn_withLogin() = runBlocking {

        loginUseCase.apply {
            onSuccess = {
                assert(true)
            }
            onFailure = {
                assert(false)
            }

        }.invoke(FakeDataProvider.user1.userName, FakeDataProvider.user1.password!!)

        isUserLoggedInUseCase.apply {
            onSuccess = {
                assert(it)
            }
            onFailure = {
                assert(false)
            }
        }.invoke()
    }

    @Test
    fun checkIsLoggedIn_withSignUp() = runBlocking {

        signUpUseCase.apply {
            onSuccess = {
                assert(true)
            }
            onFailure = {
                assert(false)
            }

        }.invoke(FakeDataProvider.myUser)

        isUserLoggedInUseCase.apply {
            onSuccess = {
                assert(it)
            }
            onFailure = {
                assert(false)
            }
        }.invoke()
    }

    @Test
    fun checkSignUp_alreadyExitingUser() = runBlocking {
        signUpUseCase.apply {
            onSuccess = {
                assert(false)
            }
            onFailure = {
                assert(it is UserAlreadyExitsException)
            }

        }.invoke(FakeDataProvider.user1)
    }

    @Test
    fun checkSignUp_withWrongUserDetails() = runBlocking {
        signUpUseCase.apply {
            onSuccess = {
                assert(false)
            }
            onFailure = {
                assert(it is EmptyUserNameException)
            }

        }.invoke(User(""))

        signUpUseCase.apply {
            onSuccess = {
                assert(false)
            }
            onFailure = {
                assert(it is InvalidPhoneNumberException)
            }

        }.invoke(User("Tanjiro", mobileNumber = 6677858))

        signUpUseCase.apply {
            onSuccess = {
                assert(false)
            }
            onFailure = {
                assert(it is EmptyPasswordException)
            }

        }.invoke(User("Tanjiro", mobileNumber = 8865534256))
    }

    @Test
    fun checkSignUp_success() = runBlocking {
        signUpUseCase.apply {
            onSuccess = {
                assert(it.userName == FakeDataProvider.myUser.userName)
            }
            onFailure = {
                assert(false)
            }

        }.invoke(FakeDataProvider.myUser)

    }
}