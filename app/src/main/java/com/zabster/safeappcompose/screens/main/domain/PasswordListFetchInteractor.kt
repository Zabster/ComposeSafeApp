package com.zabster.safeappcompose.screens.main.domain

import com.zabster.safeappcompose.db.helper.db.AppDatabaseHelper
import com.zabster.safeappcompose.screens.common.domain.BaseDBInteractor
import com.zabster.safeappcompose.screens.common.models.CredentialModel
import com.zabster.safeappcompose.screens.common.transforms.mapCredentials
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class PasswordListFetchInteractor @Inject constructor(
    private val appDatabaseHelper: AppDatabaseHelper
): BaseDBInteractor(appDatabaseHelper) {

    private val _credentialsList = MutableSharedFlow<List<CredentialModel>>(0)
    val credentialsList: Flow<List<CredentialModel>> = _credentialsList

    suspend fun fetchAllPasswords() {
        if (!appDatabaseHelper.wasInit) appDatabaseHelper.initialize()
        _credentialsList.emitAll(
            credentialDao.getCredentials().map(::mapCredentials)
        )
    }

    suspend fun fetchPasswordsByCategoryId(categoryId: Long) {
        if (!appDatabaseHelper.wasInit) appDatabaseHelper.initialize()
        _credentialsList.emitAll(
            credentialDao.getCredentialsByCategoryId(categoryId).map(::mapCredentials)
        )
    }
}