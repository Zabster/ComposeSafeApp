package com.zabster.safeappcompose.screens.login.domain

interface PasswordInteractor {

    suspend fun saveNewPassword(password: String)

}