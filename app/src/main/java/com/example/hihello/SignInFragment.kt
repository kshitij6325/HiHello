package com.example.hihello

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.auth.User
import com.example.hihello.databinding.FragmentSignInBinding
import com.example.hihello.databinding.FragmentSignUpBinding
import com.example.pojo.UIState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private val viewModel: HomeViewModel by activityViewModels()
    private var binding: FragmentSignInBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return FragmentSignInBinding.inflate(inflater).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.btnLogin?.setOnClickListener(this::signIn)
        binding?.tvSignUp?.setOnClickListener(this::moveToSignUp)
        viewModel.signInLiveData.observe(this) {
            when (it) {
                is UIState.Failure -> requireActivity().showToast(it.message)
                is UIState.Loading -> {} // no-op
                is UIState.Success -> findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToHomeFragment())
            }
        }
    }

    private fun signIn(view: View) {
        val userName = binding?.tvUserName?.text.toString()
        val password = binding?.tvPassword?.text.toString()
        viewModel.signInUser(userId = userName, password = password)
    }

    private fun moveToSignUp(view: View) {
        findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToSignUpFragment())
    }
}