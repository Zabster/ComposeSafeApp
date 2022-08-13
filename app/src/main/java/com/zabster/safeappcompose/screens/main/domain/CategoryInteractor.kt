package com.zabster.safeappcompose.screens.main.domain

import com.zabster.safeappcompose.db.entitys.CategoryEntity
import com.zabster.safeappcompose.db.helper.db.AppDatabaseHelper
import com.zabster.safeappcompose.screens.common.domain.BaseDBInteractor
import com.zabster.safeappcompose.screens.common.models.CategoryModel
import com.zabster.safeappcompose.screens.common.transforms.mapCategory
import javax.inject.Inject

class CategoryInteractor @Inject constructor(
    private val appDatabaseHelper: AppDatabaseHelper
) : BaseDBInteractor(appDatabaseHelper) {

    suspend fun deleteCategory(category: CategoryModel) {
        categoryDao.deleteById(category.id)
        if (category.isSelected) categoryDao.resetSelectedCategoryToDefault()
        credentialDao.updateCategoryToDefault(categoryId = category.id)
    }

    suspend fun updateSelectedCategory(selectedId: Long) {
        categoryDao.deselectedCurrentCategory()
        categoryDao.updateSelectedCategory(selectedId, true)
    }

    suspend fun getSelectedCategory(): CategoryModel {
        if (!appDatabaseHelper.wasInit) appDatabaseHelper.initialize()
        return categoryDao.getSelectedCategory()?.let(::mapCategory) ?: CategoryModel(
            id = CategoryEntity.TableInfo.DefaultValues.COLUMN_ID_DEF_VALUE,
            title = "",
            isDefault = true,
            isSelected = true
        )
    }
}