#include "sequencer_core.h"

#include <cmath>

namespace {
constexpr double kTwoPi = 6.283185307179586;
constexpr double kKickLengthSeconds = 0.8;
}  // namespace

void SequencerCore::triggerKick() {
    kickActive_ = true;
    kickPos_ = 0.0;
    kickPhase_ = 0.0;
}

float SequencerCore::nextKickSample() {
    if (!kickActive_) return 0.0f;
    const double t = kickPos_ / sampleRate_;
    // Chute de hauteur rapide (175 Hz -> 45 Hz) + enveloppe d'amplitude exponentielle.
    const double freq = 45.0 + 130.0 * std::exp(-t * 40.0);
    kickPhase_ += kTwoPi * freq / sampleRate_;
    if (kickPhase_ > kTwoPi) kickPhase_ -= kTwoPi;
    const double amp = std::exp(-t * 8.0);
    kickPos_ += 1.0;
    if (t > kKickLengthSeconds) kickActive_ = false;
    return static_cast<float>(std::sin(kickPhase_) * amp * 0.9);
}

void SequencerCore::render(float* out, int32_t frames, int32_t channels) {
    const bool playing = playing_.load(std::memory_order_relaxed);
    const uint32_t pattern = pattern_.load(std::memory_order_relaxed);
    const float bpm = bpm_.load(std::memory_order_relaxed);
    // Un pas = une double-croche = 1/4 de temps.
    const double stepLength = sampleRate_ * 60.0 / (static_cast<double>(bpm) * 4.0);

    for (int32_t i = 0; i < frames; ++i) {
        if (playing) {
            if (!wasPlaying_) {
                wasPlaying_ = true;
                nextStep_ = 0;
                samplesSinceStep_ = stepLength;  // déclenche le pas 0 immédiatement
            }
            if (samplesSinceStep_ >= stepLength) {
                samplesSinceStep_ -= stepLength;
                const int step = nextStep_;
                nextStep_ = (nextStep_ + 1) % kSteps;
                currentStep_.store(step, std::memory_order_relaxed);
                if (pattern & (1u << step)) triggerKick();
            }
            samplesSinceStep_ += 1.0;
        } else if (wasPlaying_) {
            wasPlaying_ = false;
            kickActive_ = false;
            currentStep_.store(-1, std::memory_order_relaxed);
        }

        const float sample = nextKickSample();
        for (int32_t c = 0; c < channels; ++c) out[i * channels + c] = sample;
    }
}
