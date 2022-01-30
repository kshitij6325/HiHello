package com.example.auth.usecase

import com.example.auth.*
import com.example.auth.data.*
import com.example.auth.datasource.FirebaseDataSource
import com.example.auth.datasource.UserDataSource
import com.example.auth.repo.FirebaseDataRepository
import com.example.auth.repo.UserRepository
import com.example.auth.repo.UserRepositoryImpl
import com.example.media_data.FirebaseStorageDataSource
import com.example.media_data.MediaRepository
import com.example.media_data.MediaSource
import com.example.media_data.MediaType
import com.example.pojo.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File

@ExperimentalCoroutinesApi
class UseCaseTestcases {

    private val remoteUserList =
        listOf(FakeDataProvider.user1, FakeDataProvider.user2, FakeDataProvider.user3)
    private val localUserList = listOf(FakeDataProvider.user1.copy(), FakeDataProvider.user2.copy())
    private val mediaMap =
        mutableMapOf<String, MediaSource>()

    private lateinit var userFirebaseDataSource: FakeUserDataSource
    private lateinit var meDataSource: UserDataSource
    private lateinit var userRoomDataSource: UserDataSource
    private lateinit var firebaseDataSource: FirebaseDataSource
    private lateinit var firebaseDataSrc: FakeFirebaseDataSource
    private lateinit var mediaSource: FakeMediaSource

    private lateinit var getUserLoggedInUseCase: GetUserLoggedInUseCase
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var logoutUseCase: LogoutUseCase
    private lateinit var signUpUseCase: SignUpUseCase
    private lateinit var updateLoggedInUserUseCase: UpdateLoggedInUserUseCase
    private lateinit var syncUsersUseCase: SyncUsersUseCase

    @Before
    @After
    fun init() {
        userFirebaseDataSource =
            FakeUserDataSource(remoteUserList.toMutableList())
        meDataSource = FakeMeDataSource()
        userRoomDataSource =
            FakeUserDataSource(localUserList.toMutableList())
        firebaseDataSource = FakeFirebaseDataSource()
        mediaSource = FakeMediaSource(mediaMap)
        val repo = UserRepositoryImpl(
            userFirebaseDataSource = userFirebaseDataSource,
            meDataSource = meDataSource,
            userRoomDataSource = userRoomDataSource
        )

        getUserLoggedInUseCase = GetUserLoggedInUseCase(repo)
        firebaseDataSrc = FakeFirebaseDataSource()
        val firebaseDataRepository = FirebaseDataRepository(firebaseDataSrc)
        loginUseCase = LoginUseCase(repo, firebaseDataRepository)
        signUpUseCase = SignUpUseCase(repo, firebaseDataRepository, MediaRepository(mediaSource))
        logoutUseCase = LogoutUseCase(repo)
        updateLoggedInUserUseCase = UpdateLoggedInUserUseCase(repo)
        syncUsersUseCase = SyncUsersUseCase(repo)
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
        getUserLoggedInUseCase.apply {
            onSuccess = {
                assert(it == null)
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

        getUserLoggedInUseCase.apply {
            onSuccess = {
                assert(it != null && it.userName == FakeDataProvider.user1.userName)
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

        getUserLoggedInUseCase.apply {
            onSuccess = {
                assert(it != null)
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

    @Test
    fun checkSignUpWithMedia_success() = runTest {
        val mediaSrc = MediaSource.File(File.createTempFile("avatar", "png"), MediaType.IMAGE)
        signUpUseCase.apply {
            onSuccess = {
                assert(it.userName == FakeDataProvider.myUser.userName)
                assert(it.profileUrl != null)
            }
            onFailure = {
                assert(false)
            }

        }.invoke(FakeDataProvider.myUser, mediaSrc)

    }

    @Test
    fun checkUserFcmUpdate_success() = runTest {
        signUpUseCase.apply {
            onSuccess = {
                assert(it.userName == FakeDataProvider.myUser.userName)
            }
            onFailure = {
                assert(false)
            }

        }.invoke(FakeDataProvider.myUser)
        updateLoggedInUserUseCase.apply {
            onSuccess = {
                assert(it)
            }
            onFailure = {
                assert(false)
            }

        }.invoke(FakeDataProvider.myUser.copy(fcmToken = "new token"))

        getUserLoggedInUseCase.apply {
            onSuccess = {
                assert(it != null && it.fcmToken == "new token")
            }
            onFailure = {
                assert(false)
            }
        }.invoke()

    }

    @Test
    fun checkSyncUserUpdate_success() = runTest {
        assert(FakeDataProvider.user1.fcmToken == null)
        userFirebaseDataSource.updateUser(FakeDataProvider.user1.copy(fcmToken = "newToken"))
        val res = syncUsersUseCase.invoke()
        when (res) {
            is Result.Failure -> assert(false)
            is Result.Success -> assert(res.data == 0)
        }
        val userRes = userRoomDataSource.getUser(FakeDataProvider.user1.userName)
        assert(userRoomDataSource.getUser(FakeDataProvider.user1.userName) is Result.Success && (userRes as Result.Success).data.fcmToken == "newToken")

    }

    @Test
    fun checkSyncUserUpdate_fail() = runTest {
        assert(FakeDataProvider.user1.fcmToken == null)
        userFirebaseDataSource.updateUser(FakeDataProvider.user1.copy(fcmToken = "newToken"))
        userFirebaseDataSource.fail = true
        val res = syncUsersUseCase.invoke()
        when (res) {
            is Result.Failure -> assert(false)
            is Result.Success -> assert(res.data == 2)
        }
        val userRes = userRoomDataSource.getUser(FakeDataProvider.user1.userName)
        assert(userRoomDataSource.getUser(FakeDataProvider.user1.userName) is Result.Success && (userRes as Result.Success).data.fcmToken == null)

    }
}