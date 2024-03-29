package it.project.houseoftasty.utility

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import it.project.houseoftasty.network.ProfileNetwork
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

// Extension functions per Int
fun Int.extendToTens() : String {
    return (if (this < 10) "0" else "") + this
}

fun Int.increment(value: Int = 1): Int {
    return this + value
}

fun Int.decrement(value: Int = 1) : Int {
    return this - value
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

fun Timestamp.timeDiffSeconds(): Long {
    return Timestamp.now().seconds - this.seconds
}

// Extension functions per View
fun View.tagToString(): String {
    return this.tag.toString()
}

fun View.showSnackbar(msgId: Int, length: Int) {
    showSnackbar(context.getString(msgId), length)
}

fun View.showSnackbar(msg: String, length: Int) {
    showSnackbar(msg, length, null, {})
}

fun View.showSnackbar(
    msgId: Int,
    length: Int,
    actionMessageId: Int,
    action: (View) -> Unit
) {
    showSnackbar(context.getString(msgId), length, context.getString(actionMessageId), action)
}

fun View.showSnackbar(
    msg: String,
    length: Int,
    actionMessage: CharSequence?,
    action: (View) -> Unit
) {
    val snackbar = Snackbar.make(this, msg, length)
    if (actionMessage != null) {
        snackbar.setAction(actionMessage) {
            action(this)
        }.show()
    }
}

// Extension functions per TextView
fun TextView.textToString(): String {
    return this.text.toString()
}

fun TextView.textToInt(): Int {
    return this.textToString().toIntOrZero()
}

fun TextView.increment(value: Int = 1): String {
    return this.textToInt().increment(value).toString()
}

fun TextView.decrement(value: Int = 1): String {
    return this.textToInt().decrement(value).toString()
}

// Extension functions per EditText
fun EditText.textToString(): String {
    return this.text.toString()
}

fun EditText.textToInt(): Int {
    return this.textToString().toIntOrZero()
}


fun EditText.increment(value: Int = 1): String {
    return this.textToInt().increment(value).toString()
}

fun EditText.decrement(value: Int = 1): String {
    return this.textToInt().decrement(value).toString()
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

fun EditText.onFocusGained(onFocusGained: () -> Boolean) {
    this.setOnFocusChangeListener { _, hasFocus ->
        if (hasFocus) onFocusGained.invoke()
    }
}

fun EditText.onFocusLost(onFocusLost: () -> Boolean) {
    this.setOnFocusChangeListener { _, hasFocus ->
        if (!hasFocus) onFocusLost.invoke()
    }
}

fun EditText.onFocusChanged(onFocusGained: () -> Boolean, onFocusLost: () -> Boolean) {
    this.setOnFocusChangeListener { _, hasFocus ->
        if (hasFocus) onFocusGained.invoke() else onFocusLost.invoke()
    }
}

// Extension functions per ByteArray
fun ByteArray.toBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, this.size)
}

// Extension functions per Bitmap
fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}

// Extension functions per ImageView
fun ImageView.imageToByteArray(): ByteArray {
    return (this.drawable as BitmapDrawable).bitmap.toByteArray()
}

// Extension functions per MutableList
fun <T> MutableList<T>.prepend(element: T) {
    add(0, element)
}

fun <T> MutableList<T>.removeElement(element: T): MutableList<T>  {
    val iterator: MutableIterator<T> = this.iterator()
    while (iterator.hasNext()) {
        val nextElement = iterator.next()
        if (nextElement == element)
            iterator.remove()
    }
    return this
}

fun <T> MutableList<T>.removeIfContains(element: T): MutableList<T>  {
    return if (this.contains(element))
        this.removeElement(element)
    else this
}

// Extension functions per MutableLiveData
fun <T> MutableLiveData<T>.update(newValue: T) {
    /* Controlla se il thread attuale è quello principale; in caso affermativo, aggiorna
        * operationCompleted usando setValue(), altrimenti usando postValue() */
    if(Looper.myLooper() == Looper.getMainLooper()) {
        this.setValue(newValue)
    } else {
        this.postValue(newValue)
    }
}

// Extension functions per AppCompatActivity
fun AppCompatActivity.checkSelfPermissionCompat(permission: String) =
    ActivityCompat.checkSelfPermission(this, permission)

fun AppCompatActivity.shouldShowRequestPermissionRationaleCompat(permission: String) =
    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)

fun AppCompatActivity.requestPermissionsCompat(permissionsArray: Array<String>,
                                               requestCode: Int) {
    ActivityCompat.requestPermissions(this, permissionsArray, requestCode)
}

// Extension functions per FirebaseAuth
fun FirebaseAuth.getCurrentUserId() : String {
    return this.currentUser?.uid.toString()
}

@SuppressLint("HardwareIds")
fun FirebaseAuth.getAndroidId() : String {
    return Settings.Secure.getString(
        app.applicationContext.contentResolver,
        Settings.Secure.ANDROID_ID)
}

@SuppressLint("HardwareIds")
fun FirebaseAuth.getCurrentUserIdOrAndroidId() : String {
    return this.currentUser?.uid ?: this.getAndroidId()
}

fun FirebaseAuth.isUserAuthenticated(): Boolean {
    return this.currentUser.isAuthenticated()
}

suspend fun FirebaseAuth.createDeviceAccountIfNotExists() {
    ProfileNetwork.getDataSource().createUserIfNotExists(this.getAndroidId())
}

// Extension functions per FirebaseUser
fun FirebaseUser?.isAuthenticated(): Boolean {
    return (this != null)
}