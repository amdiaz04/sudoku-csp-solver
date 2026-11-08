"""Tests for the web service (uses the sample images, so they need no running server)."""

import io
import os

import cv2
import numpy as np
from fastapi.testclient import TestClient

from app import app

SAMPLES = os.path.join(os.path.dirname(__file__), "samples")
IMAGE = os.path.join(SAMPLES, "synthetic.png")
EXPECTED = "4.....8.5.3..........7......2.....6.....8.4......1.......6.3.7.5..2.....1.4......"


def _post(client, data, name="upload.png"):
    return client.post("/extract", files={"image": (name, io.BytesIO(data))})


def test_health():
    with TestClient(app) as client:
        assert client.get("/health").json() == {"status": "ok"}


def test_extract_png():
    with TestClient(app) as client:
        with open(IMAGE, "rb") as f:
            r = _post(client, f.read())
        assert r.status_code == 200
        puzzle = r.json()["puzzle"]
        assert len(puzzle) == 81
        assert sum(a == b for a, b in zip(puzzle, EXPECTED)) >= 70


def test_not_an_image_is_422():
    with TestClient(app) as client:
        assert _post(client, b"not an image").status_code == 422


def test_no_grid_is_422():
    blank = cv2.imencode(".png", np.full((300, 300, 3), 255, np.uint8))[1].tobytes()
    with TestClient(app) as client:
        assert _post(client, blank).status_code == 422


def test_empty_upload_is_400():
    with TestClient(app) as client:
        assert _post(client, b"").status_code == 400
