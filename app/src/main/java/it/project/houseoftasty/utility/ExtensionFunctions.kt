package it.project.houseoftasty.utility

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import com.google.firebase.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

// Extension functions per Int
fun Int.extendToTens() : String {
    return (if (this < 10) "0" else "") + this
}

// Extension functions per String
fun String.toIntOrZero(): Int {
    return this.toIntOrNull() ?: 0
}

// Extension functions per Any
fun Any.toInt(): Int {
    return this.toString().toIntOrZero()
}

// Extension functions per LocalDateTime
fun LocalDateTime.minuteExtended(): String {
    return this.minute.extendToTens()
}

fun LocalDateTime.hourExtended(): String {
    return this.hour.extendToTens()
}

fun LocalDateTime.dayOfMonthExtended(): String {
    return this.dayOfMonth.extendToTens()
}

fun LocalDateTime.monthValueExtended(): String {
    return this.monthValue.extendToTens()
}

fun LocalDateTime.dateToString(extended: Boolean = false): String {
    val stringBuilder = StringBuilder()
    if (extended)
        stringBuilder.append(this.dayOfMonthExtended(), "/", this.monthValueExtended())
    else
        stringBuilder.append(this.dayOfMonth, "/", this.monthValue)
    return stringBuilder.append("/", this.year).toString()
}

fun LocalDateTime.timeToString(extended: Boolean = false): String {
    val stringBuilder = StringBuilder()
    if (extended)
        stringBuilder.append(this.hourExtended())
    else
        stringBuilder.append(this.hour)
    return stringBuilder.append(":", this.minuteExtended()).toString()
}

// Extension functions per Timestamp (di Firebase)
fun Timestamp.toLocalDateTime(): LocalDateTime {
    val milliseconds = this.seconds * 1000 + this.nanoseconds / 1000000
    val timezone = ZoneId.systemDefault()
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), timezone)
}

// Extension functions per View
fun View.tagToString(): String {
    return this.tag.toString()
}

// Extension functions per EditText
fun EditText.textToString(): String {
    return this.text.toString()
}

fun EditText.textToInt(): Int {
    return this.textToString().toIntOrZero()
}

fun EditText.onTextChanged(onTextChanged: () -> Boolean) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged.invoke()
        }

        override fun afterTextChanged(editable: Editable?) {
        }
    })
}

fun EditText.onFocusLost(onFocusLost: () -> Boolean) {
    this.setOnFocusChangeListener { _, hasFocus ->
        if (!hasFocus) {
            onFocusLost.invoke()
        }
    }
}

// Extension functions per MutableList
fun <T> MutableList<T>.prepend(element: T) {
    add(0, element)
}

