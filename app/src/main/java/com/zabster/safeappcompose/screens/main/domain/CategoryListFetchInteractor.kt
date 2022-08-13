package com.zabster.safeappcompose.screens.main.domain

import com.zabster.safeappcompose.db.helper.db.AppDatabaseHelper
import com.zabster.safeappcompose.screens.common.domain.BaseDBInteractor
import com.zabster.safeappcompose.screens.common.models.CategoryModel
import com.zabster.safeappcompose.screens.common.transforms.mapCategories
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryListFetchInteractor @Inject constructor(
    appDatabaseHelper: AppDatabaseHelper
) : BaseDBInteractor(appDatabaseHelper) {

    private val _categoriesList = MutableStateFlow<List<CategoryModel>>(listOf())
    val categoriesList = _categoriesList

    suspend fun fetchAllCategories() {
        _categoriesList.emitAll(
            categoryDao.getCategoriesAsFlow().map(::mapCategories)
        )
    }
}