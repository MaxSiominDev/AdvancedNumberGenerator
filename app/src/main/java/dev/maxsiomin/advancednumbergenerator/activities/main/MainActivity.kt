package dev.maxsiomin.advancednumbergenerator.activities.main

import android.app.Dialog
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import dev.maxsiomin.advancednumbergenerator.BuildConfig
import dev.maxsiomin.advancednumbergenerator.R
import dev.maxsiomin.advancednumbergenerator.activities.Updater
import dev.maxsiomin.advancednumbergenerator.base.APK_LOCATION
import dev.maxsiomin.advancednumbergenerator.base.BaseActivity
import dev.maxsiomin.advancednumbergenerator.base.DialogBuilder
import dev.maxsiomin.advancednumbergenerator.databinding.ActivityMainBinding
import dev.maxsiomin.advancednumbergenerator.fragments.home.HomeFragment

/**
 * Used for everything apart Login, Signup and ResetPassword fragments
 * Implements [Updater] interface in order to check updates every app launch
 */
class MainActivity : BaseActivity(), Updater {

    private lateinit var binding: ActivityMainBinding

    override val mViewModel by viewModels<MainViewModel>()

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
    }

    /**
     * Suggests user to update app via [UpdateDialog]
     */
    private fun suggestUpdating(latestVersionName: String) {
        val dialog = UpdateDialog.newInstance(latestVersionName)
        dialog.show(supportFragmentManager)
    }

    /**
     * Opens direct uri to .apk in browser. .apk should be automatically downloaded
     */
    override fun update() {
        openInBrowser(APK_LOCATION)
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
