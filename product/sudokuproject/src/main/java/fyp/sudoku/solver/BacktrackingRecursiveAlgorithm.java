package fyp.sudoku.solver;

import fyp.sudoku.exception.UnsolvableSudokuException;
import fyp.sudoku.model.SudokuBoard;

/**
 * Recursive backtracking method of {@link SudokuAlgorithm}. Attempts to fill each empty cell with
 * digits 1-9, recursively. If a constraint is violated, the algorithm backtracks and tries a
 * different value.
 */
public class BacktrackingRecursiveAlgorithm implements SudokuAlgorithm {

  private int nodesVisited = 0;
  private int backtracks = 0;

  /**
   * Checks if the puzzle is solvable.
   *
   * @param board Sudoku grid to solve.
   * @return true if the board can be solved.
   * @throws UnsolvableSudokuException if the puzzle cannot be solved.
   */
  @Override
  public boolean solve(SudokuBoard board) throws UnsolvableSudokuException {
    backtracks = 0;
    nodesVisited = 0;
    if (!recursiveSolver(board)) {
      throw new UnsolvableSudokuException("No solution can be found.");
    }
    return true;
  }

  /**
   * Recursive method which fills empty cells to solve the board.
   *
   * @param board Sudoku grid to solve.
   * @return true if the board can be solved otherwise false.
   */
  public boolean recursiveSolver(SudokuBoard board) {
    for (int x = 0; x < board.getLength(); x++) {
      for (int y = 0; y < board.getLength(); y++) {
        if (board.getCell(x, y) == 0) {
          // For each potential value (1-9),
          // check if no duplicates in the same row, column, or subgrid.
          for (int i = 1; i <= 9; i++) {
            if (checkDuplicate(i, board.getRow(x)) && checkDuplicate(i, board.getColumn(y))
                && checkDuplicate(i, board.getSubGrid(x, y))) {
              nodesVisited++;
              board.setCell(x, y, i);
              // Recursion
              if (recursiveSolver(board)) {
                return true;
              }
              // Backtracking
              board.setCell(x, y, 0);
              backtracks++;
            }
          }
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Checks that the potential value isn't already in the given array.
   *
   * @param value potential value .
   * @param array the row/column/subgrid the value is tested against.
   * @return true if the value doesn't exist in the array.
   */
  private boolean checkDuplicate(int value, int[] array) {
    for (int j : array) {
      if (value == j) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns the number of cell assignments attempted.
   *
   * @return number of nodes visited
   */
  public int getNodesVisited() {
    return nodesVisited;
  }

  /**
   * Returns the number of times backtracking was needed.
   *
   * @return number of backtracks
   */
  public int getBacktracks() {
    return backtracks;
  }

}
