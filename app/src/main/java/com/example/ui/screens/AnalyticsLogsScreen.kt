package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AnalyticsPoint
import com.example.model.ArgusViewModel
import com.example.model.ModerationLog
import com.example.model.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsLogsScreen(viewModel: ArgusViewModel, groupId: String?) {
    val groups by viewModel.groups.collectAsState()
    val group = groups.find { id -> id.id == groupId } ?: groups.firstOrNull()
    val analyticsMap by viewModel.analytics.collectAsState()
    val digests by viewModel.digests.collectAsState()
    val logs by viewModel.logs.collectAsState()

    val currentGroupId = group?.id ?: "-100123456789"
    val points = analyticsMap[currentGroupId] ?: emptyList()
    val digest = digests[currentGroupId]
    val groupLogs = logs.filter { it.groupId == currentGroupId || group == null }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics & Moderation Logs") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.DASHBOARD) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Group selector dropdown / header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(group?.title ?: "All Groups", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("7-Day Message & Spam Analytics", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // 7-day Analytics Chart Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Weekly Activity Trend", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                LegendItem("Messages", MaterialTheme.colorScheme.primary)
                                LegendItem("Blocked", MaterialTheme.colorScheme.error)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Custom Canvas Chart
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val primaryColor = MaterialTheme.colorScheme.primary
                            val errorColor = MaterialTheme.colorScheme.error

                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val maxMsg = (points.maxOfOrNull { it.messageCount } ?: 1000).toFloat()
                                val widthStep = size.width / (points.size.coerceAtLeast(1))
                                val path = Path()

                                points.forEachIndexed { index, pt ->
                                    val x = index * widthStep + widthStep / 2
                                    val yMsg = size.height - (pt.messageCount / maxMsg) * (size.height - 20f)

                                    if (index == 0) path.moveTo(x, yMsg) else path.lineTo(x, yMsg)

                                    // Draw dot for message count
                                    drawCircle(primaryColor, radius = 5f, center = Offset(x, yMsg))
                                }

                                drawPath(
                                    path = path,
                                    color = primaryColor,
                                    style = Stroke(width = 4f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            points.forEach { pt ->
                                Text(pt.dayName, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // Daily Digest /summary Section
            if (digest != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("AI Daily Digest (/summary)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(digest.summaryText, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Mood: ${digest.mood}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                                Text("Top: ${digest.topParticipants.joinToString(", ")}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // Moderation Logs Header
            item {
                Text("Recent Moderation Incidents", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            items(groupLogs) { log ->
                LogItemCard(log)
            }
        }
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun LogItemCard(log: ModerationLog) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val icon = when (log.actionType) {
                        "BLOCK_PHISHING" -> Icons.Default.Security
                        "DELETE_SPAM" -> Icons.Default.DeleteSweep
                        "RATE_LIMIT" -> Icons.Default.Timer
                        else -> Icons.Default.Warning
                    }
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(log.actionType, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Text(log.timestamp, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("Sender: ${log.senderUsername}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            Text(log.details, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
