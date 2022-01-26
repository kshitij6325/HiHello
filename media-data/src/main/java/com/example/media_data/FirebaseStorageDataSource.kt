package com.example.media_data

import android.graphics.Bitmap
import androidx.core.net.toUri
import com.example.pojo.Result
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class FirebaseStorageDataSource @Inject constructor(source: Source) :
    MediaDataSource<MediaSource, MediaSource.Url> {

    private val firebaseStorageRef = FirebaseStorage.getInstance().reference.child(source.source)

    companion object {
        enum class Source(val source: String) {
            USER_PROFILE("user_profile"),
            CHAT("chat")
        }
    }

    override suspend fun getMedia(
        url: MediaSource.Url,
        mediaName: String,
        mediaType: MediaType,
        onProgress: (percent: Int) -> Unit
    ): Result<MediaSource> =
        withContext(Dispatchers.IO) {
            val fsRef = FirebaseStorage.getInstance().getReferenceFromUrl(url.url)
            suspendCancellableCoroutine { cont ->
                val localFile = File.createTempFile(mediaName, mediaType.ext)
                fsRef.getFile(localFile).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val media = when (mediaType) {
                            MediaType.IMAGE -> MediaSource.File.ImageFile(localFile)
                        }
                        cont.resume(Result.Success(media))
                    } else {
                        it.exception?.let { ex ->
                            cont.resume(Result.Failure(ex))
                        }
                    }
                }.addOnFailureListener {
                    cont.resume(Result.Failure(it))
                }
            }
        }

    override suspend fun saveMedia(
        media: MediaSource,
        token: String,
        onProgress: (percent: Int) -> Unit
    ): Result<MediaSource.Url> =
        withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { cont ->
                val fsRef = firebaseStorageRef.child("$token/${media.mediaType.string}")
                when (media) {
                    is MediaSource.Bitmap -> {
                        val stream = ByteArrayOutputStream()
                        media.bm.compress(Bitmap.CompressFormat.PNG, 90, stream)
                        fsRef.putBytes(stream.toByteArray()).addOnFailureListener {
                            cont.resume(Result.Failure(it))
                        }.addOnCompleteListener {
                            Result.Success(
                                MediaSource.Url(
                                    it.result.toString(),
                                    media.mediaType
                                )
                            )
                        }
                    }
                    is MediaSource.File -> {
                        fsRef.putFile(media.file.toUri()).addOnFailureListener {
                            cont.resume(Result.Failure(it))
                        }.continueWithTask {
                            fsRef.downloadUrl
                        }.addOnCompleteListener {
                            cont.resume(
                                Result.Success(
                                    MediaSource.Url(
                                        it.result.toString(),
                                        media.mediaType
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
}