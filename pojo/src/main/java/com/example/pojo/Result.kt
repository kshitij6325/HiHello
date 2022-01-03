package com.example.pojo

import java.lang.Exception

sealed class Result<T> {
    class Success<T>(val data: T) : Result<T>()
    class Failure<T>(val exception: Exception) : Result<T>()

    fun onSuccess(callback: (T) -> Unit) {
        when (this) {
            is Success -> callback(data)
            else -> {// no op}
            }
        }
    }

    suspend fun <P> map(mapper: suspend (T) -> Result<P>): Result<P> {
        return when (this) {
            is Failure -> Failure(exception)
            is Success -> mapper(data)
        }
    }

    suspend fun catch(mapper: (Exception) -> Unit) {
        when (this) {
            is Failure -> mapper(exception)
            else -> {// no op}
            }
        }
    }
}

