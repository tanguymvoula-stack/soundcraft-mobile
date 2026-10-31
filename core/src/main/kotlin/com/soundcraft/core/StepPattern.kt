package com.soundcraft.core

/** Motif de 16 pas pour une piste. Immuable : chaque modification renvoie une nouvelle instance. */
data class StepPattern(val steps: List<Boolean>) {
    init {
        require(steps.size == STEP_COUNT) { "Un motif contient exactement $STEP_COUNT pas" }
    }

    fun toggled(index: Int): StepPattern {
        require(index in 0 until STEP_COUNT) { "Pas hors limites : $index" }
        return StepPattern(steps.mapIndexed { i, on -> if (i == index) !on else on })
    }

    /** Masque binaire : le bit i vaut 1 si le pas i est actif. */
    fun toBitmask(): Int = steps.foldIndexed(0) { i, acc, on -> if (on) acc or (1 shl i) else acc }

    companion object {
        const val STEP_COUNT = 16

        /** Kick sur les temps (pas 0, 4, 8, 12). */
        fun fourOnTheFloor() = StepPattern(List(STEP_COUNT) { it % 4 == 0 })
    }
}

object Tempo {
    const val MIN_BPM = 60f
    const val MAX_BPM = 180f
    const val DEFAULT_BPM = 120f

    fun clamp(bpm: Float): Float = bpm.coerceIn(MIN_BPM, MAX_BPM)
}
