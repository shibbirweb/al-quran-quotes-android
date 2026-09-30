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

You should see the Home tab with the "Ayah of the day" card (Arabic text, a translation, and a reference) and a bottom navigation bar with Home, Quotes, and Settings.

## Build and test commands

```bash
./gradlew assembleDebug               # build the debug APK
./gradlew installDebug                # build and install on a connected device or emulator
./gradlew testDebugUnitTest           # JVM unit tests (app/src/test)
./gradlew koverVerifyDebug            # unit tests, then fail below 100% line or branch coverage
./gradlew koverHtmlReportDebug        # unit coverage report (HTML)
./gradlew lintDebug                   # Android lint
./gradlew assembleRelease             # release build with R8
./gradlew assembleDebugAndroidTest    # compile the instrumented tests, no device needed
./gradlew connectedDebugAndroidTest   # instrumented tests, needs a device or emulator
./gradlew createDebugCoverageReport -PdeviceTestCoverage   # instrumented tests plus a JaCoCo report
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

## Database version 2 and schema changes

The Room database is at **version 2**. Version 2 keeps every quote, bundled or the user's own, in one `quotes` table and replaces the version 1 `ayahs` table. Room moves a version 1 database from `main` to version 2 with the hand-written `MigrationOneToTwo`, and the seeder then stores the bundled ayahs again (see [Ayah Data](Developer-Ayah-Data.md)). A normal install over the version 1 app from `main` works.

Database changes reach installed apps automatically: every schema change bumps the version and adds a migration, usually a Room `@AutoMigration`, and Room upgrades the database the next time the app opens it. `QuranDatabaseSchemaHistoryTest` fails if an entity changes without a version bump, so a build whose schema no longer matches its version never reaches a device. See [Ayah Data](Developer-Ayah-Data.md).

One old case remains. Debug builds of the quote CRUD branch made before that check existed could store a different version 2 schema (with `ayahs` and `user_quotes` tables). Room cannot open that database and the app crashes on start. If you have such a build installed, clear the app's storage once (this deletes the quotes you added and the edits you made on that device):

```bash
adb shell pm clear shibbir.me.alquranquotes
```

Uninstalling is not a way around a schema change. The database holds user data, so a destructive fallback is never allowed.

## Troubleshooting

- **Sync fails while downloading the JDK:** check your network or proxy, then sync again. Gradle needs to reach `api.foojay.io` the first time.
- **The app crashes with "Room cannot verify the data integrity":** a database from an old pre-release build is on the device. Clear the app's storage once (see above). Uninstalling may not be enough: the phone can restore the old database from a backup, or the uninstall can reach another profile such as a Secure Folder instead.
- **Instrumented tests fail with `NoSuchMethodError` or `AbstractMethodError`:** a test library, or something it brings along, is newer than the copy the app uses at runtime. Keep test libraries on the same version as the library they test. For example, `room-testing` 2.8.5 brings `kotlinx-serialization-json` 1.8.1, so `kotlinxSerialization` in `gradle/libs.versions.toml` is 1.8.1; at 1.7.3 `QuranDatabaseMigrationTest` failed with `AbstractMethodError`. `./gradlew :app:dependencies --configuration debugAndroidTestRuntimeClasspath` shows the versions the device tests get.

[Back to the Developer Guide](Developer-Guide.md)
