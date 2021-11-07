package dev.maxsiomin.advancednumbergenerator.base

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import dev.maxsiomin.advancednumbergenerator.fragments.contract.Navigator
import dev.maxsiomin.advancednumbergenerator.util.SharedData
import dev.maxsiomin.advancednumbergenerator.util.SharedDataImpl
import dagger.hilt.android.AndroidEntryPoint
import dev.maxsiomin.advancednumbergenerator.R
import dev.maxsiomin.advancednumbergenerator.activities.login.LoginActivity
import timber.log.Timber
import javax.inject.Inject

const val APK_LOCATION = "https://maxsiomin.dev/apps/advanced_number_generator/advanced_number_generator.apk"

typealias DialogBuilder = AlertDialog.Builder

@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity(), Navigator, OnSharedPreferenceChangeListener {

    lateinit var sharedData: SharedData

    private lateinit var analytics: FirebaseAnalytics

    @Suppress("PropertyName")
    protected open val SHARED_DATA = "sharedData"

    protected open val mViewModel by viewModels<BaseViewModel>()

    @Inject
    lateinit var auth: FirebaseAuth

    protected open val keyTheme: String get() = mViewModel.getString(R.string.key_theme)
    protected open val themeFollowSystem: String = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("onCreate called")
        super.onCreate(savedInstanceState)

        // Obtain the FirebaseAnalytics instance.
        analytics = Firebase.analytics

        sharedData = SharedDataImpl(savedInstanceState?.getBundle(SHARED_DATA))
        mViewModel.sharedPrefs.registerOnSharedPreferenceChangeListener(this)

        setMode()

        checkLogin()
    }

    /**
     * If user isn't logged in calls [goToLoginScreen]
     */
    private fun checkLogin() {
        auth.currentUser?.reload()?.addOnFailureListener {
            goToLoginScreen()
        } ?: goToLoginScreen()
    }

    /**
     * Starts [LoginActivity] and finishes current
     */
    private fun goToLoginScreen() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        Timber.d("onDestroy called")
        mViewModel.sharedPrefs.unregisterOnSharedPreferenceChangeListener(this)
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        Timber.d("onSaveInstanceState called")
        outState.putBundle(SHARED_DATA, sharedData.sharedBundle)
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        goBack()
    }

    override fun goBack() = supportFragmentManager.popBackStack()

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Timber.d("onSharedPreferenceChanged called")

        when (key) {
            keyTheme -> setMode()
        }
    }

    protected open fun setMode() {
        val theme = mViewModel.sharedPrefs.getString(keyTheme, themeFollowSystem) ?: themeFollowSystem
        Timber.d("Theme=$theme")
        AppCompatDelegate.setDefaultNightMode(theme.toInt())
    }

     override fun launchFragment(container: Int, fragment: Fragment, addToBackStack: Boolean) {
         with (supportFragmentManager.beginTransaction()) {
             replace(R.id.main_activity_fragment_container, fragment)

             if (addToBackStack)
                 addToBackStack(null)

             commit()
         }
    }

    /**
     * Opens [url] via browser
     * If browser not found, toasts [R.string.smth_went_wrong]
     */
    fun openInBrowser(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)))
        } catch (e: ActivityNotFoundException) {
            mViewModel.toast(R.string.smth_went_wrong, Toast.LENGTH_LONG)
        }
    }

    /**
     * Logs out with [FirebaseAuth].
     * Starts [LoginActivity].
     * Finishes this activity
     */
    fun onLogout() {
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
