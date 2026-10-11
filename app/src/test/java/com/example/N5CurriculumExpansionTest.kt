package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.noignore.data.AppDatabase
import com.example.noignore.japanese.curriculum.JlptAssetContentLoader
import com.example.noignore.japanese.curriculum.validation.ContentValidator
import com.example.noignore.japanese.curriculum.validation.CurriculumGraphValidator
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
class N5CurriculumExpansionTest {

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
    fun test1_n5VocabularyExpansionAndCoverage() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val n5Vocab = db.jlptContentDao().getContentByLevel("N5").filter { it.category == "VOCAB" }
        assertEquals("N5 vocabulary must reach expanded count of 289 items", 289, n5Vocab.size)

        // Verify newly added body parts vocabulary
        val me = db.jlptContentDao().getItemById("n5_v_292")
        assertNotNull("目 (n5_v_292) must exist", me)
        assertEquals("目", me!!.japanese)
        assertEquals("め", me.reading)
        assertTrue("Example Japanese must contain target word", me.exampleJapanese.contains("目"))
        assertFalse("Example English must be present", me.exampleEnglish.isBlank())

        val atama = db.jlptContentDao().getItemById("n5_v_293")
        assertNotNull("頭 (n5_v_293) must exist", atama)
        assertEquals("頭", atama!!.japanese)
        assertEquals("あたま", atama.reading)

        val te = db.jlptContentDao().getItemById("n5_v_294")
        assertNotNull("手 (n5_v_294) must exist", te)
        assertEquals("手", te!!.japanese)

        val kao = db.jlptContentDao().getItemById("n5_v_295")
        assertNotNull("顔 (n5_v_295) must exist", kao)
        assertEquals("顔", kao!!.japanese)
    }

    @Test
    fun test2_kanjiGapClosureVerified() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)

        // Verify 目 (n5_k_47) connects to vocabulary 目 (n5_v_292)
        val kEye = db.jlptContentDao().getItemById("n5_k_47")
        assertNotNull(kEye)
        assertTrue("Kanji 目 must link to vocabulary n5_v_292", kEye!!.getRelatedItemIds().contains("n5_v_292"))

        // Verify 耳 (n5_k_48) connects to vocabulary 耳 (n5_v_283)
        val kEar = db.jlptContentDao().getItemById("n5_k_48")
        assertNotNull(kEar)
        assertTrue("Kanji 耳 must link to vocabulary n5_v_283", kEar!!.getRelatedItemIds().contains("n5_v_283"))

        // Verify 口 (n5_k_49) connects to vocabulary 口 (n5_v_284)
        val kMouth = db.jlptContentDao().getItemById("n5_k_49")
        assertNotNull(kMouth)
        assertTrue("Kanji 口 must link to vocabulary n5_v_284", kMouth!!.getRelatedItemIds().contains("n5_v_284"))

        // Verify 手 (n5_k_50) connects to vocabulary 手 (n5_v_294)
        val kHand = db.jlptContentDao().getItemById("n5_k_50")
        assertNotNull(kHand)
        assertTrue("Kanji 手 must link to vocabulary n5_v_294", kHand!!.getRelatedItemIds().contains("n5_v_294"))

        // Verify 足 (n5_k_51) connects to vocabulary 足 (n5_v_285)
        val kFoot = db.jlptContentDao().getItemById("n5_k_51")
        assertNotNull(kFoot)
        assertTrue("Kanji 足 must link to vocabulary n5_v_285", kFoot!!.getRelatedItemIds().contains("n5_v_285"))
    }

    @Test
    fun test3_n5ReadingPassagesExpandedAndStructured() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val n5Reading = db.jlptContentDao().getContentByLevel("N5").filter { it.category == "READING" }
        assertEquals("N5 reading passages must reach expanded count of 18 items", 18, n5Reading.size)

        // Verify new reading passages
        for (i in 13..18) {
            val passageId = String.format("n5_r_%02d", i)
            val passage = db.jlptContentDao().getItemById(passageId)
            assertNotNull("Reading passage $passageId must exist", passage)
            assertTrue("Reading passage must have Japanese content", passage!!.japanese.isNotBlank())
            assertTrue("Reading passage must have meaning / comprehension question", passage.meaning.isNotBlank())
            assertTrue("Reading passage must have connected related items", passage.getRelatedItemIds().isNotEmpty())
        }
    }

    @Test
    fun test4_n5ListeningExercisesExpandedAndSanitized() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val n5Listening = db.jlptContentDao().getContentByLevel("N5").filter { it.category == "LISTENING" }
        assertEquals("N5 listening exercises must reach expanded count of 18 items", 18, n5Listening.size)

        // Verify n5_l_03 replacement (replaced cross-level dialog with authentic N5 library dialogue)
        val l03 = db.jlptContentDao().getItemById("n5_l_03")
        assertNotNull(l03)
        assertFalse("Cross-level vocabulary '部長' should be removed from n5_l_03", l03!!.japanese.contains("部長"))
        assertTrue("n5_l_03 must be authentic N5 beginner content", l03.japanese.contains("図書館") || l03.japanese.contains("時"))

        // Verify new listening exercises (n5_l_13 to n5_l_18)
        for (i in 13..18) {
            val listenId = String.format("n5_l_%02d", i)
            val exercise = db.jlptContentDao().getItemById(listenId)
            assertNotNull("Listening exercise $listenId must exist", exercise)
            assertTrue("Listening exercise must have Japanese dialogue", exercise!!.japanese.isNotBlank())
            assertTrue("Listening exercise must have related items", exercise.getRelatedItemIds().isNotEmpty())
        }
    }

    @Test
    fun test5_n5PracticeQuestionsAssessmentExpansion() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val n5Questions = db.jlptContentDao().getContentByLevel("N5").filter { it.category == "JLPT_EXAM" }
        assertEquals("N5 practice questions must reach expanded count of 172 items", 172, n5Questions.size)

        // Verify options and exam question prompt on all questions
        for (q in n5Questions) {
            assertTrue("Question ${q.id} must have question prompt", q.examQuestionPrompt.isNotBlank())
            val options = ContentValidator.parseOptions(q.optionsJson)
            assertTrue("Question ${q.id} must have at least 2 options (found ${options.size})", options.size >= 2)
            assertTrue("Question ${q.id} must have non-blank meaning", q.meaning.isNotBlank())
        }
    }

    @Test
    fun test6_fullCurriculumIntegrityAndZeroBrokenEdges() = runBlocking {
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val allItems = db.jlptContentDao().getAllContent()
        assertEquals(1764, allItems.size)

        val report = CurriculumGraphValidator.validateGraph(allItems)
        assertEquals("Zero broken references", 0, report.brokenReferences)
        assertEquals("Zero duplicates", 0, report.duplicates)
        assertEquals("Zero self references", 0, report.selfReferences)
        assertEquals("Zero invalid relationships", 0, report.invalidRelationships)
    }
}
