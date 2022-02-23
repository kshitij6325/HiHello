package com.example.auth_feature.auth_viemodel

import android.app.Activity
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.auth.NoSuchUserException
import com.example.auth.UserAlreadyExitsException
import com.example.auth.data.repo.FirebaseDataRepository
import com.example.auth.domain.LoginUseCase
import com.example.auth.domain.SignUpUseCase
import com.example.auth_feature.AuthViewModel
import com.example.auth_feature.MainDispatcherRule
import com.example.auth_feature.data.FakeDataProvider
import com.example.auth_feature.data.FakeFirebaseDataSource
import com.example.auth_feature.data.FakeMediaSource
import com.example.media_data.MediaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AuthViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var activity: Activity

    private lateinit var viewmodel: AuthViewModel

    @Before
    @After
    fun initialize() {
        val repo = FakeDataProvider.initDataAndRepo()
        val firebaseRepo = FirebaseDataRepository(FakeFirebaseDataSource())
        val mediaRep = MediaRepository(FakeMediaSource(mutableMapOf()))
        val loginUseCase = LoginUseCase(repo, firebaseRepo)
        val signUpUseCase = SignUpUseCase(repo, firebaseRepo, mediaRep)
        viewmodel = AuthViewModel(
            loginUseCase = loginUseCase,
            signUpUseCase = signUpUseCase
        )
    }


    @Test
    fun signUpUser_newUserSuccess() = runBlockingTest {
        viewmodel.signUpUser(FakeDataProvider.myUser, activity)

        val signUpUiState = viewmodel.signUpScreenUiState.value

        assert(signUpUiState.goToOtp)

        viewmodel.submitOtp(otp = "lakfk", activity = activity)

        val otpLiveData = viewmodel.otpScreenUiState.value

        assert(otpLiveData.isSuccess)

        assert(otpLiveData.error.isNullOrEmpty())

        //mainDispatcherRule.resumeDispatcher()

        //signUpUiState = viewmodel.signUpScreenUiStateLiveData.value()


        //assert(!signUpUiState.isLoading)
        //assert(signUpUiState.isSuccess)
    }

    @Test
    fun signUpUser_OldUserFail() = runBlockingTest {

        viewmodel.signUpUser(FakeDataProvider.user1, activity)

        val signUpUiState = viewmodel.signUpScreenUiState.value

        assert(!signUpUiState.isSuccess)
        assert(signUpUiState.error == UserAlreadyExitsException().message)

    }

    @Test
    fun loginUser_success() = runBlockingTest {

        viewmodel.signInUser(FakeDataProvider.user1.mobileNumber.toString(), activity)

        val signUpUiState = viewmodel.signInScreenUiState.value

        assert(signUpUiState.goToOtpScreen)

        viewmodel.submitOtp("otp", activity)

        assert(viewmodel.otpScreenUiState.value.isSuccess)

        assert(signUpUiState.error.isNullOrEmpty())

    }

    @Test
    fun loginUser_noSuchUser() = runBlockingTest {

        viewmodel.signInUser(
            FakeDataProvider.myUser.mobileNumber.toString(),
            activity
        )

        val signUpUiState = viewmodel.signInScreenUiState.value

        assert(!signUpUiState.isSuccess)
        assert(signUpUiState.error != null && signUpUiState.error == NoSuchUserException().message)

    }

}