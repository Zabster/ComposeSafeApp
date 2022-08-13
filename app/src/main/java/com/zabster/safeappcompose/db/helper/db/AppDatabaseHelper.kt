package com.zabster.safeappcompose.db.helper.db

import com.zabster.safeappcompose.db.AppDatabase

interface AppDatabaseHelper {

    val database: AppDatabase

    val wasInit: Boolean

    suspend fun initialize()
}