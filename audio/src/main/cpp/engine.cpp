#include "engine.h"

#include <android/log.h>

#define LOG_TAG "SoundCraftAudio"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

bool Engine::start() {
    if (stream_) return true;

    oboe::AudioStreamBuilder builder;
    builder.setDirection(oboe::Direction::Output)
            ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
            ->setSharingMode(oboe::SharingMode::Exclusive)
            ->setFormat(oboe::AudioFormat::Float)
            ->setChannelCount(oboe::ChannelCount::Stereo)
            ->setUsage(oboe::Usage::Media)
            ->setDataCallback(this);

    oboe::Result result = builder.openStream(stream_);
    if (result != oboe::Result::OK) {
        LOGE("openStream a échoué : %s", oboe::convertToText(result));
        stream_.reset();
        return false;
    }

    core_.setSampleRate(stream_->getSampleRate());
    stream_->setBufferSizeInFrames(stream_->getFramesPerBurst() * 2);

    result = stream_->requestStart();
    if (result != oboe::Result::OK) {
        LOGE("requestStart a échoué : %s", oboe::convertToText(result));
        stream_->close();
        stream_.reset();
        return false;
    }
    LOGI("Flux ouvert : %d Hz, burst %d trames", stream_->getSampleRate(),
         stream_->getFramesPerBurst());
    return true;
}

void Engine::stop() {
    if (!stream_) return;
    stream_->stop();
    stream_->close();
    stream_.reset();
}

oboe::DataCallbackResult Engine::onAudioReady(oboe::AudioStream* stream, void* audioData,
                                              int32_t numFrames) {
    core_.render(static_cast<float*>(audioData), numFrames, stream->getChannelCount());
    return oboe::DataCallbackResult::Continue;
}
