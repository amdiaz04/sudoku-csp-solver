package fyp.sudoku.solver;

import fyp.sudoku.exception.UnsolvableSudokuException;
import fyp.sudoku.model.SudokuBoard;

/**
 * Strategy interface for solving algorithms.
 */
public interface SudokuAlgorithm {

  /**
   * Attempts to solve the board.
   *
   * @param board Sudoku grid to solve.
   * @return true if the board can be solved otherwise false.
   * @throws UnsolvableSudokuException if the puzzle cannot be solved.
   */
  boolean solve(SudokuBoard board) throws UnsolvableSudokuException;

  /**
   * Get number of backtracks during solving.
   */
  int getBacktracks();

  /**
   * Get number of nodes visited during solving.
   */
  int getNodesVisited();
}
