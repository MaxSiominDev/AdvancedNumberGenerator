package dev.maxsiomin.advancednumbergenerator

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import dev.maxsiomin.advancednumbergenerator.util.nextLong
import timber.log.Timber
import java.security.SecureRandom

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

    }
}
