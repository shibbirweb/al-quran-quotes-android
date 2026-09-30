# Al Quran Quotes

An Android app that shows ayahs from the Quran as daily quotes, with the Arabic text, a translation, and the surah and ayah reference. It works offline and is kept small and lightweight.

## Features

This section is the feature tracker. Update it in the same branch as the work (see [CLAUDE.md](CLAUDE.md)).

### Done

| Feature | Notes |
| --- | --- |
| Project foundation | Jetpack Compose + Material 3 app, GitHub Actions CI that runs unit tests with a 100% coverage check (Kover), lint, the R8 release build, and instrumented tests on every pull request and on `main`. |
| Daily quote screen | Shows one ayah per day (Arabic, English translation, reference), picked from a bundled set of 33 ayahs stored in Room. Works offline, moves to the new ayah after midnight (also while the screen stays open), and updates to the bundled ayahs reach installed apps. Includes the MVVM, Hilt, and Room foundation the later features build on. Merged in PR #1. |

### In progress

| Feature | Branch | Notes |
| --- | --- | --- |
| None | | |

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

## Documentation

The [User Guide](docs/User-Guide.md) and the [Developer Guide](docs/Developer-Guide.md) live in `docs/` and are published to the project's GitHub wiki.

## Tech stack

Kotlin, Jetpack Compose (Material 3), MVVM, Kotlin Coroutines and Flow, Hilt, Room. DataStore will be added with Settings. Tests use JUnit 4, kotlinx-coroutines-test, and Compose UI tests, written test first (TDD), with Kover measuring coverage.

## Build and test

Requires Android Studio (or the Android SDK) and a JDK. Gradle provisions the JDK 25 toolchain it needs.

```bash
./gradlew assembleDebug               # build the debug APK
./gradlew testDebugUnitTest           # unit tests
./gradlew koverVerifyDebug            # unit tests + fail below 100% line and branch coverage
./gradlew koverHtmlReportDebug        # coverage report in app/build/reports/kover/htmlDebug/
./gradlew lintDebug                   # Android lint
./gradlew assembleRelease             # release build with R8
./gradlew connectedDebugAndroidTest   # instrumented tests, needs a device or emulator
./gradlew createDebugCoverageReport   # instrumented tests + device coverage report (JaCoCo)
```

## Contributing

`main` is protected. Create a `feature/<name>` or `fix/<name>` branch from `main`, open a pull request, and merge once CI passes. Project rules for architecture, testing, and commits are in [CLAUDE.md](CLAUDE.md).

## Quran text and translation

- Arabic text: [Tanzil Project](https://tanzil.net) (Simple text). The text is used verbatim and must not be modified.
- English translation: Saheeh International.
- Both were retrieved through [alquran.cloud](https://alquran.cloud).
