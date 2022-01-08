package com.example.hihello.home.auth.signup

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.lifecycle.repeatOnLifecycle
import com.example.auth.User
import com.example.basefeature.BaseFragment
import com.example.basefeature.showIf
import com.example.basefeature.showProgressDialog
import com.example.basefeature.showToast
import com.example.hihello.home.HomeViewModel
import com.example.hihello.databinding.FragmentSignUpBinding
import com.example.hihello.home.auth.login.SignInFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding>() {

    override val getBindingInflation: (LayoutInflater) -> FragmentSignUpBinding
        get() = FragmentSignUpBinding::inflate

    private val viewModel: HomeViewModel by viewModels()

    private var p: ProgressDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.btnSingup?.setOnClickListener(this::signUp)
        binding?.tvLogin?.setOnClickListener(this::moveToSignIn)

        viewModel.signUpScreenUiStateLiveData
            .map { it.error }
            .distinctUntilChanged()
            .observe(this@SignUpFragment) { message ->
                message?.let { showToast(it) }
            }


        //observe signUp state
        viewModel.signUpScreenUiStateLiveData.map { it.isSuccess }
            .distinctUntilChanged().observe(this) {
                if (it) {
                    navigate(SignUpFragmentDirections.actionSignUpFragmentToHomeFragment())
                }
            }

        viewModel.signUpScreenUiStateLiveData.map { it.isLoading }
            .distinctUntilChanged().observe(this) {
                (p ?: requireActivity().showProgressDialog("Loading...")
                    .also { this.p = it }).showIf(it)
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