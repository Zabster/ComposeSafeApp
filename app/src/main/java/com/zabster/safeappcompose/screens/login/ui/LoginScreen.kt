package com.zabster.safeappcompose.screens.login.ui

import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.zabster.safeappcompose.R
import com.zabster.safeappcompose.screens.login.models.LoginEvents
import com.zabster.safeappcompose.screens.utils.LifecycleEvent
import com.zabster.safeappcompose.screens.utils.imeInsets
import com.zabster.safeappcompose.ui.dialog.view.ShowSimpleDialog
import com.zabster.safeappcompose.ui.view.InputField

private var biometricPrompt: BiometricPrompt? = null

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()
    ObserveStates(viewModel, scaffoldState)
    InitializeBiometric(viewModel)

    Scaffold(
        scaffoldState = scaffoldState
    ) { paddingValue ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue)
                .imeInsets(rememberScrollState())
        ) {
            LoginShapeBox(modifier = Modifier.align(Alignment.Center), viewModel)
        }
    }

}

@Composable
private fun InitializeBiometric(viewModel: LoginViewModel) {
    val context = LocalContext.current
    LifecycleEvent { event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                val biometricExecutor = ContextCompat.getMainExecutor(context)
                if (biometricPrompt == null) {
                    biometricPrompt = BiometricPrompt(
                        context as FragmentActivity,
                        biometricExecutor,
                        biometricCallback(viewModel)
                    )
                }
                viewModel.sendEvent(LoginEvents.CheckBiometricStart)
            }
            else -> Unit
        }
    }
    LaunchedEffect(key1 = viewModel.biometricShowState) {
        val biometricState = viewModel.biometricShowState
        if (biometricState != null) {
            biometricPrompt?.authenticate(biometricState.info, biometricState.cryptoObject)
        }
    }
    DisposableEffect(key1 = viewModel.loginState) {
        onDispose {
            biometricPrompt?.cancelAuthentication()
            biometricPrompt = null
        }
    }
}

@Composable
private fun LoginShapeBox(modifier: Modifier, viewModel: LoginViewModel) {
    val focusManager = LocalFocusManager.current
    val (passValue, passChange) = remember { viewModel.passwordValue }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.Center) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 8.dp),
                text = stringResource(id = R.string.login_title_text)
            )
            InputField(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 16.dp)
                    .fillMaxWidth(),
                modifierError = Modifier.padding(horizontal = 16.dp),
                label = stringResource(id = R.string.login_edit_text_hint),
                isError = !viewModel.errorState.isNullOrBlank(),
                errorText = viewModel.errorState.orEmpty(),
                singleLine = true,
                value = passValue,
                onValueChange = { newText ->
                    passChange(newText)
                    viewModel.sendEvent(LoginEvents.SimpleValidate(newText))
                },
                onDoneAction = {
                    viewModel.sendEvent(LoginEvents.Validate(passValue))
                },
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
            Button(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(horizontal = 8.dp),
                enabled = !viewModel.loadingState,
                onClick = {
                    viewModel.sendEvent(LoginEvents.Validate(passValue))
                    focusManager.clearFocus()
                }
            ) {
                Text(text = stringResource(id = R.string.login_button_text))
            }
        }
    }
}

@Composable
private fun ObserveStates(viewModel: LoginViewModel, scaffoldState: ScaffoldState) {
    val dialogState = remember { viewModel.dialogState }
    ShowSimpleDialog(
        openableState = dialogState,
        onPositiveClick = { key ->
            viewModel.sendEvent(LoginEvents.DialogPositiveAction(key))
        }
    )
    viewModel.messageState?.let { msg ->
        LaunchedEffect(key1 = scaffoldState.snackbarHostState) {
            scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
            scaffoldState.snackbarHostState.showSnackbar(msg)
        }
    }
}

private fun biometricCallback(viewModel: LoginViewModel) =
    object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            viewModel.sendEvent(LoginEvents.ErrorMessage(errString.toString()))
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            viewModel.sendEvent(LoginEvents.BiometricAuth(result.cryptoObject))
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            viewModel.sendEvent(LoginEvents.ErrorMessage())
        }
    }