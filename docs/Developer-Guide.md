# Developer Guide

Welcome to the developer guide for Al Quran Quotes. The Home tab shows one quote each day: an ayah from the Quran (Arabic text, English translation, and the surah and ayah reference), or one of the quotes the user added. The Quotes tab lists every quote and lets the user add quotes and edit or delete any quote, including the bundled ayahs. It works fully offline and is kept small.

This guide is for developers who are new to the project. It explains the big picture and the reasons behind the main decisions. The code itself is the reference for the details, so each page points you to the files to read next.

## Where the rules live

`CLAUDE.md` in the repository root is the source of truth for project rules: architecture, naming, testing, and commits. These pages explain those rules in plain words. If a page and `CLAUDE.md` ever disagree, `CLAUDE.md` wins, and the page should be fixed.

`README.md` holds the feature tracker (Done, In progress, Planned).

## Pages

- [Setup](Developer-Setup.md): install the tools, open the project, and build, run, and test the app.
- [Project Structure](Developer-Project-Structure.md): the packages, what lives in each one, and the three source sets.
- [Architecture](Developer-Architecture.md): the MVVM layers, the Hilt modules, the destinations, how a quote travels from the database to the screen, and the native Material 3 UI.
- [Navigation](Developer-Navigation.md): the type-safe routes, the bottom tabs, tab navigation options, when the bottom bar shows, the top app bars, window insets, and screen transitions.
- [Ayah Data](Developer-Ayah-Data.md): the bundled `ayahs.json`, the single `quotes` table, the seed merge rules, the version 2 schema and its hand-written migration, and backups.
- [Daily Ayah Logic](Developer-Daily-Ayah-Logic.md): how "today" is computed, how the daily quote is chosen from every quote in one order, and how the screen moves to a new day.
- [Quotes and Editor](Developer-Quotes-And-Editor.md): the Quotes list, origin labels, the delete confirmation, the add and edit screen, the update rules, and the validation rules.
- [Testing and Coverage](Developer-Testing-And-Coverage.md): the TDD rule, test types per layer, fakes, and the unit and device coverage reports.
- [CI and Release](Developer-CI-And-Release.md): the GitHub Actions jobs, reports, wiki publishing, and the R8 release build.
- [Contributing](Developer-Contributing.md): branches, commits, and the checklist every feature or fix must pass.

## A quick tour

If you only have ten minutes, read these files in order:

1. `app/src/main/java/shibbir/me/alquranquotes/MainActivity.kt`: the only activity. It shows `QuranQuotesAppShell`.
2. `app/src/main/java/shibbir/me/alquranquotes/navigation/AppNavHost.kt`: every destination and the screen it shows.
3. `app/src/main/java/shibbir/me/alquranquotes/feature/dailyquote/DailyQuoteViewModel.kt`: loads the daily quote and reacts to day changes.
4. `app/src/main/java/shibbir/me/alquranquotes/feature/quoteeditor/QuoteEditorViewModel.kt`: adds a quote, or edits any quote.
5. `app/src/main/java/shibbir/me/alquranquotes/data/seed/BundledAyahSeeder.kt` and `data/local/BundledAyahSeedDao.kt`: merge the bundled ayahs into Room without undoing the user's changes.
6. `app/src/main/java/shibbir/me/alquranquotes/data/local/DailyQuoteDao.kt`: the daily lookup over the `quotes` table.

## About these pages

These pages live in the `docs/` folder of the repository and are published to the GitHub wiki automatically. See [CI and Release](Developer-CI-And-Release.md) for how that works. Every feature or fix pull request must update the pages it affects.

There is also a user guide for people who use the app. Its pages start with `User-`.

[Back to Home](Home.md) | [Developer Guide](Developer-Guide.md)
