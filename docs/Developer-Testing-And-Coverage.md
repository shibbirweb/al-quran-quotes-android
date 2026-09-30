# Developer Testing and Coverage

Tests are not optional here. Every feature is built test first, and the JVM unit tests must cover 100% of the lines and branches they can reach. This page explains how the tests are organized, how to run them, and how to read the coverage reports.

## The TDD rule

For every feature or fix:

1. Write a failing test first.
2. Write the minimum code that makes it pass.
3. Refactor while the tests stay green.

Do not write feature code without its tests.

## Test types per layer

| Layer | Test type | Where | Example |
| --- | --- | --- | --- |
| ViewModels and use cases | JVM unit tests with `kotlinx-coroutines-test` (`runTest`, a test dispatcher set as Main) and fake repositories | `app/src/test` | `DailyQuoteViewModelTest` |
| Repositories | JVM unit tests with fake DAOs and data sources | `app/src/test` | `OfflineAyahRepositoryTest` |
| Pure functions and parsers | JVM unit tests | `app/src/test` | `EpochDayTest`, `AyahJsonParserTest` |
| Room DAOs | Instrumented tests against an in-memory database | `app/src/androidTest` | `AyahDaoTest` |
| Compose screens | UI tests with `createComposeRule()` against the stateless screen | `app/src/androidTest` | `DailyQuoteScreenTest` |
| Android-bound classes | Instrumented tests | `app/src/androidTest` | `SystemDayChangeSourceTest`, `AlQuranQuotesThemeTest` |
| Whole app | One smoke test with the real Hilt graph, Room file, and asset | `app/src/androidTest` | `MainActivityTest` |

`MainActivityTest` must hold **exactly one test**. It runs the real Hilt graph, whose singleton database stays open for the whole test process. A second test would delete the database file under that open singleton.

## Fakes, not mocks

The project uses hand-written fakes, not a mocking library. Fakes are small classes that implement the same interface as the real thing and let a test control it. Some useful controls:

- `FakeAyahRepository.responseGate`: holds a call suspended, so a test can see the `Loading` state.
- `FakeAyahSeedSource.loadGate` and `nextLoadFailure`: start several callers during one load, or make one load fail.
- `MainDispatcherRule`: sets a test dispatcher as Main. It uses `UnconfinedTestDispatcher` by default, and you can pass a `StandardTestDispatcher` when the order of resumption matters.

See [Project Structure](Developer-Project-Structure.md) for the full list and where each fake lives. Reuse them before writing new ones. Plain unit tests build classes directly with fakes and do not use Hilt.

## Running tests

```bash
./gradlew testDebugUnitTest            # all JVM unit tests
./gradlew connectedDebugAndroidTest    # all instrumented tests (device or emulator)

# one unit test class, or one method
./gradlew testDebugUnitTest --tests "shibbir.me.alquranquotes.feature.dailyquote.DailyQuoteViewModelTest"
./gradlew testDebugUnitTest --tests "shibbir.me.alquranquotes.feature.dailyquote.DailyQuoteViewModelTest.showsErrorWhenLoadingFails"

# one instrumented test class
./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=shibbir.me.alquranquotes.data.local.AyahDaoTest
```

You can also run any test from Android Studio with the green arrow next to it.

## Unit test coverage with Kover

Kover measures JVM unit-test coverage. The rule lives in the `kover` block of `app/build.gradle.kts`.

```bash
./gradlew koverVerifyDebug       # runs the unit tests, fails below 100% line or branch coverage
./gradlew koverHtmlReportDebug   # writes the HTML report
```

Open `app/build/reports/kover/htmlDebug/index.html` in a browser. Click into a package and a class to see each line. Red lines never ran. Yellow lines are branches where only some paths ran. Your goal is no red and no yellow.

## What Kover excludes, and why

Some code cannot run on the plain JVM, or is not ours to test. The full, authoritative list is in `CLAUDE.md`. In short:

- **Generated code:** Hilt and Dagger factories and injectors, Room `_Impl` classes, and Compose singletons.
- **Android-bound code covered by device tests instead:** `@Composable` functions, `@dagger.Module` classes, `MainActivity`, `AlQuranQuotesApp`, `QuranDatabase`, `AssetAyahSeedSource`, `SystemDayChangeSource`, and the `ui.theme` package.

`@Preview` composables are development tools that never ship, so they need no tests.

**Never add an exclusion just to reach 100%.** Make the code testable instead (inject the dependency, remove an impossible branch) or write the missing test. A new Android-bound class may be excluded only if an instrumented test covers it, and the exclusion must be listed in `CLAUDE.md` in the same pull request.

## Device test coverage with JaCoCo

The debug build type sets `enableAndroidTestCoverage = true`, which turns on AGP's built-in JaCoCo support.

```bash
./gradlew createDebugCoverageReport
```

This runs the instrumented tests on a connected device or emulator, then writes a report to `app/build/reports/coverage/androidTest/debug/connected/index.html`.

This report measures the code Kover excludes: composables, `MainActivity`, the Room database, `SystemDayChangeSource`, and `AssetAyahSeedSource`. It is **reported, not enforced**, for now. Use it to check that a new Android-bound class really is exercised by a device test. The report reads the same way as the Kover one: open a package, then a class, and look for lines that never ran.

## Before you call a feature done

- `./gradlew koverVerifyDebug` passes (unit tests green and 100% coverage).
- `./gradlew lintDebug` passes.
- The instrumented tests pass on a device or emulator.

[Back to the Developer Guide](Developer-Guide.md)
