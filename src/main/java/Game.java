import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import nodes.ButtonGroup;
import nodes.LabeledTextField;

import java.net.URL;

/**
 * Java implementation of Minesweeper.<br><br>
 * Important note: The "grid" (canvas) uses 0-based indexing,
 * while the "board" uses 1-based indexing. For more information, see {@link Board#Board(int, int, int)}.
 *
 * @author Evan Razzaque
 */
public class Game extends Application {
    /**
     * A method to redraw the board.
     *
     * @param minesVisible whether to show the mines or not
     */
    private void updateBoard(boolean minesVisible) {
        gc.setFill(unopenedCellColor);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        if (game == null) {
            drawGrid(cols, rows);
            return;
        }

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                drawCell(x, y, minesVisible);
            }
        }

        Point2D clickedMineCell = game.getClickedMineCell();

        // Highlights the mine that the user clicked
        if (clickedMineCell != null) {
            int col = (int) clickedMineCell.getX();
            int row = (int) clickedMineCell.getY();

            fillCellBackground(col - 1, row - 1, Color.RED);
            drawMine(col - 1, row - 1, false);
        }

        drawGrid(cols, rows);
    }

    /**
     * A method to draw the state of cell (x, y) on the grid.
     *
     * @param x Grid column
     * @param y Grid row
     * @param minesVisible Whether to show the mines or not
     */
    private void drawCell(int x, int y, boolean minesVisible) {
        int cell = game.getCell(x + 1, y + 1);

        switch (cell) {
            case Cell.EMPTY:
                break;
            case Cell.FLAG, Cell.FLAG_CHORDED:
                drawFlag(x, y);
            case Cell.MINE:
                if (!minesVisible) break;

                // Covers any flags when the cell is an incorrect flag (when the mines are shown)
                fillCellBackground(x, y, unopenedCellColor);
                drawMine(x, y, cell != Cell.MINE);
                break;
            case Cell.MINE_FLAGGED, Cell.MINE_FLAGGED_CHORDED:
                drawFlag(x, y);
                break;
            default:
                drawOpenedCell(x, y, cell);
                break;
        }
    }

    /**
     * A method draw the grid.
     *
     * @param cols Amount of columns
     * @param rows Amount of rows
     */
    private void drawGrid(int cols, int rows) {
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int i = 0; i < cols; i++) {
            gc.strokeLine(i * cellSize, 0, i * cellSize, canvas.getHeight());
        }

        for (int i = 0; i < rows; i++) {
            gc.strokeLine(0, i * cellSize, canvas.getWidth(), i * cellSize);
        }
    }

    /**
     * A method to fill in a cell on the board.
     *
     * @param x     Grid column
     * @param y     Grid row
     * @param color Fill color
     */
    private void fillCellBackground(int x, int y, Color color) {
        gc.setFill(color);
        gc.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
    }

    /**
     * A method to draw an opened cell on the board.
     *
     * @param x    Grid column
     * @param y    Grid row
     * @param cell The state of the cell
     */
    private void drawOpenedCell(int x, int y, int cell) {
        fillCellBackground(x, y, openedCellColor);

        int adjacentMines = Cell.getAdjacentMines(cell);
        if (adjacentMines <= 0) return;

        gc.setFill(colors[adjacentMines - 1]);
        gc.fillText(adjacentMines + "", (x + 0.5) * cellSize, (y + 0.5) * cellSize);
    }

    /**
     * A method to draw a flag on a cell.
     *
     * @param x Grid column
     * @param y Grid row
     */
    private void drawFlag(int x, int y) {
        gc.setFill(Color.RED);
        gc.fillText("`", (x + 0.5) * cellSize, (y + 0.5) * cellSize);
    }

    /**
     * A method to draw a mine on a cell.
     *
     * @param x           Grid column
     * @param y           Grid row
     * @param isIncorrect A boolean to indicate that the current cell is flagged without having a mine
     */
    private void drawMine(int x, int y, boolean isIncorrect) {
        gc.setFill(Color.BLACK);
        gc.fillText("*", (x + 0.5) * cellSize, (y + 0.5) * cellSize);

        if (!isIncorrect) return;

        // Draws red x over flags without mines (when the mines are revealed)
        gc.setStroke(Color.RED);
        gc.strokeLine(x * cellSize, y * cellSize, (x + 1) * cellSize, (y + 1) * cellSize);
        gc.strokeLine(x * cellSize, (y + 1) * cellSize, (x + 1) * cellSize, (y) * cellSize);
    }

    /**
     * A method to update the flag count display.
     *
     * @param flags Amount of flags available
     */
    private void updateFlagCountDisplay(int flags) {
        flagCountLabel.setText(String.format("%03d", flags));
    }

    /**
     * A method to update the canvas size in respect to the board dimensions.
     */
    private void updateCanvasSize() {
        if (cols > rows) cellSize = (double) canvasWidth / cols;
        else cellSize = (double) canvasHeight / rows;

        canvas.setWidth(cellSize * cols);
        canvas.setHeight(cellSize * rows);
    }

    /**
     * A method to start a game at the clicked cell.
     *
     * @param x Cell column
     * @param y Cell row
     * @see Board#start(int, int)
     */
    private void startGame(int x, int y) {
        game = new Board(cols, rows, mineCount);

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFont(Font.font(gc.getFont().getFamily(), FontWeight.BOLD, cellSize / 1.5));

        game.start((int) (x / cellSize), (int) (y / cellSize));
        updateFlagCountDisplay(game.getFlags());
        updateBoard(showMines);
    }

    /**
     * A method to end the game instance.
     */
    private void endGame() {
        updateBoard(game.isGameLost() || showMines);
        canvas.setDisable(true);

        if (game.isGameWon()) {
            setResetButtonImage("smiley_cool");
            new Alert(Alert.AlertType.INFORMATION, "You win!").showAndWait();
        } else {
            setResetButtonImage("smiley_dead");
        }

        game.endGame();
        game = null;
    }

    /**
     * A method to change the image of the reset button.
     * @param name Image name without extension
     */
    private void setResetButtonImage(String name) {
        resetButton.setGraphic(new ImageView(
            new Image(resourcePath + "/images/" + name + ".png", 34, 34, true, false)
        ));
    }

    /**
     * A method to change board dimensions and the number of mines.
     *
     * @param cols  Number of columns
     * @param rows  Number of rows
     * @param mines Number of mines
     */
    private void configureGame(int cols, int rows, int mines) {
        colsField.setValue(cols + "");
        rowsField.setValue(rows + "");
        minesField.setValue(mines + "");

        resetButton.fire();
        updateFlagCountDisplay(mines);
    }

    Pane root;
    Canvas canvas;
    GraphicsContext gc;
    Label flagCountLabel;
    Button resetButton;

    final int canvasWidth = 720;
    final int canvasHeight = 720;

    VBox settingsContainer;
    LabeledTextField colsField, rowsField, minesField;
    ButtonGroup difficultyButtons;
    Color openedCellColor = Color.DARKGRAY;
    Color unopenedCellColor = Color.GRAY;
    Color[] colors = new Color[] {Color.BLUE, Color.GREEN, Color.RED, Color.PURPLE, Color.DARKRED, Color.CYAN,
            Color.BLACK, Color.GRAY};
    URL resourcePath = getClass().getResource("main/java/resources");

    Board game;

    int cols = 16;
    int rows = 16;
    double cellSize;
    int mineCount = 40;

    boolean isGameRunning = true;
    boolean showMines;

    /**
     * A method to configure the application and run the game.
     *
     * @param stage the primary stage for this application, onto which
     *              the application scene can be set.
     *              Applications may create other stages, if needed, but they will not be
     *              primary stages.
     */
    @Override
    public void start(Stage stage) {
        showMines = getParameters().getRaw().contains("-showMines");
        canvas = new Canvas(canvasWidth, canvasHeight);
        gc = canvas.getGraphicsContext2D();

        cellSize = (int) (canvas.getWidth() / cols);
        flagCountLabel = new Label("0" + mineCount);

        resetButton = new Button("");
        setResetButtonImage("smiley");

        colsField = new LabeledTextField(new VBox(), "Columns", cols);
        rowsField = new LabeledTextField(new VBox(), "Rows", rows);
        minesField = new LabeledTextField(new VBox(), "Mines", mineCount);

        settingsContainer = new VBox(5, colsField, rowsField, minesField);
        difficultyButtons = new ButtonGroup(new VBox(5), "Beginner", "Intermediate", "Expert");

        root = new Pane(canvas, resetButton, flagCountLabel, settingsContainer, difficultyButtons);

        Scene scene = new Scene(root, 1200, 1000);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Minesweeper");

        NodeDesigner.configure(canvas, resetButton, flagCountLabel, settingsContainer, difficultyButtons);

        canvas.setOnMouseClicked(event -> {
            if (game == null) {
                startGame((int) event.getX(), (int) event.getY());
            }

            int x = (int) (event.getX() / cellSize) + 1;
            int y = (int) (event.getY() / cellSize) + 1;

            if (event.getButton() == MouseButton.PRIMARY) {
                game.chord(x, y);
                game.dig(x, y);
            } else if (event.getButton() == MouseButton.SECONDARY) {
                game.toggleFlag(x, y);
            }

            updateFlagCountDisplay(game.getFlags());

            if (game.isGameLost() || game.isGameWon()) {
                isGameRunning = false;
                endGame();
                return;
            }

            updateBoard(showMines);
        });

        canvas.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown()) setResetButtonImage("smiley_shocked");
        });

        canvas.setOnMouseReleased(event -> {
            setResetButtonImage("smiley");
        });

        resetButton.setOnAction(event -> {
            isGameRunning = true;

            setResetButtonImage("smiley");
            updateFlagCountDisplay(mineCount);
            canvas.setDisable(false);
            game = null;

            int newCols, newRows, newMineCount;

            try {
                newCols = Integer.parseInt(colsField.getValue());
                newRows = Integer.parseInt(rowsField.getValue());
                newMineCount = Integer.parseInt(minesField.getValue());

                String errorMessage = "";

                if (newCols <= 0) {
                    errorMessage = "There must be at least one column";
                } else if (newRows <= 0) {
                    errorMessage = "There must be at least one row";
                } else if (newMineCount >= newCols * newRows) {
                    errorMessage = "There must be less mines than cells";
                } else if (mineCount < 0) {
                    errorMessage = "Mine count must be positive";
                }

                if (!errorMessage.isEmpty()) {
                    new Alert(Alert.AlertType.ERROR, errorMessage).showAndWait();
                    return;
                }

                cols = newCols;
                rows = newRows;
                updateCanvasSize();

                mineCount = newMineCount;
                updateFlagCountDisplay(mineCount);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.setTitle("Invalid input(s)");
                alert.showAndWait();
            }

            updateBoard(showMines);
        });

        difficultyButtons.get("Beginner").setOnAction(event -> configureGame(8, 8, 10));
        difficultyButtons.get("Intermediate").setOnAction(event -> configureGame(16, 16, 40));
        difficultyButtons.get("Expert").setOnAction(event -> configureGame(30, 16, 99));

        updateBoard(showMines);
        stage.show();
    }

    /**
     * The method to launch the application.
     *
     * @param args Command line args to change one or more options for the game. See {@link Game#start(Stage)} for
     *             more details.
     */
    public static void main(String[] args) {
        launch(args);
    }
}