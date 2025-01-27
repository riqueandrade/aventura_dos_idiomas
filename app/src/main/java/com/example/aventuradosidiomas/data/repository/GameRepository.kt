package com.example.aventuradosidiomas.data.repository

import com.example.aventuradosidiomas.data.AppDatabase
import com.example.aventuradosidiomas.data.entity.Mission
import com.example.aventuradosidiomas.data.entity.User
import com.example.aventuradosidiomas.data.entity.UserProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class GameRepository(private val database: AppDatabase) {
    // User operations
    fun getCurrentUser(): Flow<User?> = database.userDao().getCurrentUser()

    suspend fun createUser(name: String, language: String): Long {
        val user = User(
            name = name,
            selectedLanguage = language
        )
        return database.userDao().insertUser(user)
    }

    suspend fun updateUserPoints(userId: Long, points: Int) {
        database.userDao().addPoints(userId, points)
    }

    suspend fun updateUserLevel(userId: Long, newLevel: Int) {
        database.userDao().updateLevel(userId, newLevel)
    }

    // Mission operations
    fun getMissionsByCategory(category: String): Flow<List<Mission>> =
        database.missionDao().getMissionsByCategory(category)

    fun getUnlockedMissions(): Flow<List<Mission>> =
        database.missionDao().getUnlockedMissions()

    fun getAllCategories(): Flow<List<String>> =
        database.missionDao().getAllCategories()

    suspend fun unlockMission(missionId: Long) {
        database.missionDao().unlockMission(missionId)
    }

    // Progress operations
    fun getUserProgress(userId: Long): Flow<List<UserProgress>> =
        database.userProgressDao().getUserProgress(userId)

    fun getMissionProgress(userId: Long, missionId: Long): Flow<UserProgress?> =
        database.userProgressDao().getMissionProgress(userId, missionId)

    suspend fun updateProgress(progress: UserProgress) {
        database.userProgressDao().updateProgress(progress)
    }

    fun getCompletedMissionsCount(userId: Long): Flow<Int> =
        database.userProgressDao().getCompletedMissionsCount(userId)

    fun getTotalScore(userId: Long): Flow<Int> =
        database.userProgressDao().getTotalScore(userId)

    fun getRecentProgress(userId: Long, limit: Int = 10): Flow<List<UserProgress>> =
        database.userProgressDao().getRecentProgress(userId, limit)

    // Game logic
    suspend fun completeMission(userId: Long, missionId: Long, score: Int) {
        val mission = database.missionDao().getMissionById(missionId).first()
        mission?.let {
            // Update progress
            val progress = UserProgress(
                userId = userId,
                missionId = missionId,
                isCompleted = true,
                score = score,
                attempts = 1,
                lastAttemptDate = System.currentTimeMillis()
            )
            database.userProgressDao().insertProgress(progress)

            // Add points
            database.userDao().addPoints(userId, it.pointsReward)

            // Check for level up
            val user = database.userDao().getUserById(userId).first()
            user?.let { currentUser ->
                val newLevel = calculateNewLevel(currentUser.points + it.pointsReward)
                if (newLevel > currentUser.level) {
                    database.userDao().updateLevel(userId, newLevel)
                }
            }
        }
    }

    private fun calculateNewLevel(points: Int): Int {
        // Fórmula simples: cada 1000 pontos = 1 nível
        return (points / 1000) + 1
    }
} 