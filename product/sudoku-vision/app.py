"""Small web service around extract.py.

Run:  .venv/bin/uvicorn app:app --port 8000
Try:  curl -F image=@samples/IMG_3184.jpg localhost:8000/extract
"""

from contextlib import asynccontextmanager

from fastapi import FastAPI, File, HTTPException, UploadFile

import extract

MAX_BYTES = 20 * 1024 * 1024  # phone photos are several MB; refuse anything huge


@asynccontextmanager
async def lifespan(app):
    # Load the (slow) OCR model once at startup instead of on the first request.
    extract._get_reader()
    yield


app = FastAPI(title="Sudoku vision", lifespan=lifespan)


@app.get("/health")
def health():
    return {"status": "ok"}


# Plain `def` (not async) so FastAPI runs this CPU-heavy work in a worker thread.
@app.post("/extract")
def extract_puzzle(image: UploadFile = File(...)):
    data = image.file.read(MAX_BYTES + 1)
    if not data:
        raise HTTPException(status_code=400, detail="Empty upload")
    if len(data) > MAX_BYTES:
        raise HTTPException(status_code=413, detail="Image too large (max 20 MB)")
    try:
        puzzle = extract.extract_grid_from_bytes(data)
    except ValueError as e:
        raise HTTPException(status_code=422, detail=str(e))
    return {"puzzle": puzzle}
