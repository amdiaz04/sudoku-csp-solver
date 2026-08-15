package fyp.sudoku.exception;

/**
 * Exception thrown when a Sudoku puzzle cannot be solved.
 */
public class UnsolvableSudokuException extends Exception {

  /**
   * Creates a new UnsolvableSudokuException.
   *
   * @param message custom message regarding the error.
   */
  public UnsolvableSudokuException(String message) {
    super(message);
  }
}