package com.zabster.safeappcompose.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.window.PopupProperties
import com.zabster.safeappcompose.ui.view.models.TitleModel

@Composable
fun <MODEL : TitleModel> SelectableInputField(
    value: TextFieldValue,
    label: String,
    onValueChange: (TextFieldValue) -> Unit,
    inputDropDownAction: () -> Unit,
    dropDownItems: List<MODEL>,
    dropDownAction: (MODEL) -> Unit,
    expanded: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    onDoneAction: (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default,
    singleLine: Boolean = true,
    enabled: Boolean = true
) {
    val focusManager = LocalFocusManager.current
    Column(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = label)
            },
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            singleLine = singleLine,
            trailingIcon = {
                Icon(
                    modifier = Modifier.clickable(
                        enabled = enabled,
                        onClick = inputDropDownAction::invoke
                    ),
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "dropdown list"
                )
            },
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
        )
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = {
                expanded.value = false
            },
            properties = PopupProperties()
        ) {
            dropDownItems.forEach { model ->
                DropdownMenuItem(onClick = {
                    dropDownAction.invoke(model)
                }) {
                    Text(text = model.title)
                }
            }
        }
    }
}