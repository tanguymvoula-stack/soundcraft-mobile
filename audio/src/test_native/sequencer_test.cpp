// Test hôte du cœur DSP :
//   g++ -std=c++17 -I../main/cpp sequencer_test.cpp ../main/cpp/sequencer_core.cpp -o seqtest
#include <cmath>
#include <cstdio>
#include <utility>
#include <vector>

#include "sequencer_core.h"

static int failures = 0;
static void check(bool cond, const char* msg) {
    std::printf("%s : %s\n", cond ? "ok    " : "ECHEC ", msg);
    if (!cond) ++failures;
}

// Rend `total` trames une par une et renvoie (trame, pas) à chaque changement de pas.
static std::vector<std::pair<long, int>> stepChanges(SequencerCore& c, long total) {
    std::vector<std::pair<long, int>> out;
    float buf[2];
    int last = c.currentStep();
    for (long i = 0; i < total; ++i) {
        c.render(buf, 1, 2);
        int s = c.currentStep();
        if (s != last) { out.push_back({i, s}); last = s; }
    }
    return out;
}

static float peakOf(const std::vector<float>& v) {
    float p = 0;
    for (float x : v) p = std::fmax(p, std::fabs(x));
    return p;
}

int main() {
    {
        SequencerCore c; c.setSampleRate(48000); c.setBpm(120); c.setPattern(0x1111); c.setPlaying(true);
        auto ch = stepChanges(c, 6000 * 5);
        check(ch.size() == 5, "5 pas en 5 x 6000 trames (120 BPM, 48 kHz)");
        bool exact = ch.size() == 5;
        for (size_t k = 0; k < ch.size() && exact; ++k)
            exact = (ch[k].first == (long)k * 6000) && (ch[k].second == (int)k);
        check(exact, "le pas k démarre exactement à la trame k*6000");
    }
    {
        SequencerCore c; c.setSampleRate(48000); c.setBpm(60); c.setPlaying(true);
        auto ch = stepChanges(c, 12000 * 3);
        check(ch.size() == 3 && ch[1].first == 12000, "60 BPM : un pas toutes les 12000 trames");
    }
    {
        SequencerCore c; c.setSampleRate(48000); c.setBpm(120); c.setPlaying(true);
        auto ch = stepChanges(c, 6000 * 17);
        check(ch.size() == 17 && ch[16].second == 0, "après le pas 15, retour au pas 0");
    }
    {
        SequencerCore c; c.setSampleRate(48000); c.setBpm(120); c.setPattern(0); c.setPlaying(true);
        std::vector<float> buf(2 * 48000);
        c.render(buf.data(), 48000, 2);
        check(peakOf(buf) == 0.0f, "motif vide : silence parfait");
    }
    {
        SequencerCore c; c.setSampleRate(48000); c.setBpm(120); c.setPattern(0x1); c.setPlaying(true);
        std::vector<float> buf(2 * 6000);
        c.render(buf.data(), 6000, 2);
        float early = 0; bool sameChannels = true;
        for (int i = 0; i < 6000; ++i) {
            if (i < 1200) early = std::fmax(early, std::fabs(buf[2 * i]));
            if (buf[2 * i] != buf[2 * i + 1]) sameChannels = false;
        }
        check(early > 0.5f, "kick audible dans les 25 premières ms");
        check(peakOf(buf) <= 1.0f, "pas de saturation (pic <= 1.0)");
        check(sameChannels, "canaux gauche et droit identiques");
    }
    {
        SequencerCore c; c.setSampleRate(48000); c.setBpm(120); c.setPattern(0x1111); c.setPlaying(true);
        std::vector<float> buf(2 * 3000);
        c.render(buf.data(), 3000, 2);
        c.setPlaying(false);
        c.render(buf.data(), 3000, 2);
        check(c.currentStep() == -1, "à l'arrêt : pas courant = -1");
        check(peakOf(buf) == 0.0f, "à l'arrêt : silence immédiat");
    }
    std::printf(failures ? "\n%d ECHEC(S)\n" : "\nTous les tests passent\n", failures);
    return failures ? 1 : 0;
}
