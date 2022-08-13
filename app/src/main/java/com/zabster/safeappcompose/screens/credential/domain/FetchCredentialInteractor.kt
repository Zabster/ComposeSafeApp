package com.zabster.safeappcompose.screens.credential.domain

import com.zabster.safeappcompose.db.entitys.CredentialEntity
import com.zabster.safeappcompose.db.helper.db.AppDatabaseHelper
import com.zabster.safeappcompose.db.helper.secure.AppSecureUtils
import com.zabster.safeappcompose.screens.common.domain.BaseDBInteractor
import com.zabster.safeappcompose.screens.common.models.CategoryModel
import com.zabster.safeappcompose.screens.common.models.CredentialModel
import com.zabster.safeappcompose.screens.common.transforms.mapCategory
import com.zabster.safeappcompose.screens.common.transforms.mapCredential
import com.zabster.safeappcompose.screens.credential.models.CreateOrUpdateCredentialExceptions
import javax.inject.Inject

class FetchCredentialInteractor @Inject constructor(
    appDatabaseHelper: AppDatabaseHelper,
    private val appSecureUtils: AppSecureUtils
) : BaseDBInteractor(appDatabaseHelper) {

    suspend fun getCredential(id: Long): Pair<CredentialModel, CategoryModel> =
        credentialDao.getCredential(id)?.let { credentialEntity ->
            categoryDao.getCategoryById(credentialEntity.categoryId)?.let { categoryEntity ->
                mapCredentialWithPassword(credentialEntity) to mapCategory(categoryEntity)
            } ?: throw CreateOrUpdateCredentialExceptions.CategoryNotFound(credentialEntity.categoryId)
        } ?: throw CreateOrUpdateCredentialExceptions.CredentialNotFound(id)

    private fun mapCredentialWithPassword(credentialEntity: CredentialEntity) =
        mapCredential(credentialEntity).copy(passwordHash = appSecureUtils.decrypt(credentialEntity.hashPass))

}