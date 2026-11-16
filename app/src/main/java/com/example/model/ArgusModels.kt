package com.example.model

data class TelegramGroup(
    val id: String,
    val title: String,
    val username: String,
    val ownerName: String,
    val planStatus: PlanStatus,
    val trialDaysLeft: Int = 7,
    val memberCount: Int = 240,
    val aiScamFilterEnabled: Boolean = false,
    val rateLimitMessages: Int = 5,
    val rateLimitSeconds: Int = 5,
    val blacklistWords: MutableList<String> = mutableListOf("scam", "airdrop", "free nitro", "dm me", "crypto signals"),
    val keywordAlerts: MutableList<String> = mutableListOf("refund", "support", "admin", "hack")
)

enum class PlanStatus {
    TRIAL,
    PRO_ACTIVE,
    EXPIRED
}

data class ModerationLog(
    val id: String,
    val groupId: String,
    val groupTitle: String,
    val actionType: String, // "DELETE_SPAM", "BLOCK_PHISHING", "RATE_LIMIT", "KEYWORD_MATCH"
    val senderUsername: String,
    val timestamp: String,
    val details: String
)

data class AnalyticsPoint(
    val dayName: String,
    val messageCount: Int,
    val activeUsers: Int,
    val blockedCount: Int
)

data class DailyDigest(
    val groupId: String,
    val groupTitle: String,
    val date: String,
    val summaryText: String,
    val topParticipants: List<String>,
    val mood: String
)
