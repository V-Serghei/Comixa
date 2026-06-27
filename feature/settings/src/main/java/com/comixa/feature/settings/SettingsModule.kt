package com.comixa.feature.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.comixa.core.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsModule {

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        impl: UserPreferencesRepositoryImpl,
    ): UserPreferencesRepository

    companion object {
        @Provides
        @Singleton
        fun provideUserPreferencesDataStore(
            @ApplicationContext context: Context,
        ): DataStore<Preferences> =
            PreferenceDataStoreFactory.create(
                produceFile = { context.preferencesDataStoreFile("user_prefs") },
            )
    }
}
