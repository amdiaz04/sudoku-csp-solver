package fyp.sudoku.util;

import fyp.sudoku.model.Difficulty;
import fyp.sudoku.model.SudokuBoard;
import fyp.sudoku.model.SudokuPuzzle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Generates unsolved Sudoku puzzles. A complete board is generated first, then cells are removed.
 */
public class SudokuGenerator {

  private static final int SUDOKU_SIZE = 9;
  private final Random random = new Random();

  /**
   * Generates a new Sudoku puzzle.
   *
   * @return an unsolved SudokuBoard.
   */
  public SudokuBoard generatePuzzle() {
    // Generate a complete filled grid
    int[][] filledGrid = generateFilledGrid();
    int minCellsToRemove = 30;
    int maxCellsToRemove = 60;
    int cellsToRemove = random.nextInt(maxCellsToRemove - minCellsToRemove + 1) + minCellsToRemove;
    int[][] puzzle = removeCells(filledGrid, cellsToRemove);
    return new SudokuBoard(puzzle);
  }

  public SudokuBoard generatePuzzle(Difficulty targetDifficulty) {

    int minCellsToRemove;
    int maxCellsToRemove;

    switch (targetDifficulty) {
      case EASY:
        minCellsToRemove = 30;
        maxCellsToRemove = 40;
        break;
      case MEDIUM:
        minCellsToRemove = 41;
        maxCellsToRemove = 50;
        break;
      case HARD:
        minCellsToRemove = 51;
        maxCellsToRemove = 64;
        break;
      default:
        minCellsToRemove = 30;
        maxCellsToRemove = 60;
    }

    SudokuBoard board = null;
    Difficulty currentDifficulty;
    int attempts = 0;
    int maxAttempts = 25;

    do {
      attempts++;
      if (attempts > maxAttempts) {
        return board;
      }

      int[][] filledGrid = generateFilledGrid();
      int cellsToRemove =
          random.nextInt(maxCellsToRemove - minCellsToRemove + 1) + minCellsToRemove;
      int[][] puzzleGrid = removeCells(filledGrid, cellsToRemove);
      board = new SudokuBoard(puzzleGrid);

      try {
        currentDifficulty = SudokuPuzzle.determineDifficulty(board);
      } catch (Exception e) {
        currentDifficulty = null;
      }

    } while (currentDifficulty != targetDifficulty);

    return board;
  }

  /**
   * Removes the random number of cells from the completed grid.
   *
   * @param puzzle        the completed grid.
   * @param cellsToRemove number of cells to remove.
   * @return the puzzle with removed cells.
   */
  private int[][] removeCells(int[][] puzzle, int cellsToRemove) {
    int[][] copy = copyPuzzle(puzzle);
    int cellsRemoved = 0;

    List<int[]> positions = new ArrayList<>();
    for (int x = 0; x < SUDOKU_SIZE; x++) {
      for (int y = 0; y < SUDOKU_SIZE; y++) {
        positions.add(new int[]{x, y});
      }
    }
    Collections.shuffle(positions);

    for (int[] pos : positions) {
      if (cellsRemoved >= cellsToRemove) {
        break;
      }

      int row = pos[0];
      int col = pos[1];

      if (copy[row][col] == 0) {
        continue;
      }

      int temp = copy[row][col];
      copy[row][col] = 0;

      if (!hasUniqueness(copy)) {
        copy[row][col] = temp;
        continue;
      }
      cellsRemoved++;
    }
    return copy;
  }

  public boolean hasUniqueness(int[][] puzzle) {
    int[][] copy = copyPuzzle(puzzle);
    int[] count = {0};
    countSolutions(copy, count);
    return count[0] == 1;
  }

  /**
   * Generates a complete Sudoku grid.
   */
  public int[][] generateFilledGrid() {
    int[][] grid = new int[SUDOKU_SIZE][SUDOKU_SIZE];
    fillGrid(grid);
    return grid;
  }

  /**
   * Recursively fills the values for the grid using backtracking.
   */
  private boolean fillGrid(int[][] grid) {
    for (int row = 0; row < SUDOKU_SIZE; row++) {
      for (int column = 0; column < SUDOKU_SIZE; column++) {
        if (grid[row][column] == 0) {
          List<Integer> randomNumbers = new ArrayList<>();
          for (int n = 1; n <= 9; n++) {
            randomNumbers.add(n);
          }
          Collections.shuffle(randomNumbers, random);

          for (int value : randomNumbers) {
            if (isValid(grid, row, column, value)) {
              grid[row][column] = value;

              if (fillGrid(grid)) {
                return true;
              }

              grid[row][column] = 0;
            }
          }
          return false;
        }
      }
    }
    return true;
  }

  private void countSolutions(int[][] grid, int[] count) {
    for (int row = 0; row < SUDOKU_SIZE; row++) {
      for (int column = 0; column < SUDOKU_SIZE; column++) {
        if (grid[row][column] == 0) { // finds empty cell
          for (int value = 1; value < SUDOKU_SIZE + 1; value++) { // trys values 1-9
            if (isValid(grid, row, column, value)) {
              grid[row][column] = value; // assigns the value
              countSolutions(grid, count); // recurse

              if (count[0] > 1) { // more than one solution, stop
                grid[row][column] = 0;
                return;
              }
              grid[row][column] = 0; // reset cell, try next value
            }
          }
          return; // no value worked , back track
        }
      }
    }
    count[0]++; // board complete.
  }


  /**
   * Checks whether a value can be placed in a given cell without violating constraints.
   */
  private static boolean isValid(int[][] grid, int row, int column, int value) {
    // Check row
    for (int i = 0; i < SUDOKU_SIZE; i++) {
      if (grid[row][i] == value) {
        return false;
      }
    }

    // Check column
    for (int i = 0; i < SUDOKU_SIZE; i++) {
      if (grid[i][column] == value) {
        return false;
      }
    }

    // Check 3x3 box
    int x = (row / 3) * 3;
    int y = (column / 3) * 3;
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (grid[x + i][y + j] == value) {
          return false;
        }
      }
    }

    return true;
  }

  private int[][] copyPuzzle(int[][] puzzle) {
    int[][] copy = new int[SUDOKU_SIZE][SUDOKU_SIZE];
    for (int i = 0; i < SUDOKU_SIZE; i++) {
      System.arraycopy(puzzle[i], 0, copy[i], 0, SUDOKU_SIZE);
    }
    return copy;
  }


}
