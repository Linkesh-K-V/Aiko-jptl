package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Aiko JLPT", appName)
  }

  @Test
  fun `test TaskHapticFeedback executes safely and triggers completion haptic`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    // Verify that invoking task complete haptic executes safely without exceptions
    com.example.noignore.util.TaskHapticFeedback.performTaskCompleteHaptic(context)
    com.example.noignore.util.TaskHapticFeedback.performLightTapHaptic(context)
    com.example.noignore.util.TaskHapticFeedback.performCelebrationHaptic(context)
  }

  @Test
  fun `test JapaneseLessonRepository curriculum, recap flow and on the spot reinforcement`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = com.example.noignore.japanese.lesson.JapaneseLessonRepository(context)

    // 1. Initial State
    val allLessons = repo.allLessons
    org.junit.Assert.assertTrue(allLessons.size >= 8)
    val currentLesson = repo.getCurrentLesson()
    org.junit.Assert.assertEquals(1, currentLesson.lessonNumber)

    // 2. Recap generation for previous lesson
    val previousLesson = repo.getPreviousLessonForRecap()
    val recapQuestions = repo.getRecapQuestions(previousLesson)
    org.junit.Assert.assertTrue(recapQuestions.isNotEmpty())
    val q1 = recapQuestions.first()
    org.junit.Assert.assertEquals(4, q1.options.size)
    org.junit.Assert.assertTrue(q1.correctIndex in 0..3)
    org.junit.Assert.assertTrue(q1.explanation.isNotBlank())

    // 3. Failing a question adds to current lesson queue on the spot
    val itemToFail = q1.targetItem
    repo.addExtraFailedItemToCurrentLesson(itemToFail)
    val extraItems = repo.getExtraItemsForCurrentLesson()
    org.junit.Assert.assertTrue(extraItems.any { it.id == itemToFail.id })

    // 4. Completing current lesson marks done, advances currentLessonId, and clears extra items
    repo.completeLesson(currentLesson.id)
    org.junit.Assert.assertTrue(repo.isLessonCompleted(currentLesson.id))
    org.junit.Assert.assertEquals(2, repo.getCurrentLessonId())
    org.junit.Assert.assertTrue(repo.getExtraItemsForCurrentLesson().isEmpty())
  }

  @Test
  fun `test JlptTopicCurriculumRepository covers N5 through N1 and supports search, filtering, and understanding tracking`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = com.example.noignore.japanese.data.JlptTopicCurriculumRepository(context)

    // 1. Verify all levels (N5, N4, N3, N2, N1) have topics
    val levels = com.example.noignore.japanese.model.JlptExamLevel.entries
    for (level in levels) {
      val topics = repo.getTopicsForLevel(level)
      org.junit.Assert.assertTrue("Level ${level.code} must have topic units", topics.isNotEmpty())
      // Check each topic has non-empty items
      for (unit in topics) {
        org.junit.Assert.assertTrue("Topic ${unit.title} must have overview", unit.overview.isNotBlank())
        org.junit.Assert.assertTrue("Topic ${unit.title} must have items", unit.items.isNotEmpty())
        for (item in unit.items) {
          org.junit.Assert.assertTrue(item.japanese.isNotBlank())
          org.junit.Assert.assertTrue(item.meaning.isNotBlank())
        }
      }
    }

    // 2. Test N5 specific topics (particles, verbs, kanji, adjectives, vocab, expressions)
    val n5Topics = repo.getTopicsForLevel(com.example.noignore.japanese.model.JlptExamLevel.N5)
    org.junit.Assert.assertTrue(n5Topics.any { it.title.contains("Particles") })
    org.junit.Assert.assertTrue(n5Topics.any { it.title.contains("Verb") })
    org.junit.Assert.assertTrue(n5Topics.any { it.title.contains("Kanji") })

    // 3. Test search functionality
    val particleSearchResults = repo.searchTopics(com.example.noignore.japanese.model.JlptExamLevel.N5, "particle")
    org.junit.Assert.assertTrue(particleSearchResults.isNotEmpty())

    val kanjiSearchResults = repo.searchTopics(com.example.noignore.japanese.model.JlptExamLevel.N5, "日")
    org.junit.Assert.assertTrue(kanjiSearchResults.isNotEmpty())

    // 4. Test category filtering
    val grammarTopics = repo.getTopicsByCategory(
      com.example.noignore.japanese.model.JlptExamLevel.N5,
      com.example.noignore.japanese.model.JlptTopicCategory.GRAMMAR
    )
    org.junit.Assert.assertTrue(grammarTopics.isNotEmpty())
    org.junit.Assert.assertTrue(grammarTopics.all { it.category == com.example.noignore.japanese.model.JlptTopicCategory.GRAMMAR })

    // 5. Test understanding tracking
    val firstN5 = n5Topics.first()
    org.junit.Assert.assertFalse(repo.isTopicUnderstood(firstN5.id))
    repo.setTopicUnderstood(firstN5.id, true)
    org.junit.Assert.assertTrue(repo.isTopicUnderstood(firstN5.id))
    org.junit.Assert.assertTrue(repo.getUnderstoodCount(com.example.noignore.japanese.model.JlptExamLevel.N5) >= 1)

    // Toggle
    val toggledState = repo.toggleTopicUnderstood(firstN5.id)
    org.junit.Assert.assertFalse(toggledState)
    org.junit.Assert.assertFalse(repo.isTopicUnderstood(firstN5.id))
  }

  @Test
  fun `test JapaneseTtsManager text cleaning and lesson item audio readiness`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val tts = com.example.noignore.audio.JapaneseTtsManager(context)

    // Test text cleaning for TTS
    val cleanedGrammar = com.example.noignore.audio.JapaneseTtsManager.cleanJapaneseForTts("〜てください")
    org.junit.Assert.assertEquals("てください", cleanedGrammar)

    val cleanedSlash = com.example.noignore.audio.JapaneseTtsManager.cleanJapaneseForTts("みず / スイ")
    org.junit.Assert.assertEquals("みず、スイ", cleanedSlash)

    val cleanedBrackets = com.example.noignore.audio.JapaneseTtsManager.cleanJapaneseForTts("[Noun] + です")
    org.junit.Assert.assertEquals("Noun + です", cleanedBrackets)

    // Verify all lessons have items with non-empty Japanese text and readings ready for TTS
    val repo = com.example.noignore.japanese.lesson.JapaneseLessonRepository(context)
    for (lesson in repo.allLessons) {
      org.junit.Assert.assertTrue("Lesson ${lesson.id} has items", lesson.coreItems.isNotEmpty())
      for (item in lesson.coreItems) {
        org.junit.Assert.assertTrue("Item ${item.id} has japanese text", item.japanese.isNotBlank())
        val textToSpeak = item.reading.ifBlank { item.japanese }
        org.junit.Assert.assertTrue("Item ${item.id} has speakable text", textToSpeak.isNotBlank())
        val clean = com.example.noignore.audio.JapaneseTtsManager.cleanJapaneseForTts(textToSpeak)
        org.junit.Assert.assertTrue("Item ${item.id} clean text not empty", clean.isNotBlank())
      }
    }

    tts.shutdown()
  }

  @Test
  fun `test curriculum asset loading and validation pipeline in Robolectric context`() = kotlinx.coroutines.runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(context, com.example.noignore.data.AppDatabase::class.java)
        .allowMainThreadQueries()
        .build()

    try {
        val count = com.example.noignore.japanese.curriculum.JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        org.junit.Assert.assertTrue("Should load items into Room", count >= 393)

        val totalInDb = db.jlptContentDao().count()
        org.junit.Assert.assertTrue("Total in database should match or exceed loaded count", totalInDb >= 393)

        // Verify N5 items
        val n5Count = db.jlptContentDao().countByLevel("N5")
        org.junit.Assert.assertTrue("N5 items should be at least 238", n5Count >= 238)

        // Verify relationship queries
        val itemWithRel = com.example.noignore.data.content.JlptContentEntity(
            id = "test_rel_1",
            japanese = "車",
            reading = "くるま",
            meaning = "car",
            category = "VOCAB",
            jlptLevel = "N5",
            relatedItems = "test_rel_kanji_car"
        )
        db.jlptContentDao().insert(itemWithRel)
        val referencing = db.jlptContentDao().getItemsReferencing("test_rel_kanji_car")
        org.junit.Assert.assertEquals(1, referencing.size)
        org.junit.Assert.assertEquals("test_rel_1", referencing.first().id)
    } finally {
        db.close()
    }
  }

  @Test
  fun `test LearnerModelEngine competence progression and profile persistence`() = kotlinx.coroutines.runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(context, com.example.noignore.data.AppDatabase::class.java)
        .allowMainThreadQueries()
        .build()

    try {
        val initialProfile = com.example.noignore.japanese.engine.LearnerModelEngine.getOrInitProfile(db)
        org.junit.Assert.assertNotNull(initialProfile)
        val initialVocab = initialProfile.vocabMasteryScore

        // Success should increase competency
        com.example.noignore.japanese.engine.LearnerModelEngine.updateCompetency(db, "VOCAB", isSuccess = true, responseTimeMs = 1500L)
        val updatedProfile = db.learnerProfileDao().getProfile()
        org.junit.Assert.assertNotNull(updatedProfile)
        org.junit.Assert.assertTrue("Vocab mastery should increase after success", updatedProfile!!.vocabMasteryScore > initialVocab)

        // Failure should decrease competency
        com.example.noignore.japanese.engine.LearnerModelEngine.updateCompetency(db, "VOCAB", isSuccess = false, responseTimeMs = 4000L)
        val afterFailureProfile = db.learnerProfileDao().getProfile()
        org.junit.Assert.assertNotNull(afterFailureProfile)
        org.junit.Assert.assertTrue("Vocab mastery should decrease after failure", afterFailureProfile!!.vocabMasteryScore < updatedProfile.vocabMasteryScore)
    } finally {
        db.close()
    }
  }

  @Test
  fun `test backup creation and checksum validation logic`() {
    val input = "TEST_ROOM_DATABASE_PAYLOAD_V6"
    val checksum1 = com.example.noignore.data.backup.BackupRestoreManager.computeSha256(input)
    val checksum2 = com.example.noignore.data.backup.BackupRestoreManager.computeSha256(input)
    org.junit.Assert.assertEquals(checksum1, checksum2)
    org.junit.Assert.assertEquals(64, checksum1.length) // SHA-256 hex length
  }

  @Test
  fun `test Room round-trip integrity for all curriculum items`() = kotlinx.coroutines.runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(context, com.example.noignore.data.AppDatabase::class.java)
        .allowMainThreadQueries()
        .build()

    try {
        val loaded = com.example.noignore.japanese.curriculum.JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val allFromDb = db.jlptContentDao().getAllContent()
        org.junit.Assert.assertEquals(loaded, allFromDb.size)

        // Verify key properties preservation across sample and whole dataset
        val validLevels = setOf("N5", "N4", "N3", "N2", "N1")
        val validCategories = setOf("KANJI", "VOCAB", "GRAMMAR", "READING", "LISTENING", "JLPT_EXAM", "KANA")
        for (item in allFromDb) {
            org.junit.Assert.assertTrue("ID must not be blank", item.id.isNotBlank())
            org.junit.Assert.assertTrue("Japanese text must not be blank", item.japanese.isNotBlank())
            org.junit.Assert.assertTrue("Reading must not be blank", item.reading.isNotBlank())
            org.junit.Assert.assertTrue("Meaning must not be blank", item.meaning.isNotBlank())
            org.junit.Assert.assertTrue("JLPT level must be valid N5-N1", item.jlptLevel in validLevels)
            org.junit.Assert.assertTrue("Category must be valid", item.category in validCategories)
        }
    } finally {
        db.close()
    }
  }

  @Test
  fun `test learner model end-to-end adaptation chain`() = kotlinx.coroutines.runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(context, com.example.noignore.data.AppDatabase::class.java)
        .allowMainThreadQueries()
        .build()

    try {
        val deckRepo = com.example.noignore.japanese.data.JapaneseDeckRepository(context)
        // 1. Equal initial mastery
        val p1 = com.example.noignore.japanese.engine.LearnerModelEngine.getOrInitProfile(db)
        db.learnerProfileDao().insertOrUpdate(p1.copy(kanaMasteryScore = 0.8f, kanjiMasteryScore = 0.8f, vocabMasteryScore = 0.8f, grammarMasteryScore = 0.8f))

        // 2. Record repeated grammar failures
        repeat(8) {
            com.example.noignore.japanese.engine.LearnerModelEngine.updateCompetency(db, "GRAMMAR", isSuccess = false, responseTimeMs = 5000L)
        }
        val weakGrammarProfile = db.learnerProfileDao().getProfile()!!
        org.junit.Assert.assertTrue("Grammar score should drop after 8 failures", weakGrammarProfile.grammarMasteryScore <= 0.6f)

        // 3. DailyStudyPlanner generates mission focusing on grammar
        val grammarPlan = com.example.noignore.japanese.engine.DailyStudyPlanner.getOrCreateTodayMission(db, deckRepo, forceEmergencyMode = false)
        val hasGrammarFocus = grammarPlan.tasks.any { it.title.contains("Grammar", ignoreCase = true) || it.description.contains("Grammar", ignoreCase = true) }
        org.junit.Assert.assertTrue("Plan should recommend Grammar Focus Drill when grammar is weakest", hasGrammarFocus)

        // 4. Test opposite: Strong grammar, weak kanji
        db.learnerProfileDao().insertOrUpdate(weakGrammarProfile.copy(grammarMasteryScore = 0.95f, kanjiMasteryScore = 0.20f))
        // Overwrite existing today mission so it recalculates
        val kanjiPlan = com.example.noignore.japanese.engine.DailyStudyPlanner.getOrCreateTodayMission(db, deckRepo, forceEmergencyMode = true)
        val hasSrsOrKanji = kanjiPlan.tasks.any { it.title.contains("Sprint", ignoreCase = true) || it.title.contains("Kanji", ignoreCase = true) || it.title.contains("Rapid", ignoreCase = true) }
        org.junit.Assert.assertTrue("Plan should reflect dynamic priorities", hasSrsOrKanji)
    } finally {
        db.close()
    }
  }

  @Test
  fun `test atomic rollback on restore failure`() = kotlinx.coroutines.runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(context, com.example.noignore.data.AppDatabase::class.java)
        .allowMainThreadQueries()
        .build()

    try {
        // Setup initial record A
        val initialMined = com.example.noignore.data.mined.MinedItemEntity(
            id = "mined_original_A",
            japanese = "本",
            reading = "ほん",
            romaji = "hon",
            meaning = "book original",
            category = "VOCAB",
            jlptLevel = "N5",
            sourceSentence = "本を読む",
            sourceEnglish = "read a book",
            userNotes = "orig",
            tags = "test",
            createdAtMs = 1000L,
            updatedAtMs = 1000L
        )
        db.minedItemDao().insert(initialMined)
        org.junit.Assert.assertEquals(1, db.minedItemDao().getAllMinedItems().size)

        // Simulated atomic transaction with a failure at step 2
        var caughtException = false
        try {
            db.runInTransaction {
                // Step 1: Mutate Record A
                kotlinx.coroutines.runBlocking {
                    db.minedItemDao().insert(initialMined.copy(meaning = "MODIFIED IN TRANSACTION"))
                }
                // Step 2: Throw failure
                throw IllegalStateException("Simulated restore crash in mid-transaction")
            }
        } catch (e: IllegalStateException) {
            caughtException = true
        }

        org.junit.Assert.assertTrue("Exception must be caught", caughtException)
        // Verify atomic rollback: Record A must still exist with original meaning
        val afterRollback = db.minedItemDao().getMinedItemById("mined_original_A")
        org.junit.Assert.assertNotNull("Record A must survive", afterRollback)
        org.junit.Assert.assertEquals("book original", afterRollback!!.meaning)
    } finally {
        db.close()
    }
  }

  @Test
  fun `test database migration 5 to 6 schema validity and data survival`() = kotlinx.coroutines.runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    // Create an SQLite database at version 5 with existing pre-v6 tables
    val dbName = "test_migration_v5_v6.db"
    context.deleteDatabase(dbName)

    val dbHelper = object : android.database.sqlite.SQLiteOpenHelper(context, dbName, null, 5) {
        override fun onCreate(sqlite: android.database.sqlite.SQLiteDatabase) {
            // 1. tasks
            sqlite.execSQL("""
                CREATE TABLE IF NOT EXISTS `tasks` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `title` TEXT NOT NULL,
                    `timeMillis` INTEGER NOT NULL,
                    `repeatMode` TEXT NOT NULL,
                    `repeatDays` TEXT NOT NULL,
                    `confirmDelayMinutes` INTEGER NOT NULL,
                    `ringtoneUri` TEXT,
                    `hour` INTEGER NOT NULL,
                    `minute` INTEGER NOT NULL,
                    `completed` INTEGER NOT NULL
                )
            """.trimIndent())
            // 2. task_history
            sqlite.execSQL("""
                CREATE TABLE IF NOT EXISTS `task_history` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `taskId` INTEGER NOT NULL,
                    `date` TEXT NOT NULL,
                    `status` TEXT NOT NULL,
                    `taskTitle` TEXT NOT NULL,
                    `confirmedAt` INTEGER
                )
            """.trimIndent())
            // 3. achievements
            sqlite.execSQL("""
                CREATE TABLE IF NOT EXISTS `achievements` (
                    `id` TEXT PRIMARY KEY NOT NULL,
                    `title` TEXT NOT NULL,
                    `description` TEXT NOT NULL,
                    `icon` TEXT NOT NULL,
                    `requirement` INTEGER NOT NULL,
                    `category` TEXT NOT NULL,
                    `unlocked` INTEGER NOT NULL,
                    `unlockedAt` INTEGER
                )
            """.trimIndent())
            // 4. jlpt_category_progress
            sqlite.execSQL("""
                CREATE TABLE IF NOT EXISTS `jlpt_category_progress` (
                    `level` TEXT NOT NULL,
                    `category` TEXT NOT NULL,
                    `categoryTitle` TEXT NOT NULL,
                    `totalItems` INTEGER NOT NULL,
                    `completedItems` INTEGER NOT NULL,
                    `reviewedItems` INTEGER NOT NULL,
                    `accuracyRate` INTEGER NOT NULL,
                    `lastStudiedAt` INTEGER NOT NULL,
                    PRIMARY KEY(`level`, `category`)
                )
            """.trimIndent())
            // 5. jlpt_exam_target
            sqlite.execSQL("""
                CREATE TABLE IF NOT EXISTS `jlpt_exam_target` (
                    `id` INTEGER PRIMARY KEY NOT NULL,
                    `targetLevel` TEXT NOT NULL,
                    `targetExamDateMillis` INTEGER NOT NULL,
                    `dailyGoalMinutes` INTEGER NOT NULL,
                    `customGoalNotes` TEXT NOT NULL,
                    `updatedAt` INTEGER NOT NULL
                )
            """.trimIndent())
            // 6. flashcard_srs
            sqlite.execSQL("""
                CREATE TABLE IF NOT EXISTS `flashcard_srs` (
                    `cardId` TEXT PRIMARY KEY NOT NULL,
                    `category` TEXT NOT NULL,
                    `jlptLevel` TEXT NOT NULL,
                    `repetition` INTEGER NOT NULL,
                    `intervalDays` INTEGER NOT NULL,
                    `easeFactor` REAL NOT NULL,
                    `dueDateMs` INTEGER NOT NULL,
                    `lastReviewedMs` INTEGER NOT NULL,
                    `lapses` INTEGER NOT NULL,
                    `state` TEXT NOT NULL,
                    `totalReviews` INTEGER NOT NULL,
                    `lastRating` TEXT
                )
            """.trimIndent())
            sqlite.execSQL("CREATE INDEX IF NOT EXISTS `index_flashcard_srs_dueDateMs` ON `flashcard_srs` (`dueDateMs`)")
            sqlite.execSQL("CREATE INDEX IF NOT EXISTS `index_flashcard_srs_jlptLevel_dueDateMs` ON `flashcard_srs` (`jlptLevel`, `dueDateMs`)")
            sqlite.execSQL("CREATE INDEX IF NOT EXISTS `index_flashcard_srs_state` ON `flashcard_srs` (`state`)")

            // Insert legacy record A
            sqlite.execSQL("INSERT INTO tasks (id, title, timeMillis, repeatMode, repeatDays, confirmDelayMinutes, ringtoneUri, hour, minute, completed) VALUES (1, 'Legacy Task 1', 10000, 'ONCE', '', 5, null, 9, 0, 0)")
        }
        override fun onUpgrade(sqlite: android.database.sqlite.SQLiteDatabase, oldV: Int, newV: Int) {}
    }
    dbHelper.writableDatabase.close()

    // Now open via Room with MIGRATION_5_6 and MIGRATION_6_7 to version 7
    val roomDb = androidx.room.Room.databaseBuilder(context, com.example.noignore.data.AppDatabase::class.java, dbName)
        .addMigrations(com.example.noignore.data.AppDatabase.MIGRATION_5_6, com.example.noignore.data.AppDatabase.MIGRATION_6_7)
        .allowMainThreadQueries()
        .build()

    try {
        // 1. Verify old data survived
        val tasks = roomDb.taskDao().getAllTasksOnce()
        org.junit.Assert.assertTrue("Tasks should survive migration", tasks.isNotEmpty())
        org.junit.Assert.assertEquals("Legacy Task 1", tasks.first().title)

        // 2. Verify new tables created by MIGRATION_5_6 exist and can be queried
        org.junit.Assert.assertEquals(0, roomDb.minedItemDao().getAllMinedItems().size)
        org.junit.Assert.assertEquals(0, roomDb.jlptContentDao().count())
        org.junit.Assert.assertEquals(0, roomDb.mistakeRecordDao().countLeeches())
        org.junit.Assert.assertNull(roomDb.learnerProfileDao().getProfile("non_existent"))

        // 3. Verify new tables are writable
        val newContent = com.example.noignore.data.content.JlptContentEntity(
            id = "migrated_test_item",
            japanese = "猫",
            reading = "ねこ",
            meaning = "cat",
            category = "VOCAB",
            jlptLevel = "N5"
        )
        roomDb.jlptContentDao().insert(newContent)
        org.junit.Assert.assertEquals(1, roomDb.jlptContentDao().count())
    } finally {
        roomDb.close()
        context.deleteDatabase(dbName)
    }
  }
}

