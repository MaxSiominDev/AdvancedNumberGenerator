package dev.maxsiomin.advancednumbergenerator.fragments.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceClickListener
import androidx.preference.PreferenceFragmentCompat
import dev.maxsiomin.advancednumbergenerator.BuildConfig
import dev.maxsiomin.advancednumbergenerator.base.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import dev.maxsiomin.advancednumbergenerator.R
import dev.maxsiomin.advancednumbergenerator.activities.main.APK_LOCATION
import dev.maxsiomin.advancednumbergenerator.activities.main.MainActivity
import dev.maxsiomin.advancednumbergenerator.activities.main.openInBrowser
import javax.inject.Inject

private const val DEVELOPER_EMAIL = "max@maxsiomin.dev"
private const val DEVELOPER_WEBSITE = "https://maxsiomin.dev/"

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    private val mViewModel by viewModels<BaseViewModel>()

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as AppCompatActivity).supportActionBar!!.title = getString(R.string.settings)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        findPreference(R.string.key_verify_email).apply {
            // If email already verified email verification shouldn't be sent
            if (!auth.currentUser!!.isEmailVerified) {
                isVisible = true
                setOnClickListener {
                    auth.currentUser!!.sendEmailVerification().addOnCompleteListener { task ->
                        mViewModel.toast(
                            if (task.isSuccessful) R.string.check_email else R.string.unable_to_send_email_verification,
                            Toast.LENGTH_LONG
                        )
                    }
                }
            }
        }

        findPreference(R.string.key_log_out).setOnClickListener {
            auth.signOut()
            (activity as MainActivity).onLogout()
        }

        findPreference(R.string.key_help_and_feedback).setOnClickListener { sendEmail() }

        findPreference(R.string.key_share_this_app).setOnClickListener { shareThisApp() }

        findPreference(R.string.key_more_apps).setOnClickListener { moreApps() }

        findPreference(R.string.key_app_version).summary = BuildConfig.VERSION_NAME
    }

    private fun findPreference(@StringRes key: Int): Preference =
        super.findPreference(mViewModel.getString(key))!!

    /**
     * Open mail client with my email
     */
    private fun sendEmail() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("mailto:$DEVELOPER_EMAIL")))
        } catch (e: ActivityNotFoundException) {
            mViewModel.toast(R.string.mail_client_not_found, Toast.LENGTH_SHORT)
        }
    }

    /**
     * Share direct link to .apk
     */
    private fun shareThisApp() {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, APK_LOCATION)
            type = "text/plain"
        }

        try {
            startActivity(Intent.createChooser(intent, null))
        } catch (e: ActivityNotFoundException) {
            mViewModel.toast(R.string.smth_went_wrong, Toast.LENGTH_LONG)
        }
    }

    /**
     * My website
     */
    private fun moreApps() {
        requireActivity().openInBrowser(DEVELOPER_WEBSITE)
    }

    private fun Preference.setOnClickListener(onClick: () -> Unit) {
        this.onPreferenceClickListener = OnPreferenceClickListener {
            onClick()
            true
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = SettingsFragment()
    }
}
