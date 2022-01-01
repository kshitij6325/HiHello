package com.example.pojo

import java.lang.Exception

sealed class Result<T> {
    class Success<T>(val data: T) : Result<T>()
    class Failure<T>(val exception: Exception) : Result<T>()
}