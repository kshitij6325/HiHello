package com.example.hihello.home.homeactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.navigation.findNavController
import com.example.basefeature.showToast
import com.example.hihello.NavHomeDirections
import com.example.hihello.R
import com.example.hihello.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.homeActivityUiStateLiveData
            .map { it.isLoggedIn }
            .distinctUntilChanged()
            .observe(this@HomeActivity) {
                if (it) {
                    findNavController(R.id.nav_host_fragment).navigate(NavHomeDirections.actionChatNavGraph())
                }

            }

        // show error message toast when lifecycle state it at-least started
        viewModel.homeActivityUiStateLiveData.map { it.isLoggedInError }
            .distinctUntilChanged()
            .observe(this) {
                showToast(it)
            }
        viewModel.isUserLoggedIn()
    }
}