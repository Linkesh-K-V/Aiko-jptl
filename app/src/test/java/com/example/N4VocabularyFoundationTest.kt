package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.noignore.data.AppDatabase
import com.example.noignore.data.content.JlptContentEntity
import com.example.noignore.data.learner.LearnerProfileEntity
import com.example.noignore.data.srs.FlashcardSrsEntity
import com.example.noignore.japanese.curriculum.JlptAssetContentLoader
import com.example.noignore.japanese.curriculum.graph.KnowledgeGraphService
import com.example.noignore.japanese.curriculum.prerequisite.PrerequisiteEngine
import com.example.noignore.japanese.curriculum.prerequisite.VocabPrerequisiteModel
import com.example.noignore.japanese.curriculum.transition.LearnerStage
import com.example.noignore.japanese.curriculum.transition.LearnerTransitionManager
import com.example.noignore.japanese.data.JapaneseDeckRepository
import com.example.noignore.japanese.engine.DailyStudyPlanner
import com.example.noignore.japanese.model.JapaneseItem
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
class N4VocabularyFoundationTest {

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

    @Test
    fun test01_canonicalN4VocabularyInventoryAndDeduplication() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val n4Vocab = db.jlptContentDao().getContentByLevelAndCategory("N4", "VOCAB")
        val n5Vocab = db.jlptContentDao().getContentByLevelAndCategory("N5", "VOCAB")

        // Exact count certified
        assertEquals("Canonical N4 vocabulary must be exactly 575 items", 575, n4Vocab.size)
        assertEquals("N5 vocabulary baseline must remain exactly 289 items", 289, n5Vocab.size)

        // Strict N5 deduplication: zero overlap in japanese words
        val n4Words = n4Vocab.map { it.japanese }.toSet()
        val n5Words = n5Vocab.map { it.japanese }.toSet()
        val overlap = n4Words.intersect(n5Words)
        assertTrue("Zero overlap between N5 and N4 vocabulary, but found: $overlap", overlap.isEmpty())
    }

    @Test
    fun test02_promotedLegacyVocabularyPreservedAndCanonical() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)

        val expectedLegacy = mapOf(
            "n4_v_01" to "案内する",
            "n4_v_02" to "遠慮する",
            "n4_v_03" to "準備",
            "n4_v_04" to "約束",
            "n4_v_05" to "連絡",
            "n4_v_06" to "都合",
            "n4_v_07" to "紹介する",
            "n4_v_08" to "注意する",
            "n4_v_09" to "経験",
            "n4_v_10" to "景色",
            "n4_v_11" to "習慣",
            "n4_v_12" to "世話",
            "n4_v_13" to "趣味",
            "n4_v_14" to "将来",
            "n4_v_15" to "原因"
        )

        for ((id, expectedWord) in expectedLegacy) {
            val item = db.jlptContentDao().getItemById(id)
            assertNotNull("Legacy item $id must exist in Room", item)
            assertEquals("Word for $id must match canonical promotion", expectedWord, item!!.japanese)
            assertEquals("JLPT level must be N4", "N4", item.jlptLevel)
            assertEquals("Category must be VOCAB", "VOCAB", item.category)
            assertTrue("Must have non-empty reading", item.reading.isNotBlank())
            assertTrue("Must have non-empty meaning", item.meaning.isNotBlank())
            assertTrue("Must have natural example sentence", item.exampleJapanese.isNotBlank())
            assertTrue("Must have English translation", item.exampleEnglish.isNotBlank())
        }
    }

    @Test
    fun test03_kanjiToVocabularyGraphIntegration() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val n4KanjiList = db.jlptContentDao().getContentByLevelAndCategory("N4", "KANJI")

        assertEquals(175, n4KanjiList.size)
        var totalKanjiEdges = 0
        for (k in n4KanjiList) {
            val edges = k.getRelatedItemIds()
            assertTrue("Kanji ${k.japanese} (${k.id}) must have connected vocabulary edges", edges.isNotEmpty())
            totalKanjiEdges += edges.size

            // Verify each target item actually exists in the database
            for (edgeId in edges) {
                val target = db.jlptContentDao().getItemById(edgeId)
                assertNotNull("Target item $edgeId for kanji ${k.japanese} must exist in database", target)
            }
        }
        assertTrue("Total N4 Kanji vocabulary relationships must be >= 300, got: $totalKanjiEdges", totalKanjiEdges >= 300)
    }

    @Test
    fun test04_kanaFirstPrincipleAndTierDistribution() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val n4Vocab = db.jlptContentDao().getContentByLevelAndCategory("N4", "VOCAB")

        // 1. Every item must have reading and meaning
        for (v in n4Vocab) {
            assertTrue("Vocabulary ${v.japanese} must have non-blank reading", v.reading.isNotBlank())
            assertTrue("Vocabulary ${v.japanese} must have non-blank meaning", v.meaning.isNotBlank())
            assertTrue("Vocabulary ${v.japanese} must have example Japanese", v.exampleJapanese.isNotBlank())
            assertTrue("Vocabulary ${v.japanese} must have example English", v.exampleEnglish.isNotBlank())
        }

        // 2. Kana-First classification: items with no kanji must be Level A
        val kanaOnlyItems = n4Vocab.filter { item ->
            val kanji = VocabPrerequisiteModel.extractKanji(item.japanese)
            kanji.isEmpty()
        }
        assertTrue("Must have kana-accessible items (Tier A)", kanaOnlyItems.isNotEmpty())

        for (item in kanaOnlyItems) {
            val jItem = item.toJapaneseItem()
            val eval = VocabPrerequisiteModel.evaluatePrerequisites(
                item = jItem,
                knownKana = VocabPrerequisiteModel.extractKana(item.reading),
                knownKanji = emptySet(),
                knownGrammarIds = setOf("n5_g_27", "n5_g_01", "n5_g_12") // standard N5 polite foundations
            )
            assertTrue("Kana-only item ${item.japanese} must be appropriate with Kana literacy alone", eval.isAppropriate)
        }
    }

    @Test
    fun test05_gatingIntegrity_uncertifiedLearnerCannotAccessN4Vocab() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)

        val uncertifiedProfile = LearnerProfileEntity(
            id = "test_user",
            kanaMasteryScore = 0.60f,
            grammarMasteryScore = 0.50f,
            vocabMasteryScore = 0.40f
        )

        // Across uncertified stages: N5_FOUNDATION, N5_ACTIVE, N5_COMPLETED, N5_CERTIFIED, N4_BRIDGE
        val uncertifiedStages = listOf(
            LearnerStage.N5_FOUNDATION,
            LearnerStage.N5_ACTIVE,
            LearnerStage.N5_COMPLETED,
            LearnerStage.N5_CERTIFIED,
            LearnerStage.N4_BRIDGE
        )

        for (stage in uncertifiedStages) {
            val isUnlocked = PrerequisiteEngine.isContentUnlocked(
                contentId = "n4_v_01",
                contentLevel = "N4",
                contentCategory = "VOCAB",
                stage = stage,
                profile = uncertifiedProfile
            )
            assertFalse("N4 vocabulary must NOT be unlocked for stage ${stage.name}", isUnlocked)
        }

        // In N4_ACTIVE, it unlocks
        val n4ActiveUnlocked = PrerequisiteEngine.isContentUnlocked(
            contentId = "n4_v_01",
            contentLevel = "N4",
            contentCategory = "VOCAB",
            stage = LearnerStage.N4_ACTIVE,
            profile = uncertifiedProfile.copy(kanaMasteryScore = 1.0f, grammarMasteryScore = 0.90f)
        )
        assertTrue("N4 vocabulary must be unlocked for N4_ACTIVE stage", n4ActiveUnlocked)
    }

    @Test
    fun test06_dailyStudyPlannerSupportsN4VocabularyProgression() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)

        // Insert completed profile for N4 learner
        val n4Profile = LearnerProfileEntity(
            id = "default_profile",
            kanaMasteryScore = 1.0f,
            vocabMasteryScore = 0.90f,
            grammarMasteryScore = 0.88f,
            kanjiMasteryScore = 0.85f
        )
        db.learnerProfileDao().insertOrUpdate(n4Profile)

        val plan = DailyStudyPlanner.getOrCreateTodayMission(
            db = db,
            deckRepo = repo,
            explicitStage = LearnerStage.N4_ACTIVE,
            explicitLevel = "N4"
        )

        assertNotNull(plan)
        assertFalse("Plan should not be empty", plan.tasks.isEmpty())
        assertTrue("Plan should contain N4 mission tasks", plan.tasks.any { it.title.contains("N4") || it.drillType == "VOCAB" || it.drillType == "FLASHCARDS" })
    }

    @Test
    fun test07_srsCardIntegrationAndIsolationForN4Vocab() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val srsDao = db.flashcardSrsDao()

        // 1. Existing N5 card
        val n5Card = FlashcardSrsEntity(
            cardId = "n5_v_01",
            category = "VOCAB",
            jlptLevel = "N5",
            repetition = 8,
            intervalDays = 30,
            easeFactor = 2.6f,
            state = "REVIEW",
            lastRating = "GOOD",
            totalReviews = 12
        )
        srsDao.insertOrUpdate(n5Card)

        // 2. New N4 card created
        val n4Card = FlashcardSrsEntity(
            cardId = "n4_v_01",
            category = "VOCAB",
            jlptLevel = "N4",
            repetition = 1,
            intervalDays = 1,
            easeFactor = 2.5f,
            state = "LEARNING",
            lastRating = "GOOD",
            totalReviews = 1
        )
        srsDao.insertOrUpdate(n4Card)

        // Check retrieval and isolation
        val retrievedN5 = srsDao.getCardSrs("n5_v_01")!!
        val retrievedN4 = srsDao.getCardSrs("n4_v_01")!!

        assertEquals(8, retrievedN5.repetition)
        assertEquals(30, retrievedN5.intervalDays)
        assertEquals("N5", retrievedN5.jlptLevel)

        assertEquals(1, retrievedN4.repetition)
        assertEquals(1, retrievedN4.intervalDays)
        assertEquals("N4", retrievedN4.jlptLevel)
    }

    @Test
    fun test08_remediationPlanResolvesN4VocabularyUnderlyingKanji() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)

        // Retrieve n4_v_08: 注意する (contains n4_k_87 注 and n4_k_08 意)
        val chuiItem = db.jlptContentDao().getItemById("n4_v_08")!!
        val plan = KnowledgeGraphService.buildRemediationPlan(db, chuiItem)

        assertEquals("n4_v_08", plan.targetItem.id)
        assertTrue("Remediation must contain prerequisite kanji", plan.prerequisiteKanji.isNotEmpty())
        assertTrue("Remediation summary must mention targeted vocabulary remediation", plan.summaryExplanation.contains("Targeted Vocabulary Remediation"))
    }
}
