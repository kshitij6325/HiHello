package com.example.chat_data.data

import com.example.media_data.MediaDataSource
import com.example.media_data.MediaSource
import com.example.media_data.MediaType
import com.example.pojo.Result
import java.lang.Exception

class FakeMediaSource(private val mediaMap: MutableMap<String, MediaSource>) :
    MediaDataSource<MediaSource, MediaSource.Url> {
    var isFail = false
    override suspend fun getMedia(
        url: MediaSource.Url,
        mediaName: String,
        mediaType: MediaType,
        onProgress: (percent: Int) -> Unit
    ): Result<MediaSource> {
        if (isFail) return Result.Failure(Exception("Failure"))
        return mediaMap[url.url]?.let { Result.Success(it) }
            ?: Result.Failure(Exception("No such media"))
    }

    override suspend fun saveMedia(
        media: MediaSource,
        token: String,
        onProgress: (percent: Int) -> Unit
    ): Result<MediaSource.Url> {
        if (isFail) return Result.Failure(Exception("Failure"))
        mediaMap[media.toString()] = media
        return Result.Success(MediaSource.Url(media.toString(), media.mediaType))
    }
}