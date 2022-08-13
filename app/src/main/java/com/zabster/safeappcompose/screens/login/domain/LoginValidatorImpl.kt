package com.zabster.safeappcompose.screens.login.domain

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.zabster.safeappcompose.R
import com.zabster.safeappcompose.db.helper.secure.AppSecureUtils
import com.zabster.safeappcompose.db.prefs.PreferencesKeys
import com.zabster.safeappcompose.di.res.ResourceManager
import com.zabster.safeappcompose.screens.login.models.ValidateResult
import com.zabster.safeappcompose.screens.utils.runIfNotEmpty
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class LoginValidatorImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val secureHelper: AppSecureUtils,
    private val resourceManager: ResourceManager
) : LoginValidator {

    private val _validationState = MutableSharedFlow<ValidateResult>(replay = 0)
    override val validationState: Flow<ValidateResult> = _validationState

    private var cachedLastPasswordWord: String? = null

    override suspend fun validate(password: String) {
        cachedLastPasswordWord = password
        _validationState.emitAll(
            dataStore.data.map { pref -> pref[PreferencesKeys.PasswordKey] }
                .map { passwordFromStore -> passwordFromStore.runIfNotEmpty(secureHelper::decrypt) }
                .map { passwordFromStore -> check(password, passwordFromStore) }
                .catch { emit(ValidateResult.ValidateResultMessage(resourceManager.getString(R.string.error_text_some_wrong))) }
        )
    }

    override fun cachedValidate(newPassword: String): ValidateResult {
        if (cachedLastPasswordWord == null) return ValidateResult.Empty
        return checkEmpty(newPassword).checkLength(newPassword).asSilent()
    }

    private fun check(password: String, oldPassword: String?) =
        checkEmpty(password).checkLength(password)
            .checkValid(password, oldPassword)
            .asNew(!secureHelper.haveKeys() || oldPassword.isNullOrBlank())

    private fun checkEmpty(password: String) =
        if (password.isBlank()) ValidateResult.ValidateResultInputError(
            errorMessage = resourceManager.getString(R.string.login_input_empty_error_text)
        ) else ValidateResult.ValidationCorrect(password)

    private fun ValidateResult.checkLength(password: String) =
        when {
            this is ValidateResult.ValidateResultInputError -> this
            (password.length <= PASSWORD_MIN_LENGTH) -> ValidateResult.ValidateResultInputError(
                errorMessage = resourceManager.getString(
                    R.string.login_input_small_error_text,
                    PASSWORD_MIN_LENGTH
                )
            )
            else -> ValidateResult.ValidationCorrect(password)
        }

    private fun ValidateResult.checkValid(new: String, old: String?) =
        if (this is ValidateResult.ValidationCorrect && !old.isNullOrBlank()) {
            if (new == old) {
                ValidateResult.ValidationCorrect(new)
            } else {
                ValidateResult.ValidateResultMessage(resourceManager.getString(R.string.login_input_incorrect_error_text))
            }
        } else this

    private fun ValidateResult.asNew(isNew: Boolean) =
        if (this is ValidateResult.ValidationCorrect && isNew) ValidateResult.NewUser
        else this

    private fun ValidateResult.asSilent(): ValidateResult =
        if (this is ValidateResult.ValidationCorrect) ValidateResult.SilentValidationCorrect
        else this

    companion object {
        private const val PASSWORD_MIN_LENGTH = 5
    }
}


