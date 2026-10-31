#include <jni.h>

#include "engine.h"

namespace {
Engine gEngine;
}

extern "C" {

JNIEXPORT jboolean JNICALL
Java_com_soundcraft_audio_AudioEngine_nativeStart(JNIEnv*, jobject) {
    return gEngine.start() ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_com_soundcraft_audio_AudioEngine_nativeStop(JNIEnv*, jobject) {
    gEngine.core().setPlaying(false);
    gEngine.stop();
}

JNIEXPORT void JNICALL
Java_com_soundcraft_audio_AudioEngine_nativeSetPlaying(JNIEnv*, jobject, jboolean playing) {
    gEngine.core().setPlaying(playing == JNI_TRUE);
}

JNIEXPORT void JNICALL
Java_com_soundcraft_audio_AudioEngine_nativeSetBpm(JNIEnv*, jobject, jfloat bpm) {
    gEngine.core().setBpm(bpm);
}

JNIEXPORT void JNICALL
Java_com_soundcraft_audio_AudioEngine_nativeSetPattern(JNIEnv*, jobject, jint mask) {
    gEngine.core().setPattern(static_cast<uint32_t>(mask));
}

JNIEXPORT jint JNICALL
Java_com_soundcraft_audio_AudioEngine_nativeCurrentStep(JNIEnv*, jobject) {
    return gEngine.core().currentStep();
}

}  // extern "C"
