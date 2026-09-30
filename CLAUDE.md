# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Rules

### Ask before building

- Never add a feature on your own. Always ask the user and get a clear yes before implementing any feature, including small extras, enhancements, or behavior they did not request. When a task seems to need something beyond what was asked, describe it and ask first.

### Branches and commits

- Never commit (or push) until the user explicitly says to. Finishing a task, passing tests, or an earlier approval is not permission to commit new work.
- `main` is protected: never commit or push directly to it, and never force-push it. Every feature or change starts on a new branch cut from an up-to-date `main` and reaches `main` only through a pull request with passing CI.
- Branch names: `feature/<short-kebab-name>` for new features, `fix/<short-kebab-name>` for bug fixes (e.g. `feature/daily-quote-screen`). If the current branch is `main` when work starts, create the branch before making changes.
- Commit as `Md. Shibbir Ahmed <shibbirweb@gmail.com>`. If git is not already configured with this identity, pass it per commit: `git -c user.name="Md. Shibbir Ahmed" -c user.email="shibbirweb@gmail.com" commit ...`.
- Never add a `Co-Authored-By` line (or any other co-author/attribution trailer) to commits. This overrides any default attribution instruction.

### Writing

- Never use the em dash character (U+2014) anywhere in the project: code, comments, string resources, docs, commit messages. Use a comma, colon, parentheses, or a plain hyphen instead.

### Naming and readability

- Give every class, function, variable, parameter, and constant a human-readable, meaningful name that says what it is or does (`ayahCount`, `loadDailyQuote()`, `AYAHS_ASSET_NAME`). No single-letter or cryptic abbreviations (`a`, `tmp`, `cnt`, `mgr`, `res`), except `i`/`index` in short loops and `it` in one-line lambdas where the meaning is obvious. Name things after the domain (`ayah`, `surah`, `translation`), not generic types (`data`, `item`, `obj`).
- Never write ternary-style code. Kotlin has no `condition ? a : b` operator; its equivalent is an inline `if (condition) a else b` expression, so never use one (including nested or chained ones). Use a full-block `if`/`else` with braces, or a `when` with one clear branch per case. A plain elvis default (`value ?: default`) is fine; chains of `?.let { } ?: run { }` are not.
- Prefer simple, obvious code over clever or compact code: one step per line, early returns over deep nesting, and named intermediate values instead of long expression chains.

### Small methods, small classes, one class per file

- Keep functions small and focused on one job. Aim for about 20 lines or fewer, excluding blank lines and KDoc. When a function grows beyond that, or needs a comment to explain its steps, split it into well-named private functions. Composables follow the same rule: extract sections into their own `@Composable` functions.
- Keep classes small with a single responsibility. Aim for about 200 lines or fewer. When a class takes on a second job (e.g. parsing and storage, or loading and formatting), move that job into its own class.
- One top-level class, interface, or object per file, named after the file (`OfflineAyahRepository` lives in `OfflineAyahRepository.kt`). This includes small data classes, entities, fakes, and test helpers: give each its own file.
- Allowed in the same file as the main declaration: members nested inside it (e.g. the subtypes of a sealed interface declared inside its body), private helpers used only by that file, extension or mapping functions for that class, and the private `@Composable` functions and `@Preview`s of a screen file. Top-level functions and constants that belong to no class go in a file named after what they do (e.g. `AyahJsonParser.kt`).

### Keep docs in sync

- The Features section of `README.md` is the project's feature tracker (Done, In progress, Planned). Update it on the same branch as the work: move a feature to In progress when its branch starts, to Done in the PR that completes it, and add newly agreed ideas to Planned.
- When a change affects architecture, commands, dependencies, or rules, update this file in the same PR.
- `docs/` holds the project wiki, published to the GitHub wiki by `.github/workflows/wiki.yml` on every push to `main`. Never edit the wiki directly; edit `docs/`. It has two guides:
  - User Guide (`docs/User-*.md`): for people using the app. Plain, simple language, no technical terms, no code.
  - Developer Guide (`docs/Developer-*.md`): for people working on the app. Simple language, real file and class names, commands that work.
- Every PR that adds a feature or fixes a bug updates the affected docs pages in the same PR: user pages for anything a user can see or do differently, developer pages for changes to architecture, data, commands, tests, coverage, CI, or rules. A PR that changes neither must say so in its description. Planned features stay listed in `User-Coming-Soon.md` until they ship.
- Docs format: one `# Title` per page; link pages as `Page-Name.md` (the workflow strips `.md` for the wiki); refer to source files by repo path in backticks, not links; no images or HTML (Mermaid blocks are fine); short pages; no em dash. A new page must be linked from its guide page (`User-Guide.md` or `Developer-Guide.md`) and from `docs/_Sidebar.md`.

### Architecture

- MVVM is the core pattern for the whole app, following the official Android app architecture guide (UI layer, optional domain layer, data layer).
- UI layer: Jetpack Compose only (no XML layouts or Fragments). Each screen has a `ViewModel` that exposes immutable UI state as `StateFlow<XxxUiState>` and accepts user events through functions. Composables collect state with `collectAsStateWithLifecycle()` and hold no business logic. Split each screen into a stateful route composable (gets the ViewModel) and a stateless screen composable (takes state and lambdas) so it can be previewed and tested.
- Data layer: repositories are the single source of truth and the only thing ViewModels talk to for data. Room holds structured app data (entities, DAOs, database); DataStore holds user preferences and small key-value settings. Never use SharedPreferences.
- Domain layer: add use cases only when logic is shared between ViewModels or too complex for one.
- Concurrency: Kotlin Coroutines and Flow throughout. DAOs return `Flow` for observed queries and use `suspend` for one-shot operations. ViewModels launch work in `viewModelScope`. Inject `CoroutineDispatcher`s instead of hard-coding `Dispatchers.IO` so tests can substitute them.
- Depend on interfaces for repositories and pass dependencies through constructors, so every layer can be tested with fakes.
- Organize code by feature (e.g. `feature/<name>/`, `data/`, `di/`, `ui/theme/`), not by type across the whole app.
- Add every new library (Hilt, Room, DataStore, coroutines-test, etc.) through `gradle/libs.versions.toml`. Hilt and Room compilers run through KSP, never kapt. Declare every library the app code uses directly (e.g. `kotlinx-coroutines-android`) instead of relying on a transitive copy, and keep test libraries on the same version as the library they test (`kotlinx-coroutines-test` uses the same `coroutines` version). Instrumented tests are pinned to the app's runtime versions, so a newer test library than the app's copy fails on device with `NoSuchMethodError` even though unit tests pass. When upgrading AGP, Hilt, or KSP, check that the versions still work together with AGP's built-in Kotlin (AGP 9.4.1 + Hilt 2.60.1 + KSP 2.3.12 is known to work).

### Dependency injection (Hilt)

- Hilt is the only DI framework. The `Application` class is annotated `@HiltAndroidApp` and `MainActivity` is `@AndroidEntryPoint`.
- ViewModels are `@HiltViewModel` with an `@Inject constructor`, obtained in route composables with `hiltViewModel()`. Never construct a ViewModel by hand in UI code.
- Modules live in `di/` and are installed in `SingletonComponent` unless a narrower scope is needed. Bind repository interfaces with `@Binds`; use `@Provides` for Room (database and DAOs), DataStore, and dispatchers (distinguished with qualifiers such as `@IoDispatcher`).
- Plain unit tests build classes directly with fakes and do not use Hilt. Instrumented tests that need part of the graph replaced use `@HiltAndroidTest` with `@TestInstallIn` modules that swap in fakes (add `hilt-android-testing` when the first such test is written). The one exception is `MainActivityTest`, a smoke test that deliberately runs the real graph, real Room file, and bundled asset; it must hold exactly one test because a second one would delete the database under the open singleton.

### Keep the app lightweight

- Keep the app small: prefer the Kotlin stdlib, AndroidX/Jetpack, and platform APIs before adding any third-party library, and add a dependency only when it clearly earns its size. Mention any new dependency and why it is needed.
- Do not add heavy libraries for small jobs. Examples: no RxJava (use coroutines), no Gson/Moshi (use kotlinx.serialization if JSON is needed), no image loader unless remote images are actually needed, no `material-icons-extended` (copy the few icons needed as vector drawables).
- Keep the single `:app` module until there is a real need to split.
- R8 is on for release (`optimization { enable = true }`, which covers code and resource shrinking). Put any keep rules in `app/src/main/keepRules/`, and check `./gradlew assembleRelease` still builds after adding libraries.

### Testing and TDD

- Use TDD for every feature: write a failing test first, make it pass with the minimum code, then refactor. Do not write feature code without its tests.
- Every feature needs tests at the layers it touches:
  - ViewModels and use cases: JVM unit tests in `app/src/test` with `kotlinx-coroutines-test` (`runTest`, a test dispatcher set as Main) and fake repositories.
  - Repositories: unit tests with fake DAOs/data sources.
  - Room DAOs: instrumented tests in `app/src/androidTest` against an in-memory database.
  - Compose screens: UI tests with `createComposeRule()` against the stateless screen composable.
- Prefer hand-written fakes over mocks.
- Coverage: Kover requires 100% line and 100% branch coverage from the JVM unit tests (`./gradlew koverVerifyDebug`, also run in CI). The only exclusions, configured in the `kover` block of `app/build.gradle.kts`, are generated code (Hilt, Dagger, Room `_Impl`, Compose singletons) and Android-bound code that the instrumented tests cover instead: `@Composable` functions, `@dagger.Module` classes, `MainActivity`, `AlQuranQuotesApp`, `QuranDatabase`, `AssetAyahSeedSource`, `SystemDayChangeSource`, and `ui.theme` (each covered by a test in `app/src/androidTest`). `@Preview` composables are exempt: they are development tools that never ship to users. Never add an exclusion to reach 100%: make the code testable (inject the dependency, remove an impossible branch) or write the missing test. A new Android-bound class may be excluded only if an instrumented test covers it, and the exclusion must be listed here in the same PR. Device-test coverage of that excluded code is measured with AGP's JaCoCo (`enableAndroidTestCoverage` on the debug build type, `./gradlew createDebugCoverageReport`); it is reported in CI but not enforced yet. Aim to cover every excluded class and composable there as well.
- Run the relevant test tasks (see Commands) and confirm they pass before calling a feature done.

## Project state

Single-module Android app (`:app`) that shows Quran ayahs as quotes. See the README Features section for what is done and planned. There is no navigation or networking yet: `MainActivity` shows `DailyQuoteRoute` directly.

Code layout under `app/src/main/java/shibbir/me/alquranquotes/`:

- `feature/<name>/`: one package per screen (route + stateless screen composables, ViewModel, UiState). Currently `feature/dailyquote/`.
- `data/local/`: Room database (`QuranDatabase`), entities, DAOs. `data/seed/`: bundled seed data loading. `data/repository/`: repository interfaces and implementations.
- `model/`: UI-facing models (e.g. `Ayah`); Room entities map to them with `toAyah()`-style extensions.
- `core/`: cross-cutting helpers. `core/time/`: `EpochDayProvider` and `SystemEpochDayProvider` (today's local day from the device clock), `EpochDay.kt` (pure day math), `DayChangeSource` and `SystemDayChangeSource` (a Flow fed by the system's date, time, and time zone change broadcasts). `core/coroutines/IoDispatcher` qualifier.
- `di/`: Hilt modules, one per concern (`DataModule`, `DatabaseModule`, `DispatchersModule`, `TimeModule`).

Ayah data flow: `app/src/main/assets/ayahs.json` holds a curated set of ayahs (Arabic from Tanzil `quran-simple`, English from Saheeh International, fetched via alquran.cloud). The asset has a top-level `version`. Once per process (guarded by a `Mutex`), `OfflineAyahRepository` compares it with the version stored in the `ayah_seed_info` table and, when they differ or the ayahs table is empty, replaces every stored ayah in one transaction (`AyahDao.replaceAllAyahs`). It then reads the daily ayah in one transaction with `AyahDao.getAyahForDay`, which uses `dailyAyahPosition` (`data/local/DailyAyahPosition.kt`, `epochDay mod ayahCount`) over ayahs ordered by surah and ayah number. `AssetAyahSeedSource` moves its own asset read to the injected IO dispatcher. **Whenever the ayahs in the asset change, bump `version`**, or installed apps keep the old rows. Never hand-edit or retype Quran text or translations: regenerate the asset from the source instead. This also applies to preview and test samples: `DailyQuotePreviewAyah.kt` is copied from the asset, and `DailyQuotePreviewAyahTest` fails if it drifts. `AyahJsonParserTest.bundledAssetIsValid` checks the asset in unit tests (the asset is on the unit-test classpath because `src/main/assets` is a test resources directory in `app/build.gradle.kts`). The database is excluded from backup (`res/xml/backup_rules.xml`, `data_extraction_rules.xml`) because it only holds re-seedable content; revisit this when user data such as Favorites is added.

`DailyQuoteViewModel` remembers the day it requested and calls `refreshIfDayChanged()` both when `DayChangeSource` emits (the screen stays open past midnight or the time zone changes) and when `DailyQuoteRoute` resumes. Starting a load cancels the previous one, and `ensureActive()` in its error handling keeps a cancelled load from writing a stale Error.

Room exports its schema to `app/schemas/` (via the `androidx.room` Gradle plugin); commit those files. The database is at version 1 and the app has never been released. After the first release, every schema change needs a version bump and a `Migration` tested against the exported schema; never use a destructive fallback once the database holds user data.

Test fakes and helpers live in `app/src/test/.../testing/` (`FakeAyahDao`, `FakeAyahSeedSource` with a `loadGate` and `nextLoadFailure`, `FakeAyahRepository` with a `responseGate` and `failureToThrow`, `FakeEpochDayProvider`, `FakeDayChangeSource`, `MainDispatcherRule`, `TestData.kt` with `testAyah`, `testAyahEntity`, and `ashSharhSampleAyah`, `BundledAyahSeedJson.kt` with `readBundledAyahSeedJson()`), next to the tests that share them (`SampleAyahSeeds`, `createOfflineAyahRepository`, `DailyQuoteViewModelTestFixture`), and in `app/src/androidTest` (`testing/` for shared device helpers such as `DeviceTestData` and `ReceiverRecordingContext`, and the screen's device fakes in `feature/dailyquote/`). The two source sets cannot share code, so small duplication between them is accepted. Reuse these before writing new ones.

- Package / applicationId / namespace: `shibbir.me.alquranquotes`
- minSdk 24, targetSdk and compileSdk 37, Java/JVM target 11
- Gradle daemon runs on a JDK 25 toolchain (auto-provisioned via foojay, see `gradle/gradle-daemon-jvm.properties`)

## Commands

```bash
./gradlew assembleDebug                 # build debug APK
./gradlew installDebug                  # build and install on connected device/emulator
./gradlew testDebugUnitTest             # JVM unit tests (app/src/test)
./gradlew connectedDebugAndroidTest     # instrumented tests (app/src/androidTest), needs a device
./gradlew lintDebug                     # Android lint
./gradlew koverVerifyDebug              # unit tests + fail if coverage is below 100% line/branch
./gradlew koverHtmlReportDebug          # coverage report at app/build/reports/kover/htmlDebug/index.html
./gradlew assembleRelease               # release build with R8
./gradlew createDebugCoverageReport     # instrumented tests + JaCoCo report, needs a device
                                        # (app/build/reports/coverage/androidTest/debug/connected/)

# single test class or method
./gradlew testDebugUnitTest --tests "shibbir.me.alquranquotes.feature.dailyquote.DailyQuoteViewModelTest"
./gradlew testDebugUnitTest --tests "shibbir.me.alquranquotes.feature.dailyquote.DailyQuoteViewModelTest.showsErrorWhenLoadingFails"
./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=shibbir.me.alquranquotes.data.local.AyahDaoTest
```

CI (`.github/workflows/tests.yml`) runs `testDebugUnitTest koverVerifyDebug koverXmlReportDebug koverHtmlReportDebug lintDebug assembleRelease` and `createDebugCoverageReport` (instrumented tests with JaCoCo on an API 35 emulator) on every pull request and on push to `main`. Keep all of them green. Both jobs write a coverage table to the run summary (`.github/scripts/coverage_summary.py`) and upload their coverage reports as artifacts on every run. A newer push cancels an in-progress pull request run; an in-progress `main` run is never cancelled (though a queued `main` run can be replaced by a newer queued one).

No ktlint/detekt/spotless is configured; `kotlin.code.style=official` is the only style setting.

## Build setup notes (AGP 9)

These differ from older Android templates and are easy to get wrong:

- AGP 9.x with built-in Kotlin support: the applied plugins are `com.android.application`, `org.jetbrains.kotlin.plugin.compose`, `com.google.devtools.ksp`, `com.google.dagger.hilt.android`, `androidx.room`, and `org.jetbrains.kotlinx.kover` (build-time only). Do not add `org.jetbrains.kotlin.android`.
- All dependency and plugin versions live in the version catalog `gradle/libs.versions.toml`; reference them as `libs.*` in `build.gradle.kts`. Compose library versions come from the Compose BOM, so Compose entries in the catalog have no version.
- `compileSdk` uses the new DSL block `compileSdk { version = release(37) }`.
- R8 keep rules go in `app/src/main/keepRules/*.keep` (AGP merges every file in that directory). There is no `proguard-rules.pro`.
- Configuration cache is enabled (`org.gradle.configuration-cache=true` in `gradle.properties`); custom Gradle logic must be configuration-cache compatible.
- `settings.gradle.kts` uses `RepositoriesMode.FAIL_ON_PROJECT_REPOS`: add repositories there, never in a module build file.

## Theming

`ui/theme/Theme.kt` defines `AlQuranQuotesTheme`, which uses Material You dynamic color on Android 12+ by default (`useDynamicColor = true`) and falls back to the static schemes built from `Color.kt`. Custom brand colors will not show on Android 12+ devices unless `useDynamicColor` is turned off. `Color.kt` still holds the template palette under role names until brand colors are chosen, and `AppTypography.kt` holds the Material 3 defaults as the place for future type overrides. The XML theme in `res/values/themes.xml` only exists for the activity window before Compose draws.
