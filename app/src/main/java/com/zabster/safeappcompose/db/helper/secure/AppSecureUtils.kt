package com.zabster.safeappcompose.db.helper.secure

import androidx.biometric.BiometricPrompt

interface AppSecureUtils {

    fun haveKeys(): Boolean

    fun encrypt(password: String): String

    fun decrypt(password: String): String

    fun createBiometricCryptoObject(): BiometricPrompt.CryptoObject
}