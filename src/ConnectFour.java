package connectfour;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

public class ConnectFour extends JPanel {
    private static final long serialVersionUID = 1L;

    // Define named constants for the drawing graphics
    public static final String TITLE = "Connect Four";
    public static final Color COLOR_BG = new Color(240, 248, 255);  // Light blue background
    public static final Color COLOR_BG_STATUS = new Color(51, 122, 183);  // Status bar blue
    public static final Color COLOR_PLAYER1 = new Color(231, 76, 60);  // Coral red for Player 1
    public static final Color COLOR_PLAYER2 = new Color(46, 204, 113);  // Emerald green for Player 2
    public static final Font FONT_STATUS = new Font("Arial", Font.BOLD, 14);

    // Define game objects
    private Board board;
    private GameState currentState;
    private GamePiece currentPlayer;
    private JLabel statusBar;
    private boolean isAIMode;  // true for PvAI, false for PvP

    /** Constructor to setup the UI and game components */
    public ConnectFour() {
        initGame();
        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();
                // Get only the column clicked since pieces fall to bottom
                int colSelected = mouseX / Cell.SIZE;

                if (currentState == GameState.PLAYING) {
                    // Only allow moves if it's player's turn
                    if (!isAIMode || currentPlayer == GamePiece.RED) {
                        handleMove(colSelected);
                    }
                } else {
                    newGame();
                }
                repaint();
            }
        });

        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setBackground(COLOR_BG_STATUS);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(300, 30));
        statusBar.setHorizontalAlignment(JLabel.LEFT);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

        super.setLayout(new BorderLayout());
        super.add(statusBar, BorderLayout.PAGE_END);
        super.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 30));
        super.setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS, 2, false));
    }

    /** Initialize the game (run once) */
    public void initGame() {
        board = new Board();
        selectGameMode();
        newGame();
    }

    /** Select game mode through a dialog */
    private void selectGameMode() {
        // Set up custom colors
        Color bgColor = new Color(240, 248, 255);
        Color buttonColor = new Color(51, 122, 183);
        Color textColor = new Color(33, 33, 33);

        // Create a custom panel with a vertical box layout
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(bgColor);

        // Create and style the label
        JLabel label = new JLabel("Select Game Mode");
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(textColor);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Create option buttons
        String[] options = {"Player vs Player", "Player vs AI"};
        int result = JOptionPane.showOptionDialog(
                this,
                panel,
                "Connect Four",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        isAIMode = (result == 1);
    }

    /** Reset the game-board contents and the current-state, ready for new game */
    public void newGame() {
        for (int row = 0; row < Board.ROWS; ++row) {
            for (int col = 0; col < Board.COLS; ++col) {
                board.cells[row][col].content = GamePiece.EMPTY;
            }
        }
        currentPlayer = GamePiece.RED;
        currentState = GameState.PLAYING;

        // If AI mode and AI goes first (Yellow), make AI move immediately
        if (isAIMode && currentPlayer == GamePiece.YELLOW) {
            Timer timer = new Timer(500, e -> {
                int aiCol = AIPlayer.makeMove(board);
                if (aiCol != -1) {
                    handleMove(aiCol);
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    /** Handle player move and AI move if in AI mode */
    private void handleMove(int col) {
        if (col >= 0 && col < Board.COLS && currentState == GameState.PLAYING) {
            // Look for an empty cell starting from the bottom row
            for (int row = Board.ROWS - 1; row >= 0; row--) {
                if (board.cells[row][col].content == GamePiece.EMPTY) {
                    board.cells[row][col].content = currentPlayer;
                    currentState = board.stepGame(currentPlayer, row, col);
                    repaint(); // Update the board immediately after move

                    if (currentState == GameState.PLAYING) {
                        currentPlayer = (currentPlayer == GamePiece.RED) ? GamePiece.YELLOW : GamePiece.RED;

                        // If it's AI's turn
                        if (isAIMode && currentPlayer == GamePiece.YELLOW) {
                            // Add small delay for better UX
                            Timer timer = new Timer(500, e -> {
                                int aiCol = AIPlayer.makeMove(board);
                                if (aiCol != -1) {
                                    // Find empty row for AI move
                                    for (int aiRow = Board.ROWS - 1; aiRow >= 0; aiRow--) {
                                        if (board.cells[aiRow][aiCol].content == GamePiece.EMPTY) {
                                            // Make AI move
                                            board.cells[aiRow][aiCol].content = GamePiece.YELLOW;
                                            currentState = board.stepGame(GamePiece.YELLOW, aiRow, aiCol);

                                            // Switch back to player's turn if game is still ongoing
                                            if (currentState == GameState.PLAYING) {
                                                currentPlayer = GamePiece.RED;
                                            }
                                            repaint();
                                            break;
                                        }
                                    }
                                }
                            });
                            timer.setRepeats(false);
                            timer.start();
                        }
                    }
                    break;
                }
            }
        }
    }

    /** Custom painting codes on this JPanel */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(COLOR_BG);
        board.paint(g);

        if (currentState == GameState.PLAYING) {
            statusBar.setForeground(Color.BLACK);
            statusBar.setText((currentPlayer == GamePiece.RED) ? "Red's Turn" : "Green's Turn");
        } else if (currentState == GameState.DRAW) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("It's a Draw! Click to play again.");
        } else if (currentState == GameState.RED_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("Red Won! Click to play again.");
        } else if (currentState == GameState.YELLOW_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("Yellow Won! Click to play again.");
        }
    }

    /** The entry "main" method */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(TITLE);
                frame.setContentPane(new ConnectFour());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}