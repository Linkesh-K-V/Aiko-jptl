package com.example.noignore.japanese.curriculum

import android.content.Context
import android.util.Log
import com.example.noignore.data.AppDatabase
import com.example.noignore.data.content.JlptContentEntity
import com.example.noignore.japanese.curriculum.validation.ContentValidator
import com.example.noignore.japanese.data.JlptExamDeckData
import com.example.noignore.japanese.data.JlptExamDeckExtended
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

object JlptAssetContentLoader {

    private const val TAG = "JlptContentLoader"
    private const val PREF_CONTENT_VERSION = "jlpt_curriculum_asset_version"
    private const val CURRENT_CURRICULUM_VERSION = 7

    suspend fun loadAllCurriculumIntoRoom(
        context: Context,
        forceReload: Boolean = false,
        includeLegacyBaseline: Boolean = false
    ): Int = withContext(Dispatchers.IO) {
        val db = AppDatabase.getInstance(context)
        loadAllCurriculumIntoRoom(context, db, forceReload, includeLegacyBaseline)
    }

    suspend fun loadAllCurriculumIntoRoom(
        context: Context,
        db: AppDatabase,
        forceReload: Boolean = false,
        includeLegacyBaseline: Boolean = false
    ): Int = withContext(Dispatchers.IO) {
        val prefs = context.getSharedPreferences("renshuu_japanese_prefs", Context.MODE_PRIVATE)
        val loadedVersion = prefs.getInt(PREF_CONTENT_VERSION, 0)

        val contentDao = db.jlptContentDao()

        val existingCount = contentDao.count()
        if (!forceReload && loadedVersion >= CURRENT_CURRICULUM_VERSION && existingCount > 50) {
            Log.d(TAG, "Curriculum already up to date ($existingCount items loaded).")
            return@withContext existingCount
        }

        Log.i(TAG, "Starting curriculum asset import into Room SQLite...")
        val allEntities = mutableListOf<JlptContentEntity>()

        // 1. Ingest structured asset JSON datasets across N5 to N1 first (priority)
        val levels = listOf("n5", "n4", "n3", "n2", "n1")
        val fileCategories = listOf("kanji", "vocabulary", "grammar", "questions", "reading", "listening", "kana")

        for (lvl in levels) {
            for (cat in fileCategories) {
                val assetPath = "jlpt/$lvl/$cat.json"
                try {
                    val jsonStr = readAssetFile(context, assetPath)
                    if (jsonStr.isNotBlank()) {
                        val parsed = parseJsonArray(jsonStr, lvl.uppercase(), cat.uppercase())
                        allEntities.addAll(parsed)
                        Log.d(TAG, "Loaded ${parsed.size} items from $assetPath")
                    }
                } catch (_: java.io.FileNotFoundException) {
                    // Category file not yet populated for this level
                } catch (e: Exception) {
                    Log.w(TAG, "Could not load $assetPath: ${e.message}")
                }
            }
        }

        // 2. Ingest hardcoded baseline deck items for any non-overlapping legacy items if requested
        if (includeLegacyBaseline) {
            val assetIds = allEntities.map { it.id }.toSet()
            try {
                for (k in JlptExamDeckData.kanjiList) {
                    if (!assetIds.contains(k.id)) {
                        allEntities.add(JlptContentEntity.fromJapaneseItem(k, "LegacyKanjiList"))
                    }
                }
                for (q in JlptExamDeckData.jlptExamQuestions) {
                    if (!assetIds.contains(q.id)) {
                        allEntities.add(JlptContentEntity.fromJapaneseItem(q, "LegacyExamQuestions"))
                    }
                }
                for (eq in JlptExamDeckExtended.extendedExamQuestions) {
                    if (!assetIds.contains(eq.id)) {
                        allEntities.add(JlptContentEntity.fromJapaneseItem(eq, "ExtendedExamQuestions"))
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading legacy baseline deck: ${e.message}")
            }
        }

        // 3. Educational Validation Pipeline
        val (validEntities, validationReport) = ContentValidator.validateBatch(allEntities)
        if (validationReport.isNotEmpty()) {
            Log.w(TAG, "Curriculum validation report: ${validationReport.take(15).joinToString("\n")}")
        }

        // 4. Batch insert into Room database
        contentDao.insertAll(validEntities)
        prefs.edit().putInt(PREF_CONTENT_VERSION, CURRENT_CURRICULUM_VERSION).apply()

        Log.i(TAG, "Successfully populated Room with ${validEntities.size} verified JLPT items.")
        return@withContext validEntities.size
    }

    private fun readAssetFile(context: Context, path: String): String {
        return context.assets.open(path).use { stream ->
            BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).use { reader ->
                reader.readText()
            }
        }
    }

    private fun parseJsonArray(jsonString: String, defaultLevel: String, defaultCategory: String): List<JlptContentEntity> {
        val list = mutableListOf<JlptContentEntity>()
        val array = JSONArray(jsonString)

        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val id = obj.optString("id", "${defaultLevel.lowercase()}_${defaultCategory.lowercase()}_$i")
            val japanese = obj.getString("japanese")
            val reading = obj.optString("reading", "")
            val romaji = obj.optString("romaji", "")
            val meaning = obj.optString("meaning", "")
            val category = obj.optString("category", defaultCategory)
            val level = obj.optString("jlptLevel", defaultLevel)

            val optionsList = mutableListOf<String>()
            if (obj.has("options")) {
                val optArr = obj.getJSONArray("options")
                for (j in 0 until optArr.length()) {
                    optionsList.add(optArr.getString(j))
                }
            }
            val optionsJson = if (optionsList.isNotEmpty()) {
                "[" + optionsList.joinToString(",") { "\"${it.replace("\"", "\\\"")}\"" } + "]"
            } else ""

            val relatedList = mutableListOf<String>()
            if (obj.has("relatedItems")) {
                val relObj = obj.get("relatedItems")
                if (relObj is org.json.JSONArray) {
                    for (k in 0 until relObj.length()) {
                        val s = relObj.getString(k).trim()
                        if (s.isNotEmpty()) relatedList.add(s)
                    }
                } else if (relObj is String) {
                    relObj.split(",").map { it.trim() }.filter { it.isNotEmpty() }.forEach { relatedList.add(it) }
                }
            }
            val relatedItemsStr = relatedList.distinct().joinToString(",")

            list.add(
                JlptContentEntity(
                    id = id,
                    japanese = japanese,
                    reading = reading,
                    romaji = romaji,
                    meaning = meaning,
                    category = category,
                    jlptLevel = level,
                    onyomi = obj.optString("onyomi", ""),
                    kunyomi = obj.optString("kunyomi", ""),
                    strokeCount = obj.optInt("strokeCount", 0),
                    radical = obj.optString("radical", ""),
                    structure = obj.optString("structure", ""),
                    mnemonicOrNote = obj.optString("mnemonicOrNote", ""),
                    exampleJapanese = obj.optString("exampleJapanese", ""),
                    exampleReading = obj.optString("exampleReading", ""),
                    exampleRomaji = obj.optString("exampleRomaji", ""),
                    exampleEnglish = obj.optString("exampleEnglish", ""),
                    partOfSpeech = obj.optString("partOfSpeech", ""),
                    difficulty = obj.optInt("difficulty", 1),
                    tags = obj.optString("tags", ""),
                    relatedItems = relatedItemsStr,
                    audioReference = obj.optString("audioReference", ""),
                    source = obj.optString("source", "Aiko Curriculum"),
                    examQuestionType = obj.optString("examQuestionType", ""),
                    examQuestionPrompt = obj.optString("examQuestionPrompt", ""),
                    optionsJson = optionsJson
                )
            )
        }
        return list
    }
}
