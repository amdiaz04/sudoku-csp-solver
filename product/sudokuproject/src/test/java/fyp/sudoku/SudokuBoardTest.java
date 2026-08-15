package fyp.sudoku;

import fyp.sudoku.exception.InvalidSudokuBoardException;
import fyp.sudoku.model.SudokuBoard;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link SudokuBoard}.
 */
public class SudokuBoardTest {

  @Test
  public void testInitialiseGridNull() {
    assertThrows(InvalidSudokuBoardException.class, () -> new SudokuBoard(null));
  }

  @Test
  public void testInitialiseGridWrongSize() {
    int[][] grid = new int[8][9];
    int[][] grid2 = new int[8][10];

    // Right size but one row is too short
    int[][] grid3 = new int[9][9];
    grid3[4] = new int[4];
    assertThrows(InvalidSudokuBoardException.class, () -> new SudokuBoard(grid));
    assertThrows(InvalidSudokuBoardException.class, () -> new SudokuBoard(grid2));
    assertThrows(InvalidSudokuBoardException.class, () -> new SudokuBoard(grid3));
  }


  @Test
  public void testGetCellValid() {
    int[][] grid = {
        {2, 0, 0, 0, 7, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 2, 7, 3, 4},
        {4, 6, 7, 0, 0, 8, 0, 0, 9},
        {0, 0, 0, 9, 1, 0, 0, 0, 8},
        {0, 1, 0, 0, 8, 7, 0, 0, 0},
        {0, 8, 6, 2, 5, 4, 1, 0, 7},
        {0, 0, 8, 3, 4, 0, 0, 2, 0},
        {9, 4, 0, 0, 2, 0, 8, 5, 0},
        {6, 5, 0, 0, 9, 0, 4, 0, 0}
    };
    SudokuBoard board = new SudokuBoard(grid);

    assertEquals(2, board.getCell(0, 0));
    assertEquals(4, board.getCell(2, 0));
    assertEquals(8, board.getCell(4, 4));
    assertEquals(2, board.getCell(6, 7));
  }

  @Test
  public void testGetCellOutOfBounds() {
    int[][] grid = {
        {2, 0, 0, 0, 7, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 2, 7, 3, 4},
        {4, 6, 7, 0, 0, 8, 0, 0, 9},
        {0, 0, 0, 9, 1, 0, 0, 0, 8},
        {0, 1, 0, 0, 8, 7, 0, 0, 0},
        {0, 8, 6, 2, 5, 4, 1, 0, 7},
        {0, 0, 8, 3, 4, 0, 0, 2, 0},
        {9, 4, 0, 0, 2, 0, 8, 5, 0},
        {6, 5, 0, 0, 9, 0, 4, 0, 0}
    };
    SudokuBoard board = new SudokuBoard(grid);
    assertThrows(IndexOutOfBoundsException.class, () -> board.getCell(0, 9));
  }

  @Test
  public void testGetRowValid() {
    int[][] grid = {
        {2, 0, 0, 0, 7, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 2, 7, 3, 4},
        {4, 6, 7, 0, 0, 8, 0, 0, 9},
        {0, 0, 0, 9, 1, 0, 0, 0, 8},
        {0, 1, 0, 0, 8, 7, 0, 0, 0},
        {0, 8, 6, 2, 5, 4, 1, 0, 7},
        {0, 0, 8, 3, 4, 0, 0, 2, 0},
        {9, 4, 0, 0, 2, 0, 8, 5, 0},
        {6, 5, 0, 0, 9, 0, 4, 0, 0}
    };
    SudokuBoard board = new SudokuBoard(grid);
    assertArrayEquals(new int[]{2, 0, 0, 0, 7, 0, 0, 0, 0}, board.getRow(0));
    assertArrayEquals(new int[]{4, 6, 7, 0, 0, 8, 0, 0, 9}, board.getRow(2));
    assertArrayEquals(new int[]{0, 0, 8, 3, 4, 0, 0, 2, 0}, board.getRow(6));
    assertArrayEquals(new int[]{6, 5, 0, 0, 9, 0, 4, 0, 0}, board.getRow(8));
  }

  @Test
  public void testGetRowOutOfBounds() {
    int[][] grid = {
        {2, 0, 0, 0, 7, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 2, 7, 3, 4},
        {4, 6, 7, 0, 0, 8, 0, 0, 9},
        {0, 0, 0, 9, 1, 0, 0, 0, 8},
        {0, 1, 0, 0, 8, 7, 0, 0, 0},
        {0, 8, 6, 2, 5, 4, 1, 0, 7},
        {0, 0, 8, 3, 4, 0, 0, 2, 0},
        {9, 4, 0, 0, 2, 0, 8, 5, 0},
        {6, 5, 0, 0, 9, 0, 4, 0, 0}
    };
    SudokuBoard board = new SudokuBoard(grid);
    assertThrows(IndexOutOfBoundsException.class, () -> board.getRow(9));
  }

  @Test
  public void testGetColumnValid() {
    int[][] grid = {
        {2, 0, 0, 0, 7, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 2, 7, 3, 4},
        {4, 6, 7, 0, 0, 8, 0, 0, 9},
        {0, 0, 0, 9, 1, 0, 0, 0, 8},
        {0, 1, 0, 0, 8, 7, 0, 0, 0},
        {0, 8, 6, 2, 5, 4, 1, 0, 7},
        {0, 0, 8, 3, 4, 0, 0, 2, 0},
        {9, 4, 0, 0, 2, 0, 8, 5, 0},
        {6, 5, 0, 0, 9, 0, 4, 0, 0}
    };
    SudokuBoard board = new SudokuBoard(grid);
    assertArrayEquals(new int[]{2, 0, 4, 0, 0, 0, 0, 9, 6}, board.getColumn(0));
    assertArrayEquals(new int[]{7, 0, 0, 1, 8, 5, 4, 2, 9}, board.getColumn(4));
    assertArrayEquals(new int[]{0, 4, 9, 8, 0, 7, 0, 0, 0}, board.getColumn(8));
  }

  @Test
  public void testGetColumnOutOfBounds() {
    int[][] grid = {
        {2, 0, 0, 0, 7, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 2, 7, 3, 4},
        {4, 6, 7, 0, 0, 8, 0, 0, 9},
        {0, 0, 0, 9, 1, 0, 0, 0, 8},
        {0, 1, 0, 0, 8, 7, 0, 0, 0},
        {0, 8, 6, 2, 5, 4, 1, 0, 7},
        {0, 0, 8, 3, 4, 0, 0, 2, 0},
        {9, 4, 0, 0, 2, 0, 8, 5, 0},
        {6, 5, 0, 0, 9, 0, 4, 0, 0}
    };
    SudokuBoard board = new SudokuBoard(grid);
    assertThrows(IndexOutOfBoundsException.class, () -> board.getColumn(9));
  }

  @Test
  public void testGetSubGridIndex() {
    int[][] grid = {
        {2, 0, 0, 0, 7, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 2, 7, 3, 4},
        {4, 6, 7, 0, 0, 8, 0, 0, 9},
        {0, 0, 0, 9, 1, 0, 0, 0, 8},
        {0, 1, 0, 0, 8, 7, 0, 0, 0},
        {0, 8, 6, 2, 5, 4, 1, 0, 7},
        {0, 0, 8, 3, 4, 0, 0, 2, 0},
        {9, 4, 0, 0, 2, 0, 8, 5, 0},
        {6, 5, 0, 0, 9, 0, 4, 0, 0}
    };
    SudokuBoard board = new SudokuBoard(grid);
    assertEquals(5, board.getSubGridIndex(5, 6));
    assertEquals(1, board.getSubGridIndex(2, 4));
    assertEquals(7, board.getSubGridIndex(7, 5));

  }

  @Test
  public void testGetSubGrid() {
    int[][] grid = {
        {2, 0, 0, 0, 7, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 2, 7, 3, 4},
        {4, 6, 7, 0, 0, 8, 0, 0, 9},
        {0, 0, 0, 9, 1, 0, 0, 0, 8},
        {0, 1, 0, 0, 8, 7, 0, 0, 0},
        {0, 8, 6, 2, 5, 4, 1, 0, 7},
        {0, 0, 8, 3, 4, 0, 0, 2, 0},
        {9, 4, 0, 0, 2, 0, 8, 5, 0},
        {6, 5, 0, 0, 9, 0, 4, 0, 0}
    };
    SudokuBoard board = new SudokuBoard(grid);
    int[] expected = {2, 0, 0, 0, 0, 0, 4, 6, 7};
    // Same subgrid
    assertArrayEquals(expected, board.getSubGrid(1, 1));
    assertArrayEquals(expected, board.getSubGrid(0, 2));
    assertArrayEquals(expected, board.getSubGrid(2, 0));
    assertArrayEquals(expected, board.getSubGrid(2, 2));

  }

  @Test
  public void testGetSubGridOutOfBounds() {
    int[][] grid = {
        {2, 0, 0, 0, 7, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 2, 7, 3, 4},
        {4, 6, 7, 0, 0, 8, 0, 0, 9},
        {0, 0, 0, 9, 1, 0, 0, 0, 8},
        {0, 1, 0, 0, 8, 7, 0, 0, 0},
        {0, 8, 6, 2, 5, 4, 1, 0, 7},
        {0, 0, 8, 3, 4, 0, 0, 2, 0},
        {9, 4, 0, 0, 2, 0, 8, 5, 0},
        {6, 5, 0, 0, 9, 0, 4, 0, 0}
    };
    SudokuBoard board = new SudokuBoard(grid);
    assertThrows(IndexOutOfBoundsException.class, () -> board.getSubGrid(9, 0));
  }

  @Test
  public void testGetGrid() {
    int[][] grid = {
        {2, 0, 0, 0, 7, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 2, 7, 3, 4},
        {4, 6, 7, 0, 0, 8, 0, 0, 9},
        {0, 0, 0, 9, 1, 0, 0, 0, 8},
        {0, 1, 0, 0, 8, 7, 0, 0, 0},
        {0, 8, 6, 2, 5, 4, 1, 0, 7},
        {0, 0, 8, 3, 4, 0, 0, 2, 0},
        {9, 4, 0, 0, 2, 0, 8, 5, 0},
        {6, 5, 0, 0, 9, 0, 4, 0, 0}
    };

    SudokuBoard board = new SudokuBoard(grid);
    int[][] gridCopy = board.getGrid();
    gridCopy[0][0] = 4;

    assertEquals(2, board.getCell(0, 0));
  }


  @Test
  public void testDeepCopy() {
    int[][] grid = {
        {2, 0, 0, 0, 7, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 2, 7, 3, 4},
        {4, 6, 7, 0, 0, 8, 0, 0, 9},
        {0, 0, 0, 9, 1, 0, 0, 0, 8},
        {0, 1, 0, 0, 8, 7, 0, 0, 0},
        {0, 8, 6, 2, 5, 4, 1, 0, 7},
        {0, 0, 8, 3, 4, 0, 0, 2, 0},
        {9, 4, 0, 0, 2, 0, 8, 5, 0},
        {6, 5, 0, 0, 9, 0, 4, 0, 0}
    };

    SudokuBoard board = new SudokuBoard(grid);
    SudokuBoard boardCopy = board.deepCopy();
    boardCopy.setCell(0, 0, 6);
    assertEquals(2, board.getCell(0, 0));
    assertEquals(6, boardCopy.getCell(0, 0));
    assertNotSame(board.getGrid(), boardCopy.getGrid());
  }


}
