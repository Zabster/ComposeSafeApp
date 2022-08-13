package com.zabster.safeappcompose

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.zabster.safeappcompose.db.prefs.PreferencesSettings
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application()

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = PreferencesSettings.PREFERENCES_NAME
)

/*
todo 1) обработчики ошибок
todo 2) оптимизация
...

 */