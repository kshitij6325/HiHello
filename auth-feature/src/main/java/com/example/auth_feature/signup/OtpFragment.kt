package com.example.auth_feature.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import com.example.auth_feature.AuthViewModel
import com.example.auth_feature.R
import com.example.auth_feature.databinding.FragmentOtpBinding
import com.example.basefeature.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*

@AndroidEntryPoint
class OtpFragment : MediaBaseFragment<FragmentOtpBinding>() {

    override val getBindingInflation: (LayoutInflater) -> FragmentOtpBinding
        get() = FragmentOtpBinding::inflate


    override val onFileSelected: suspend (ActivityResult) -> Unit = {
        viewModel.setUserAvatar(it.data?.data)
    }

    private val viewModel: AuthViewModel by hiltNavGraphViewModels(R.id.auth_nav)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.btnOtp?.setOnClickListener(this::submitOtp)

        viewModel.toastError
            .onEach { message ->
                message?.let { showToast(it) }
            }.launchIn(viewLifecycleOwner.lifecycleScope)

        //observe signUp state
        viewModel.otpScreenUiState
            .map { it.isSuccess }
            .distinctUntilChanged()
            .onEach {
                if (it) {
                    navigate(
                        requireActivity().resources.getString(R.string.chat_home_frag_deeplink_string),
                        R.id.auth_nav
                    )
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.otpScreenUiState
            .map { it.isLoading }
            .distinctUntilChanged()
            .onEach {
                showLoaderIf("Verifying otp...", it)
            }.launchIn(viewLifecycleOwner.lifecycleScope)

    }

    private fun submitOtp(view: View) {
        val otp = binding?.etvOtp?.text.toString()
        viewModel.submitOtp(otp, requireActivity())
    }
}