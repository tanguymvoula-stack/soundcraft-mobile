# SoundCraft Studio

DAW Android hors-ligne, pensé pour le tactile et pour Samsung DeX. Redémarrage de zéro.

## État : fondation (v0.1.0)

Ce qui existe :
- un moteur audio C++ (Oboe, faible latence) avec un séquenceur 16 pas sample-accurate et un kick synthétisé ;
- une interface sombre adaptative (téléphone / DeX) : transport, tempo 60–180 BPM, grille de 16 pas ;
- une CI GitHub Actions : test natif du séquenceur, tests du module `core`, build de l'APK debug.

Ce qui n'existe pas encore : autres instruments, effets, mixer, piano roll, arrangement, sauvegarde, export.
Rien n'a été validé sur un appareil réel à ce stade.

## Modules

| Module  | Rôle |
|---------|------|
| `core`  | Modèle de données en Kotlin pur (motif, tempo), testable sans Android |
| `audio` | Moteur C++/Oboe + façade Kotlin `AudioEngine` |
| `app`   | Interface Compose, ViewModel, point d'entrée |

Le cœur DSP (`audio/src/main/cpp/sequencer_core.*`) ne dépend ni d'Android ni d'Oboe : il se teste sur une machine ordinaire (`audio/src/test_native`).

## Build

La CI tourne sur `main` et `feature/**`. Récupérer l'artefact `soundcraft-studio-debug-apk`.
L'APK est signé avec une clé debug fixe (`app/debug.keystore`, publique) : une nouvelle version s'installe par-dessus la précédente.
L'identifiant d'application est `com.soundcraft.studio` : l'ancienne app `com.soundcraft.mobile` reste installée à part, à désinstaller à la main.

## Limites connues

- Si la sortie audio est déconnectée en cours de lecture (casque débranché), le flux n'est pas rouvert automatiquement ; relancer Play.
- ABI arm64-v8a uniquement.
