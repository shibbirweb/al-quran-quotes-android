# Developer Setup

This page gets you from a fresh machine to a running app and a green test run.

## Tools you need

- **Android Studio** (a recent stable version) with the Android SDK. The app compiles against SDK 37, and Android Studio will offer to download it on first sync.
- **A JDK to start Gradle.** Any recent JDK works to launch the Gradle wrapper. Android Studio ships one.
- **A device or emulator** for running the app and the instrumented tests. The app supports Android 7.0 (API 24) and newer. CI uses an API 35 emulator.

You do not need to install JDK 25 by hand. The Gradle daemon runs on a JDK 25 toolchain, and Gradle downloads it for you through the foojay resolver. The settings are in `gradle/gradle-daemon-jvm.properties` and the `foojay-resolver-convention` plugin in `settings.gradle.kts`. The app code itself targets Java 11.

## Clone and open

```bash
git clone https://github.com/shibbirweb/al-quran-quotes-android.git
cd al-quran-quotes-android
```

Then in Android Studio choose **Open** and pick the project folder. Let the Gradle sync finish. The first sync downloads the JDK toolchain, the Android Gradle Plugin, and all libraries, so it can take a few minutes.

## Run the app

Pick a device or emulator in Android Studio and press **Run**, or use the command line:

```bash
./gradlew installDebug
```

You should see the "ayah of the day" screen with Arabic text, a translation, and a reference.

## Build and test commands

```bash
./gradlew assembleDebug               # build the debug APK
./gradlew installDebug                # build and install on a connected device or emulator
./gradlew testDebugUnitTest           # JVM unit tests (app/src/test)
./gradlew koverVerifyDebug            # unit tests, then fail below 100% line or branch coverage
./gradlew koverHtmlReportDebug        # unit coverage report (HTML)
./gradlew lintDebug                   # Android lint
./gradlew assembleRelease             # release build with R8
./gradlew connectedDebugAndroidTest   # instrumented tests, needs a device or emulator
./gradlew createDebugCoverageReport   # instrumented tests plus a JaCoCo report
```

See [Testing and Coverage](Developer-Testing-And-Coverage.md) for running a single test and reading the reports.

## Build setup notes

The project uses AGP 9 and a few settings that differ from older Android templates:

- All library and plugin versions live in `gradle/libs.versions.toml`. Build files refer to them as `libs.*`.
- AGP 9 has built-in Kotlin support. Do not add the `org.jetbrains.kotlin.android` plugin.
- Hilt and Room code generation runs through KSP, never kapt.
- Repositories are declared only in `settings.gradle.kts` (`RepositoriesMode.FAIL_ON_PROJECT_REPOS`).
- The Gradle configuration cache is on (`gradle.properties`), so custom Gradle logic must support it.
- There is no ktlint, detekt, or spotless. `kotlin.code.style=official` is the only style setting.

## Uninstall old builds after schema changes

The Room database is at version 1 and the app has never been released. While that is true, a developer may change the schema without adding a migration. An old debug build on your device still has the old database file, and Room will crash when it opens it.

If you change an entity or pull a branch that did, uninstall the app from your device (or clear its storage) before running it again:

```bash
adb uninstall shibbir.me.alquranquotes
```

After the first release this shortcut is no longer allowed. Every schema change will then need a version bump and a tested `Migration`. See [Ayah Data](Developer-Ayah-Data.md).

## Troubleshooting

- **Sync fails while downloading the JDK:** check your network or proxy, then sync again. Gradle needs to reach `api.foojay.io` the first time.
- **Instrumented tests fail with `NoSuchMethodError`:** a test library is newer than the copy the app uses at runtime. Keep test libraries on the same version as the library they test.

[Back to the Developer Guide](Developer-Guide.md)
