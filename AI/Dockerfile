FROM python:3.10-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --upgrade pip
RUN pip install --no-cache-dir -r requirements.txt

COPY ./Program ./Program
COPY ./Data ./Data

# API 진입점 예시 (수정 가능)
CMD ["uvicorn", "Program.API.main:app", "--host", "0.0.0.0", "--port", "5000"]
