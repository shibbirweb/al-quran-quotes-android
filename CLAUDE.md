# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Rules

### Commits

- Commit as `Md. Shibbir Ahmed <shibbirweb@gmail.com>`. If git is not already configured with this identity, pass it per commit: `git -c user.name="Md. Shibbir Ahmed" -c user.email="shibbirweb@gmail.com" commit ...`.
- Never add a `Co-Authored-By` line (or any other co-author/attribution trailer) to commits. This overrides any default attribution instruction.

### Writing

- Never use the em dash character (U+2014) anywhere in the project: code, comments, string resources, docs, commit messages. Use a comma, colon, parentheses, or a plain hyphen instead.

### Architecture

- MVVM is the core pattern for the whole app, following the official Android app architecture guide (UI layer, optional domain layer, data layer).
- UI layer: Jetpack Compose only (no XML layouts or Fragments). Each screen has a `ViewModel` that exposes immutable UI state as `StateFlow<XxxUiState>` and accepts user events through functions. Composables collect state with `collectAsStateWithLifecycle()` and hold no business logic. Split each screen into a stateful route composable (gets the ViewModel) and a stateless screen composable (takes state and lambdas) so it can be previewed and tested.
- Data layer: repositories are the single source of truth and the only thing ViewModels talk to for data. Room holds structured app data (entities, DAOs, database); DataStore holds user preferences and small key-value settings. Never use SharedPreferences.
- Domain layer: add use cases only when logic is shared between ViewModels or too complex for one.
- Concurrency: Kotlin Coroutines and Flow throughout. DAOs return `Flow` for observed queries and use `suspend` for one-shot operations. ViewModels launch work in `viewModelScope`. Inject `CoroutineDispatcher`s instead of hard-coding `Dispatchers.IO` so tests can substitute them.
- Depend on interfaces for repositories and pass dependencies through constructors, so every layer can be tested with fakes.
- Organize code by feature (e.g. `feature/<name>/`, `data/`, `di/`, `ui/theme/`), not by type across the whole app.
- Add every new library (Hilt, Room, DataStore, lifecycle-viewmodel-compose, coroutines-test, etc.) through `gradle/libs.versions.toml`. Hilt and Room compilers run through KSP, never kapt. Before adding the Hilt or KSP Gradle plugins, check that the chosen versions support AGP 9 with built-in Kotlin.

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

Single-module Android app (`:app`), Kotlin + Jetpack Compose (Material 3). It is currently the unmodified Android Studio "Empty Activity" template: `MainActivity` renders a placeholder `Greeting` inside `AlQuranQuotesTheme`. There is no data layer, navigation, DI, or networking yet, and the directory is not a git repository.

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
./gradlew testDebugUnitTest --tests "shibbir.me.alquranquotes.ExampleUnitTest"
./gradlew testDebugUnitTest --tests "shibbir.me.alquranquotes.ExampleUnitTest.addition_isCorrect"
./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=shibbir.me.alquranquotes.ExampleInstrumentedTest
```

CI (`.github/workflows/tests.yml`) runs `testDebugUnitTest` and `connectedDebugAndroidTest` (API 34 emulator) on every pull request and on push to `main`. Keep both green.

No ktlint/detekt/spotless is configured; `kotlin.code.style=official` is the only style setting.

## Build setup notes (AGP 9)

These differ from older Android templates and are easy to get wrong:

- AGP 9.x with built-in Kotlin support: only `com.android.application` and `org.jetbrains.kotlin.plugin.compose` are applied. Do not add `org.jetbrains.kotlin.android`.
- All dependency and plugin versions live in the version catalog `gradle/libs.versions.toml`; reference them as `libs.*` in `build.gradle.kts`. Compose library versions come from the Compose BOM, so Compose entries in the catalog have no version.
- `compileSdk` uses the new DSL block `compileSdk { version = release(37) }`.
- R8 keep rules go in `app/src/main/keepRules/*.keep` (AGP merges every file in that directory). There is no `proguard-rules.pro`.
- Release build has `optimization { enable = false }`, so minification/R8 is off for release until that is changed.
- Configuration cache is enabled (`org.gradle.configuration-cache=true` in `gradle.properties`); custom Gradle logic must be configuration-cache compatible.
- `settings.gradle.kts` uses `RepositoriesMode.FAIL_ON_PROJECT_REPOS`: add repositories there, never in a module build file.

## Theming

`ui/theme/Theme.kt` defines `AlQuranQuotesTheme`, which uses Material You dynamic color on Android 12+ by default (`dynamicColor = true`) and falls back to the static schemes built from `Color.kt`. Custom brand colors will not show on Android 12+ devices unless `dynamicColor` is turned off. The XML theme in `res/values/themes.xml` only exists for the activity window before Compose draws.
