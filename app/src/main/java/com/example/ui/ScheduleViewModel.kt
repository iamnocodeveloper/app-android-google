package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ScheduleViewModel(private val repository: ScheduleRepository) : ViewModel() {

    private val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val _currentDate = MutableStateFlow(df.format(Date()))
    val currentDate: StateFlow<String> = _currentDate.asStateFlow()

    // Dashboard & Stats Data
    val allTimeBlocksWithTasks: StateFlow<List<TimeBlockWithTasks>> = repository.getAllTimeBlocksWithTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    val todayBlocks: StateFlow<List<TimeBlockWithTasks>> = _currentDate
        .flatMapLatest { date -> repository.getTimeBlocksByDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    fun getTimeBlock(id: Int): Flow<TimeBlockWithTasks?> {
        return repository.getTimeBlock(id)
    }

    fun nextDay() {
        val c = Calendar.getInstance()
        c.time = df.parse(_currentDate.value)!!
        c.add(Calendar.DAY_OF_YEAR, 1)
        _currentDate.value = df.format(c.time)
    }

    fun previousDay() {
        val c = Calendar.getInstance()
        c.time = df.parse(_currentDate.value)!!
        c.add(Calendar.DAY_OF_YEAR, -1)
        _currentDate.value = df.format(c.time)
    }

    fun toggleTask(taskId: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(taskId, isCompleted)
        }
    }

    fun addTask(blockId: Int, title: String, isHighPriority: Boolean = false) {
        viewModelScope.launch {
            repository.insertTask(Task(blockId = blockId, title = title, isHighPriority = isHighPriority))
        }
    }

    fun addTimeBlock(title: String, startTime: String, durationMinutes: Int, category: String) {
        viewModelScope.launch {
            val date = _currentDate.value
            repository.insertTimeBlock(
                TimeBlock(
                    title = title,
                    startTime = startTime,
                    durationMinutes = durationMinutes,
                    category = category,
                    date = date
                )
            )
        }
    }
    
    // Seed dummy data for demo purposes if empty
    fun seedDataIfNeeded() {
        viewModelScope.launch {
            repository.getAllTimeBlocksWithTasks().firstOrNull()?.let { blocks ->
                if (blocks.isEmpty()) {
                    val date = df.format(Date())
                    repository.insertTimeBlock(TimeBlock(title = "Rutina Mañanera", startTime = "08:00", durationMinutes = 45, category = "Salud", date = date))
                    // Will let the user add the rest, or we could add more precise seeding if we had to wait for auto-generated IDs
                }
            }
        }
    }
}

class ScheduleViewModelFactory(private val repository: ScheduleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScheduleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScheduleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
