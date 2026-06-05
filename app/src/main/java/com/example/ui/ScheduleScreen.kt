package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.data.TimeBlockWithTasks

@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel, navController: NavController) {
    val blocks by viewModel.todayBlocks.collectAsState()
    val currentDate by viewModel.currentDate.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopBar("Tempo", showProfile = true) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, "Agregar")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text("HOY", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(currentDate, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { viewModel.previousDay() },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Default.ChevronLeft, null, tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(
                        onClick = { viewModel.nextDay() },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(horizontal = 24.dp))
            
            LazyColumn(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (blocks.isEmpty()) {
                    item {
                        Text(
                            "Sin bloques para hoy.",
                            modifier = Modifier.padding(24.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                items(blocks) { blockWithTasks ->
                    TimeBlockItem(blockWithTasks) {
                        navController.navigate("block_details/${blockWithTasks.timeBlock.id}")
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
    
    if (showAddDialog) {
        AddTimeBlockDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { title, start, duration, category ->
                viewModel.addTimeBlock(title, start, duration, category)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun TimeBlockItem(blockWithTasks: TimeBlockWithTasks, onClick: () -> Unit) {
    val block = blockWithTasks.timeBlock
    val tasks = blockWithTasks.tasks
    
    val completedCount = tasks.count { it.isCompleted }
    val totalCount = tasks.size
    
    val color = when (block.category) {
        "Trabajo" -> MaterialTheme.colorScheme.primary
        "Salud" -> MaterialTheme.colorScheme.secondary
        "Personal" -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.surfaceTint
    }
    
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), verticalAlignment = Alignment.Top) {
        Text(
            text = block.startTime,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.width(60.dp).padding(top = 16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        
        Card(
            modifier = Modifier.weight(1f).border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha=0.5f), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.width(4.dp).height(40.dp).clip(RoundedCornerShape(8.dp)).background(color))
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(block.title, style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${block.durationMinutes} min", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    if (totalCount > 0 && completedCount == totalCount) {
                        Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(8.dp)) {
                            Text("COMPLETADO", modifier = Modifier.padding(horizontal=8.dp, vertical=2.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$completedCount/$totalCount tareas", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

@Composable
fun AddTimeBlockDialog(onDismiss: () -> Unit, onAdd: (String, String, Int, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("09:00") }
    var duration by remember { mutableStateOf("60") }
    var category by remember { mutableStateOf("Trabajo") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Bloque") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, singleLine = true)
                OutlinedTextField(value = startTime, onValueChange = { startTime = it }, label = { Text("Hora Inicio (HH:mm)") }, singleLine = true)
                OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Duración (minutos)") }, singleLine = true)
                // In a real app we'd use a Dropdown. Here simpler text for layout constraints.
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Categoría (Trabajo, Salud...)") }, singleLine = true)
            }
        },
        confirmButton = {
            Button(onClick = { onAdd(title, startTime, duration.toIntOrNull() ?: 60, category) }) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
