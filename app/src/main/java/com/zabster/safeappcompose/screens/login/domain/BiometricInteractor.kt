package com.zabster.safeappcompose.screens.login.domain

import android.content.Context
import android.util.Base64
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.zabster.safeappcompose.R
import com.zabster.safeappcompose.db.helper.secure.AppSecureUtils
import com.zabster.safeappcompose.db.prefs.PreferencesKeys
import com.zabster.safeappcompose.di.res.ResourceManager
import com.zabster.safeappcompose.screens.login.models.BiometricLoginModel
import com.zabster.safeappcompose.screens.utils.orZero
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class BiometricInteractor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appSecureUtils: AppSecureUtils,
    private val dataStore: DataStore<Preferences>,
    private val resourceManager: ResourceManager
) {

    private val _biometricDialogState = MutableStateFlow(false)
    val biometricDialogState: Flow<Boolean> = _biometricDialogState

    suspend fun decryptBiometricData(cryptoObject: BiometricPrompt.CryptoObject?): String? {
        val data = dataStore.data.first()
        val pass = data[PreferencesKeys.PasswordKey]

        return cryptoObject?.cipher?.let { cipher ->
            val encryptedData = Base64.decode(pass, Base64.NO_WRAP)
            cipher.doFinal(encryptedData).toString(Charsets.UTF_8)
        }
    }

    suspend fun checkBiometricStart() {
        _biometricDialogState.emitAll(
            dataStore.data.map { prefs -> prefs[PreferencesKeys.PasswordKey] }
                .map { password ->
                    !password.isNullOrBlank()
                }
                .map { havePassword ->
                    havePassword && checkAvailableBiometrics() != null
                }
        )
    }

    fun createBiometricPromptInfo(): BiometricLoginModel =
        BiometricLoginModel(
            info = createInfo(),
            cryptoObject = appSecureUtils.createBiometricCryptoObject()
        )

    private fun createInfo(): BiometricPrompt.PromptInfo {
        val authenticators = checkAvailableBiometrics().orZero()
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle(resourceManager.getString(R.string.biometric_dialog_title))
            .setSubtitle(resourceManager.getString(R.string.biometric_dialog_subtitle))
            .setAllowedAuthenticators(authenticators)
            .takeNegativeIf(authenticators)
            .build()
    }

    private fun checkAvailableBiometrics(): Int? {
        val biometricManager = BiometricManager.from(context)
        val firstLevel = BiometricManager.Authenticators.BIOMETRIC_STRONG
        val nextLevel = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        return when (BiometricManager.BIOMETRIC_SUCCESS) {
            biometricManager.canAuthenticate(nextLevel) -> {
                nextLevel
            }
            biometricManager.canAuthenticate(firstLevel) -> {
                firstLevel
            }
            else -> null
        }
    }

    private fun BiometricPrompt.PromptInfo.Builder.takeNegativeIf(authenticators: Int) = apply {
        if (authenticators == BiometricManager.Authenticators.BIOMETRIC_STRONG) {
            setNegativeButtonText(resourceManager.getString(R.string.biometric_dialog_negative))
        }
    }
}