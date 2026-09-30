# Developer Guide

Welcome to the developer guide for Al Quran Quotes. The app shows one ayah from the Quran each day, with the Arabic text, an English translation, and the surah and ayah reference. It works fully offline and is kept small.

This guide is for developers who are new to the project. It explains the big picture and the reasons behind the main decisions. The code itself is the reference for the details, so each page points you to the files to read next.

## Where the rules live

`CLAUDE.md` in the repository root is the source of truth for project rules: architecture, naming, testing, and commits. These pages explain those rules in plain words. If a page and `CLAUDE.md` ever disagree, `CLAUDE.md` wins, and the page should be fixed.

`README.md` holds the feature tracker (Done, In progress, Planned).

## Pages

- [Setup](Developer-Setup.md): install the tools, open the project, and build, run, and test the app.
- [Project Structure](Developer-Project-Structure.md): the packages, what lives in each one, and the three source sets.
- [Architecture](Developer-Architecture.md): the MVVM layers, the Hilt modules, and how an ayah travels from the asset to the screen.
- [Ayah Data](Developer-Ayah-Data.md): the bundled `ayahs.json`, its version, seeding rules, and how to update the data safely.
- [Daily Ayah Logic](Developer-Daily-Ayah-Logic.md): how "today" is computed, how the daily ayah is chosen, and how the screen moves to a new day.
- [Testing and Coverage](Developer-Testing-And-Coverage.md): the TDD rule, test types per layer, fakes, and the unit and device coverage reports.
- [CI and Release](Developer-CI-And-Release.md): the GitHub Actions jobs, reports, wiki publishing, and the R8 release build.
- [Contributing](Developer-Contributing.md): branches, commits, and the checklist every feature or fix must pass.

## A quick tour

If you only have ten minutes, read these files in order:

1. `app/src/main/java/shibbir/me/alquranquotes/MainActivity.kt`: the only activity. It shows the daily quote screen.
2. `app/src/main/java/shibbir/me/alquranquotes/feature/dailyquote/DailyQuoteScreen.kt`: the route and the stateless screen.
3. `app/src/main/java/shibbir/me/alquranquotes/feature/dailyquote/DailyQuoteViewModel.kt`: loads the ayah and reacts to day changes.
4. `app/src/main/java/shibbir/me/alquranquotes/data/repository/OfflineAyahRepository.kt`: seeds Room from the asset and reads the daily ayah.
5. `app/src/main/java/shibbir/me/alquranquotes/data/local/AyahDao.kt`: the Room queries.

## About these pages

These pages live in the `docs/` folder of the repository and are published to the GitHub wiki automatically. See [CI and Release](Developer-CI-And-Release.md) for how that works. Every feature or fix pull request must update the pages it affects.

There is also a user guide for people who use the app. Its pages start with `User-`.

[Back to Home](Home.md) | [Developer Guide](Developer-Guide.md)
