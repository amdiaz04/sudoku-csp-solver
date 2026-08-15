package fyp.sudoku.solver;

import fyp.sudoku.exception.UnsolvableSudokuException;
import fyp.sudoku.model.SudokuBoard;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Solver that runs AC-3 preprocessing followed by backtracking with MRV variable ordering on the
 * reduced domains.
 */
public class Ac3MrvAlgorithm implements SudokuAlgorithm {

  private int nodesVisited = 0;
  private int backtracks = 0;
  private static final int BOARD_SIZE = 9;

  @Override
  public boolean solve(SudokuBoard board) throws UnsolvableSudokuException {
    backtracks = 0;
    nodesVisited = 0;

    Set<Integer>[][] domains = new Set[board.getLength()][board.getLength()];
    Ac3Operations.setDomains(board, domains);

    if (!Ac3Operations.queueArcs(domains)) {
      throw new UnsolvableSudokuException("No solution");
    }

    for (int row = 0; row < board.getLength(); row++) {
      for (int col = 0; col < board.getLength(); col++) {
        if (board.getCell(row, col) == 0 && domains[row][col].size() == 1) {
          board.setCell(row, col, domains[row][col].iterator().next());
        }
      }
    }

    List<Integer>[][] domainList = new List[board.getLength()][board.getLength()];
    for (int row = 0; row < board.getLength(); row++) {
      for (int col = 0; col < board.getLength(); col++) {
        domainList[row][col] = new ArrayList<>(domains[row][col]);
      }
    }

    if (!recursiveSolver(board, domainList)) {
      throw new UnsolvableSudokuException("No solution can be found.");
    }
    return true;
  }

  /**
   * Backtracking solver that selects the cell with the fewest candidates first.
   */
  private boolean recursiveSolver(SudokuBoard board, List<Integer>[][] domains) {
    int[] cell = findMRV(board, domains);
    if (cell == null) {
      return true;
    }
    int x = cell[0];
    int y = cell[1];

    List<Integer> updatedDomains = filterDomains(board, domains[x][y], x, y);
    for (int value : updatedDomains) {
      nodesVisited++;
      board.setCell(x, y, value);
      if (recursiveSolver(board, domains)) {
        return true;
      }
      board.setCell(x, y, 0);
      backtracks++;
    }
    return false;
  }


  /**
   * Finds the empty cell with the smallest domain.
   *
   * @return coordinates as {row, column}, or null if the board is complete.
   */
  private int[] findMRV(SudokuBoard board, List<Integer>[][] domains) {
    int[] mrv = null;
    int smallestDomain = BOARD_SIZE + 1;
    for (int x = 0; x < BOARD_SIZE; x++) {
      for (int y = 0; y < BOARD_SIZE; y++) {
        if (board.getCell(x, y) == 0) {
          int domainSize = filterDomains(board, domains[x][y], x, y).size();
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
   * Filters a cell's domain to only values that satisfy row, column and sub-grid constraints.
   */
  private List<Integer> filterDomains(SudokuBoard board, List<Integer> domains, int x, int y) {
    List<Integer> filtered = new ArrayList<>();
    for (int value : domains) {
      if (checkDuplicate(value, board.getRow(x))
          && checkDuplicate(value, board.getColumn(y))
          && checkDuplicate(value, board.getSubGrid(x, y))) {
        filtered.add(value);
      }
    }
    return filtered;
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
