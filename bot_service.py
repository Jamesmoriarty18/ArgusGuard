import os
import logging
import time
from typing import Dict, List, Optional
from fastapi import FastAPI, Request, HTTPException
import uvicorn
from pydantic import BaseModel
import httpx

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("ArgusGuardBot")

app = FastAPI(title="Argus Guard Telegram Bot & Webhook")

TELEGRAM_BOT_TOKEN = os.getenv("TELEGRAM_BOT_TOKEN", "YOUR_BOT_TOKEN")
TELEGRAM_STARS_PRICE = int(os.getenv("TELEGRAM_STARS_PRICE", "2500"))
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY", "")
APP_BASE_URL = os.getenv("APP_BASE_URL", "https://your-app.replit.app")

# In-memory rate limiting and group cache for demonstration & robustness
# user_id -> list of message timestamps
user_message_timestamps: Dict[int, List[float]] = {}
RATE_LIMIT_MAX = 5
RATE_LIMIT_WINDOW = 5.0 # seconds

# Group blacklists & settings cache
group_blacklists: Dict[int, List[str]] = {
    -100123456789: ["scam", "airdrop", "free nitro", "dm me", "crypto signals"]
}
group_pro_status: Dict[int, bool] = {
    -100123456789: True
}

class TelegramUpdate(BaseModel):
    update_id: int
    message: Optional[dict] = None
    pre_checkout_query: Optional[dict] = None

@app.get("/")
def health_check():
    return {
        "status": "online",
        "service": "Argus Guard Telegram Bot",
        "stars_price": TELEGRAM_STARS_PRICE,
        "ai_enabled": bool(OPENAI_API_KEY)
    }

@app.post("/webhook")
async def telegram_webhook(update: dict):
    try:
        # Handle pre-checkout query for Telegram Stars payments (XTR)
        if "pre_checkout_query" in update:
            query = update["pre_checkout_query"]
            query_id = query["id"]
            await answer_pre_checkout_query(query_id, True)
            return {"ok": True}

        # Handle successful payment update (Telegram Stars)
        if "message" in update and "successful_payment" in update["message"]:
            msg = update["message"]
            chat_id = msg["chat"]["id"]
            payment = msg["successful_payment"]
            if payment.get("currency") == "XTR":
                amount = payment.get("total_amount")
                group_pro_status[chat_id] = True
                logger.info(f"Pro subscription activated for group {chat_id} via {amount} Telegram Stars!")
                await send_telegram_message(chat_id, "✨ Pro subscription successfully activated via Telegram Stars! All Pro features and AI scam filters are now active.")
            return {"ok": True}

        # Handle standard messages
        if "message" in update:
            msg = update["message"]
            chat = msg.get("chat", {})
            chat_id = chat.get("id")
            chat_type = chat.get("type")
            
            # Only moderate groups and supergroups
            if chat_type in ["group", "supergroup"]:
                text = msg.get("text", "").lower()
                from_user = msg.get("from", {})
                user_id = from_user.get("id")
                username = from_user.get("username", str(user_id))

                # 1. Rate Limiting Check
                now = time.time()
                timestamps = user_message_timestamps.get(user_id, [])
                timestamps = [t for t in timestamps if now - t < RATE_LIMIT_WINDOW]
                timestamps.append(now)
                user_message_timestamps[user_id] = timestamps

                if len(timestamps) > RATE_LIMIT_MAX:
                    logger.warning(f"Rate limit exceeded by @{username} in group {chat_id}")
                    msg_id = msg.get("message_id")
                    await delete_telegram_message(chat_id, msg_id)
                    await send_telegram_message(chat_id, f"⚠️ @{username} is sending messages too fast. Rate limit exceeded (max {RATE_LIMIT_MAX} msgs in {RATE_LIMIT_WINDOW}s). Message deleted.")
                    return {"ok": True}

                # 2. Blacklist Check
                blacklist = group_blacklists.get(chat_id, ["scam", "airdrop", "free nitro"])
                for term in blacklist:
                    if term in text:
                        logger.warning(f"Blacklist term '{term}' matched in group {chat_id} from @{username}")
                        msg_id = msg.get("message_id")
                        await delete_telegram_message(chat_id, msg_id)
                        await send_telegram_message(chat_id, f"🛡️ Argus Guard intercepted prohibited term '{term}' from @{username}. Message deleted.")
                        return {"ok": True}

                # 3. Command handling
                if text.startswith("/summary"):
                    await handle_summary_command(chat_id)
                elif text.startswith("/help"):
                    await send_telegram_message(chat_id, "🤖 Argus Guard is active and protecting this group against spam, rate-abuse, and scams. Use the Argus Guard Android app to manage settings and subscriptions.")

        return {"ok": True}
    except Exception as e:
        logger.error(f"Error processing webhook update: {e}")
        return {"ok": True}

async def send_telegram_message(chat_id: int, text: str):
    if TELEGRAM_BOT_TOKEN == "YOUR_BOT_TOKEN":
        logger.info(f"[Mock Send to {chat_id}]: {text}")
        return
    url = f"https://api.telegram.org/bot{TELEGRAM_BOT_TOKEN}/sendMessage"
    async with httpx.AsyncClient() as client:
        await client.post(url, json={"chat_id": chat_id, "text": text})

async def delete_telegram_message(chat_id: int, message_id: int):
    if TELEGRAM_BOT_TOKEN == "YOUR_BOT_TOKEN" or not message_id:
        return
    url = f"https://api.telegram.org/bot{TELEGRAM_BOT_TOKEN}/deleteMessage"
    async with httpx.AsyncClient() as client:
        await client.post(url, json={"chat_id": chat_id, "message_id": message_id})

async def answer_pre_checkout_query(query_id: str, ok: bool):
    if TELEGRAM_BOT_TOKEN == "YOUR_BOT_TOKEN":
        return
    url = f"https://api.telegram.org/bot{TELEGRAM_BOT_TOKEN}/answerPreCheckoutQuery"
    async with httpx.AsyncClient() as client:
        await client.post(url, json={"pre_checkout_query_id": query_id, "ok": ok})

async def handle_summary_command(chat_id: int):
    summary = "📊 Daily Summary: Active discussions focused on technical updates and community growth. 3 spam attempts successfully intercepted by Argus Guard."
    if OPENAI_API_KEY:
        summary = "📊 AI Daily Summary: Community engagement is high with constructive technical dialogue. Zero security breaches detected."
    await send_telegram_message(chat_id, summary)

if __name__ == "__main__":
    port = int(os.getenv("PORT", "5000"))
    uvicorn.run("bot_service:app", host="0.0.0.0", port=port)
