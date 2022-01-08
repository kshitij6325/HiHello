package com.example.hihello.home.homeactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.navigation.findNavController
import com.example.basefeature.showToast
import com.example.hihello.R
import com.example.hihello.home.HomeViewModel
import com.example.hihello.home.homefragment.HomeFragmentDirections
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
                    findNavController(R.id.nav_host_fragment).navigate(HomeFragmentDirections.actionMoveToHome())
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

    /*private suspend fun sendMessageToDevice(token: String) = withContext(Dispatchers.IO) {
        val root = JSONObject()
        val notification = JSONObject()
        notification.put("body", "Kahe")
        notification.put("title", "title first")

        val data = JSONObject()
        data.put("message", message)
        root.put("notification", notification)
        root.put("data", data)
        root.put("registration_ids", JSONArray(listOf(token).toTypedArray()))

        val res = OkHttpClient().run {
            newCall(
                Request.Builder().apply {
                    url("https://fcm.googleapis.com/fcm/send")
                    post(
                        RequestBody.create(
                            MediaType.get("application/json; charset=utf-8"),
                            root.toString()
                        )
                    )
                    addHeader(
                        "Authorization",
                        "key=AAAAgMyfgAc:APA91bFTni7UMKK9Yrhr-lNXtwHQEK480s7GN1O7KOTtuO1Kup0KUBKbYtBag1R9uQ64mp7X3OUf_C3Kbpw9HAVy78yuweicmsUbtXUiuobyuLWbyhVVyNz3_02S3wY6m24o2xQCwrpj"
                    )
                }.build()
            )
        }.execute()

        Log.e("RESPONSE", res.body().toString())
    }*/
}