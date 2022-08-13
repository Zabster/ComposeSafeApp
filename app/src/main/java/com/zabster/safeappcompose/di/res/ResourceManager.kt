package com.zabster.safeappcompose.di.res

import androidx.annotation.StringRes

/**
 * Resource instance helpers
 */
interface ResourceManager {

    /**
     * Get string resources
     *
     * @param resStringId id string resource
     */
    fun getString(@StringRes resStringId: Int): String

    /**
     * Get string resources with args
     *
     * @param resStringId id string resource
     * @param formatArgs some argument for formatting
     */
    fun getString(@StringRes resStringId: Int, vararg formatArgs: Any): String
}