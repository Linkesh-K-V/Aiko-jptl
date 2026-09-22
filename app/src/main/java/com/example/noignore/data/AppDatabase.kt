package com.example.noignore.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.noignore.data.jlpt.JlptExamTargetEntity
import com.example.noignore.data.jlpt.JlptProgressDao
import com.example.noignore.data.jlpt.JlptProgressEntity
import com.example.noignore.data.jlpt.JlptProgressRepository
import com.example.noignore.data.srs.FlashcardSrsDao
import com.example.noignore.data.srs.FlashcardSrsEntity
import androidx.room.migration.Migration
import com.example.noignore.data.content.JlptContentDao
import com.example.noignore.data.content.JlptContentEntity
import com.example.noignore.data.learner.LearnerProfileDao
import com.example.noignore.data.learner.LearnerProfileEntity
import com.example.noignore.data.learner.MistakeRecordDao
import com.example.noignore.data.learner.MistakeRecordEntity
import com.example.noignore.data.mined.MinedItemDao
import com.example.noignore.data.mined.MinedItemEntity
import com.example.noignore.data.mission.DailyMissionDao
import com.example.noignore.data.mission.DailyMissionEntity
import com.example.noignore.japanese.curriculum.canonical.CanonicalCurriculumIds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Task::class,
        TaskHistory::class,
        Achievement::class,
        JlptProgressEntity::class,
        JlptExamTargetEntity::class,
        FlashcardSrsEntity::class,
        MinedItemEntity::class,
        JlptContentEntity::class,
        MistakeRecordEntity::class,
        LearnerProfileEntity::class,
        DailyMissionEntity::class
    ],
    version = 7,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun taskHistoryDao(): TaskHistoryDao
    abstract fun achievementDao(): AchievementDao
    abstract fun jlptProgressDao(): JlptProgressDao
    abstract fun flashcardSrsDao(): FlashcardSrsDao
    abstract fun minedItemDao(): MinedItemDao
    abstract fun jlptContentDao(): JlptContentDao
    abstract fun mistakeRecordDao(): MistakeRecordDao
    abstract fun learnerProfileDao(): LearnerProfileDao
    abstract fun dailyMissionDao(): DailyMissionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. mined_items
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `mined_items` (
                        `id` TEXT NOT NULL,
                        `japanese` TEXT NOT NULL,
                        `reading` TEXT NOT NULL,
                        `romaji` TEXT NOT NULL,
                        `meaning` TEXT NOT NULL,
                        `category` TEXT NOT NULL,
                        `jlptLevel` TEXT NOT NULL,
                        `sourceSentence` TEXT NOT NULL,
                        `sourceEnglish` TEXT NOT NULL,
                        `userNotes` TEXT NOT NULL,
                        `tags` TEXT NOT NULL,
                        `createdAtMs` INTEGER NOT NULL,
                        `updatedAtMs` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_mined_items_japanese` ON `mined_items` (`japanese`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_mined_items_jlptLevel` ON `mined_items` (`jlptLevel`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_mined_items_createdAtMs` ON `mined_items` (`createdAtMs`)")

                // 2. jlpt_content_items
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `jlpt_content_items` (
                        `id` TEXT NOT NULL,
                        `japanese` TEXT NOT NULL,
                        `reading` TEXT NOT NULL,
                        `romaji` TEXT NOT NULL,
                        `meaning` TEXT NOT NULL,
                        `category` TEXT NOT NULL,
                        `jlptLevel` TEXT NOT NULL,
                        `onyomi` TEXT NOT NULL,
                        `kunyomi` TEXT NOT NULL,
                        `strokeCount` INTEGER NOT NULL,
                        `radical` TEXT NOT NULL,
                        `mnemonicOrNote` TEXT NOT NULL,
                        `exampleJapanese` TEXT NOT NULL,
                        `exampleReading` TEXT NOT NULL,
                        `exampleRomaji` TEXT NOT NULL,
                        `exampleEnglish` TEXT NOT NULL,
                        `partOfSpeech` TEXT NOT NULL,
                        `difficulty` INTEGER NOT NULL,
                        `tags` TEXT NOT NULL,
                        `relatedItems` TEXT NOT NULL,
                        `audioReference` TEXT NOT NULL,
                        `source` TEXT NOT NULL,
                        `examQuestionType` TEXT NOT NULL,
                        `examQuestionPrompt` TEXT NOT NULL,
                        `optionsJson` TEXT NOT NULL,
                        `mastery` INTEGER NOT NULL,
                        `reviewCount` INTEGER NOT NULL,
                        `lastReviewedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_jlpt_content_items_jlptLevel` ON `jlpt_content_items` (`jlptLevel`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_jlpt_content_items_category` ON `jlpt_content_items` (`category`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_jlpt_content_items_jlptLevel_category` ON `jlpt_content_items` (`jlptLevel`, `category`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_jlpt_content_items_japanese` ON `jlpt_content_items` (`japanese`)")

                // 3. mistake_records
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `mistake_records` (
                        `cardId` TEXT NOT NULL,
                        `jlptLevel` TEXT NOT NULL,
                        `category` TEXT NOT NULL,
                        `lapseCount` INTEGER NOT NULL,
                        `totalAttempts` INTEGER NOT NULL,
                        `mistakeType` TEXT NOT NULL,
                        `lastMistakeMs` INTEGER NOT NULL,
                        `isLeech` INTEGER NOT NULL,
                        `interventionNotes` TEXT NOT NULL,
                        PRIMARY KEY(`cardId`)
                    )
                """.trimIndent())

                // 4. learner_profile
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `learner_profile` (
                        `id` TEXT NOT NULL,
                        `vocabMasteryScore` REAL NOT NULL,
                        `kanjiMasteryScore` REAL NOT NULL,
                        `grammarMasteryScore` REAL NOT NULL,
                        `readingMasteryScore` REAL NOT NULL,
                        `listeningMasteryScore` REAL NOT NULL,
                        `recallStrengthScore` REAL NOT NULL,
                        `averageResponseTimeMs` INTEGER NOT NULL,
                        `confidenceScore` REAL NOT NULL,
                        `preferredStudyDurationMinutes` INTEGER NOT NULL,
                        `preferredDifficulty` TEXT NOT NULL,
                        `immersionLevel` TEXT NOT NULL,
                        `lastCalculatedMs` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())

                // 5. daily_missions
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `daily_missions` (
                        `dateString` TEXT NOT NULL,
                        `missionJson` TEXT NOT NULL,
                        `isFiveMinuteMode` INTEGER NOT NULL,
                        `isCompleted` INTEGER NOT NULL,
                        `completedAtMs` INTEGER,
                        PRIMARY KEY(`dateString`)
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Add kanaMasteryScore to learner_profile
                db.execSQL("ALTER TABLE `learner_profile` ADD COLUMN `kanaMasteryScore` REAL NOT NULL DEFAULT 0.0")

                // 2. Add structure to jlpt_content_items
                db.execSQL("ALTER TABLE `jlpt_content_items` ADD COLUMN `structure` TEXT NOT NULL DEFAULT ''")

                // 3. Consolidate SRS reviews and remove duplicate records safely
                for ((dupId, canonicalId) in CanonicalCurriculumIds.DUPLICATE_TO_CANONICAL_MAP) {
                    // Update mistake records
                    db.execSQL("UPDATE `mistake_records` SET `cardId` = '$canonicalId' WHERE `cardId` = '$dupId'")

                    // Consolidate SRS rows: accumulate review count and lapses into canonical item
                    db.execSQL("""
                        UPDATE `flashcard_srs` 
                        SET `totalReviews` = `totalReviews` + COALESCE((SELECT `totalReviews` FROM `flashcard_srs` WHERE `cardId` = '$dupId'), 0),
                            `lapses` = `lapses` + COALESCE((SELECT `lapses` FROM `flashcard_srs` WHERE `cardId` = '$dupId'), 0)
                        WHERE `cardId` = '$canonicalId'
                    """.trimIndent())
                    db.execSQL("DELETE FROM `flashcard_srs` WHERE `cardId` = '$dupId' AND EXISTS (SELECT 1 FROM `flashcard_srs` WHERE `cardId` = '$canonicalId')")
                    db.execSQL("UPDATE `flashcard_srs` SET `cardId` = '$canonicalId' WHERE `cardId` = '$dupId'")

                    // Remove duplicate from jlpt_content_items
                    db.execSQL("DELETE FROM `jlpt_content_items` WHERE `id` = '$dupId'")
                }
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "noignore_database"
                )
                    .addMigrations(MIGRATION_5_6, MIGRATION_6_7)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Prepopulate default achievements and JLPT progress
                            CoroutineScope(Dispatchers.IO).launch {
                                val dbInstance = getInstance(context)
                                dbInstance.achievementDao().insertAll(INITIAL_ACHIEVEMENTS)
                                JlptProgressRepository(dbInstance.jlptProgressDao()).initializeDefaultsIfEmpty()
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        val INITIAL_ACHIEVEMENTS = listOf(
            Achievement("aiko_approval_7", "Aiko's 7-Day Approval", "Maintain a 7-day streak to earn Sensei Aiko's official seal of approval", "💮", 7, "APPROVAL"),
            Achievement("aiko_approval_30", "Aiko's 30-Day Approval", "Maintain a 30-day streak to earn Sensei Aiko's master endorsement", "👑", 30, "APPROVAL"),
            Achievement("streak_1", "First Spark", "Complete your first task and ignite your streak", "🔥", 1, "STREAK"),
            Achievement("streak_3", "Consistent Step", "Maintain a 3-day discipline streak", "⚡", 3, "STREAK"),
            Achievement("streak_7", "Disciplined Week", "7 consecutive days without backing down", "⚔️", 7, "STREAK"),
            Achievement("streak_14", "Fortress of Will", "14 days of unbroken consistency", "🛡️", 14, "STREAK"),
            Achievement("streak_30", "Iron Will", "Reach an elite 30-day streak", "👑", 30, "STREAK"),
            Achievement("streak_90", "Demon Slayer", "90 days of legendary discipline", "👹", 90, "STREAK"),
            Achievement("tasks_5", "Getting Started", "Finish 5 tasks total", "🎯", 5, "COMPLETION"),
            Achievement("tasks_25", "Executioner", "Finish 25 tasks total", "🔨", 25, "COMPLETION"),
            Achievement("tasks_50", "Relentless Force", "Finish 50 tasks total", "🌋", 50, "COMPLETION"),
            Achievement("tasks_100", "Master of Discipline", "Finish 100 tasks without compromise", "🏆", 100, "COMPLETION")
        )
    }
}
