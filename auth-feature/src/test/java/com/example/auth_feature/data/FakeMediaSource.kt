package com.example.auth_feature.data

import com.example.media_data.MediaDataSource
import com.example.media_data.MediaSource
import com.example.media_data.MediaType
import com.example.pojo.Result
import java.lang.Exception

class FakeMediaSource(private val mediaMap: MutableMap<String, MediaSource>) :
    MediaDataSource {
    var isFail = false
    override suspend fun getMedia(
        url: MediaSource.Url,
        mediaName: String,
        mediaType: MediaType,
        onProgress: (percent: Int) -> Unit
    ): Result<MediaSource.File> {
        if (isFail) return Result.Failure(Exception("Failure"))
        return mediaMap[url.url]?.let {
            Result.Success(
                MediaSource.File(
                    kotlin.io.path.createTempFile(
                        url.toString()
                    ).toFile(),
                    MediaType.IMAGE
                )
            )
        }
            ?: Result.Failure(Exception("No such media"))
    }

    override suspend fun saveMedia(
        media: MediaSource.File,
        token: String,
        onProgress: (percent: Int) -> Unit
    ): Result<MediaSource.Url> {
        if (isFail) return Result.Failure(Exception("Failure"))
        mediaMap[media.toString()] = media
        return Result.Success(MediaSource.Url(media.toString(), media.mediaType))
    }
}