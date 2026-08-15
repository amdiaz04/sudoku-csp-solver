package fyp.sudoku.sudoku_api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import fyp.sudoku.exception.UnsolvableSudokuException;
import fyp.sudoku.model.Difficulty;
import fyp.sudoku.model.SudokuBoard;
import fyp.sudoku.sudoku_api.types.CalcAvgResponse;
import java.io.BufferedReader;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link SudokuManager} verifying puzzle loading and average performance calculation.
 */
public class SudokuManagerTest {

  @Test
  public void testCalcAvg() throws UnsolvableSudokuException {
    SudokuManager manager = new SudokuManager();
    CalcAvgResponse results = manager.calcAvgPerformance(10, Difficulty.MEDIUM);
    assertNotNull(results);
    System.out.println(results);
    assertNotNull(results.avgBacktracks());
    assertNotNull(results.avgNodesVisited());
  }

  @Test
  public void testLoadFromUser() throws Exception {
    String inputData = "103000400000000000000000000000000000000000000000000000000000000000000000000000002";

    SudokuManager manager = new SudokuManager();
    BufferedReader reader = new BufferedReader(new StringReader(inputData));
    SudokuBoard board = manager.loadFromUser(reader);

    assertNotNull(board);
    System.out.println(board);
    assertEquals(3, board.getCell(0, 2));
    assertEquals(2, board.getCell(8, 8));
  }

}
