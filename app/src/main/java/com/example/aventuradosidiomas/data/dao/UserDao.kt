package com.example.aventuradosidiomas.data.dao

import androidx.room.*
import com.example.aventuradosidiomas.data.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: Long): Flow<User?>

    @Query("SELECT * FROM users LIMIT 1")
    fun getCurrentUser(): Flow<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET points = points + :points WHERE id = :userId")
    suspend fun addPoints(userId: Long, points: Int)

    @Query("UPDATE users SET level = :newLevel WHERE id = :userId")
    suspend fun updateLevel(userId: Long, newLevel: Int)
} 