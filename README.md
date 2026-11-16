# Argus Guard — Telegram Group Moderation Bot & Dashboard

Argus Guard is a robust Telegram group moderation bot with a companion Android Dashboard and monthly subscriptions paid using Telegram Stars (`XTR`).

---

## Architecture & Project Structure
- **Android App (`/app`)**: Kotlin & Jetpack Compose mobile dashboard and admin control center for managing groups, blacklist terms, rate limits, Pro AI scam detection, 7-day analytics charts, moderation logs, and Telegram Stars subscriptions.
- **Telegram Webhook Bot (`bot_service.py`)**: Python 3.11+ & FastAPI asynchronous webhook service handling real-time message moderation, rate-limiting, blacklist filtering, `/summary` daily digests, and Telegram Stars (`XTR`) recurring payment updates.

---

## Local & Replit Deployment Guide

### 1. BotFather Setup
1. Open Telegram and message **@BotFather**.
2. Send `/newbot` and follow the prompts to create your bot.
3. Copy your API token (`TELEGRAM_BOT_TOKEN`).

### 2. Environment Variables (`.env`)
Configure the following environment variables on your server or Replit:
```env
TELEGRAM_BOT_TOKEN=your_telegram_bot_token_here
TELEGRAM_STARS_PRICE=2500
OPENAI_API_KEY=your_openai_api_key_optional
APP_BASE_URL=https://your-deployed-app.replit.app
PORT=5000
```

### 3. Deploying & Setting Webhook
Run the Python bot service:
```bash
pip install -r requirements.txt
python bot_service.py
```
Register your webhook with Telegram:
```text
https://api.telegram.org/bot<TELEGRAM_BOT_TOKEN>/setWebhook?url=<APP_BASE_URL>/webhook
```

### 4. Telegram Group Permissions
Add your bot to your Telegram group and promote it to **Administrator** with the following permissions:
- **Delete Messages**
- **Restrict Members (Ban/Mute)**
- **Read Message Content**

---

## Features
- **Real-Time Moderation**: Automatic rate-limiting and blacklist term filtering.
- **Pro AI Scam Detection**: Optional AI-powered analysis for phishing and fake admin impersonation.
- **Telegram Stars (XTR)**: Native monthly subscriptions using Telegram Stars.
- **Analytics & Digests**: 7-day analytics charts and `/summary` daily discussion digests.
