package fyp.sudoku.solver;

import fyp.sudoku.exception.UnsolvableSudokuException;
import fyp.sudoku.model.SudokuBoard;
import java.util.ArrayList;
import java.util.List;

/**
 * Backtracking solver with Minimum Remaining Values heuristic. Selects the empty cell with the
 * fewest valid candidates at each step.
 */
public class MrvAlgorithm implements SudokuAlgorithm {

  private int nodesVisited = 0;
  private int backtracks = 0;
  private static final int BOARD_SIZE = 9;

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
   * Recursive solver that picks the most constrained cell first.
   */
  private boolean recursiveSolver(SudokuBoard board) {
    int[] cell = findMRV(board);
    if (cell == null) {
      return true;
    }
    int x = cell[0];
    int y = cell[1];
    for (int value : getDomains(board, x, y)) {
      nodesVisited++;
      board.setCell(x, y, value);
      if (recursiveSolver(board)) {
        return true;
      }
      board.setCell(x, y, 0);
      backtracks++;
    }
    return false;
  }

  /**
   * Finds the empty cell with the smallest number of valid values.
   *
   * @return coordinates as {row, column}, or null if the board is complete.
   */
  private int[] findMRV(SudokuBoard board) {
    int[] mrv = null;
    int smallestDomain = BOARD_SIZE + 1;
    for (int x = 0; x < BOARD_SIZE; x++) {
      for (int y = 0; y < BOARD_SIZE; y++) {
        if (board.getCell(x, y) == 0) {
          int domainSize = getDomains(board, x, y).size();
          if (domainSize < smallestDomain) {
            smallestDomain = domainSize;
            mrv = new int[]{x, y};
          }
        }
      }
    }
    return mrv;
  }

  /**
   * Returns all valid values for a cell based on row, column and sub-grid constraints.
   */
  private List<Integer> getDomains(SudokuBoard board, int x, int y) {
    List<Integer> domains = new ArrayList<>();
    for (int i = 1; i <= BOARD_SIZE; i++) {
      if (checkDuplicate(i, board.getRow(x))
          && checkDuplicate(i, board.getColumn(y))
          && checkDuplicate(i, board.getSubGrid(x, y))) {
        domains.add(i);
      }
    }
    return domains;
  }

  private boolean checkDuplicate(int value, int[] array) {
    for (int j : array) {
      if (value == j) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int getBacktracks() {
    return backtracks;
  }

  @Override
  public int getNodesVisited() {
    return nodesVisited;
  }


}
