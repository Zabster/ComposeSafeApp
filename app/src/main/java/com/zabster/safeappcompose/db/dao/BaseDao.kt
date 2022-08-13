package com.zabster.safeappcompose.db.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

/**
 * Base DAO actions
 */
abstract class BaseDao<T> {

    /**
     * Insert data or update if have conflict
     *
     * @param data object from db
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertOrUpdate(data: T): Long

    /**
     * Update data
     *
     * @param data object from db
     */
    @Update
    abstract suspend fun update(data: T)

    /**
     * Delete data
     *
     * @param data object from db
     */
    @Delete
    abstract suspend fun delete(data: T)
}