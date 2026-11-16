package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.ArgusViewModel
import com.example.model.Screen
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: ArgusViewModel = viewModel()
                    val currentScreen by viewModel.currentScreen.collectAsState()
                    val selectedGroupId by viewModel.selectedGroupId.collectAsState()

                    // Handle system back button press when not on dashboard
                    if (currentScreen != Screen.DASHBOARD) {
                        BackHandler {
                            viewModel.navigateTo(Screen.DASHBOARD)
                        }
                    }

                    when (currentScreen) {
                        Screen.DASHBOARD -> DashboardScreen(viewModel)
                        Screen.GROUP_DETAIL -> GroupDetailScreen(viewModel, selectedGroupId)
                        Screen.ANALYTICS_LOGS -> AnalyticsLogsScreen(viewModel, selectedGroupId)
                        Screen.BILLING -> BillingScreen(viewModel, selectedGroupId)
                        Screen.BOT_SETUP -> BotSetupScreen(viewModel)
                        Screen.ACCOUNT_LINK -> AccountLinkScreen(viewModel)
                    }
                }
            }
        }
    }
}
