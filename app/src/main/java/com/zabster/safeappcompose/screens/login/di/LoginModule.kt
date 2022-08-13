package com.zabster.safeappcompose.screens.login.di

import com.zabster.safeappcompose.screens.login.domain.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
interface LoginModule {

    @Binds
    @ViewModelScoped
    fun LoginValidatorImpl.bindValidator(): LoginValidator

    @Binds
    @ViewModelScoped
    fun DialogCreatorInteractorImpl.bindDialogCreator(): DialogCreatorInteractor

    @Binds
    @ViewModelScoped
    fun PasswordInteractorImpl.bindPasswordInteractor(): PasswordInteractor
}