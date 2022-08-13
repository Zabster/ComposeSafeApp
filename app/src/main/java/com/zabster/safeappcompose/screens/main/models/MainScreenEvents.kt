package com.zabster.safeappcompose.screens.main.models

import com.zabster.safeappcompose.screens.common.models.CategoryModel
import com.zabster.safeappcompose.screens.common.models.CredentialModel

sealed class MainScreenEvents {

    object NavigateToCreateCredential: MainScreenEvents()

    object NavigateToSettings: MainScreenEvents()


    data class LoadCredentialByCategory(val category: CategoryModel): MainScreenEvents()

    data class OnCredentialUpdate(val model: CredentialModel): MainScreenEvents()

    data class OnCredentialRemove(val model: CredentialModel): MainScreenEvents()

    data class RemoveCategory(val category: CategoryModel): MainScreenEvents()

    data class ShowPassword(val model: CredentialModel) : MainScreenEvents()

    data class ShowMessage(val message: String) : MainScreenEvents()

}