package com.zabster.safeappcompose.screens.login.domain

import com.zabster.safeappcompose.ui.dialog.model.DialogModel

interface DialogCreatorInteractor {

    fun createNewUserDialog(password: String): DialogModel
}