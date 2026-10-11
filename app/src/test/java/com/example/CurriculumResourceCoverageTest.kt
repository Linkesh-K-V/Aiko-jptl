package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.noignore.data.content.JlptContentEntity
import com.example.noignore.japanese.curriculum.resource.CurriculumCoverageState
import com.example.noignore.japanese.curriculum.resource.CurriculumResourceRepository
import com.example.noignore.japanese.curriculum.resource.ResourceVerificationStatus
import com.example.noignore.japanese.curriculum.resource.StudyResourceType
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
class CurriculumResourceCoverageTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun test01_allBundledResourcesAreVerifiedAndNonEmpty() {
        val resources = CurriculumResourceRepository.getAllResources(context)
        assertTrue("Resource bundle must contain verified items", resources.isNotEmpty())
        assertEquals("Exactly 18 verified external resources in curated foundation", 18, resources.size)

        for (res in resources) {
            assertTrue("ID must be non-blank", res.id.isNotBlank())
            assertTrue("Title must be non-blank", res.title.isNotBlank())
            assertTrue("Provider must be non-blank", res.provider.isNotBlank())
            assertTrue("URL must start with https://", res.url.startsWith("https://"))
            assertTrue("Associated concepts must not be empty", res.associatedConceptIds.isNotEmpty())
            assertTrue("Description must be non-blank", res.description.isNotBlank())
            assertTrue("Recommendation reason must be non-blank", res.reasonForRecommendation.isNotBlank())
            assertEquals("Verification status must be VERIFIED", ResourceVerificationStatus.VERIFIED, res.verificationStatus)
            assertEquals("Last verified date must be 2026-10-10", "2026-10-10", res.lastVerifiedDate)
        }
    }

    @Test
    fun test02_conceptResourceMappingIntegrity() {
        // N5 particle は / が / を
        val particles = CurriculumResourceRepository.getResourcesForConcept(context, "n5_g_01")
        assertTrue("Particle は (n5_g_01) must map to Tae Kim particles intro", particles.any { it.id == "res_g_tae_kim_particles_intro" })

        // N4 potential form
        val potential = CurriculumResourceRepository.getResourcesForConcept(context, "n4_g_01")
        assertTrue("Potential form (n4_g_01) must map to Tae Kim potential reference", potential.any { it.id == "res_g_tae_kim_potential" })

        // N4 kanji 都 (n4_k_10)
        val toCapital = CurriculumResourceRepository.getResourcesForConcept(context, "n4_k_10")
        assertTrue("Kanji 都 (n4_k_10) must map to Jisho entry", toCapital.any { it.id == "res_k_jisho_capital" })

        // N4 vocabulary 都合 (n4_v_06)
        val tsugo = CurriculumResourceRepository.getResourcesForConcept(context, "n4_v_06")
        assertTrue("Vocabulary 都合 (n4_v_06) must map to Jisho entry", tsugo.any { it.id == "res_v_jisho_n4_tsugo" })
    }

    @Test
    fun test03_coverageStateCorrectnessAndHonesty() {
        val item = JlptContentEntity(
            id = "n5_g_01",
            japanese = "は (Topic Marker)",
            reading = "わ",
            meaning = "Topic marker particle indicating conversational theme",
            category = "GRAMMAR",
            jlptLevel = "N5"
        )

        // Baseline profile without practice or formal assessment
        val baselineProfile = CurriculumResourceRepository.getCoverageProfile(
            context = context,
            item = item,
            isIncludedInLesson = false,
            isPracticed = false,
            isAssessed = false
        )

        assertTrue(baselineProfile.coverageStates.contains(CurriculumCoverageState.AVAILABLE_IN_AIKO))
        assertTrue(baselineProfile.coverageStates.contains(CurriculumCoverageState.INSTRUCTIONAL_CONTENT_AVAILABLE))
        assertFalse("Cannot be TAUGHT_BY_LESSON unless explicitly included in lesson", baselineProfile.coverageStates.contains(CurriculumCoverageState.TAUGHT_BY_LESSON))
        assertTrue(baselineProfile.coverageStates.contains(CurriculumCoverageState.RECOMMENDED_FURTHER_STUDY))
        assertFalse(baselineProfile.coverageStates.contains(CurriculumCoverageState.PRACTICED))
        assertFalse(baselineProfile.coverageStates.contains(CurriculumCoverageState.ASSESSED))

        // Profile with structured lesson inclusion, practice and formal assessment completed
        val practicedProfile = CurriculumResourceRepository.getCoverageProfile(
            context = context,
            item = item,
            isIncludedInLesson = true,
            isPracticed = true,
            isAssessed = true,
            isMastered = true,
            hasCurriculumGap = true
        )

        assertTrue(practicedProfile.coverageStates.contains(CurriculumCoverageState.TAUGHT_BY_LESSON))
        assertTrue(practicedProfile.coverageStates.contains(CurriculumCoverageState.PRACTICED))
        assertTrue(practicedProfile.coverageStates.contains(CurriculumCoverageState.ASSESSED))
        assertTrue(practicedProfile.coverageStates.contains(CurriculumCoverageState.DEMONSTRATED_MASTERY))
        assertTrue(practicedProfile.coverageStates.contains(CurriculumCoverageState.NOT_YET_COVERED))
        assertFalse(practicedProfile.knownGaps.isBlank())
    }

    @Test
    fun test04_noDuplicateUrlsAndValidSyntax() {
        val resources = CurriculumResourceRepository.getAllResources(context)
        val urls = resources.map { it.url }
        assertEquals("All URLs in bundle must be unique", urls.toSet().size, urls.size)
        assertTrue("All URLs must be valid web URLs", urls.all { it.startsWith("https://") && it.contains(".") })
    }

    @Test
    fun test05_resourceTypesDistribution() {
        val resources = CurriculumResourceRepository.getAllResources(context)
        val types = resources.map { it.resourceType }.toSet()

        assertTrue(types.contains(StudyResourceType.GRAMMAR_EXPLANATION))
        assertTrue(types.contains(StudyResourceType.KANJI_REFERENCE))
        assertTrue(types.contains(StudyResourceType.VOCABULARY_REFERENCE))
        assertTrue(types.contains(StudyResourceType.READING_PRACTICE))
        assertTrue(types.contains(StudyResourceType.LISTENING_PRACTICE))
        assertTrue(types.contains(StudyResourceType.EXERCISES_AND_QUIZZES))
        assertTrue(types.contains(StudyResourceType.GENERAL_REVIEW))
    }
}
