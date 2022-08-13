package com.zabster.safeappcompose.screens.login.models

sealed class ValidateResult {

    data class ValidateResultInputError(val errorMessage: String) : ValidateResult()

    data class ValidateResultMessage(val message: String) : ValidateResult()

    object NewUser: ValidateResult()

    data class ValidationCorrect(val message: String): ValidateResult()

    object SilentValidationCorrect: ValidateResult()

    object Empty: ValidateResult()
}