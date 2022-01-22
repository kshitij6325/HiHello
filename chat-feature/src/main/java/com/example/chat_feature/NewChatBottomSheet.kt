package com.example.chat_feature

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import com.example.basefeature.showToast
import com.example.chat_feature.chathome.ChatHomeFragment
import com.example.chat_feature.databinding.BottomsheetFragmentAddChatBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewChatBottomSheet : BottomSheetDialogFragment() {

    private val viewModel by viewModels<ChatUserViewModel>()
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
                    (parentFragment as? ChatHomeFragment)?.updateChatList?.invoke()
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