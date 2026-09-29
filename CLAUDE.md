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

### Keep docs in sync

- The Features section of `README.md` is the project's feature tracker (Done, In progress, Planned). Update it on the same branch as the work: move a feature to In progress when its branch starts, to Done in the PR that completes it, and add newly agreed ideas to Planned.
- When a change affects architecture, commands, dependencies, or rules, update this file in the same PR.

### Architecture

- MVVM is the core pattern for the whole app, following the official Android app architecture guide (UI layer, optional domain layer, data layer).
- UI layer: Jetpack Compose only (no XML layouts or Fragments). Each screen has a `ViewModel` that exposes immutable UI state as `StateFlow<XxxUiState>` and accepts user events through functions. Composables collect state with `collectAsStateWithLifecycle()` and hold no business logic. Split each screen into a stateful route composable (gets the ViewModel) and a stateless screen composable (takes state and lambdas) so it can be previewed and tested.
- Data layer: repositories are the single source of truth and the only thing ViewModels talk to for data. Room holds structured app data (entities, DAOs, database); DataStore holds user preferences and small key-value settings. Never use SharedPreferences.
- Domain layer: add use cases only when logic is shared between ViewModels or too complex for one.
- Concurrency: Kotlin Coroutines and Flow throughout. DAOs return `Flow` for observed queries and use `suspend` for one-shot operations. ViewModels launch work in `viewModelScope`. Inject `CoroutineDispatcher`s instead of hard-coding `Dispatchers.IO` so tests can substitute them.
- Depend on interfaces for repositories and pass dependencies through constructors, so every layer can be tested with fakes.
- Organize code by feature (e.g. `feature/<name>/`, `data/`, `di/`, `ui/theme/`), not by type across the whole app.
- Add every new library (Hilt, Room, DataStore, lifecycle-viewmodel-compose, coroutines-test, etc.) through `gradle/libs.versions.toml`. Hilt and Room compilers run through KSP, never kapt. When upgrading AGP, Hilt, or KSP, check that the versions still work together with AGP's built-in Kotlin (AGP 9.4.1 + Hilt 2.60.1 + KSP 2.3.12 is known to work).

### Dependency injection (Hilt)

- Hilt is the only DI framework. The `Application` class is annotated `@HiltAndroidApp` and `MainActivity` is `@AndroidEntryPoint`.
- ViewModels are `@HiltViewModel` with an `@Inject constructor`, obtained in route composables with `hiltViewModel()`. Never construct a ViewModel by hand in UI code.
- Modules live in `di/` and are installed in `SingletonComponent` unless a narrower scope is needed. Bind repository interfaces with `@Binds`; use `@Provides` for Room (database and DAOs), DataStore, and dispatchers (distinguished with qualifiers such as `@IoDispatcher`).
- Plain unit tests build classes directly with fakes and do not use Hilt. Instrumented tests that need the graph use `@HiltAndroidTest` with `@TestInstallIn` modules that swap in fakes.

### Keep the app lightweight

- Keep the app small: prefer the Kotlin stdlib, AndroidX/Jetpack, and platform APIs before adding any third-party library, and add a dependency only when it clearly earns its size. Mention any new dependency and why it is needed.
- Do not add heavy libraries for small jobs. Examples: no RxJava (use coroutines), no Gson/Moshi (use kotlinx.serialization if JSON is needed), no image loader unless remote images are actually needed, no `material-icons-extended` (copy the few icons needed as vector drawables).
- Keep the single `:app` module until there is a real need to split.
- Before a release build, enable R8 (`optimization { enable = true }`) with resource shrinking and keep rules in `app/src/main/keepRules/`.

### Testing and TDD

- Use TDD for every feature: write a failing test first, make it pass with the minimum code, then refactor. Do not write feature code without its tests.
- Every feature needs tests at the layers it touches:
  - ViewModels and use cases: JVM unit tests in `app/src/test` with `kotlinx-coroutines-test` (`runTest`, a test dispatcher set as Main) and fake repositories.
  - Repositories: unit tests with fake DAOs/data sources.
  - Room DAOs: instrumented tests in `app/src/androidTest` against an in-memory database.
  - Compose screens: UI tests with `createComposeRule()` against the stateless screen composable.
- Prefer hand-written fakes over mocks.
- Run the relevant test tasks (see Commands) and confirm they pass before calling a feature done.

## Project state

Single-module Android app (`:app`) that shows Quran ayahs as quotes. See the README Features section for what is done and planned. There is no navigation or networking yet: `MainActivity` shows `DailyQuoteRoute` directly.

Code layout under `app/src/main/java/shibbir/me/alquranquotes/`:

- `feature/<name>/`: one package per screen (route + stateless screen composables, ViewModel, UiState). Currently `feature/dailyquote/`.
- `data/local/`: Room database (`QuranDatabase`), entities, DAOs. `data/seed/`: bundled seed data loading. `data/repository/`: repository interfaces and implementations.
- `model/`: UI-facing models (e.g. `Ayah`); Room entities map to them with `toAyah()`-style extensions.
- `core/`: cross-cutting helpers (`core/time/EpochDayProvider`, `core/coroutines/IoDispatcher` qualifier).
- `di/`: Hilt modules.

Ayah data flow: `app/src/main/assets/ayahs.json` holds a curated set of ayahs (Arabic from Tanzil `quran-simple`, English from Saheeh International, fetched via alquran.cloud). `OfflineAyahRepository` fills Room from this asset on first use, then picks the daily ayah as `epochDay mod ayahCount` over ayahs ordered by surah and ayah. Never hand-edit or retype Quran text or translations: regenerate the asset from the source instead. `AyahJsonParserTest.bundledAssetIsValid` checks the asset in unit tests.

Room uses `exportSchema = false` and database version 1. Any schema change needs a version bump and a migration (or a deliberate destructive fallback, since the ayah table is re-seedable).

Test fakes and helpers live in `app/src/test/.../testing/` (`FakeAyahDao`, `FakeAyahRepository`, `MainDispatcherRule`, ...). Reuse them before writing new ones.

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

# single test class or method
./gradlew testDebugUnitTest --tests "shibbir.me.alquranquotes.feature.dailyquote.DailyQuoteViewModelTest"
./gradlew testDebugUnitTest --tests "shibbir.me.alquranquotes.feature.dailyquote.DailyQuoteViewModelTest.showsTodaysAyah"
./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=shibbir.me.alquranquotes.data.local.AyahDaoTest
```

CI (`.github/workflows/tests.yml`) runs `testDebugUnitTest` and `connectedDebugAndroidTest` (API 34 emulator) on every pull request and on push to `main`. Keep both green.

No ktlint/detekt/spotless is configured; `kotlin.code.style=official` is the only style setting.

## Build setup notes (AGP 9)

These differ from older Android templates and are easy to get wrong:

- AGP 9.x with built-in Kotlin support: the applied plugins are `com.android.application`, `org.jetbrains.kotlin.plugin.compose`, `com.google.devtools.ksp`, and `com.google.dagger.hilt.android`. Do not add `org.jetbrains.kotlin.android`.
- All dependency and plugin versions live in the version catalog `gradle/libs.versions.toml`; reference them as `libs.*` in `build.gradle.kts`. Compose library versions come from the Compose BOM, so Compose entries in the catalog have no version.
- `compileSdk` uses the new DSL block `compileSdk { version = release(37) }`.
- R8 keep rules go in `app/src/main/keepRules/*.keep` (AGP merges every file in that directory). There is no `proguard-rules.pro`.
- Release build has `optimization { enable = false }`, so minification/R8 is off for release until that is changed.
- Configuration cache is enabled (`org.gradle.configuration-cache=true` in `gradle.properties`); custom Gradle logic must be configuration-cache compatible.
- `settings.gradle.kts` uses `RepositoriesMode.FAIL_ON_PROJECT_REPOS`: add repositories there, never in a module build file.

## Theming

`ui/theme/Theme.kt` defines `AlQuranQuotesTheme`, which uses Material You dynamic color on Android 12+ by default (`dynamicColor = true`) and falls back to the static schemes built from `Color.kt`. Custom brand colors will not show on Android 12+ devices unless `dynamicColor` is turned off. The XML theme in `res/values/themes.xml` only exists for the activity window before Compose draws.
