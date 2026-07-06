package com.example.todayeat.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class FoodCount(val foodName: String, val count: Int)

data class HistoryWithCount(
    val id: Long,
    val foodName: String,
    val timestamp: Long,
    val totalCount: Int
)

@Dao
interface HistoryDao {

    @Insert
    suspend fun insert(entity: HistoryEntity)

    @Query("SELECT * FROM history ORDER BY timestamp DESC LIMIT :limit")
    fun getRecent(limit: Int = 50): Flow<List<HistoryEntity>>

    @Query("SELECT COUNT(*) FROM history WHERE foodName = :name")
    suspend fun getCountByName(name: String): Int

    @Query("""
        SELECT foodName, COUNT(*) as count
        FROM history
        GROUP BY foodName
    """)
    fun getAllCounts(): Flow<List<FoodCount>>

    @Query("""
        SELECT h.id, h.foodName, h.timestamp,
               (SELECT COUNT(*) FROM history h2 WHERE h2.foodName = h.foodName) as totalCount
        FROM history h
        ORDER BY h.timestamp DESC
        LIMIT :limit
    """)
    fun getHistoryWithCounts(limit: Int = 50): Flow<List<HistoryWithCount>>

    @Query("SELECT foodName FROM history WHERE timestamp > :cutoff GROUP BY foodName")
    fun getCooldownFoods(cutoff: Long): Flow<List<String>>

   @Query("DELETE FROM history WHERE id = :id")
   suspend fun deleteById(id: Long)

   @Query("DELETE FROM history")
   suspend fun deleteAll()

   @Query("SELECT COUNT(*) FROM history")
    suspend fun getTotalCount(): Int
}
