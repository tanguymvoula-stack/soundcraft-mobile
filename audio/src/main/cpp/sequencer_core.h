#pragma once
// Cœur DSP : séquenceur 16 pas + synthé de grosse caisse.
// Aucune dépendance à Android ni à Oboe : testable sur n'importe quelle machine.
// render() est appelé depuis le thread audio : pas d'allocation, pas de verrou.

#include <atomic>
#include <cstdint>

class SequencerCore {
public:
    static constexpr int kSteps = 16;

    void setSampleRate(double sampleRate) { sampleRate_ = sampleRate; }

    // Thread-safe : appelables depuis le thread UI.
    void setPlaying(bool playing) { playing_.store(playing, std::memory_order_relaxed); }
    void setBpm(float bpm) { bpm_.store(bpm, std::memory_order_relaxed); }
    void setPattern(uint32_t mask) { pattern_.store(mask, std::memory_order_relaxed); }
    // Pas en cours de lecture, ou -1 à l'arrêt.
    int currentStep() const { return currentStep_.load(std::memory_order_relaxed); }

    // Écrit `frames` trames entrelacées dans `out` (même signal sur tous les canaux).
    void render(float* out, int32_t frames, int32_t channels);

private:
    void triggerKick();
    float nextKickSample();

    double sampleRate_ = 48000.0;

    std::atomic<bool> playing_{false};
    std::atomic<float> bpm_{120.0f};
    std::atomic<uint32_t> pattern_{0x1111};
    std::atomic<int> currentStep_{-1};

    // État réservé au thread audio.
    bool wasPlaying_ = false;
    double samplesSinceStep_ = 0.0;
    int nextStep_ = 0;

    bool kickActive_ = false;
    double kickPos_ = 0.0;   // en échantillons depuis le déclenchement
    double kickPhase_ = 0.0; // radians
};
