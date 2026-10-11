package com.example.noignore.japanese.curriculum.resource

import android.content.Context
import com.example.noignore.data.content.JlptContentEntity
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

object CurriculumResourceRepository {

    private const val RESOURCE_ASSET_PATH = "jlpt/further_study_resources.json"

    @Volatile
    private var cachedResources: List<CurriculumResource>? = null

    fun getAllResources(context: Context): List<CurriculumResource> {
        cachedResources?.let { return it }
        synchronized(this) {
            cachedResources?.let { return it }
            val loaded = loadFromAssets(context)
            cachedResources = loaded
            return loaded
        }
    }

    fun getResourcesForConcept(context: Context, conceptId: String): List<CurriculumResource> {
        return getAllResources(context).filter { res ->
            res.verificationStatus == ResourceVerificationStatus.VERIFIED &&
            res.associatedConceptIds.contains(conceptId)
        }
    }

    fun getResourcesForConcepts(context: Context, conceptIds: List<String>): List<CurriculumResource> {
        val targetSet = conceptIds.toSet()
        return getAllResources(context).filter { res ->
            res.verificationStatus == ResourceVerificationStatus.VERIFIED &&
            res.associatedConceptIds.any { targetSet.contains(it) }
        }
    }

    fun getCoverageProfile(
        context: Context,
        item: JlptContentEntity,
        isIncludedInLesson: Boolean = false,
        isPracticed: Boolean = false,
        isAssessed: Boolean = false,
        isMastered: Boolean = false,
        hasCurriculumGap: Boolean = true
    ): ConceptCoverageProfile {
        val resources = getResourcesForConcept(context, item.id)
        val states = mutableSetOf(CurriculumCoverageState.AVAILABLE_IN_AIKO)
        if (item.meaning.isNotBlank() || item.structure.isNotBlank()) {
            states.add(CurriculumCoverageState.INSTRUCTIONAL_CONTENT_AVAILABLE)
        }
        if (isIncludedInLesson) {
            states.add(CurriculumCoverageState.TAUGHT_BY_LESSON)
        }
        if (isPracticed) states.add(CurriculumCoverageState.PRACTICED)
        if (isAssessed) states.add(CurriculumCoverageState.ASSESSED)
        if (isMastered) states.add(CurriculumCoverageState.DEMONSTRATED_MASTERY)
        if (resources.isNotEmpty()) states.add(CurriculumCoverageState.RECOMMENDED_FURTHER_STUDY)
        if (hasCurriculumGap) states.add(CurriculumCoverageState.NOT_YET_COVERED)

        val whatCovers = when (item.category.uppercase()) {
            "GRAMMAR" -> "Aiko provides native formation structure, English breakdown, and audio-supported authentic examples."
            "KANJI" -> "Aiko covers stroke count, radical meanings, on/kun readings, writing pad drills, and compound vocabulary."
            "VOCAB" -> "Aiko teaches headword furigana, part-of-speech classification, English translation, and context sentences."
            "READING" -> "Aiko provides interactive reading passages with line-by-line comprehension questions."
            "LISTENING" -> "Aiko provides spoken native audio dialogue questions with comprehension options."
            else -> "Aiko covers core recognition and test preparation drills."
        }

        val whatLearnerCanPractice = "Interactive quizzes, spaced repetition flashcards, and daily mission drills."
        val knownGaps = when (item.jlptLevel) {
            "N5", "N4" -> "Covers core standard textbook usage. Complex idiomatic dialects and archaic literary exceptions are not covered in Aiko."
            else -> "Upper intermediate/advanced nuance requires extensive immersion reading."
        }

        return ConceptCoverageProfile(
            conceptId = item.id,
            conceptTitle = item.japanese,
            category = item.category,
            jlptLevel = item.jlptLevel,
            coverageStates = states,
            whatAikoCovers = whatCovers,
            whatLearnerCanPractice = whatLearnerCanPractice,
            knownGaps = knownGaps,
            furtherStudyResources = resources
        )
    }

    private fun loadFromAssets(context: Context): List<CurriculumResource> {
        return try {
            val jsonStr = context.assets.open(RESOURCE_ASSET_PATH).use { stream ->
                BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).use { it.readText() }
            }
            val arr = JSONArray(jsonStr)
            val list = mutableListOf<CurriculumResource>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val conceptsJson = obj.optJSONArray("associatedConceptIds")
                val conceptsList = mutableListOf<String>()
                if (conceptsJson != null) {
                    for (k in 0 until conceptsJson.length()) {
                        conceptsList.add(conceptsJson.getString(k))
                    }
                }

                val typeStr = obj.optString("resourceType", "GENERAL_REVIEW")
                val type = try {
                    StudyResourceType.valueOf(typeStr)
                } catch (_: Exception) {
                    StudyResourceType.GENERAL_REVIEW
                }

                val statusStr = obj.optString("verificationStatus", "VERIFIED")
                val status = try {
                    ResourceVerificationStatus.valueOf(statusStr)
                } catch (_: Exception) {
                    ResourceVerificationStatus.UNVERIFIED
                }

                list.add(
                    CurriculumResource(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        provider = obj.getString("provider"),
                        url = obj.getString("url"),
                        associatedConceptIds = conceptsList,
                        jlptLevel = obj.optString("jlptLevel", "N5"),
                        resourceType = type,
                        language = obj.optString("language", "en"),
                        description = obj.optString("description", ""),
                        reasonForRecommendation = obj.optString("reasonForRecommendation", ""),
                        verificationStatus = status,
                        lastVerifiedDate = obj.optString("lastVerifiedDate", "2026-10-10"),
                        provenance = obj.optString("provenance", "Curated educational partner"),
                        contentNotes = obj.optString("contentNotes", "")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }
}
