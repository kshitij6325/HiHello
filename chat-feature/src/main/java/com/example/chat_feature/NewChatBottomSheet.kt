package com.example.chat_feature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import com.example.basefeature.showToast
import com.example.chat_feature.databinding.BottomsheetFragmentAddChatBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewChatBottomSheet : BottomSheetDialogFragment() {

    private val viewModel by viewModels<ChatHomeViewModel>()
    private var binding: BottomsheetFragmentAddChatBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomsheetFragmentAddChatBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.btnSend?.setOnClickListener {
            viewModel.sendChat(
                userName = binding?.tvUserName?.text?.toString() ?: "",
                message = "Hey \uD83D\uDC4B"
            )
        }

        viewModel.newChatUiStateLiveData
            .map { it.isSuccess }
            .distinctUntilChanged()
            .observe(this) {
                if (it) {
                    dismiss()
                }
            }

        viewModel.newChatUiStateLiveData
            .map { it.error }
            .distinctUntilChanged()
            .observe(this) {
                showToast(it)
            }
    }
}