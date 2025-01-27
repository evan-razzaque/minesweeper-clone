/**
 * A class used to represent the state of a cell on a board.
 *
 * @author Evan Razzaque
 */
public class Cell {
    // Bitwise Flags
    static final int EMPTY = 0;
    static final int FLAG = 1;
    static final int CHORD_FLAG = 2;
    static final int MINE = 4;
    static final int OPENED = 8;
    static final int ONE = 16;
    static final int SEVEN = ONE << 6;
    static final int BORDER = SEVEN << 1;

    static final int FLAG_CHORDED = FLAG | CHORD_FLAG;
    static final int MINE_FLAGGED = MINE | FLAG;
    static final int MINE_FLAGGED_CHORDED = MINE | FLAG_CHORDED;

    // Accounts for the exponent of OPENED when computing adjacent mines
    static final int MINE_OFFSET = (int) (Math.log(OPENED) / Math.log(2));

    /**
     * A method to get the number of adjacent mines from a cell state.
     *
     * @param cell Cell state
     * @return Number of adjacent mines
     */
    static int getAdjacentMines(int cell) {
        if (cell == Cell.OPENED) return 0;
        return ((cell & Cell.OPENED) == 0)? -1 : (int) (Math.log(cell - Cell.OPENED) / Math.log(2)) - MINE_OFFSET;
    }

    /**
     * A method to store the number of adjacent mines at a cell.
     *
     * @param x Cell column
     * @param y Cell row
     * @param mines Number of mines
     * @param board The board containing the cell
     */
    static void storeAdjacentMineCount(int x, int y, int mines, int[][] board) {
        board[y][x] += (int) Math.pow(2, MINE_OFFSET + mines);
    }
}
