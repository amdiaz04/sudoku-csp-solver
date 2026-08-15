package fyp.sudoku;

import fyp.sudoku.exception.UnsolvableSudokuException;
import fyp.sudoku.solver.MacAlgorithm;
import org.junit.Test;

/**
 * Tests for {@link MacAlgorithm}.
 */
public class MacAlgorithmTest {

  private final MacAlgorithm solver = new MacAlgorithm();

  @Test
  public void testEasy() throws UnsolvableSudokuException {
    AlgorithmTest.testSolveValidBoardEasy(solver);
  }

  @Test
  public void testMedium() throws UnsolvableSudokuException {
    AlgorithmTest.testSolveValidBoardMedium(solver);
  }

  @Test
  public void testHard() throws UnsolvableSudokuException {
    AlgorithmTest.testSolveValidBoardHard(solver);
  }

  @Test
  public void testInvalid() {
    AlgorithmTest.testSolveInvalidBoard(solver);
  }

  @Test
  public void testComplete() throws UnsolvableSudokuException {
    AlgorithmTest.testSolveCompleteBoard(solver);
  }

  @Test
  public void testGenerated() throws UnsolvableSudokuException {
    AlgorithmTest.testGeneratedBoard(solver);
  }
}