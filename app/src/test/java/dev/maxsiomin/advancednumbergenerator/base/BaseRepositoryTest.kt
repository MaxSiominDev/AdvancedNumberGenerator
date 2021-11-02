package dev.maxsiomin.advancednumbergenerator.base

import junit.framework.TestCase.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BaseRepositoryTest {

    @Test
    fun `non-connection error returns false`() {
        assertFalse(BaseRepository.isConnectionError("some error"))
    }

    @Test
    fun `connection error returns true`() {
        assertTrue(BaseRepository.isConnectionError("unable to resolve host"))
    }
}
