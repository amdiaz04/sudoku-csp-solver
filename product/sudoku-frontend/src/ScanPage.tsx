// Page for scanning a photo of a Sudoku, fixing misread cells, and checking
// whether the puzzle is solvable and has a unique solution

import { useState, useEffect } from "react";
import { SudokuGrid } from "@/components/SudokuGrid";
import { scanImage, solveCheck } from "@/api/sudokuApi";
import type { Grid, Difficulty } from "@/types/sudoku";
import { Button } from "@/components/ui/button";

function ScanPage() {
  const emptyGrid: Grid = Array(9)
    .fill(null)
    .map(() => Array(9).fill(0));

  const [file, setFile] = useState<File | null>(null);
  const [scannedGrid, setScannedGrid] = useState<Grid>(emptyGrid); // as read from the photo
  const [grid, setGrid] = useState<Grid>(emptyGrid); // scanned grid plus the user's edits
  const [difficulty, setDifficulty] = useState<Difficulty | null>(null);

  // Result of checking the current grid (runs automatically after every change)
  const [checking, setChecking] = useState<boolean>(false);
  const [unique, setUnique] = useState<boolean | null>(null);
  const [problem, setProblem] = useState<string | null>(null); // invalid or unsolvable
  const [solution, setSolution] = useState<Grid | null>(null);
  const [showSolution, setShowSolution] = useState<boolean>(false);

  const [hasGrid, setHasGrid] = useState<boolean>(false);
  const [loadingScan, setLoadingScan] = useState<boolean>(false);
  const [scanError, setScanError] = useState<string | null>(null);

  // Forget the result of any earlier check, ready for a new one
  function resetCheck() {
    setChecking(true);
    setUnique(null);
    setProblem(null);
    setSolution(null);
    setShowSolution(false);
  }

  // Check the grid half a second after the last change, ignoring stale replies
  useEffect(() => {
    if (!hasGrid) return;
    let cancelled = false;
    const timer = setTimeout(async () => {
      try {
        const result = await solveCheck(grid);
        if (cancelled) return;
        setUnique(result.unique);
        setSolution(result.grid);
      } catch (err) {
        if (cancelled) return;
        setProblem(err instanceof Error ? err.message : "Could not check the puzzle");
      } finally {
        if (!cancelled) setChecking(false);
      }
    }, 500);
    return () => {
      cancelled = true;
      clearTimeout(timer);
    };
  }, [grid, hasGrid]);

  // A new photo clears everything from the previous one
  function handleFile(e: React.ChangeEvent<HTMLInputElement>) {
    setFile(e.target.files?.[0] ?? null);
    setScannedGrid(emptyGrid);
    setGrid(emptyGrid);
    setDifficulty(null);
    setHasGrid(false);
    setChecking(false);
    setUnique(null);
    setProblem(null);
    setSolution(null);
    setShowSolution(false);
    setScanError(null);
  }

  // Send the photo to the backend and show the grid it reads
  async function readPhoto() {
    if (!file) return;
    setLoadingScan(true);
    setScanError(null);
    try {
      const result = await scanImage(file);
      resetCheck();
      setScannedGrid(result.grid);
      setGrid(result.grid);
      setDifficulty(result.difficulty);
      setHasGrid(true);
    } catch (err) {
      setHasGrid(false);
      setGrid(emptyGrid);
      setScannedGrid(emptyGrid);
      setDifficulty(null);
      setScanError(err instanceof Error ? err.message : "Could not read the photo");
    } finally {
      setLoadingScan(false);
    }
  }

  // The user fixed (or cleared) a cell
  function editCell(row: number, col: number, value: number) {
    if (grid[row][col] === value) return;
    resetCheck();
    setGrid(grid.map((r, i) => (i === row ? r.map((v, j) => (j === col ? value : v)) : r)));
  }

  const edited = grid.some((row, i) => row.some((v, j) => v !== scannedGrid[i][j]));

  return (
    <div className="mx-auto flex w-full max-w-7xl flex-col gap-6 p-4 md:p-6">
      <header className="space-y-2">
        <h1 className="text-2xl font-semibold tracking-tight md:text-3xl">
          Scan a Puzzle
        </h1>
        <p className="text-sm text-muted-foreground">
          Take or choose a photo of a Sudoku. Tap any cell to fix a digit that
          was misread (edited cells turn blue), then solve it.
        </p>
      </header>

      {/* Controls */}
      <div className="flex flex-col gap-3 md:flex-row md:items-center md:gap-4">
        <input
          type="file"
          accept="image/*"
          onChange={handleFile}
          className="w-full text-sm md:w-auto"
        />
        <Button
          className="h-11 w-full md:h-8 md:w-auto"
          onClick={readPhoto}
          disabled={!file || loadingScan}
        >
          {loadingScan ? "Reading..." : "Read Puzzle"}
        </Button>
        <Button
          className="h-11 w-full md:h-8 md:w-auto"
          onClick={() => setShowSolution(true)}
          disabled={!solution || showSolution}
        >
          Solve Puzzle
        </Button>
        {edited && (
          <Button
            variant="outline"
            className="h-11 w-full md:h-8 md:w-auto"
            onClick={() => {
              resetCheck();
              setGrid(scannedGrid);
            }}
          >
            Reset edits
          </Button>
        )}
      </div>

      {scanError && (
        <p className="rounded-md border border-red-300 bg-red-50 p-3 text-sm text-red-700">
          {scanError}
        </p>
      )}

      {/* Grids */}
      <div className="flex flex-col gap-6 md:flex-row md:items-start md:gap-10">
        <div className="flex w-full flex-col gap-3 md:w-auto">
          <SudokuGrid
            grid={grid}
            editable={hasGrid}
            onCellChange={editCell}
            original={scannedGrid}
          />
          <p className="text-sm text-muted-foreground">
            Difficulty: {difficulty}
          </p>

          {/* Result of checking the grid, shown before solving */}
          {hasGrid && checking && (
            <p className="rounded-md border p-3 text-sm text-muted-foreground">
              Checking puzzle...
            </p>
          )}
          {problem && (
            <p className="rounded-md border border-red-300 bg-red-50 p-3 text-sm text-red-700">
              This puzzle cannot be solved: {problem}
              {edited
                ? " Check the cells you edited (blue)."
                : " A digit may have been misread, tap a cell to fix it."}
            </p>
          )}
          {unique === true && (
            <p className="rounded-md border border-green-300 bg-green-50 p-3 text-sm text-green-800">
              Unique solution: this puzzle has exactly one solution.
            </p>
          )}
          {unique === false && (
            <p className="rounded-md border border-amber-300 bg-amber-50 p-3 text-sm text-amber-800">
              Multiple solutions: this puzzle has more than one solution. A
              digit may be missing or misread.
            </p>
          )}
        </div>

        <div className="flex w-full flex-col gap-4 md:w-auto">
          <SudokuGrid grid={showSolution && solution ? solution : emptyGrid} />
        </div>
      </div>
    </div>
  );
}

export default ScanPage;
