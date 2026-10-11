# Aiko JLPT

**Aiko JLPT** is an Android Japanese-learning application built with
Kotlin and Jetpack Compose. Its long-term goal is to guide learners
through JLPT N5--N1 using structured lessons, curriculum relationships,
practice, spaced repetition, assessments, learner progress, and
supplementary study resources.

> **Development status:** Active development. The current handoff covers
> Phase 19.5 --- Curriculum Coverage & Further Study. Some reported
> features compile and the resource-coverage test suite has been
> reported as passing, but curriculum counts and several test/runtime
> checks remain unresolved. This repository version should not be
> described as a fully verified or release-ready JLPT curriculum.

## Goals

-   Organize Japanese learning content by JLPT level and category.
-   Connect curriculum entities such as Kanji, vocabulary, grammar,
    reading, listening, and questions.
-   Support structured lessons and multiple practice modes.
-   Track learner progress and spaced-repetition reviews.
-   Surface supplementary resources while distinguishing available
    content from taught, practised, assessed, and mastered concepts.
-   Preserve learning data through backup and restore.
-   Expand content quality and coverage progressively from N5/N4 toward
    N3--N1.

## Reported technology stack

The latest project audit reports the following technologies. Confirm
versions against the current Gradle configuration if maintaining or
releasing a checkout.

-   Kotlin
-   Android SDK and Jetpack Compose
-   Material 3
-   Room / SQLite
-   Jetpack DataStore
-   Navigation Compose
-   Kotlin Coroutines and Flow
-   Gson for JSON serialization/parsing
-   KSP for Room
-   Robolectric for Android-dependent JVM tests
-   Gemini integration through `GeminiService` (network/privacy behavior
    must be understood before claiming the app is fully offline)

## Main areas of the codebase

Paths below are based on the current technical handoff; confirm exact
locations in your checkout.

  ------------------------------------------------------------------------------------------------------------------
  Area                    Location                                                           Purpose
  ----------------------- ------------------------------------------------------------------ -----------------------
  App entry and           `app/src/main/java/com/example/MainActivity.kt`                    Activity and Compose
  navigation                                                                                 navigation

  Curriculum JSON assets  `app/src/main/assets/jlpt/`                                        Bundled curriculum by
                                                                                             level and category

  Further-study resources `app/src/main/assets/jlpt/further_study_resources.json`            Supplementary resource
                                                                                             metadata

  Room database           `.../data/AppDatabase.kt`                                          Database schema and
                                                                                             entities

  Curriculum asset loader `.../japanese/curriculum/JlptAssetContentLoader.kt`                Loads curriculum assets
                                                                                             into the app

  Canonical curriculum    `.../japanese/curriculum/canonical/CanonicalCurriculumIds.kt`      Legacy ID alias
  IDs                                                                                        handling

  Graph validation        `.../japanese/curriculum/validation/CurriculumGraphValidator.kt`   Checks curriculum
                                                                                             relationships

  Knowledge graph service `.../japanese/curriculum/graph/KnowledgeGraphService.kt`           Relationship-based
                                                                                             remediation

  Spaced repetition       `.../japanese/engine/FsrsScheduler.kt`                             Review scheduling
                                                                                             logic; verify
                                                                                             algorithm/state
                                                                                             fidelity

  Backup and restore      `.../data/backup/BackupRestoreManager.kt`                          JSON export/import and
                                                                                             integrity checks

  AI integration          `.../ai/GeminiService.kt`                                          Gemini service
                                                                                             integration

  Coverage                `.../japanese/curriculum/resource/`                                Resource data and
  model/repository                                                                           coverage-state logic

  Coverage screen         `.../ui/study/CurriculumCoverageScreen.kt`                         Curriculum coverage
                                                                                             dashboard

  Lesson flow             `.../ui/lesson/LessonStudyFlowScreen.kt`                           Structured lesson
                                                                                             experience
  ------------------------------------------------------------------------------------------------------------------

## Features described in the current handoff

Implementation status should be checked against the current source and
tests.

-   Kana learning and practice.
-   Kanji study and writing practice.
-   Vocabulary and grammar content.
-   Structured lessons and lesson-completion flow.
-   Flashcard practice and spaced repetition.
-   Reading and listening practice.
-   JLPT exam/mock-test practice.
-   Learner progress, daily tasks, and achievements.
-   Curriculum graph validation and relationship-based remediation.
-   JSON backup and restore.
-   Curriculum coverage dashboard and further-study links.
-   Gemini-based functionality through `GeminiService`.

## Phase 19.5: Curriculum Coverage & Further Study

This phase adds a bundled catalogue of external learning resources and a
coverage model intended to distinguish:

-   `AVAILABLE_IN_AIKO`
-   `INSTRUCTIONAL_CONTENT_AVAILABLE`
-   `TAUGHT_BY_LESSON`
-   `PRACTICED`
-   `ASSESSED`
-   `DEMONSTRATED_MASTERY`
-   `RECOMMENDED_FURTHER_STUDY`
-   `NOT_YET_COVERED`

These states should represent different kinds of evidence. For example,
a curriculum record existing does not mean a learner has been taught or
mastered the concept, and an external link does not prove assessment or
mastery.

The resource catalogue is reported to include 18 external resources.
Check the actual JSON and each destination before relying on the
mappings or claiming that every resource is current and relevant.

## Current verification status

The latest audit report provides the following status. These are
report-derived results and should be reproduced when possible.

  -----------------------------------------------------------------------------
  Check                                     Reported status
  ----------------------------------------- -----------------------------------
  `compile_applet`                          Succeeded with exit code 0 in the
                                            reported environment

  `CurriculumResourceCoverageTest`          5 tests passed, 0 failures, 0
                                            errors, 0 skipped

  `CanonicalCurriculumIdsMaintenanceTest`   Full Gradle execution not
                                            completed; assertions were checked
                                            by code/script

  `CurriculumGraphTest`                     Full Gradle execution not
                                            completed; assertions/counts were
                                            checked by code/script

  Android emulator/device interactions      Pending; the reported cloud
                                            environment had no emulator/ADB

  Curriculum entity counts                  **Unresolved** pending a
                                            reproducible count from source
                                            assets

  Full official JLPT coverage               Not established
  -----------------------------------------------------------------------------

### Important curriculum count discrepancy

The reported N5 category breakdown is 104 Kanji + 289 vocabulary + 53
grammar + 12 reading + 12 listening + 30 questions + 211 Kana = **711**,
not the previously claimed 865. N4 counts also conflict between reports.

Do not rely on aggregate counts until a script computes canonical
records directly from the authoritative assets and explains its
inclusion rules. The reported graph totals likewise need to be
reproduced from the same source snapshot.

## Development setup

The exact setup may vary with the current branch and local environment.

1.  Clone the repository and open it in a compatible version of Android
    Studio.
2.  Allow Gradle sync to complete and install the Android SDK version
    required by the project configuration.
3.  Inspect `settings.gradle(.kts)`, the root and module
    `build.gradle(.kts)` files, and the Gradle wrapper to confirm the
    required JDK, SDK, and dependency versions.
4.  Configure any required secrets using the project's documented local
    secret mechanism. Never commit API keys, `.env` files, signing
    credentials, or generated secrets.
5.  Build and run the app on an emulator or physical Android device.

### Useful verification commands

Run these from the repository root, using the project's Gradle wrapper.

``` bash
# Compile/build the debug app
./gradlew :app:assembleDebug

# Run the curriculum resource coverage tests
./gradlew :app:testDebugUnitTest --tests "com.example.CurriculumResourceCoverageTest" --no-daemon

# Run canonical ID maintenance tests
./gradlew :app:testDebugUnitTest --tests "com.example.CanonicalCurriculumIdsMaintenanceTest" --no-daemon

# Run curriculum graph tests
./gradlew :app:testDebugUnitTest --tests "com.example.CurriculumGraphTest" --no-daemon
```

Test class names may need to be adjusted if package names differ in the
current checkout. A command only counts as passing if it finishes and
returns a successful exit code with the expected tests executed.
Compilation alone does not prove that tests passed.

The audit reported slow Robolectric worker startup. Inspect Gradle logs
and dependency/cache status, including whether the Robolectric
`android-all` artifact is available. Consider moving pure JSON and graph
validation tests to plain JVM JUnit so they do not require Robolectric.

## Known risks and work in progress

Before treating this version as release-ready, address or explicitly
document these items:

1.  **Curriculum counts:** N5/N4 inventories conflict between reports;
    compute counts directly from assets.
2.  **Graph and maintenance test execution:** run both Gradle suites to
    completion; script-only checks are not equivalent.
3.  **Resource mapping:** the reported desire resource maps to
    `n5_g_12`, while the te-form resource also reportedly includes
    `n5_g_12`. Verify the canonical meaning and actual destination page.
4.  **Privacy and network behavior:** `GeminiService` and a BuildConfig
    API key conflict with a blanket "fully offline" claim. Document
    remote calls and what data they send; do not treat a key packaged in
    an APK as secret.
5.  **Backup completeness:** lesson completion is reported to use
    SharedPreferences. Verify whether backup/restore includes it.
6.  **SRS implementation:** verify that the scheduler's algorithm
    matches the fields actually persisted. The reported SRS entity
    fields do not show FSRS stability/difficulty.
7.  **Mastery and leech rules:** define exact thresholds and test them;
    do not infer mastery from review count alone.
8.  **Third-party licensing:** verify rights and attribution for
    WaniKani/Heisig-derived mnemonics and KanjiVG or other imported
    assets.
9.  **Release package identity:** the reported application ID is
    `com.aistudio.noignore.szsbxv`. Choose and verify a permanent
    developer-controlled `applicationId` before any Play Store release.
10. **Runtime UX:** test coverage navigation, external URL launching,
    browser-unavailable fallback, and confirm that opening resources
    does not alter progress or SRS state.

## Roadmap

### Immediate --- correctness and verification

-   Generate a deterministic curriculum inventory report from source
    assets.
-   Recompute graph totals and validate semantic relationships.
-   Complete the graph and maintenance Gradle tests.
-   Resolve the desire/te-form resource overlap.
-   Audit backup coverage, Gemini data flow, SRS persistence,
    mastery/leech rules, and third-party licensing.
-   Perform Android runtime checks when an emulator/device is available.

### Next --- curriculum depth

-   Improve N4 structured lessons, reading, listening, and assessments.
-   Validate N4 vocabulary for correctness, duplication, and useful
    examples.
-   Review N5 grammar quality and inherited curriculum issues.

### Later --- upper-level expansion

-   Build N3, N2, and N1 systematically with structured lessons and
    meaningful assessment.
-   Expand coverage based on validated educational needs rather than
    record counts alone.

## Contributing and change-safety notes

-   Inspect existing code and assets before making changes.
-   Use canonical curriculum IDs and validate every relationship
    semantically, not just syntactically.
-   Add regression tests for mapping, graph, coverage-state,
    persistence, and scheduling changes.
-   Avoid unnecessary Room migrations and protect existing learner data.
-   Never claim a test passed unless it completed successfully.
-   Separate implemented, tested, reported, inferred, and pending
    behavior in audit notes.

## License and third-party content

Add or verify the project's own license before public distribution.
Audit all bundled curriculum text, mnemonics, audio, images, and
stroke-order data for source provenance, redistribution rights, and
required attribution. Do not assume that educational use automatically
permits redistribution.

## Project status

Aiko JLPT has a substantial Android learning foundation and a reported
Phase 19.5 curriculum-resource implementation. It remains under active
development. The current priority is reproducible curriculum/graph
counts, completed automated tests, privacy and backup clarity, algorithm
verification, licensing, and runtime checks---not a claim of complete
N5--N1 syllabus coverage.
