package fyp.sudoku;

import fyp.sudoku.model.SudokuBoard;
import fyp.sudoku.util.SudokuReader;
import fyp.sudoku.exception.SudokuReadingException;
import java.io.IOException;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.StringReader;

/**
 * Tests for {@link SudokuReader}.
 */
public class SudokuReaderTest {

  @Test
  public void testReadValidBoardZeros() throws Exception {
    String inputData = "1" + "0".repeat(79) + "2";

    BufferedReader reader = new BufferedReader(new StringReader(inputData));
    SudokuBoard board = SudokuReader.readSinglePuzzle(reader);
    assertNotNull(board);

    assertEquals(1, board.getCell(0, 0));
    assertEquals(2, board.getCell(8, 8));
  }

  @Test
  public void testReadValidBoardPeriod() throws Exception {
    String inputData = "1" + ".".repeat(79) + "2";

    BufferedReader reader = new BufferedReader(new StringReader(inputData));
    SudokuBoard board = SudokuReader.readSinglePuzzle(reader);
    assertNotNull(board);

    assertEquals(0, board.getCell(0, 1));
    assertEquals(1, board.getCell(0, 0));
    assertEquals(2, board.getCell(8, 8));
  }

  @Test
  public void testReadMultipleBoards() throws SudokuReadingException, IOException {
    String inputData = "1" + "0".repeat(79) + "2\n" +
        "5" + "0".repeat(79) + "9\n" +
        "8" + "0".repeat(79) + "7";

    BufferedReader reader = new BufferedReader(new StringReader(inputData));
    List<SudokuBoard> boards = SudokuReader.readMultiplePuzzles(reader);

    assertEquals(1, boards.get(0).getCell(0, 0));
    assertEquals(2, boards.get(0).getCell(8, 8));

    assertEquals(5, boards.get(1).getCell(0, 0));
    assertEquals(9, boards.get(1).getCell(8, 8));

    assertEquals(8, boards.get(2).getCell(0, 0));
    assertEquals(7, boards.get(2).getCell(8, 8));
  }

  @Test
  public void testAddRowValid() throws SudokuReadingException {
    int[][] grid = new int[9][9];
    String line = "123456789";

    SudokuReader.addRow(grid, 0, line);

    assertEquals(1, grid[0][0]);
    assertEquals(5, grid[0][4]);
    assertEquals(9, grid[0][8]);
  }


  @Test(expected = SudokuReadingException.class)
  public void testReadInvalidGridSize() throws SudokuReadingException {
    int[][] grid = new int[9][9];
    String line = "1234X6789"; // X is invalid

    SudokuReader.addRow(grid, 0, line);
  }

  @Test(expected = SudokuReadingException.class)
  public void testReadInvalidCells() throws SudokuReadingException {
    int[][] grid = new int[9][9];
    String line = "12345678"; // only 8 characters

    SudokuReader.addRow(grid, 0, line);
  }

}
