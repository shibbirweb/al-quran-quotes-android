# Al Quran Quotes

An Android app that shows ayahs from the Quran as daily quotes, with the Arabic text, a translation, and the surah and ayah reference. It works offline and is kept small and lightweight.

## Features

This section is the feature tracker. Update it in the same branch as the work (see [CLAUDE.md](CLAUDE.md)).

### Done

| Feature | Notes |
| --- | --- |
| Project foundation | Jetpack Compose + Material 3 app, GitHub Actions CI that runs unit and instrumented tests on every pull request and on `main`. |

### In progress

| Feature | Branch | Notes |
| --- | --- | --- |
| Daily quote screen | `feature/daily-quote-screen` | Shows one ayah per day (Arabic, English translation, reference), picked from a bundled set of 33 ayahs stored in Room. Works offline. Includes the MVVM, Hilt, and Room foundation the later features build on. |

### Planned

| Feature | Notes |
| --- | --- |
| Favorites | Bookmark ayahs and view the saved list (Room). |
| Share quote | Share an ayah through the Android share sheet, as text and possibly as an image card. |
| Daily reminder | A daily notification with the day's ayah at a time the user chooses. |
| Settings | Theme and Arabic font size, stored in DataStore. |
| More translation languages | English stays the default; users can add the translation languages they want. |
| Custom Arabic font | Users can upload their own font file for the Arabic text. |
| Online ayah source | Fetch more ayahs and translations online, on top of the bundled offline set. |

## Tech stack

Kotlin, Jetpack Compose (Material 3), MVVM, Kotlin Coroutines and Flow, Hilt, Room, DataStore (with Settings). Tests use JUnit 4, kotlinx-coroutines-test, and Compose UI tests, written test first (TDD).

## Build and test

Requires Android Studio (or the Android SDK) and a JDK. Gradle provisions the JDK 25 toolchain it needs.

```bash
./gradlew assembleDebug               # build the debug APK
./gradlew testDebugUnitTest           # unit tests
./gradlew connectedDebugAndroidTest   # instrumented tests, needs a device or emulator
```

## Contributing

`main` is protected. Create a `feature/<name>` or `fix/<name>` branch from `main`, open a pull request, and merge once CI passes. Project rules for architecture, testing, and commits are in [CLAUDE.md](CLAUDE.md).

## Quran text and translation

- Arabic text: [Tanzil Project](https://tanzil.net) (Simple text). The text is used verbatim and must not be modified.
- English translation: Saheeh International.
- Both were retrieved through [alquran.cloud](https://alquran.cloud).
