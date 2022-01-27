package com.example.basefeature

import android.app.ProgressDialog
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
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

fun View.tintBackground(@ColorInt color: Int) {
    background = DrawableCompat.wrap(background).apply {
        DrawableCompat.setTint(this, color)
    }
}

fun View.getDimen(@DimenRes dimen: Int) = context.getDimen(dimen)

fun Context.getDimen(@DimenRes dimen: Int) = resources.getDimensionPixelOffset(dimen)

fun ImageView.setTint(@ColorRes color: Int) {
    setColorFilter(ContextCompat.getColor(context, color))
}

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.visibleIf(condition: Boolean) {
    visibility = if (condition) View.VISIBLE else View.GONE
}

fun TextView.setTextWithVisibility(mtext: String?) {
    if (!mtext.isNullOrEmpty()) {
        visible()
        text = mtext
    } else {
        gone()
    }
}