package dev.maxsiomin.advancednumbergenerator.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ExtensionsKtTest {

    @Test
    fun `safeToLong works properly`() {
        assertThat(null.safeToLong()).isNull()
        assertThat("".safeToLong()).isNull()
        assertThat("something".safeToLong()).isNull()
        assertThat("1".safeToLong()).isEqualTo(1L)
    }

    @Test
    fun `custom random result is always in range`() {
        val random = java.util.Random()
        for (i in 0..1000) {
            assertThat(random.nextLong(0, 1)).isIn(0L..1L)
        }
    }

    @Test
    fun `notNull for String works properly`() {
        assertThat("".notNull()).isEqualTo("")
        assertThat("something".notNull()).isEqualTo("something")
        assertThat(null.notNull()).isEqualTo("")
    }

    @Test
    fun `overloaded operator contains works properly`() {
        assertThat(null in 1L..9L).isFalse()
        assertThat("" in 1L..9L).isFalse()
        assertThat("something" in 1L..9L).isFalse()
        assertThat("-1" in 1L..9L).isFalse()
        assertThat("-1" in -5L..0L).isTrue()
        assertThat("0" in 1L..9L).isFalse()
        assertThat("3" in 1L..9L).isTrue()
        assertThat("15" in 1L..9L).isFalse()
    }
}
