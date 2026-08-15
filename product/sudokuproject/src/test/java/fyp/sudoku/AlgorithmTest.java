package fyp.sudoku;

import fyp.sudoku.exception.UnsolvableSudokuException;
import fyp.sudoku.model.SudokuBoard;
import fyp.sudoku.solver.Ac3Algorithm;
import fyp.sudoku.solver.SudokuAlgorithm;
import fyp.sudoku.util.SudokuGenerator;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Shared test helper containing reusable test cases for all solver implementations. Each algorithm
 * test class calls these methods to avoid duplicating test logic.
 */
public class AlgorithmTest {

  public static void testSolveValidBoardEasy(SudokuAlgorithm algorithm)
      throws UnsolvableSudokuException {
    int[][] grid = {
        {2, 0, 1, 4, 7, 9, 6, 8, 5},
        {8, 9, 5, 1, 6, 2, 7, 3, 4},
        {4, 6, 7, 5, 3, 8, 2, 0, 9},
        {7, 2, 4, 9, 1, 3, 5, 6, 8},
        {5, 1, 9, 6, 0, 7, 3, 4, 2},
        {3, 8, 6, 2, 5, 4, 1, 9, 7},
        {1, 7, 8, 3, 4, 5, 9, 2, 6},
        {9, 4, 3, 7, 2, 6, 8, 5, 0},
        {6, 5, 2, 8, 9, 1, 4, 7, 3}
    };

    int[][] expected = {
        {2, 3, 1, 4, 7, 9, 6, 8, 5},
        {8, 9, 5, 1, 6, 2, 7, 3, 4},
        {4, 6, 7, 5, 3, 8, 2, 1, 9},
        {7, 2, 4, 9, 1, 3, 5, 6, 8},
        {5, 1, 9, 6, 8, 7, 3, 4, 2},
        {3, 8, 6, 2, 5, 4, 1, 9, 7},
        {1, 7, 8, 3, 4, 5, 9, 2, 6},
        {9, 4, 3, 7, 2, 6, 8, 5, 1},
        {6, 5, 2, 8, 9, 1, 4, 7, 3}
    };

    SudokuBoard board = new SudokuBoard(grid);
    assertTrue(algorithm.solve(board));
    assertArrayEquals(expected, board.getGrid());
  }

  public static void testSolveValidBoardMedium(SudokuAlgorithm algorithm)
      throws UnsolvableSudokuException {
    int[][] grid = {
        {5, 3, 0, 0, 7, 0, 0, 0, 0},
        {6, 0, 0, 1, 9, 5, 0, 0, 0},
        {0, 9, 8, 0, 0, 0, 0, 6, 0},
        {8, 0, 0, 0, 6, 0, 0, 0, 3},
        {4, 0, 0, 8, 0, 3, 0, 0, 1},
        {7, 0, 0, 0, 2, 0, 0, 0, 6},
        {0, 6, 0, 0, 0, 0, 2, 8, 0},
        {0, 0, 0, 4, 1, 9, 0, 0, 5},
        {0, 0, 0, 0, 8, 0, 0, 7, 9}
    };

    int[][] expected = {
        {5, 3, 4, 6, 7, 8, 9, 1, 2},
        {6, 7, 2, 1, 9, 5, 3, 4, 8},
        {1, 9, 8, 3, 4, 2, 5, 6, 7},
        {8, 5, 9, 7, 6, 1, 4, 2, 3},
        {4, 2, 6, 8, 5, 3, 7, 9, 1},
        {7, 1, 3, 9, 2, 4, 8, 5, 6},
        {9, 6, 1, 5, 3, 7, 2, 8, 4},
        {2, 8, 7, 4, 1, 9, 6, 3, 5},
        {3, 4, 5, 2, 8, 6, 1, 7, 9}
    };

    SudokuBoard board = new SudokuBoard(grid);
    assertTrue(algorithm.solve(board));
    assertArrayEquals(expected, board.getGrid());
  }

  public static void testSolveValidBoardHard(SudokuAlgorithm algorithm)
      throws UnsolvableSudokuException {

    int[][] grid = {
        {3, 0, 0, 0, 8, 0, 0, 0, 0},
        {0, 0, 0, 7, 0, 0, 0, 0, 5},
        {1, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 3, 6, 0},
        {0, 0, 2, 0, 0, 4, 0, 0, 0},
        {0, 7, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 6, 0, 1, 3, 0},
        {0, 4, 5, 2, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 8, 0, 0}
    };

    int[][] expected = {
        {3, 5, 4, 1, 8, 6, 9, 2, 7},
        {2, 9, 8, 7, 4, 3, 6, 1, 5},
        {1, 6, 7, 9, 5, 2, 4, 8, 3},
        {4, 8, 1, 5, 2, 7, 3, 6, 9},
        {9, 3, 2, 6, 1, 4, 5, 7, 8},
        {5, 7, 6, 3, 9, 8, 2, 4, 1},
        {7, 2, 9, 8, 6, 5, 1, 3, 4},
        {8, 4, 5, 2, 3, 1, 7, 9, 6},
        {6, 1, 3, 4, 7, 9, 8, 5, 2}
    };

    SudokuBoard board = new SudokuBoard(grid);
    assertTrue(algorithm.solve(board));
    assertArrayEquals(expected, board.getGrid());
  }

  public static void testSolveInvalidBoard(SudokuAlgorithm algorithm) {
    int[][] grid = {
        {0, 0, 0, 0, 0, 6, 0, 0, 0},
        {0, 1, 0, 0, 0, 0, 2, 0, 0},
        {0, 0, 8, 0, 7, 0, 0, 9, 0},
        {0, 0, 0, 6, 0, 8, 0, 0, 3},
        {0, 6, 0, 0, 0, 0, 0, 5, 0},
        {9, 0, 0, 4, 0, 8, 0, 0, 0},
        {0, 4, 1, 0, 1, 0, 7, 0, 0},
        {0, 0, 6, 0, 0, 0, 7, 0, 0},
        {0, 0, 0, 7, 0, 0, 0, 0, 0}
    };

    SudokuBoard board = new SudokuBoard(grid);
    assertThrows(UnsolvableSudokuException.class, () -> algorithm.solve(board));
  }

  public static void testSolveCompleteBoard(SudokuAlgorithm algorithm)
      throws UnsolvableSudokuException {

    int[][] grid = {
        {2, 3, 1, 4, 7, 9, 6, 8, 5},
        {8, 9, 5, 1, 6, 2, 7, 3, 4},
        {4, 6, 7, 5, 3, 8, 2, 1, 9},
        {7, 2, 4, 9, 1, 3, 5, 6, 8},
        {5, 1, 9, 6, 8, 7, 3, 4, 2},
        {3, 8, 6, 2, 5, 4, 1, 9, 7},
        {1, 7, 8, 3, 4, 5, 9, 2, 6},
        {9, 4, 3, 7, 2, 6, 8, 5, 1},
        {6, 5, 2, 8, 9, 1, 4, 7, 3}
    };

    SudokuBoard board = new SudokuBoard(grid);
    assertTrue(algorithm.solve(board));
  }

  public static void testGeneratedBoard(SudokuAlgorithm algorithm)
      throws UnsolvableSudokuException {
    SudokuBoard board = new SudokuGenerator().generatePuzzle();
    assertTrue(algorithm.solve(board));
  }
}
