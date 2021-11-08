package dev.maxsiomin.advancednumbergenerator.activities.main

import android.widget.Toast
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.maxsiomin.advancednumbergenerator.BuildConfig
import dev.maxsiomin.advancednumbergenerator.R
import dev.maxsiomin.advancednumbergenerator.base.BaseViewModel
import dev.maxsiomin.advancednumbergenerator.repository.updaterepository.Success
import dev.maxsiomin.advancednumbergenerator.repository.updaterepository.UpdateRepository
import dev.maxsiomin.advancednumbergenerator.util.UiActions
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(uiActions: UiActions) : BaseViewModel(uiActions) {

    /**
     * Checks for updates. If updates found calls [onUpdateFound]
     */
    fun checkForUpdates(onUpdateFound: (String) -> Unit) {
        val updateRepository = UpdateRepository(this) { result ->
            if (result is Success) {
                val currentVersionName = BuildConfig.VERSION_NAME
                if (currentVersionName != result.latestVersionName) {
                    onUpdateFound(result.latestVersionName)
                }
            } else {
                toast(R.string.last_version_checking_failed, Toast.LENGTH_LONG)
            }
        }

        updateRepository.searchForLatVersion()
    }
}
