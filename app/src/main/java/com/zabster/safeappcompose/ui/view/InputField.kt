package com.zabster.safeappcompose.ui.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

private typealias ComposeView = @Composable () -> Unit

@Composable
fun InputField(
    modifier: Modifier = Modifier,
    modifierError: Modifier = Modifier,
    label: String = "",
    isError: Boolean = false,
    singleLine: Boolean = true,
    errorText: String = "",
    value: String,
    onValueChange: (String) -> Unit,
    onDoneAction: (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default,
    enabled: Boolean = true,
    endIcon: @Composable (() -> Unit)? = null
) {
    val focusManager = LocalFocusManager.current
    val isPassword = keyboardType == KeyboardType.Password
    val passwordVisible = rememberSaveable { mutableStateOf(!isPassword) }
    Column(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            label = { Text(text = label) },
            isError = isError,
            singleLine = singleLine,
            value = value,
            onValueChange = onValueChange,
            keyboardActions = KeyboardActions(
                onDone = {
                    onDoneAction?.invoke()
                    focusManager.clearFocus()
                }
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            trailingIcon = takeIf { isPassword }?.let { passwordIcon(passwordVisible, enabled) }
                ?: endIcon,
            visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation()
        )
        Box(modifier = Modifier.heightIn(min = 26.dp)) {
            this@Column.AnimatedVisibility(visible = isError) {
                Text(
                    modifier = modifierError.fillMaxWidth(),
                    text = errorText,
                    style = MaterialTheme.typography.overline.copy(color = MaterialTheme.colors.error)
                )
            }
        }
    }
}

@Composable
private fun passwordIcon(passwordVisible: MutableState<Boolean>, enabled: Boolean): ComposeView = {
    IconButton(
        enabled = enabled,
        onClick = {
            passwordVisible.value = !passwordVisible.value
        }
    ) {
        Icon(
            imageVector = if (passwordVisible.value) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
            contentDescription = ""
        )
    }
}
