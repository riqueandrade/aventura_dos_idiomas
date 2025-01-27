package com.example.aventuradosidiomas.data.dao

import androidx.room.*
import com.example.aventuradosidiomas.data.entity.Mission
import kotlinx.coroutines.flow.Flow

@Dao
interface MissionDao {
    @Query("SELECT * FROM missions")
    fun getAllMissions(): Flow<List<Mission>>

    @Query("SELECT DISTINCT category FROM missions")
    fun getAllCategories(): Flow<List<String>>

    @Query("SELECT * FROM missions WHERE category = :category")
    fun getMissionsByCategory(category: String): Flow<List<Mission>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMission(mission: Mission)

    @Query("DELETE FROM missions")
    suspend fun deleteAllMissions()

    @Query("SELECT * FROM missions WHERE isUnlocked = 1 ORDER BY requiredLevel")
    fun getUnlockedMissions(): Flow<List<Mission>>

    @Query("SELECT * FROM missions WHERE id = :missionId")
    fun getMissionById(missionId: Long): Flow<Mission?>

    @Update
    suspend fun updateMission(mission: Mission)

    @Query("UPDATE missions SET isUnlocked = 1 WHERE id = :missionId")
    suspend fun unlockMission(missionId: Long)
} 