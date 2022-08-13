package com.zabster.safeappcompose.di.res

import android.content.Context

/**
 * Impl for [ResourceManager]
 *
 * @param context application context
 */
class ResourceManagerImpl(private val context: Context) : ResourceManager {

    override fun getString(resStringId: Int): String =
        context.getString(resStringId)

    override fun getString(resStringId: Int, vararg formatArgs: Any): String =
        context.getString(resStringId, *formatArgs)
}