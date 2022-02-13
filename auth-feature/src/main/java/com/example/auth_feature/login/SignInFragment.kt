package com.example.auth_feature.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.example.auth_feature.AuthViewModel
import com.example.auth_feature.R
import com.example.auth_feature.databinding.FragmentSignInBinding
import com.example.basefeature.BaseFragment
import com.example.basefeature.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*

@AndroidEntryPoint
class SignInFragment : BaseFragment<FragmentSignInBinding>() {


    override val getBindingInflation: (LayoutInflater) -> FragmentSignInBinding
        get() = FragmentSignInBinding::inflate


    private val viewModel: AuthViewModel by hiltNavGraphViewModels(R.id.auth_nav)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.btnLogin?.setOnClickListener(this::signIn)
        binding?.tvSignUp?.setOnClickListener(this::moveToSignUp)

        viewModel.signInScreenUiState
            .map { it.isSuccess }
            .distinctUntilChanged()
            .onEach {
                if (it) {
                    viewModel.navigateToChat(requireActivity(), this@SignInFragment::navigate)
                }
            }.launchIn(uiScope)

        //observe signIn state
        viewModel.signInScreenUiState
            .map { it.isLoading }
            .distinctUntilChanged()
            .onEach {
                showLoaderIf("Signing in...", it)
            }.launchIn(uiScope)

        //show error toast
        viewModel.toastError
            .onEach {
                showToast(it)
            }.launchIn(uiScope)
    }

    private fun signIn(view: View) {
        val userName = binding?.tvUserName?.text.toString()
        val password = binding?.tvPassword?.text.toString()
        viewModel.signInUser(userId = userName, password = password)
    }

    private fun moveToSignUp(view: View) {
        viewModel.navigateToSignUp(this::navigate)

    }

}