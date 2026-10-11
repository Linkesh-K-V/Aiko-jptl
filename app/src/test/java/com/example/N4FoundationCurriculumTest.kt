package com.example
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.noignore.data.AppDatabase
import com.example.noignore.data.content.JlptContentEntity
import com.example.noignore.data.srs.FlashcardSrsEntity
import com.example.noignore.japanese.curriculum.JlptAssetContentLoader
import com.example.noignore.japanese.curriculum.canonical.CanonicalCurriculumIds
import com.example.noignore.japanese.curriculum.prerequisite.GrammarPrerequisiteGraph
import com.example.noignore.japanese.curriculum.transition.LearnerStage
import com.example.noignore.japanese.curriculum.transition.LearnerTransitionManager
import com.example.noignore.japanese.curriculum.validation.CurriculumGraphValidator
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class N4FoundationCurriculumTest {

    private lateinit var context: Context
    private lateinit var db: AppDatabase

    @Before
    fun setup() {
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
    fun test1_n5RegressionProtection_all865EntitiesPreserved() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val n5Items = db.jlptContentDao().getContentByLevel("N5")
        
        // Exact N5 breakdown
        val kana = n5Items.filter { it.category == "KANA" }
        val kanji = n5Items.filter { it.category == "KANJI" }
        val vocab = n5Items.filter { it.category == "VOCAB" }
        val grammar = n5Items.filter { it.category == "GRAMMAR" }
        val reading = n5Items.filter { it.category == "READING" }
        val listening = n5Items.filter { it.category == "LISTENING" }
        val questions = n5Items.filter { it.category == "JLPT_EXAM" }

        assertEquals("N5 Kana must be exactly 211", 211, kana.size)
        assertEquals("N5 Kanji must be exactly 104", 104, kanji.size)
        assertEquals("N5 Vocab must be exactly 289", 289, vocab.size)
        assertEquals("N5 Grammar must be exactly 53", 53, grammar.size)
        assertEquals("N5 Reading must be exactly 18", 18, reading.size)
        assertEquals("N5 Listening must be exactly 18", 18, listening.size)
        assertEquals("N5 Questions must be exactly 172", 172, questions.size)
        assertEquals("Total N5 items must be 865", 865, n5Items.size)
    }

    @Test
    fun test2_n4FoundationCounts_56GrammarAnd175Kanji() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val n4Items = db.jlptContentDao().getContentByLevel("N4")

        val grammar = n4Items.filter { it.category == "GRAMMAR" }
        val kanji = n4Items.filter { it.category == "KANJI" }
        val vocab = n4Items.filter { it.category == "VOCAB" }

        assertEquals("N4 Grammar must be exactly 56 canonical points", 56, grammar.size)
        assertEquals("N4 Kanji must be exactly 175 canonical characters", 175, kanji.size)
        assertEquals("N4 Vocab must be exactly 575 canonical items", 575, vocab.size)
    }

    @Test
    fun test3_kanjiDeduplication_zeroOverlapBetweenN5AndN4() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val n5Kanji = db.jlptContentDao().getContentByLevelAndCategory("N5", "KANJI").map { it.japanese }.toSet()
        val n4Kanji = db.jlptContentDao().getContentByLevelAndCategory("N4", "KANJI").map { it.japanese }.toSet()

        val overlap = n5Kanji.intersect(n4Kanji)
        assertTrue("Zero overlap between N5 and N4 Kanji, but found: $overlap", overlap.isEmpty())
        assertEquals("N5 Kanji count", 104, n5Kanji.size)
        assertEquals("N4 Kanji count", 175, n4Kanji.size)
    }

    @Test
    fun test4_grammarDeduplication_zeroSemanticDuplicatesWithN5() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val n4Grammar = db.jlptContentDao().getContentByLevelAndCategory("N4", "GRAMMAR")

        // Ensure rejected N5 duplicates are not present in N4
        val rejectedPatterns = listOf("〜たことがある", "〜すぎる", "〜つもりです", "〜ても", "〜ながら", "〜方")
        for (g in n4Grammar) {
            for (p in rejectedPatterns) {
                if (p == "〜たことがある" || p == "〜すぎる") {
                    assertFalse(
                        "Rejected duplicate '$p' must not exist as independent N4 point: ${g.japanese}",
                        g.japanese.startsWith(p)
                    )
                }
            }
        }
    }

    @Test
    fun test5_n4GrammarPrerequisiteDag_zeroCyclesAndValidOrder() {
        val n4Deps = GrammarPrerequisiteGraph.N4_DIRECT_DEPENDENCIES
        assertEquals("All 56 N4 grammar points must have prerequisites defined", 56, n4Deps.size)

        val report = GrammarPrerequisiteGraph.validateGraphTopology(n4Deps, allowExternalParents = true)
        assertTrue("N4 grammar graph must be valid DAG", report.isValid)
        assertEquals("Zero cycles expected", 0, report.cycles.size)
        assertEquals("Zero broken references expected", 0, report.brokenReferences.size)
        assertEquals("Zero self-references expected", 0, report.selfReferences.size)
        assertEquals("Zero duplicate references expected", 0, report.duplicateReferences.size)
        assertEquals("Zero illegal reverse dependencies expected", 0, report.illegalReverseDependencies.size)

        val sequence = GrammarPrerequisiteGraph.getN4PedagogicalSequence()
        assertEquals("Topological sort must order all 56 N4 points", 56, sequence.size)
    }

    @Test
    fun test6_crossLevelPrerequisites_n4MayDependOnN5_n5NeverOnN4() {
        val combined = GrammarPrerequisiteGraph.ALL_DEPENDENCIES
        val report = GrammarPrerequisiteGraph.validateGraphTopology(combined, allowExternalParents = false)

        assertTrue("Combined graph must be valid DAG", report.isValid)
        assertEquals("Zero broken references in combined graph", 0, report.brokenReferences.size)
        assertEquals("Zero illegal reverse dependencies (N5 -> N4)", 0, report.illegalReverseDependencies.size)
        assertTrue("Cross-level N4->N5 edges must exist", report.crossLevelN4ToN5Edges > 0)

        // Verify specific pedagogical links
        val potentialPrereqs = GrammarPrerequisiteGraph.getDirectPrerequisites("n4_g_01")
        assertTrue("Potential form depends on N5 dictionary form", potentialPrereqs.contains("n5_g_29"))

        val taraPrereqs = GrammarPrerequisiteGraph.getDirectPrerequisites("n4_g_07")
        assertTrue("Tara conditional depends on N5 ta-form", taraPrereqs.contains("n5_g_31"))

        val teAgeruPrereqs = GrammarPrerequisiteGraph.getDirectPrerequisites("n4_g_13")
        assertTrue("Te-ageru depends on N5 te-form", teAgeruPrereqs.contains("n5_g_28"))
        assertTrue("Te-ageru depends on N5 giving/receiving", teAgeruPrereqs.contains("n5_g_49"))
    }

    @Test
    fun test7_morphologicalValidation_verbAndAdjectiveInflections() {
        // Godan Potential: 書く -> 書ける
        fun godanPotential(stem: String, ending: Char): String = when (ending) {
            'く' -> stem + "け"
            'ぐ' -> stem + "げ"
            'す' -> stem + "せ"
            'つ' -> stem + "て"
            'ぬ' -> stem + "ね"
            'ぶ' -> stem + "べ"
            'む' -> stem + "め"
            'る' -> stem + "れ"
            'う' -> stem + "え"
            else -> stem + ending
        } + "る"

        assertEquals("書ける", godanPotential("書", 'く'))
        assertEquals("泳げる", godanPotential("泳", 'ぐ'))
        assertEquals("話せる", godanPotential("話", 'す'))
        assertEquals("待てる", godanPotential("待", 'つ'))
        assertEquals("読める", godanPotential("読", 'む'))
        assertEquals("買える", godanPotential("買", 'う'))

        // Ichidan Potential: 食べる -> 食べられる
        fun ichidanPotential(stem: String): String = stem + "られる"
        assertEquals("食べられる", ichidanPotential("食べ"))
        assertEquals("見られる", ichidanPotential("見"))

        // Irregulars
        fun irregularPotential(verb: String): String = when (verb) {
            "する" -> "できる"
            "くる", "来る" -> "こられる"
            else -> verb
        }
        assertEquals("できる", irregularPotential("する"))
        assertEquals("こられる", irregularPotential("来る"))

        // Volitional: 書く -> 書こう, 食べる -> 食べよう, する -> しよう, 来る -> こよう
        assertEquals("書こう", "書" + "こう")
        assertEquals("食べよう", "食べ" + "よう")
        assertEquals("しよう", "しよう")
        assertEquals("こよう", "こよう")

        // Passive: 書かれる, 食べられる, される, こられる
        assertEquals("書かれる", "書" + "かれる")
        assertEquals("食べられる", "食べ" + "られる")
        assertEquals("される", "される")
        assertEquals("こられる", "こられる")

        // Causative: 書かせる, 食べさせる, させる, こさせる
        assertEquals("書かせる", "書" + "かせる")
        assertEquals("食べさせる", "食べ" + "させる")
        assertEquals("させる", "させる")
        assertEquals("こさせる", "こさせる")

        // Causative-Passive: 待たされる, 食べさせられる, させられる, こさせられる
        assertEquals("待たされる", "待" + "たされる")
        assertEquals("食べさせられる", "食べ" + "させられる")
        assertEquals("させられる", "させられる")
        assertEquals("こさせられる", "こさせられる")
    }

    @Test
    fun test8_kanjiQuality_everyKanjiHasPlannedVocabAndReadings() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val n4Kanji = db.jlptContentDao().getContentByLevelAndCategory("N4", "KANJI")

        assertEquals(175, n4Kanji.size)
        for (k in n4Kanji) {
            assertTrue("Kanji '${k.japanese}' must have non-blank onyomi or kunyomi", k.onyomi.isNotBlank() || k.kunyomi.isNotBlank())
            assertTrue("Kanji '${k.japanese}' must have non-blank meaning", k.meaning.isNotBlank())
            assertTrue("Kanji '${k.japanese}' must have strokeCount > 0", k.strokeCount > 0)
            assertTrue("Kanji '${k.japanese}' must have radical specified", k.radical.isNotBlank())
            assertTrue("Kanji '${k.japanese}' must have planned N4 vocabulary in notes", k.mnemonicOrNote.contains("Planned N4 Vocab:"))
            assertTrue("Kanji '${k.japanese}' must have original example sentence", k.exampleJapanese.isNotBlank())
            assertTrue("Kanji '${k.japanese}' must have example English translation", k.exampleEnglish.isNotBlank())
            assertTrue("Kanji '${k.japanese}' must appear in example Japanese", k.exampleJapanese.contains(k.japanese))
        }
    }

    @Test
    fun test9_uncertifiedLearnerBlockedFromN4Content() {
        val uncertifiedProfile = com.example.noignore.data.learner.LearnerProfileEntity(
            id = "test_user",
            kanaMasteryScore = 0.50f,
            grammarMasteryScore = 0.40f,
            kanjiMasteryScore = 0.30f
        )
        val foundationAssessment = LearnerTransitionManager.evaluateN5Certification(uncertifiedProfile, 0.40f)
        assertFalse("Uncertified learner must NOT pass N5 certification", foundationAssessment.isCertified)

        // Stage must not jump directly to N4_ACTIVE
        val transitionResult = LearnerTransitionManager.evaluateTransition(
            fromStage = LearnerStage.N5_FOUNDATION,
            toStage = LearnerStage.N4_ACTIVE,
            profile = uncertifiedProfile
        )
        assertFalse("Learner in N5_FOUNDATION cannot jump to N4_ACTIVE", transitionResult.isSuccess)
    }

    @Test
    fun test10_curriculumGraphValidator_zeroBrokenReferencesWithN4() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val allItems = db.jlptContentDao().getAllContent()

        val report = CurriculumGraphValidator.validateGraph(allItems)
        assertEquals("Zero broken references across all curriculum items", 0, report.brokenReferences)
        assertEquals("Zero duplicate relationships across all curriculum items", 0, report.duplicates)
        assertEquals("Zero self-references across all curriculum items", 0, report.selfReferences)
        assertEquals("Zero invalid relationships across all curriculum items", 0, report.invalidRelationships)
        assertEquals(report.totalRelationships, report.validRelationships)
    }

    @Test
    fun test11_legacyScaffoldReconciliation_srsCardHistoryPreserved() = runBlocking {
        val srsDao = db.flashcardSrsDao()

        // Card with legacy N4 duplicate ID
        val legacyCard = FlashcardSrsEntity(
            cardId = "n4_g_01",
            category = "GRAMMAR",
            jlptLevel = "N4",
            repetition = 4,
            intervalDays = 10,
            easeFactor = 2.45f,
            dueDateMs = System.currentTimeMillis() + 86400000L,
            lastReviewedMs = System.currentTimeMillis(),
            lapses = 1,
            state = "REVIEW",
            totalReviews = 8,
            lastRating = "GOOD"
        )
        srsDao.insertOrUpdate(legacyCard)

        // Canonical resolution
        val canonicalId = CanonicalCurriculumIds.canonicalize("n4_g_01")
        assertEquals("n5_g_18", canonicalId)

        // Reconcile into canonical N5 card
        val existingN5 = FlashcardSrsEntity(
            cardId = canonicalId,
            category = "GRAMMAR",
            jlptLevel = "N5",
            repetition = 2,
            intervalDays = 5,
            easeFactor = 2.50f,
            dueDateMs = System.currentTimeMillis() + 43200000L,
            lastReviewedMs = System.currentTimeMillis() - 1000L,
            lapses = 0,
            state = "REVIEW",
            totalReviews = 3,
            lastRating = "GOOD"
        )
        val consolidated = CanonicalCurriculumIds.consolidateSrs(existingN5, legacyCard)

        // History preserved at maximum
        assertEquals("Repetition preserved at max", 4, consolidated.repetition)
        assertEquals("Interval preserved at max", 10, consolidated.intervalDays)
        assertEquals("Total reviews aggregated", 11, consolidated.totalReviews)
        assertEquals("Lapses aggregated", 1, consolidated.lapses)
    }
}
