package com.example.noignore.data.content

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface JlptContentDao {
    @Query("SELECT * FROM jlpt_content_items ORDER BY id ASC")
    fun getAllContentFlow(): Flow<List<JlptContentEntity>>

    @Query("SELECT * FROM jlpt_content_items ORDER BY id ASC")
    suspend fun getAllContent(): List<JlptContentEntity>

    @Query("SELECT * FROM jlpt_content_items WHERE jlptLevel = :level ORDER BY id ASC")
    fun getContentByLevelFlow(level: String): Flow<List<JlptContentEntity>>

    @Query("SELECT * FROM jlpt_content_items WHERE jlptLevel = :level ORDER BY id ASC")
    suspend fun getContentByLevel(level: String): List<JlptContentEntity>

    @Query("SELECT * FROM jlpt_content_items WHERE category = :category ORDER BY id ASC")
    suspend fun getContentByCategory(category: String): List<JlptContentEntity>

    @Query("SELECT * FROM jlpt_content_items WHERE jlptLevel = :level AND category = :category ORDER BY id ASC")
    suspend fun getContentByLevelAndCategory(level: String, category: String): List<JlptContentEntity>

    @Query("SELECT * FROM jlpt_content_items WHERE id = :id LIMIT 1")
    suspend fun getItemById(id: String): JlptContentEntity?

    @Query("SELECT * FROM jlpt_content_items WHERE id IN (:ids)")
    suspend fun getItemsByIds(ids: List<String>): List<JlptContentEntity>

    @Query("SELECT * FROM jlpt_content_items WHERE japanese = :japanese LIMIT 1")
    suspend fun getItemByJapanese(japanese: String): JlptContentEntity?

    @Query("SELECT * FROM jlpt_content_items WHERE relatedItems LIKE '%' || :itemId || '%'")
    suspend fun getItemsReferencing(itemId: String): List<JlptContentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<JlptContentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: JlptContentEntity)

    @Update
    suspend fun update(item: JlptContentEntity)

    @Query("UPDATE jlpt_content_items SET mastery = :mastery, reviewCount = reviewCount + 1, lastReviewedAt = :timestamp WHERE id = :id")
    suspend fun recordReview(id: String, mastery: Int, timestamp: Long)

    @Query("SELECT COUNT(*) FROM jlpt_content_items")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM jlpt_content_items WHERE jlptLevel = :level")
    suspend fun countByLevel(level: String): Int

    @Query("SELECT COUNT(*) FROM jlpt_content_items WHERE jlptLevel = :level AND category = :category")
    suspend fun countByLevelAndCategory(level: String, category: String): Int

    @Query("DELETE FROM jlpt_content_items")
    suspend fun clearAll()
}
