package com.example.hihello.home.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.map
import com.example.basefeature.BaseFragment
import com.example.basefeature.showToast
import com.example.hihello.home.HomeViewModel
import com.example.hihello.databinding.FragmentSignInBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInFragment : BaseFragment<FragmentSignInBinding>() {


    override val getBindingInflation: (LayoutInflater) -> FragmentSignInBinding
        get() = FragmentSignInBinding::inflate


    private val viewModel: HomeViewModel by viewModels()

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
                    navigate(SignInFragmentDirections.actionSignInFragmentToHomeFragment())
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