package fyp.sudoku.sudoku_api.types;

import java.util.Map;

/**
 * Response object for average performance calculation endpoints. Each map is keyed by algorithm
 * name.
 */
public record CalcAvgResponse(Map<String, Double> avgBacktracks,
                              Map<String, Double> avgNodesVisited, Map<String, Double> avgTime) {

}