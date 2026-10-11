package com.example

import com.example.noignore.character.CharacterLevel
import com.example.noignore.character.CharacterMood
import com.example.noignore.character.CharacterStateResolver
import com.example.noignore.character.DialogueTone
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testCharacterLevelResolver() {
        val beginner = CharacterStateResolver.resolve(currentStreak = 0, missesLast7Days = 0, didMissToday = false)
        assertEquals(CharacterLevel.BEGINNER, beginner.level)

        val consistent = CharacterStateResolver.resolve(currentStreak = 3, missesLast7Days = 0, didMissToday = false)
        assertEquals(CharacterLevel.CONSISTENT, consistent.level)

        val disciplined = CharacterStateResolver.resolve(currentStreak = 7, missesLast7Days = 0, didMissToday = false)
        assertEquals(CharacterLevel.DISCIPLINED, disciplined.level)

        val hardened = CharacterStateResolver.resolve(currentStreak = 30, missesLast7Days = 0, didMissToday = false)
        assertEquals(CharacterLevel.HARDENED, hardened.level)

        val legend = CharacterStateResolver.resolve(currentStreak = 100, missesLast7Days = 0, didMissToday = false)
        assertEquals(CharacterLevel.LEGEND, legend.level)
    }

    @Test
    fun testCharacterMoodResolver() {
        val missedToday = CharacterStateResolver.resolve(
            currentStreak = 5,
            missesLast7Days = 1,
            didMissToday = true,
            justUnlockedAchievement = false
        )
        assertEquals(CharacterMood.DISAPPOINTED, missedToday.mood)

        val silent = CharacterStateResolver.resolve(
            currentStreak = 0,
            missesLast7Days = 3,
            didMissToday = false,
            justUnlockedAchievement = false
        )
        assertEquals(CharacterMood.SILENT, silent.mood)

        val proud = CharacterStateResolver.resolve(
            currentStreak = 10,
            missesLast7Days = 0,
            didMissToday = false,
            justUnlockedAchievement = true
        )
        assertEquals(CharacterMood.PROUD, proud.mood)
    }

    @Test
    fun testDialogueToneResolver() {
        val silentState = CharacterStateResolver.resolve(
            currentStreak = 0,
            missesLast7Days = 3,
            didMissToday = false
        )
        assertEquals(DialogueTone.COLD, silentState.tone)

        val calmState = CharacterStateResolver.resolve(
            currentStreak = 0,
            missesLast7Days = 0,
            didMissToday = false
        )
        assertEquals(DialogueTone.STRICT, calmState.tone)
    }

    @Test
    fun testDailyConsistencyProgressBarProgressCalculation() {
        // Zero progress
        val zeroData = com.example.noignore.ui.study.DailyConsistencyData(
            completedTasks = 0,
            targetTasks = 3,
            completedLessons = 0,
            targetLessons = 15
        )
        assertEquals(0f, zeroData.overallProgress, 0.001f)
        assertEquals(0, zeroData.percentage)
        assertEquals(com.example.noignore.ui.study.ConsistencyTier.NOT_STARTED, zeroData.tier)
        assertFalse(zeroData.isMilestonePassed(25))

        // Halfway progress (100% tasks + 0% lessons = 50% overall)
        val halfwayData = com.example.noignore.ui.study.DailyConsistencyData(
            completedTasks = 3,
            targetTasks = 3,
            completedLessons = 0,
            targetLessons = 15
        )
        assertEquals(0.5f, halfwayData.overallProgress, 0.001f)
        assertEquals(50, halfwayData.percentage)
        assertEquals(com.example.noignore.ui.study.ConsistencyTier.STEADY, halfwayData.tier)
        assertTrue(halfwayData.isMilestonePassed(25))
        assertTrue(halfwayData.isMilestonePassed(50))
        assertFalse(halfwayData.isMilestonePassed(75))

        // Full mastery (3/3 tasks + 15/15 lessons = 100%)
        val masteryData = com.example.noignore.ui.study.DailyConsistencyData(
            completedTasks = 3,
            targetTasks = 3,
            completedLessons = 15,
            targetLessons = 15,
            currentStreak = 7
        )
        assertEquals(1.0f, masteryData.overallProgress, 0.001f)
        assertEquals(100, masteryData.percentage)
        assertEquals(com.example.noignore.ui.study.ConsistencyTier.MASTERY, masteryData.tier)
        assertTrue(masteryData.isMilestonePassed(100))
        assertTrue(masteryData.aikoFeedback.contains("Subarashii"))
    }

    @Test
    fun testAikoApprovalBadgesLogic() {
        val sevenDayBadge = com.example.noignore.model.AikoApprovalBadge.SEVEN_DAY
        val thirtyDayBadge = com.example.noignore.model.AikoApprovalBadge.THIRTY_DAY

        // At 0 streak days
        assertFalse(sevenDayBadge.isUnlocked(0))
        assertFalse(thirtyDayBadge.isUnlocked(0))
        assertEquals(7, sevenDayBadge.daysRemaining(0))
        assertEquals(30, thirtyDayBadge.daysRemaining(0))
        assertEquals(0f, sevenDayBadge.progress(0), 0.001f)

        // At 5 streak days
        assertFalse(sevenDayBadge.isUnlocked(5))
        assertEquals(2, sevenDayBadge.daysRemaining(5))
        assertEquals(5f / 7f, sevenDayBadge.progress(5), 0.001f)
        assertFalse(thirtyDayBadge.isUnlocked(5))
        assertEquals(25, thirtyDayBadge.daysRemaining(5))

        // At 7 streak days - 7-day badge unlocked, 30-day locked
        assertTrue(sevenDayBadge.isUnlocked(7))
        assertEquals(0, sevenDayBadge.daysRemaining(7))
        assertEquals(1f, sevenDayBadge.progress(7), 0.001f)
        assertFalse(thirtyDayBadge.isUnlocked(7))
        assertEquals(23, thirtyDayBadge.daysRemaining(7))

        // At 29 streak days
        assertTrue(sevenDayBadge.isUnlocked(29))
        assertFalse(thirtyDayBadge.isUnlocked(29))
        assertEquals(1, thirtyDayBadge.daysRemaining(29))

        // At 30 streak days - Both unlocked!
        assertTrue(sevenDayBadge.isUnlocked(30))
        assertTrue(thirtyDayBadge.isUnlocked(30))
        assertEquals(0, thirtyDayBadge.daysRemaining(30))
        assertEquals(1f, thirtyDayBadge.progress(30), 0.001f)

        // Beyond 30 days
        assertTrue(thirtyDayBadge.isUnlocked(45))
        assertEquals(0, thirtyDayBadge.daysRemaining(45))

        // Verify Hanko seals and titles
        assertEquals("承認", sevenDayBadge.hankoSeal)
        assertEquals("特認", thirtyDayBadge.hankoSeal)
        assertEquals("aiko_approval_7", sevenDayBadge.id)
        assertEquals("aiko_approval_30", thirtyDayBadge.id)
    }

    @Test
    fun testSpacedRepetitionSm2AlgorithmProgression() {
        val repo = com.example.noignore.data.srs.FlashcardSrsRepository(object : com.example.noignore.data.srs.FlashcardSrsDao {
            override fun getCardSrsFlow(cardId: String) = kotlinx.coroutines.flow.emptyFlow<com.example.noignore.data.srs.FlashcardSrsEntity?>()
            override suspend fun getCardSrs(cardId: String) = null
            override fun getAllCardsFlow() = kotlinx.coroutines.flow.emptyFlow<List<com.example.noignore.data.srs.FlashcardSrsEntity>>()
            override suspend fun getAllCardsSync() = emptyList<com.example.noignore.data.srs.FlashcardSrsEntity>()
            override fun getDueCardsFlow(currentTimeMs: Long) = kotlinx.coroutines.flow.emptyFlow<List<com.example.noignore.data.srs.FlashcardSrsEntity>>()
            override suspend fun getDueCardsSync(currentTimeMs: Long) = emptyList<com.example.noignore.data.srs.FlashcardSrsEntity>()
            override suspend fun getDueCardsByLevelSync(currentTimeMs: Long, jlptLevel: String) = emptyList<com.example.noignore.data.srs.FlashcardSrsEntity>()
            override suspend fun getDueCardsByCategorySync(currentTimeMs: Long, category: String) = emptyList<com.example.noignore.data.srs.FlashcardSrsEntity>()
            override fun getLearningCardsFlow() = kotlinx.coroutines.flow.emptyFlow<List<com.example.noignore.data.srs.FlashcardSrsEntity>>()
            override suspend fun getLearningCardsSync() = emptyList<com.example.noignore.data.srs.FlashcardSrsEntity>()
            override fun getNewCountFlow() = kotlinx.coroutines.flow.emptyFlow<Int>()
            override fun getLearningCountFlow() = kotlinx.coroutines.flow.emptyFlow<Int>()
            override fun getDueReviewCountFlow(currentTimeMs: Long) = kotlinx.coroutines.flow.emptyFlow<Int>()
            override suspend fun getTotalTrackedCount() = 0
            override suspend fun insertOrUpdate(entity: com.example.noignore.data.srs.FlashcardSrsEntity) {}
            override suspend fun insertAll(entities: List<com.example.noignore.data.srs.FlashcardSrsEntity>) {}
            override suspend fun deleteCard(cardId: String) {}
            override suspend fun clearAll() {}
        })

        val nowMs = 1700000000000L
        val newCard = com.example.noignore.data.srs.FlashcardSrsEntity(
            cardId = "n5_kanji_1",
            category = "KANJI",
            jlptLevel = "N5"
        )

        // Review 1: Good -> Interval should be 1 day
        val rev1 = repo.calculateSm2Schedule(newCard, com.example.noignore.japanese.model.AnkiRating.GOOD, nowMs)
        assertEquals(1, rev1.repetition)
        assertEquals(1, rev1.intervalDays)
        assertEquals(2.50f, rev1.easeFactor, 0.01f)
        assertEquals(nowMs + 1 * 24L * 60 * 60 * 1000, rev1.dueDateMs)
        assertEquals("REVIEW", rev1.state)

        // Review 2: Good -> Interval should be 3 days
        val rev2 = repo.calculateSm2Schedule(rev1, com.example.noignore.japanese.model.AnkiRating.GOOD, nowMs)
        assertEquals(2, rev2.repetition)
        assertEquals(3, rev2.intervalDays)
        assertEquals(2.50f, rev2.easeFactor, 0.01f)
        assertEquals(nowMs + 3 * 24L * 60 * 60 * 1000, rev2.dueDateMs)

        // Review 3: Good -> Interval should be 3 * 2.5 = 8 days
        val rev3 = repo.calculateSm2Schedule(rev2, com.example.noignore.japanese.model.AnkiRating.GOOD, nowMs)
        assertEquals(3, rev3.repetition)
        assertEquals(8, rev3.intervalDays)

        // Review 4: Easy -> Ease factor gets +0.15 bonus, expanded interval
        val rev4Easy = repo.calculateSm2Schedule(rev3, com.example.noignore.japanese.model.AnkiRating.EASY, nowMs)
        assertEquals(4, rev4Easy.repetition)
        assertEquals(2.65f, rev4Easy.easeFactor, 0.01f)
        assertTrue(rev4Easy.intervalDays > rev3.intervalDays * 2)

        // Review 5: Failure (Again) -> Repetition resets to 0, ease drops, interval resets to 0 (10m relearn queue), lapses incremented
        val rev5Again = repo.calculateSm2Schedule(rev4Easy, com.example.noignore.japanese.model.AnkiRating.AGAIN, nowMs)
        assertEquals(0, rev5Again.repetition)
        assertEquals(0, rev5Again.intervalDays)
        assertEquals(1, rev5Again.lapses)
        assertEquals("LEARNING", rev5Again.state)
        assertEquals(nowMs + 10L * 60 * 1000, rev5Again.dueDateMs)
        assertEquals(2.45f, rev5Again.easeFactor, 0.01f)

        // Memory retention calculation on fresh review
        val srsData = rev3.toSrsCardData()
        val retention = srsData.calculateMemoryRetention(nowMs)
        assertTrue(retention >= 0.95f)
    }

    @Test
    fun testContentValidatorRulesAndRejection() {
        val seenIds = mutableSetOf<String>()
        val seenKeys = mutableSetOf<String>()

        // 1. Valid Kanji item
        val validKanji = com.example.noignore.data.content.JlptContentEntity(
            id = "n5_k_999",
            japanese = "水",
            reading = "みず",
            meaning = "water",
            category = "KANJI",
            jlptLevel = "N5",
            exampleJapanese = "水を飲みます。",
            exampleEnglish = "I drink water."
        )
        val res1 = com.example.noignore.japanese.curriculum.validation.ContentValidator.validateItem(validKanji, seenIds, seenKeys)
        assertTrue(res1 is com.example.noignore.japanese.curriculum.validation.ValidationResult.Valid)
        seenIds.add(validKanji.id)
        seenKeys.add("${validKanji.japanese.trim()}_${validKanji.reading.trim()}_${validKanji.jlptLevel.uppercase()}")

        // 2. Duplicate ID detection
        val dupIdItem = validKanji.copy(meaning = "water alternative")
        val resDupId = com.example.noignore.japanese.curriculum.validation.ContentValidator.validateItem(dupIdItem, seenIds, seenKeys)
        assertTrue(resDupId is com.example.noignore.japanese.curriculum.validation.ValidationResult.Invalid)

        // 3. Blank fields
        val blankItem = validKanji.copy(id = "n5_k_1000", meaning = "")
        val resBlank = com.example.noignore.japanese.curriculum.validation.ContentValidator.validateItem(blankItem, seenIds, seenKeys)
        assertTrue(resBlank is com.example.noignore.japanese.curriculum.validation.ValidationResult.Invalid)

        // 4. Invalid JLPT Level
        val invalidLevel = validKanji.copy(id = "n5_k_1001", jlptLevel = "N9")
        val resLevel = com.example.noignore.japanese.curriculum.validation.ContentValidator.validateItem(invalidLevel, seenIds, seenKeys)
        assertTrue(resLevel is com.example.noignore.japanese.curriculum.validation.ValidationResult.Invalid)

        // 5. Exam question with missing prompt
        val badExam = com.example.noignore.data.content.JlptContentEntity(
            id = "n5_q_999",
            japanese = "問題",
            reading = "もんだい",
            meaning = "question",
            category = "JLPT_EXAM",
            jlptLevel = "N5",
            examQuestionPrompt = "",
            optionsJson = "[\"A\",\"B\"]"
        )
        val resBadExam = com.example.noignore.japanese.curriculum.validation.ContentValidator.validateItem(badExam, seenIds, seenKeys)
        assertTrue(resBadExam is com.example.noignore.japanese.curriculum.validation.ValidationResult.Invalid)
    }

    @Test
    fun testJlptExamEngineScoringAndThresholds() {
        assertEquals(80, com.example.noignore.japanese.engine.JlptExamEngine.getPassingThreshold("N5"))
        assertEquals(90, com.example.noignore.japanese.engine.JlptExamEngine.getPassingThreshold("N4"))
        assertEquals(95, com.example.noignore.japanese.engine.JlptExamEngine.getPassingThreshold("N3"))
        assertEquals(90, com.example.noignore.japanese.engine.JlptExamEngine.getPassingThreshold("N2"))
        assertEquals(100, com.example.noignore.japanese.engine.JlptExamEngine.getPassingThreshold("N1"))

        // Scaled score calculation
        val scaledPerfect = com.example.noignore.japanese.engine.JlptExamEngine.calculateScaledSectionScore(20, 20)
        assertEquals(60, scaledPerfect)

        val scaledZero = com.example.noignore.japanese.engine.JlptExamEngine.calculateScaledSectionScore(0, 20)
        assertEquals(0, scaledZero)

        // Exam grading - Pass condition
        val passResult = com.example.noignore.japanese.engine.JlptExamEngine.gradeExam(
            level = "N5",
            knowledgeRaw = 18, knowledgeMax = 20,
            readingRaw = 15, readingMax = 20,
            listeningRaw = 16, listeningMax = 20
        )
        assertTrue(passResult.isOverallPassed)
        assertTrue(passResult.totalScaledScore >= 80)

        // Exam grading - Sectional failure condition (one section below 19)
        val failSectionResult = com.example.noignore.japanese.engine.JlptExamEngine.gradeExam(
            level = "N5",
            knowledgeRaw = 20, knowledgeMax = 20,
            readingRaw = 20, readingMax = 20,
            listeningRaw = 2, listeningMax = 20 // Below 19 sectional threshold
        )
        assertFalse(failSectionResult.isOverallPassed)
    }

    @Test
    fun testDailyStudyPlannerPlanGeneration() {
        // Normal mode plan
        val normalPlan = com.example.noignore.japanese.engine.DailyStudyPlanner.createDefaultPlan(
            cardsDueCount = 12,
            curriculumLevel = "N5",
            isEmergencyMode = false
        )
        assertFalse(normalPlan.isFiveMinuteMode)
        assertTrue(normalPlan.tasks.isNotEmpty())
        assertTrue(normalPlan.estimatedMinutes >= 15)

        // 5-minute Emergency Sprint mode
        val emergencyPlan = com.example.noignore.japanese.engine.DailyStudyPlanner.createDefaultPlan(
            cardsDueCount = 8,
            curriculumLevel = "N5",
            isEmergencyMode = true
        )
        assertTrue(emergencyPlan.isFiveMinuteMode)
        assertEquals(5, emergencyPlan.estimatedMinutes)
        assertEquals(2, emergencyPlan.tasks.size)
    }
}
