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
class FirebaseStorageDataSource @Inject constructor() :
    MediaDataSource {

    private val firebaseStorageRef = FirebaseStorage.getInstance().reference

    override suspend fun getMedia(
        url: MediaSource.Url,
        mediaName: String,
        mediaType: MediaType,
        onProgress: (percent: Int) -> Unit
    ): Result<MediaSource.File> =
        withContext(Dispatchers.IO) {
            val fsRef = FirebaseStorage.getInstance().getReferenceFromUrl(url.url)
            suspendCancellableCoroutine { cont ->
                val localFile = File.createTempFile(mediaName, mediaType.ext)
                fsRef.getFile(localFile).addOnCompleteListener {
                    if (it.isSuccessful) {
                        cont.resume(Result.Success(MediaSource.File(localFile, url.mediaType)))
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
        media: MediaSource.File,
        token: String,
        onProgress: (percent: Int) -> Unit
    ): Result<MediaSource.Url> =
        withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { cont ->
                val fsRef = firebaseStorageRef.child(token)
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