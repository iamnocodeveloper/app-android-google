package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM time_blocks WHERE date = :date ORDER BY startTime ASC")
    fun getTimeBlocksWithTasksByDate(date: String): Flow<List<TimeBlockWithTasks>>

    @Query("SELECT * FROM time_blocks WHERE id = :blockId")
    fun getTimeBlockWithTasks(blockId: Int): Flow<TimeBlockWithTasks?>

    @Query("SELECT * FROM time_blocks ORDER BY date DESC, startTime ASC")
    fun getAllTimeBlocksWithTasks(): Flow<List<TimeBlockWithTasks>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimeBlock(timeBlock: TimeBlock): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: Int, isCompleted: Boolean)

    @Delete
    suspend fun deleteTimeBlock(timeBlock: TimeBlock)

    @Delete
    suspend fun deleteTask(task: Task)
    
    // For Statistics
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<Task>>
    
    @Query("SELECT * FROM time_blocks")
    fun getAllTimeBlocks(): Flow<List<TimeBlock>>
}
