package com.zabster.safeappcompose.screens.login.models

import androidx.biometric.BiometricPrompt

data class BiometricLoginModel(
    val info: BiometricPrompt.PromptInfo,
    val cryptoObject: BiometricPrompt.CryptoObject
)
