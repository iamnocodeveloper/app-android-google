package com.example

import android.app.Application
import com.example.data.AppDatabase
import com.example.data.ScheduleRepository

class TempoApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { ScheduleRepository(database.scheduleDao()) }
}
