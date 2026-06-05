package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun StatsScreen(viewModel: ScheduleViewModel) {
    Scaffold(
        topBar = { TopBar("Tempo", showProfile = true) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Column {
                    Text("Resumen de Desempeño", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onSurface)
                    Text("Tu ritmo de trabajo se mantiene constante esta semana.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            item {
                RachaCard()
            }

            item {
                DistribucionTiempoCard()
            }

            item {
                TareasFrecuentesCard()
            }

            item {
                AnalisisProductividadCard()
            }
            
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun RachaCard() {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Racha de Cumplimiento", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Icon(Icons.Default.LocalFireDepartment, null, tint = MaterialTheme.colorScheme.secondary)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text("12", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.width(8.dp))
                Text("días seguidos", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                repeat(6) {
                    Box(modifier = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)).background(MaterialTheme.colorScheme.secondary))
                }
                Box(modifier = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)).background(MaterialTheme.colorScheme.outlineVariant))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("¡Superaste tu récord anterior!", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
fun DistribucionTiempoCard() {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxWidth()) {
            Text("Distribución del Tiempo", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.size(160.dp), contentAlignment = Alignment.Center) {
                    val colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.surfaceVariant)
                    val sweeps = listOf(180f, 90f, 54f, 36f)
                    val trackColor = MaterialTheme.colorScheme.surfaceContainerLow
                    
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawArc(
                            color = trackColor,
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 16.dp.toPx())
                        )
                        var currentAngle = -90f
                        sweeps.forEachIndexed { i, sweep ->
                            drawArc(
                                color = colors[i],
                                startAngle = currentAngle,
                                sweepAngle = sweep,
                                useCenter = false,
                                style = Stroke(width = 16.dp.toPx())
                            )
                            currentAngle += sweep
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("42h", style = MaterialTheme.typography.headlineMedium)
                        Text("Total", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LegendRow("Trabajo", "21.0h (50%)", MaterialTheme.colorScheme.primary)
                LegendRow("Salud", "10.5h (25%)", MaterialTheme.colorScheme.secondary)
                LegendRow("Personal", "6.3h (15%)", MaterialTheme.colorScheme.tertiary)
                LegendRow("Otros", "4.2h (10%)", MaterialTheme.colorScheme.surfaceVariant)
            }
        }
    }
}

@Composable
fun LegendRow(label: String, value: String, color: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(color))
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.bodyLarge)
        }
        Text(value, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun TareasFrecuentesCard() {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Tareas Más Frecuentes", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TaskFreqRow(Icons.Default.Terminal, MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer, "Revisión de Código", "Proyecto Alpha • 8 veces", "+15%", MaterialTheme.colorScheme.primary)
                TaskFreqRow(Icons.Default.FitnessCenter, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer, "Sesión de Gimnasio", "Rutina A • 5 veces", "0%", MaterialTheme.colorScheme.secondary)
            }
        }
    }
}

@Composable
fun TaskFreqRow(icon: ImageVector, bg: Color, fg: Color, title: String, subtitle: String, perc: String, percColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(0.3f), RoundedCornerShape(8.dp)).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(bg), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = fg)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(perc, style = MaterialTheme.typography.labelLarge, color = percColor)
            Text("vs sem. ant.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun AnalisisProductividadCard() {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Icon(
                Icons.Default.AutoAwesome, 
                contentDescription = null, 
                modifier = Modifier.size(120.dp).align(Alignment.BottomEnd).offset(x = 30.dp, y = 30.dp),
                tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f)
            )
            
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Análisis de Productividad", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Tus horas más productivas son entre las 9:00 y las 11:00 AM. Lograste completar el 92% de las tareas críticas durante esta ventana.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary.copy(0.9f))
                
                Spacer(modifier = Modifier.height(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.onPrimary.copy(0.2f), RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.onPrimary.copy(0.1f)).padding(horizontal = 16.dp, vertical=8.dp)) {
                        Column {
                            Text("EFICIENCIA", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary.copy(0.8f))
                            Text("94.2%", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimary, contentColor = MaterialTheme.colorScheme.primary)) {
                        Text("Ver Detalles")
                    }
                }
            }
        }
    }
}
