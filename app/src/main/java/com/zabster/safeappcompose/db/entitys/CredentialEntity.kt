package com.zabster.safeappcompose.db.entitys

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Password db model
 *
 * @param id column id
 * @param name name for password save model
 * @param description description for password save model
 * @param hashPass password json data (base64)
 * @param categoryId category id
 * @param timeStamp time when entity was created
 */
@Entity(tableName = CredentialEntity.TABLE_NAME)
data class CredentialEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = Columns.COLUMN_NAME) val name: String,
    @ColumnInfo(name = Columns.COLUMN_DESC) val description: String,
    @ColumnInfo(name = Columns.COLUMN_HASH_PASS) val hashPass: String,
    @ColumnInfo(name = Columns.COLUMN_CATEGORY_ID) val categoryId: Long,
    @ColumnInfo(name = Columns.COLUMN_TIME_STAMP) val timeStamp: Long = System.currentTimeMillis()
) {

    /**
     * Table information
     */
    companion object TableInfo {

        /**
         * Table name
         */
        const val TABLE_NAME = "credentials"

        /**
         * Column names
         */
        object Columns {

            const val COLUMN_NAME = "name"
            const val COLUMN_DESC = "description"
            const val COLUMN_HASH_PASS = "hashPass"
            const val COLUMN_CATEGORY_ID = "categoryId"
            const val COLUMN_TIME_STAMP = "timeStamp"
        }
    }
}
