package com.example.media_data

import androidx.annotation.IntRange
import com.example.pojo.Result

interface MediaDataSource<Input : MediaSource, Output : MediaSource> {
    suspend fun getMedia(
        url: Output,
        mediaName: String,
        mediaType: MediaType,
        onProgress: (percent: Int) -> Unit
    ): Result<Input>

    suspend fun saveMedia(
        media: Input,
        token: String,
        onProgress: (percent: Int) -> Unit
    ): Result<Output>
}