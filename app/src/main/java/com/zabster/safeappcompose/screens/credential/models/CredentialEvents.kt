package com.zabster.safeappcompose.screens.credential.models

import com.zabster.safeappcompose.screens.common.models.CategoryModel

sealed class CredentialEvents {

    object ShowAllCategories: CredentialEvents()

    object Save: CredentialEvents()

    object Validate: CredentialEvents()

    data class RequestScreen(val id: Long?): CredentialEvents()

    data class ChooseCategory(val categoryModel: CategoryModel): CredentialEvents()

    data class ShowCategoriesByName(val name: String): CredentialEvents()

}
