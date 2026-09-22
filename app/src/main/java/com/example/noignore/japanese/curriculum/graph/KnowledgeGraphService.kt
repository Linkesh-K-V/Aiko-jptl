package com.example.noignore.japanese.curriculum.graph

import com.example.noignore.data.AppDatabase
import com.example.noignore.data.content.JlptContentEntity

data class GraphStatistics(
    val totalCurriculumItems: Int,
    val connectedItems: Int,
    val isolatedItems: Int,
    val totalRelationshipEdges: Int,
    val kanjiToVocabEdges: Int,
    val vocabToReadingEdges: Int,
    val vocabToListeningEdges: Int,
    val grammarToReadingEdges: Int,
    val grammarToListeningEdges: Int,
    val examToKnowledgeEdges: Int,
    val vocabToGrammarEdges: Int,
    val kanaToVocabEdges: Int = 0
)

data class RemediationPlan(
    val targetItem: JlptContentEntity,
    val prerequisiteKanji: List<JlptContentEntity>,
    val supportingVocabulary: List<JlptContentEntity>,
    val supportingGrammar: List<JlptContentEntity>,
    val contextualReadings: List<JlptContentEntity>,
    val contextualListenings: List<JlptContentEntity>,
    val targetedQuestions: List<JlptContentEntity>,
    val summaryExplanation: String
)

/**
 * Japanese Knowledge Graph Service.
 * Traversing relational links between Kanji, Vocabulary, Grammar, Reading, Listening, and Exam questions
 * to build contextual reinforcement and adaptive remediation pathways.
 */
object KnowledgeGraphService {

    suspend fun getRelatedEntities(db: AppDatabase, item: JlptContentEntity): List<JlptContentEntity> {
        val relatedIds = item.getRelatedItemIds()
        if (relatedIds.isEmpty()) return emptyList()
        return db.jlptContentDao().getItemsByIds(relatedIds)
    }

    suspend fun buildRemediationPlan(db: AppDatabase, failedItem: JlptContentEntity): RemediationPlan {
        val related = getRelatedEntities(db, failedItem)
        
        val kanji = related.filter { it.category.equals("KANJI", ignoreCase = true) }
        val vocab = related.filter { it.category.equals("VOCAB", ignoreCase = true) }
        val grammar = related.filter { it.category.equals("GRAMMAR", ignoreCase = true) }
        val reading = related.filter { it.category.equals("READING", ignoreCase = true) }
        val listening = related.filter { it.category.equals("LISTENING", ignoreCase = true) }
        val questions = related.filter { it.category.equals("JLPT_EXAM", ignoreCase = true) }

        val explanation = when (failedItem.category.uppercase()) {
            "VOCAB" -> {
                buildString {
                    append("Targeted Vocabulary Remediation for '${failedItem.japanese}' (${failedItem.reading}): ")
                    if (kanji.isNotEmpty()) append("Review prerequisite kanji [${kanji.joinToString { it.japanese }}]. ")
                    if (grammar.isNotEmpty()) append("Practice sentence pattern [${grammar.first().japanese}]. ")
                    if (reading.isNotEmpty()) append("Review in context of reading passage [${reading.first().meaning}].")
                }
            }
            "GRAMMAR" -> {
                buildString {
                    append("Grammar Remediation for '${failedItem.japanese}': ")
                    if (vocab.isNotEmpty()) append("Review core vocabulary in examples [${vocab.take(3).joinToString { it.japanese }}]. ")
                    if (reading.isNotEmpty()) append("Contextual passage drill [${reading.first().meaning}]. ")
                    if (questions.isNotEmpty()) append("Test yourself on practice question [${questions.first().id}].")
                }
            }
            "KANJI" -> {
                buildString {
                    append("Kanji Remediation for '${failedItem.japanese}': ")
                    if (vocab.isNotEmpty()) append("Review compound vocabulary [${vocab.joinToString { it.japanese }}]. ")
                    if (reading.isNotEmpty()) append("See usage in reading context [${reading.first().meaning}].")
                }
            }
            else -> {
                "Knowledge reinforcement for '${failedItem.japanese}' using ${related.size} connected curriculum items."
            }
        }

        return RemediationPlan(
            targetItem = failedItem,
            prerequisiteKanji = kanji,
            supportingVocabulary = vocab,
            supportingGrammar = grammar,
            contextualReadings = reading,
            contextualListenings = listening,
            targetedQuestions = questions,
            summaryExplanation = explanation
        )
    }

    fun calculateStatistics(items: List<JlptContentEntity>): GraphStatistics {
        val itemMap = items.associateBy { it.id }
        var connected = 0
        var isolated = 0

        var kanjiToVocab = 0
        var vocabToReading = 0
        var vocabToListening = 0
        var grammarToReading = 0
        var grammarToListening = 0
        var examToKnowledge = 0
        var vocabToGrammar = 0
        var kanaToVocab = 0

        val uniquePairs = mutableSetOf<Pair<String, String>>()

        for (item in items) {
            val relatedIds = item.getRelatedItemIds()
            if (relatedIds.isEmpty()) {
                isolated++
                continue
            }
            connected++

            val srcCat = item.category.uppercase()
            for (targetId in relatedIds) {
                val target = itemMap[targetId] ?: continue
                val targetCat = target.category.uppercase()

                // Sort pair to count unique undirected edge once
                val pair = if (item.id < target.id) Pair(item.id, target.id) else Pair(target.id, item.id)
                if (!uniquePairs.contains(pair)) {
                    uniquePairs.add(pair)

                    // Classify directed type
                    when {
                        (srcCat == "KANJI" && targetCat == "VOCAB") || (srcCat == "VOCAB" && targetCat == "KANJI") -> kanjiToVocab++
                        (srcCat == "VOCAB" && targetCat == "READING") || (srcCat == "READING" && targetCat == "VOCAB") -> vocabToReading++
                        (srcCat == "VOCAB" && targetCat == "LISTENING") || (srcCat == "LISTENING" && targetCat == "VOCAB") -> vocabToListening++
                        (srcCat == "GRAMMAR" && targetCat == "READING") || (srcCat == "READING" && targetCat == "GRAMMAR") -> grammarToReading++
                        (srcCat == "GRAMMAR" && targetCat == "LISTENING") || (srcCat == "LISTENING" && targetCat == "GRAMMAR") -> grammarToListening++
                        (srcCat == "JLPT_EXAM") || (targetCat == "JLPT_EXAM") -> examToKnowledge++
                        (srcCat == "VOCAB" && targetCat == "GRAMMAR") || (srcCat == "GRAMMAR" && targetCat == "VOCAB") -> vocabToGrammar++
                        (srcCat == "KANA" && targetCat == "VOCAB") || (srcCat == "VOCAB" && targetCat == "KANA") -> kanaToVocab++
                    }
                }
            }
        }

        val totalEdges = uniquePairs.size

        return GraphStatistics(
            totalCurriculumItems = items.size,
            connectedItems = connected,
            isolatedItems = isolated,
            totalRelationshipEdges = totalEdges,
            kanjiToVocabEdges = kanjiToVocab,
            vocabToReadingEdges = vocabToReading,
            vocabToListeningEdges = vocabToListening,
            grammarToReadingEdges = grammarToReading,
            grammarToListeningEdges = grammarToListening,
            examToKnowledgeEdges = examToKnowledge,
            vocabToGrammarEdges = vocabToGrammar,
            kanaToVocabEdges = kanaToVocab
        )
    }
}
