package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ArgusViewModel
import com.example.model.PlanStatus
import com.example.model.Screen
import com.example.model.TelegramGroup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: ArgusViewModel) {
    val groups by viewModel.groups.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = androidx.compose.ui.Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Security,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = androidx.compose.ui.Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = androidx.compose.ui.Modifier.width(12.dp))
                        Column {
                            Text("Argus Guard", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Telegram Group Defense", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.ACCOUNT_LINK) },
                        modifier = Modifier.testTag("account_link_button")
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Account Link")
                    }
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.BOT_SETUP) },
                        modifier = Modifier.testTag("bot_setup_button")
                    ) {
                        Icon(Icons.Default.HelpOutline, contentDescription = "Bot Setup")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                    label = { Text("Groups") },
                    selected = true,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Star, contentDescription = null) },
                    label = { Text("Billing") },
                    selected = false,
                    onClick = { viewModel.navigateTo(Screen.BILLING) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Setup") },
                    selected = false,
                    onClick = { viewModel.navigateTo(Screen.BOT_SETUP) }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("add_group_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Telegram Group", tint = MaterialTheme.colorScheme.onPrimary)
            }
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
            // Status banner card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Protected Groups Overview", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                Text("${groups.size} Active", color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            StatItem("Total Protected", "${groups.sumOf { it.memberCount }} users")
                            StatItem("Blocked Today", "37 spam")
                            StatItem("AI Shield", "${groups.count { it.aiScamFilterEnabled }} Pro groups")
                        }
                    }
                }
            }

            item {
                Text(
                    "Your Telegram Groups",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(groups) { group ->
                GroupCard(
                    group = group,
                    onClick = { viewModel.navigateTo(Screen.GROUP_DETAIL, group.id) },
                    onBillingClick = { viewModel.navigateTo(Screen.BILLING, group.id) },
                    onAnalyticsClick = { viewModel.navigateTo(Screen.ANALYTICS_LOGS, group.id) }
                )
            }
        }
    }

    if (showAddDialog) {
        AddGroupDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { title, username ->
                viewModel.addGroup(title, username)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun GroupCard(
    group: TelegramGroup,
    onClick: () -> Unit,
    onBillingClick: () -> Unit,
    onAnalyticsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("group_card_${group.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Group,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(group.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(group.username, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }

                // Plan Status Badge
                val (badgeColor, badgeText) = when (group.planStatus) {
                    PlanStatus.PRO_ACTIVE -> Pair(MaterialTheme.colorScheme.primary, "PRO ACTIVE")
                    PlanStatus.TRIAL -> Pair(MaterialTheme.colorScheme.tertiary, "TRIAL (${group.trialDaysLeft}d)")
                    PlanStatus.EXPIRED -> Pair(MaterialTheme.colorScheme.error, "EXPIRED")
                }
                Surface(
                    color = badgeColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = badgeColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Members: ${group.memberCount}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Blacklist: ${group.blacklistWords.size} terms", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (group.aiScamFilterEnabled) {
                        Text("AI Scam Shield", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onAnalyticsClick) {
                    Icon(Icons.Default.BarChart, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Analytics & Logs", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onBillingClick,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Stars Pro", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun AddGroupDialog(onDismiss: () -> Unit, onAdd: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Register Telegram Group") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Add Argus Guard bot (@ArgusGuardBot) to your Telegram group and grant admin permissions (delete messages, restrict users). Enter group details below:")
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Group Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("group_title_input")
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Telegram Username / Invite (@group)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("group_username_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank()) onAdd(title, username.ifBlank { "@group" }) },
                modifier = Modifier.testTag("confirm_add_group")
            ) {
                Text("Register Group")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
