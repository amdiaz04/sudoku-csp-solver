package fyp.sudoku.solver;

import fyp.sudoku.exception.UnsolvableSudokuException;
import fyp.sudoku.model.SudokuBoard;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Combines MAC with MRV variable ordering. Selects the most constrained cell at each step and
 * enforces arc consistency after every assignment.
 */
public class MacMrvAlgorithm implements SudokuAlgorithm {

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
    if (!runMac(board, domains)) {
      throw new UnsolvableSudokuException("No solution");
    }
    return true;
  }

  /**
   * Recursive MAC search using MRV to select the next cell.
   */
  private boolean runMac(SudokuBoard board, Set<Integer>[][] domains) {
    int[] cell = findMRV(board, domains);

    if (cell == null) {
      return true;
    }

    int x = cell[0];
    int y = cell[1];

    // try each value in the domain
    List<Integer> assignments = new ArrayList<>(domains[x][y]);
    for (int value : assignments) {
      nodesVisited++;
      board.setCell(x, y, value);

      // save the domains incase the value doesn't work
      Set<Integer>[][] savedDomains = Ac3Operations.copyDomains(domains);

      // set the domain to size 1 with the new assignment only
      domains[x][y].clear();
      domains[x][y].add(value);

      // run AC-3 again and checks the value against all neighbours
      if (Ac3Operations.queueAssignmentArcs(domains, x, y)) {
        if (runMac(board, domains)) {
          return true;
        }
      }

      // the value is removed so the domains are restored and the next is tried
      board.setCell(x, y, 0);
      Ac3Operations.restoreDomains(domains, savedDomains);
      backtracks++;
    }
    return false;
  }

  /**
   * Finds the empty cell with the smallest domain.
   *
   * @return coordinates as {row, column}, or null if the board is complete.
   */
  private int[] findMRV(SudokuBoard board, Set<Integer>[][] domains) {
    int[] mrv = null;
    int smallestDomain = 10;
    for (int x = 0; x < BOARD_SIZE; x++) {
      for (int y = 0; y < BOARD_SIZE; y++) {
        if (board.getCell(x, y) == 0) {
          int domainSize = domains[x][y].size();
          if (domainSize < smallestDomain) {
            smallestDomain = domainSize;
            mrv = new int[]{x, y};
          }
        }
      }
    }
    return mrv;
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
