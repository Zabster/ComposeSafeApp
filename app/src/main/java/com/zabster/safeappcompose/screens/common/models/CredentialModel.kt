package com.zabster.safeappcompose.screens.common.models

data class CredentialModel(
    val id: Long,
    val title: String,
    val subtitle: String,
    val passwordHash: String,
)
