package fyp.sudoku.sudoku_api;

import fyp.sudoku.exception.SudokuReadingException;
import fyp.sudoku.exception.UnsolvableSudokuException;
import fyp.sudoku.model.Difficulty;
import fyp.sudoku.model.SudokuBoard;
import fyp.sudoku.model.SudokuPuzzle;
import fyp.sudoku.solver.MacAlgorithm;
import fyp.sudoku.solver.MacMrvAlgorithm;
import fyp.sudoku.solver.Ac3Algorithm;
import fyp.sudoku.solver.MrvAlgorithm;
import fyp.sudoku.solver.Ac3MrvAlgorithm;
import fyp.sudoku.solver.BacktrackingRecursiveAlgorithm;
import fyp.sudoku.solver.SudokuAlgorithm;
import fyp.sudoku.sudoku_api.types.CalcAvgResponse;
import fyp.sudoku.util.SudokuGenerator;
import fyp.sudoku.util.SudokuReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Manages Sudoku puzzle state and coordinates solving across different algorithms. Handles puzzle
 * generation, solving, and performance benchmarking.
 */
@Component
public class SudokuManager {

  private final SudokuGenerator generator;
  private SudokuPuzzle currentPuzzle;
  private SudokuBoard currentGrid;
  private SudokuAlgorithm currentAlgorithm;
  private final Map<String, SudokuAlgorithm> algorithms;
  private int lastBacktracks;
  private int lastNodesVisited;
  private double lastSolveTime;

  public SudokuManager() {
    this.generator = new SudokuGenerator();
    this.currentGrid = null;
    this.currentPuzzle = null;
    this.algorithms = new HashMap<>();
    SudokuAlgorithm defaultAlgorithm = new BacktrackingRecursiveAlgorithm();
    SudokuAlgorithm ac3 = new Ac3Algorithm();
    SudokuAlgorithm mrv = new MrvAlgorithm();
    SudokuAlgorithm ac3mrv = new Ac3MrvAlgorithm();
    SudokuAlgorithm mac = new MacAlgorithm();
    SudokuAlgorithm macmrv = new MacMrvAlgorithm();
    algorithms.put("Backtracking", defaultAlgorithm);
    algorithms.put("MRV", mrv);
    algorithms.put("AC-3", ac3);
    algorithms.put("AC-3 + MRV", ac3mrv);
    algorithms.put("MAC", mac);
    algorithms.put("MAC + MRV", macmrv);
    this.currentAlgorithm = defaultAlgorithm;
    this.lastBacktracks = 0;
    this.lastNodesVisited = 0;
  }

  // Set the type of solver
  public void setAlgorithm(SudokuAlgorithm algorithm) {
    this.currentAlgorithm = algorithm;
  }

  public Map<String, SudokuAlgorithm> getAlgorithms() {
    return new HashMap<>(algorithms);
  }

  public List<String> getAlgorithmNames() {
    return new ArrayList<>(algorithms.keySet());
  }

  // Generate random puzzle
  public SudokuBoard generateRandomBoard() {
    currentGrid = generator.generatePuzzle();
    currentPuzzle = new SudokuPuzzle(currentGrid);
    return currentGrid;
  }

  // Generate random puzzle with a selected difficulty
  public SudokuBoard generateBoardWithDifficulty(Difficulty difficulty) {
    currentGrid = generator.generatePuzzle(difficulty);
    currentPuzzle = new SudokuPuzzle(currentGrid);
    return currentGrid;
  }

  // Generate a puzzle from a file
  public SudokuBoard loadFromUser(BufferedReader reader)
      throws IOException, SudokuReadingException, UnsolvableSudokuException {
    currentGrid = SudokuReader.readSinglePuzzle(reader);
    currentPuzzle = new SudokuPuzzle(currentGrid);
    return currentGrid;
  }


  // Get difficulty of current puzzle
  public Difficulty getDifficulty() throws UnsolvableSudokuException {
    return currentPuzzle.getDifficulty();
  }

  // List all possible difficulties
  public List<Difficulty> getDifficulties() {
    return List.of(Difficulty.values());
  }

  public int[][] getCurrentGrid() {
    if (currentGrid == null) {
      return null;
    }
    return currentGrid.getGrid();
  }

  // Solve the current puzzle with the algorithm selected
  public void solvePuzzle() throws UnsolvableSudokuException {
    long start = System.nanoTime();
    currentGrid = currentPuzzle.solve(currentAlgorithm);
    long end = System.nanoTime();
    lastSolveTime = (end - start) / 1000000.0;
    lastBacktracks = currentAlgorithm.getBacktracks();
    lastNodesVisited = currentAlgorithm.getNodesVisited();
  }

  // Get backtracks
  public int getBacktracks() {
    return lastBacktracks;
  }

  // Get nodes visited
  public int getNodesVisited() {
    return lastNodesVisited;
  }

  public double getSolveTime() {
    return lastSolveTime;
  }

  private CalcAvgResponse runCalculation(List<SudokuBoard> boards)
      throws UnsolvableSudokuException {
    Map<String, Double> totalBacktracks = new HashMap<>();
    Map<String, Double> totalNodesVisited = new HashMap<>();
    Map<String, Double> totalTime = new HashMap<>();

    for (String name : algorithms.keySet()) {
      totalBacktracks.put(name, 0.0);
      totalNodesVisited.put(name, 0.0);
      totalTime.put(name, 0.0);
    }

    for (SudokuBoard board : boards) {
      SudokuPuzzle puzzle = new SudokuPuzzle(board);
      for (String name : algorithms.keySet()) {
        long start = System.nanoTime();
        puzzle.solve(algorithms.get(name));
        long end = System.nanoTime();
        double time = (end - start) / 1000000.0;

        // replace the value for each algorithm by adding from last no of metrics
        totalBacktracks.put(name, totalBacktracks.get(name) + puzzle.getBacktracks());
        totalNodesVisited.put(name, totalNodesVisited.get(name) + puzzle.getNodesVisited());
        totalTime.put(name, totalTime.get(name) + time);
      }
    }

    Map<String, Double> avgBacktracks = new HashMap<>();
    Map<String, Double> avgNodesVisited = new HashMap<>();
    Map<String, Double> avgTime = new HashMap<>();

    for (String name : algorithms.keySet()) {
      avgBacktracks.put(name, totalBacktracks.get(name) / boards.size());
      avgNodesVisited.put(name, totalNodesVisited.get(name) / boards.size());
      avgTime.put(name, totalTime.get(name) / boards.size());
    }

    return new CalcAvgResponse(avgBacktracks, avgNodesVisited, avgTime);
  }

  public CalcAvgResponse calcAvgFromFile(BufferedReader reader)
      throws SudokuReadingException, IOException, UnsolvableSudokuException {
    List<SudokuBoard> boards = new ArrayList<>();
    boards = SudokuReader.readMultiplePuzzles(reader);
    return runCalculation(boards);
  }

  public CalcAvgResponse calcAvgPerformance(int puzzleCount, Difficulty difficulty)
      throws UnsolvableSudokuException {
    List<SudokuBoard> boards = new ArrayList<>();
    for (int i = 0; i < puzzleCount; i++) {
      if (difficulty == null) {
        boards.add(generator.generatePuzzle());
      } else {
        boards.add(generator.generatePuzzle(difficulty));
      }
    }
    return runCalculation(boards);
  }
}


