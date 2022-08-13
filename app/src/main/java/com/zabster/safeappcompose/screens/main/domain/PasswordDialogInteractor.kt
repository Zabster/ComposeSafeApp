package com.zabster.safeappcompose.screens.main.domain

import com.zabster.safeappcompose.R
import com.zabster.safeappcompose.db.helper.secure.AppSecureUtils
import com.zabster.safeappcompose.di.res.ResourceManager
import com.zabster.safeappcompose.screens.main.models.PasswordDialogKey
import com.zabster.safeappcompose.ui.dialog.model.DialogModel
import javax.inject.Inject

class PasswordDialogInteractor @Inject constructor(
    private val resourceManager: ResourceManager,
    private val appSecureUtils: AppSecureUtils
) {

    fun showPasswordDialog(hashPas: String): DialogModel {
        val password = appSecureUtils.decrypt(hashPas)
        return DialogModel.DialogDataModel(
            key = PasswordDialogKey(password, resourceManager.getString(R.string.dialog_button_copy)),
            title = resourceManager.getString(R.string.dialog_password_success_title),
            message = password,
            positiveButton = resourceManager.getString(R.string.dialog_button_copy),
            negativeButton = resourceManager.getString(R.string.dialog_button_negative),
            isSelectable = true
        )
    }
}