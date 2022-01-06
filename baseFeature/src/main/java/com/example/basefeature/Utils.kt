package com.example.basefeature

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.lang.Exception

fun Context.showToast(string: String) = Toast.makeText(this, string, Toast.LENGTH_SHORT).show()

fun Fragment.showToast(string: String) = try {
    Toast.makeText(requireContext(), string, Toast.LENGTH_SHORT).show()
} catch (e: Exception) {
}