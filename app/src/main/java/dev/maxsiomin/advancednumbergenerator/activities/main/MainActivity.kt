package dev.maxsiomin.advancednumbergenerator.activities.main

import android.app.Activity
import android.app.Dialog
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
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import dev.maxsiomin.advancednumbergenerator.BuildConfig
import dev.maxsiomin.advancednumbergenerator.R
import dev.maxsiomin.advancednumbergenerator.activities.Updater
import dev.maxsiomin.advancednumbergenerator.activities.login.LoginActivity
import dev.maxsiomin.advancednumbergenerator.databinding.ActivityMainBinding
import dev.maxsiomin.advancednumbergenerator.fragments.contract.Navigator
import dev.maxsiomin.advancednumbergenerator.fragments.home.HomeFragment
import dev.maxsiomin.advancednumbergenerator.util.SHARED_DATA
import dev.maxsiomin.advancednumbergenerator.util.SharedData
import dev.maxsiomin.advancednumbergenerator.util.SharedDataImpl
import timber.log.Timber
import javax.inject.Inject

const val APK_LOCATION = "https://maxsiomin.dev/apps/advanced_number_generator/advanced_number_generator.apk"

typealias DialogBuilder = AlertDialog.Builder

/**
 * Used for everything apart Login, Signup and ResetPassword fragments
 * Implements [Updater] interface in order to check updates every app launch
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), Updater, Navigator, OnSharedPreferenceChangeListener {

    private lateinit var binding: ActivityMainBinding

    lateinit var sharedData: SharedData

    private lateinit var analytics: FirebaseAnalytics

    private val mViewModel by viewModels<MainViewModel>()

    @Inject
    lateinit var auth: FirebaseAuth

    private val keyTheme: String get() = mViewModel.getString(R.string.key_theme)
    private val themeFollowSystem = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.main_activity_fragment_container, HomeFragment.newInstance())
                .commit()
        }

        mViewModel.checkForUpdates { latestVersionName ->
            suggestUpdating(latestVersionName)
        }

        // Obtain the FirebaseAnalytics instance.
        analytics = Firebase.analytics

        @Suppress("SameParameterValue")
        sharedData = SharedDataImpl(savedInstanceState?.getBundle(SHARED_DATA))
        mViewModel.sharedPrefs.registerOnSharedPreferenceChangeListener(this)

        setupMode()

        checkLogin()
    }

    /**
     * Suggests user to update app via [UpdateDialog]
     */
    private fun suggestUpdating(latestVersionName: String) {
        UpdateDialog.newInstance(latestVersionName).show(supportFragmentManager)
    }

    /**
     * Opens direct uri to .apk in browser. .apk should be automatically downloaded
     */
    override fun update() {
        openInBrowser(APK_LOCATION)
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
            keyTheme -> setupMode()
        }
    }

    private fun setupMode() {
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
     * Logs out with [FirebaseAuth].
     * Starts [LoginActivity].
     * Finishes this activity
     */
    fun onLogout() {
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    class UpdateDialog : DialogFragment() {

        private val updater get() = requireActivity() as Updater

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

            val currentVersionName = BuildConfig.VERSION_NAME
            val latestVersionName = requireArguments().getString(LATEST_VERSION_NAME)

            val dialog = DialogBuilder(requireContext())
                .setMessage(getString(R.string.update_app, currentVersionName, latestVersionName))
                .setNegativeButton(R.string.no_thanks) { _, _ -> }
                .setPositiveButton(R.string.update) { _, _ ->
                    updater.update()
                }
                .create()

            dialog.setCanceledOnTouchOutside(false)

            return dialog
        }

        fun show(manager: FragmentManager) {
            show(manager, TAG)
        }

        companion object {

            const val TAG = "UpdateDialog"

            /** Key for args */
            private const val LATEST_VERSION_NAME = "latestVersion"

            /**
             * Puts [latestVersionName] to args
             */
            @JvmStatic
            fun newInstance(latestVersionName: String): UpdateDialog {
                val dialogFragment = UpdateDialog()

                val args = Bundle()
                args.putString(LATEST_VERSION_NAME, latestVersionName)
                dialogFragment.arguments = args

                return dialogFragment
            }
        }
    }
}

/**
 * Opens [url] via browser
 * If browser not found, toasts [R.string.smth_went_wrong]
 */
fun Activity.openInBrowser(@Suppress("SameParameterValue") url: String) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)))
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, R.string.smth_went_wrong, Toast.LENGTH_LONG).show()
    }
}
