package com.example.hihello

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMCloudMessaging : FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.e("Firebase token :: ", p0)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(this, p0.data.get("key").toString(), Toast.LENGTH_SHORT).show()
        }
    }
}