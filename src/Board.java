package connectfour;
import java.awt.*;

public class Board {
    // Define named constants
    public static final int ROWS = 6;  // 6x7 grid for Connect Four
    public static final int COLS = 7;

    // Define named constants for drawing
    public static final int CANVAS_WIDTH = Cell.SIZE * COLS;
    public static final int CANVAS_HEIGHT = Cell.SIZE * ROWS;
    public static final int GRID_WIDTH = 8;
    public static final int GRID_WIDTH_HALF = GRID_WIDTH / 2;
    public static final Color COLOR_GRID = Color.BLUE;  // Blue grid for Connect Four
    public static final int Y_OFFSET = 1;

    Cell[][] cells;

    public Board() {
        initGame();
    }

    public void initGame() {
        cells = new Cell[ROWS][COLS];
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                cells[row][col] = new Cell(row, col);
            }
        }
    }

    public void newGame() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                cells[row][col].newGame();
            }
        }
    }

    public GameState stepGame(GamePiece player, int selectedRow, int selectedCol) {
        // Update game board
        cells[selectedRow][selectedCol].content = player;

        // Check for win conditions (4-in-a-line)
        if (hasWon(player, selectedRow, selectedCol)) {
            return (player == GamePiece.RED) ? GameState.RED_WON : GameState.YELLOW_WON;
        }

        // Check for draw
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                if (cells[row][col].content == GamePiece.EMPTY) {
                    return GameState.PLAYING;
                }
            }
        }
        return GameState.DRAW;
    }

    /** Check if the player has won after placing at (row, col) */
    public boolean hasWon(GamePiece player, int row, int col) {
        // Check horizontal
        int count = 0;
        for (int c = 0; c < COLS; c++) {
            if (cells[row][c].content == player) {
                count++;
                if (count >= 4) return true;
            } else {
                count = 0;
            }
        }

        // Check vertical
        count = 0;
        for (int r = 0; r < ROWS; r++) {
            if (cells[r][col].content == player) {
                count++;
                if (count >= 4) return true;
            } else {
                count = 0;
            }
        }

        // Check diagonal (top-left to bottom-right)
        count = 0;
        int startRow = row - Math.min(row, col);
        int startCol = col - Math.min(row, col);
        while (startRow < ROWS && startCol < COLS) {
            if (cells[startRow][startCol].content == player) {
                count++;
                if (count >= 4) return true;
            } else {
                count = 0;
            }
            startRow++;
            startCol++;
        }

        // Check diagonal (top-right to bottom-left)
        count = 0;
        startRow = row - Math.min(row, COLS - 1 - col);
        startCol = col + Math.min(row, COLS - 1 - col);
        while (startRow < ROWS && startCol >= 0) {
            if (cells[startRow][startCol].content == player) {
                count++;
                if (count >= 4) return true;
            } else {
                count = 0;
            }
            startRow++;
            startCol--;
        }

        return false;
    }

    public void paint(Graphics g) {
        // Draw the grid-lines
        g.setColor(COLOR_GRID);
        for (int row = 1; row < ROWS; ++row) {
            g.fillRoundRect(0, Cell.SIZE * row - GRID_WIDTH_HALF,
                    CANVAS_WIDTH - 1, GRID_WIDTH,
                    GRID_WIDTH, GRID_WIDTH);
        }
        for (int col = 1; col < COLS; ++col) {
            g.fillRoundRect(Cell.SIZE * col - GRID_WIDTH_HALF, 0 + Y_OFFSET,
                    GRID_WIDTH, CANVAS_HEIGHT - 1,
                    GRID_WIDTH, GRID_WIDTH);
        }

        // Draw all the cells
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                cells[row][col].paint(g);
            }
        }
    }

}