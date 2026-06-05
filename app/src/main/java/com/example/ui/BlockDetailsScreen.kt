package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.data.Task

@Composable
fun BlockDetailsScreen(viewModel: ScheduleViewModel, blockId: Int, onBack: () -> Unit) {
    val blockWithTasks by viewModel.getTimeBlock(blockId).collectAsState(initial = null)
    var showAddTaskDialog by remember { mutableStateOf(false) }

    if (blockWithTasks == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { 
            CircularProgressIndicator() 
        }
        return
    }

    val block = blockWithTasks!!.timeBlock
    val tasks = blockWithTasks!!.tasks
    
    val completedCount = tasks.count { it.isCompleted }

    Scaffold(
        topBar = { TopBar("Detalle de Bloque", onBack = onBack) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp),
            contentPadding = PaddingValues(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(0.5f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.width(6.dp).height(120.dp).background(MaterialTheme.colorScheme.primary))
                        
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(
                                text = "${block.startTime} • ${block.durationMinutes} min",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = block.title,
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("$completedCount de ${tasks.size} tareas completadas", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Tareas", style = MaterialTheme.typography.headlineMedium)
                    TextButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.FilterList, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Filtrar")
                    }
                }
            }

            items(tasks) { task ->
                TaskRow(
                    task = task,
                    onToggle = { isChecked -> viewModel.toggleTask(task.id, isChecked) }
                )
            }

            item {
                Button(
                    onClick = { showAddTaskDialog = true },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Añadir Tarea", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }

    if (showAddTaskDialog) {
        var taskTitle by remember { mutableStateOf("") }
        var isHighPriority by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddTaskDialog = false },
            title = { Text("Nueva Tarea") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = taskTitle,
                        onValueChange = { taskTitle = it },
                        label = { Text("Descripción") },
                        singleLine = true
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isHighPriority, onCheckedChange = { isHighPriority = it })
                        Text("Alta prioridad")
                    }
                }
            },
            confirmButton = {
                Button(onClick = { 
                    viewModel.addTask(blockId, taskTitle, isHighPriority)
                    showAddTaskDialog = false
                }) {
                    Text("Agregar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTaskDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun TaskRow(task: Task, onToggle: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(0.3f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (task.isCompleted) MaterialTheme.colorScheme.secondary else Color.Transparent)
                    .border(2.dp, if (task.isCompleted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outlineVariant, CircleShape)
            ) {
                // To keep it simple, checking status through click on entire row is better, but this visuals works:
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggle(it) },
                    modifier = Modifier.fillMaxSize(),
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.Transparent, 
                        checkmarkColor = MaterialTheme.colorScheme.onSecondary,
                        uncheckedColor = Color.Transparent
                    )
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                if (task.isCompleted) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp)) {
                        Text("Completado", modifier = Modifier.padding(horizontal=6.dp, vertical=2.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    }
                } else if (task.isHighPriority) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PriorityHigh, null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(14.dp))
                        Text("Alta prioridad", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
                    }
                }
            }
            Icon(Icons.Default.DragIndicator, null, tint = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}
