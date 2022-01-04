package com.example.pojo

sealed class UIState<T> {
    class Loading<T> : UIState<T>()
    class Success<T>(val data: T) : UIState<T>()
    class Failure<T>(val message: String) : UIState<T>()
}