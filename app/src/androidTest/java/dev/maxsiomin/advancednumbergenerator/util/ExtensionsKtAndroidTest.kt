package dev.maxsiomin.advancednumbergenerator.util

import android.text.SpannableStringBuilder
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ExtensionsKtAndroidTest {

    @Test
    fun toEditableForLongWorksProperly() {
        assertThat(1L.toEditable()).isEqualTo(SpannableStringBuilder("1"))
    }

    @Test
    fun toLongForEditableWorksProperly() {
        assertThat(SpannableStringBuilder("1").toLong()).isEqualTo(1L)
    }

    @Test
    fun stringToEditableWorksProperly() {
        assertThat("something".toEditable()).isEqualTo(SpannableStringBuilder("something"))
    }
}
