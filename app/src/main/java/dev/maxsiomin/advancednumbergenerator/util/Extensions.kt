package dev.maxsiomin.advancednumbergenerator.util

import android.content.Context
import android.content.SharedPreferences
import android.text.Editable
import android.text.SpannableStringBuilder
import android.view.MenuItem
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout

fun String?.safeToLong(): Long? {
    return try {
        this?.toLong()
    } catch (e: NumberFormatException) {
        null
    }
}

fun String?.notNull(): String = this ?: ""

fun Long.toEditable(): Editable = SpannableStringBuilder(this.toString())

/**
 * @throws NumberFormatException
 */
fun Editable.toLong(): Long = this.toString().toLong()

/**
 * It works like [kotlin.random.Random] but I need this method for usage of [java.security.SecureRandom]
 */
fun java.util.Random.nextLong(min: Long, max: Long): Long =
    longs(1, min, max + 1).reduce(0) { _, b -> b }

fun SharedPreferences.getBoolean(@StringRes resId: Int, defValue: Boolean, context: Context): Boolean =
    getBoolean(context.getString(resId), defValue)

fun SharedPreferences.getNullableString(@StringRes resId: Int, context: Context): String? {
    val key = context.getString(resId)
    if (contains(key))
        return getString(key, "")

    return null
}

fun String.toEditable() = SpannableStringBuilder(this)

/**
 * setError(null)
 */
fun TextInputLayout.clearError() {
    error = null
}

operator fun LongRange.contains(value: String?): Boolean {
    return try {
        value!!.toLong() in this
    } catch (e: NumberFormatException) {
        false
    } catch (e: NullPointerException) {
        false
    }
}

val MenuItem.id get() = itemId
