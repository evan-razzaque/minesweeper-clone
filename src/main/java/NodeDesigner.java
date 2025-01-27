import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import nodes.ButtonGroup;
import nodes.LabeledTextField;

import java.io.InputStream;
import java.util.Objects;

/**
 * A class to handle node design.
 *
 * @author Evan Razzaque
 */
public class NodeDesigner {
    /**
     * A method to configure the nodes.
     *
     * @param canvas Canvas
     * @param resetButton Reset Button
     * @param flagCountLabel Flag Count Display
     */
    static void configure(Canvas canvas, Button resetButton, Label flagCountLabel, VBox settingsContainer, ButtonGroup difficultyButtons) {
        try {
            InputStream minesweeperFont = NodeDesigner.class.getResource("main/java/resources/fonts/mine-sweeper.ttf").openStream();
            InputStream sevenSegmentFont = NodeDesigner.class.getResource("main/java/resources/fonts/7segment.ttf").openStream();

            Font.loadFont(minesweeperFont, 12);
            Font.loadFont(sevenSegmentFont, 12);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        canvas.relocate(200, 100);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false);
        gc.setLineWidth(2);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(Font.font("MINE-SWEEPER"));

        resetButton.setPadding(Insets.EMPTY);
        resetButton.setPrefSize(42, 42);
        Platform.runLater(() -> {
            resetButton.relocate(canvas.getLayoutX() + canvas.getWidth() / 2 - resetButton.getWidth() / 2, 50);
        });

        flagCountLabel.relocate(200, 50);
        flagCountLabel.setPadding(new Insets(-2, 4, -2, 2));
        flagCountLabel.setBackground(Background.fill(Color.BLACK));
        flagCountLabel.setFont(Font.font("7-Segment", 40));
        flagCountLabel.setTextFill(Color.RED);

        settingsContainer.relocate(50, 100);

        for (Node child : settingsContainer.getChildren()) {
            ((LabeledTextField) child).getContainer().setPrefWidth(50);
        }

        difficultyButtons.relocate(50, 300);
    }
}
