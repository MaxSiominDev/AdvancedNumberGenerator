package dev.maxsiomin.advancednumbergenerator.util

import android.content.Context
import android.content.SharedPreferences
import android.os.IBinder
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.preference.PreferenceManager
import dev.maxsiomin.advancednumbergenerator.annotations.ToastDuration

/**
 * ViewModels and some others classes should use this interface in order to
 */
interface UiActions {

    /** Show toast */
    fun toast(@StringRes resId: Int, @ToastDuration length: Int)

    fun toast(message: String, @ToastDuration length: Int)

    /** Gets string from resources */
    fun getString(@StringRes resId: Int, vararg args: Any): String

    /** Hides keyboard */
    fun hideKeyboard(windowToken: IBinder)

    val sharedPrefs: SharedPreferences
}

class UiActionsImpl(private val context: Context) : UiActions {

    /**
     * Contains strings that were already loaded from resources
     */
    private val strings = mutableMapOf<@StringRes Int, String>()

    private val inputMethodManager: InputMethodManager
        get() = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    override fun toast(resId: Int, length: Int) =
        toast(getString(resId), length)

    override fun toast(message: String, length: Int) =
        Toast.makeText(context, message, length).show()

    /**
     * If string has no arguments and were already loaded returns it from [strings]
     */
    override fun getString(resId: Int, vararg args: Any): String {
        if (args.isNotEmpty())
            return context.getString(resId, *args)

        if (strings[resId] == null)
            strings[resId] = context.getString(resId)

        return strings[resId]!!
    }

    override fun hideKeyboard(windowToken: IBinder) {
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

    override val sharedPrefs: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)
}
