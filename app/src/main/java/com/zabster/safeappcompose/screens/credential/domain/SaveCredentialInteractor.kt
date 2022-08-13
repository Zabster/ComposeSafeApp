package com.zabster.safeappcompose.screens.credential.domain

import com.zabster.safeappcompose.db.entitys.CategoryEntity
import com.zabster.safeappcompose.db.entitys.CredentialEntity
import com.zabster.safeappcompose.db.helper.db.AppDatabaseHelper
import com.zabster.safeappcompose.db.helper.secure.AppSecureUtils
import com.zabster.safeappcompose.screens.common.domain.BaseDBInteractor
import com.zabster.safeappcompose.screens.utils.orZero
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

class SaveCredentialInteractor @Inject constructor(
    appDatabaseHelper: AppDatabaseHelper,
    private val appSecureUtils: AppSecureUtils,
) : BaseDBInteractor(appDatabaseHelper) {

    private val _saveState = MutableSharedFlow<Boolean?>(0)
    val saveState: Flow<Boolean> = _saveState.filterNotNull()

    fun validate(title: String, pass: String, categoryName: String) =
        title.isNotBlank() && pass.isNotBlank() && categoryName.isNotBlank()

    suspend fun saveCredential(
        id: Long?,
        title: String,
        desc: String,
        pass: String,
        categoryName: String
    ) {
        if (!validate(title, pass, categoryName)) {
            _saveState.emit(false)
            return
        }

        val categoryId = categoryDao.getCategoryByName(categoryName)?.id ?: run {
            categoryDao.insertOrUpdate(CategoryEntity(name = categoryName))
        }
        val newCredentialId = credentialDao.insertOrUpdate(
            CredentialEntity(
                id = id.orZero(),
                name = title,
                description = desc,
                hashPass = appSecureUtils.encrypt(pass),
                categoryId = categoryId
            )
        )
        _saveState.emit(newCredentialId > 0)
    }
}