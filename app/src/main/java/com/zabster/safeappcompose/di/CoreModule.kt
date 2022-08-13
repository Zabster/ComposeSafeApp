package com.zabster.safeappcompose.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.zabster.safeappcompose.dataStore
import com.zabster.safeappcompose.db.helper.db.AppDatabaseHelper
import com.zabster.safeappcompose.db.helper.db.AppDatabaseHelperImpl
import com.zabster.safeappcompose.db.helper.secure.AppSecureUtils
import com.zabster.safeappcompose.db.helper.secure.AppSecureUtilsImpl
import com.zabster.safeappcompose.di.dispatchers.DispatchersHelper
import com.zabster.safeappcompose.di.dispatchers.DispatchersHelperImpl
import com.zabster.safeappcompose.di.res.ResourceManager
import com.zabster.safeappcompose.di.res.ResourceManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Singleton
    @Provides
    fun provideResources(@ApplicationContext context: Context): ResourceManager =
        ResourceManagerImpl(context)

    @Singleton
    @Provides
    fun provideDispatchers(): DispatchersHelper = DispatchersHelperImpl()

    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.dataStore

    @Singleton
    @Provides
    fun provideSecureHelper(
        @ApplicationContext context: Context
    ): AppSecureUtils = AppSecureUtilsImpl(context)

    @Singleton
    @Provides
    fun provideDataBase(
        @ApplicationContext context: Context,
        resourceManager: ResourceManager,
        dispatchersHelper: DispatchersHelper,
        dataStore: DataStore<Preferences>,
        appSecureUtils: AppSecureUtils
    ): AppDatabaseHelper = AppDatabaseHelperImpl(
        context = context,
        resourceManager = resourceManager,
        dispatchersHelper = dispatchersHelper,
        dataStore = dataStore,
        appSecureUtils = appSecureUtils
    )
}