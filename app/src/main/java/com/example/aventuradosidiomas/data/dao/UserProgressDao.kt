package com.example.aventuradosidiomas.data.dao

import androidx.room.*
import com.example.aventuradosidiomas.data.entity.UserProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {
    @Query("SELECT * FROM user_progress WHERE userId = :userId")
    fun getUserProgress(userId: Long): Flow<List<UserProgress>>

    @Query("SELECT * FROM user_progress WHERE userId = :userId AND missionId = :missionId")
    fun getMissionProgress(userId: Long, missionId: Long): Flow<UserProgress?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: UserProgress)

    @Update
    suspend fun updateProgress(progress: UserProgress)

    @Query("""
        SELECT COUNT(*) FROM user_progress 
        WHERE userId = :userId AND isCompleted = 1
    """)
    fun getCompletedMissionsCount(userId: Long): Flow<Int>

    @Query("""
        SELECT SUM(score) FROM user_progress 
        WHERE userId = :userId AND isCompleted = 1
    """)
    fun getTotalScore(userId: Long): Flow<Int>

    @Transaction
    @Query("""
        SELECT * FROM user_progress 
        WHERE userId = :userId 
        ORDER BY lastAttemptDate DESC 
        LIMIT :limit
    """)
    fun getRecentProgress(userId: Long, limit: Int = 10): Flow<List<UserProgress>>
} 