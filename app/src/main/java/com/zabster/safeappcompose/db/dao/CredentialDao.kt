package com.zabster.safeappcompose.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.zabster.safeappcompose.db.entitys.CategoryEntity
import com.zabster.safeappcompose.db.entitys.CredentialEntity
import kotlinx.coroutines.flow.Flow

/**
 * Access to user credential data
 */
@Dao
abstract class CredentialDao : BaseDao<CredentialEntity>() {

    /**
     * Get list credentials
     *
     * @return list credentials
     */
    @Query("select * from credentials")
    abstract fun getCredentials(): Flow<List<CredentialEntity>>

    /**
     * Get credential by id
     *
     * @return list credentials
     */
    @Query("select * from credentials where id=:id")
    abstract suspend fun getCredential(id: Long): CredentialEntity?

    /**
     * Get list credentials only one category
     *
     * @param categoryId id category for sort
     *
     * @return list credentials
     */
    @Query("select * from credentials where categoryId=:categoryId")
    abstract fun getCredentialsByCategoryId(categoryId: Long): Flow<List<CredentialEntity>>

    /**
     * Count user data
     *
     * @return [Int] count
     */
    @Query("select count(*) from credentials")
    abstract suspend fun count(): Int

    /**
     * Set default category to credentials when delete category
     *
     * @param categoryId deleted category id
     * @param defCategoryId category id for switching
     */
    @Query("update credentials set categoryId=:defCategoryId where categoryId=:categoryId")
    abstract suspend fun updateCategoryToDefault(categoryId: Long, defCategoryId: Long = CategoryEntity.TableInfo.DefaultValues.COLUMN_ID_DEF_VALUE)

    /**
     * Delete credential by id
     *
     * @param id credential id
     */
    @Query("delete from credentials where id=:id")
    abstract suspend fun deleteById(id: Long)
}