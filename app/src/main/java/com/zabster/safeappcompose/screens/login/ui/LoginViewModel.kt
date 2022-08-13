package com.zabster.safeappcompose.screens.login.ui

import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.zabster.safeappcompose.R
import com.zabster.safeappcompose.di.NavigationDispatcher
import com.zabster.safeappcompose.di.res.ResourceManager
import com.zabster.safeappcompose.screens.common.vm.BaseViewModel
import com.zabster.safeappcompose.screens.login.domain.BiometricInteractor
import com.zabster.safeappcompose.screens.login.domain.DialogCreatorInteractor
import com.zabster.safeappcompose.screens.login.domain.LoginValidator
import com.zabster.safeappcompose.screens.login.domain.PasswordInteractor
import com.zabster.safeappcompose.screens.login.models.BiometricLoginModel
import com.zabster.safeappcompose.screens.login.models.LoginEvents
import com.zabster.safeappcompose.screens.login.models.NewUserKey
import com.zabster.safeappcompose.screens.login.models.ValidateResult
import com.zabster.safeappcompose.screens.utils.Screen
import com.zabster.safeappcompose.ui.dialog.model.DialogKey
import com.zabster.safeappcompose.ui.dialog.model.DialogModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginValidator: LoginValidator,
    private val dialogCreator: DialogCreatorInteractor,
    private val passwordInteractor: PasswordInteractor,
    private val biometricInteractor: BiometricInteractor,
    private val resourceManager: ResourceManager,
    navigationDispatcher: NavigationDispatcher,
) : BaseViewModel(navigationDispatcher) {

    val dialogState = mutableStateOf<DialogModel>(DialogModel.HideDialog)
    val passwordValue = mutableStateOf("")

    var loadingState by mutableStateOf(false)
        private set
    var errorState by mutableStateOf<String?>(null)
        private set
    var messageState by mutableStateOf<String?>(null)
        private set
    var biometricShowState by mutableStateOf<BiometricLoginModel?>(null)
        private set
    var loginState by mutableStateOf(false)
        private set

    init {
        collectStates()
    }

    fun sendEvent(eventType: LoginEvents) {
        when (eventType) {
            is LoginEvents.Validate -> validate(eventType.password)
            is LoginEvents.DialogPositiveAction -> checkDialogPositiveResult(eventType.key)
            is LoginEvents.SimpleValidate -> simpleValidate(eventType.newPassword)
            is LoginEvents.BiometricAuth -> biometricAuth(eventType.cryptoObject)
            is LoginEvents.ErrorMessage -> showErrorMessage(eventType.message)
            is LoginEvents.CheckBiometricStart -> checkBiometricStart()
        }
    }

    private fun showErrorMessage(message: String) {
        hideLoading()
        messageState = message.ifBlank {
            resourceManager.getString(R.string.error_text_some_wrong)
        }
    }

    private fun biometricAuth(cryptoObject: BiometricPrompt.CryptoObject?) = viewModelScope.launch {
        cryptoObject?.let { biometricCryptoObject ->
            validate(
                biometricInteractor.decryptBiometricData(biometricCryptoObject).orEmpty()
            )
        }
    }

    private fun checkBiometricStart() = viewModelScope.launch {
        biometricInteractor.checkBiometricStart()
    }

    private fun checkDialogPositiveResult(key: DialogKey) {
        when (key) {
            is NewUserKey -> viewModelScope.launch {
                passwordInteractor.saveNewPassword(key.password)
                navigateToMainScreen()
            }
            else -> Unit
        }
    }

    private fun collectStates() {
        viewModelScope.launch {
            loginValidator.validationState.collectLatest(::checkResult)
        }
        viewModelScope.launch {
            biometricInteractor.biometricDialogState.collectLatest { available ->
                biometricShowState = if (available) {
                    biometricInteractor.createBiometricPromptInfo()
                } else null
            }
        }
    }

    private fun checkResult(result: ValidateResult) {
        when (result) {
            is ValidateResult.ValidateResultInputError -> {
                errorState = result.errorMessage
            }
            is ValidateResult.ValidateResultMessage -> {
                messageState = result.message
            }
            is ValidateResult.Empty -> Unit
            is ValidateResult.NewUser -> {
                hideLoading()
                errorState = null
                dialogState.value = dialogCreator.createNewUserDialog(passwordValue.value)
            }
            is ValidateResult.ValidationCorrect -> {
                hideLoading()
                errorState = null
                navigateToMainScreen()
            }
            is ValidateResult.SilentValidationCorrect -> {
                hideLoading()
                errorState = null
            }
        }
    }

    private fun showLoading() {
        loadingState = true
    }

    private fun hideLoading() {
        loadingState = false
    }

    private fun navigateToMainScreen() {
        loginState = true
        navigateSingleTo(Screen.MainScreen)
    }

    private fun validate(password: String) = viewModelScope.launch {
        showLoading()
        messageState = null
        loginValidator.validate(password)
    }

    private fun simpleValidate(newPassword: String) {
        messageState = null
        checkResult(loginValidator.cachedValidate(newPassword))
    }
}