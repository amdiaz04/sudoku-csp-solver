package fyp.sudoku.model;

import fyp.sudoku.exception.InvalidSudokuBoardException;
import java.util.Arrays;

/**
 * Representation of a 9×9 Sudoku grid.
 */
public class SudokuBoard {

  private static final int SUDOKU_SIZE = 9;
  private final int[][] grid;


  /**
   * Creates a Sudoku board from a pre-populated 9x9 matrix.
   *
   * @param grid a 2D array representing the Sudoku grid.
   * @throws InvalidSudokuBoardException if the grid is null or not the correct size.
   */
  public SudokuBoard(int[][] grid) {
    if (grid == null) {
      throw new InvalidSudokuBoardException("Grid cannot be null.");
    }

    // Checks number of rows.
    if (grid.length != SUDOKU_SIZE) {
      throw new InvalidSudokuBoardException("Grid must have 9 rows");
    }

    // Checks the length of each row.
    for (int i = 0; i < SUDOKU_SIZE; i++) {
      if (grid[i].length != SUDOKU_SIZE) {
        throw new InvalidSudokuBoardException("Each row must have 9 columns");
      }
    }
    this.grid = grid;
  }

  /**
   * Returns the value stored at a specific cell.
   *
   * @return value at the given cell
   */
  public int getCell(int x, int y) {
    return grid[x][y];
  }

  /**
   * Returns the values store at a specific row.
   *
   * @return array containing row values.
   */
  public int[] getRow(int x) {
    return Arrays.copyOf(grid[x], SUDOKU_SIZE);
  }

  /**
   * Returns the values store at a specific column.
   *
   * @return array containing column values.
   */
  public int[] getColumn(int y) {
    int[] column = new int[SUDOKU_SIZE];
    for (int x = 0; x < SUDOKU_SIZE; x++) {
      column[x] = grid[x][y];
    }
    return column;
  }

  /**
   * Returns the index of the subgrid at a specific cell.
   *
   * @return index of the subgrid
   */
  public int getSubGridIndex(int x, int y) {
    // Integer division
    int rowSection = x / 3;
    int columnSection = y / 3;
    // Subgrid = 3x + y
    return 3 * rowSection + columnSection;
  }

  /**
   * Returns the values of the subgrid at a specific cell.
   *
   * @return array of values in the subgrid.
   */
  public int[] getSubGrid(int x, int y) {
    x = 3 * (x / 3);
    y = 3 * (y / 3);
    int subgridIndex = 0;
    int[] subgrid = new int[SUDOKU_SIZE];

    for (int row = x; row < x + 3; row++) {
      for (int column = y; column < y + 3; column++) {
        subgrid[subgridIndex] = grid[row][column];
        subgridIndex++;
      }
    }
    return subgrid;
  }

  /**
   * Returns the grid of the Sudoku board.
   *
   * @return copied grid in an array.
   */
  public int[][] getGrid() {
    int[][] copy = new int[SUDOKU_SIZE][SUDOKU_SIZE];
    for (int i = 0; i < SUDOKU_SIZE; i++) {
      System.arraycopy(grid[i], 0, copy[i], 0, 9);
    }
    return copy;
  }

  /**
   * Returns the size of the board.
   *
   * @return number of rows (9)
   */
  public int getLength() {
    return grid.length;
  }

  /**
   * Writes a value into the grid.
   */
  public void setCell(int x, int y, int cellValue) {
    grid[x][y] = cellValue;
  }

  /**
   * Creates a deep copy of a SudokuBoard.
   *
   * @return a new SudokuBoard with the same values.
   */
  public SudokuBoard deepCopy() {
    int[][] newGrid = new int[SUDOKU_SIZE][SUDOKU_SIZE];
    for (int i = 0; i < SUDOKU_SIZE; i++) {
      System.arraycopy(this.grid[i], 0, newGrid[i], 0, SUDOKU_SIZE);
    }
    return new SudokuBoard(newGrid);
  }

}
