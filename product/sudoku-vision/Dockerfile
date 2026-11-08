FROM python:3.12-slim

# Don't buffer logs, don't write .pyc files
ENV PYTHONUNBUFFERED=1 \
    PYTHONDONTWRITEBYTECODE=1 \
    PIP_NO_CACHE_DIR=1

# Run as a non-root user (same idea as the API image)
RUN useradd --create-home app
WORKDIR /app

# CPU-only PyTorch first. EasyOCR depends on torch, and the default PyPI wheel
# bundles CUDA libraries that add gigabytes and are useless on Cloud Run.
RUN pip install torch torchvision --index-url https://download.pytorch.org/whl/cpu

COPY requirements.txt .
RUN pip install -r requirements.txt

COPY --chown=app:app app.py extract.py ./

USER app

# Download the EasyOCR model now so a cold start doesn't have to fetch it
RUN python -c "import easyocr; easyocr.Reader(['en'], gpu=False)"

# Cloud Run sets $PORT (8080); locally this falls back to 8000
EXPOSE 8000
CMD ["sh", "-c", "exec uvicorn app:app --host 0.0.0.0 --port ${PORT:-8000}"]
