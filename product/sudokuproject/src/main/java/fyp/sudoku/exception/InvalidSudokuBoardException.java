package fyp.sudoku.exception;

/**
 * Exception thrown when Sudoku board is invalid.
 */
public class InvalidSudokuBoardException extends RuntimeException {

  /**
   * Creates a new InvalidSudokuBoardException.
   *
   * @param message custom message regarding the error.
   */
  public InvalidSudokuBoardException(String message) {
    super(message);
  }
}