package com.example.noignore.japanese.engine

import com.example.noignore.data.AppDatabase
import com.example.noignore.data.mission.DailyMissionEntity
import com.example.noignore.japanese.curriculum.graph.KnowledgeGraphService
import com.example.noignore.japanese.data.JapaneseDeckRepository
import com.example.noignore.japanese.model.JapaneseItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DailyMissionTask(
    val title: String,
    val description: String,
    val targetCount: Int,
    var currentCount: Int = 0,
    val isDone: Boolean = false
)

data class DailyMissionPlan(
    val dateString: String,
    val isFiveMinuteMode: Boolean,
    val estimatedMinutes: Int,
    val tasks: List<DailyMissionTask>,
    val motivationalQuote: String,
    val isCompleted: Boolean
)

/**
 * Intelligent Adaptive Daily Study Planner.
 * Balances FSRS spaced repetition dues, high-priority leech interventions,
 * and time-boxed emergency modes.
 */
object DailyStudyPlanner {

    private fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun createDefaultPlan(
        cardsDueCount: Int,
        curriculumLevel: String = "N5",
        isEmergencyMode: Boolean = false
    ): DailyMissionPlan {
        val tasks = mutableListOf<DailyMissionTask>()
        if (isEmergencyMode) {
            tasks.add(DailyMissionTask("⚡ 5-Min Sprint", "Review top ${cardsDueCount.coerceAtLeast(5)} due cards", minOf(cardsDueCount.coerceAtLeast(5), 10)))
            tasks.add(DailyMissionTask("🎯 Quick Check", "1 High-leverage grammar pattern", 1))
            return DailyMissionPlan(
                dateString = getTodayDateString(),
                isFiveMinuteMode = true,
                estimatedMinutes = 5,
                tasks = tasks,
                motivationalQuote = "5 minutes every single day is 100x better than 2 hours once a week!",
                isCompleted = false
            )
        } else {
            tasks.add(DailyMissionTask("🧠 Spaced Repetition", "Clear all ${cardsDueCount.coerceAtLeast(5)} due review cards", cardsDueCount.coerceAtLeast(5)))
            tasks.add(DailyMissionTask("📖 JLPT $curriculumLevel Mastery", "Learn 5 new vocabulary / kanji items", 5))
            tasks.add(DailyMissionTask("⛩️ Grammar & Reading", "1 Contextual passage reading drill", 1))
            return DailyMissionPlan(
                dateString = getTodayDateString(),
                isFiveMinuteMode = false,
                estimatedMinutes = 20,
                tasks = tasks,
                motivationalQuote = "Consistency builds effortless recall. Teacher Aiko is proud of your effort!",
                isCompleted = false
            )
        }
    }

    suspend fun getOrCreateTodayMission(
        db: AppDatabase,
        deckRepo: JapaneseDeckRepository,
        forceEmergencyMode: Boolean = false
    ): DailyMissionPlan = withContext(Dispatchers.IO) {
        val todayStr = getTodayDateString()
        val missionDao = db.dailyMissionDao()
        val existingEntity = missionDao.getMission(todayStr)

        if (existingEntity != null && !forceEmergencyMode) {
            return@withContext parseMissionEntity(existingEntity)
        }

        // Generate dynamic mission based on dues, weaknesses, and Learner Profile
        val dueCards = db.flashcardSrsDao().getDueCardsSync(System.currentTimeMillis())
        val leeches = db.mistakeRecordDao().getLeeches()
        val learnerProfile = db.learnerProfileDao().getProfile("default_profile")

        val topLeechCardId = leeches.firstOrNull()?.cardId
        val topLeechItem = if (topLeechCardId != null) db.jlptContentDao().getItemById(topLeechCardId) else null
        val remediationPlan = if (topLeechItem != null) KnowledgeGraphService.buildRemediationPlan(db, topLeechItem) else null

        val isEmergency = forceEmergencyMode || (existingEntity?.isFiveMinuteMode == true)
        val estimatedMins = if (isEmergency) 5 else (learnerProfile?.preferredStudyDurationMinutes ?: 20)

        val tasks = mutableListOf<DailyMissionTask>()

        if (isEmergency) {
            tasks.add(DailyMissionTask("Rapid SRS Sprint", "Review top 5 urgent cards", 5, 0))
            if (remediationPlan != null) {
                tasks.add(DailyMissionTask("Leech Rescue", "Deconstruct '${remediationPlan.targetItem.japanese}' via knowledge graph", 1, 0))
            } else if (leeches.isNotEmpty()) {
                tasks.add(DailyMissionTask("Leech Rescue", "Deconstruct 1 difficult leech item", 1, 0))
            } else {
                tasks.add(DailyMissionTask("Daily Habit", "Complete 1 practice sentence", 1, 0))
            }
        } else {
            val srsCount = dueCards.size.coerceIn(5, 25)
            tasks.add(DailyMissionTask("Spaced Repetition Review", "Clear due FSRS review queue", srsCount, 0))

            // Adaptively target the learner's weakest mastery area
            if (learnerProfile != null) {
                val kanaScore = learnerProfile.kanaMasteryScore
                val kanjiScore = learnerProfile.kanjiMasteryScore
                val grammarScore = learnerProfile.grammarMasteryScore
                val vocabScore = learnerProfile.vocabMasteryScore
                val readingScore = learnerProfile.readingMasteryScore

                if (kanaScore < 0.6f && kanaScore <= grammarScore && kanaScore <= kanjiScore) {
                    tasks.add(DailyMissionTask("Kana Foundation Drill", "Master core Hiragana & Katakana characters", 10, 0))
                } else if (grammarScore <= kanjiScore && grammarScore <= vocabScore) {
                    tasks.add(DailyMissionTask("Grammar Focus Drill", "Study grammar patterns & sentence structures", 5, 0))
                } else if (kanjiScore <= vocabScore && kanjiScore <= readingScore) {
                    tasks.add(DailyMissionTask("Kanji Stroke & Recall", "Practice writing & recognizing kanji", 8, 0))
                } else {
                    tasks.add(DailyMissionTask("Core JLPT Practice", "Study kanji or vocabulary items", 10, 0))
                }

                if (remediationPlan != null) {
                    when (remediationPlan.targetItem.category.uppercase()) {
                        "VOCAB" -> {
                            val kanjiNote = if (remediationPlan.prerequisiteKanji.isNotEmpty()) " + kanji [${remediationPlan.prerequisiteKanji.first().japanese}]" else ""
                            val readingNote = if (remediationPlan.contextualReadings.isNotEmpty()) " + contextual reading" else ""
                            tasks.add(DailyMissionTask("Targeted Vocab Remediation", "Review '${remediationPlan.targetItem.japanese}'$kanjiNote$readingNote", 1, 0))
                        }
                        "GRAMMAR" -> {
                            val readingNote = if (remediationPlan.contextualReadings.isNotEmpty()) " + contextual reading" else " + formation"
                            tasks.add(DailyMissionTask("Targeted Grammar Repair", "Review '${remediationPlan.targetItem.japanese}'$readingNote", 1, 0))
                        }
                        "KANJI" -> {
                            val vocabNote = if (remediationPlan.supportingVocabulary.isNotEmpty()) " + vocab [${remediationPlan.supportingVocabulary.first().japanese}]" else ""
                            tasks.add(DailyMissionTask("Targeted Kanji Reinforcement", "Review '${remediationPlan.targetItem.japanese}'$vocabNote", 1, 0))
                        }
                        else -> {
                            tasks.add(DailyMissionTask("Leech Intervention", "Tackle persistently confusing cards", leeches.size.coerceAtMost(3), 0))
                        }
                    }
                } else if (leeches.isNotEmpty()) {
                    tasks.add(DailyMissionTask("Leech Intervention", "Tackle persistently confusing cards", leeches.size.coerceAtMost(3), 0))
                } else if (readingScore < 0.6f) {
                    tasks.add(DailyMissionTask("Reading Comprehension", "Read 1 authentic passage", 1, 0))
                } else {
                    tasks.add(DailyMissionTask("Immersion Practice", "Complete 1 listening or reading drill", 1, 0))
                }
            } else {
                tasks.add(DailyMissionTask("Core JLPT Practice", "Study kanji or grammar items", 10, 0))
                if (remediationPlan != null) {
                    when (remediationPlan.targetItem.category.uppercase()) {
                        "VOCAB" -> {
                            val kanjiNote = if (remediationPlan.prerequisiteKanji.isNotEmpty()) " + kanji [${remediationPlan.prerequisiteKanji.first().japanese}]" else ""
                            tasks.add(DailyMissionTask("Targeted Vocab Remediation", "Review '${remediationPlan.targetItem.japanese}'$kanjiNote", 1, 0))
                        }
                        "GRAMMAR" -> {
                            tasks.add(DailyMissionTask("Targeted Grammar Repair", "Review '${remediationPlan.targetItem.japanese}'", 1, 0))
                        }
                        "KANJI" -> {
                            tasks.add(DailyMissionTask("Targeted Kanji Reinforcement", "Review '${remediationPlan.targetItem.japanese}'", 1, 0))
                        }
                        else -> {
                            tasks.add(DailyMissionTask("Targeted Remediation", "Deconstruct '${remediationPlan.targetItem.japanese}' via knowledge graph", 1, 0))
                        }
                    }
                } else if (leeches.isNotEmpty()) {
                    tasks.add(DailyMissionTask("Leech Intervention", "Tackle persistently confusing cards", leeches.size.coerceAtMost(3), 0))
                } else {
                    tasks.add(DailyMissionTask("Reading Comprehension", "Read 1 authentic passage", 1, 0))
                }
            }
        }

        val quote = if (isEmergency) {
            "Small daily progress is better than zero. 5 minutes counts!"
        } else {
            "継続は力なり (Continuity is strength) — Step by step to JLPT success!"
        }

        val plan = DailyMissionPlan(
            dateString = todayStr,
            isFiveMinuteMode = isEmergency,
            estimatedMinutes = estimatedMins,
            tasks = tasks,
            motivationalQuote = quote,
            isCompleted = false
        )

        // Save to Room
        val missionJson = serializeMissionPlan(plan)
        missionDao.insertOrUpdate(
            DailyMissionEntity(
                dateString = todayStr,
                missionJson = missionJson,
                isFiveMinuteMode = isEmergency,
                isCompleted = false
            )
        )

        return@withContext plan
    }

    private fun serializeMissionPlan(plan: DailyMissionPlan): String {
        val root = JSONObject()
        root.put("estimatedMinutes", plan.estimatedMinutes)
        root.put("quote", plan.motivationalQuote)

        val taskArray = JSONArray()
        for (t in plan.tasks) {
            val tObj = JSONObject().apply {
                put("title", t.title)
                put("description", t.description)
                put("targetCount", t.targetCount)
                put("currentCount", t.currentCount)
                put("isDone", t.isDone)
            }
            taskArray.put(tObj)
        }
        root.put("tasks", taskArray)
        return root.toString()
    }

    private fun parseMissionEntity(entity: DailyMissionEntity): DailyMissionPlan {
        return try {
            val root = JSONObject(entity.missionJson)
            val estimated = root.optInt("estimatedMinutes", 15)
            val quote = root.optString("quote", "継続は力なり")

            val tasks = mutableListOf<DailyMissionTask>()
            val taskArr = root.optJSONArray("tasks")
            if (taskArr != null) {
                for (i in 0 until taskArr.length()) {
                    val tObj = taskArr.getJSONObject(i)
                    tasks.add(
                        DailyMissionTask(
                            title = tObj.getString("title"),
                            description = tObj.optString("description", ""),
                            targetCount = tObj.optInt("targetCount", 1),
                            currentCount = tObj.optInt("currentCount", 0),
                            isDone = tObj.optBoolean("isDone", false)
                        )
                    )
                }
            }

            DailyMissionPlan(
                dateString = entity.dateString,
                isFiveMinuteMode = entity.isFiveMinuteMode,
                estimatedMinutes = estimated,
                tasks = tasks,
                motivationalQuote = quote,
                isCompleted = entity.isCompleted
            )
        } catch (_: Exception) {
            DailyMissionPlan(
                dateString = entity.dateString,
                isFiveMinuteMode = entity.isFiveMinuteMode,
                estimatedMinutes = 15,
                tasks = listOf(DailyMissionTask("Daily JLPT Study", "Review cards", 10, 0, false)),
                motivationalQuote = "継続は力なり",
                isCompleted = entity.isCompleted
            )
        }
    }
}
