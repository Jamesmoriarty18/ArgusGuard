package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ArgusViewModel
import com.example.model.PlanStatus
import com.example.model.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingScreen(viewModel: ArgusViewModel, groupId: String?) {
    val groups by viewModel.groups.collectAsState()
    val group = groups.find { it.id == groupId } ?: groups.firstOrNull()

    var showSuccessDialog by remember { mutableStateOf(false) }

    if (group == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Select a group to manage billing")
        }
        return
    }

    val isPro = group.planStatus == PlanStatus.PRO_ACTIVE

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Telegram Stars Subscription") },
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
            // Group Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Group: ${group.title}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (group.planStatus) {
                                PlanStatus.PRO_ACTIVE -> "Status: Pro Active (Renews automatically via Telegram Stars)"
                                PlanStatus.TRIAL -> "Status: Trial (${group.trialDaysLeft} days remaining)"
                                PlanStatus.EXPIRED -> "Status: Expired (Basic moderation mode only)"
                            },
                            fontSize = 13.sp,
                            color = if (isPro) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Telegram Stars Pricing Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Argus Guard Pro", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "${viewModel.telegramStarsPrice} XTR / month",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Approximately ${viewModel.approximateUsdPrice} / month (configurable by operator)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Features list
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FeatureRow("Unlimited Blacklist Terms (vs 5 on free trial)")
                            FeatureRow("AI Scam & Phishing Detection (OpenAI / Gemini)")
                            FeatureRow("Priority Telegram Stars Recurring Invoicing")
                            FeatureRow("Automated Daily Digests & Advanced Analytics")
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        if (!isPro) {
                            Button(
                                onClick = {
                                    viewModel.subscribeGroupWithStars(group.id)
                                    showSuccessDialog = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("subscribe_stars_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Subscribe with ${viewModel.telegramStarsPrice} Telegram Stars", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    viewModel.cancelSubscription(group.id)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("cancel_subscription_button"),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Cancel Pro Subscription")
                            }
                        }
                    }
                }
            }

            // Instructions on Telegram Stars
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("About Telegram Stars Billing", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Telegram Stars (XTR) is Telegram's native digital currency for bot services and mini apps. Subscriptions renew automatically every 30 days. You can cancel at any time directly from the dashboard or bot chat.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Pro Subscription Activated!") },
            text = { Text("Payment of ${viewModel.telegramStarsPrice} Telegram Stars verified successfully. Pro features, unlimited blacklist terms, and AI scam filters are now active for ${group.title}.") },
            confirmButton = {
                Button(onClick = {
                    showSuccessDialog = false
                    viewModel.navigateTo(Screen.DASHBOARD)
                }) {
                    Text("Return to Dashboard")
                }
            }
        )
    }
}

@Composable
fun FeatureRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}
