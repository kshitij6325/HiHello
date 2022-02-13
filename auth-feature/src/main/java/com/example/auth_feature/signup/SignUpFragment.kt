package com.example.auth_feature.signup


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.example.auth.User
import com.example.auth_feature.AuthViewModel
import com.example.auth_feature.R
import com.example.auth_feature.databinding.FragmentSignUpBinding
import com.example.basefeature.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*

@AndroidEntryPoint
class SignUpFragment : MediaBaseFragment<FragmentSignUpBinding>() {

    override val getBindingInflation: (LayoutInflater) -> FragmentSignUpBinding
        get() = FragmentSignUpBinding::inflate

    override val onFileSelected: suspend (ActivityResult) -> Unit = {
        viewModel.setUserAvatar(it.data?.data)
    }

    private val viewModel: AuthViewModel by hiltNavGraphViewModels(R.id.auth_nav)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.btnSingup?.setOnClickListener(this::signUp)
        binding?.tvLogin?.setOnClickListener(this::moveToSignIn)

        viewModel.toastError
            .onEach { message ->
                message?.let { showToast(it) }
            }.launchIn(uiScope)

        //observe signUp state
        viewModel.signUpScreenUiState
            .map { it.isSuccess }
            .distinctUntilChanged().onEach {
                if (it) {
                    viewModel.navigateToChat(requireActivity(), this@SignUpFragment::navigate)
                }
            }.launchIn(uiScope)

        viewModel.signUpScreenUiState
            .map { it.isLoading }
            .distinctUntilChanged().onEach {
                showLoaderIf("Signing up...", it)
            }.launchIn(uiScope)

        viewModel.signUpScreenUiState
            .map { it.imageUri }
            .distinctUntilChanged()
            .onEach {
                binding?.ivAvatar?.setImageURI(it)
            }.launchIn(uiScope)

        viewModel.signUpScreenUiState
            .map { it.goToOtp }
            .distinctUntilChanged()
            .onEach {
                if (it)
                    viewModel.navigateToOtp(this@SignUpFragment::navigate)
            }.launchIn(uiScope)

        binding?.ivAvatar?.setOnClickListener {
            openFilePicker()
        }
    }


    private fun signUp(view: View) {
        val userName = binding?.tvUserName?.text.toString()
        val mobile = binding?.tvMobile?.text.toString()
        val password = binding?.tvPassword?.text.toString()

        viewModel.signUpUser(
            User(
                userName = userName,
                mobileNumber = mobile.toLongOrNull(),
                password = password
            ),
            requireActivity()
        )
    }

    private fun moveToSignIn(view: View) {
        viewModel.navigateToSignIn(this::navigate)
    }


}