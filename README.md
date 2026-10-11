# Aiko JLPT 

**Updated:** 12 October 2026\
**Basis:** AI Studio report dated 11 October 2026, plus the developer's
explicit audit corrections\
**Current focus:** Phase 19.5 closure and data/architecture
verification\
**Evidence rule:** Reported facts are not automatically independently
reproduced. Test execution, compilation, asset-derived counts, and
runtime behavior must be labelled separately.

## 1. Product goal

Aiko JLPT is an Android Japanese-learning app built with Kotlin and
Jetpack Compose. Its long-term goal is to guide learners from JLPT N5
through N1 using structured lessons, curriculum relationships,
Kanji/vocabulary/grammar practice, reading and listening, spaced
repetition, assessments, learner progress, and transparent
recommendations for further study.

The intended learning loop is: **learn → practise → assess → review
mistakes → revise → demonstrate retention → progress**.

Do not claim full official JLPT coverage or mastery merely because
records or links exist. Content quality, syllabus coverage, and learner
mastery need separate evidence.

## 2. Architecture reported by AI Studio

The latest report identifies these major areas. Paths below are reported
paths; check the current checkout before editing.

  -----------------------------------------------------------------------------------------------------------------------
  Area                    Reported location                                                    Responsibility
  ----------------------- -------------------------------------------------------------------- --------------------------
  App entry/navigation    `app/src/main/java/com/example/MainActivity.kt`                      Activity setup and Compose
                                                                                               navigation

  Room database           `.../data/AppDatabase.kt`                                            Room database, reported
                                                                                               schema version 8

  Curriculum cache        `.../data/content/JlptContentEntity.kt`, `JlptContentDao.kt`         Curriculum records stored
                                                                                               in Room

  Asset loading           `.../japanese/curriculum/JlptAssetContentLoader.kt`                  Loads curriculum JSON and
                                                                                               resolves aliases

  Canonical IDs           `.../japanese/curriculum/canonical/CanonicalCurriculumIds.kt`        Legacy-ID alias registry

  Graph validation        `.../japanese/curriculum/validation/CurriculumGraphValidator.kt`     Broken references,
                                                                                               duplicate edges,
                                                                                               self-links, cycles

  Remediation graph       `.../japanese/curriculum/graph/KnowledgeGraphService.kt`             Prerequisite/remediation
                                                                                               relationships

  SRS                     `.../japanese/engine/FsrsScheduler.kt`,                              Review scheduling and
                          `.../data/srs/FlashcardSrsEntity.kt`                                 stored card state

  Learner model           `.../data/learner/LearnerProfileEntity.kt`                           Reported skill/competency
                                                                                               state

  Lesson completion       `.../japanese/.../JapaneseLessonRepository.kt`                       Reportedly writes
                                                                                               completion to
                                                                                               SharedPreferences

  Backup                  `.../data/backup/BackupRestoreManager.kt`                            JSON export/import and
                                                                                               checksum validation

  AI integration          `.../ai/GeminiService.kt`                                            Gemini/LLM integration;
                                                                                               network/privacy
                                                                                               implications must be
                                                                                               documented

  Resource model          `.../japanese/curriculum/resource/CurriculumResourceModels.kt`       External resources and
                                                                                               coverage states

  Resource repository     `.../japanese/curriculum/resource/CurriculumResourceRepository.kt`   Loads resource JSON and
                                                                                               evaluates coverage

  Resource data           `app/src/main/assets/jlpt/further_study_resources.json`              Bundled further-study
                                                                                               links

  Coverage UI             `.../ui/study/CurriculumCoverageScreen.kt`                           Coverage dashboard

  Further-study UI        `.../ui/study/FurtherStudyComponents.kt`                             Resource cards/section

  Lesson UI               `.../ui/lesson/LessonStudyFlowScreen.kt`                             Structured lesson flow and
                                                                                               completion view

  Resource tests          `.../CurriculumResourceCoverageTest.kt`                              Reported 5
                                                                                               resource/coverage tests

  Graph/maintenance tests `.../CurriculumGraphTest.kt`,                                        Reported graph and
                          `.../CanonicalCurriculumIdsMaintenanceTest.kt`                       canonical-ID tests
  -----------------------------------------------------------------------------------------------------------------------

The report describes Kotlin 2.2.10, compile/target SDK 36, min SDK 26,
Compose BOM 2024.09.00, Room 2.7.0, DataStore 1.1.7, Navigation Compose
2.8.9, Coroutines 1.10.2, Gson 2.10.1, Robolectric 4.16.1, and Secrets
Gradle Plugin 2.0.1. Treat these as report-derived until confirmed in
the current Gradle files.

## 3. Curriculum inventory: counts are UNRESOLVED

Do **not** repeat the reported total of 1,764 or the per-level totals as
certified facts yet.

The reported N5 breakdown is:

-   104 Kanji
-   289 vocabulary
-   53 grammar
-   12 reading
-   12 listening
-   30 exam questions
-   211 Kana

These sum to **711**, not the claimed 865. The difference is **154**.

The N4 reports conflict as well:

-   Earlier report: 56 grammar, 2 reading, 1 listening, 1 question.
-   AI Studio report: 40 grammar, 10 reading, 10 listening, 0 questions.
-   Both report 175 Kanji and 575 vocabulary, but these also need
    asset-derived verification.

**Status: all curriculum counts are UNRESOLVED** until a reproducible
script counts canonical records directly from the authoritative JSON
assets, explains inclusion/exclusion rules, checks duplicate IDs, and
prints per-level/per-category totals. Do not reconcile the discrepancy
by guessing.

The report's graph totals---2,330 unique edges, 1,082
Kanji-to-vocabulary edges, 183 vocabulary-to-grammar edges, 179
Kana-to-vocabulary edges, 3,136 directional relationships, zero broken
references, and zero cycles/duplicate edges---are also **reported, not
independently reproduced here**. Recompute them from the assets.

N3/N2/N1 were described as scaffolds with 35, 30, and 24 records
respectively. These counts are unresolved too; their content depth is
plainly not evidence of comprehensive level coverage.

## 4. Phase 19.5 --- implementation and actual test status

Phase 19.5 adds a locally bundled resource catalogue and
curriculum-coverage layer. Reported states are:

-   `AVAILABLE_IN_AIKO`
-   `INSTRUCTIONAL_CONTENT_AVAILABLE`
-   `TAUGHT_BY_LESSON`
-   `PRACTICED`
-   `ASSESSED`
-   `DEMONSTRATED_MASTERY`
-   `RECOMMENDED_FURTHER_STUDY`
-   `NOT_YET_COVERED`

The intended distinction is important: a record existing is not the same
as instructional content existing; instructional content existing is not
proof that a structured lesson teaches it; a lesson being available is
not proof the learner completed it; a resource link is not proof of
practice, assessment, or mastery.

### Test evidence

  ---------------------------------------------------------------------------------------------------------------------------------------------
  Check                                     Status
  ----------------------------------------- ---------------------------------------------------------------------------------------------------
  `compile_applet`                          AI Studio reports exit code 0. This is a compilation check, not the full test suite.

  `CurriculumResourceCoverageTest`          **Executed: 5/5 passed**, according to the report. Command reported:
                                            `gradle :app:testDebugUnitTest --tests "com.example.CurriculumResourceCoverageTest" --no-daemon`.
                                            XML report reported under `app/build/test-results/testDebugUnitTest/`.

  `CanonicalCurriculumIdsMaintenanceTest`   **Not executed to completion.** Assertions were checked by code/script; that is not equivalent to a
                                            completed Gradle test run.

  `CurriculumGraphTest`                     **Not executed to completion.** Assertions/counts were checked by code/script; that is not
                                            equivalent to a completed Gradle test run.

  Android emulator/device interactions      **Pending.** AI Studio reports no emulator/ADB in its environment.
  ---------------------------------------------------------------------------------------------------------------------------------------------

The report attributes slow test startup to Robolectric/Android SDK 34
bootstrap and memory/fork delays. An offline or blocked download of
Robolectric's `android-all` artifact is another plausible cause and must
be checked in Gradle logs/cache; do not assume the cause is proven.

**Recommended test architecture:** move pure JSON parsing, canonical-ID
validation, and graph/count checks into plain JVM JUnit tests that do
not require Android APIs or Robolectric. Retain Robolectric only for
tests that actually need Android framework behavior. Run the relevant
tests to completion and save XML reports.

## 5. Known corrections and unresolved design risks

### 5.1 Graph/source relationships

The report says these invalid links were removed from
`app/src/main/assets/jlpt/n5/vocabulary.json`:

-   `n5_v_02` (飲む) must not link to `n4_k_07` (園).
-   `n5_v_56` (駅) must not link to `n4_k_10` (都).

A regression test named
`test07_staleLegacyCrossLevelLinksMustNotExistInSourceData` is reported
in `CanonicalCurriculumIdsMaintenanceTest.kt`. The correction is
report-confirmed, but the maintenance suite has not completed a Gradle
run. Recheck source assets and rerun tests.

### 5.2 Grammar-resource mapping collision

The report says the desire resource maps `res_g_tae_kim_desire` to
`n5_g_12` (〜たいです). But the Tae Kim te-form resource is also
reported as mapping to `n5_g_12–16`, which includes `n5_g_12`.

This overlap needs review. The URL
`https://guidetojapanese.org/learn/grammar/teform` may be a general
te-form conjugation page rather than an appropriate resource for the
desire construction. Verify the canonical meaning of every ID and the
actual page contents. Do not assign `n5_g_12` to a te-form resource
unless that ID truly represents te-form.

### 5.3 Offline/privacy and Gemini

The AI Studio report claims "zero external data leakage," "fully
offline," and no runtime network requests, but it also identifies
`GeminiService.kt` and a BuildConfig API key. These statements conflict.

Document the actual behavior: - Which features call Gemini or any other
remote endpoint? - What user/content data is sent? - When are requests
made? - Is the key injected into BuildConfig and packaged in the APK? -
Are remote features optional, disabled offline, or required?

A key embedded in a client APK must not be treated as secret. Move
sensitive calls behind a controlled backend or use an appropriate
restricted-key design. Do not claim the entire application is fully
offline if Gemini requests can occur.

### 5.4 Backup completeness

The report says lesson completion is stored in SharedPreferences, while
`BackupRestoreManager` exports Room data. If lesson-completion flags are
outside Room and are not separately serialized, they may be omitted from
backup/restore.

Inspect both implementations. Either include lesson completion in
backup/restore with backward-compatible import/export, or explicitly
document that it is not backed up. Add a round-trip test proving lesson
completion survives export and restore if it is intended to be
preserved.

### 5.5 FSRS vs stored schema

The report claims an FSRS scheduler calculates stability, difficulty,
retrievability, and review intervals. The listed `FlashcardSrsEntity`
fields are `easeFactor`, `intervalDays`, `repetition`, `dueDate`,
`lapses`, and `state`; they do not show stability or difficulty fields.

Inspect `FsrsScheduler.kt`, `FlashcardSrsEntity.kt`, and the
DAO/repository: - If stability/difficulty are computed but not
persisted, explain how scheduling state survives process restarts. - If
the actual implementation is SM-2 with an FSRS-labelled wrapper, correct
the description. - Do not call it a full FSRS implementation without
verifying its algorithm and persisted state.

### 5.6 Mastery and leech rules

The AI Studio report leaves the mastery criteria and leech lapse
threshold blank. These must be explicitly defined in code and
documentation.

Specify the exact rule, threshold, relevant fields, reset/decay
behavior, and tests. Do not infer mastery from review count alone. Make
UI wording match the actual evidence, such as "retention signal" rather
than broad language proficiency if that is all the model measures.

### 5.7 Copyright and third-party assets

The report says Kanji study cards use Heisig/WaniKani-style mnemonics
and identifies KanjiVG-related stroke-order data. Review provenance,
licence terms, attribution, and redistribution permissions for: -
WaniKani mnemonics or copied explanations. - Heisig mnemonics or copied
text. - KanjiVG stroke-order data and its applicable licence/attribution
requirements. - Any other imported curriculum, audio, example sentences,
and images.

Do not assume that educational use makes copied content unrestricted.
Replace unlicensed material or provide required attribution/licence
notices before distribution.

### 5.8 Play Store package identity

The report identifies the audit target as
`com.aistudio.noignore.szsbxv`. Treat this as a development/build
identity until the release package is deliberately chosen.

Before any Play Store release, set and verify a stable,
developer-controlled `applicationId`; confirm signing configuration,
versioning, app name, privacy disclosures, API-key handling,
permissions, and third-party licences. Do not publish using a temporary
AI Studio/sandbox application ID.

## 6. Prioritized fix list --- 17 items

1.  **Recount curriculum assets:** add/run a deterministic script that
    counts canonical entities by level and category; resolve the N5
    711-versus-865 discrepancy and N4 count conflict.
2.  **Recompute graph totals:** derive entity and edge counts from the
    same canonical assets and publish the script output.
3.  **Run maintenance tests:** execute
    `CanonicalCurriculumIdsMaintenanceTest` to completion and retain the
    Gradle XML report.
4.  **Run graph tests:** execute `CurriculumGraphTest` to completion and
    retain the Gradle XML report.
5.  **Diagnose test startup:** inspect Gradle logs for `android-all`
    download failures, offline mode, repository access, and cache misses
    before attributing the hang to memory alone.
6.  **Decouple pure graph tests:** use plain JVM JUnit for JSON parsing,
    canonical-ID checks, relationship validity, and counts; reserve
    Robolectric for Android-dependent tests.
7.  **Fix the te-form resource mapping:** validate the canonical IDs and
    actual Tae Kim page; remove the `n5_g_12` overlap if semantically
    wrong.
8.  **Define mastery criteria:** document and test the exact rule and
    distinguish retention evidence from language proficiency.
9.  **Define leech criteria:** document and test the lapse threshold,
    state transitions, and remediation behavior.
10. **Audit FSRS fidelity:** compare `FsrsScheduler` calculations with
    persisted entity fields; correct either the algorithm/state model or
    the claims.
11. **Audit backup completeness:** check SharedPreferences
    lesson-completion state and add it to backup/restore if preservation
    is intended.
12. **Resolve privacy claims:** document Gemini network behavior and
    exactly what information leaves the device.
13. **Secure the Gemini key:** verify BuildConfig packaging and avoid
    relying on a client-distributed API key as a secret.
14. **Audit third-party licences:** verify WaniKani/Heisig mnemonic
    provenance and KanjiVG attribution/licence obligations.
15. **Prepare release identity:** choose a permanent `applicationId` and
    verify it in release configuration before Play Store publication.
16. **Run Android interaction checks:** on a real device/emulator, test
    the coverage screen, lesson-completion resource section, external
    intents, browser-unavailable fallback, and progress/SRS
    non-mutation.
17. **Publish a truthful closure report:** separate completed tests,
    script-only checks, source inspection, pending runtime checks, and
    remaining defects; do not mark the phase fully verified until its
    acceptance criteria are met.

## 7. Next milestone

Do not start a broad new feature phase yet. First close the data-count
discrepancies, test execution, resource mapping, backup, SRS/mastery,
privacy, and licensing issues. After those are resolved, deepen N4
structured lessons, reading, listening, and assessments. N3/N2/N1 remain
future curriculum expansion work; their record counts do not prove
complete level coverage.

------------------------------------------------------------------------

## Acceptance Checklist

Each row is one command to run or one file to inspect, with a concrete
expected result. Run commands from the repository root. If a command is
unavailable, report the exact failure instead of marking it passed.

  ------------------------------------------------------------------------------------------------------------------------------------------------------------
  Check                   One command or file to inspect                                                                               Exact expected result
  ----------------------- ------------------------------------------------------------------------------------------------------------ -----------------------
  Curriculum inventory    `python tools/count_curriculum_assets.py` *(create this script if absent)*                                   Prints canonical counts
                                                                                                                                       per level/category; N5
                                                                                                                                       category sum equals its
                                                                                                                                       computed total, and no
                                                                                                                                       unexplained mismatch
                                                                                                                                       remains.

  Graph integrity         `python tools/run_full_integrity_verification.py`                                                            Exit code 0; zero
                                                                                                                                       broken references,
                                                                                                                                       duplicate edges,
                                                                                                                                       self-links,
                                                                                                                                       semantic-invalid links,
                                                                                                                                       or cycles; counts match
                                                                                                                                       the inventory script's
                                                                                                                                       source snapshot.

  Resource suite          `./gradlew :app:testDebugUnitTest --tests "com.example.CurriculumResourceCoverageTest" --no-daemon`          `BUILD SUCCESSFUL`; 5
                                                                                                                                       tests executed, 0
                                                                                                                                       failures, 0 errors, 0
                                                                                                                                       skipped; XML report
                                                                                                                                       exists.

  Maintenance suite       `./gradlew :app:testDebugUnitTest --tests "com.example.CanonicalCurriculumIdsMaintenanceTest" --no-daemon`   `BUILD SUCCESSFUL`; all
                                                                                                                                       maintenance tests
                                                                                                                                       execute and pass; XML
                                                                                                                                       report lists executed
                                                                                                                                       tests rather than
                                                                                                                                       script-only
                                                                                                                                       verification.

  Graph suite             `./gradlew :app:testDebugUnitTest --tests "com.example.CurriculumGraphTest" --no-daemon`                     `BUILD SUCCESSFUL`; all
                                                                                                                                       graph tests execute and
                                                                                                                                       pass using freshly
                                                                                                                                       computed counts.

  Desire/te-form mapping  Inspect `app/src/main/assets/jlpt/further_study_resources.json`                                              Desire guide maps only
                                                                                                                                       to the verified desire
                                                                                                                                       concept; te-form guide
                                                                                                                                       maps only to concepts
                                                                                                                                       actually covered by
                                                                                                                                       that page; no
                                                                                                                                       unsupported `n5_g_12`
                                                                                                                                       overlap.

  Backup completeness     Inspect `.../data/backup/BackupRestoreManager.kt` and the lesson-completion persistence code                 Lesson completion is
                                                                                                                                       explicitly
                                                                                                                                       exported/restored or
                                                                                                                                       explicitly documented
                                                                                                                                       as excluded; if
                                                                                                                                       included, a round-trip
                                                                                                                                       test preserves its
                                                                                                                                       values.

  SRS fidelity            Inspect `.../japanese/engine/FsrsScheduler.kt` and `.../data/srs/FlashcardSrsEntity.kt`                      Algorithm documentation
                                                                                                                                       matches implemented
                                                                                                                                       calculations and
                                                                                                                                       persisted state; any
                                                                                                                                       claimed
                                                                                                                                       stability/difficulty
                                                                                                                                       state is reproducibly
                                                                                                                                       stored or correctly
                                                                                                                                       described as transient.

  Gemini privacy/key      Inspect `.../ai/GeminiService.kt`, `app/build.gradle.kts`, and generated BuildConfig configuration           All remote data flows
                                                                                                                                       are documented; no
                                                                                                                                       claim of fully offline
                                                                                                                                       behavior while remote
                                                                                                                                       calls are enabled; no
                                                                                                                                       production secret is
                                                                                                                                       assumed safe merely
                                                                                                                                       because it is in
                                                                                                                                       BuildConfig.

  Release package         Inspect `app/build.gradle.kts`                                                                               Release `applicationId`
                                                                                                                                       is a deliberate, stable
                                                                                                                                       developer-controlled
                                                                                                                                       ID---not a temporary
                                                                                                                                       `com.aistudio...`
                                                                                                                                       sandbox ID.

  Third-party licences    Inspect the repository's licence/attribution files and provenance for mnemonic/stroke-order assets           Each redistributed
                                                                                                                                       third-party asset has
                                                                                                                                       documented
                                                                                                                                       permission/licence and
                                                                                                                                       required attribution,
                                                                                                                                       or is replaced/removed.

  Runtime resource flow   Run the app on an Android emulator/device and exercise the coverage screen and external links                Screen opens; filters
                                                                                                                                       work; link launches or
                                                                                                                                       shows fallback;
                                                                                                                                       returning to the app
                                                                                                                                       does not change lesson
                                                                                                                                       completion,
                                                                                                                                       prerequisites, or SRS
                                                                                                                                       state.
  ------------------------------------------------------------------------------------------------------------------------------------------------------------

**Closure rule:** Phase 19.5 is not fully verified until the count/graph
scripts and relevant Gradle suites complete successfully, the mapping
and data-safety issues are resolved, and runtime-only claims are either
tested on Android or explicitly left pending.
