# SoundCraft Mobile

An open-source, offline-first modular music studio for Android, designed for touch use and Samsung DeX desktop mode.

## Current status: foundation prototype

This repository currently contains an adaptive Compose interface and a GitHub Actions workflow that builds a debug APK. The transport and step sequencer are UI prototypes only; **audio playback, instruments, effects, project saving, and export are not implemented yet**.

## Design goals

- Android phone and tablet layouts, including resizable Samsung DeX windows
- Keyboard and mouse-friendly desktop interaction alongside touch controls
- Local operation: no AI feature or remote AI API is required by the installed app
- Modular architecture for a future audio engine, instruments, effects, sequencer, mixer, and export
- Automated debug APK builds through GitHub Actions

## Build

The GitHub Actions workflow runs on pushes to `main` and `feature/android-dex-foundation`, and can also be started manually from the Actions tab. On success, download the `soundcraft-mobile-debug-apk` artifact from the workflow run.

## Development status

Samsung DeX support is a design target and has not yet been validated on physical DeX hardware. The app is an early prototype, not a functioning digital audio workstation.
