package com.zabster.safeappcompose.screens.credential.domain

import com.zabster.safeappcompose.db.helper.db.AppDatabaseHelper
import com.zabster.safeappcompose.screens.common.domain.BaseDBInteractor
import com.zabster.safeappcompose.screens.common.models.CategoryModel
import com.zabster.safeappcompose.screens.common.transforms.mapCategories
import com.zabster.safeappcompose.screens.common.transforms.mapCategory
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class CategoryInteractor @Inject constructor(
    appDatabaseHelper: AppDatabaseHelper,
) : BaseDBInteractor(appDatabaseHelper) {

    private val _categoriesList = MutableSharedFlow<List<CategoryModel>>()
    val categoriesList = _categoriesList

    suspend fun fetchAllCategories() {
        _categoriesList.emitAll(
            categoryDao.getCategoriesAsFlow()
                .map(::mapCategories)
        )
    }

    suspend fun fetchCategoriesByName(name: String) {
        _categoriesList.emit(
            categoryDao.getCategoryByNameContains(name).map(::mapCategory)
        )
    }
}