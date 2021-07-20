package com.cybershark.drawingsapp.util

import android.content.Context
import android.net.Uri
import android.text.format.DateUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.File
import java.util.*

internal fun Date.getFriendlyString(): String {
    val currentTime = Date()
    val timeInAgo = DateUtils.getRelativeTimeSpanString(this.time, currentTime.time, DateUtils.MINUTE_IN_MILLIS)
    return timeInAgo.toString()
}

internal fun AppCompatActivity.showToast(message: String, length: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, message, length).show()
internal fun Fragment.showToast(message: String, length: Int = Toast.LENGTH_SHORT) = Toast.makeText(context, message, length).show()
internal fun Context.showToast(message: String, length: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, message, length).show()

fun <T> AppCompatActivity.observe(liveData: LiveData<T>, action: (t: T) -> Unit) {
    liveData.observe(this) { t ->
        action(t)
    }
}

fun <T> Fragment.observe(liveData: LiveData<T>, action: (t: T) -> Unit) {
    liveData.observe(viewLifecycleOwner) { t ->
        action(t)
    }
}

internal fun Context.copyImage(inputFileUri: Uri): Uri {
    val inputFileExtension = inputFileUri.toFile().extension.ifBlank { "jpg" }
    val inputFileName = inputFileUri.toFile().nameWithoutExtension.ifBlank { "drawing" }
    val date = Date().time
    val outputFileName = "$inputFileName-$date.$inputFileExtension"
    this.openFileOutput(outputFileName, Context.MODE_PRIVATE).use {
        it.write(inputFileUri.toFile().readBytes())
    }
    val outputFile = File(this.filesDir, outputFileName)
    if (outputFile.exists() && outputFile.canRead()) {
        return outputFile.toUri()
    } else {
        throw Exception("Error copying image")
    }
}

fun MutableLiveData<UIState>.getDefault() = this.apply { value = UIState.IDLE }

fun MutableLiveData<UIState>.setLoading() = this.apply { value = UIState.LOADING }

fun MutableLiveData<UIState>.setSuccess(message: String) =
    this.apply { value = UIState.COMPLETED(message) }

fun MutableLiveData<UIState>.setError(message: String) =
    this.apply { value = UIState.ERROR(message) }

fun MutableLiveData<UIState>.setError(throwable: Throwable?) =
    this.apply { value = UIState.ERROR(throwable?.message ?: "An error occurred") }