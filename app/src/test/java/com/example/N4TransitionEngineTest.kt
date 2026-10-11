package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.noignore.data.AppDatabase
import com.example.noignore.data.learner.LearnerProfileEntity
import com.example.noignore.data.srs.FlashcardSrsEntity
import com.example.noignore.japanese.curriculum.JlptAssetContentLoader
import com.example.noignore.japanese.curriculum.prerequisite.GrammarPrerequisiteGraph
import com.example.noignore.japanese.curriculum.prerequisite.PrerequisiteEngine
import com.example.noignore.japanese.curriculum.transition.LearnerStage
import com.example.noignore.japanese.curriculum.transition.LearnerTransitionManager
import com.example.noignore.japanese.data.JapaneseDeckRepository
import com.example.noignore.japanese.engine.DailyStudyPlanner
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class N4TransitionEngineTest {

    private lateinit var context: Context
    private lateinit var db: AppDatabase
    private lateinit var deckRepo: JapaneseDeckRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        deckRepo = JapaneseDeckRepository(context)
    }

    @After
    fun tearDown() {
        db.close()
    }

    // =========================================================================
    // PART 1 & 2: LEARNER STATE MACHINE & N5 CERTIFICATION GATE
    // =========================================================================

    @Test
    fun test01_stateMachineLegalSequentialProgression() {
        var stage = LearnerStage.N5_FOUNDATION
        assertEquals("N5", stage.levelCode)
        assertFalse("N5_FOUNDATION cannot access N4", stage.isN4Accessible())
        assertFalse("N5_FOUNDATION is not N5 complete", stage.isN5Complete())
        assertFalse("N5_FOUNDATION is not certified", stage.isN5Certified())

        // Profile with high Kana mastery unlocks N5_ACTIVE
        val kanaProfile = LearnerProfileEntity(
            kanaMasteryScore = 0.65f,
            vocabMasteryScore = 0.10f,
            grammarMasteryScore = 0.10f
        )
        val transToActive = LearnerTransitionManager.evaluateTransition(
            fromStage = stage,
            toStage = LearnerStage.N5_ACTIVE,
            profile = kanaProfile
        )
        assertTrue("N5_FOUNDATION -> N5_ACTIVE must succeed with 65% Kana", transToActive.isSuccess)
        stage = LearnerStage.N5_ACTIVE

        // Complete N5 syllabus
        val completedProfile = kanaProfile.copy(
            kanaMasteryScore = 0.85f,
            vocabMasteryScore = 0.60f,
            grammarMasteryScore = 0.60f
        )
        val transToCompleted = LearnerTransitionManager.evaluateTransition(
            fromStage = stage,
            toStage = LearnerStage.N5_COMPLETED,
            profile = completedProfile
        )
        assertTrue("N5_ACTIVE -> N5_COMPLETED must succeed with syllabus requirements", transToCompleted.isSuccess)
        stage = LearnerStage.N5_COMPLETED
        assertTrue(stage.isN5Complete())

        // N5 Certification Gate: requires Kana >= 0.90, Grammar >= 0.85, Kanji >= 0.80, Diagnostic >= 0.80
        val certifiedProfile = completedProfile.copy(
            kanaMasteryScore = 0.95f,
            grammarMasteryScore = 0.88f,
            kanjiMasteryScore = 0.82f
        )
        val certReport = LearnerTransitionManager.evaluateN5Certification(certifiedProfile, diagnosticScore = 0.85f)
        assertTrue("Certification report must indicate certified", certReport.isCertified)
        assertEquals(0, certReport.missingRequirements.size)

        val transToCertified = LearnerTransitionManager.evaluateTransition(
            fromStage = stage,
            toStage = LearnerStage.N5_CERTIFIED,
            profile = certifiedProfile,
            n5DiagnosticScore = 0.85f
        )
        assertTrue("N5_COMPLETED -> N5_CERTIFIED must succeed", transToCertified.isSuccess)
        stage = LearnerStage.N5_CERTIFIED
        assertTrue(stage.isN5Certified())
    }

    @Test
    fun test02_stateMachineRejectsIllegalJumps() {
        val beginnerProfile = LearnerProfileEntity(kanaMasteryScore = 0.10f)

        // 1. N5_FOUNDATION -> N4_ACTIVE must strictly FAIL
        val jump1 = LearnerTransitionManager.evaluateTransition(
            fromStage = LearnerStage.N5_FOUNDATION,
            toStage = LearnerStage.N4_ACTIVE,
            profile = beginnerProfile
        )
        assertFalse("N5_FOUNDATION -> N4_ACTIVE must be rejected", jump1.isSuccess)
        assertTrue(jump1.message.contains("Illegal transition"))

        // 2. N5_ACTIVE -> N4_ACTIVE must strictly FAIL
        val jump2 = LearnerTransitionManager.evaluateTransition(
            fromStage = LearnerStage.N5_ACTIVE,
            toStage = LearnerStage.N4_ACTIVE,
            profile = beginnerProfile
        )
        assertFalse("N5_ACTIVE -> N4_ACTIVE must be rejected", jump2.isSuccess)

        // 3. N5_ACTIVE -> N4_BRIDGE must strictly FAIL without N5 certification
        val jump3 = LearnerTransitionManager.evaluateTransition(
            fromStage = LearnerStage.N5_ACTIVE,
            toStage = LearnerStage.N4_BRIDGE,
            profile = beginnerProfile
        )
        assertFalse("N5_ACTIVE -> N4_BRIDGE must be rejected without certification", jump3.isSuccess)

        // 4. Backward transition is legal (refresher)
        val backward = LearnerTransitionManager.evaluateTransition(
            fromStage = LearnerStage.N5_CERTIFIED,
            toStage = LearnerStage.N5_ACTIVE,
            profile = beginnerProfile
        )
        assertTrue("Backward transition for review is legal", backward.isSuccess)
    }

    @Test
    fun test03_n5CertificationGateEnforcesMultiDimensionalCompetence() {
        // Failing Kana
        val failKana = LearnerProfileEntity(
            kanaMasteryScore = 0.85f, // Required 0.90
            grammarMasteryScore = 0.90f,
            kanjiMasteryScore = 0.90f
        )
        assertFalse("Fails on Kana < 0.90", PrerequisiteEngine.isN5Certified(failKana, 0.90f))

        // Failing Grammar
        val failGrammar = LearnerProfileEntity(
            kanaMasteryScore = 0.95f,
            grammarMasteryScore = 0.75f, // Required 0.85
            kanjiMasteryScore = 0.90f
        )
        assertFalse("Fails on Grammar < 0.85", PrerequisiteEngine.isN5Certified(failGrammar, 0.90f))

        // Failing Kanji
        val failKanji = LearnerProfileEntity(
            kanaMasteryScore = 0.95f,
            grammarMasteryScore = 0.90f,
            kanjiMasteryScore = 0.70f // Required 0.80
        )
        assertFalse("Fails on Kanji < 0.80", PrerequisiteEngine.isN5Certified(failKanji, 0.90f))

        // Failing Diagnostic
        val failDiagnostic = LearnerProfileEntity(
            kanaMasteryScore = 0.95f,
            grammarMasteryScore = 0.90f,
            kanjiMasteryScore = 0.85f
        )
        assertFalse("Fails on Diagnostic < 0.80", PrerequisiteEngine.isN5Certified(failDiagnostic, 0.70f))

        // Passing all criteria
        val passAll = LearnerProfileEntity(
            kanaMasteryScore = 0.92f,
            grammarMasteryScore = 0.87f,
            kanjiMasteryScore = 0.81f
        )
        assertTrue("Passes all multi-dimensional thresholds", PrerequisiteEngine.isN5Certified(passAll, 0.82f))
    }

    // =========================================================================
    // PART 3: N4 BRIDGE TRANSITION
    // =========================================================================

    @Test
    fun test04_n4BridgeControlledTransition() {
        val certifiedProfile = LearnerProfileEntity(
            kanaMasteryScore = 0.95f,
            grammarMasteryScore = 0.90f,
            kanjiMasteryScore = 0.85f
        )

        // Certified student enters N4_BRIDGE
        val enterBridge = LearnerTransitionManager.evaluateTransition(
            fromStage = LearnerStage.N5_CERTIFIED,
            toStage = LearnerStage.N4_BRIDGE,
            profile = certifiedProfile
        )
        assertTrue("Certified learner can enter N4_BRIDGE", enterBridge.isSuccess)

        // Attempting to advance to N4_ACTIVE with incomplete bridge (only 1 session)
        val advanceFail = LearnerTransitionManager.evaluateTransition(
            fromStage = LearnerStage.N4_BRIDGE,
            toStage = LearnerStage.N4_ACTIVE,
            profile = certifiedProfile,
            bridgeSessionsCompleted = 1,
            bridgeDiagnosticScore = 0.75f
        )
        assertFalse("Bridge with 1 session must not unlock N4_ACTIVE", advanceFail.isSuccess)
        assertEquals(2, advanceFail.missingRequirements.size)

        // Complete Bridge: 3 sessions and diagnostic >= 80%
        val bridgeReport = LearnerTransitionManager.evaluateN4Bridge(sessionsCompleted = 3, diagnosticScore = 0.85f)
        assertTrue("Bridge is complete with 3 sessions & 85% diagnostic", bridgeReport.isBridgeComplete)

        val advanceSuccess = LearnerTransitionManager.evaluateTransition(
            fromStage = LearnerStage.N4_BRIDGE,
            toStage = LearnerStage.N4_ACTIVE,
            profile = certifiedProfile,
            bridgeSessionsCompleted = 3,
            bridgeDiagnosticScore = 0.85f
        )
        assertTrue("Complete bridge successfully unlocks N4_ACTIVE", advanceSuccess.isSuccess)
    }

    // =========================================================================
    // PART 4: SRS RETENTION & ZERO-LOSS LEVEL TRANSITIONS
    // =========================================================================

    @Test
    fun test05_srsCardsAndReviewsPreservedAcrossLevels() = runBlocking {
        val srsDao = db.flashcardSrsDao()

        // 1. Insert N5 card into SRS with existing history
        val n5Card = FlashcardSrsEntity(
            cardId = "n5_v_01",
            totalReviews = 10,
            intervalDays = 14,
            easeFactor = 2.5f,
            state = "REVIEW",
            lastRating = "GOOD",
            dueDateMs = System.currentTimeMillis() - 1000L // Overdue
        )
        srsDao.insertOrUpdate(n5Card)

        // 2. Simulate learner in N4 stage checking due cards
        val dueCards = srsDao.getDueCardsSync(System.currentTimeMillis())
        assertEquals("N5 card must remain in active SRS queue", 1, dueCards.size)
        assertEquals("n5_v_01", dueCards[0].cardId)
        assertEquals(10, dueCards[0].totalReviews)
        assertEquals(14, dueCards[0].intervalDays)

        // 3. Rate the card as AGAIN (lapse during N4 review)
        val lapsed = n5Card.copy(
            totalReviews = n5Card.totalReviews + 1,
            lapses = n5Card.lapses + 1,
            state = "LEARNING",
            lastRating = "AGAIN",
            intervalDays = 0
        )
        srsDao.insertOrUpdate(lapsed)

        val updated = srsDao.getCardSrs("n5_v_01")
        assertNotNull(updated)
        assertEquals(11, updated!!.totalReviews)
        assertEquals(1, updated.lapses)
        assertEquals("LEARNING", updated.state)
    }

    // =========================================================================
    // PART 5: DAILY STUDY PLANNER GENERALIZATION
    // =========================================================================

    @Test
    fun test06_dailyStudyPlannerStageAwareMissions() = runBlocking {
        // N4 Bridge plan emphasizes N5 review and bridge diagnostic
        val bridgePlan = DailyStudyPlanner.createDefaultPlan(
            cardsDueCount = 12,
            curriculumLevel = "N4",
            learnerStage = LearnerStage.N4_BRIDGE
        )
        assertFalse(bridgePlan.isFiveMinuteMode)
        assertTrue("Bridge mission has 3 tasks", bridgePlan.tasks.size == 3)
        assertTrue("Task 2 is N4 Bridge Core Review", bridgePlan.tasks[1].title.contains("N4 Bridge"))
        assertTrue("Task 3 is Bridge Diagnostic Check", bridgePlan.tasks[2].title.contains("Diagnostic"))

        // N4 Active plan incorporates N4 practice
        val activeN4Plan = DailyStudyPlanner.createDefaultPlan(
            cardsDueCount = 8,
            curriculumLevel = "N4",
            learnerStage = LearnerStage.N4_ACTIVE
        )
        assertTrue(activeN4Plan.tasks[1].title.contains("N4"))

        // Dynamic database mission generation for N4_BRIDGE
        val dbMission = DailyStudyPlanner.getOrCreateTodayMission(
            db = db,
            deckRepo = deckRepo,
            explicitStage = LearnerStage.N4_BRIDGE
        )
        assertTrue("DB mission for bridge contains bridge tasks", dbMission.tasks.any { it.title.contains("Bridge") })
    }

    // =========================================================================
    // PART 6: PREREQUISITE ENGINE CONTENT GATING
    // =========================================================================

    @Test
    fun test07_prerequisiteEngineLevelAwareContentUnlocked() {
        val n5Profile = LearnerProfileEntity(
            kanaMasteryScore = 0.70f,
            vocabMasteryScore = 0.40f,
            grammarMasteryScore = 0.40f
        )

        // N4 content is strictly locked for an N5 learner
        val n4GrammarUnlocked = PrerequisiteEngine.isContentUnlocked(
            contentId = "n4_g_01",
            contentLevel = "N4",
            contentCategory = "GRAMMAR",
            stage = LearnerStage.N5_ACTIVE,
            profile = n5Profile
        )
        assertFalse("N4 grammar must be LOCKED for N5_ACTIVE learner", n4GrammarUnlocked)

        // N5 grammar is unlocked if parents are satisfied
        val n5GrammarUnlocked = PrerequisiteEngine.isContentUnlocked(
            contentId = "n5_g_46",
            contentLevel = "N5",
            contentCategory = "GRAMMAR",
            stage = LearnerStage.N5_ACTIVE,
            profile = n5Profile,
            masteredIds = setOf("n5_g_01")
        )
        assertTrue("N5 demonstratives unlocked when n5_g_01 is mastered", n5GrammarUnlocked)

        // Once in N4_ACTIVE, N4 content check evaluates grammar prerequisites (n4_g_01 requires n5_g_17 and n5_g_29)
        val inN4Active = PrerequisiteEngine.isContentUnlocked(
            contentId = "n4_g_01",
            contentLevel = "N4",
            contentCategory = "GRAMMAR",
            stage = LearnerStage.N4_ACTIVE,
            profile = n5Profile.copy(kanaMasteryScore = 1.0f, grammarMasteryScore = 0.90f),
            masteredIds = setOf("n5_g_17", "n5_g_29")
        )
        assertTrue("N4 grammar accessible in N4_ACTIVE stage", inN4Active)
    }

    // =========================================================================
    // PART 7 & 8: GRAPH DAG VALIDATION & CROSS-LEVEL RULES
    // =========================================================================

    @Test
    fun test08_crossLevelGraphRulesAndCycleDetection() {
        // Legal cross-level graph: N4 depends on N5
        val legalCrossLevelMap = mapOf(
            "n5_g_01" to emptyList<String>(),
            "n5_g_29" to listOf("n5_g_01"),
            "n4_g_01" to listOf("n5_g_29"), // N4 -> N5: LEGAL!
            "n4_g_02" to listOf("n4_g_01")   // N4 -> N4: LEGAL!
        )
        val legalReport = GrammarPrerequisiteGraph.validateGraphTopology(legalCrossLevelMap)
        assertTrue("Legal cross-level graph must validate", legalReport.isValid)
        assertEquals(1, legalReport.crossLevelN4ToN5Edges)
        assertEquals(0, legalReport.illegalReverseDependencies.size)
        assertEquals(0, legalReport.cycles.size)

        val topologicalOrder = GrammarPrerequisiteGraph.topologicalSort(legalCrossLevelMap)
        assertNotNull("Topological sort must succeed", topologicalOrder)
        val idxN5 = topologicalOrder!!.indexOf("n5_g_29")
        val idxN4 = topologicalOrder.indexOf("n4_g_01")
        assertTrue("N5 prerequisite must precede N4 dependent", idxN5 < idxN4)

        // RULE 2: N5 content must NEVER depend on N4 content (Illegal reverse dependency)
        val illegalReverseMap = mapOf(
            "n5_g_01" to emptyList(),
            "n4_g_01" to listOf("n5_g_01"),
            "n5_g_02" to listOf("n4_g_01") // ILLEGAL: N5 depends on N4!
        )
        val reverseReport = GrammarPrerequisiteGraph.validateGraphTopology(illegalReverseMap)
        assertFalse("Illegal reverse dependency must fail validation", reverseReport.isValid)
        assertEquals(1, reverseReport.illegalReverseDependencies.size)
        assertTrue(reverseReport.illegalReverseDependencies[0].contains("Illegal reverse dependency"))

        // RULE 5: Circular dependency must be rejected (N5 -> N4 -> N5)
        val circularMap = mapOf(
            "n5_g_29" to listOf("n4_g_01"),
            "n4_g_01" to listOf("n5_g_29")
        )
        val circularReport = GrammarPrerequisiteGraph.validateGraphTopology(circularMap)
        assertFalse("Circular dependency must be rejected", circularReport.isValid)
        assertTrue(circularReport.cycles.isNotEmpty())
        assertNull("Topological sort must return null on cycle", GrammarPrerequisiteGraph.topologicalSort(circularMap))

        // RULE 6 & 7: Self-cycle & broken references
        val brokenMap = mapOf(
            "n4_g_05" to listOf("n4_g_05"), // Self reference
            "n4_g_06" to listOf("unknown_parent") // Broken reference
        )
        val brokenReport = GrammarPrerequisiteGraph.validateGraphTopology(brokenMap)
        assertFalse(brokenReport.isValid)
        assertEquals(1, brokenReport.selfReferences.size)
        assertEquals(1, brokenReport.brokenReferences.size)

        // RULE 8: Duplicate parent references
        val dupMap = mapOf(
            "n4_g_10" to listOf("n5_g_01", "n5_g_01")
        )
        val dupReport = GrammarPrerequisiteGraph.validateGraphTopology(dupMap, allowExternalParents = true)
        assertFalse(dupReport.isValid)
        assertEquals(1, dupReport.duplicateReferences.size)
    }

    // =========================================================================
    // PART 10: N5 REGRESSION PROTECTION
    // =========================================================================

    @Test
    fun test09_canonicalN5PrerequisiteSequencePreserved() = runBlocking {
        // Ingest bundled curriculum into in-memory DB
        val count = JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        assertEquals(1764, count)

        // Authoritative 53 N5 sequence remains 100% intact
        val sequence = GrammarPrerequisiteGraph.getPedagogicalSequence()
        assertEquals("Canonical N5 sequence contains all 53 items", 53, sequence.size)

        val copulaIdx = sequence.indexOf("n5_g_01")
        val demonstrativeIdx = sequence.indexOf("n5_g_46")
        val dictFormIdx = sequence.indexOf("n5_g_29")
        val teFormIdx = sequence.indexOf("n5_g_28")
        val quoteIdx = sequence.indexOf("n5_g_50")
        val relClauseIdx = sequence.indexOf("n5_g_51")

        assertTrue("Copula precedes demonstratives", copulaIdx < demonstrativeIdx)
        assertTrue("Dict form precedes quotation", dictFormIdx < quoteIdx)
        assertTrue("Te-form follows dict form", dictFormIdx < teFormIdx)
        assertTrue("Dict form precedes relative clause", dictFormIdx < relClauseIdx)
    }
}
