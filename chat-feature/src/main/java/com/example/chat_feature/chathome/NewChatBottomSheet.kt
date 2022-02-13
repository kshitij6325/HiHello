package com.example.chat_feature.chathome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.basefeature.showToast
import com.example.chat_feature.ChatHomeViewModel
import com.example.chat_feature.databinding.BottomsheetFragmentAddChatBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

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
            .onEach {
                if (it) {
                    dismiss()
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.newChatUiStateLiveData
            .map { it.error }
            .distinctUntilChanged()
            .onEach {
                showToast(it)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }
}