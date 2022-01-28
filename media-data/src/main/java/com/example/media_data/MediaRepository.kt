package com.example.media_data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepository @Inject constructor(private val userFirebaseStorageDataSource: MediaDataSource) {

    suspend fun uploadMedia(
        mediaSource: MediaSource.File,
        token: String,
        onProgress: (percent: Int) -> Unit
    ) = userFirebaseStorageDataSource.saveMedia(mediaSource, token, onProgress)

}