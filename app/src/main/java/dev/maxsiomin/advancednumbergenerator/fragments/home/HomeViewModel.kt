package dev.maxsiomin.advancednumbergenerator.fragments.home

import dagger.hilt.android.lifecycle.HiltViewModel
import dev.maxsiomin.advancednumbergenerator.R
import dev.maxsiomin.advancednumbergenerator.base.BaseViewModel
import dev.maxsiomin.advancednumbergenerator.base.stringMutableLiveData
import dev.maxsiomin.advancednumbergenerator.util.*
import java.util.*
import javax.inject.Inject

fun getEmsByLength(length: Int): Int = length / 2 + 1

@HiltViewModel
class HomeViewModel @Inject constructor(uiActions: UiActions) : BaseViewModel(uiActions) {

    val generatedNumberLiveData = stringMutableLiveData()

    @Inject
    lateinit var random: Random

    fun generateNumber(min: String, max: String, cheatingEnabled: Boolean, cheatingValue: String?): GenerationResult {

        val minLong: Long = min.safeToLong() ?: return GenerationFailure(R.string.incorrect_min)
        val maxLong: Long = max.safeToLong() ?: return GenerationFailure(R.string.incorrect_max)

        return when {
            minLong > maxLong -> GenerationFailure(R.string.min_more_max)
            cheatingEnabled && cheatingValue in minLong..maxLong -> GenerationSuccess(cheatingValue!!.toLong())
            else -> GenerationSuccess(random.nextLong(minLong, maxLong))
        }
    }

    fun onNumberGenerated(result: Long?) {
        result?.let {

        }
        generatedNumberLiveData.value = result?.toString().notNull()
    }
}
