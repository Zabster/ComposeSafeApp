package com.zabster.safeappcompose.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.zabster.safeappcompose.db.entitys.CategoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Access to category object
 */
@Dao
abstract class CategoryDao : BaseDao<CategoryEntity>() {

    /**
     * Get list categories as Flow
     *
     * @return [Flow] list categories
     */
    @Query("select * from category")
    abstract fun getCategoriesAsFlow(): Flow<List<CategoryEntity>>

    /**
     * Get selected category id
     *
     * @return [Long] category id
     */
    @Query("select id from category where is_selected=${CategoryEntity.TableInfo.DefaultValues.COLUMN_IS_ACTIVE_DEF_VALUE}")
    abstract suspend fun getSelectedCategoryId(): Long?

    /**
     * Get selected category
     *
     * @return [CategoryEntity] category
     */
    @Query("select * from category where is_selected=${CategoryEntity.TableInfo.DefaultValues.COLUMN_IS_ACTIVE_DEF_VALUE}")
    abstract suspend fun getSelectedCategory(): CategoryEntity?

    /**
     * Get category by id
     *
     * @param id category id from db
     *
     * @return [CategoryEntity] category info
     */
    @Query("select * from category where id=:id")
    abstract suspend fun getCategoryById(id: Long): CategoryEntity?

    /**
     * Get category by name
     *
     * @param name category name from db
     *
     * @return [CategoryEntity] category info
     */
    @Query("select * from category where name=:name")
    abstract suspend fun getCategoryByName(name: String): CategoryEntity?

    /**
     * Get category by list by word contains
     *
     * @param word category id from db word
     *
     * @return list of [CategoryEntity] category info
     */
    @Query("select * from category where name like :word || '%'")
    abstract suspend fun getCategoryByNameContains(word: String): List<CategoryEntity>

    /**
     * Delete category by id
     */
    @Query("delete from category where id=:categoryId")
    abstract suspend fun deleteById(categoryId: Long)

    /**
     * Set select category by id
     */
    @Query("update category set is_selected=${CategoryEntity.TableInfo.DefaultValues.COLUMN_IS_ACTIVE_DEF_VALUE} where id=${CategoryEntity.TableInfo.DefaultValues.COLUMN_ID_DEF_VALUE}")
    abstract suspend fun resetSelectedCategoryToDefault()

    @Query("update category set is_selected=:isSelected where id=:categoryId")
    abstract suspend fun updateSelectedCategory(categoryId: Long, isSelected: Boolean)

    @Query("update category set is_selected=${0} where is_selected=${1}")
    abstract suspend fun deselectedCurrentCategory()
}