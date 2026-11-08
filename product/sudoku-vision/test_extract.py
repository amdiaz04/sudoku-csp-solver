"""Runs every image in samples/ and compares against samples/expected.txt.

expected.txt format, one per line:  filename.jpg 4.....8.5.3....  (81 chars)
"""

import os

import pytest

from extract import extract_grid

SAMPLES = os.path.join(os.path.dirname(__file__), "samples")
EXPECTED = os.path.join(SAMPLES, "expected.txt")


def _load_expected():
    if not os.path.exists(EXPECTED):
        return []
    with open(EXPECTED) as f:
        rows = []
        for line in f:
            if not line.strip() or line.startswith("#"):
                continue
            name, *rest = line.split()
            # join pieces so a stray space inside the 81 chars doesn't break parsing
            rows.append((name, "".join(rest)))
        return rows


@pytest.mark.parametrize("filename,expected", _load_expected())
def test_sample(filename, expected):
    assert len(expected) == 81
    got = extract_grid(os.path.join(SAMPLES, filename))
    matched = sum(a == b for a, b in zip(got, expected))
    print(f"\n{filename}: {matched}/81 cells match")
    print(f"expected {expected}\n     got {got}")
    assert matched >= 70, f"only {matched}/81 cells matched"
