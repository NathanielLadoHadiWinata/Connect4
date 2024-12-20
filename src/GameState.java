package connectfour;

import java.util.ArrayList;
import java.util.Random;

public class AIPlayer {
    private static final Random random = new Random();

    public static int makeMove(Board board) {
        // Get all valid moves (columns that aren't full)
        ArrayList<Integer> validMoves = new ArrayList<>();
        for (int col = 0; col < Board.COLS; col++) {
            if (board.cells[0][col].content == GamePiece.EMPTY) {
                validMoves.add(col);
            }
        }

        // If there's a winning move, take it
        for (int col : validMoves) {
            int row = getLowestEmptyRow(board, col);
            if (row != -1) {
                board.cells[row][col].content = GamePiece.YELLOW;
                if (board.hasWon(GamePiece.YELLOW, row, col)) {
                    board.cells[row][col].content = GamePiece.EMPTY;
                    return col;
                }
                board.cells[row][col].content = GamePiece.EMPTY;
            }
        }

        // If opponent has a winning move, block it
        for (int col : validMoves) {
            int row = getLowestEmptyRow(board, col);
            if (row != -1) {
                board.cells[row][col].content = GamePiece.RED;
                if (board.hasWon(GamePiece.RED, row, col)) {
                    board.cells[row][col].content = GamePiece.EMPTY;
                    return col;
                }
                board.cells[row][col].content = GamePiece.EMPTY;
            }
        }

        // Otherwise, make a random move
        if (!validMoves.isEmpty()) {
            return validMoves.get(random.nextInt(validMoves.size()));
        }

        return -1; // No valid moves available
    }

    private static int getLowestEmptyRow(Board board, int col) {
        for (int row = Board.ROWS - 1; row >= 0; row--) {
            if (board.cells[row][col].content == GamePiece.EMPTY) {
                return row;
            }
        }
        return -1;
    }
}