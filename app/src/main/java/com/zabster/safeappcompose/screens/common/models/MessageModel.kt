package com.zabster.safeappcompose.screens.common.models

data class MessageModel(
    val message: String?,
    val id: Int,
) {
    companion object {
        fun empty() = MessageModel(null, 0)
    }
}


