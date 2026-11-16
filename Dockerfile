FROM python:3.11-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY bot_service.py .

EXPOSE 7860

ENV PORT=7860

CMD ["uvicorn", "bot_service:app", "--host", "0.0.0.0", "--port", "7860"]
