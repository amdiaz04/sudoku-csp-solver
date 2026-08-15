package fyp.sudoku.solver;

import fyp.sudoku.model.SudokuBoard;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class containing shared AC-3 operations used by multiple solvers. Provides methods for
 * domain initialisation, neighbour lookup, arc revision and domain backup/restore.
 */
public class Ac3Operations {

  private static final int BOARD_SIZE = 9;

  /**
   * Initialises domains for each cell. Assigned cells get a single-value domain, empty cells get
   * {1-9}.
   *
   * @param board   the current board state.
   * @param domains 9x9 array of domain sets to populate.
   */
  public static void setDomains(SudokuBoard board, Set<Integer>[][] domains) {
    // loop every cell.
    for (int row = 0; row < BOARD_SIZE; row++) {
      for (int column = 0; column < BOARD_SIZE; column++) {
        Set<Integer> set = new HashSet<>(); // create the set of domains for that cell
        if (board.getCell(row, column)
            != 0) { // if the cell isn't empty only add value for the domain
          set.add(board.getCell(row, column));
        } else {
          for (int i = 1; i <= BOARD_SIZE; i++) {
            set.add(i); // otherwise add all numbers 1-9
          }
        }
        domains[row][column] = set; // add the set of domains for that coordinate
      }
    }
  }

  /**
   * Returns all cells that share a row, column or sub-grid with the given cell.
   *
   * @param row    row index of the cell.
   * @param column column index of the cell.
   * @return list of neighbour coordinates as {row, column} arrays.
   */
  public static List<int[]> getNeighbours(int row, int column) {
    List<int[]> nList = new ArrayList<>();
    boolean[][] hasSeen = new boolean[BOARD_SIZE][BOARD_SIZE];
    hasSeen[row][column] = true;    // remove duplicates

    // all cells in the same row
    for (int c = 0; c < BOARD_SIZE; c++) {
      if (!hasSeen[row][c]) {
        hasSeen[row][c] = true;
        nList.add(new int[]{row, c});
      }
    }

    // all cells in the same column
    for (int r = 0; r < BOARD_SIZE; r++) {
      if (!hasSeen[r][column]) {
        hasSeen[r][column] = true;
        nList.add(new int[]{r, column});
      }
    }

    // all cells in the same subgrid
    int subgridRow = (row / 3) * 3;
    int subgridColumn = (column / 3) * 3;
    for (int r = subgridRow; r < subgridRow + 3; r++) {
      for (int c = subgridColumn; c < subgridColumn + 3; c++) {
        if (!hasSeen[r][c]) {
          hasSeen[r][c] = true;
          nList.add(new int[]{r, c});
        }
      }
    }

    return nList;
  }

  /**
   * Removes values from the domain of cell (xRow, xColumn) that have no compatible value in the
   * domain of cell (yRow, yColumn).
   *
   * @return true if any value was removed from the domain.
   */
  public static boolean revise(Set<Integer>[][] domains, int xRow, int xColumn, int yRow,
      int yColumn) {
    // if x = y, remove the one that has no other choice.
    boolean hasChanged = false;

    Set<Integer> xDomain = domains[xRow][xColumn];
    Set<Integer> yDomain = domains[yRow][yColumn];
    Set<Integer> copy = new HashSet<>(xDomain);

    for (int x : copy) {
      boolean isCompatible = false;
      for (int y : yDomain) {
        if (x != y) { // if cell
          isCompatible = true;
          break;
        }
      }
      if (!isCompatible) {
        xDomain.remove(x);
        hasChanged = true;
      }
    }
    return hasChanged;
  }

  /**
   * Runs AC-3 by queuing all arcs in the grid and revising until its consistent.
   *
   * @param domains the current domain sets for each cell.
   * @return false if any domain becomes empty, true otherwise.
   */
  public static boolean queueArcs(Set<Integer>[][] domains) {

    Deque<int[]> queue = new ArrayDeque<>();

    for (int arcRow = 0; arcRow < BOARD_SIZE; arcRow++) {
      for (int arcColumn = 0; arcColumn < BOARD_SIZE; arcColumn++) {
        for (int[] neighbour : getNeighbours(arcRow, arcColumn)) {
          queue.addLast(new int[]{arcRow, arcColumn, neighbour[0], neighbour[1]});
        }
      }
    }
    while (!queue.isEmpty()) {
      int[] arc = queue.removeFirst();
      if (revise(domains, arc[0], arc[1], arc[2], arc[3])) {
        if (domains[arc[0]][arc[1]].isEmpty()) {
          return false;
        }
        for (int[] neighbour : getNeighbours(arc[0], arc[1])) {
          queue.addLast(new int[]{neighbour[0], neighbour[1], arc[0], arc[1]});
        }
      }
    }
    return true;
  }

  /**
   * Runs a focused AC-3, only queuing arcs connected to the given cell. Used by MAC to enforce
   * consistency after each assignment.
   *
   * @return false if any domain becomes empty, true otherwise.
   */
  public static boolean queueAssignmentArcs(Set<Integer>[][] domains, int x, int y) {
    Deque<int[]> queue = new ArrayDeque<>();

    // add the arcs only from the neighbours of the assignment
    for (int[] neighbour : getNeighbours(x, y)) {
      queue.addLast(new int[]{neighbour[0], neighbour[1], x, y});
    }

    while (!queue.isEmpty()) {
      int[] arc = queue.removeFirst();
      // remove inconsistencies
      if (revise(domains, arc[0], arc[1], arc[2], arc[3])) {

        // if domains are zero, backtrack later - return false
        if (domains[arc[0]][arc[1]].isEmpty()) {
          return false;
        }

        // if domain is changed, add arcs
        for (int[] n : getNeighbours(arc[0], arc[1])) {
          queue.addLast(new int[]{n[0], n[1], arc[0], arc[1]});
        }
      }
    }
    return true;
  }

  /**
   * Returns a deep copy of the domain array.
   */
  public static Set<Integer>[][] copyDomains(Set<Integer>[][] domains) {
    Set<Integer>[][] copy = new Set[BOARD_SIZE][BOARD_SIZE];

    for (int i = 0; i < BOARD_SIZE; i++) {
      for (int j = 0; j < BOARD_SIZE; j++) {
        copy[i][j] = new HashSet<>(domains[i][j]);
      }
    }
    return copy;
  }

  /**
   * Restores domains from a saved copy.
   */
  public static void restoreDomains(Set<Integer>[][] domains, Set<Integer>[][] saved) {
    for (int i = 0; i < BOARD_SIZE; i++) {
      for (int j = 0; j < BOARD_SIZE; j++) {
        domains[i][j] = new HashSet<>(saved[i][j]);
      }
    }
  }
}