package fyp.sudoku;

import fyp.sudoku.exception.UnsolvableSudokuException;
import fyp.sudoku.solver.Ac3MrvAlgorithm;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link Ac3MrvAlgorithm}.
 */
public class Ac3MrvAlgorithmTest {

  Ac3MrvAlgorithm solver = new Ac3MrvAlgorithm();

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
