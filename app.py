import os
import threading
import uvicorn
from bot_service import app

def run_server():
    port = int(os.getenv("PORT", "7860"))
    uvicorn.run(app, host="0.0.0.0", port=port)

if __name__ == "__main__":
    # Start FastAPI server for Hugging Face Spaces free tier
    server_thread = threading.Thread(target=run_server, daemon=True)
    server_thread.start()
    
    # Keep main thread alive
    print("Argus Guard Bot server running on Hugging Face Spaces free tier...")
    server_thread.join()
