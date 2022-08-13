package com.zabster.safeappcompose.screens.common.models

import com.zabster.safeappcompose.ui.view.models.TitleModel

data class CategoryModel(
    override val title: String,
    val id: Long,
    val isDefault: Boolean,
    val isSelected: Boolean
) : TitleModel
