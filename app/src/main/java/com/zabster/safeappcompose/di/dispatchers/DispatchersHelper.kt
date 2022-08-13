package com.zabster.safeappcompose.di.dispatchers

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Coroutine dispatchers
 */
interface DispatchersHelper {

    /**
     * Main thread
     */
    val main: CoroutineDispatcher

    /**
     * On-demand created threads
     */
    val io: CoroutineDispatcher

}