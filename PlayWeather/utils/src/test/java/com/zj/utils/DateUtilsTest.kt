package com.zj.utils.weather

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DateUtilsTest {

    @Test
    fun `first hourly item is now and following items use forecast city offset hour`() {
        assertEquals("现在", getHourlyTimeName("2026-05-25T23:00+08:00", 0, "现在", "时"))
        assertEquals("0时", getHourlyTimeName("2026-05-26T00:00+08:00", 1, "现在", "时"))
        assertEquals("11时", getHourlyTimeName("2026-05-25T11:00-04:00", 1, "现在", "时"))
    }

    @Test
    fun `local minutes are read from the time zone in the api timestamp`() {
        assertEquals(22 * 60 + 42, getLocalMinutes("2026-05-25T22:42+08:00"))
        assertEquals(10 * 60 + 42, getLocalMinutes("2026-05-25T10:42-04:00"))
    }

    @Test
    fun `daytime is based on city local time and sunrise sunset`() {
        assertTrue(isDaytime("2026-05-25T10:42-04:00", "05:45", "20:25"))
        assertFalse(isDaytime("2026-05-25T22:42+08:00", "04:52", "19:30"))
    }

    @Test
    fun `sun progress is based on city local time`() {
        val progress = getSunProgress("2026-05-25T10:42-04:00", "05:42", "20:42")
        assertEquals(1f / 3f, progress, 0.01f)
    }
}
