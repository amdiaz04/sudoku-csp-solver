package fyp.sudoku;

import fyp.sudoku.exception.UnsolvableSudokuException;
import fyp.sudoku.model.Difficulty;
import fyp.sudoku.model.SudokuBoard;
import fyp.sudoku.model.SudokuPuzzle;
import fyp.sudoku.solver.BacktrackingRecursiveAlgorithm;
import fyp.sudoku.solver.SudokuAlgorithm;
import fyp.sudoku.util.SudokuGenerator;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link SudokuPuzzle}.
 */
public class SudokuPuzzleTest {

  @Test
  public void testGetDifficulty() throws UnsolvableSudokuException {
    SudokuBoard newBoard = new SudokuGenerator().generatePuzzle();
    SudokuPuzzle newPuzzle = new SudokuPuzzle(newBoard);
    Difficulty difficulty = newPuzzle.getDifficulty();
    Difficulty storedDifficulty = newPuzzle.getDifficulty();

    assertNotNull(difficulty);
    assertEquals(difficulty, storedDifficulty);
    assertTrue(difficulty == Difficulty.EASY || difficulty == Difficulty.MEDIUM ||
        difficulty == Difficulty.HARD);
  }

  @Test
  public void testDetermineDifficulties() throws UnsolvableSudokuException {
    SudokuGenerator generator = new SudokuGenerator();

    SudokuBoard easyBoard = generator.generatePuzzle(Difficulty.EASY);
    assertEquals(Difficulty.EASY, new SudokuPuzzle(easyBoard).getDifficulty());

    SudokuBoard mediumBoard = generator.generatePuzzle(Difficulty.MEDIUM);
    assertEquals(Difficulty.MEDIUM, new SudokuPuzzle(mediumBoard).getDifficulty());

    SudokuBoard hardBoard = generator.generatePuzzle(Difficulty.HARD);
    assertEquals(Difficulty.HARD, new SudokuPuzzle(hardBoard).getDifficulty());
  }

  @Test
  public void testSolverWithBacktrackingRecursion() throws UnsolvableSudokuException {
    SudokuBoard newBoard = new SudokuGenerator().generatePuzzle();
    SudokuPuzzle newPuzzle = new SudokuPuzzle(newBoard);
    SudokuAlgorithm backtrackingSolver = new BacktrackingRecursiveAlgorithm();
    SudokuBoard solvedPuzzle = newPuzzle.solve(backtrackingSolver);

    assertNotNull(solvedPuzzle);
  }

}
