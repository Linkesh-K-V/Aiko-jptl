package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.noignore.data.AppDatabase
import com.example.noignore.data.content.JlptContentEntity
import com.example.noignore.data.learner.LearnerProfileEntity
import com.example.noignore.data.learner.MistakeRecordEntity
import com.example.noignore.japanese.curriculum.JlptAssetContentLoader
import com.example.noignore.japanese.curriculum.JlptLessonMapper
import com.example.noignore.japanese.curriculum.prerequisite.VocabPresentationEngine
import com.example.noignore.japanese.data.JapaneseDeckRepository
import com.example.noignore.japanese.data.JapaneseStoryRepository
import com.example.noignore.japanese.engine.AdaptiveAssessmentEngine
import com.example.noignore.japanese.engine.LearnerModelEngine
import com.example.noignore.japanese.engine.QuestionCategoryBucket
import com.example.noignore.japanese.model.JapaneseCategory
import com.example.noignore.japanese.model.JapaneseItem
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class N5RemediationAndAssessmentTest {

    private lateinit var context: Context
    private lateinit var db: AppDatabase
    private lateinit var storyRepo: JapaneseStoryRepository
    private lateinit var deckRepo: JapaneseDeckRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        storyRepo = JapaneseStoryRepository(context)
        deckRepo = JapaneseDeckRepository(context)
    }

    @After
    fun tearDown() {
        db.close()
    }

    // =========================================================================
    // PART A & B: READING & LISTENING ROOM MIGRATION & REACHABILITY (DEF-12-01)
    // =========================================================================

    @Test
    fun test01_readingPassagesLoadedFromRoomAuthoritativeSource() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val roomReadings = db.jlptContentDao().getContentByLevelAndCategory("N5", "READING")

        // 1. All 18 N5 reading items are loaded from Room
        assertEquals("All 18 N5 reading passages must be in Room", 18, roomReadings.size)

        val lessons = roomReadings.map { JlptLessonMapper.toReadingLesson(it) }
        assertEquals("Mapper must produce 18 ReadingLesson objects", 18, lessons.size)

        // 2. No legacy-only reading item is treated as the N5 curriculum
        for (lesson in lessons) {
            assertTrue("IDs must start with n5_r_ (authoritative Room ID)", lesson.id.startsWith("n5_r_"))
            assertFalse("Legacy ID reading_n5_ must NOT be present", lesson.id.startsWith("reading_n5_"))
            assertTrue("Title must not be blank", lesson.title.isNotBlank())
            assertTrue("Sentences must be present", lesson.sentences.isNotEmpty())
            assertTrue("Comprehension question must be present", lesson.questions.isNotEmpty())
            assertEquals("Exactly 4 options in comprehension question", 4, lesson.questions[0].options.size)
            assertEquals("JLPT level must be N5", "N5", lesson.jlptLevel)
        }
    }

    @Test
    fun test02_readingCompletionUpdatesLearnerStateAndPersistence() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val profileBefore = LearnerModelEngine.getOrInitProfile(db)
        val initialReadingCompetency = profileBefore.readingMasteryScore

        val passageId = "n5_r_01"
        assertFalse("Passage initially not marked completed", storyRepo.isReadingLessonCompleted(passageId))

        // Complete lesson
        storyRepo.markReadingLessonCompleted(passageId)
        assertTrue("Passage marked completed in repository", storyRepo.isReadingLessonCompleted(passageId))

        // Update learner model competency
        LearnerModelEngine.updateCompetency(db, category = "READING", isSuccess = true, responseTimeMs = 2000L)
        db.jlptContentDao().recordReview(passageId, mastery = 80, timestamp = System.currentTimeMillis())

        val profileAfter = LearnerModelEngine.getOrInitProfile(db)
        assertTrue("Reading competency score must increase after successful completion",
            profileAfter.readingMasteryScore > initialReadingCompetency)

        val entity = db.jlptContentDao().getItemById(passageId)
        assertNotNull(entity)
        assertEquals("Mastery updated in Room", 80, entity!!.mastery)
        assertEquals("Review count incremented in Room", 1, entity.reviewCount)
    }

    @Test
    fun test03_listeningExercisesLoadedFromRoomAuthoritativeSource() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val roomListening = db.jlptContentDao().getContentByLevelAndCategory("N5", "LISTENING")

        // 4. All 18 N5 listening items are loaded
        assertEquals("All 18 N5 listening exercises must be in Room", 18, roomListening.size)

        val exercises = roomListening.map { JlptLessonMapper.toListeningExercise(it) }
        assertEquals("Mapper must produce 18 ListeningExercise objects", 18, exercises.size)

        for (ex in exercises) {
            assertTrue("IDs must start with n5_l_ (authoritative Room ID)", ex.id.startsWith("n5_l_"))
            assertFalse("Legacy ID listen_n5_ must NOT be present", ex.id.startsWith("listen_n5_"))
            assertTrue("Audio dialogue must not be blank", ex.audioText.isNotBlank())
            assertTrue("Prompt must not be blank", ex.questionPrompt.isNotBlank())
            assertEquals("Exactly 4 options per listening drill", 4, ex.options.size)
            assertTrue("Furigana transcript available", ex.furiganaTranscript.isNotBlank())
            assertTrue("English translation available", ex.englishTranslation.isNotBlank())
        }
    }

    @Test
    fun test04_listeningCompletionUpdatesLearnerState() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val profileBefore = LearnerModelEngine.getOrInitProfile(db)
        val initialListeningCompetency = profileBefore.listeningMasteryScore

        val listenId = "n5_l_01"
        storyRepo.markListeningExerciseCompleted(listenId)
        assertTrue(storyRepo.isListeningExerciseCompleted(listenId))

        LearnerModelEngine.updateCompetency(db, category = "LISTENING", isSuccess = true, responseTimeMs = 2800L)
        db.jlptContentDao().recordReview(listenId, mastery = 85, timestamp = System.currentTimeMillis())

        val profileAfter = LearnerModelEngine.getOrInitProfile(db)
        assertTrue("Listening competency score must increase after successful completion",
            profileAfter.listeningMasteryScore > initialListeningCompetency)
    }

    // =========================================================================
    // PART C: DEF-12-02 LEVEL-C VOCABULARY KANA-FIRST PRESENTATION
    // =========================================================================

    @Test
    fun test05_levelCVocabularyIdentificationAndKanaFirstPresentation() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val n5Vocab = db.jlptContentDao().getContentByLevelAndCategory("N5", "VOCAB")
            .map { it.toJapaneseItem() }

        assertEquals("Total N5 vocabulary must be 289 items", 289, n5Vocab.size)

        val levelCItems = n5Vocab.filter { VocabPresentationEngine.isLevelCVocab(it) }
        assertEquals("Exactly 124 Level-C vocabulary items must be identified", 124, levelCItems.size)

        // Test Level-C item in Beginner mode with Furigana OFF
        val deskItem = n5Vocab.first { it.japanese == "机" }
        val beginnerPresentation = VocabPresentationEngine.resolvePresentation(
            item = deskItem,
            isBeginnerMode = true,
            isFuriganaEnabled = false,
            masteredKanji = emptySet()
        )

        assertEquals("Primary display in beginner mode must be Hiragana reading (つくえ)",
            "つくえ", beginnerPresentation.primaryDisplay)
        assertEquals("Secondary display in beginner mode must be Kanji (机)",
            "机", beginnerPresentation.secondaryDisplay)
        assertTrue("Must be classified as Kana-First", beginnerPresentation.isKanaFirst)
        assertTrue("Must remain readable even if furigana is disabled!",
            beginnerPresentation.isReadableWithoutFurigana)
    }

    @Test
    fun test06_levelCVocabularyMasteredKanjiRestoresNormalKanjiDisplay() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val n5Vocab = db.jlptContentDao().getContentByLevelAndCategory("N5", "VOCAB")
            .map { it.toJapaneseItem() }

        val deskItem = n5Vocab.first { it.japanese == "机" }

        // Once the learner has mastered the Kanji "机", standard presentation is restored
        val masteredPresentation = VocabPresentationEngine.resolvePresentation(
            item = deskItem,
            isBeginnerMode = false,
            isFuriganaEnabled = true,
            masteredKanji = setOf("机")
        )

        assertEquals("Primary display for mastered kanji must be Kanji (机)",
            "机", masteredPresentation.primaryDisplay)
        assertEquals("Secondary display for mastered kanji must be reading (つくえ)",
            "つくえ", masteredPresentation.secondaryDisplay)
        assertFalse("Should not be kana-first once kanji is mastered", masteredPresentation.isKanaFirst)
    }

    // =========================================================================
    // PART E, F, G, H: DEF-12-03 ASSESSMENT EXPANSION, ROTATION & ADAPTIVITY
    // =========================================================================

    @Test
    fun test07_questionBank150QuestionsAndFullGrammarCoverage() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val questions = db.jlptContentDao().getContentByLevelAndCategory("N5", "JLPT_EXAM")
            .map { it.toJapaneseItem() }

        assertEquals("Question bank must contain exactly 172 practice questions", 172, questions.size)

        // Verify quality of all 172 questions
        for (q in questions) {
            assertTrue("Prompt must not be blank for ${q.id}", q.examQuestionPrompt.isNotBlank())
            assertEquals("Exactly 4 options required for ${q.id}", 4, q.options.size)
            assertEquals("All 4 options must be distinct for ${q.id}", 4, q.options.toSet().size)
            assertTrue("Explanation / note must be provided for ${q.id}", q.mnemonicOrNote.isNotBlank())
        }

        // Verify 100% Grammar Coverage (all 53 N5 grammar points covered)
        val testedGrammar = mutableSetOf<String>()
        val testedKanji = mutableSetOf<String>()
        val testedVocab = mutableSetOf<String>()

        for (q in questions) {
            val entity = db.jlptContentDao().getItemById(q.id)
            val relIds = entity?.getRelatedItemIds() ?: emptyList()
            for (rel in relIds) {
                if (rel.startsWith("n5_g_")) testedGrammar.add(rel)
                if (rel.startsWith("n5_k_")) testedKanji.add(rel)
                if (rel.startsWith("n5_v_")) testedVocab.add(rel)
            }
        }

        assertEquals("All 53 N5 grammar points must be tested across the 172 questions", 53, testedGrammar.size)
        assertTrue("Substantial Kanji coverage (>= 70 kanji tested)", testedKanji.size >= 70)
        assertTrue("Substantial Vocab coverage (>= 90 vocab tested)", testedVocab.size >= 90)
    }

    @Test
    fun test08_questionBankClassificationStructure() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val questions = db.jlptContentDao().getContentByLevelAndCategory("N5", "JLPT_EXAM")
            .map { it.toJapaneseItem() }

        val buckets = AdaptiveAssessmentEngine.classifyQuestionBank(questions)

        assertTrue("Must contain Sentence Completion questions", (buckets[QuestionCategoryBucket.SENTENCE_COMPLETION]?.size ?: 0) >= 15)
        assertTrue("Must contain Particle Selection questions", (buckets[QuestionCategoryBucket.PARTICLE_SELECTION]?.size ?: 0) >= 15)
        assertTrue("Must contain Kanji Reading questions", (buckets[QuestionCategoryBucket.KANJI_READING]?.size ?: 0) >= 20)
        assertTrue("Must contain Grammar / Context questions", (buckets[QuestionCategoryBucket.GRAMMAR_CONTEXT]?.size ?: 0) >= 30)
        assertTrue("Must contain Vocabulary Meaning questions", (buckets[QuestionCategoryBucket.VOCABULARY_MEANING]?.size ?: 0) >= 30)
    }

    @Test
    fun test09_assessmentRotationAndRepetitionHorizon() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val allQuestions = db.jlptContentDao().getContentByLevelAndCategory("N5", "JLPT_EXAM")
            .map { it.toJapaneseItem() }

        // Horizon calculations
        val h10 = AdaptiveAssessmentEngine.calculateRepeatHorizon(allQuestions.size, 10)
        assertEquals("Repeat horizon for 10 q/day must be 17.2 days", 17.2, h10.repeatHorizonDaysAt1SessionPerDay, 0.01)

        val h20 = AdaptiveAssessmentEngine.calculateRepeatHorizon(allQuestions.size, 20)
        assertEquals("Repeat horizon for 20 q/day must be 8.6 days", 8.6, h20.repeatHorizonDaysAt1SessionPerDay, 0.01)

        val h30 = AdaptiveAssessmentEngine.calculateRepeatHorizon(allQuestions.size, 30)
        assertEquals("Repeat horizon for 30 q/day must be 5.73 days", 172.0 / 30.0, h30.repeatHorizonDaysAt1SessionPerDay, 0.01)

        // Multi-day simulation: verify no immediate repeats across 5 consecutive 10-question sessions
        val servedHistory = mutableSetOf<String>()
        for (day in 1..5) {
            val session = AdaptiveAssessmentEngine.selectAdaptiveQuestions(
                allQuestions = allQuestions,
                sessionSize = 10,
                currentSessionIndex = day,
                recentServedIds = servedHistory
            )
            assertEquals(10, session.size)
            // Verify no question in this session was already served
            for (q in session) {
                assertFalse("Question ${q.id} must not be repeated within initial horizon", servedHistory.contains(q.id))
                servedHistory.add(q.id)
            }
        }
        assertEquals("5 days of 10 questions = 50 unique questions served", 50, servedHistory.size)
    }

    @Test
    fun test10_adaptiveAssessmentPersonalizationAcrossLearners() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val allQuestions = db.jlptContentDao().getContentByLevelAndCategory("N5", "JLPT_EXAM")
            .map { it.toJapaneseItem() }

        // Learner A: Weak grammar
        val learnerA = LearnerProfileEntity(
            id = "learner_a",
            grammarMasteryScore = 0.20f,
            kanjiMasteryScore = 0.80f,
            vocabMasteryScore = 0.80f,
            kanaMasteryScore = 1.0f
        )
        val sessionA = AdaptiveAssessmentEngine.selectAdaptiveQuestions(allQuestions, 10, 1, profile = learnerA)
        val grammarCountA = sessionA.count {
            val b = QuestionCategoryBucket.fromQuestion(it)
            b == QuestionCategoryBucket.GRAMMAR_CONTEXT || b == QuestionCategoryBucket.PARTICLE_SELECTION
        }
        assertTrue("Learner A (weak grammar) must receive boosted grammar questions (got $grammarCountA)", grammarCountA >= 6)

        // Learner B: Weak Kanji
        val learnerB = LearnerProfileEntity(
            id = "learner_b",
            grammarMasteryScore = 0.80f,
            kanjiMasteryScore = 0.20f,
            vocabMasteryScore = 0.80f,
            kanaMasteryScore = 1.0f
        )
        val sessionB = AdaptiveAssessmentEngine.selectAdaptiveQuestions(allQuestions, 10, 1, profile = learnerB)
        val kanjiCountB = sessionB.count { QuestionCategoryBucket.fromQuestion(it) == QuestionCategoryBucket.KANJI_READING }
        assertTrue("Learner B (weak kanji) must receive boosted Kanji questions (got $kanjiCountB)", kanjiCountB >= 5)

        // Learner C: Weak Vocabulary
        val learnerC = LearnerProfileEntity(
            id = "learner_c",
            grammarMasteryScore = 0.80f,
            kanjiMasteryScore = 0.80f,
            vocabMasteryScore = 0.20f,
            kanaMasteryScore = 1.0f
        )
        val sessionC = AdaptiveAssessmentEngine.selectAdaptiveQuestions(allQuestions, 10, 1, profile = learnerC)
        val vocabCountC = sessionC.count { QuestionCategoryBucket.fromQuestion(it) == QuestionCategoryBucket.VOCABULARY_MEANING }
        assertTrue("Learner C (weak vocab) must receive boosted Vocabulary questions (got $vocabCountC)", vocabCountC >= 5)

        // Learner E: Repeated mistakes on a specific question
        val targetMistakeId = "n5_q_84"
        val mistakeRecord = MistakeRecordEntity(
            cardId = targetMistakeId,
            jlptLevel = "N5",
            category = "JLPT_EXAM",
            lapseCount = 4,
            totalAttempts = 5,
            mistakeType = "PARTICLE_CONFUSION",
            lastMistakeMs = System.currentTimeMillis(),
            isLeech = true,
            interventionNotes = "Repeated error on てもいいですか"
        )
        val sessionE = AdaptiveAssessmentEngine.selectAdaptiveQuestions(
            allQuestions = allQuestions,
            sessionSize = 10,
            currentSessionIndex = 1,
            mistakes = listOf(mistakeRecord)
        )
        assertTrue("Learner E must receive the mistake item for remediation", sessionE.any { it.id == targetMistakeId })
    }
}
