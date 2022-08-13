package com.zabster.safeappcompose.ui.dialog.model

sealed class DialogModel {

    object HideDialog: DialogModel()

    data class DialogDataModel(
        val key: DialogKey = EmptyKey,
        val title: String = "",
        val message: String = "",
        val isCancellable: Boolean = true,
        val isSelectable: Boolean = false,
        val positiveButton: String? = null,
        val negativeButton: String? = null
    ) : DialogModel()
}
