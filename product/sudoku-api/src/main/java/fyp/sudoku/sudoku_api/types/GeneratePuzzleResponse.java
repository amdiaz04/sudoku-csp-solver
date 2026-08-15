package fyp.sudoku.sudoku_api.types;

import fyp.sudoku.model.Difficulty;

/**
 * Response object for puzzle generation and file upload endpoints.
 */
public record GeneratePuzzleResponse(int[][] grid, Difficulty difficulty) {

}