package fyp.sudoku;

import static org.junit.Assert.*;

import fyp.sudoku.model.SudokuBoard;
import fyp.sudoku.solver.Ac3Operations;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;

/**
 * Tests for {@link Ac3Operations}.
 */
public class Ac3OperationsTest {

  @Test
  public void testSetDomains() {
    int[][] grid = {
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 8, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0}
    };
    SudokuBoard board = new SudokuBoard(grid);
    Set<Integer>[][] domains = new Set[9][9];
    Ac3Operations.setDomains(board, domains);

    assertTrue(domains[1][1].contains(8));
    assertEquals(1, domains[1][1].size());

    assertTrue(domains[4][4].contains(1));
    assertEquals(9, domains[4][4].size());
  }

  @Test
  public void testGetNeighbours() {

    List<int[]> neighbours = Ac3Operations.getNeighbours(1, 1);
    assertFalse(neighbours.isEmpty());
    for (int[] cell : neighbours) {
      assertFalse(cell[0] == 1 && cell[1] == 1);
    }
  }

  @Test
  public void testRevise() {

    Set<Integer>[][] domains = new Set[9][9];

    HashSet<Integer> set1 = new HashSet<>();
    set1.addAll(List.of(1, 2));

    HashSet<Integer> set2 = new HashSet<>();
    set2.addAll(List.of(2));

    domains[0][0] = set1;
    domains[0][1] = set2;

    boolean hasDomainChanged = Ac3Operations.revise(domains, 0, 0, 0, 1);

    assertTrue(hasDomainChanged);
    assertEquals(Set.of(1), domains[0][0]);
    assertTrue(domains[0][0].contains(1));
    assertFalse(domains[0][0].contains(2));
  }

  @Test
  public void testQueueArcs() {
    int[][] grid = {
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 8, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0}
    };

    SudokuBoard board = new SudokuBoard(grid);
    Set<Integer>[][] domains = new Set[9][9];
    Ac3Operations.setDomains(board, domains);
    boolean result = Ac3Operations.queueArcs(domains);

    assertTrue(result);
    assertEquals(1, domains[1][1].size());
    assertTrue(domains[1][1].contains(8));
    assertEquals(8, domains[2][2].size());
    assertFalse(domains[2][2].contains(8));
  }

  @Test
  public void testQueueArcsForCell() {
    int[][] grid = {
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0}
    };

    SudokuBoard board = new SudokuBoard(grid);
    Set<Integer>[][] domains = new Set[9][9];
    Ac3Operations.setDomains(board, domains);

    domains[0][0].clear();
    domains[0][0].add(2);

    boolean result = Ac3Operations.queueAssignmentArcs(domains, 0, 0);

    assertTrue(result);
    assertFalse(domains[0][5].contains(2));
    assertFalse(domains[4][0].contains(2));
    assertFalse(domains[1][1].contains(2));
  }
}
