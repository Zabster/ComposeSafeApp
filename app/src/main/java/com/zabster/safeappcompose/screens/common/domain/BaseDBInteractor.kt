package com.zabster.safeappcompose.screens.common.domain

import com.zabster.safeappcompose.db.helper.db.AppDatabaseHelper

abstract class BaseDBInteractor(private val appDatabaseHelper: AppDatabaseHelper) {

    protected val credentialDao by lazy { appDatabaseHelper.database.credentialDao() }
    protected val categoryDao by lazy { appDatabaseHelper.database.categoryDao() }

}