package com.example.auth.usecase

import com.example.auth.*
import com.example.auth.data.FakeDataProvider
import com.example.auth.data.FakeFirebaseDataSource
import com.example.auth.repo.FirebaseDataRepository
import com.example.pojo.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class UseCaseTestcases {
    private lateinit var firebaseDataSrc: FakeFirebaseDataSource
    private lateinit var isUserLoggedInUseCase: IsUserLoggedInUseCase
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var logoutUseCase: LogoutUseCase
    private lateinit var signUpUseCase: SignUpUseCase

    @Before
    @After
    fun init() {
        val repo = FakeDataProvider.initDataAndRepo()
        isUserLoggedInUseCase = IsUserLoggedInUseCase(repo)
        firebaseDataSrc = FakeFirebaseDataSource()
        val firebaseDataRepository = FirebaseDataRepository(firebaseDataSrc)
        loginUseCase = LoginUseCase(repo, firebaseDataRepository)
        signUpUseCase = SignUpUseCase(repo, firebaseDataRepository)
        logoutUseCase = LogoutUseCase(repo)
    }


    @Test
    fun loggInUser_IfUserNotExists() = runTest {
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
    fun loggInUser_IfUserExists_wrongPassword() = runTest {
        loginUseCase.apply {
            onSuccess = {
                assert(false)
            }
            onFailure = {
                assert(it is WrongPasswordException)
            }

        }.invoke(FakeDataProvider.user1.userName, "Wrong password")
    }

    @Test
    fun loggInUser_checkFcmTokenUpdate() = runTest {
        firebaseDataSrc.firebaseToken = "token"

        val res = firebaseDataSrc.getFirebaseToken()
        assert(res is Result.Success && res.data == "token")

        firebaseDataSrc.firebaseToken = "token2"
        loginUseCase.apply {
            onSuccess = {
                assert(it.fcmToken == "token2")
            }
            onFailure = {
                assert(false)
            }

        }.invoke(FakeDataProvider.user1.userName, FakeDataProvider.user1.password ?: "")
    }

    @Test
    fun checkIsLoggedIn_withoutLogin() = runTest {
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
    fun checkIsLoggedIn_withLogin() = runTest {

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
    fun checkIsLoggedIn_withSignUp() = runTest {

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
    fun checkSignUp_alreadyExitingUser() = runTest {
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
    fun checkSignUp_withWrongUserDetails() = runTest {
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
    fun checkSignUp_success() = runTest {
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