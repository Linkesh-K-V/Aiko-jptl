package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.noignore.data.AppDatabase
import com.example.noignore.data.content.JlptContentEntity
import com.example.noignore.data.learner.LearnerProfileEntity
import com.example.noignore.data.srs.FlashcardSrsEntity
import com.example.noignore.japanese.curriculum.JlptAssetContentLoader
import com.example.noignore.japanese.curriculum.canonical.CanonicalCurriculumIds
import com.example.noignore.japanese.curriculum.prerequisite.GrammarPrerequisiteGraph
import com.example.noignore.japanese.curriculum.prerequisite.PrerequisiteEngine
import com.example.noignore.japanese.curriculum.transition.LearnerStage
import com.example.noignore.japanese.curriculum.transition.LearnerTransitionManager
import com.example.noignore.japanese.curriculum.validation.CurriculumGraphValidator
import com.example.noignore.japanese.data.JapaneseDeckRepository
import com.example.noignore.japanese.engine.DailyStudyPlanner
import com.example.noignore.japanese.engine.JlptSearchEngine
import com.example.noignore.japanese.engine.SearchFilter
import com.example.noignore.japanese.model.JapaneseCategory
import com.example.noignore.japanese.model.JlptExamLevel
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
class N4FoundationCertificationTest {

    private lateinit var context: Context
    private lateinit var db: AppDatabase
    private lateinit var repo: JapaneseDeckRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repo = JapaneseDeckRepository(context)
    }

    @After
    fun tearDown() {
        db.close()
    }

    // =========================================================================
    // PART 1 & 2: INVENTORY RECONCILIATION & LEGACY N4 AUDIT
    // =========================================================================

    @Test
    fun test01_curriculumInventory_1204TotalEntitiesReconciled() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val allItems = db.jlptContentDao().getAllContent()
        assertEquals("Total database entities must be exactly 1,764", 1764, allItems.size)

        val byLevel = allItems.groupBy { it.jlptLevel.uppercase() }
        assertEquals(865, byLevel["N5"]?.size)
        assertEquals(810, byLevel["N4"]?.size)
        assertEquals(35, byLevel["N3"]?.size)
        assertEquals(30, byLevel["N2"]?.size)
        assertEquals(24, byLevel["N1"]?.size)

        // N4 breakdown: 56 grammar + 175 kanji + 575 canonical vocab + 2 reading + 1 listening + 1 questions = 810
        val n4Items = byLevel["N4"]!!
        val n4Grammar = n4Items.filter { it.category == "GRAMMAR" }
        val n4Kanji = n4Items.filter { it.category == "KANJI" }
        val n4Vocab = n4Items.filter { it.category == "VOCAB" }
        val n4Reading = n4Items.filter { it.category == "READING" }
        val n4Listening = n4Items.filter { it.category == "LISTENING" }
        val n4Questions = n4Items.filter { it.category == "JLPT_EXAM" }

        assertEquals(56, n4Grammar.size)
        assertEquals(175, n4Kanji.size)
        assertEquals(575, n4Vocab.size)
        assertEquals(2, n4Reading.size)
        assertEquals(1, n4Listening.size)
        assertEquals(1, n4Questions.size)
    }

    // =========================================================================
    // PART 3, 4, 5, 6: GRAMMAR, KANJI & VOCABULARY SEPARATION
    // =========================================================================

    @Test
    fun test02_grammarAndKanjiDeduplicationAgainstN5() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val n5Kanji = db.jlptContentDao().getContentByLevelAndCategory("N5", "KANJI").map { it.japanese }.toSet()
        val n4Kanji = db.jlptContentDao().getContentByLevelAndCategory("N4", "KANJI").map { it.japanese }.toSet()

        val overlap = n5Kanji.intersect(n4Kanji)
        assertTrue("Zero overlap between N5 and N4 Kanji, but found: $overlap", overlap.isEmpty())
        assertEquals(104, n5Kanji.size)
        assertEquals(175, n4Kanji.size)

        // N4 Kanji planned vocab integrated into real graph edges in Phase 19
        val n4KanjiEntities = db.jlptContentDao().getContentByLevelAndCategory("N4", "KANJI")
        var totalKanjiEdges = 0
        for (k in n4KanjiEntities) {
            assertTrue("Kanji '${k.japanese}' must have Planned N4 Vocab in note", k.mnemonicOrNote.contains("Planned N4 Vocab:"))
            val related = k.getRelatedItemIds()
            assertTrue("Kanji '${k.japanese}' must have connected vocabulary in Phase 19", related.isNotEmpty())
            totalKanjiEdges += related.size
        }
        assertTrue("Total N4 Kanji vocabulary edges must be at least 300", totalKanjiEdges >= 300)
    }

    // =========================================================================
    // PART 7: KNOWLEDGE GRAPH DAG INTEGRITY
    // =========================================================================

    @Test
    fun test03_knowledgeGraphTopologyAndZeroIllegalDependencies() {
        val combined = GrammarPrerequisiteGraph.ALL_DEPENDENCIES
        val report = GrammarPrerequisiteGraph.validateGraphTopology(combined, allowExternalParents = false)

        assertTrue("Combined graph must be valid DAG", report.isValid)
        assertEquals("Zero broken references", 0, report.brokenReferences.size)
        assertEquals("Zero duplicate references", 0, report.duplicateReferences.size)
        assertEquals("Zero self references", 0, report.selfReferences.size)
        assertEquals("Zero cycles", 0, report.cycles.size)
        assertEquals("Zero illegal N5->N4 reverse dependencies", 0, report.illegalReverseDependencies.size)
        assertTrue("N4->N5 dependencies must exist", report.crossLevelN4ToN5Edges > 0)
    }

    // =========================================================================
    // PART 8: BLACK-BOX GATING VERIFICATION ACROSS ALL 6 STAGES
    // =========================================================================

    @Test
    fun test04_blackBoxGatingAcrossAllSixStages() {
        val baseProfile = LearnerProfileEntity(id = "test_user")

        val stages = listOf(
            LearnerStage.N5_FOUNDATION,
            LearnerStage.N5_ACTIVE,
            LearnerStage.N5_COMPLETED,
            LearnerStage.N5_CERTIFIED,
            LearnerStage.N4_BRIDGE,
            LearnerStage.N4_ACTIVE
        )

        for (stage in stages) {
            val isN4Accessible = stage.isN4Accessible()

            // 1. Direct Content Gating Check
            val n4GrammarUnlocked = PrerequisiteEngine.isContentUnlocked(
                contentId = "n4_g_01",
                contentLevel = "N4",
                contentCategory = "GRAMMAR",
                stage = stage,
                profile = baseProfile.copy(kanaMasteryScore = 1.0f, grammarMasteryScore = 0.90f),
                masteredIds = setOf("n5_g_17", "n5_g_29")
            )
            val n4KanjiUnlocked = PrerequisiteEngine.isContentUnlocked(
                contentId = "n4_k_01",
                contentLevel = "N4",
                contentCategory = "KANJI",
                stage = stage,
                profile = baseProfile.copy(kanaMasteryScore = 1.0f)
            )

            if (!isN4Accessible) {
                assertFalse("Stage ${stage.name} must NOT access N4 grammar", n4GrammarUnlocked)
                assertFalse("Stage ${stage.name} must NOT access N4 kanji", n4KanjiUnlocked)
            } else {
                assertTrue("Stage ${stage.name} must access N4 grammar when prerequisites are satisfied", n4GrammarUnlocked)
                assertTrue("Stage ${stage.name} must access N4 kanji", n4KanjiUnlocked)
            }
        }
    }

    @Test
    fun test05_uiLevelSelectorCannotBypassGating() {
        // Force learner stage to N5_ACTIVE
        LearnerTransitionManager.setLearnerStage(context, LearnerStage.N5_ACTIVE)

        // Attempt to select N4 level in repository
        repo.setSelectedExamLevel(JlptExamLevel.N4)

        // Must be clamped / gated back to N5
        val selected = repo.getSelectedExamLevel()
        assertEquals("Uncertified learner selecting N4 must be gated to N5", JlptExamLevel.N5, selected)

        // Querying N4 deck items in N5 stage must return empty list
        val n4Items = repo.getItemsByJlptLevel("N4")
        assertTrue("N4 items must be empty for N5 learner", n4Items.isEmpty())

        // Search engine gating
        val sampleItems = listOf(
            com.example.noignore.japanese.model.JapaneseItem(
                id = "n4_k_01",
                japanese = "会",
                reading = "かい",
                romaji = "kai",
                meaning = "meet, society",
                jlptLevel = "N4",
                category = JapaneseCategory.KANJI
            ),
            com.example.noignore.japanese.model.JapaneseItem(
                id = "n5_k_01",
                japanese = "日",
                reading = "ひ",
                romaji = "hi",
                meaning = "sun, day",
                jlptLevel = "N5",
                category = JapaneseCategory.KANJI
            )
        )
        val searchResults = JlptSearchEngine.search(
            items = sampleItems,
            filter = SearchFilter(level = "N4"),
            isN4Accessible = LearnerTransitionManager.getLearnerStage(context).isN4Accessible()
        )
        assertTrue("Search for N4 items by N5 learner must return zero items", searchResults.isEmpty())
    }

    // =========================================================================
    // PART 9: SRS HISTORY PRESERVATION & ISOLATION
    // =========================================================================

    @Test
    fun test06_srsCardHistoryPreservedAndNoLeakage() = runBlocking {
        val srsDao = db.flashcardSrsDao()

        // 1. Insert N5 card
        val n5Card = FlashcardSrsEntity(
            cardId = "n5_v_01",
            category = "VOCAB",
            jlptLevel = "N5",
            repetition = 5,
            intervalDays = 15,
            easeFactor = 2.5f,
            state = "REVIEW",
            lastRating = "GOOD",
            totalReviews = 10,
            dueDateMs = System.currentTimeMillis() - 1000L
        )
        srsDao.insertOrUpdate(n5Card)

        // 2. Insert N4 card
        val n4Card = FlashcardSrsEntity(
            cardId = "n4_k_01",
            category = "KANJI",
            jlptLevel = "N4",
            repetition = 1,
            intervalDays = 1,
            easeFactor = 2.5f,
            state = "LEARNING",
            lastRating = "GOOD",
            totalReviews = 1,
            dueDateMs = System.currentTimeMillis() - 1000L
        )
        srsDao.insertOrUpdate(n4Card)

        // Verify N5 card history is completely untouched
        val retrievedN5 = srsDao.getCardSrs("n5_v_01")
        assertNotNull(retrievedN5)
        assertEquals(5, retrievedN5!!.repetition)
        assertEquals(15, retrievedN5.intervalDays)
        assertEquals(10, retrievedN5.totalReviews)

        // Verify canonical consolidation for legacy N4 scaffold
        val legacyCard = FlashcardSrsEntity(
            cardId = "n4_g_01",
            repetition = 3,
            intervalDays = 8,
            totalReviews = 6
        )
        val consolidated = CanonicalCurriculumIds.consolidateSrs(retrievedN5, legacyCard)
        assertEquals(5, consolidated.repetition) // max(5, 3)
        assertEquals(15, consolidated.intervalDays) // max(15, 8)
        assertEquals(16, consolidated.totalReviews) // 10 + 6
    }

    // =========================================================================
    // PART 10 & 11: N5 REGRESSION & PHASE 17 DEFECTS STATUS
    // =========================================================================

    @Test
    fun test07_n5CertifiedBaselineRegressionCheck() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val n5Items = db.jlptContentDao().getContentByLevel("N5")

        assertEquals(865, n5Items.size)
        assertEquals(211, n5Items.count { it.category == "KANA" })
        assertEquals(104, n5Items.count { it.category == "KANJI" })
        assertEquals(289, n5Items.count { it.category == "VOCAB" })
        assertEquals(53, n5Items.count { it.category == "GRAMMAR" })
        assertEquals(18, n5Items.count { it.category == "READING" })
        assertEquals(18, n5Items.count { it.category == "LISTENING" })
        assertEquals(172, n5Items.count { it.category == "JLPT_EXAM" })

        // DEF-17-01: n5_k_27 本 outgoing relatedItems inconsistency is confirmed OPEN
        val honKanji = db.jlptContentDao().getItemById("n5_k_27")
        assertNotNull(honKanji)
        assertTrue(honKanji!!.getRelatedItemIds().isNotEmpty())

        // DEF-17-02: 12 N5 grammar points with single question is confirmed OPEN
        val n5Questions = db.jlptContentDao().getContentByLevelAndCategory("N5", "JLPT_EXAM")
        val grammarQuestionCounts = mutableMapOf<String, Int>()
        for (q in n5Questions) {
            for (rel in q.getRelatedItemIds()) {
                if (rel.startsWith("n5_g_")) {
                    grammarQuestionCounts[rel] = (grammarQuestionCounts[rel] ?: 0) + 1
                }
            }
        }
        val singleQuestionGrammar = grammarQuestionCounts.filter { it.value == 1 }
        assertEquals(12, singleQuestionGrammar.size)
    }

    // =========================================================================
    // PART 12: BLIND N4 FOUNDATION SIMULATION
    // =========================================================================

    @Test
    fun test08_blindN4ActiveLearnerFoundationSimulation() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)

        // Set simulated learner to N4_ACTIVE with complete N5 foundation
        LearnerTransitionManager.setLearnerStage(context, LearnerStage.N4_ACTIVE)
        val n4Profile = LearnerProfileEntity(
            id = "default_profile",
            kanaMasteryScore = 1.0f,
            vocabMasteryScore = 0.95f,
            grammarMasteryScore = 0.90f,
            kanjiMasteryScore = 0.88f
        )
        db.learnerProfileDao().insertOrUpdate(n4Profile)

        // 1. Can see N4 Grammar foundation
        val n4GrammarPoints = db.jlptContentDao().getContentByLevelAndCategory("N4", "GRAMMAR")
        assertEquals(56, n4GrammarPoints.size)

        // 2. Can see N4 Kanji foundation
        val n4KanjiCharacters = db.jlptContentDao().getContentByLevelAndCategory("N4", "KANJI")
        assertEquals(175, n4KanjiCharacters.size)

        // 3. Prerequisites are enforced: potential requires dictionary form
        val canAccessPotential = PrerequisiteEngine.isContentUnlocked(
            contentId = "n4_g_01",
            contentLevel = "N4",
            contentCategory = "GRAMMAR",
            stage = LearnerStage.N4_ACTIVE,
            profile = n4Profile,
            masteredIds = setOf("n5_g_17", "n5_g_29")
        )
        assertTrue(canAccessPotential)

        val cannotBypassPrereqs = PrerequisiteEngine.isContentUnlocked(
            contentId = "n4_g_01",
            contentLevel = "N4",
            contentCategory = "GRAMMAR",
            stage = LearnerStage.N4_ACTIVE,
            profile = n4Profile,
            masteredIds = emptySet()
        )
        assertFalse("Cannot access N4 grammar without mastered prerequisites", cannotBypassPrereqs)

        // 4. Planned vocab is connected to canonical database entities
        val n4KanjiSample = n4KanjiCharacters.first()
        assertTrue("Planned vocab exists in pedagogical notes", n4KanjiSample.mnemonicOrNote.contains("Planned N4 Vocab:"))
        assertTrue("Connected graph edges exist to canonical vocabulary in Phase 19", n4KanjiSample.getRelatedItemIds().isNotEmpty())

        // 5. Daily study planner generates valid N4 missions
        val mission = DailyStudyPlanner.getOrCreateTodayMission(
            db = db,
            deckRepo = repo,
            explicitStage = LearnerStage.N4_ACTIVE,
            explicitLevel = "N4"
        )
        assertNotNull(mission)
        assertTrue(mission.tasks.any { it.title.contains("N4") })
    }
}
