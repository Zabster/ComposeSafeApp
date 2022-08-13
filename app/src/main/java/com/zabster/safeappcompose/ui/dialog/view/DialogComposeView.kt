package com.zabster.safeappcompose.ui.dialog.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.zabster.safeappcompose.ui.dialog.model.DialogKey
import com.zabster.safeappcompose.ui.dialog.model.DialogModel

typealias ButtonClick = (DialogKey) -> Unit

@Composable
fun ShowSimpleDialog(
    openableState: MutableState<DialogModel>,
    onPositiveClick: ButtonClick? = null,
    onNegativeClick: ButtonClick? = null
) {
    val dialogModel = (openableState.value as? DialogModel.DialogDataModel) ?: return
    AlertDialog(
        onDismissRequest = {
            openableState.value = DialogModel.HideDialog
        },
        title = { Text(text = dialogModel.title) },
        text = {
            if (dialogModel.isSelectable) {
                SelectionContainer { Text(text = dialogModel.message) }
            } else Text(text = dialogModel.message)
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                dialogModel.negativeButton?.let { negativeButton ->
                    TextButton(onClick = {
                        openableState.value = DialogModel.HideDialog
                        onNegativeClick?.invoke(dialogModel.key)
                    }) {
                        Text(text = negativeButton)
                    }
                }
                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                dialogModel.positiveButton?.let { positiveButton ->
                    Button(onClick = {
                        openableState.value = DialogModel.HideDialog
                        onPositiveClick?.invoke(dialogModel.key)
                    }) {
                        Text(text = positiveButton)
                    }
                }
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = dialogModel.isCancellable,
            dismissOnClickOutside = dialogModel.isCancellable
        )
    )
}