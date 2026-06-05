package com.example.data

import kotlinx.coroutines.flow.Flow

class ScheduleRepository(private val scheduleDao: ScheduleDao) {
    fun getTimeBlocksByDate(date: String): Flow<List<TimeBlockWithTasks>> =
        scheduleDao.getTimeBlocksWithTasksByDate(date)

    fun getTimeBlock(blockId: Int): Flow<TimeBlockWithTasks?> =
        scheduleDao.getTimeBlockWithTasks(blockId)
        
    fun getAllTimeBlocksWithTasks(): Flow<List<TimeBlockWithTasks>> = 
        scheduleDao.getAllTimeBlocksWithTasks()

    val allTasks: Flow<List<Task>> = scheduleDao.getAllTasks()
    val allTimeBlocks: Flow<List<TimeBlock>> = scheduleDao.getAllTimeBlocks()

    suspend fun insertTimeBlock(timeBlock: TimeBlock) {
        scheduleDao.insertTimeBlock(timeBlock)
    }

    suspend fun insertTask(task: Task) {
        scheduleDao.insertTask(task)
    }

    suspend fun toggleTaskCompletion(taskId: Int, isCompleted: Boolean) {
        scheduleDao.updateTaskStatus(taskId, isCompleted)
    }

    suspend fun deleteTimeBlock(timeBlock: TimeBlock) {
        scheduleDao.deleteTimeBlock(timeBlock)
    }

    suspend fun deleteTask(task: Task) {
        scheduleDao.deleteTask(task)
    }
}
