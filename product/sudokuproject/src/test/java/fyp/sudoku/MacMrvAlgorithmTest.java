package fyp.sudoku;

import fyp.sudoku.exception.UnsolvableSudokuException;
import fyp.sudoku.solver.MacAlgorithm;
import fyp.sudoku.solver.MacMrvAlgorithm;
import org.junit.Test;

/**
 * Tests for {@link MacMrvAlgorithm}.
 */
public class MacMrvAlgorithmTest {

  private final MacMrvAlgorithm solver = new MacMrvAlgorithm();

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