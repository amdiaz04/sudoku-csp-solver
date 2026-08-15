package fyp.sudoku.sudoku_api;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fyp.sudoku.exception.UnsolvableSudokuException;
import fyp.sudoku.model.Difficulty;
import fyp.sudoku.model.SudokuBoard;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Tests for {@link SudokuController} verifying correct response structure and status codes for each
 * endpoint.
 */
@WebMvcTest(SudokuController.class)
public class SudokuControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private SudokuManager sudokuManager;

  @Test
  public void testInvalidEndpoint() throws Exception {
    mockMvc.perform(get("/api/sudoku/unknown"))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testGenerateRandom() throws Exception {
    int[][] grid = new int[9][9];
    SudokuBoard board = new SudokuBoard(grid);

    when(sudokuManager.generateRandomBoard()).thenReturn(board);
    when(sudokuManager.getDifficulty()).thenReturn(Difficulty.EASY);

    mockMvc.perform(get("/api/sudoku/generate"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.grid").isArray())
        .andExpect(jsonPath("$.difficulty").value("EASY"));
  }

  @Test
  public void testGenerateWithDifficulty() throws Exception {
    int[][] grid = new int[9][9];
    SudokuBoard board = new SudokuBoard(grid);

    when(sudokuManager.generateBoardWithDifficulty(Difficulty.HARD)).thenReturn(board);
    when(sudokuManager.getDifficulty()).thenReturn(Difficulty.HARD);

    mockMvc.perform(get("/api/sudoku/generate/HARD"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.grid").isArray())
        .andExpect(jsonPath("$.difficulty").value("HARD"));
  }

  @Test
  public void testGenerateWithInvalidDifficulty() throws Exception {
    mockMvc.perform(get("/api/sudoku/generate/INVALID"))
        .andExpect(status().isBadRequest());
  }


  @Test
  public void testSolve() throws Exception {
    int[][] grid = new int[9][9];

    when(sudokuManager.getCurrentGrid()).thenReturn(grid);
    when(sudokuManager.getBacktracks()).thenReturn(5);
    when(sudokuManager.getNodesVisited()).thenReturn(20);
    when(sudokuManager.getSolveTime()).thenReturn(1.5);

    mockMvc.perform(post("/api/sudoku/solve"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.grid").isArray())
        .andExpect(jsonPath("$.backtracks").value(5))
        .andExpect(jsonPath("$.nodesVisited").value(20))
        .andExpect(jsonPath("$.solveTime").value(1.5));
  }

  @Test
  public void testSolveEmptyGrid() throws Exception {
    when(sudokuManager.getCurrentGrid()).thenReturn(new int[9][9]);

    mockMvc.perform(post("/api/sudoku/solve"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.grid").exists());
  }

  @Test
  public void testSolveUnsolvableBoard() throws Exception {
    doThrow(new UnsolvableSudokuException("No solution"))
        .when(sudokuManager).solvePuzzle();

    mockMvc.perform(post("/api/sudoku/solve"))
        .andExpect(status().isBadRequest());
  }


}