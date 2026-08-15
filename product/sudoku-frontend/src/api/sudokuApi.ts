/* API service functions to talk to backend.

Includes: getting difficulties, generating a puzzle, solving the puzzle, 
  getting all possible algorithm names, setting an algorithm for the solver.*/

import type {
  SolvePuzzleResponse,
  Difficulty,
  GeneratePuzzleResponse,
  CalcAvgResponse,
} from "@/types/sudoku";

export async function getDifficulties(): Promise<Difficulty[]> {
  const response = await fetch("/api/sudoku/difficulties");
  if (!response.ok) {
    throw new Error("Failed to get difficulties");
  }
  return response.json();
}

export async function generatePuzzle(
  difficulty?: Difficulty,
): Promise<GeneratePuzzleResponse> {
  const url = difficulty
    ? `/api/sudoku/generate/${difficulty}`
    : "/api/sudoku/generate";
  const response = await fetch(url);
  if (!response.ok) {
    throw new Error("Failed to generate Sudoku puzzle");
  }
  return response.json();
}

export async function solvePuzzle(): Promise<SolvePuzzleResponse> {
  const response = await fetch("/api/sudoku/solve", 
    { method: "POST" });
  if (!response.ok) {
    throw new Error("Failed to solve Sudoku puzzle");
  }
  return response.json();
}

export async function getAlgorithmsNames(): Promise<string[]> {
  const response = await fetch("/api/sudoku/algorithm-names");
  if (!response.ok) {
    throw new Error("Failed to get algorithms");
  }
  return response.json();
}

export async function setAlgorithm(alg: string) {
  const response = await fetch(
    `/api/sudoku/algorithm?name=${encodeURIComponent(alg)}`,
    { method: "POST" },
  );
  if (!response.ok) {
    throw new Error("Failed to set algorithm");
  }
}


export async function calcAvg(puzzleCount: number, difficulty?: Difficulty): Promise<CalcAvgResponse>{
  const url = difficulty
  ? `/api/sudoku/calculate-average?puzzleCount=${puzzleCount}&difficulty=${difficulty}`
  : `/api/sudoku/calculate-average?puzzleCount=${puzzleCount}`;
  const response = await fetch(url);
  if (!response.ok) {
    throw new Error("Failed to calculate performance");
  }
  return response.json();
}

export async function uploadPuzzle(file: File): Promise<GeneratePuzzleResponse | null> {
  const text = await file.text();
  const response = await fetch("/api/sudoku/upload-puzzle", {
    method: "POST",
    body: text,
  });
  if (!response.ok) return null;
  return response.json();
}

export async function uploadPuzzles(file: File): Promise<CalcAvgResponse | null> {
  const text = await file.text();
  const response = await fetch("/api/sudoku/upload-puzzles", {
    method: "POST",
    body: text,
  });
  if (!response.ok) return null;
  return response.json();
}