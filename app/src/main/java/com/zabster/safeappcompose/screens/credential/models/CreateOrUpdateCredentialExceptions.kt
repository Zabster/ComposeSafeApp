package com.zabster.safeappcompose.screens.credential.models

sealed class CreateOrUpdateCredentialExceptions: Exception() {

    data class CredentialNotFound(val id: Long): CreateOrUpdateCredentialExceptions()
    data class CategoryNotFound(val id: Long): CreateOrUpdateCredentialExceptions()

}
