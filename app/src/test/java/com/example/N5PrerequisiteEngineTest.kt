package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.noignore.data.AppDatabase
import com.example.noignore.data.learner.LearnerProfileEntity
import com.example.noignore.japanese.curriculum.JlptAssetContentLoader
import com.example.noignore.japanese.curriculum.prerequisite.GrammarPrerequisiteGraph
import com.example.noignore.japanese.curriculum.prerequisite.KanaPrerequisiteModel
import com.example.noignore.japanese.curriculum.prerequisite.KanaStageType
import com.example.noignore.japanese.curriculum.prerequisite.PrerequisiteEngine
import com.example.noignore.japanese.curriculum.prerequisite.VocabPrerequisiteLevel
import com.example.noignore.japanese.curriculum.prerequisite.VocabPrerequisiteModel
import com.example.noignore.japanese.data.JapaneseDeckRepository
import com.example.noignore.japanese.engine.DailyStudyPlanner
import com.example.noignore.japanese.engine.LearnerModelEngine
import com.example.noignore.japanese.lesson.JapaneseLessonRepository
import com.example.noignore.japanese.model.JapaneseCategory
import com.example.noignore.japanese.model.JapaneseItem
import com.example.noignore.ui.onboarding.BeginnerStartingLevel
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class N5PrerequisiteEngineTest {

    private lateinit var context: Context
    private lateinit var db: AppDatabase

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun test1_kanaProgressionStagesOrderedCorrectly() {
        val stages = KanaPrerequisiteModel.STAGES
        assertEquals(13, stages.size)

        // Stage 1 must be Hiragana vowels
        assertEquals(KanaStageType.HIRAGANA_VOWELS, stages[0].stageType)
        assertEquals(listOf("あ", "い", "う", "え", "お"), stages[0].characters)
        assertEquals(1, stages[0].stageNumber)

        // Stage 2 must be Ka-row
        assertEquals(KanaStageType.HIRAGANA_KA_ROW, stages[1].stageType)
        assertEquals(listOf("か", "き", "く", "け", "こ"), stages[1].characters)

        // Stage 3 must be Sa-row
        assertEquals(KanaStageType.HIRAGANA_SA_ROW, stages[2].stageType)
        assertEquals(listOf("さ", "し", "す", "せ", "そ"), stages[2].characters)

        // For a complete beginner with 0.0 kana mastery, only Stage 1 is unlocked initially
        val unlockedBeginner = KanaPrerequisiteModel.getUnlockedStages(0.0f, emptySet())
        assertTrue("Stage 1 must be unlocked for beginner", unlockedBeginner.contains(1))
        assertFalse("Stage 2 must not be prematurely unlocked", unlockedBeginner.contains(2))

        // When Lesson 1 is completed or kanaMastery reaches threshold, Stage 2 unlocks
        val unlockedAfterLesson1 = KanaPrerequisiteModel.getUnlockedStages(0.0f, setOf(1))
        assertTrue("Stage 2 unlocks after Lesson 1 completion", unlockedAfterLesson1.contains(2))

        // When kana mastery reaches 0.35f, stages up to 5 are unlocked
        val unlockedProgress = KanaPrerequisiteModel.getUnlockedStages(0.35f, emptySet())
        assertTrue(unlockedProgress.containsAll(listOf(1, 2, 3, 4, 5)))
    }

    @Test
    fun test2_kanaMasteryScoreDefinitionAndTransitions() = runBlocking {
        // Initializing profile for an absolute beginner
        val profile = LearnerModelEngine.initializeStartingProfile(
            db = db,
            startingLevel = BeginnerStartingLevel.ABSOLUTE_BEGINNER,
            dailyTargetMinutes = 15
        )
        assertEquals(0.05f, profile.kanaMasteryScore, 0.001f)
        assertEquals(0.0f, profile.vocabMasteryScore, 0.001f)
        assertEquals(0.0f, profile.grammarMasteryScore, 0.001f)

        // Successful recall in Kana increases score
        LearnerModelEngine.updateCompetency(db, "KANA", isSuccess = true, responseTimeMs = 1200L)
        val afterSuccess = db.learnerProfileDao().getProfile()!!
        assertTrue("Kana score increases after successful recall", afterSuccess.kanaMasteryScore > 0.05f)

        // Repeated lapse decreases score
        LearnerModelEngine.updateCompetency(db, "KANA", isSuccess = false, responseTimeMs = 3500L)
        val afterLapse = db.learnerProfileDao().getProfile()!!
        assertTrue("Kana score decreases after lapse", afterLapse.kanaMasteryScore < afterSuccess.kanaMasteryScore)
    }

    @Test
    fun test3_kanaToVocabularyTransitionEvaluator() {
        // Known Kana = only Vowels (あ, い, う, え, お)
        val vowelKana = setOf('あ', 'い', 'う', 'え', 'お')

        val wordAi = JapaneseItem(
            id = "test_v_01",
            japanese = "あい",
            reading = "あい",
            romaji = "ai",
            meaning = "Love",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N5"
        )
        val evalAi = VocabPrerequisiteModel.evaluatePrerequisites(wordAi, vowelKana, emptySet(), emptySet())
        assertTrue("Word containing only known vowel kana must be appropriate", evalAi.isAppropriate)
        assertEquals(0, evalAi.missingKana.size)

        val wordSakana = JapaneseItem(
            id = "test_v_02",
            japanese = "さかな",
            reading = "さかな",
            romaji = "sakana",
            meaning = "Fish",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N5"
        )
        val evalSakana = VocabPrerequisiteModel.evaluatePrerequisites(wordSakana, vowelKana, emptySet(), emptySet())
        assertFalse("Word containing unlearned kana must be gated for beginner", evalSakana.isAppropriate)
        assertTrue("Missing kana must be identified", evalSakana.missingKana.contains('さ'))
        assertTrue("Missing kana must be identified", evalSakana.missingKana.contains('か'))
        assertTrue("Missing kana must be identified", evalSakana.missingKana.contains('な'))
    }

    @Test
    fun test4_kanjiGatingForBeginners() {
        // Complete beginner with kana mastery 0.05f
        assertTrue("Kanji must be gated when kana mastery < 0.50f", PrerequisiteEngine.isKanjiGated(0.05f))
        assertTrue("Kanji must be gated when kana mastery is 0.40f", PrerequisiteEngine.isKanjiGated(0.40f))

        // Once kana mastery reaches 0.50f+, kanji gating lifts
        assertFalse("Kanji gate lifts once kana mastery >= 0.50f", PrerequisiteEngine.isKanjiGated(0.50f))
        assertFalse("Kanji gate lifts once kana mastery is 0.85f", PrerequisiteEngine.isKanjiGated(0.85f))
    }

    @Test
    fun test5_vocabularyPrerequisiteClassificationLevels() {
        val pureKana = JapaneseItem(
            id = "v_here",
            japanese = "ここ",
            reading = "ここ",
            romaji = "koko",
            meaning = "Here",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N5"
        )
        assertEquals(VocabPrerequisiteLevel.LEVEL_A, VocabPrerequisiteModel.classifyVocabLevel(pureKana))

        val basicKanji = JapaneseItem(
            id = "v_water",
            japanese = "水",
            reading = "みず",
            romaji = "mizu",
            meaning = "Water",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N5"
        )
        assertEquals(VocabPrerequisiteLevel.LEVEL_B, VocabPrerequisiteModel.classifyVocabLevel(basicKanji))

        val grammarDependent = JapaneseItem(
            id = "v_eat_polite",
            japanese = "食べます",
            reading = "たべます",
            romaji = "tabemasu",
            meaning = "To eat",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N5"
        )
        assertEquals(VocabPrerequisiteLevel.LEVEL_C, VocabPrerequisiteModel.classifyVocabLevel(grammarDependent))

        val complexTime = JapaneseItem(
            id = "v_time",
            japanese = "何時何分",
            reading = "なんじなんぷん",
            romaji = "nanjinanpun",
            meaning = "What time and what minute",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N5"
        )
        assertEquals(VocabPrerequisiteLevel.LEVEL_D, VocabPrerequisiteModel.classifyVocabLevel(complexTime))
    }

    @Test
    fun test6_all45N5GrammarPrerequisiteGraphExplicitDependencies() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val allGrammar = db.jlptContentDao().getContentByCategory("GRAMMAR").filter { it.jlptLevel == "N5" }
        assertEquals("Must contain all 53 N5 grammar entries", 53, allGrammar.size)

        // Strict Te-form dependencies
        val teKudasaiPrereqs = GrammarPrerequisiteGraph.getDirectPrerequisites("n5_g_05") // 〜てください
        assertTrue("〜てください must depend directly on te-form n5_g_28", teKudasaiPrereqs.contains("n5_g_28"))

        val teMoIiPrereqs = GrammarPrerequisiteGraph.getDirectPrerequisites("n5_g_06") // 〜てもいいですか
        assertTrue("〜てもいいですか must depend on te-form n5_g_28", teMoIiPrereqs.contains("n5_g_28"))

        val teWaIkemasenPrereqs = GrammarPrerequisiteGraph.getDirectPrerequisites("n5_g_13") // 〜てはいけません
        assertTrue("〜てはいけません must depend on te-form n5_g_28", teWaIkemasenPrereqs.contains("n5_g_28"))

        val teIruPrereqs = GrammarPrerequisiteGraph.getDirectPrerequisites("n5_g_14") // 〜ている
        assertTrue("〜ている must depend on te-form n5_g_28", teIruPrereqs.contains("n5_g_28"))

        // Strict Nai-form dependencies
        val naideKudasaiPrereqs = GrammarPrerequisiteGraph.getDirectPrerequisites("n5_g_15") // 〜ないでください
        assertTrue("〜ないでください must depend on nai-form n5_g_30", naideKudasaiPrereqs.contains("n5_g_30"))

        val nakerebaPrereqs = GrammarPrerequisiteGraph.getDirectPrerequisites("n5_g_16") // 〜なければなりません
        assertTrue("〜なければなりません must depend on nai-form n5_g_30", nakerebaPrereqs.contains("n5_g_30"))

        // Strict Ta-form dependencies
        val taKotoGaAruPrereqs = GrammarPrerequisiteGraph.getDirectPrerequisites("n5_g_18") // 〜たことがある
        assertTrue("〜たことがある must depend on ta-form n5_g_31", taKotoGaAruPrereqs.contains("n5_g_31"))

        // Dictionary form foundation
        val kotoGaDekiruPrereqs = GrammarPrerequisiteGraph.getDirectPrerequisites("n5_g_17") // 〜ことができる
        assertTrue("〜ことができる must depend on dictionary form n5_g_29", kotoGaDekiruPrereqs.contains("n5_g_29"))

        // Pedagogical topological sort verification
        val sequence = GrammarPrerequisiteGraph.getPedagogicalSequence()
        assertEquals("Topological sequence must contain all 53 items", 53, sequence.size)

        val teFormIdx = sequence.indexOf("n5_g_28")
        val teKudasaiIdx = sequence.indexOf("n5_g_05")
        assertTrue("動詞のて形 must precede 〜てください in pedagogical sequence", teFormIdx < teKudasaiIdx)

        val dictFormIdx = sequence.indexOf("n5_g_29")
        assertTrue("動詞の辞書形 must precede 動詞のて形 in pedagogical sequence", dictFormIdx < teFormIdx)

        val naiFormIdx = sequence.indexOf("n5_g_30")
        val naideKudasaiIdx = sequence.indexOf("n5_g_15")
        assertTrue("動詞のない形 must precede 〜ないでください in pedagogical sequence", naiFormIdx < naideKudasaiIdx)

        val taFormIdx = sequence.indexOf("n5_g_31")
        val taKotoIdx = sequence.indexOf("n5_g_18")
        assertTrue("動詞のた形 must precede 〜たことがある in pedagogical sequence", taFormIdx < taKotoIdx)
    }

    @Test
    fun test7_particleProgressionAudited() {
        val sequence = GrammarPrerequisiteGraph.getPedagogicalSequence()
        val waIdx = sequence.indexOf("n5_g_01") // は / です
        val woIdx = sequence.indexOf("n5_g_02") // を
        val niIdx = sequence.indexOf("n5_g_03") // に / へ
        val deIdx = sequence.indexOf("n5_g_04") // で
        val moIdx = sequence.indexOf("n5_g_07") // も
        val noIdx = sequence.indexOf("n5_g_08") // の
        val toIdx = sequence.indexOf("n5_g_33") // と
        val karaMadeIdx = sequence.indexOf("n5_g_10") // から〜まで
        val gaIdx = sequence.indexOf("n5_g_09") // があります/います

        assertTrue("は must precede を", waIdx < woIdx)
        assertTrue("は must precede に", waIdx < niIdx)
        assertTrue("を must precede で (action location)", woIdx < deIdx)
        assertTrue("は must precede も", waIdx < moIdx)
        assertTrue("の must precede と (accompaniment)", noIdx < toIdx)
        assertTrue("に must precede から〜まで", niIdx < karaMadeIdx)
        assertTrue("は must precede があります", waIdx < gaIdx)
    }

    @Test
    fun test8_adjectiveFoundationPrecedesComparisons() {
        val sequence = GrammarPrerequisiteGraph.getPedagogicalSequence()
        val iAdjIdx = sequence.indexOf("n5_g_25") // い形容詞の活用
        val naAdjIdx = sequence.indexOf("n5_g_26") // な形容詞の活用
        val comparisonIdx = sequence.indexOf("n5_g_22") // 〜より / 〜のほうが
        val superlativeIdx = sequence.indexOf("n5_g_23") // 〜の中で〜が一番
        val excessiveIdx = sequence.indexOf("n5_g_37") // 〜すぎる

        assertTrue("い-adjective must precede comparison", iAdjIdx < comparisonIdx)
        assertTrue("な-adjective must precede comparison", naAdjIdx < comparisonIdx)
        assertTrue("Comparison must precede superlative", comparisonIdx < superlativeIdx)
        assertTrue("い-adjective must precede 〜すぎる", iAdjIdx < excessiveIdx)
    }

    @Test
    fun test9_dailyStudyPlannerDay1BeginnerMissionCorrectness() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)

        // Setup Day 1 absolute beginner profile
        LearnerModelEngine.initializeStartingProfile(
            db = db,
            startingLevel = BeginnerStartingLevel.ABSOLUTE_BEGINNER,
            dailyTargetMinutes = 15
        )

        val deckRepo = JapaneseDeckRepository(context)
        val mission = DailyStudyPlanner.getOrCreateTodayMission(db, deckRepo, forceEmergencyMode = false)

        assertNotNull(mission)
        assertEquals(3, mission.tasks.size)

        // Day 1 Beginner Mission MUST NOT contain reading or kanji tasks!
        val hasReading = mission.tasks.any { it.drillType == "READING" }
        val hasKanji = mission.tasks.any { it.drillType == "KANJI" }
        assertFalse("Day 1 beginner MUST NOT be assigned reading comprehension", hasReading)
        assertFalse("Day 1 beginner MUST NOT be assigned Kanji stroke lab", hasKanji)

        // Must prioritize Kana foundation and structured lesson progression
        val task1 = mission.tasks[0]
        val task2 = mission.tasks[1]
        val task3 = mission.tasks[2]

        assertEquals("Kana Foundation Drill", task1.title)
        assertEquals("KANA", task1.drillType)

        assertEquals("Syllabus Step: Lesson Study", task2.title)
        assertEquals("LESSON", task2.drillType)

        assertEquals("Kana Sound Recognition", task3.title)
        assertEquals("KANA", task3.drillType)
    }

    @Test
    fun test10_readingAndListeningReadinessGating() {
        // Zero knowledge beginner: Not ready for reading or listening
        assertFalse(PrerequisiteEngine.isReadingReady(0.05f, 0.0f, 0.0f))
        assertFalse(PrerequisiteEngine.isListeningReady(0.05f, 0.0f))

        // High kana mastery but no vocab or grammar: Not ready for reading
        assertFalse(PrerequisiteEngine.isReadingReady(0.90f, 0.10f, 0.10f))

        // Sufficient kana, vocab, and grammar: Ready for authentic reading!
        assertTrue(PrerequisiteEngine.isReadingReady(0.70f, 0.40f, 0.35f))
        assertTrue(PrerequisiteEngine.isListeningReady(0.70f, 0.40f))
    }

    @Test
    fun test11_beginnerOnboardingPathRoutingAndStateSynchronization() = runBlocking {
        val lessonRepo = JapaneseLessonRepository(context)

        // Track 1: Absolute Beginner
        val profileBeg = PrerequisiteEngine.createStartingProfile(BeginnerStartingLevel.ABSOLUTE_BEGINNER)
        lessonRepo.resetLessonProgress()
        assertEquals(0.05f, profileBeg.kanaMasteryScore, 0.001f)
        assertEquals(1, lessonRepo.getCurrentLessonId())
        assertTrue("Absolute beginner starts with 0 completed lessons", lessonRepo.getCompletedLessonIds().isEmpty())

        // Track 2: Kana-Known
        val profileKana = PrerequisiteEngine.createStartingProfile(BeginnerStartingLevel.SOME_KANA)
        lessonRepo.markLessonsCompleted((1..6).toList())
        lessonRepo.setCurrentLessonId(7)
        assertEquals(0.85f, profileKana.kanaMasteryScore, 0.001f)
        assertEquals(7, lessonRepo.getCurrentLessonId())
        assertEquals("Greetings & Foundation Kanji", lessonRepo.getCurrentLesson().title)
        assertEquals(6, lessonRepo.getCompletedLessonIds().size)

        // Track 3: Advanced Explorer
        val profileAdv = PrerequisiteEngine.createStartingProfile(BeginnerStartingLevel.ADVANCED_EXPLORER)
        lessonRepo.markLessonsCompleted((1..13).toList())
        lessonRepo.setCurrentLessonId(14)
        assertEquals(1.0f, profileAdv.kanaMasteryScore, 0.001f)
        assertEquals(14, lessonRepo.getCurrentLessonId())
        assertEquals(13, lessonRepo.getCompletedLessonIds().size)
    }

    @Test
    fun test12_freshBeginnerFullLearningPathSimulation() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val lessonRepo = JapaneseLessonRepository(context)
        lessonRepo.resetLessonProgress()

        // 1. Onboarding as Absolute Beginner
        LearnerModelEngine.initializeStartingProfile(db, BeginnerStartingLevel.ABSOLUTE_BEGINNER)
        var profile = db.learnerProfileDao().getProfile()!!
        assertEquals(0.05f, profile.kanaMasteryScore, 0.001f)

        // 2. Studies Lesson 1 (あいうえお)
        val lesson1 = lessonRepo.getCurrentLesson()
        assertEquals(1, lesson1.lessonNumber)
        assertEquals("Hiragana Vowels (あ・い・う・え・お)", lesson1.title)

        // Learner masters vowel kana
        repeat(5) {
            LearnerModelEngine.updateCompetency(db, "KANA", isSuccess = true, responseTimeMs = 1200L)
        }
        profile = db.learnerProfileDao().getProfile()!!
        assertTrue("Kana mastery increases to around 0.15f+", profile.kanaMasteryScore >= 0.14f)

        // 3. Completes Lesson 1
        lessonRepo.completeLesson(1)
        assertTrue(lessonRepo.isLessonCompleted(1))
        assertEquals(2, lessonRepo.getCurrentLessonId())
        assertEquals("Hiragana Ka-row (か・き・く・け・こ)", lessonRepo.getCurrentLesson().title)

        // 4. Kana -> Vocabulary Transition
        // Learner now knows vowels and ka-row
        val knownKana = setOf('あ', 'い', 'う', 'え', 'お', 'か', 'き', 'く', 'け', 'こ')
        val wordAka = JapaneseItem(id = "sim_v1", japanese = "あか", reading = "あか", romaji = "aka", meaning = "Red", category = JapaneseCategory.VOCAB)
        val evalAka = VocabPrerequisiteModel.evaluatePrerequisites(wordAka, knownKana, emptySet(), emptySet())
        assertTrue("Word 'あか' using known vowels and ka-row is appropriate", evalAka.isAppropriate)

        // Word using unlearned Sa-row is safely held back
        val wordKasa = JapaneseItem(id = "sim_v2", japanese = "かさ", reading = "かさ", romaji = "kasa", meaning = "Umbrella", category = JapaneseCategory.VOCAB)
        val evalKasa = VocabPrerequisiteModel.evaluatePrerequisites(wordKasa, knownKana, emptySet(), emptySet())
        assertFalse("Word 'かさ' requiring unlearned 'さ' is held back until Sa-row is learned", evalKasa.isAppropriate)
        assertTrue(evalKasa.missingKana.contains('さ'))
    }
}
