package com.example.data

import androidx.room.*

@Entity(tableName = "time_blocks")
data class TimeBlock(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val startTime: String, // HH:mm format
    val durationMinutes: Int,
    val category: String, // "Trabajo", "Salud", "Personal", "Otros"
    val date: String // YYYY-MM-DD format
)

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = TimeBlock::class,
            parentColumns = ["id"],
            childColumns = ["blockId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("blockId")]
)
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val blockId: Int,
    val title: String,
    val isCompleted: Boolean = false,
    val isHighPriority: Boolean = false
)

data class TimeBlockWithTasks(
    @Embedded val timeBlock: TimeBlock,
    @Relation(
        parentColumn = "id",
        entityColumn = "blockId"
    )
    val tasks: List<Task>
)
