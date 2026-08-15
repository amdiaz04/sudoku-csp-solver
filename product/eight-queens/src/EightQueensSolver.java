import java.util.Arrays;

public class EightQueensSolver {

    private static final int BOARD_SIZE = 8;
    private final int[] col = new int[BOARD_SIZE];

    public int[] solve() {
        if (queens(0)) {
            return col;
        }
        return null;
    }

    private boolean queens(int row) {
        if (row == BOARD_SIZE) {
            return true;
        }
        for (int column = 0; column < BOARD_SIZE; column++) {
            col[row] = column;

            // If returns false, backtracks.
            if (promising(row) && queens(row + 1)) {
                return true;
            }
        }
        return false;
    }

    private boolean promising(int row) {
        for (int k = 0; k < row; k++) {
            // Same column
            if (col[row] == col[k]) {
                return false;
            }
            // Same diagonal if difference of rows and difference of columns is the same.
            if (Math.abs(col[row] - col[k]) == row - k) {
                return false;

            }
        }
        return true;
    }

    private int[][] toBoard() {
        int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < BOARD_SIZE; row++) {
            board[row][col[row]] = 1;
        }
        return board;
    }

    private static void printBoard(int[][] board) {
        for (int[] row : board) {
            System.out.println(Arrays.toString(row));
        }
    }

    public static void main(String[] args) {
        EightQueensSolver solver = new EightQueensSolver();
        int[] positions = solver.solve();
        if (positions != null) {
            System.out.println("1D Array Representation:\n" + Arrays.toString(positions));
            System.out.println("\n2D Array Representation:");
            printBoard(solver.toBoard());
        }
    }
}

