package fyp.sudoku;

import fyp.sudoku.exception.SudokuReadingException;
import fyp.sudoku.exception.UnsolvableSudokuException;
import fyp.sudoku.model.Difficulty;
import fyp.sudoku.model.SudokuBoard;
import fyp.sudoku.model.SudokuPuzzle;
import fyp.sudoku.solver.Ac3Algorithm;
import fyp.sudoku.solver.Ac3MrvAlgorithm;
import fyp.sudoku.solver.MacMrvAlgorithm;
import fyp.sudoku.solver.BacktrackingRecursiveAlgorithm;
import fyp.sudoku.solver.MacAlgorithm;
import fyp.sudoku.solver.MrvAlgorithm;
import fyp.sudoku.solver.SudokuAlgorithm;
import fyp.sudoku.util.SudokuGenerator;
import fyp.sudoku.util.SudokuReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Benchmark runner that solves generated and external puzzles with all algorithms and outputs
 * results as CSV style for analysis in the report.
 */
public class SudokuMain {

  private static final int PUZZLES_PER_DIFFICULTY = 50;

  public static void main(String[] args) {
    Map<String, SudokuAlgorithm> algorithms = new HashMap<>();
    algorithms.put("Backtracking", new BacktrackingRecursiveAlgorithm());
    algorithms.put("AC-3", new Ac3Algorithm());
    algorithms.put("MRV", new MrvAlgorithm());
    algorithms.put("AC-3+MRV", new Ac3MrvAlgorithm());
    algorithms.put("MAC", new MacAlgorithm());
    algorithms.put("MAC+MRV", new MacMrvAlgorithm());

    Difficulty[] difficulties = {Difficulty.EASY, Difficulty.MEDIUM, Difficulty.HARD};
    SudokuGenerator generator = new SudokuGenerator();

    System.out.println("algorithm,difficulty,puzzle_id,backtracks,nodes_visited,solve_time");

    for (Difficulty difficulty : difficulties) {
      SudokuBoard[] puzzles = new SudokuBoard[PUZZLES_PER_DIFFICULTY];
      for (int i = 0; i < PUZZLES_PER_DIFFICULTY; i++) {
        puzzles[i] = generator.generatePuzzle(difficulty);
      }

      for (int i = 0; i < PUZZLES_PER_DIFFICULTY; i++) {
        for (Map.Entry<String, SudokuAlgorithm> entry : algorithms.entrySet()) {
          String name = entry.getKey();
          SudokuAlgorithm algorithm = entry.getValue();

          try {
            SudokuPuzzle puzzle = new SudokuPuzzle(puzzles[i]);
            double start = System.nanoTime();
            puzzle.solve(algorithm);
            double end = System.nanoTime();
            double solveTime = (end - start) / 1000000;
            int backtracks = algorithm.getBacktracks();
            int nodesVisited = algorithm.getNodesVisited();

            System.out.println(
                name + "," + difficulty + "," + (i + 1) + "," + backtracks + "," + nodesVisited
                    + "," + solveTime);
          } catch (UnsolvableSudokuException e) {
            System.err.println("Failed");
          }
        }
      }
    }

    try (BufferedReader reader =
        Files.newBufferedReader(Path.of("puzzles/puzzle.txt"), StandardCharsets.UTF_8)) {
      List<SudokuBoard> boards = SudokuReader.readMultiplePuzzles(reader);

      for (int i = 0; i < boards.size(); i++) {
        for (Map.Entry<String, SudokuAlgorithm> entry : algorithms.entrySet()) {
          String name = entry.getKey();
          SudokuAlgorithm algorithm = entry.getValue();

          try {
            SudokuPuzzle puzzle = new SudokuPuzzle(boards.get(i));
            double start = System.nanoTime();
            puzzle.solve(algorithm);
            double end = System.nanoTime();
            double solveTime = (end - start) / 1000000;
            int backtracks = algorithm.getBacktracks();
            int nodesVisited = algorithm.getNodesVisited();
            System.out.println(
                name + "," + "EXTERNAL" + "," + (i + 1) + "," + backtracks + "," + nodesVisited
                    + "," + solveTime);
          } catch (UnsolvableSudokuException e) {
            System.err.println("Failed");
          }
        }
      }
    } catch (IOException | SudokuReadingException e) {
      System.err.println("Failed");
    }
  }
}