// Page for generating puzzles, selecting algorithms and solving them

import { useState, useEffect } from "react";
import { SudokuGrid } from "@/components/SudokuGrid";
import {
  generatePuzzle,
  solvePuzzle,
  getDifficulties,
  getAlgorithmsNames,
  setAlgorithm,
  uploadPuzzle,
} from "@/api/sudokuApi";
import type { Grid, Difficulty } from "@/types/sudoku";
import { Button } from "@/components/ui/button";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

function App() {
  const rows = Array(9).fill(null);
  const emptyGrid: Grid = rows.map(() => {
    const row = Array(9).fill(0); // fill each row with array of zeros
    return row;
  });

  const [grid, setGrid] = useState<Grid>(emptyGrid);
  const [solvedGrid, setSolvedGrid] = useState<Grid>(emptyGrid);

  const [hasGrid, setHasGrid] = useState<boolean>(false);
  const [solved, setSolved] = useState<boolean>(false);

  const [backtracks, setBacktracks] = useState<number | null>(null);
  const [nodesVisited, setNodesVisited] = useState<number | null>(null);
  const [solveTime, setSolveTime] = useState<number | null>(null);

  const [difficulties, setDifficulties] = useState<Difficulty[]>([]);
  const [difficulty, setDifficulty] = useState<Difficulty | "RANDOM" | "">("");
  const [puzzleDifficulty, setPuzzleDifficulty] = useState<Difficulty | null>(
    null,
  );

  const [algorithmNames, setAlgorithmNames] = useState<string[]>([]);
  const [algorithm, setSolverAlgorithm] = useState<string>();

  const [loadingGenerator, setLoadingGen] = useState<boolean>(false);
  const [loadingSolver, setLoadingSolv] = useState<boolean>(false);
  const [loadingUpload, setLoadingUpload] = useState<boolean>(false);

  const [file, setFile] = useState<File | null>(null);
  const [fileInputKey, setFileInputKey] = useState<number>(0);

  // Fetch difficulties and algorithms (for the drop down component) on the first render
  useEffect(() => {
    async function fetchDifficulties() {
      const result = await getDifficulties();
      setDifficulties(result);
    }
    async function fetchAlgorithms() {
      const result = await getAlgorithmsNames();
      setAlgorithmNames(result);
    }
    fetchDifficulties();
    fetchAlgorithms();
  }, []); // fetches once

  // Generate a new puzzle with a difficulty or random
  async function getGeneratePuzzle() {
    // only show loading if the request takes long than 500ms
    const timer = setTimeout(() => setLoadingGen(true), 200);
    setBacktracks(null); // reset stats
    setNodesVisited(null);

    let result;
    if (difficulty === "RANDOM" || difficulty === undefined) {
      result = await generatePuzzle();
    } else {
      result = await generatePuzzle(difficulty as Difficulty);
    }

    clearTimeout(timer);
    setGrid(result.grid);
    setPuzzleDifficulty(result.difficulty);
    setSolvedGrid(emptyGrid);

    setSolved(false);
    setHasGrid(true);
    setLoadingGen(false);
  }

  // Solve the puzzle with the user's selected algorithm
  async function getSolver() {
    const timer = setTimeout(() => setLoadingSolv(true), 500);
    if (!algorithm) return;

    await setAlgorithm(algorithm);
    const result = await solvePuzzle();

    clearTimeout(timer);
    setSolvedGrid(result.grid);
    setBacktracks(result.backtracks);
    setNodesVisited(result.nodesVisited);
    setSolveTime(result.solveTime);
    setSolved(true);
    setLoadingSolv(false);
  }

  // Validates uploaded file is a .txt before accepting
  function handleFile(e: React.ChangeEvent<HTMLInputElement>) {
    if (e.target.files && e.target.files[0]) {
      const selected = e.target.files[0];
      if (!selected.name.endsWith(".txt")) {
        alert("Please upload a .txt file");
        e.target.value = "";
        return;
      }
      setFile(selected);
    }
  }

  // Upload a puzzle file and load it onto the grid
  async function solveFilePuzzle() {
    if (!file) return;
    setLoadingUpload(true);
    setBacktracks(null);
    setNodesVisited(null);
    setSolveTime(null);

    const result = await uploadPuzzle(file);

    if (!result) {
      alert("Invalid file format. Check the file is formatted correctly.");
      setFile(null);
      setFileInputKey((prev) => prev + 1);
      setLoadingUpload(false);
      return;
    }

    setGrid(result.grid);
    setPuzzleDifficulty(result.difficulty);
    setSolvedGrid(emptyGrid);
    setSolved(false);
    setHasGrid(true);
    setLoadingUpload(false);
  }

  return (
    <>
      {/* Whole screen */}
      <div className="mx-auto flex w-full max-w-7xl flex-col gap-6 p-6">
        {/* Title and headings */}
        <header className="space-y-2">
          <h1 className="text-3xl font-semibold tracking-tight">
            Sudoku Solver
          </h1>
          <p className="text-sm text-muted-foreground">
            Generate a puzzle by selecting a difficulty and solve it with
            different algorithms.
          </p>
        </header>

        {/* Puzzle controls */}
        <div className="flex flex-col gap-4">
          {/* Row 1: Generate or upload a puzzle */}
          <div className="flex flex-row items-center gap-4">
            <Select
              value={difficulty}
              onValueChange={(val) =>
                setDifficulty(val as Difficulty | "RANDOM")
              }
            >
              <SelectTrigger className="w-48">
                <SelectValue placeholder="Select difficulty" />
              </SelectTrigger>
              <SelectContent side="bottom">
                <SelectItem value="RANDOM">RANDOM</SelectItem>
                {difficulties.map((diff) => (
                  <SelectItem key={diff} value={diff}>
                    {diff}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>

            <Button
              onClick={getGeneratePuzzle}
              disabled={!difficulty || loadingGenerator}
            >
              {loadingGenerator ? "Generating..." : "Generate Puzzle"}
            </Button>

            <div className="mx-2 h-6 w-px bg-border" />
            <input
              key={fileInputKey}
              type="file"
              accept=".txt"
              onChange={handleFile}
              className="text-sm"
            />
            <Button onClick={solveFilePuzzle} disabled={!file || loadingUpload}>
              {loadingUpload ? "Uploading..." : "Upload Puzzle"}
            </Button>
          </div>

          {/* Row 2: Select algorithm and solve */}
          <div className="flex flex-row items-center gap-4">
            <Select
              value={algorithm}
              onValueChange={(val) => {
                setSolverAlgorithm(val);
                setSolved(false);
                setSolvedGrid(emptyGrid);
                setBacktracks(null);
                setNodesVisited(null);
              }}
            >
              <SelectTrigger className="w-48">
                <SelectValue placeholder="Select algorithm" />
              </SelectTrigger>
              <SelectContent side="bottom">
                {algorithmNames.map((alg) => (
                  <SelectItem key={alg} value={alg}>
                    {alg}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>

            <Button
              onClick={getSolver}
              disabled={!hasGrid || !algorithm || solved || loadingSolver}
            >
              {loadingSolver ? "Solving..." : "Solve Puzzle"}
            </Button>
          </div>
        </div>

        {/* Grids */}
        <div className="flex flex-row items-start gap-10">
          {/* Unsolved grid */}
          <div className="flex flex-col gap-2">
            <SudokuGrid grid={grid} />

            {/* Difficulty text */}
            <p className="text-sm text-muted-foreground">
              Difficulty: {puzzleDifficulty}
            </p>
          </div>

          {/* Solved grid */}
          <div className="flex flex-col gap-4">
            <SudokuGrid grid={solvedGrid} />

            {/* Statistics text */}
            {backtracks != null && nodesVisited != null && (
              <div className="w-full rounded-md border p-3 text-sm">
                <div className="flex items-center justify-between">
                  <span>Backtracks</span>
                  <span>{backtracks}</span>
                </div>
                <div className="flex items-center justify-between">
                  <span>Nodes visited</span>
                  <span>{nodesVisited}</span>
                </div>
                <div className="flex items-center justify-between">
                  <span>Solve time</span>
                  <span>{solveTime?.toFixed(2)} ms</span>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </>
  );
}

export default App;
