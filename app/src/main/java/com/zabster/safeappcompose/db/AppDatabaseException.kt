package com.zabster.safeappcompose.db

sealed class AppDatabaseException: Exception() {

    object DatabaseAlreadyCreating: AppDatabaseException()

}
