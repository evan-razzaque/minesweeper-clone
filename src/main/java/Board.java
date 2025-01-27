import javafx.geometry.Point2D;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class representing a Minesweeper board.
 *
 * @author Evan Razzaque
 */
public class Board {
    private final int[][] board;
    private final int cols, rows;
    private final int cellCount, mines;

    private int cellsDug;
    private int flags;
    private Point2D clickedMineCell;
    private boolean gameLost;
    private boolean gameWon;

    /**
     * A constructor to set up the board.
     * The board itself contains 2 extra rows and columns
     * to prevent array out of bounds errors,
     * which means cells will be addressed using 1-based indexing rather than 0-based.
     *
     * @param cols Number of columns
     * @param rows Number of rows
     * @param mines Number of mines
     * @see #padBoard()
     */
    public Board(int cols, int rows, int mines) {
        this.cols = cols;
        this.rows = rows;

        board = new int[rows + 2][cols + 2];
        padBoard();

        cellCount = rows * cols;

        this.mines = mines;
        flags = mines;
    }

    /**
     * A method to start the game around cell (x, y).
     *
     * @param x Cell column
     * @param y Cell row
     * @see #placeMines(int, int)
     */
    public void start(int x, int y) {
        placeMines(mines, x % cols + cols * y);
    }

    /**
     * A method to set the state of the cells outside the board to {@link Cell#BORDER},
     * which is used to prevent being able to dig outside the board bounds.
     */
    private void padBoard() {
        Arrays.fill(board[0], Cell.BORDER);
        Arrays.fill(board[board.length - 1], Cell.BORDER);

        for (int i = 1; i < board.length - 1; i++) {
            board[i][0] = Cell.BORDER;
            board[i][board[0].length - 1] = Cell.BORDER;
        }
    }

    /**
     * A method to convert a numeric value to a point.
     *
     * @param value The value to convert
     * @return The coordinate from the numeric value
     */
    private Point valueToPoint(int value) {
        return new Point(value % cols, value / cols);
    }

    /**
     * A method to place the mines on the board.
     *
     * @param mineCount Number of mines to place
     * @param origin The position of the cell that is excluded from having a mine.
     */
    private void placeMines(int mineCount, int origin) {
        ThreadLocalRandom.current()
            .ints(0, cellCount)
            .distinct()
            .filter(value -> value != origin)
            .limit(mineCount).forEach(value -> {
                Point cell = valueToPoint(value);
                board[cell.y() + 1][cell.x() + 1] = Cell.MINE;
            });
    }

    /**
     * A method to search and perform a callback on each adjacent cell. This method will also increment a counter based
     * on the user-defined callback
     *
     * @param x Cell column
     * @param y Cell row
     * @param callback The callback to run. If the callback returns true, the counter is incremented by 1. Otherwise,
     *                nothing happens.
     * @return The value of the counter, or -1 if the player loses the game
     */
    private int searchAdjacentCells(int x, int y, TriFunction<Integer, Integer, Integer> callback) {
        int counter = 0;

        for (int a = 0; a < 8; a++) {
            int col = x + (int) Math.round(Math.cos(a * 0.75));
            int row = y + (int) Math.round(Math.sin(a * 0.75));

            boolean objectFound = callback.apply(col, row, counter);
            if (objectFound) counter++;
            if (gameLost) return -1;
        }

        return counter;
    }

    /**
     * A method to dig around the cell (x, y) until all paths (function calls) have reached a cell with adjacent
     * mines or have reached the border.
     *
     * @param x Cell column
     * @param y Cell row
     * @param areCellsEmpty A boolean to determine if a cell contains no adjacent mines
     * @return The state of the last dug cell
     */
    private int dig(int x, int y, boolean areCellsEmpty) {
        if (board[y][x] == Cell.MINE) {
            return Cell.MINE;
        } else if (board[y][x] == Cell.MINE_FLAGGED || (board[y][x] & Cell.CHORD_FLAG) > 0) {
            return Cell.FLAG;
        } else if (board[y][x] == Cell.FLAG) {
            toggleFlag(x, y);
        }

        if (!areCellsEmpty) {
            if ((board[y][x] & Cell.OPENED) > 0) return Cell.OPENED;
            board[y][x] = Cell.OPENED;
            cellsDug++;
        }

        int adjacentMines = searchAdjacentCells(x, y, (col, row, mineCount) -> {
            int cell = board[row][col];

            if (areCellsEmpty && cell < Cell.CHORD_FLAG) {
                dig(col, row, false);
            } else return (cell & Cell.MINE) > 0;

            return false;
        });

        if (adjacentMines > 0) {
            Cell.storeAdjacentMineCount(x, y, adjacentMines, board);
            return Cell.OPENED;
        }

        if (!areCellsEmpty) dig(x, y, true);
        return Cell.OPENED;
    }

    /**
     * A method to initiate digging a cell.
     *
     * @param x Cell column
     * @param y Cell row
     */
    public void dig(int x, int y) {
        if ((board[y][x] & Cell.FLAG) > 0) return;

        int cell = dig(x, y, false);

        if (cell == Cell.MINE) {
            clickedMineCell = new Point2D(x, y);
            gameLost = true;
        }

        if (cellCount - mines == cellsDug) gameWon = true;
    }

    /**
     * A method to add or remove a flag on a cell.
     *
     * @param x Cell column
     * @param y Cell row
     */
    public void toggleFlag(int x, int y) {
        int cell = board[y][x];
        if (cell >= Cell.OPENED) return;

        if ((cell & Cell.FLAG) == 0) {
            if (flags <= 0) return;

            flags--;
            board[y][x] += Cell.FLAG;
        } else {
            flags++;
            board[y][x] -= Cell.FLAG;

            if ((cell & Cell.CHORD_FLAG) > 0) {
                board[y][x] -= Cell.CHORD_FLAG;
            }
        }
    }

    /**
     * A method to perform chording around an opened cell
     * with the corresponding amount of flags
     *
     * @param x Cell column
     * @param y Cell row
     */
    public void chord(int x, int y) {
        int adjacentMines = Cell.getAdjacentMines(board[y][x]);
        if (adjacentMines <= 0) return;

        int flags = searchAdjacentCells(x, y, (col, row, flagCount) -> {
            if ((board[row][col] & Cell.FLAG) == 0) return false;

            if ((board[row][col] & Cell.CHORD_FLAG) == 0) {
                board[row][col] += Cell.CHORD_FLAG;
            }

            return true;
        });

        if (flags != adjacentMines) return;

        searchAdjacentCells(x, y, (col, row, unused) -> {
            if (board[row][col] == Cell.BORDER) return false;

            int cell = dig(col, row, false);

            if (cell == Cell.MINE) {
                clickedMineCell = new Point2D(col, row);
                gameLost = true;
            }

            return false;
        });
    }

    /**
     * A method to end the game.
     */
    public void endGame() {
        cellsDug = 0;
        gameLost = true;
    }

    public int getFlags() {
        return flags;
    }

    /**
     * Gets the state of the cell (x, y).
     *
     * @param x Cell column
     * @param y Cell row
     * @return The state of the cell
     */
    public int getCell(int x, int y) {
        return board[y][x];
    }

    /**
     * Gets the coordinate of the clicked mine.
     *
     * @return Coordinate of the clicked mine
     */
    public Point2D getClickedMineCell() {
        return clickedMineCell;
    }

    public boolean isGameLost() {
        return gameLost;
    }

    public boolean isGameWon() {
        return gameWon;
    }
}