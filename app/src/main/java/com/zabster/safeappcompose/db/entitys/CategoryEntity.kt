package com.zabster.safeappcompose.db.entitys

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Category db model
 *
 * @param id column id
 * @param name category name
 * @param isSelected select category or not
 */
@Entity(tableName = CategoryEntity.TABLE_NAME)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = Columns.COLUMN_NAME) val name: String,
    @ColumnInfo(
        name = Columns.COLUMN_IS_SELECTED,
        defaultValue = DefaultValues.COLUMN_IS_NO_ACTIVE_DEF_VALUE,
        collate = ColumnInfo.NOCASE
    ) val isSelected: Boolean = false
) {

    /**
     * Table information
     */
    companion object TableInfo {

        /**
         * Table name
         */
        const val TABLE_NAME = "category"

        /**
         * Column names
         */
        object Columns {

            const val COLUMN_NAME = "name"
            const val COLUMN_IS_SELECTED = "is_selected"
        }

        /**
         * Some default values
         */
        object DefaultValues {

            const val COLUMN_IS_NO_ACTIVE_DEF_VALUE = "0"
            const val COLUMN_IS_ACTIVE_DEF_VALUE = 1
            const val COLUMN_ID_DEF_VALUE = 1L
            const val COLUMN_ID_DEF_NO_SET_VALUE = -1L
        }
    }
}