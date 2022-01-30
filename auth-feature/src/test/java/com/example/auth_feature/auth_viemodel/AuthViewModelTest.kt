package com.example.auth_feature.auth_viemodel

import android.app.Application
import android.content.ContentResolver
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.auth.NoSuchUserException
import com.example.auth.UserAlreadyExitsException
import com.example.auth.repo.FirebaseDataRepository
import com.example.auth.usecase.LoginUseCase
import com.example.auth.usecase.SignUpUseCase
import com.example.auth_feature.AuthViewModel
import com.example.auth_feature.MainDispatcherRule
import com.example.auth_feature.data.FakeDataProvider
import com.example.auth_feature.data.FakeFirebaseDataSource
import com.example.auth_feature.data.FakeMeDataSource
import com.example.auth_feature.data.FakeMediaSource
import com.example.auth_feature.getOrAwaitValue
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
    private lateinit var contentResolver: ContentResolver

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
        viewmodel.signUpUser(FakeDataProvider.myUser, contentResolver)

        val signUpUiState = viewmodel.signUpScreenUiStateLiveData.getOrAwaitValue()

        assert(signUpUiState.isSuccess)
        assert(signUpUiState.error.isNullOrEmpty())

        //mainDispatcherRule.resumeDispatcher()

        //signUpUiState = viewmodel.signUpScreenUiStateLiveData.getOrAwaitValue()


        //assert(!signUpUiState.isLoading)
        //assert(signUpUiState.isSuccess)
    }

    @Test
    fun signUpUser_OldUserFail() = runBlockingTest {

        viewmodel.signUpUser(FakeDataProvider.user1, contentResolver)

        val signUpUiState = viewmodel.signUpScreenUiStateLiveData.getOrAwaitValue()

        assert(!signUpUiState.isSuccess)
        assert(signUpUiState.error == UserAlreadyExitsException().message)

    }

    @Test
    fun loginUser_success() = runBlockingTest {

        viewmodel.signInUser(FakeDataProvider.user1.userName, FakeDataProvider.user1.password ?: "")

        val signUpUiState = viewmodel.signInScreenUiStateLiveData.getOrAwaitValue()

        assert(signUpUiState.isSuccess)
        assert(signUpUiState.error.isNullOrEmpty())

    }

    @Test
    fun loginUser_noSuchUser() = runBlockingTest {

        viewmodel.signInUser(
            FakeDataProvider.myUser.userName,
            FakeDataProvider.myUser.password ?: ""
        )

        val signUpUiState = viewmodel.signInScreenUiStateLiveData.getOrAwaitValue()

        assert(!signUpUiState.isSuccess)
        assert(signUpUiState.error != null && signUpUiState.error == NoSuchUserException().message)

    }

}