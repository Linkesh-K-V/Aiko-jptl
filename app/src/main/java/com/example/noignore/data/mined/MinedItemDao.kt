package com.example.noignore.data.mined

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MinedItemDao {
    @Query("SELECT * FROM mined_items ORDER BY createdAtMs DESC")
    fun getAllMinedItemsFlow(): Flow<List<MinedItemEntity>>

    @Query("SELECT * FROM mined_items ORDER BY createdAtMs DESC")
    suspend fun getAllMinedItems(): List<MinedItemEntity>

    @Query("SELECT * FROM mined_items WHERE id = :id LIMIT 1")
    suspend fun getMinedItemById(id: String): MinedItemEntity?

    @Query("SELECT * FROM mined_items WHERE japanese = :japanese LIMIT 1")
    suspend fun getMinedItemByJapanese(japanese: String): MinedItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: MinedItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<MinedItemEntity>)

    @Update
    suspend fun update(item: MinedItemEntity)

    @Delete
    suspend fun delete(item: MinedItemEntity)

    @Query("DELETE FROM mined_items WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM mined_items")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM mined_items")
    fun countFlow(): Flow<Int>
}
