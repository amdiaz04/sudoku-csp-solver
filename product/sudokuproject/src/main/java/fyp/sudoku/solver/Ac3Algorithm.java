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
 * Solver that runs AC-3 as a preprocessing step to prune domains, then hands off to backtracking
 * for any remaining empty cells.
 */
public class Ac3Algorithm implements SudokuAlgorithm {

  private int nodesVisited = 0;
  private int backtracks = 0;

  @Override
  public boolean solve(SudokuBoard board) throws UnsolvableSudokuException {
    backtracks = 0;
    nodesVisited = 0;
    Set<Integer>[][] domains = new Set[board.getLength()][board.getLength()];

    Ac3Operations.setDomains(board, domains);
    if (!Ac3Operations.queueArcs(domains)) {
      throw new UnsolvableSudokuException("Empty domain was found");
    }

    for (int row = 0; row < board.getLength(); row++) {
      for (int column = 0; column < board.getLength(); column++) {
        if (board.getCell(row, column) == 0 && domains[row][column].size() == 1) {
          board.setCell(row, column, domains[row][column].iterator().next());
        }
      }
    }
    boolean complete = true;
    for (int row = 0; row < board.getLength(); row++) {
      for (int column = 0; column < board.getLength(); column++) {
        if (board.getCell(row, column) == 0) {
          complete = false;
          break;
        }
      }
    }
    if (!complete) {
      BacktrackingRecursiveAlgorithm backtracking = new BacktrackingRecursiveAlgorithm();
      backtracking.solve(board);
      nodesVisited = backtracking.getNodesVisited();
      backtracks = backtracking.getBacktracks();
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

