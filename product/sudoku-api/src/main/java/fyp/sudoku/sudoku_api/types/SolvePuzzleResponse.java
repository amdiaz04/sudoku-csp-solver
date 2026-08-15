package fyp.sudoku.sudoku_api.types;

/**
 * Response object for the solve endpoint, includes the solved grid and performance metrics.
 */
public record SolvePuzzleResponse(int[][] grid, int backtracks, int nodesVisited,
                                  double solveTime) {

}
