package com.zabster.safeappcompose.screens.login.models

import androidx.biometric.BiometricPrompt
import com.zabster.safeappcompose.ui.dialog.model.DialogKey

sealed class LoginEvents {

    object CheckBiometricStart : LoginEvents()

    data class Validate(val password: String): LoginEvents()

    data class SimpleValidate(val newPassword: String): LoginEvents()

    data class DialogPositiveAction(val key: DialogKey): LoginEvents()

    data class BiometricAuth(val cryptoObject: BiometricPrompt.CryptoObject?): LoginEvents()

    data class ErrorMessage(val message: String = ""): LoginEvents()
}
