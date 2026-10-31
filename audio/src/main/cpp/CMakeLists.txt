cmake_minimum_required(VERSION 3.22.1)
project(soundcraft_audio LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_package(oboe REQUIRED CONFIG)

add_library(soundcraft_audio SHARED
        jni_bridge.cpp
        engine.cpp
        sequencer_core.cpp)

target_link_libraries(soundcraft_audio oboe::oboe log)
