package com.zabster.safeappcompose.screens.main.models

import com.zabster.safeappcompose.ui.dialog.model.DialogKey

data class PasswordDialogKey(
    val password: String,
    val copyText: String,
): DialogKey
