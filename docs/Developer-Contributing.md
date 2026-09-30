# Developer Contributing

This page lists how changes reach `main` and the checklist every feature or fix must pass. The rules come from `CLAUDE.md`, which stays the source of truth.

## Ask before building

Never add a feature on your own. This includes small extras, "while I am here" improvements, and behavior nobody asked for. If a task seems to need something beyond what was asked, describe it and get a clear yes first. New ideas that are agreed go into the Planned table of the `README.md` feature tracker.

## Branches

- `main` is protected. Never commit or push directly to it, and never force-push it.
- Every change starts on a new branch cut from an up-to-date `main`.
- Name branches `feature/<short-kebab-name>` for new features and `fix/<short-kebab-name>` for bug fixes, for example `feature/daily-quote-screen`.
- A change reaches `main` only through a pull request with passing CI. See [CI and Release](Developer-CI-And-Release.md).

```bash
git switch main
git pull
git switch -c feature/favorites
```

## Commits

- **Commit only when told to.** Finishing a task, passing tests, or an earlier approval is not permission to commit new work. Wait until the project owner says to commit.
- **Identity:** commit as `Md. Shibbir Ahmed <shibbirweb@gmail.com>`. If your git is not set up with this identity, pass it per commit:

  ```bash
  git -c user.name="Md. Shibbir Ahmed" -c user.email="shibbirweb@gmail.com" commit
  ```

- **Format:** the history uses a short type prefix and a one-line summary, such as `feat: daily quote screen with bundled ayahs and README feature tracker`. Use `feat:` for features and `fix:` for bug fixes.
- **No co-author lines.** Never add `Co-Authored-By` or any other co-author or attribution trailer.
- **No em dash** anywhere: code, comments, strings, docs, or commit messages. Use a comma, colon, parentheses, or a plain hyphen.

## Code style in short

`CLAUDE.md` has the full rules. The ones people miss most often:

- Human-readable names after the domain (`ayahCount`, not `cnt`; `ayah`, not `item`).
- No inline `if (condition) a else b` expressions. Use a full `if`/`else` block with braces or a `when`.
- Small functions (about 20 lines) and small classes (about 200 lines).
- One top-level class, interface, or object per file, named after the file.
- New libraries go through `gradle/libs.versions.toml`, and only when they clearly earn their size.

## Checklist for a feature or fix

Work through this list on your branch before you ask for a review.

1. **Agreed first.** The feature or fix was agreed with the project owner.
2. **Tracker updated at the start.** For a feature, move it to In progress in the `README.md` feature tracker when the branch starts.
3. **Tests first.** Write a failing test, make it pass, then refactor. Cover every layer the change touches. See [Testing and Coverage](Developer-Testing-And-Coverage.md).
4. **Coverage is 100%.** `./gradlew koverVerifyDebug` passes. Do not add a Kover exclusion to get there. A new Android-bound class may be excluded only if a device test covers it, and it must be listed in `CLAUDE.md`.
5. **Device tests pass.** `./gradlew connectedDebugAndroidTest` (or `createDebugCoverageReport`) is green on a device or emulator.
6. **Lint passes.** `./gradlew lintDebug`.
7. **Release build works** if you added or changed a library: `./gradlew assembleRelease`.
8. **Ayah data rules** if you touched `ayahs.json`: regenerated from source and `version` bumped. See [Ayah Data](Developer-Ayah-Data.md).
9. **Docs pages updated.** User pages (`docs/User-*.md`) for anything a user can see or do differently. Developer pages (`docs/Developer-*.md`) for changes to architecture, data, commands, tests, coverage, CI, or rules. If neither changed, say so in the pull request description. A new page must be linked from its guide page and from `docs/_Sidebar.md`.
10. **Tracker updated at the end.** For a feature, move it to Done in the `README.md` feature tracker in the pull request that completes it.
11. **`CLAUDE.md` updated** when the change affects architecture, commands, dependencies, or rules.
12. **Commit when told**, push the branch, and open a pull request. Merge once CI is green.

## Pull requests

- Describe what changed and why, in plain words.
- Write the test plan as plain bullet points.
- Link the docs pages you updated, or state that no docs change was needed.

[Back to the Developer Guide](Developer-Guide.md)
