package com.example.hihello

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import android.R.id.message
import android.media.MediaDataSource
import androidx.room.Room
import com.example.auth.User
import com.example.auth.datasource.MeLocalDataSource
import com.example.auth.datasource.UserFirebaseDataSource
import com.example.auth.datasource.UserRoomDataSource
import com.example.auth.repo.UserRepository
import com.example.pojo.Result
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONArray

import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    private val firebaseInstance = FirebaseMessaging.getInstance()

    private val secondId =
        "cH6Oz_DDRHeekfj6MlKnwE:APA91bGxebnnE3HWKswNRM67Ys_LtwiE52XmDyPiOnEea0pw0e2fjFAnRy32nABtgndF-NPZMOuyBC2gipgFvzsmf-cHWL7REQZU4_SG3V82qgxAxL9y5MCipFAmo_Kvt8ZW4lFrq8G8"
    private val userDataSource = UserFirebaseDataSource()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val roomDb = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "hihello"
        ).build()

        val userRepo = UserRepository(
            userFirebaseDataSource = UserFirebaseDataSource(),
            userRoomDataSource = UserRoomDataSource(roomDb.getUserDao()),
            meDataSource = MeLocalDataSource(
                getSharedPreferences(
                    "hihello_shared_perf",
                    MODE_PRIVATE
                )
            )
        )

        CoroutineScope(Job()).launch {
            val res =
                userRepo.signUpUser(
                    User(
                        "kshitij6325",
                        secondId,
                        "Rahul",
                        "Sharma",
                        8888888888,
                        "url"
                    )
                )

            when (res) {
                is Result.Failure -> Log.e("RES :: ", res.exception.message.toString())
                is Result.Success -> Log.e(
                    "SUCC :: ", "${
                        when (val resE = userRepo.getLoggedInUser()) {
                            is Result.Success -> resE.data.firstName
                            is Result.Failure -> resE.exception.message

                        }
                    }"
                )
            }
        }
    }

    private suspend fun sendMessageToDevice(token: String) = withContext(Dispatchers.IO) {
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
    }
}