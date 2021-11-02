package dev.maxsiomin.advancednumbergenerator.base

import dev.maxsiomin.advancednumbergenerator.util.UiActions
import dev.maxsiomin.advancednumbergenerator.util.notNull

/**
 * All repositories in project must extend this class
 */
abstract class BaseRepository(uiActions: UiActions) : UiActions by uiActions {

    companion object {

        private const val CONNECTION_ERROR = "unable to resolve host"

        @JvmStatic
        fun isConnectionError(error: String?) = CONNECTION_ERROR in error.notNull().lowercase()
    }
}
