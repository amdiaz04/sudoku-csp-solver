package fyp.sudoku.util;

import fyp.sudoku.exception.SudokuReadingException;
import fyp.sudoku.model.SudokuBoard;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for parsing Sudoku boards files from BufferReader.
 */
public final class SudokuReader {

  private static final int SUDOKU_SIZE = 9;

  private SudokuReader() {
  }

  /**
   * Reads a grid from a file: each line of the file represents a row. Puts each line it into a 2d
   * array and represents it as a {@link SudokuBoard}.
   *
   * @param reader the file/source of the input.
   * @return parsed {@link SudokuBoard}
   * @throws SudokuReadingException if the input isn't in the correct format or there's failure.
   */
  public static SudokuBoard readSinglePuzzle(BufferedReader reader)
      throws SudokuReadingException, IOException {
    int[][] grid = new int[SUDOKU_SIZE][SUDOKU_SIZE];

    try {
      String line = reader.readLine();
      if (line == null) {
        throw new SudokuReadingException("File is empty");
      }
      line = line.trim();
      if (line.length() == 81) {
        line = line.replace('.', '0');
        for (int row = 0; row < SUDOKU_SIZE; row++) {
          String rowString = line.substring(row * 9, (row + 1) * 9);
          addRow(grid, row, rowString);
        }
      } else {
        throw new SudokuReadingException("Invalid format");
      }

    } catch (IOException e) {
      throw new SudokuReadingException("File could not be read ");
    }
    return new SudokuBoard(grid);
  }

  public static List<SudokuBoard> readMultiplePuzzles(BufferedReader reader)
      throws IOException, SudokuReadingException {
    List<SudokuBoard> boards = new ArrayList<>();
    String line;
    while ((line = reader.readLine()) != null) {
      line = line.trim();
      BufferedReader lineReader = new BufferedReader(new StringReader(line));
      boards.add(readSinglePuzzle(lineReader));
    }
    if (boards.isEmpty()) {
      throw new SudokuReadingException("No puzzles found in file");
    }
    return boards;
  }

  /**
   * Validates each line then copies it into the grid.
   *
   * @param grid     potential Sudoku grid.
   * @param rowIndex index of row being populated.
   * @param line     the row from source.
   * @throws SudokuReadingException if length or characters are invalid
   */
  public static void addRow(int[][] grid, int rowIndex, String line)
      throws SudokuReadingException {
    // Whether the line has 9 characters
    if (line.length() != SUDOKU_SIZE) {
      throw new SudokuReadingException(
          "Invalid line length: " + line.length() + ". Must be equal to: " + SUDOKU_SIZE);
    }
    // If the character is valid
    for (int column = 0; column < SUDOKU_SIZE; column++) {
      char c = line.charAt(column);
      // Outside the bounds
      if (c < '0' || c > '9') {
        throw new SudokuReadingException(
            "Invalid character: " + c + "Must be integer between 1 - " + SUDOKU_SIZE);
      }
      grid[rowIndex][column] = c - '0';
    }
  }
}