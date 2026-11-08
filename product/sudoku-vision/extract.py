"""Read a Sudoku from a photo and return it as an 81-char string ('.' = blank).

Usage:
    python extract.py photo.jpg [--debug OUT_DIR]
"""

import argparse
import os
import sys

import cv2
import numpy as np

WARP_SIZE = 450  # warped grid is WARP_SIZE x WARP_SIZE pixels
CELL = WARP_SIZE // 9
MARGIN = 0.12  # fraction of each cell trimmed to drop grid lines
INK_LEVEL = 0.4  # pixel counts as ink if darker than this fraction between ink and paper
MIN_AREA_RATIO = 0.02  # digit blob must cover at least this share of the cell
MIN_HEIGHT_RATIO = 0.3  # ...and be at least this tall

_reader = None


def _get_reader():
    """EasyOCR model load is slow, so create it once."""
    global _reader
    if _reader is None:
        import easyocr

        _reader = easyocr.Reader(["en"], gpu=False, verbose=False)
    return _reader


def _order_corners(pts):
    """Order 4 points as top-left, top-right, bottom-right, bottom-left."""
    pts = pts.reshape(4, 2).astype("float32")
    s = pts.sum(axis=1)
    d = np.diff(pts, axis=1).ravel()
    return np.array(
        [pts[np.argmin(s)], pts[np.argmin(d)], pts[np.argmax(s)], pts[np.argmax(d)]],
        dtype="float32",
    )


def find_and_warp_grid(image):
    """Find the biggest 4-corner shape (the grid) and flatten it to a square."""
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    blur = cv2.GaussianBlur(gray, (7, 7), 0)
    thresh = cv2.adaptiveThreshold(
        blur, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C, cv2.THRESH_BINARY_INV, 11, 2
    )
    contours, _ = cv2.findContours(thresh, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    contours = sorted(contours, key=cv2.contourArea, reverse=True)

    for contour in contours[:10]:
        peri = cv2.arcLength(contour, True)
        approx = cv2.approxPolyDP(contour, 0.02 * peri, True)
        if len(approx) == 4:
            src = _order_corners(approx)
            dst = np.array(
                [[0, 0], [WARP_SIZE, 0], [WARP_SIZE, WARP_SIZE], [0, WARP_SIZE]],
                dtype="float32",
            )
            matrix = cv2.getPerspectiveTransform(src, dst)
            return cv2.warpPerspective(gray, matrix, (WARP_SIZE, WARP_SIZE))

    raise ValueError("Could not find a Sudoku grid in the image")


def _clean_cell(binary):
    """Keep only the digit: the biggest blob centred in the cell, ignoring line fragments."""
    count, labels, stats, centroids = cv2.connectedComponentsWithStats(binary, connectivity=8)
    h, w = binary.shape
    best, best_area = None, 0
    for i in range(1, count):
        x, y, cw, ch, area = stats[i]
        cx, cy = x + cw / 2, y + ch / 2
        centred = 0.2 * w <= cx <= 0.8 * w and 0.2 * h <= cy <= 0.8 * h
        too_wide = cw > 0.8 * w  # curved grid-line fragments are wide and thin
        if (
            not centred
            or too_wide
            or area < MIN_AREA_RATIO * h * w
            or ch < MIN_HEIGHT_RATIO * h
        ):
            continue
        if area > best_area:
            best, best_area = i, area
    out = np.zeros_like(binary)
    if best is not None:
        out[labels == best] = 255
    return out


def _remove_grid_lines(ink):
    """Erase long thin horizontal/vertical strokes (grid lines); digits are too short to match."""
    length = int(CELL * 0.7)
    lines = np.zeros_like(ink)
    for size in ((length, 1), (1, length)):
        kernel = cv2.getStructuringElement(cv2.MORPH_RECT, size)
        lines = cv2.bitwise_or(lines, cv2.morphologyEx(ink, cv2.MORPH_OPEN, kernel))
    lines = cv2.dilate(lines, np.ones((3, 3), np.uint8))
    return cv2.bitwise_and(ink, cv2.bitwise_not(lines))


def split_cells(warped):
    """Cut the warped grid into 81 cleaned cell images (digit = white, blank = all black)."""
    # Threshold on real ink darkness over the whole grid so faint show-through
    # from the back of the page is ignored.
    low = np.percentile(warped, 3)
    background = np.median(warped)
    thresh = low + INK_LEVEL * (background - low)
    ink = (warped < thresh).astype(np.uint8) * 255
    ink = _remove_grid_lines(ink)

    cells = []
    pad = int(CELL * MARGIN)
    for row in range(9):
        for col in range(9):
            cell = ink[
                row * CELL + pad : (row + 1) * CELL - pad,
                col * CELL + pad : (col + 1) * CELL - pad,
            ]
            cells.append(_clean_cell(cell))
    return cells


def _is_blank(binary):
    return not binary.any()


def _prepare_for_ocr(binary):
    """Tight-crop the digit, scale it up, black on white with padding."""
    ys, xs = np.nonzero(binary)
    digit = binary[ys.min() : ys.max() + 1, xs.min() : xs.max() + 1]
    scale = 40.0 / digit.shape[0]
    digit = cv2.resize(
        digit, None, fx=scale, fy=scale, interpolation=cv2.INTER_AREA
    )
    img = 255 - digit
    return cv2.copyMakeBorder(img, 24, 24, 24, 24, cv2.BORDER_CONSTANT, value=255)


def read_digit(binary):
    """Return (digit_char, confidence) for one cell; ('.', 1.0) when blank."""
    if _is_blank(binary):
        return ".", 1.0
    img = _prepare_for_ocr(binary)
    h, w = img.shape[:2]
    # recognize() skips EasyOCR's text detector, which often misses a lone digit;
    # the whole cell is treated as one pre-cropped line of text.
    results = _get_reader().recognize(
        img,
        horizontal_list=[[0, w, 0, h]],
        free_list=[],
        allowlist="123456789",
        detail=1,
    )
    if not results:
        return ".", 0.0
    _, text, conf = max(results, key=lambda r: r[2])
    text = text.strip()
    return (text[0], float(conf)) if text and text[0] in "123456789" else (".", 0.0)


def extract_grid(image_path, debug_dir=None):
    """Return the puzzle in the image as an 81-char string ('.' = blank)."""
    image = cv2.imread(image_path)
    if image is None:
        raise ValueError(f"Could not read image: {image_path}")

    warped = find_and_warp_grid(image)
    cells = split_cells(warped)

    if debug_dir:
        os.makedirs(debug_dir, exist_ok=True)
        cv2.imwrite(os.path.join(debug_dir, "warped.png"), warped)
        for i, cell in enumerate(cells):
            cv2.imwrite(os.path.join(debug_dir, f"cell_{i // 9}_{i % 9}.png"), cell)

    digits = [read_digit(cell)[0] for cell in cells]
    return "".join(digits)


def format_grid(puzzle):
    lines = []
    for r in range(9):
        row = puzzle[r * 9 : (r + 1) * 9]
        lines.append(" ".join(row[0:3]) + " | " + " ".join(row[3:6]) + " | " + " ".join(row[6:9]))
        if r in (2, 5):
            lines.append("------+-------+------")
    return "\n".join(lines)


def main():
    parser = argparse.ArgumentParser(description="Read a Sudoku from a photo")
    parser.add_argument("images", nargs="+", help="image file(s)")
    parser.add_argument("--debug", metavar="DIR", help="save warped grid and cells here")
    args = parser.parse_args()

    for path in args.images:
        try:
            puzzle = extract_grid(path, args.debug)
        except ValueError as e:
            print(f"{path}: {e}", file=sys.stderr)
            continue
        print(f"{path}\n{puzzle}\n{format_grid(puzzle)}\n")


if __name__ == "__main__":
    main()
