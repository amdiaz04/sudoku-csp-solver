package fyp.sudoku.solver;

import fyp.sudoku.exception.UnsolvableSudokuException;
import fyp.sudoku.model.SudokuBoard;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Maintaining Arc Consistency solver. Runs AC-3 after every assignment during search to prune
 * domains continuously.
 */
public class MacAlgorithm implements SudokuAlgorithm {

  private int nodesVisited = 0;
  private int backtracks = 0;
  private static final int BOARD_SIZE = 9;

  @Override
  public boolean solve(SudokuBoard board) throws UnsolvableSudokuException {
    backtracks = 0;
    nodesVisited = 0;

    Set<Integer>[][] domains = new Set[board.getLength()][board.getLength()];
    Ac3Operations.setDomains(board, domains);

    // same as `Ac3Algorithm` runs once before search and fill any safe cells
    if (!Ac3Operations.queueArcs(domains)) {
      throw new UnsolvableSudokuException("No solution");
    }
    for (int row = 0; row < board.getLength(); row++) {
      for (int column = 0; column < board.getLength(); column++) {
        if (board.getCell(row, column) == 0 && domains[row][column].size() == 1) {
          board.setCell(row, column, domains[row][column].iterator().next());
        }
      }
    }

    // start MAC
    if (!runMac(board, domains)) {
      throw new UnsolvableSudokuException("No solution");
    }

    return true;
  }

  /**
   * Recursive MAC search. Selects the first empty cell, tries each value in its domain and enforces
   * arc consistency after each assignment.
   */
  private boolean runMac(SudokuBoard board, Set<Integer>[][] domains) {
    int[] emptyCell = null;

    // find first empty cell
    outer:
    for (int x = 0; x < BOARD_SIZE; x++) {
      for (int y = 0; y < BOARD_SIZE; y++) {
        if (board.getCell(x, y) == 0) {
          emptyCell = new int[]{x, y};
          break outer;
        }
      }
    }

    if (emptyCell == null) {
      return true;
    }

    int x = emptyCell[0];
    int y = emptyCell[1];

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


  @Override
  public int getBacktracks() {
    return backtracks;
  }

  @Override
  public int getNodesVisited() {
    return nodesVisited;
  }

}

