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
    val isDone: Boolean = false,
    val drillType: String = "FLASHCARDS"
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
        isEmergencyMode: Boolean = false,
        kanaMasteryScore: Float = 1.0f,
        learnerStage: com.example.noignore.japanese.curriculum.transition.LearnerStage = com.example.noignore.japanese.curriculum.transition.LearnerStage.N5_ACTIVE
    ): DailyMissionPlan {
        val tasks = mutableListOf<DailyMissionTask>()
        if (isEmergencyMode) {
            tasks.add(DailyMissionTask("⚡ 5-Min Sprint", "Review top ${cardsDueCount.coerceAtLeast(5)} due cards", minOf(cardsDueCount.coerceAtLeast(5), 10), 0, false, "SRS"))
            tasks.add(DailyMissionTask("🎯 Quick Check", "1 High-leverage grammar pattern", 1, 0, false, "GRAMMAR"))
            return DailyMissionPlan(
                dateString = getTodayDateString(),
                isFiveMinuteMode = true,
                estimatedMinutes = 5,
                tasks = tasks,
                motivationalQuote = "5 minutes every single day is 100x better than 2 hours once a week!",
                isCompleted = false
            )
        } else if (learnerStage == com.example.noignore.japanese.curriculum.transition.LearnerStage.N4_BRIDGE) {
            tasks.add(DailyMissionTask("🧠 Spaced Repetition", "Clear all ${cardsDueCount.coerceAtLeast(5)} due review cards", cardsDueCount.coerceAtLeast(5), 0, false, "SRS"))
            tasks.add(DailyMissionTask("🌉 N4 Bridge: Core Review", "Consolidate N5 verb inflections & particles", 5, 0, false, "GRAMMAR"))
            tasks.add(DailyMissionTask("🎯 Bridge Diagnostic Check", "Test conjugation speed and grammar discrimination", 5, 0, false, "QUIZ"))
            return DailyMissionPlan(
                dateString = getTodayDateString(),
                isFiveMinuteMode = false,
                estimatedMinutes = 20,
                tasks = tasks,
                motivationalQuote = "Solidifying your foundation turns N4 from a struggle into a smooth climb!",
                isCompleted = false
            )
        } else if (kanaMasteryScore < 0.50f) {
            tasks.add(DailyMissionTask("Kana Foundation Drill", "Learn Hiragana & Katakana fundamentals", 10, 0, false, "KANA"))
            tasks.add(DailyMissionTask("Syllabus Step: Lesson Study", "Progress through structured Hiragana lessons", 1, 0, false, "LESSON"))
            tasks.add(DailyMissionTask("Kana Sound Recognition", "Practice vowel & consonant associations", 5, 0, false, "KANA"))
            return DailyMissionPlan(
                dateString = getTodayDateString(),
                isFiveMinuteMode = false,
                estimatedMinutes = 15,
                tasks = tasks,
                motivationalQuote = "Mastering Kana is the golden key that unlocks Japanese. Keep going!",
                isCompleted = false
            )
        } else {
            val levelLabel = if (learnerStage.isN4Accessible()) "N4" else curriculumLevel
            tasks.add(DailyMissionTask("🧠 Spaced Repetition", "Clear all ${cardsDueCount.coerceAtLeast(5)} due review cards", cardsDueCount.coerceAtLeast(5), 0, false, "SRS"))
            tasks.add(DailyMissionTask("📖 JLPT $levelLabel Mastery", "Learn 5 new vocabulary / kanji items", 5, 0, false, "FLASHCARDS"))
            tasks.add(DailyMissionTask("⛩️ Grammar & Reading", "1 Contextual passage reading drill", 1, 0, false, "READING"))
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
        forceEmergencyMode: Boolean = false,
        explicitStage: com.example.noignore.japanese.curriculum.transition.LearnerStage? = null,
        explicitLevel: String? = null
    ): DailyMissionPlan = withContext(Dispatchers.IO) {
        val todayStr = getTodayDateString()
        val missionDao = db.dailyMissionDao()
        val existingEntity = missionDao.getMission(todayStr)

        if (existingEntity != null && !forceEmergencyMode && explicitStage == null) {
            return@withContext parseMissionEntity(existingEntity)
        }

        // Generate dynamic mission based on dues, weaknesses, and Learner Profile
        val dueCards = db.flashcardSrsDao().getDueCardsSync(System.currentTimeMillis())
        val leeches = db.mistakeRecordDao().getLeeches()
        val learnerProfile = db.learnerProfileDao().getProfile("default_profile")
        val examTarget = db.jlptProgressDao().getTargetExamSync()

        val activeStage = explicitStage ?: com.example.noignore.japanese.curriculum.transition.LearnerStage.N5_ACTIVE
        val activeLevel = explicitLevel ?: if (activeStage.isN4Accessible()) "N4" else (examTarget?.targetLevel ?: "N5")

        val topLeechCardId = leeches.firstOrNull()?.cardId
        val topLeechItem = if (topLeechCardId != null) db.jlptContentDao().getItemById(topLeechCardId) else null
        val remediationPlan = if (topLeechItem != null) KnowledgeGraphService.buildRemediationPlan(db, topLeechItem) else null

        val isEmergency = forceEmergencyMode || (existingEntity?.isFiveMinuteMode == true)
        val estimatedMins = if (isEmergency) 5 else (learnerProfile?.preferredStudyDurationMinutes ?: 20)

        val tasks = mutableListOf<DailyMissionTask>()

        if (isEmergency) {
            tasks.add(DailyMissionTask("Rapid SRS Sprint", "Review top 5 urgent cards", 5, 0, false, "SRS"))
            if (remediationPlan != null) {
                tasks.add(DailyMissionTask("Leech Rescue", "Deconstruct '${remediationPlan.targetItem.japanese}' via knowledge graph", 1, 0, false, "LEECH"))
            } else if (leeches.isNotEmpty()) {
                tasks.add(DailyMissionTask("Leech Rescue", "Deconstruct 1 difficult leech item", 1, 0, false, "LEECH"))
            } else {
                tasks.add(DailyMissionTask("Daily Habit", "Complete 1 practice sentence", 1, 0, false, "QUIZ"))
            }
        } else if (activeStage == com.example.noignore.japanese.curriculum.transition.LearnerStage.N4_BRIDGE) {
            // N4 Bridge: prioritize N5 maintenance and high-leverage review
            val srsCount = dueCards.size.coerceIn(5, 25)
            tasks.add(DailyMissionTask("Spaced Repetition Review", "Clear due FSRS review queue (N5 maintenance)", srsCount, 0, false, "SRS"))
            tasks.add(DailyMissionTask("Bridge: High-Leverage N5 Review", "Consolidate core verb inflections & grammar foundations", 5, 0, false, "GRAMMAR"))
            tasks.add(DailyMissionTask("Bridge: Transition Diagnostic", "Practice speed recall & grammar discrimination", 5, 0, false, "QUIZ"))
        } else {
            val kanaScore = learnerProfile?.kanaMasteryScore ?: 0.1f
            val kanjiScore = learnerProfile?.kanjiMasteryScore ?: 0.1f
            val grammarScore = learnerProfile?.grammarMasteryScore ?: 0.1f
            val vocabScore = learnerProfile?.vocabMasteryScore ?: 0.1f
            val readingScore = learnerProfile?.readingMasteryScore ?: 0.1f
            val isKanaBeginner = kanaScore < 0.50f

            // Task 1: Primary foundation or SRS queue
            if (dueCards.isNotEmpty()) {
                val srsCount = dueCards.size.coerceIn(5, 25)
                tasks.add(DailyMissionTask("Spaced Repetition Review", "Clear due FSRS review queue", srsCount, 0, false, "SRS"))
            } else if (isKanaBeginner) {
                tasks.add(DailyMissionTask("Kana Foundation Drill", "Learn Hiragana & Katakana fundamentals", 10, 0, false, "KANA"))
            } else {
                val label = if (activeStage.isN4Accessible()) "JLPT N4 Practice" else "Core JLPT Practice"
                tasks.add(DailyMissionTask(label, "Review active cards and vocabulary", 10, 0, false, "FLASHCARDS"))
            }

            // Task 2: Adaptively target learner's progressive mastery respecting prerequisite gating
            if (isKanaBeginner) {
                if (tasks.none { it.drillType == "KANA" }) {
                    tasks.add(DailyMissionTask("Kana Foundation Drill", "Master core Hiragana characters", 10, 0, false, "KANA"))
                } else {
                    tasks.add(DailyMissionTask("Syllabus Step: Lesson Study", "Progress through structured Hiragana lessons", 1, 0, false, "LESSON"))
                }
            } else if (vocabScore < 0.50f && vocabScore <= grammarScore && vocabScore <= kanjiScore) {
                tasks.add(DailyMissionTask("Basic Vocabulary Drill", "Study high-frequency foundational words", 8, 0, false, "VOCAB"))
            } else if (grammarScore < 0.50f && grammarScore <= kanjiScore) {
                tasks.add(DailyMissionTask("Grammar Focus Drill", "Study grammar patterns & sentence structures", 5, 0, false, "GRAMMAR"))
            } else if (!com.example.noignore.japanese.curriculum.prerequisite.PrerequisiteEngine.isKanjiGated(kanaScore) && kanjiScore <= vocabScore && kanjiScore <= readingScore) {
                tasks.add(DailyMissionTask("Kanji Stroke & Recall", "Practice writing & recognizing kanji", 8, 0, false, "KANJI"))
            } else {
                val label = if (activeStage.isN4Accessible()) "JLPT N4 Skill Expansion" else "JLPT Skill Expansion"
                tasks.add(DailyMissionTask(label, "Study vocabulary or grammar patterns", 8, 0, false, "FLASHCARDS"))
            }

            // Task 3: Targeted Remediation, Reading/Listening (if prerequisite ready), or foundational reinforcement
            if (remediationPlan != null) {
                when (remediationPlan.targetItem.category.uppercase()) {
                    "VOCAB" -> {
                        val kanjiNote = if (remediationPlan.prerequisiteKanji.isNotEmpty()) " + kanji [${remediationPlan.prerequisiteKanji.first().japanese}]" else ""
                        val readingNote = if (remediationPlan.contextualReadings.isNotEmpty()) " + contextual reading" else ""
                        tasks.add(DailyMissionTask("Targeted Vocab Remediation", "Review '${remediationPlan.targetItem.japanese}'$kanjiNote$readingNote", 1, 0, false, "VOCAB"))
                    }
                    "GRAMMAR" -> {
                        val readingNote = if (remediationPlan.contextualReadings.isNotEmpty()) " + contextual reading" else " + formation"
                        tasks.add(DailyMissionTask("Targeted Grammar Repair", "Review '${remediationPlan.targetItem.japanese}'$readingNote", 1, 0, false, "GRAMMAR"))
                    }
                    "KANJI" -> {
                        val vocabNote = if (remediationPlan.supportingVocabulary.isNotEmpty()) " + vocab [${remediationPlan.supportingVocabulary.first().japanese}]" else ""
                        tasks.add(DailyMissionTask("Targeted Kanji Reinforcement", "Review '${remediationPlan.targetItem.japanese}'$vocabNote", 1, 0, false, "KANJI"))
                    }
                    else -> {
                        tasks.add(DailyMissionTask("Leech Intervention", "Tackle persistently confusing cards", leeches.size.coerceAtMost(3), 0, false, "LEECH"))
                    }
                }
            } else if (leeches.isNotEmpty()) {
                tasks.add(DailyMissionTask("Leech Intervention", "Tackle persistently confusing cards", leeches.size.coerceAtMost(3), 0, false, "LEECH"))
            } else if (com.example.noignore.japanese.curriculum.prerequisite.PrerequisiteEngine.isReadingReady(kanaScore, vocabScore, grammarScore)) {
                tasks.add(DailyMissionTask("Reading Comprehension", "Read 1 authentic passage", 1, 0, false, "READING"))
            } else if (com.example.noignore.japanese.curriculum.prerequisite.PrerequisiteEngine.isListeningReady(kanaScore, vocabScore)) {
                tasks.add(DailyMissionTask("Listening Comprehension", "Practice authentic audio dialogue", 1, 0, false, "LISTENING"))
            } else if (isKanaBeginner) {
                tasks.add(DailyMissionTask("Kana Sound Recognition", "Practice vowel & consonant associations", 5, 0, false, "KANA"))
            } else if (vocabScore < 0.50f) {
                tasks.add(DailyMissionTask("Everyday Phrases", "Practice essential conversational words", 5, 0, false, "VOCAB"))
            } else {
                tasks.add(DailyMissionTask("Comprehensive Check", "Complete 1 quick practice round", 5, 0, false, "FLASHCARDS"))
            }
        }

        val quote = if (isEmergency) {
            "Small daily progress is better than zero. 5 minutes counts!"
        } else if (activeStage == com.example.noignore.japanese.curriculum.transition.LearnerStage.N4_BRIDGE) {
            "Bridge the gap: solid fundamentals today unlock effortless fluency tomorrow!"
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
                put("drillType", t.drillType)
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
                            isDone = tObj.optBoolean("isDone", false),
                            drillType = tObj.optString("drillType", "FLASHCARDS")
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
