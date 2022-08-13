package com.zabster.safeappcompose.screens.common.transforms

import com.zabster.safeappcompose.db.entitys.CategoryEntity
import com.zabster.safeappcompose.db.entitys.CredentialEntity
import com.zabster.safeappcompose.screens.common.models.CategoryModel
import com.zabster.safeappcompose.screens.common.models.CredentialModel

fun mapCategories(categories: List<CategoryEntity>): List<CategoryModel> =
    categories.map(::mapCategory)

fun mapCategory(entity: CategoryEntity): CategoryModel =
    CategoryModel(
        id = entity.id,
        title = entity.name,
        isDefault = entity.id == CategoryEntity.TableInfo.DefaultValues.COLUMN_ID_DEF_VALUE,
        isSelected = entity.isSelected
    )

fun mapCredentials(listEntity: List<CredentialEntity>): List<CredentialModel> =
    listEntity.map(::mapCredential)

fun mapCredential(entity: CredentialEntity): CredentialModel =
    CredentialModel(
        id = entity.id,
        title = entity.name,
        subtitle = entity.description,
        passwordHash = entity.hashPass
    )