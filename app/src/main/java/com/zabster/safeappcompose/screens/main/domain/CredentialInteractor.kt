package com.zabster.safeappcompose.screens.main.domain

import com.zabster.safeappcompose.db.helper.db.AppDatabaseHelper
import com.zabster.safeappcompose.screens.common.domain.BaseDBInteractor
import javax.inject.Inject

class CredentialInteractor @Inject constructor(
    appDatabaseHelper: AppDatabaseHelper,
) : BaseDBInteractor(appDatabaseHelper) {

    suspend fun removeCredential(id: Long) {
        credentialDao.deleteById(id)
    }
}