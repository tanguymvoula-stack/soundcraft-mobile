package com.soundcraft.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StepPatternTest {
    @Test
    fun fourOnTheFloorBitmask() {
        // pas 0, 4, 8, 12 -> 0b0001_0001_0001_0001
        assertEquals(0x1111, StepPattern.fourOnTheFloor().toBitmask())
    }

    @Test
    fun toggleFlipsOnlyOneStep() {
        val p = StepPattern.fourOnTheFloor().toggled(1)
        assertEquals(0x1113, p.toBitmask())
        assertEquals(0x1111, p.toggled(1).toBitmask())
    }

    @Test
    fun rejectsOutOfRange() {
        assertFailsWith<IllegalArgumentException> { StepPattern.fourOnTheFloor().toggled(16) }
    }

    @Test
    fun tempoIsClamped() {
        assertEquals(60f, Tempo.clamp(10f))
        assertEquals(180f, Tempo.clamp(999f))
    }
}
