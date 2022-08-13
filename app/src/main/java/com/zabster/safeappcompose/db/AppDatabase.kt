package com.zabster.safeappcompose.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zabster.safeappcompose.db.dao.CategoryDao
import com.zabster.safeappcompose.db.dao.CredentialDao
import com.zabster.safeappcompose.db.entitys.CategoryEntity
import com.zabster.safeappcompose.db.entitys.CredentialEntity

@Database(
    entities = [
        CategoryEntity::class,
        CredentialEntity::class
    ],
    version = AppDatabase.DATABASE_VERSION
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao

    abstract fun credentialDao(): CredentialDao

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "SafeDB"
    }
}

