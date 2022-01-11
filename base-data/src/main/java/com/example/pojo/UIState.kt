package com.example.pojo

import java.lang.Exception

sealed class UIState<T> {
    class Loading<T> : UIState<T>()
    class Success<T>(val data: T) : UIState<T>()
    class Failure<T>(val message: String) : UIState<T>()

    fun onSuccess(exec: (T) -> Unit): UIState<T> {
        when (this) {
            is Success -> exec(data)
            else -> {} //no-op
        }
        return this
    }

    fun onFailure(exec: (String) -> Unit): UIState<T> {
        when (this) {
            is Failure -> exec(message)
            else -> {} //no-op
        }
        return this
    }

    fun onLoading(exec: () -> Unit): UIState<T> {
        when (this) {
            is Loading -> exec()
            else -> {} //no-op
        }
        return this
    }
}