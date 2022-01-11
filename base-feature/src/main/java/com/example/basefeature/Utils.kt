package com.example.basefeature

import android.app.ProgressDialog
import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.lang.Exception

fun Context.showToast(string: String?) =
    string?.let { Toast.makeText(this, string, Toast.LENGTH_SHORT).show() }

fun Fragment.showToast(string: String?) = try {
    string?.let {
        Toast.makeText(requireContext(), string, Toast.LENGTH_SHORT).show()
    }
} catch (e: Exception) {
}

fun Context.showProgressDialog(message: String) = ProgressDialog(this).apply {
    setMessage(message)
    setCancelable(false)
}

fun ProgressDialog.showIf(condition: Boolean) = if (condition) show() else hide()

fun <T> MutableLiveData<T>.update(evaluator: (T?) -> T) {
    val currentValue = value
    value = currentValue?.run(evaluator)
}