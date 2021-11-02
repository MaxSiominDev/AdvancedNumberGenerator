package dev.maxsiomin.advancednumbergenerator.util

import android.os.Bundle
import dev.maxsiomin.advancednumbergenerator.activities.login.LoginActivity
import dev.maxsiomin.advancednumbergenerator.base.BaseActivity
import dev.maxsiomin.advancednumbergenerator.base.BaseFragment

typealias LongSharedDataKey = SharedDataKey<Long>
typealias StringSharedDataKey = SharedDataKey<String>

val BaseFragment.sharedData: SharedData
    get() {
        return if (usedByBaseActivity)
            (requireActivity() as BaseActivity).sharedData
        else
            (requireActivity() as LoginActivity).sharedData
    }

interface SharedData {

    val sharedBundle: Bundle

    fun getSharedLong(key: LongSharedDataKey): Long?

    fun putSharedLong(key: LongSharedDataKey, value: Long?)

    fun getSharedString(key: StringSharedDataKey): String?

    fun putSharedString(key: StringSharedDataKey, value: String?)
}

class SharedDataImpl(bundle: Bundle?) : SharedData {

    override val sharedBundle: Bundle = bundle ?: Bundle()

    override fun getSharedLong(key: LongSharedDataKey): Long? {
        if (sharedBundle.containsKey(key.value))
            return sharedBundle.getLong(key.value)
        return null
    }

    override fun putSharedLong(key: LongSharedDataKey, value: Long?) {
        if (value == null)
            sharedBundle.remove(key.value)
        else
            sharedBundle.putLong(key.value, value)
    }

    override fun getSharedString(key: StringSharedDataKey): String? =
        sharedBundle.getString(key.value)

    override fun putSharedString(key: StringSharedDataKey, value: String?) {
        if (value == null)
            sharedBundle.remove(key.value)
        else
            sharedBundle.putString(key.value, value)
    }
}

@Suppress("unused")
data class SharedDataKey <T> (
    val value: String,
)

object SharedDataKeys {

    val MIN_NUMBER = LongSharedDataKey("minNumber")
    val MAX_NUMBER = LongSharedDataKey("maxNumber")
    val GENERATED_NUMBER = LongSharedDataKey("generatedNumber")
    val EMAIL = StringSharedDataKey("email")
    val PASSWORD = StringSharedDataKey("password")
}
