package com.zabster.safeappcompose.screens.login.domain

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.zabster.safeappcompose.db.helper.secure.AppSecureUtils
import com.zabster.safeappcompose.db.prefs.PreferencesKeys
import javax.inject.Inject

class PasswordInteractorImpl @Inject constructor(
    private val secureHelper: AppSecureUtils,
    private val dataStore: DataStore<Preferences>,
) : PasswordInteractor {

    override suspend fun saveNewPassword(password: String) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.PasswordKey] = secureHelper.encrypt(password)
        }
    }
}