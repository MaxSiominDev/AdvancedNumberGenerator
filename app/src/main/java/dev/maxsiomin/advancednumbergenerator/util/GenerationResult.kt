package dev.maxsiomin.advancednumbergenerator.util

import androidx.annotation.StringRes

sealed class GenerationResult

data class GenerationSuccess(
    val generatedNumber: Long,
) : GenerationResult()

data class GenerationFailure(
    @StringRes val error: Int,
) : GenerationResult()
