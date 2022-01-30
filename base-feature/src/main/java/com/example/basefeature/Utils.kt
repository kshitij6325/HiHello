package com.example.basefeature

import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.Exception
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.io.path.outputStream

fun Context.showToast(string: String?) =
    string?.let { Toast.makeText(this, string, Toast.LENGTH_SHORT).show() }

fun Fragment.showToast(string: String?) = try {
    string?.let {
        Toast.makeText(requireContext(), string, Toast.LENGTH_SHORT).show()
    }
} catch (e: Exception) {
}

fun Fragment.showProgressDialog(message: String) = ProgressDialog(requireContext()).apply {
    setMessage(message)
    setCancelable(false)
}

fun ProgressDialog.showIf(condition: Boolean) = if (condition) show() else cancel()

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

fun Long.getTime(): String {
    return DateTimeFormatter.ofPattern("hh:mm a").withZone(ZoneId.systemDefault())
        .format(Instant.ofEpochMilli(this))
}

suspend fun Uri.getFile(resolver: ContentResolver): File = withContext(Dispatchers.IO) {
    val file = kotlin.io.path.createTempFile("avatar", "png")
    val inputStream = resolver.openInputStream(this@getFile)

    inputStream.use { input ->
        file.outputStream().use { output ->
            input?.copyTo(output)
        }
    }
    return@withContext file.toFile()
}