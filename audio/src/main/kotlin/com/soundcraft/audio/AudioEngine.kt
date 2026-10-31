package com.soundcraft.audio

/**
 * Façade Kotlin du moteur audio natif (C++/Oboe).
 * Les setters sont sûrs depuis le thread UI : ils n'écrivent que des valeurs atomiques.
 */
object AudioEngine {
    init {
        System.loadLibrary("soundcraft_audio")
    }

    /** Ouvre le flux de sortie. Renvoie false si la sortie audio n'a pas pu être ouverte. */
    fun start(): Boolean = nativeStart()

    /** Arrête la lecture et libère le flux audio. */
    fun stop() = nativeStop()

    fun setPlaying(playing: Boolean) = nativeSetPlaying(playing)
    fun setBpm(bpm: Float) = nativeSetBpm(bpm)

    /** Bit i = pas i actif. */
    fun setPattern(mask: Int) = nativeSetPattern(mask)

    /** Pas en cours (0..15), ou -1 à l'arrêt. */
    fun currentStep(): Int = nativeCurrentStep()

    private external fun nativeStart(): Boolean
    private external fun nativeStop()
    private external fun nativeSetPlaying(playing: Boolean)
    private external fun nativeSetBpm(bpm: Float)
    private external fun nativeSetPattern(mask: Int)
    private external fun nativeCurrentStep(): Int
}
