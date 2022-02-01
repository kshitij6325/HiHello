package com.example.auth_feature.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.app.ProgressDialog
import androidx.activity.result.ActivityResult
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.navigation.NavDeepLinkRequest
import com.example.auth.User
import com.example.auth_feature.AuthViewModel
import com.example.auth_feature.R
import com.example.auth_feature.databinding.FragmentOtpBinding
import com.example.auth_feature.databinding.FragmentSignUpBinding
import com.example.basefeature.*
import dagger.hilt.android.AndroidEntryPoint

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

        viewModel.otpScreenUiStateLiveData
            .map { it.error }
            .distinctUntilChanged()
            .observe(this) { message ->
                message?.let { showToast(it) }
            }
        //observe signUp state
        viewModel.otpScreenUiStateLiveData.map { it.isSuccess }
            .distinctUntilChanged().observe(this) {
                if (it) {
                    navigate(
                        requireActivity().resources.getString(R.string.chat_home_frag_deeplink_string),
                        R.id.auth_nav
                    )
                }
            }

        viewModel.otpScreenUiStateLiveData.map { it.isLoading }
            .distinctUntilChanged().observe(this) {
                showLoaderIf("Verifying otp...", it)
            }

    }

    private fun submitOtp(view: View) {
        val otp = binding?.etvOtp?.text.toString()
        viewModel.submitOtp(otp, requireActivity())
    }
}