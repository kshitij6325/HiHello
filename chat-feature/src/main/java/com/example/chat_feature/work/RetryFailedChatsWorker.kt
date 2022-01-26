package com.example.chat_feature.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.chat_data.usecase.RetryUnSendChats
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RetryFailedChatsWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val retryUnSendChats: RetryUnSendChats
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        retryUnSendChats.invoke()
        return Result.success()
    }

}