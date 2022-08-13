package com.zabster.safeappcompose.screens.login.domain

import com.zabster.safeappcompose.R
import com.zabster.safeappcompose.di.res.ResourceManager
import com.zabster.safeappcompose.screens.login.models.NewUserKey
import com.zabster.safeappcompose.ui.dialog.model.DialogModel
import javax.inject.Inject

class DialogCreatorInteractorImpl @Inject constructor(
    private val resourceManager: ResourceManager
) : DialogCreatorInteractor {

    override fun createNewUserDialog(password: String): DialogModel =
        DialogModel.DialogDataModel(
            key = NewUserKey(password),
            title = resourceManager.getString(R.string.dialog_set_password_title_text),
            message = resourceManager.getString(R.string.dialog_set_password_message_text),
            positiveButton = resourceManager.getString(R.string.dialog_set_password_positive_action_text),
            negativeButton = resourceManager.getString(R.string.dialog_set_password_negative_action_text)
        )
}