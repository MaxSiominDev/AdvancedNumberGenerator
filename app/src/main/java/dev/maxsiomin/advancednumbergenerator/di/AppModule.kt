package dev.maxsiomin.advancednumbergenerator.di

import android.content.Context
import dev.maxsiomin.advancednumbergenerator.util.UiActions
import dev.maxsiomin.advancednumbergenerator.util.UiActionsImpl
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.security.SecureRandom
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideUiActions(@ApplicationContext context: Context): UiActions = UiActionsImpl(context)

    @Singleton
    @Provides
    fun provideRandom(): java.util.Random = SecureRandom.getInstanceStrong()

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
}
