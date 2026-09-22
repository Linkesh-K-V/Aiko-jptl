package com.example.noignore.data.backup

import android.content.Context
import android.net.Uri
import com.example.noignore.data.AppDatabase
import com.example.noignore.data.Task
import com.example.noignore.data.srs.FlashcardSrsEntity
import com.example.noignore.japanese.curriculum.canonical.CanonicalCurriculumIds
import com.example.noignore.japanese.data.JapaneseDeckRepository
import com.example.noignore.model.RepeatMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.room.withTransaction
import java.security.MessageDigest

/**
 * Robust JSON-based Backup & Restore Manager.
 * Safely exports all Room databases (Tasks, SRS card states, Achievements, JLPT Progress)
 * and study preferences to a single portable JSON file with SHA-256 integrity verification,
 * and restores them with ACID transactions.
 */
class BackupRestoreManager(private val context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val deckRepo = JapaneseDeckRepository(context)

    companion object {
        const val BACKUP_SCHEMA_VERSION = 2

        fun computeSha256(input: String): String {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(input.toByteArray(Charsets.UTF_8))
            return hash.joinToString("") { "%02x".format(it) }
        }
    }

    suspend fun exportBackupJson(): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        root.put("version", BACKUP_SCHEMA_VERSION)
        root.put("timestamp", System.currentTimeMillis())
        root.put("exportDate", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
        root.put("app", "Aiko JLPT")

        // 1. Export Tasks
        val tasks = db.taskDao().getAllTasksOnce()
        val tasksArray = JSONArray()
        for (task in tasks) {
            val tObj = JSONObject().apply {
                put("id", task.id)
                put("title", task.title)
                put("timeMillis", task.timeMillis)
                put("repeatMode", task.repeatMode.name)
                put("repeatDays", task.repeatDays)
                put("confirmDelayMinutes", task.confirmDelayMinutes)
                put("ringtoneUri", task.ringtoneUri ?: "")
                put("hour", task.hour)
                put("minute", task.minute)
                put("completed", task.completed)
            }
            tasksArray.put(tObj)
        }
        root.put("tasks", tasksArray)

        // 2. Export SRS Cards
        val srsCards = db.flashcardSrsDao().getAllCardsSync()
        val srsArray = JSONArray()
        for (card in srsCards) {
            val cObj = JSONObject().apply {
                put("cardId", card.cardId)
                put("category", card.category)
                put("jlptLevel", card.jlptLevel)
                put("repetition", card.repetition)
                put("intervalDays", card.intervalDays)
                put("easeFactor", card.easeFactor.toDouble())
                put("dueDateMs", card.dueDateMs)
                put("lastReviewedMs", card.lastReviewedMs)
                put("lapses", card.lapses)
                put("state", card.state)
                put("totalReviews", card.totalReviews)
                put("lastRating", card.lastRating ?: "")
            }
            srsArray.put(cObj)
        }
        root.put("srsCards", srsArray)

        // 3. Export Coins & Global Stats
        val statsObj = JSONObject().apply {
            put("kaoCoins", deckRepo.getKaoCoins())
            put("cardsStudiedToday", deckRepo.getCardsStudiedToday())
            put("totalReviews", deckRepo.getTotalReviews())
            put("streak", deckRepo.getStudyStreak())
        }
        root.put("stats", statsObj)

        // 4. Export Achievements
        val achievements = db.achievementDao().getAllAchievementsOnce()
        val achArray = JSONArray()
        for (a in achievements) {
            val aObj = JSONObject().apply {
                put("id", a.id)
                put("unlocked", a.unlocked)
                put("unlockedAt", a.unlockedAt)
            }
            achArray.put(aObj)
        }
        root.put("achievements", achArray)

        // 5. Export Mined Items
        val minedItems = db.minedItemDao().getAllMinedItems()
        val minedArray = JSONArray()
        for (m in minedItems) {
            val mObj = JSONObject().apply {
                put("id", m.id)
                put("japanese", m.japanese)
                put("reading", m.reading)
                put("romaji", m.romaji)
                put("meaning", m.meaning)
                put("category", m.category)
                put("jlptLevel", m.jlptLevel)
                put("sourceSentence", m.sourceSentence)
                put("sourceEnglish", m.sourceEnglish)
                put("userNotes", m.userNotes)
                put("tags", m.tags)
                put("createdAtMs", m.createdAtMs)
                put("updatedAtMs", m.updatedAtMs)
            }
            minedArray.put(mObj)
        }
        root.put("minedItems", minedArray)

        // Compute checksum over content payload for tamper and corruption detection
        val payloadToHash = root.toString()
        root.put("checksumSha256", computeSha256(payloadToHash))

        return@withContext root.toString(2)
    }

    suspend fun writeBackupToUri(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val jsonString = exportBackupJson()
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream, Charsets.UTF_8).use { writer ->
                    writer.write(jsonString)
                    writer.flush()
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun restoreBackupFromUri(uri: Uri): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val sb = StringBuilder()
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).use { reader ->
                    var line = reader.readLine()
                    while (line != null) {
                        sb.append(line).append("\n")
                        line = reader.readLine()
                    }
                }
            }
            val jsonString = sb.toString().trim()
            if (jsonString.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("Backup file is empty."))
            }
            val root = JSONObject(jsonString)

            // Validate header schema version
            val version = root.optInt("version", 1)
            if (version > BACKUP_SCHEMA_VERSION) {
                return@withContext Result.failure(
                    IllegalStateException("Backup schema version $version is newer than supported version $BACKUP_SCHEMA_VERSION. Please update the app.")
                )
            }

            // Strict SHA-256 verification if present
            if (root.has("checksumSha256")) {
                val storedChecksum = root.getString("checksumSha256")
                val rootCopy = JSONObject(jsonString)
                rootCopy.remove("checksumSha256")
                val computed = computeSha256(rootCopy.toString())
                if (storedChecksum.isNotBlank() && computed.isNotBlank() && storedChecksum != computed) {
                    return@withContext Result.failure(
                        SecurityException("Backup SHA-256 checksum mismatch! The file may be corrupt or tampered with.")
                    )
                }
            }

            var restoredItemCount = 0

            // Execute all table restores inside an atomic Room database transaction
            db.withTransaction {
                // 1. Restore SRS Cards
                if (root.has("srsCards")) {
                    val srsArray = root.getJSONArray("srsCards")
                    val entities = mutableListOf<FlashcardSrsEntity>()
                    for (i in 0 until srsArray.length()) {
                        val cObj = srsArray.getJSONObject(i)
                        val rawCardId = cObj.getString("cardId")
                        val canonicalCardId = CanonicalCurriculumIds.canonicalize(rawCardId)
                        val entity = FlashcardSrsEntity(
                            cardId = canonicalCardId,
                            category = cObj.optString("category", ""),
                            jlptLevel = cObj.optString("jlptLevel", "ALL"),
                            repetition = cObj.optInt("repetition", 0),
                            intervalDays = cObj.optInt("intervalDays", 0),
                            easeFactor = cObj.optDouble("easeFactor", 2.50).toFloat(),
                            dueDateMs = cObj.optLong("dueDateMs", 0L),
                            lastReviewedMs = cObj.optLong("lastReviewedMs", 0L),
                            lapses = cObj.optInt("lapses", 0),
                            state = cObj.optString("state", "NEW"),
                            totalReviews = cObj.optInt("totalReviews", 0),
                            lastRating = if (cObj.has("lastRating") && !cObj.isNull("lastRating")) cObj.getString("lastRating") else null
                        )
                        val existingIdx = entities.indexOfFirst { it.cardId == canonicalCardId }
                        if (existingIdx >= 0) {
                            entities[existingIdx] = CanonicalCurriculumIds.consolidateSrs(entities[existingIdx], entity)
                        } else {
                            entities.add(entity)
                        }
                    }
                    if (entities.isNotEmpty()) {
                        db.flashcardSrsDao().insertAll(entities)
                        for (entity in entities) {
                            deckRepo.recordAnkiReview(
                                itemId = entity.cardId,
                                rating = com.example.noignore.japanese.model.AnkiRating.GOOD,
                                category = entity.category,
                                jlptLevel = entity.jlptLevel
                            )
                        }
                        restoredItemCount += entities.size
                    }
                }

                // 2. Restore Stats & Coins
                if (root.has("stats")) {
                    val stats = root.getJSONObject("stats")
                    val coins = stats.optInt("kaoCoins", 0)
                    deckRepo.setKaoCoins(coins)
                }

                // 3. Restore Tasks if present
                if (root.has("tasks")) {
                    val tasksArray = root.getJSONArray("tasks")
                    for (i in 0 until tasksArray.length()) {
                        val tObj = tasksArray.getJSONObject(i)
                        val task = Task(
                            id = tObj.optInt("id", 0),
                            title = tObj.getString("title"),
                            timeMillis = tObj.optLong("timeMillis", System.currentTimeMillis()),
                            repeatMode = try {
                                RepeatMode.valueOf(tObj.optString("repeatMode", "ONCE"))
                            } catch (_: Exception) { RepeatMode.ONCE },
                            repeatDays = tObj.optString("repeatDays", ""),
                            confirmDelayMinutes = tObj.optInt("confirmDelayMinutes", 5),
                            ringtoneUri = tObj.optString("ringtoneUri").ifBlank { null },
                            hour = tObj.optInt("hour", 9),
                            minute = tObj.optInt("minute", 0),
                            completed = tObj.optBoolean("completed", false)
                        )
                        db.taskDao().insert(task)
                        restoredItemCount++
                    }
                }

                // 4. Restore Mined Items
                if (root.has("minedItems")) {
                    val minedArray = root.getJSONArray("minedItems")
                    val minedEntities = mutableListOf<com.example.noignore.data.mined.MinedItemEntity>()
                    for (i in 0 until minedArray.length()) {
                        val mObj = minedArray.getJSONObject(i)
                        minedEntities.add(
                            com.example.noignore.data.mined.MinedItemEntity(
                                id = mObj.getString("id"),
                                japanese = mObj.getString("japanese"),
                                reading = mObj.getString("reading"),
                                romaji = mObj.optString("romaji", ""),
                                meaning = mObj.getString("meaning"),
                                category = mObj.optString("category", "VOCAB"),
                                jlptLevel = mObj.optString("jlptLevel", "Custom"),
                                sourceSentence = mObj.optString("sourceSentence", ""),
                                sourceEnglish = mObj.optString("sourceEnglish", ""),
                                userNotes = mObj.optString("userNotes", ""),
                                tags = mObj.optString("tags", ""),
                                createdAtMs = mObj.optLong("createdAtMs", System.currentTimeMillis()),
                                updatedAtMs = mObj.optLong("updatedAtMs", System.currentTimeMillis())
                            )
                        )
                    }
                    if (minedEntities.isNotEmpty()) {
                        db.minedItemDao().insertAll(minedEntities)
                        restoredItemCount += minedEntities.size
                    }
                }
            }

            Result.success(restoredItemCount)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
