package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ArgusViewModel
import com.example.model.PlanStatus
import com.example.model.Screen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GroupDetailScreen(viewModel: ArgusViewModel, groupId: String?) {
    val groups by viewModel.groups.collectAsState()
    val group = groups.find { it.id == groupId } ?: groups.firstOrNull()

    var newWord by remember { mutableStateOf("") }
    var showLimitDialog by remember { mutableStateOf(false) }

    if (group == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Group not found")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(group.title) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.DASHBOARD) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.BILLING, group.id) }) {
                        Icon(Icons.Default.Star, contentDescription = "Billing")
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
            // Status & Info Card
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
                            Column {
                                Text(group.username, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Owner: ${group.ownerName}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            val isPro = group.planStatus == PlanStatus.PRO_ACTIVE
                            Badge(containerColor = if (isPro) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary) {
                                Text(if (isPro) "PRO ACTIVE" else "TRIAL (${group.trialDaysLeft}d left)", color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }
                }
            }

            // Pro AI Scam Filter
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SmartToy, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("AI Scam & Phishing Detection", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Uses OpenAI / AI model to analyze incoming messages for malicious links, crypto scams, and fake admin impersonation.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = group.aiScamFilterEnabled,
                            onCheckedChange = { enabled ->
                                if (group.planStatus == PlanStatus.EXPIRED && enabled) {
                                    // Pro required
                                    viewModel.navigateTo(Screen.BILLING, group.id)
                                } else {
                                    viewModel.updateGroupSettings(group.id, enabled, group.rateLimitMessages)
                                }
                            },
                            modifier = Modifier.testTag("ai_filter_switch")
                        )
                    }
                }
            }

            // Rate Limiting Config
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Rate Limiting (Anti-Spam)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Max messages per user in 5 seconds window before temporary restriction: ${group.rateLimitMessages} messages",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Slider(
                            value = group.rateLimitMessages.toFloat(),
                            onValueChange = { value ->
                                viewModel.updateGroupSettings(group.id, group.aiScamFilterEnabled, value.toInt())
                            },
                            valueRange = 2f..20f,
                            steps = 18,
                            modifier = Modifier.testTag("rate_limit_slider")
                        )
                    }
                }
            }

            // Blacklist Manager
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Block, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Blacklist Terms", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                            val isTrial = group.planStatus != PlanStatus.PRO_ACTIVE
                            val maxAllowed = if (isTrial) 5 else 50
                            Text("${group.blacklistWords.size} / $maxAllowed", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Messages containing these terms (case-insensitive) are automatically deleted and senders restricted.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = newWord,
                                onValueChange = { newWord = it },
                                label = { Text("Add forbidden term") },
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("blacklist_input")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val isTrial = group.planStatus != PlanStatus.PRO_ACTIVE
                                    if (isTrial && group.blacklistWords.size >= 5) {
                                        showLimitDialog = true
                                    } else {
                                        viewModel.addBlacklistWord(group.id, newWord)
                                        newWord = ""
                                    }
                                },
                                modifier = Modifier.testTag("add_blacklist_button")
                            ) {
                                Text("Add")
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Chips for blacklist words
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            group.blacklistWords.forEach { word ->
                                InputChip(
                                    selected = false,
                                    onClick = { viewModel.removeBlacklistWord(group.id, word) },
                                    label = { Text(word) },
                                    trailingIcon = { Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(16.dp)) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showLimitDialog) {
        AlertDialog(
            onDismissRequest = { showLimitDialog = false },
            title = { Text("Trial Blacklist Limit Reached") },
            text = { Text("Free and trial groups are limited to 5 blacklist terms. Upgrade to Pro with Telegram Stars to unlock unlimited blacklist terms, AI scam detection, and priority moderation!") },
            confirmButton = {
                Button(
                    onClick = {
                        showLimitDialog = false
                        viewModel.navigateTo(Screen.BILLING, group.id)
                    }
                ) {
                    Text("Upgrade to Pro")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLimitDialog = false }) {
                    Text("Dismiss")
                }
            }
        )
    }
}
