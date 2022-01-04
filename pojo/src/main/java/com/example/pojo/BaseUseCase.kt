package com.example.pojo

import java.lang.Exception

open class BaseUseCase<T> {
    var onSuccess: (suspend (T) -> Unit)? = null
    var onFailure: (suspend (Exception) -> Unit)? = null
}
