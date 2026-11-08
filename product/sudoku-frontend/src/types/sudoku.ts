/* Type definitions for type safety.

Includes: grid type, solve puzzle response structure, difficulties, generate puzzle response structure. */

export type Grid = number[][];

export type SolvePuzzleResponse = {
  grid: Grid;
  backtracks: number;
  nodesVisited: number;
  solveTime: number;
};

export type Difficulty = "EASY" | "MEDIUM" | "HARD";

export type GeneratePuzzleResponse = {
  grid: Grid;
  difficulty: Difficulty;
};

export type CalcAvgResponse = {
  avgBacktracks: Record<string, number>;
  avgNodesVisited: Record<string, number>;
  avgTime: Record<string, number>;
};

export type SolveCheckResponse = {
  grid: Grid;
  unique: boolean;
};
