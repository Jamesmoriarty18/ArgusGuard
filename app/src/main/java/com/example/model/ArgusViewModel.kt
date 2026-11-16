package com.example.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ArgusViewModel : ViewModel() {

    private val _groups = MutableStateFlow<List<TelegramGroup>>(
        listOf(
            TelegramGroup(
                id = "-100123456789",
                title = "Tech Innovators Chat",
                username = "@TechInnovatorsChat",
                ownerName = "Alex Developer",
                planStatus = PlanStatus.PRO_ACTIVE,
                trialDaysLeft = 0,
                memberCount = 1250,
                aiScamFilterEnabled = true,
                rateLimitMessages = 5,
                rateLimitSeconds = 5,
                blacklistWords = mutableListOf("crypto scam", "airdrop", "free nitro", "dm me", "binance giveaway"),
                keywordAlerts = mutableListOf("bug", "outage", "crash", "billing")
            ),
            TelegramGroup(
                id = "-100987654321",
                title = "Crypto Alpha Lounge",
                username = "@CryptoAlphaLounge",
                ownerName = "Alex Developer",
                planStatus = PlanStatus.TRIAL,
                trialDaysLeft = 5,
                memberCount = 890,
                aiScamFilterEnabled = false,
                rateLimitMessages = 5,
                rateLimitSeconds = 5,
                blacklistWords = mutableListOf("pump", "dump", "vip signal", "telegram channel"),
                keywordAlerts = mutableListOf("scam", "loss", "liquidated")
            )
        )
    )
    val groups: StateFlow<List<TelegramGroup>> = _groups.asStateFlow()

    private val _selectedGroupId = MutableStateFlow<String?>(null)
    val selectedGroupId: StateFlow<String?> = _selectedGroupId.asStateFlow()

    private val _currentScreen = MutableStateFlow(Screen.DASHBOARD)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _logs = MutableStateFlow<List<ModerationLog>>(
        listOf(
            ModerationLog(
                id = "log-1",
                groupId = "-100123456789",
                groupTitle = "Tech Innovators Chat",
                actionType = "BLOCK_PHISHING",
                senderUsername = "@fake_support_bot",
                timestamp = "10 mins ago",
                details = "AI detected phishing attempt impersonating admin."
            ),
            ModerationLog(
                id = "log-2",
                groupId = "-100123456789",
                groupTitle = "Tech Innovators Chat",
                actionType = "DELETE_SPAM",
                senderUsername = "@crypto_pump_king",
                timestamp = "25 mins ago",
                details = "Blacklist term 'airdrop' matched. Message deleted & user restricted."
            ),
            ModerationLog(
                id = "log-3",
                groupId = "-100987654321",
                groupTitle = "Crypto Alpha Lounge",
                actionType = "RATE_LIMIT",
                senderUsername = "@spammer99",
                timestamp = "1 hour ago",
                details = "Exceeded 5 messages in 5 seconds. Temporary mute applied."
            )
        )
    )
    val logs: StateFlow<List<ModerationLog>> = _logs.asStateFlow()

    private val _analytics = MutableStateFlow<Map<String, List<AnalyticsPoint>>>(
        mapOf(
            "-100123456789" to listOf(
                AnalyticsPoint("Mon", 340, 180, 12),
                AnalyticsPoint("Tue", 450, 210, 8),
                AnalyticsPoint("Wed", 520, 260, 15),
                AnalyticsPoint("Thu", 390, 190, 6),
                AnalyticsPoint("Fri", 610, 310, 22),
                AnalyticsPoint("Sat", 780, 420, 30),
                AnalyticsPoint("Sun", 550, 290, 14)
            ),
            "-100987654321" to listOf(
                AnalyticsPoint("Mon", 120, 75, 4),
                AnalyticsPoint("Tue", 150, 90, 7),
                AnalyticsPoint("Wed", 140, 85, 5),
                AnalyticsPoint("Thu", 180, 110, 9),
                AnalyticsPoint("Fri", 210, 130, 12),
                AnalyticsPoint("Sat", 250, 160, 18),
                AnalyticsPoint("Sun", 190, 115, 8)
            )
        )
    )
    val analytics: StateFlow<Map<String, List<AnalyticsPoint>>> = _analytics.asStateFlow()

    private val _digests = MutableStateFlow<Map<String, DailyDigest>>(
        mapOf(
            "-100123456789" to DailyDigest(
                groupId = "-100123456789",
                groupTitle = "Tech Innovators Chat",
                date = "Today, Nov 16",
                summaryText = "Discussion focused on API v2 release, Kotlin coroutines performance, and database indexing strategies. Overall positive and collaborative tone with 3 spam incidents successfully neutralized.",
                topParticipants = listOf("@alex_dev", "@sarah_code", "@dev_guru"),
                mood = "Highly Constructive (Positive)"
            ),
            "-100987654321" to DailyDigest(
                groupId = "-100987654321",
                groupTitle = "Crypto Alpha Lounge",
                date = "Today, Nov 16",
                summaryText = "Members discussed layer-2 scaling solutions and market trends. 4 scam links offering fake airdrops were intercepted and banned automatically by Argus Guard AI filter.",
                topParticipants = listOf("@trader_joe", "@blockchain_pro", "@satoshi_fan"),
                mood = "Active & Vigilant"
            )
        )
    )
    val digests: StateFlow<Map<String, DailyDigest>> = _digests.asStateFlow()

    // Telegram Stars configuration (e.g. 2500 Stars ~ US$50/mo)
    val telegramStarsPrice: Int = 2500
    val approximateUsdPrice: String = "US$50.00"

    fun navigateTo(screen: Screen, groupId: String? = null) {
        if (groupId != null) {
            _selectedGroupId.value = groupId
        }
        _currentScreen.value = screen
    }

    fun addGroup(title: String, username: String) {
        val newId = "-100" + (100000..999999).random()
        val newGroup = TelegramGroup(
            id = newId,
            title = title,
            username = if (username.startsWith("@")) username else "@$username",
            ownerName = "Alex Developer",
            planStatus = PlanStatus.TRIAL,
            trialDaysLeft = 7,
            memberCount = 50
        )
        _groups.update { current -> current + newGroup }
        _analytics.update { current ->
            current + (newId to listOf(
                AnalyticsPoint("Mon", 10, 5, 1),
                AnalyticsPoint("Tue", 15, 8, 2),
                AnalyticsPoint("Wed", 20, 10, 1),
                AnalyticsPoint("Thu", 25, 12, 3),
                AnalyticsPoint("Fri", 30, 15, 2),
                AnalyticsPoint("Sat", 40, 20, 4),
                AnalyticsPoint("Sun", 35, 18, 2)
            ))
        }
        _digests.update { current ->
            current + (newId to DailyDigest(
                groupId = newId,
                groupTitle = title,
                date = "Today",
                summaryText = "Group newly registered with Argus Guard. Trial period active for 7 days.",
                topParticipants = listOf("@owner"),
                mood = "Fresh & Clean"
            ))
        }
    }

    fun updateGroupSettings(groupId: String, aiEnabled: Boolean, rateLimit: Int) {
        _groups.update { list ->
            list.map { g ->
                if (g.id == groupId) {
                    g.copy(aiScamFilterEnabled = aiEnabled, rateLimitMessages = rateLimit)
                } else g
            }
        }
    }

    fun addBlacklistWord(groupId: String, word: String) {
        if (word.isBlank()) return
        _groups.update { list ->
            list.map { g ->
                if (g.id == groupId) {
                    if (!g.blacklistWords.contains(word.trim())) {
                        g.blacklistWords.add(word.trim().lowercase())
                    }
                }
                g
            }
        }
    }

    fun removeBlacklistWord(groupId: String, word: String) {
        _groups.update { list ->
            list.map { g ->
                if (g.id == groupId) {
                    g.blacklistWords.remove(word)
                }
                g
            }
        }
    }

    fun subscribeGroupWithStars(groupId: String) {
        _groups.update { list ->
            list.map { g ->
                if (g.id == groupId) {
                    g.copy(planStatus = PlanStatus.PRO_ACTIVE, trialDaysLeft = 0)
                } else g
            }
        }
        // Add log entry
        val group = _groups.value.find { it.id == groupId }
        val title = group?.title ?: "Group"
        val newLog = ModerationLog(
            id = "sub-${System.currentTimeMillis()}",
            groupId = groupId,
            groupTitle = title,
            actionType = "STARS_SUBSCRIPTION",
            senderUsername = "@owner",
            timestamp = "Just now",
            details = "Successfully subscribed with $telegramStarsPrice Telegram Stars (~$approximateUsdPrice/mo)."
        )
        _logs.update { listOf(newLog) + it }
    }

    fun cancelSubscription(groupId: String) {
        _groups.update { list ->
            list.map { g ->
                if (g.id == groupId) {
                    g.copy(planStatus = PlanStatus.EXPIRED)
                } else g
            }
        }
    }
}

enum class Screen {
    DASHBOARD,
    GROUP_DETAIL,
    ANALYTICS_LOGS,
    BILLING,
    BOT_SETUP,
    ACCOUNT_LINK
}
