package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.noignore.data.AppDatabase
import com.example.noignore.data.content.JlptContentEntity
import com.example.noignore.data.learner.MistakeRecordEntity
import com.example.noignore.data.srs.FlashcardSrsEntity
import com.example.noignore.japanese.curriculum.JlptAssetContentLoader
import com.example.noignore.japanese.curriculum.graph.KnowledgeGraphService
import com.example.noignore.japanese.curriculum.validation.CurriculumGraphValidator
import com.example.noignore.japanese.data.JapaneseDeckRepository
import com.example.noignore.japanese.engine.DailyStudyPlanner
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
class CurriculumGraphTest {

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
    fun test1_knownKanjiToVocabularyRelationshipStoredAndRetrieved() = runBlocking {
        // Ingest bundled assets into Room
        val count = JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        assertEquals(789, count)

        // Retrieve Kanji 水 (n5_k_18)
        val kanji = db.jlptContentDao().getItemById("n5_k_18")
        assertNotNull("Kanji n5_k_18 must exist", kanji)
        assertEquals("水", kanji!!.japanese)
        assertTrue("Kanji n5_k_18 must relate to vocabulary n5_v_32", kanji.getRelatedItemIds().contains("n5_v_32"))

        // Retrieve Vocabulary 水 (n5_v_32)
        val vocab = db.jlptContentDao().getItemById("n5_v_32")
        assertNotNull("Vocab n5_v_32 must exist", vocab)
        assertEquals("水", vocab!!.japanese)
        assertTrue("Vocab n5_v_32 must relate to Kanji n5_k_18", vocab.getRelatedItemIds().contains("n5_k_18"))

        // Retrieve via DAO query
        val referencing = db.jlptContentDao().getItemsReferencing("n5_k_18")
        assertTrue("DAO query must find items referencing n5_k_18", referencing.any { it.id == "n5_v_32" })
    }

    @Test
    fun test2_vocabularyToReadingRelationshipStored() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)

        // Vocabulary 駅 (n5_v_56) is used in Reading n5_r_03
        val vocab = db.jlptContentDao().getItemById("n5_v_56")
        assertNotNull(vocab)
        assertTrue("Vocab 駅 must link to Reading n5_r_03", vocab!!.getRelatedItemIds().contains("n5_r_03"))

        val reading = db.jlptContentDao().getItemById("n5_r_03")
        assertNotNull(reading)
        assertTrue("Reading n5_r_03 must link back to Vocab 駅 (n5_v_56)", reading!!.getRelatedItemIds().contains("n5_v_56"))
    }

    @Test
    fun test3_grammarToReadingRelationshipStored() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)

        // Grammar 〜前に / 〜後で (n5_g_20) is in Reading n5_r_01 ("朝ごはんを食べた後で")
        val grammar = db.jlptContentDao().getItemById("n5_g_20")
        assertNotNull(grammar)
        assertTrue("Grammar n5_g_20 must link to Reading n5_r_01", grammar!!.getRelatedItemIds().contains("n5_r_01"))

        val reading = db.jlptContentDao().getItemById("n5_r_01")
        assertNotNull(reading)
        assertTrue("Reading n5_r_01 must link to Grammar n5_g_20", reading!!.getRelatedItemIds().contains("n5_g_20"))
    }

    @Test
    fun test4_examQuestionToKnowledgeRelationshipStored() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)

        // Exam question n1_q_01 tests grammar n1_g_01 (〜たるもの)
        val question = db.jlptContentDao().getItemById("n1_q_01")
        assertNotNull(question)
        assertEquals("JLPT_EXAM", question!!.category)
        assertTrue("Question n1_q_01 must link to grammar n1_g_01", question.getRelatedItemIds().contains("n1_g_01"))

        val grammar = db.jlptContentDao().getItemById("n1_g_01")
        assertNotNull(grammar)
        assertTrue("Grammar n1_g_01 must link to Question n1_q_01", grammar!!.getRelatedItemIds().contains("n1_q_01"))
    }

    @Test
    fun test5_invalidRelationshipIdsRejectedByValidator() {
        val existingIds = setOf("n5_k_01", "n5_v_01")

        // Test broken reference
        val brokenIssues = CurriculumGraphValidator.validateSingleRelationship("n5_k_01", "non_existent_id", existingIds)
        assertEquals(1, brokenIssues.size)
        assertTrue(brokenIssues[0].contains("does not exist in curriculum"))

        // Test self-reference
        val selfIssues = CurriculumGraphValidator.validateSingleRelationship("n5_k_01", "n5_k_01", existingIds)
        assertEquals(1, selfIssues.size)
        assertTrue(selfIssues[0].contains("cannot link to itself"))

        // Test graph validator with malformed items
        val malformedItems = listOf(
            JlptContentEntity(id = "item_1", japanese = "日", reading = "ひ", meaning = "sun", category = "KANJI", relatedItems = "item_1,item_999,item_999"),
            JlptContentEntity(id = "item_2", japanese = "月", reading = "つき", meaning = "moon", category = "KANJI", relatedItems = "")
        )
        val report = CurriculumGraphValidator.validateGraph(malformedItems)
        assertEquals(3, report.totalRelationships)
        assertEquals(0, report.validRelationships)
        assertEquals(3, report.invalidRelationships)
        assertEquals(1, report.selfReferences)
        assertEquals(1, report.duplicates)
        assertEquals(1, report.brokenReferences)
    }

    @Test
    fun test6_originalBaselinePreservedAndExpanded() = runBlocking {
        val count = JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        assertEquals("Expanded curriculum count must be 789", 789, count)

        // Verify categories count
        val kanji = db.jlptContentDao().getContentByCategory("KANJI")
        val vocab = db.jlptContentDao().getContentByCategory("VOCAB")
        val grammar = db.jlptContentDao().getContentByCategory("GRAMMAR")
        val reading = db.jlptContentDao().getContentByCategory("READING")
        val listening = db.jlptContentDao().getContentByCategory("LISTENING")
        val exam = db.jlptContentDao().getContentByCategory("JLPT_EXAM")
        val kana = db.jlptContentDao().getContentByCategory("KANA")

        assertEquals(169, kanji.size)
        assertEquals(266, vocab.size)
        assertEquals(80, grammar.size)
        assertEquals(15, reading.size)
        assertEquals(14, listening.size)
        assertEquals(34, exam.size)
        assertEquals(211, kana.size)
        assertEquals(789, kanji.size + vocab.size + grammar.size + reading.size + listening.size + exam.size + kana.size)

        // Verify key IDs still have their original fields intact
        val n5k1 = db.jlptContentDao().getItemById("n5_k_01")
        assertNotNull(n5k1)
        assertEquals("一", n5k1!!.japanese)
        assertEquals("いち", n5k1.reading)

        val n1v1 = db.jlptContentDao().getItemById("n1_v_01")
        assertNotNull(n1v1)
        assertEquals("一概に", n1v1!!.japanese)

        // Verify new Phase 4 items exist with provenance
        val n5v98 = db.jlptContentDao().getItemById("n5_v_98")
        assertNotNull(n5v98)
        assertEquals("二つ", n5v98!!.japanese)
        assertEquals("ふたつ", n5v98.reading)
        assertEquals("NoIgnore Curriculum/JMdict reference", n5v98.source)

        val n5g24 = db.jlptContentDao().getItemById("n5_g_24")
        assertNotNull(n5g24)
        assertEquals("NoIgnore Pedagogical Grammar Suite", n5g24!!.source)
    }

    @Test
    fun test7_userSrsDataSurvivesGraphPopulation() = runBlocking {
        val srsDao = db.flashcardSrsDao()

        // 1. Insert existing user SRS progress before curriculum load
        val testCard = FlashcardSrsEntity(
            cardId = "n5_v_32",
            category = "VOCAB",
            jlptLevel = "N5",
            repetition = 3,
            intervalDays = 7,
            easeFactor = 2.5f,
            dueDateMs = System.currentTimeMillis() + 86400000L,
            lastReviewedMs = System.currentTimeMillis(),
            lapses = 0,
            state = "REVIEW",
            totalReviews = 5,
            lastRating = "GOOD"
        )
        srsDao.insertOrUpdate(testCard)

        // Verify user SRS card is present
        val savedCard = srsDao.getCardSrs("n5_v_32")
        assertNotNull(savedCard)
        assertEquals(5, savedCard!!.totalReviews)

        // 2. Now perform curriculum asset ingestion with relationships
        val count = JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        assertEquals(789, count)

        // 3. Verify user SRS card is 100% intact with all history preserved
        val postLoadCard = srsDao.getCardSrs("n5_v_32")
        assertNotNull("User SRS card must survive curriculum graph load", postLoadCard)
        assertEquals(5, postLoadCard!!.totalReviews)
        assertEquals(7, postLoadCard.intervalDays)
        assertEquals("REVIEW", postLoadCard.state)
        assertEquals("GOOD", postLoadCard.lastRating)
    }

    @Test
    fun test8_curriculumGraphValidatorOnAllCurriculum() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val allItems = db.jlptContentDao().getAllContent()
        assertEquals(789, allItems.size)

        val report = CurriculumGraphValidator.validateGraph(allItems)
        println(report.formatReport())

        assertEquals("Zero broken references expected", 0, report.brokenReferences)
        assertEquals("Zero duplicate relationships expected", 0, report.duplicates)
        assertEquals("Zero self-references expected", 0, report.selfReferences)
        assertEquals("Zero invalid relationships expected", 0, report.invalidRelationships)
        assertEquals(report.totalRelationships, report.validRelationships)
        assertEquals(635, report.connectedItemsCount)
        assertEquals(154, report.isolatedItemsCount)
    }

    @Test
    fun test9_knowledgeGraphServiceRemediationPlan() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)

        // Test remediation for failed vocabulary item 水 (n5_v_32)
        val vocabWater = db.jlptContentDao().getItemById("n5_v_32")!!
        val plan = KnowledgeGraphService.buildRemediationPlan(db, vocabWater)

        assertEquals("n5_v_32", plan.targetItem.id)
        assertTrue("Remediation must include prerequisite kanji 水", plan.prerequisiteKanji.any { it.id == "n5_k_18" })
        assertTrue("Summary explanation must describe targeted remediation", plan.summaryExplanation.contains("Targeted Vocabulary Remediation"))

        // Test graph statistics
        val stats = KnowledgeGraphService.calculateStatistics(db.jlptContentDao().getAllContent())
        assertEquals(789, stats.totalCurriculumItems)
        assertEquals(635, stats.connectedItems)
        assertEquals(154, stats.isolatedItems)
        assertEquals(937, stats.totalRelationshipEdges)
        assertEquals(398, stats.kanjiToVocabEdges)
        assertEquals(80, stats.vocabToReadingEdges)
        assertEquals(70, stats.vocabToListeningEdges)
        assertEquals(25, stats.grammarToReadingEdges)
        assertEquals(25, stats.grammarToListeningEdges)
        assertEquals(72, stats.examToKnowledgeEdges)
        assertEquals(81, stats.vocabToGrammarEdges)
        assertEquals(175, stats.kanaToVocabEdges)
    }

    @Test
    fun test10_dailyStudyPlannerIntegratesKnowledgeGraphTasks() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)

        // Record a leech mistake on vocabulary 水 (n5_v_32)
        db.mistakeRecordDao().insertOrUpdate(
            MistakeRecordEntity(
                cardId = "n5_v_32",
                jlptLevel = "N5",
                category = "VOCAB",
                lapseCount = 5,
                totalAttempts = 6,
                isLeech = true
            )
        )

        val deckRepo = JapaneseDeckRepository(context)
        val mission = DailyStudyPlanner.getOrCreateTodayMission(db, deckRepo, forceEmergencyMode = false)

        assertNotNull(mission)
        assertTrue("Mission tasks must include targeted knowledge graph remediation",
            mission.tasks.any { it.title == "Targeted Vocab Remediation" || it.title == "Leech Intervention" })
    }

    @Test
    fun test11_curriculumValidationBatchCheck() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val allItems = db.jlptContentDao().getAllContent()
        assertEquals(789, allItems.size)

        val (validItems, report) = com.example.noignore.japanese.curriculum.validation.ContentValidator.validateBatch(allItems)
        val rejections = report.filter { it.startsWith("[REJECTED]") }
        assertEquals("Zero curriculum items should be rejected by ContentValidator: $rejections", 0, rejections.size)
        assertEquals(789, validItems.size)
    }

    @Test
    fun test12_internalCurriculumTargetsVerified() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val n5Items = db.jlptContentDao().getContentByLevel("N5")

        val n5Kanji = n5Items.filter { it.category == "KANJI" }
        val n5Vocab = n5Items.filter { it.category == "VOCAB" }
        val n5Grammar = n5Items.filter { it.category == "GRAMMAR" }
        val n5Reading = n5Items.filter { it.category == "READING" }
        val n5Listening = n5Items.filter { it.category == "LISTENING" }
        val n5Exam = n5Items.filter { it.category == "JLPT_EXAM" }
        val n5Kana = n5Items.filter { it.category == "KANA" }

        assertTrue("N5 Kanji target 100+ met", n5Kanji.size >= 100)
        assertTrue("N5 Vocab internal target 200+ met (actual ${n5Vocab.size})", n5Vocab.size >= 200)
        assertTrue("N5 Grammar internal target 45+ met (actual ${n5Grammar.size})", n5Grammar.size >= 45)
        assertTrue("N5 Reading internal target 12+ met (actual ${n5Reading.size})", n5Reading.size >= 12)
        assertTrue("N5 Listening internal target 12+ met (actual ${n5Listening.size})", n5Listening.size >= 12)
        assertTrue("N5 Exam questions internal target 30+ met (actual ${n5Exam.size})", n5Exam.size >= 30)
        assertTrue("N5 Kana foundation target 200+ met (actual ${n5Kana.size})", n5Kana.size >= 200)

        // Verify N5 Kanji isolated count is exactly 0
        val isolatedN5Kanji = n5Kanji.filter { kanji ->
            val rels = kanji.relatedItems.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            val incoming = n5Items.any { other -> other.id != kanji.id && other.relatedItems.contains(kanji.id) }
            rels.isEmpty() && !incoming
        }
        assertEquals("N5 Kanji must have zero isolated items", 0, isolatedN5Kanji.size)
    }

    @Test
    fun test13_grammarFormationRulesPopulated() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val grammarItems = db.jlptContentDao().getContentByCategory("GRAMMAR").filter { it.jlptLevel == "N5" }
        assertEquals(45, grammarItems.size)
        assertTrue("All 45 N5 grammar items must have non-blank formation structure", grammarItems.all { it.structure.isNotBlank() })

        val deParticle = grammarItems.first { it.id == "n5_g_04" }
        assertTrue("Formation rule contains expected structure", deParticle.structure.contains("で + Action Verb"))

        val teWaIkemasen = grammarItems.first { it.id == "n5_g_13" }
        assertTrue("Formation rule contains expected structure", teWaIkemasen.structure.contains("Verb [て-form] + はいけません"))
    }

    @Test
    fun test14_kanaCurriculumFoundationPresentAndConnected() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val kanaItems = db.jlptContentDao().getContentByCategory("KANA")
        assertEquals(211, kanaItems.size)

        val hiragana = kanaItems.filter { it.id.startsWith("kana_h_") }
        val katakana = kanaItems.filter { it.id.startsWith("kana_k_") }
        assertEquals(105, hiragana.size)
        assertEquals(106, katakana.size)

        assertTrue("Kana items must have non-blank meaning", kanaItems.all { it.meaning.isNotBlank() })
        val aChar = hiragana.first { it.japanese == "あ" }
        assertTrue("Hiragana 'あ' must link to vocabulary like '朝'", aChar.getRelatedItemIds().isNotEmpty())
    }

    @Test
    fun test15_deduplicationPreservesSrsAndConsolidatesCanonicalIds() {
        val dupId = "n5_v_212"
        val canonicalId = com.example.noignore.japanese.curriculum.canonical.CanonicalCurriculumIds.canonicalize(dupId)
        assertEquals("n5_v_03", canonicalId)

        val srs1 = FlashcardSrsEntity(cardId = dupId, totalReviews = 4, lapses = 1)
        val srs2 = FlashcardSrsEntity(cardId = canonicalId, totalReviews = 6, lapses = 2)
        val consolidated = com.example.noignore.japanese.curriculum.canonical.CanonicalCurriculumIds.consolidateSrs(srs2, srs1)
        assertEquals(canonicalId, consolidated.cardId)
        assertEquals(10, consolidated.totalReviews)
        assertEquals(3, consolidated.lapses)
    }

    @Test
    fun test16_learnerProfileSupportsKanaMasteryScore() = runBlocking {
        val profile = com.example.noignore.japanese.engine.LearnerModelEngine.getOrInitProfile(db)
        assertNotNull(profile)
        assertTrue("Default profile includes kanaMasteryScore", profile.kanaMasteryScore >= 0f)

        com.example.noignore.japanese.engine.LearnerModelEngine.updateCompetency(
            db = db,
            category = "KANA",
            isSuccess = true,
            responseTimeMs = 1200L
        )
        val updated = db.learnerProfileDao().getProfile()
        assertNotNull(updated)
        assertTrue("Kana mastery increases on success", updated!!.kanaMasteryScore > profile.kanaMasteryScore)
    }
}
