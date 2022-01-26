package com.example.hihello

import android.util.Log
import com.example.auth.usecase.GetUserLoggedInUseCase
import com.example.auth.usecase.UpdateLoggedInUserUseCase
import com.example.chat_data.repo.CHAT_DATA
import com.example.chat_data.usecase.ReceiveChatUseCase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class FCMCloudMessaging : FirebaseMessagingService() {
    @Inject
    lateinit var chatUseCase: ReceiveChatUseCase

    @Inject
    lateinit var userUpdateLoggedInUseCase: UpdateLoggedInUserUseCase

    @Inject
    lateinit var getLoggedInUser: GetUserLoggedInUseCase

    private var scope = CoroutineScope(SupervisorJob())

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        scope.launch {
            getLoggedInUser.apply {
                onSuccess = { loggedInUser ->
                    if (loggedInUser != null) {
                        userUpdateLoggedInUseCase.invoke(loggedInUser.copy(fcmToken = p0))
                    }
                }
            }.invoke()
        }
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        scope.launch {
            val chatString = p0.data[CHAT_DATA].toString()
            chatUseCase.invoke(chatString)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}