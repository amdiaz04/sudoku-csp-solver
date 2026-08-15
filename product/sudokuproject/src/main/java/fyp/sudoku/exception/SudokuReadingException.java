package fyp.sudoku.exception;

/**
 * Exception thrown when a Sudoku puzzle cannot be read.
 */
public class SudokuReadingException extends Exception {

  /**
   * Creates a new SudokuReadingException.
   *
   * @param message custom message regarding the error.
   */
  public SudokuReadingException(String message) {
    super(message);
  }
}
