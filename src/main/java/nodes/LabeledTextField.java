package nodes;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * A class to implement a node with a label and a text field.
 *
 * @author Evan Razzaque
 */
public class LabeledTextField extends Pane {
    private final Label label;
    private final TextField textField;
    private final Pane container;

    // Creates a container with a label and text field
    private LabeledTextField(Pane container, String labelText, Object value) {
        super();

        this.container = container;
        label = new Label(labelText);
        textField = new TextField(value.toString());

        container.getChildren().addAll(label,textField);
        getChildren().add(container);
    }

    /**
     * A constructor to create a labeled text field with the label on top.
     *
     * @param container The container to contain the label and text field
     * @param labelText Label text
     * @param value The value of the text field
     */
    public LabeledTextField(VBox container, String labelText, Object value) {
        this((Pane) container, labelText, value);
    }

    /**
     * A constructor to create a labeled text field with the label on the left.
     *
     * @param container The container to contain the label and text field
     * @param labelText Label text
     * @param value The value of the text field
     */
    public LabeledTextField(HBox container, String labelText, Object value) {
        this((Pane) container, labelText, value);
        container.setAlignment(Pos.CENTER);
    }

    public Pane getContainer() {
        return container;
    }

    public String getLabelText() {
        return label.getText();
    }

    public void setLabelText(String text) {
        label.setText(text);
    }

    /**
     * Gets the value of the text field.
     *
     * @return Text field value
     */
    public String getValue() {
        return textField.getText();
    }

    /**
     * Sets the value of the text field.
     *
     * @param value The value to set the text field to
     */
    public void setValue(Object value) {
        textField.setText(value.toString());
    }
}
