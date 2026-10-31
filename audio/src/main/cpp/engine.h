#pragma once
#include <oboe/Oboe.h>

#include <memory>

#include "sequencer_core.h"

// Enveloppe Oboe autour de SequencerCore : ouvre le flux de sortie faible latence.
class Engine : public oboe::AudioStreamDataCallback {
public:
    bool start();  // idempotent
    void stop();

    SequencerCore& core() { return core_; }

    oboe::DataCallbackResult onAudioReady(oboe::AudioStream* stream, void* audioData,
                                          int32_t numFrames) override;

private:
    SequencerCore core_;
    std::shared_ptr<oboe::AudioStream> stream_;
};
