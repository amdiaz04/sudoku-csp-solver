package fyp.sudoku.model;

import fyp.sudoku.exception.UnsolvableSudokuException;
import fyp.sudoku.solver.BacktrackingRecursiveAlgorithm;
import fyp.sudoku.solver.SudokuAlgorithm;

/**
 * Represents a puzzle that stores the unsolved grid that can be solved by multiple algorithms.
 */
public class SudokuPuzzle {

  private Difficulty difficulty;
  private final SudokuBoard unsolvedPuzzle;
  private static final int EASY_BACKTRACKS = 100;

  private static final int MEDIUM_BACKTRACKS = 1000;

  private int backtracks;
  private int nodesVisited;


  /**
   * Creates a new Sudoku puzzle copy from an unsolved board.
   *
   * @param unsolvedPuzzle unsolved Sudoku board
   */
  public SudokuPuzzle(SudokuBoard unsolvedPuzzle) {
    this.unsolvedPuzzle = unsolvedPuzzle.deepCopy();
    this.difficulty = null;
    this.backtracks = 0;
    this.nodesVisited = 0;
  }

  /**
   * Returns a copy of the unsolved board.
   *
   * @return deep copy of the board.
   */
  public SudokuBoard getUnsolved() {
    return unsolvedPuzzle.deepCopy();
  }

  /**
   * Returns the difficulty of this puzzle. Useful if a puzzle isn't randomly generated (e.g., read
   * from a file).
   *
   * @return determined difficulty of this puzzle.
   * @throws UnsolvableSudokuException if the puzzle cannot be solved.
   */
  public Difficulty getDifficulty() throws UnsolvableSudokuException {
    if (this.difficulty == null) {
      this.difficulty = determineDifficulty(this.unsolvedPuzzle);
    }
    return this.difficulty;
  }

  /**
   * Returns the recent number of backtracks performed.
   */
  public int getBacktracks() {
    return backtracks;
  }

  /**
   * Returns the recent number of nodes visited.
   */
  public int getNodesVisited() {
    return nodesVisited;
  }

  /**
   * Returns the difficulty of the puzzle by analysing its performance metrics.
   *
   * @return determined difficulty of the puzzle.
   * @throws UnsolvableSudokuException if the puzzle cannot be solved.
   */
  public static Difficulty determineDifficulty(SudokuBoard board) throws UnsolvableSudokuException {
    SudokuBoard copy = board.deepCopy();
    BacktrackingRecursiveAlgorithm solver = new BacktrackingRecursiveAlgorithm();
    solver.solve(copy);

    int backtracks = solver.getBacktracks();

    if (backtracks < EASY_BACKTRACKS) {
      return Difficulty.EASY;
    } else if (backtracks < MEDIUM_BACKTRACKS) {
      return Difficulty.MEDIUM;
    } else {
      return Difficulty.HARD;
    }
  }

  /**
   * Solves the puzzle using the recursive backtracking algorithm.
   *
   * @return solved Sudoku board.
   * @throws UnsolvableSudokuException if the puzzle cannot be solved.
   */
  public SudokuBoard solve(SudokuAlgorithm algorithm) throws UnsolvableSudokuException {
    SudokuBoard copy = unsolvedPuzzle.deepCopy();
    algorithm.solve(copy);

    this.backtracks = algorithm.getBacktracks();
    this.nodesVisited = algorithm.getNodesVisited();
    return copy;
  }
}
