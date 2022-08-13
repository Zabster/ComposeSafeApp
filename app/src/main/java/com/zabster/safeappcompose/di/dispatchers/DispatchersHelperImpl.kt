package com.zabster.safeappcompose.di.dispatchers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Impl for [DispatchersHelper]
 */
class DispatchersHelperImpl : DispatchersHelper {
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val io: CoroutineDispatcher = Dispatchers.IO
}