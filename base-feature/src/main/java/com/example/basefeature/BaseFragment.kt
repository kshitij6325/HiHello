package com.example.basefeature

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    abstract val getBindingInflation: (LayoutInflater) -> VB
    var binding: VB? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return getBindingInflation(inflater).also {
            binding = it
        }.root
    }

    fun navigate(direction: NavDirections) {
        findNavController().navigate(direction)
    }

    fun navigate(deeplink: String) {
        val request = NavDeepLinkRequest.Builder
            .fromUri(deeplink.toUri())
            .build()
        findNavController().navigate(request)
    }

    fun launch(block: suspend CoroutineScope.() -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch(block = block)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}