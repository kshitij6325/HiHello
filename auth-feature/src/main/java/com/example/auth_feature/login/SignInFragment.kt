package com.example.auth_feature.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import com.example.auth_feature.AuthViewModel
import com.example.auth_feature.R
import com.example.auth_feature.databinding.FragmentSignInBinding
import com.example.basefeature.BaseFragment
import com.example.basefeature.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : BaseFragment<FragmentSignInBinding>() {


    override val getBindingInflation: (LayoutInflater) -> FragmentSignInBinding
        get() = FragmentSignInBinding::inflate


    private val viewModel: AuthViewModel by hiltNavGraphViewModels(R.id.auth_nav)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.btnLogin?.setOnClickListener(this::signIn)
        binding?.tvSignUp?.setOnClickListener(this::moveToSignUp)

        //observe signIn state
        viewModel.signInScreenUiStateLiveData
            .map { it.isSuccess }
            .distinctUntilChanged()
            .observe(this) {
                if (it) {
                    navigate(
                        requireActivity().resources.getString(R.string.chat_home_frag_deeplink_string),
                        R.id.auth_nav
                    )
                }
            }

        //show error toast
        viewModel.signInScreenUiStateLiveData.map { it.error }
            .distinctUntilChanged()
            .observe(this) {
                showToast(it)
            }
    }

    private fun signIn(view: View) {
        val userName = binding?.tvUserName?.text.toString()
        val password = binding?.tvPassword?.text.toString()
        viewModel.signInUser(userId = userName, password = password)
    }

    private fun moveToSignUp(view: View) {
        navigate(SignInFragmentDirections.actionSignInFragmentToSignUpFragment())
    }

}