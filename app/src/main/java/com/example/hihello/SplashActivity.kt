package com.example.hihello

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.auth.User
import com.example.auth.usecase.SignUpUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import okhttp3.*
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    //private val firebaseInstance = FirebaseMessaging.getInstance()

    @Inject
    lateinit var signUpUseCase: SignUpUseCase

    // private val secondId =
    //    "cH6Oz_DDRHeekfj6MlKnwE:APA91bGxebnnE3HWKswNRM67Ys_LtwiE52XmDyPiOnEea0pw0e2fjFAnRy32nABtgndF-NPZMOuyBC2gipgFvzsmf-cHWL7REQZU4_SG3V82qgxAxL9y5MCipFAmo_Kvt8ZW4lFrq8G8"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CoroutineScope(Job()).launch {
            signUpUseCase.invoke(
                User(
                    "Rahul123444455765",
                    mobileNumber = 9923339600,
                    profileUrl = "ramesh"
                ), {
                    Toast.makeText(
                        this@SplashActivity,
                        it.fcmToken,
                        Toast.LENGTH_LONG
                    )
                        .show()
                }, {
                    Toast.makeText(this@SplashActivity, it.message, Toast.LENGTH_LONG)
                        .show()
                })
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