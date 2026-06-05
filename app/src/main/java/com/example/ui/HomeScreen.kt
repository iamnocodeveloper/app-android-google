package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.TimeBlockWithTasks
import androidx.compose.foundation.clickable

@Composable
fun HomeScreen(viewModel: ScheduleViewModel, navController: NavController) {
    val blocks by viewModel.todayBlocks.collectAsState()
    
    val totalTasks = blocks.flatMap { it.tasks }.size
    val completedTasks = blocks.flatMap { it.tasks }.count { it.isCompleted }
    val progress = if (totalTasks > 0) completedTasks.toFloat() / totalTasks else 0f
    val percentage = (progress * 100).toInt()
    
    val nextBlock = blocks.firstOrNull { 
        // Naive logic: just grab the first one that has pending tasks or whatever is first
        // We'll just grab the first one
        it.tasks.any { !it.isCompleted }
    } ?: blocks.firstOrNull()

    Scaffold(
        topBar = { TopBar("Tempo", showProfile = true) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("schedule") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, "Agregar bloque")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            
            // Resumen del Día
            item {
                ResumenDelDiaCard(percentage, completedTasks, totalTasks - completedTasks)
            }
            
            // Próximo Bloque
            item {
                ProximoBloqueCard(nextBlock, navController)
            }
            
            // Productividad Semanal (Fake data for visual)
            item {
                ProductividadSemanalCard()
            }
            
            // Áreas de Enfoque
            item {
                AreasDeEnfoqueGrid(blocks)
            }
            
            item { Spacer(modifier = Modifier.height(88.dp)) } // Bottom padding for FAB
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String, showProfile: Boolean = false, onBack: (() -> Unit)? = null) {
    TopAppBar(
        title = { Text(title, style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Atrás", tint = MaterialTheme.colorScheme.primary)
                }
            } else {
                IconButton(onClick = {  }) {
                    Icon(Icons.Default.Menu, "Menú", tint = MaterialTheme.colorScheme.primary)
                }
            }
        },
        actions = {
            if (showProfile) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Default.Notifications, "Notificaciones", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .border(2.dp, MaterialTheme.colorScheme.primaryContainer, CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}

@Composable
fun ResumenDelDiaCard(percentage: Int, completed: Int, pending: Int) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular Progress
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                val primaryColor = MaterialTheme.colorScheme.secondary
                val trackColor = MaterialTheme.colorScheme.surfaceContainer
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = trackColor,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = primaryColor,
                        startAngle = -90f,
                        sweepAngle = 360f * (percentage / 100f),
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$percentage%", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
                    Text("Completado", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            
            Spacer(modifier = Modifier.width(24.dp))
            
            Column {
                Text("Resumen del Día", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
                Text("Vas por buen camino. Te quedan $pending tareas por finalizar.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(color = MaterialTheme.colorScheme.surfaceContainerLow, shape = RoundedCornerShape(8.dp)) {
                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, "Hechas", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("$completed Hechas", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                    Surface(color = MaterialTheme.colorScheme.surfaceContainerLow, shape = RoundedCornerShape(8.dp)) {
                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, "Pendientes", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("$pending Pendientes", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProximoBloqueCard(nextBlock: TimeBlockWithTasks?, navController: NavController) {
    if (nextBlock == null) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        ) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth()) {
                Text("Crea un bloque para comenzar tu día", color = MaterialTheme.colorScheme.onPrimaryContainer, style = MaterialTheme.typography.headlineSmall)
            }
        }
        return
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxWidth()) {
            Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Próximo Bloque",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(nextBlock.timeBlock.title, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Alarm, null, tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(nextBlock.timeBlock.startTime, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { navController.navigate("block_details/${nextBlock.timeBlock.id}") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimary, contentColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Ver Detalles")
            }
        }
    }
}

@Composable
fun ProductividadSemanalCard() {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Productividad Semanal", style = MaterialTheme.typography.headlineMedium)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { }) {
                    Text("Ver detalles", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
                    Icon(Icons.Default.ArrowForward, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            
            // Fake Bar Chart
            Row(
                modifier = Modifier.fillMaxWidth().height(150.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val heights = listOf(0.6f, 0.8f, 0.4f, 0.9f, 0.7f, 0.2f, 0.2f)
                val days = listOf("L", "M", "M", "J", "V", "S", "D")
                
                heights.forEachIndexed { index, height ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .fillMaxHeight(height)
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                .background(if (index == 3) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primaryContainer)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(days[index], style = MaterialTheme.typography.labelSmall, color = if(index == 3) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun AreasDeEnfoqueGrid(blocks: List<TimeBlockWithTasks>) {
    Column {
        Text("Áreas de Enfoque", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
            val weight = Modifier.weight(1f)
            AreaCard("Trabajo", Icons.Default.Work, MaterialTheme.colorScheme.primary, blocks.count { it.timeBlock.category == "Trabajo" }, weight)
            AreaCard("Salud", Icons.Default.FitnessCenter, MaterialTheme.colorScheme.secondary, blocks.count { it.timeBlock.category == "Salud" }, weight)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
            val weight = Modifier.weight(1f)
            AreaCard("Personal", Icons.Default.Person, MaterialTheme.colorScheme.tertiary, blocks.count { it.timeBlock.category == "Personal" }, weight)
            AreaCard("Otros", Icons.Default.Add, MaterialTheme.colorScheme.outline, blocks.count { it.timeBlock.category == "Otros" }, weight)
        }
    }
}

@Composable
fun AreaCard(title: String, icon: ImageVector, tint: Color, count: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(tint.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = tint)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(title, style = MaterialTheme.typography.labelLarge)
            Text(if (count > 0) "$count Bloques" else "Sin bloques", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
