package com.example.media_data

sealed class MediaSource(val mediaType: MediaType) {
    class File(val file: java.io.File, mediaType: MediaType) : MediaSource(mediaType)
    class Url(val url: String, mediaType: MediaType) : MediaSource(mediaType)
}

enum class MediaType(val string: String, val ext: String) {
    IMAGE("image", "png"),
}