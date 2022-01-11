package com.example.auth_feature.signup

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.navigation.NavDeepLinkRequest
import com.example.auth.User
import com.example.auth_feature.AuthViewModel
import com.example.auth_feature.R
import com.example.auth_feature.databinding.FragmentSignUpBinding
import com.example.basefeature.BaseFragment
import com.example.basefeature.showIf
import com.example.basefeature.showProgressDialog
import com.example.basefeature.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding>() {

    override val getBindingInflation: (LayoutInflater) -> FragmentSignUpBinding
        get() = FragmentSignUpBinding::inflate

    private val viewModel: AuthViewModel by viewModels()

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
                    navigate(requireActivity().resources.getString(R.string.home_frag_deeplink_string))
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