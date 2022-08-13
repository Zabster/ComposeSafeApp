package com.zabster.safeappcompose.db.helper.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zabster.safeappcompose.R
import com.zabster.safeappcompose.db.entitys.CategoryEntity
import com.zabster.safeappcompose.di.res.ResourceManager

class DatabaseCreationCallback(
    private val resourceManager: ResourceManager
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        insertDefaultCategory(db)
    }

    private fun insertDefaultCategory(db: SupportSQLiteDatabase) {
        val cv = ContentValues().apply {
            put(
                CategoryEntity.TableInfo.Columns.COLUMN_NAME,
                resourceManager.getString(R.string.category_default_name)
            )
            put(CategoryEntity.TableInfo.Columns.COLUMN_IS_SELECTED, true)
        }
        db.insert(CategoryEntity.TABLE_NAME, SQLiteDatabase.CONFLICT_IGNORE, cv)
    }
}