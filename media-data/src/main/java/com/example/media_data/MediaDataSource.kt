package com.example.media_data

import androidx.annotation.IntRange
import com.example.pojo.Result

interface MediaDataSource {
    suspend fun getMedia(
        url: MediaSource.Url,
        mediaName: String,
        mediaType: MediaType,
        onProgress: (percent: Int) -> Unit
    ): Result<MediaSource.File>

    suspend fun saveMedia(
        media: MediaSource.File,
        token: String,
        onProgress: (percent: Int) -> Unit
    ): Result<MediaSource.Url>
}