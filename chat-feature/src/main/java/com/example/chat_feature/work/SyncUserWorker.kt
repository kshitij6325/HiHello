package com.example.chat_feature.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.auth.domain.SyncUsersUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncUserWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncUsersUseCase: SyncUsersUseCase
) : CoroutineWorker(appContext, workerParams) {


    override suspend fun doWork(): Result {

        return when (syncUsersUseCase.invoke()) {
            is com.example.pojo.Result.Failure -> Result.failure()
            is com.example.pojo.Result.Success -> Result.success()
        }
    }


    companion object {
        const val TAG = "SyncUserWork"
    }

}