package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.noignore.data.AppDatabase
import com.example.noignore.japanese.curriculum.JlptAssetContentLoader
import com.example.noignore.japanese.curriculum.canonical.CanonicalCurriculumIds
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
class CanonicalCurriculumIdsMaintenanceTest {

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
    fun test01_canonicalN4KanjiIdsMustNotBeRewritten() {
        // Requirement 1, 2, 3: Valid N4 IDs resolve to their correct N4 entities without rewriting
        assertEquals("n4_k_04", CanonicalCurriculumIds.canonicalize("n4_k_04"))
        assertEquals("n4_k_07", CanonicalCurriculumIds.canonicalize("n4_k_07"))
        assertEquals("n4_k_10", CanonicalCurriculumIds.canonicalize("n4_k_10"))

        assertFalse(CanonicalCurriculumIds.isDuplicate("n4_k_04"))
        assertFalse(CanonicalCurriculumIds.isDuplicate("n4_k_07"))
        assertFalse(CanonicalCurriculumIds.isDuplicate("n4_k_10"))
    }

    @Test
    fun test02_genuineLegacyAliasesStillResolveCorrectly() {
        // Requirement 4: Genuine legacy aliases continue to resolve correctly
        assertEquals("n5_k_15", CanonicalCurriculumIds.canonicalize("k_1"))
        assertEquals("n5_k_16", CanonicalCurriculumIds.canonicalize("k_2"))
        assertEquals("n5_k_18", CanonicalCurriculumIds.canonicalize("k_3"))
        assertEquals("n5_k_76", CanonicalCurriculumIds.canonicalize("curric_4_3"))
        assertEquals("n5_v_03", CanonicalCurriculumIds.canonicalize("curric_2_5"))
        assertEquals("n5_v_03", CanonicalCurriculumIds.canonicalize("n5_v_212"))
        assertEquals("n5_g_18", CanonicalCurriculumIds.canonicalize("n4_g_01"))
        assertEquals("n5_g_37", CanonicalCurriculumIds.canonicalize("n4_g_02"))
        assertEquals("n5_g_37", CanonicalCurriculumIds.canonicalize("n4_g_04"))
    }

    @Test
    fun test03_n5KanjiRecordsRemainUnchanged() = runBlocking {
        // Requirement 5: N5 Kanji records remain unchanged
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val n5Kanji = db.jlptContentDao().getContentByLevelAndCategory("N5", "KANJI")
        assertEquals(104, n5Kanji.size)

        val sha = db.jlptContentDao().getItemById("n5_k_77")
        assertNotNull(sha)
        assertEquals("社", sha!!.japanese)

        val nomi = db.jlptContentDao().getItemById("n5_k_60")
        assertNotNull(nomi)
        assertEquals("飲", nomi!!.japanese)

        val eki = db.jlptContentDao().getItemById("n5_k_76")
        assertNotNull(eki)
        assertEquals("駅", eki!!.japanese)
    }

    @Test
    fun test04_vocabularyLinksToCorrectN4KanjiEntities() = runBlocking {
        // Requirement 6: Vocabulary cannot acquire an unrelated Kanji relationship
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)

        // 1. 都合 (n4_v_06) must link to 都 (n4_k_10), NOT 駅 (n5_k_76)
        val tsugo = db.jlptContentDao().getItemById("n4_v_06")!!
        assertTrue("都合 must link to 都 (n4_k_10)", tsugo.getRelatedItemIds().contains("n4_k_10"))
        assertFalse("都合 must NOT link to 駅 (n5_k_76)", tsugo.getRelatedItemIds().contains("n5_k_76"))

        // 2. 京都 (n4_v_27) must link to 都 (n4_k_10), NOT 駅 (n5_k_76)
        val kyoto = db.jlptContentDao().getItemById("n4_v_27")!!
        assertTrue("京都 must link to 都 (n4_k_10)", kyoto.getRelatedItemIds().contains("n4_k_10"))
        assertFalse("京都 must NOT link to 駅 (n5_k_76)", kyoto.getRelatedItemIds().contains("n5_k_76"))

        // 3. 会社員 (n4_v_34) links to 員 (n4_k_04)
        val kaishain = db.jlptContentDao().getItemById("n4_v_34")!!
        assertTrue("会社員 must link to 員 (n4_k_04)", kaishain.getRelatedItemIds().contains("n4_k_04"))

        // 4. 動物園 (n4_v_59) must link to 園 (n4_k_07), NOT 飲 (n5_k_60)
        val doubutsuen = db.jlptContentDao().getItemById("n4_v_59")!!
        assertTrue("動物園 must link to 園 (n4_k_07)", doubutsuen.getRelatedItemIds().contains("n4_k_07"))
        assertFalse("動物園 must NOT link to 飲 (n5_k_60)", doubutsuen.getRelatedItemIds().contains("n5_k_60"))

        // 5. 店員 (n4_v_108) must link to 員 (n4_k_04), NOT 社 (n5_k_77)
        val tenin = db.jlptContentDao().getItemById("n4_v_108")!!
        assertTrue("店員 must link to 員 (n4_k_04)", tenin.getRelatedItemIds().contains("n4_k_04"))
        assertFalse("店員 must NOT link to 社 (n5_k_77)", tenin.getRelatedItemIds().contains("n5_k_77"))

        // 6. 首都 (n4_v_292) must link to 都 (n4_k_10), NOT 駅 (n5_k_76)
        val shuto = db.jlptContentDao().getItemById("n4_v_292")!!
        assertTrue("首都 must link to 都 (n4_k_10)", shuto.getRelatedItemIds().contains("n4_k_10"))
        assertFalse("首都 must NOT link to 駅 (n5_k_76)", shuto.getRelatedItemIds().contains("n5_k_76"))
    }

    @Test
    fun test05_all175N4KanjiRemainDistinctAndValid() = runBlocking {
        // Requirement: All 175 N4 Kanji remain distinct, valid canonical entities
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val n4Kanji = db.jlptContentDao().getContentByLevelAndCategory("N4", "KANJI")
        assertEquals(175, n4Kanji.size)

        val inMember = db.jlptContentDao().getItemById("n4_k_04")
        assertNotNull(inMember)
        assertEquals("員", inMember!!.japanese)

        val enGarden = db.jlptContentDao().getItemById("n4_k_07")
        assertNotNull(enGarden)
        assertEquals("園", enGarden!!.japanese)

        val toCapital = db.jlptContentDao().getItemById("n4_k_10")
        assertNotNull(toCapital)
        assertEquals("都", toCapital!!.japanese)
    }

    @Test
    fun test06_repeatedLoadingPreservesGraphIntegrityWithZeroBrokenRefs() = runBlocking {
        // Requirement 7, 8, 9: Repeated loading does not duplicate, produces 0 broken refs
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val countFirst = db.jlptContentDao().count()
        assertEquals(1764, countFirst)

        // Repeat load
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)
        val countSecond = db.jlptContentDao().count()
        assertEquals(1764, countSecond)

        val allItems = db.jlptContentDao().getAllContent()
        val report = CurriculumGraphValidator.validateGraph(allItems)
        assertEquals("Broken references must be 0", 0, report.brokenReferences)
        assertEquals("Duplicates must be 0", 0, report.duplicates)
        assertEquals("Self references must be 0", 0, report.selfReferences)
        assertEquals("Invalid relationships must be 0", 0, report.invalidRelationships)
    }

    @Test
    fun test07_staleLegacyCrossLevelLinksMustNotExistInSourceData() = runBlocking {
        // Specifically prevent n4_k_07 (園) in n5_v_02 (飲む) and n4_k_10 (都) in n5_v_56 (駅)
        JlptAssetContentLoader.loadAllCurriculumIntoRoom(context, db, forceReload = true)

        val nomu = db.jlptContentDao().getItemById("n5_v_02")!!
        assertFalse("飲む (n5_v_02) must not link to 園 (n4_k_07)", nomu.getRelatedItemIds().contains("n4_k_07"))
        assertTrue("飲む (n5_v_02) must link to 飲 (n5_k_60)", nomu.getRelatedItemIds().contains("n5_k_60"))

        val eki = db.jlptContentDao().getItemById("n5_v_56")!!
        assertFalse("駅 (n5_v_56) must not link to 都 (n4_k_10)", eki.getRelatedItemIds().contains("n4_k_10"))
        assertTrue("駅 (n5_v_56) must link to 駅 (n5_k_76)", eki.getRelatedItemIds().contains("n5_k_76"))
    }
}
