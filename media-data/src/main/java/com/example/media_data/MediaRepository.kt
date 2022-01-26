package com.example.media_data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepository @Inject constructor(private val userFirebaseStorageDataSource: MediaDataSource<MediaSource, MediaSource.Url>) {

    suspend fun uploadMedia(
        mediaSource: MediaSource,
        token: String,
        onProgress: (percent: Int) -> Unit
    ) = userFirebaseStorageDataSource.saveMedia(mediaSource, token, onProgress)

}