package com.example.hihello

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.example.pojo.UIState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.isUserLoggedInLiveData.observe(this) {
            when (it) {
                is UIState.Failure -> Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                is UIState.Success -> {
                    if (it.data) {
                        Toast.makeText(this, "work in progress", Toast.LENGTH_SHORT).show()
                    } else {
                        startActivity(Intent(this, AuthActivity::class.java))
                        finish()
                    }
                }
                else -> {} // no-op
            }
        }
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