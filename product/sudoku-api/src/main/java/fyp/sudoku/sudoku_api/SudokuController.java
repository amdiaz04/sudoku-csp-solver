package fyp.sudoku.sudoku_api;

import fyp.sudoku.exception.InvalidSudokuBoardException;
import fyp.sudoku.exception.SudokuReadingException;
import fyp.sudoku.exception.UnsolvableSudokuException;
import fyp.sudoku.model.Difficulty;
import fyp.sudoku.model.SudokuBoard;
import fyp.sudoku.solver.SudokuAlgorithm;
import fyp.sudoku.sudoku_api.types.CalcAvgResponse;
import fyp.sudoku.sudoku_api.types.GeneratePuzzleResponse;
import fyp.sudoku.sudoku_api.types.SolvePuzzleResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST controller for Sudoku operations including puzzle generation, solving, and performance
 * benchmarking.
 */
@RestController
@RequestMapping("/api/sudoku")

public class SudokuController {

  @Autowired
  private SudokuManager sudokuManager;

  @GetMapping("/difficulties")
  public List<Difficulty> getDifficulties() {
    return sudokuManager.getDifficulties();
  }

  @GetMapping("/generate")
  public GeneratePuzzleResponse generateRandom() throws UnsolvableSudokuException {
    SudokuBoard board = sudokuManager.generateRandomBoard();
    Difficulty difficulty = sudokuManager.getDifficulty();
    return new GeneratePuzzleResponse(board.getGrid(), difficulty);
  }

  @GetMapping("/generate/{difficulty}")
  public GeneratePuzzleResponse generateWithDifficulty(@PathVariable Difficulty difficulty)
      throws UnsolvableSudokuException {
    SudokuBoard board = sudokuManager.generateBoardWithDifficulty(difficulty);
    Difficulty diff = sudokuManager.getDifficulty();
    return new GeneratePuzzleResponse(board.getGrid(), diff);
  }

  @GetMapping("/algorithm-names")
  public List<String> getAlgorithmNames() {
    return sudokuManager.getAlgorithmNames();
  }


  @PostMapping("/algorithm")
  public String setAlgorithm(@RequestParam String name) {
    SudokuAlgorithm algorithm = sudokuManager.getAlgorithms().get(name);
    if (algorithm == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown algorithm: " + name);
    }
    sudokuManager.setAlgorithm(algorithm);
    return "Solver is set as " + name + " algorithm";
  }

  @GetMapping
  public int[][] getGrid() {
    return sudokuManager.getCurrentGrid();
  }

  @PostMapping("/solve")
  public SolvePuzzleResponse solve() throws UnsolvableSudokuException {
    sudokuManager.solvePuzzle();
    return new SolvePuzzleResponse(
        sudokuManager.getCurrentGrid(),
        sudokuManager.getBacktracks(),
        sudokuManager.getNodesVisited(),
        sudokuManager.getSolveTime()
    );
  }

  @GetMapping("/calculate-average")
  public CalcAvgResponse calcAvg(@RequestParam int puzzleCount,
      @RequestParam(required = false) Difficulty difficulty) throws UnsolvableSudokuException {
    if (puzzleCount < 1) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "puzzleCount must be at least 1");
    }
    return sudokuManager.calcAvgPerformance(puzzleCount, difficulty);
  }

  // user uploads a file, read it and pass contents over
  @PostMapping("/upload-puzzle")
  public GeneratePuzzleResponse fileUpload(@RequestBody String file)
      throws IOException, SudokuReadingException, UnsolvableSudokuException {
    BufferedReader reader = new BufferedReader(new StringReader(file));
    SudokuBoard board = sudokuManager.loadFromUser(reader);
    return new GeneratePuzzleResponse(board.getGrid(), sudokuManager.getDifficulty());
  }

  @PostMapping("/upload-puzzles")
  public CalcAvgResponse statsFileUpload(@RequestBody String file)
      throws IOException, SudokuReadingException, UnsolvableSudokuException {
    BufferedReader reader = new BufferedReader(new StringReader(file));
    return sudokuManager.calcAvgFromFile(reader);
  }

  /**
   * Returns a 400 response when a puzzle cannot be solved.
   *
   * @param e the exception thrown by the solver.
   * @return error message with 400 status.
   */
  @ExceptionHandler(UnsolvableSudokuException.class)
  public ResponseEntity<String> handleUnsolvablePuzzle(UnsolvableSudokuException e) {
    return ResponseEntity.badRequest().body(e.getMessage());
  }

  /**
   * Returns a 400 response when an uploaded puzzle file cannot be parsed.
   *
   * @param e the exception thrown by the reader.
   * @return error message with 400 status.
   */
  @ExceptionHandler({SudokuReadingException.class, InvalidSudokuBoardException.class})
  public ResponseEntity<String> handleBadPuzzleInput(Exception e) {
    return ResponseEntity.badRequest().body(e.getMessage());
  }

  /**
   * Returns a 409 response when solving is requested before any puzzle exists.
   *
   * @param e the exception thrown by the manager.
   * @return error message with 409 status.
   */
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<String> handleNoPuzzle(IllegalStateException e) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
  }

}
