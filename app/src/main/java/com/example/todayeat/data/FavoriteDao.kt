package com.example.todayeat.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE foodName = :name")
    suspend fun delete(name: String)

    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    fun getAll(): Flow<List<FavoriteEntity>>

    @Query("SELECT foodName FROM favorites")
    fun getAllNames(): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE foodName = :name)")
    suspend fun isFavorite(name: String): Boolean

    @Query("SELECT COUNT(*) FROM favorites")
    suspend fun getCount(): Int

    @Query("DELETE FROM favorites")
    suspend fun deleteAll()
}
