package com.example.media_data

sealed class MediaSource(val mediaType: MediaType) {

    sealed class File(val file: java.io.File, mediaType: MediaType) : MediaSource(mediaType) {
        class ImageFile(val imageFile: java.io.File) : File(imageFile, MediaType.IMAGE)
    }


    class Url(val url: String, mediaType: MediaType) : MediaSource(mediaType)

    class Bitmap(val bm: android.graphics.Bitmap) : MediaSource(MediaType.IMAGE)

}

enum class MediaType(val string: String, val ext: String) {
    IMAGE("image", "png"),
}