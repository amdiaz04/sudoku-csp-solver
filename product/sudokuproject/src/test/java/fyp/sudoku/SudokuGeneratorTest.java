package fyp.sudoku;

import fyp.sudoku.model.SudokuBoard;
import fyp.sudoku.util.SudokuGenerator;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link SudokuGenerator}.
 */
public class SudokuGeneratorTest {

  @Test
  public void testGenerateSudokuFilled() {
    SudokuGenerator generator = new SudokuGenerator();
    int[][] grid = generator.generateFilledGrid();
    assertEquals(9, grid.length);
    for (int[] row : grid) {
      assertEquals(9, row.length);
      for (int value : row) {
        assertTrue(value >= 1 && value <= 9);
      }
    }
    assertTrue(isSudokuValid(grid));
  }

  @Test
  public void testGenerateSudokuPuzzle() {
    SudokuBoard board = new SudokuGenerator().generatePuzzle();
    int[][] grid = board.getGrid();
    int emptyCellsCount = 0;
    for (int row = 0; row < 9; row++) {
      for (int column = 0; column < 9; column++) {
        if (grid[row][column] == 0) {
          emptyCellsCount++;
        }
      }
    }
    assertTrue(emptyCellsCount >= 30 && emptyCellsCount <= 60);
  }


  private boolean isSudokuValid(int[][] grid) {

    // Checks each row
    for (int row = 0; row < 9; row++) {
      boolean[] hasValue = new boolean[10];
      for (int column = 0; column < 9; column++) {
        int value = grid[row][column];
        if (hasValue[value]) {
          return false;
        }
        hasValue[value] = true;
      }
    }

    // Checks each column
    for (int column = 0; column < 9; column++) {
      boolean[] hasValue = new boolean[10];
      for (int row = 0; row < 9; row++) {
        int value = grid[row][column];
        if (hasValue[value]) {
          return false;
        }
        hasValue[value] = true;
      }
    }

    // Checks each subgrid
    for (int subgridRow = 0; subgridRow < 9; subgridRow += 3) {
      for (int subgridColumn = 0; subgridColumn < 9; subgridColumn += 3) {
        boolean[] hasValue = new boolean[10];
        for (int row = 0; row < 3; row++) {
          for (int column = 0; column < 3; column++) {
            int value = grid[subgridRow + row][subgridColumn + column];
            if (hasValue[value]) {
              return false;
            }
            hasValue[value] = true;
          }
        }
      }
    }
    return true;
  }

  @Test
  public void testPuzzleWithUniqueSolution() {
    SudokuGenerator generator = new SudokuGenerator();
    SudokuBoard board = generator.generatePuzzle();
    assertTrue(generator.hasUniqueness(board.getGrid()));
  }

  @Test
  public void testFilledGridWithOneCellRemovedIsUnique() {
    SudokuGenerator generator = new SudokuGenerator();
    int[][] grid = generator.generateFilledGrid();
    grid[0][0] = 0;
    assertTrue(generator.hasUniqueness(grid));
  }

  @Test
  public void testPuzzleIsNotUnique() {
    int[][] grid = { // puzzle with 16 clues is never unique
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 3, 0, 8, 5},
        {0, 0, 1, 0, 2, 0, 0, 0, 0},
        {0, 0, 0, 5, 0, 7, 0, 0, 0},
        {0, 0, 4, 0, 0, 0, 1, 0, 0},
        {0, 9, 0, 0, 0, 0, 0, 0, 0},
        {5, 0, 0, 0, 0, 0, 0, 7, 3},
        {0, 0, 2, 0, 1, 0, 0, 0, 0},
        {0, 0, 0, 0, 4, 0, 0, 0, 0}
    };
    SudokuGenerator generator = new SudokuGenerator();
    assertFalse(generator.hasUniqueness(grid));
  }

}
