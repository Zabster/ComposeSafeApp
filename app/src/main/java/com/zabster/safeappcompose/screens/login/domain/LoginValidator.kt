package com.zabster.safeappcompose.screens.login.domain

import com.zabster.safeappcompose.screens.login.models.ValidateResult
import kotlinx.coroutines.flow.Flow

interface LoginValidator {

    val validationState: Flow<ValidateResult>

    suspend fun validate(password: String)

    fun cachedValidate(newPassword: String): ValidateResult
}