package com.example.hihello.home.auth.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import com.example.auth.User
import com.example.basefeature.BaseFragment
import com.example.basefeature.showToast
import com.example.hihello.home.HomeViewModel
import com.example.hihello.databinding.FragmentSignUpBinding
import com.example.hihello.home.auth.login.SignInFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding>() {

    override val getBindingInflation: (LayoutInflater) -> FragmentSignUpBinding
        get() = FragmentSignUpBinding::inflate

    private val viewModel: HomeViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.btnSingup?.setOnClickListener(this::signUp)
        binding?.tvLogin?.setOnClickListener(this::moveToSignIn)

        launch {

            //observe signUp state
            viewModel.signUpScreenUiStateLiveData.map { it.isSuccess }.collect {
                if (it) {
                    navigate(SignUpFragmentDirections.actionSignUpFragmentToHomeFragment())
                }
            }

            //show error toast
            viewModel.signUpScreenUiStateLiveData.map { it.error }
                .distinctUntilChanged()
                .collect {
                    showToast(it)
                }
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
            )
        )
    }

    private fun moveToSignIn(view: View) {
        navigate(SignUpFragmentDirections.actionSignUpFragmentToSignInFragment())
    }

}