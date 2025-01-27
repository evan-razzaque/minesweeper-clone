package nodes;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import java.util.*;

/**
 * A class to implement a group of buttons.
 *
 * @author Evan Razzaque
 */
public class ButtonGroup extends Parent {
    private final Pane container;
    private final LinkedHashMap<String, Button> buttons;

    /**
     * A constructor to create a button group with a list of string names
     * as keys for each button.
     *
     * @param container The container to hold the buttons
     * @param buttonNames The names of the buttons
     */
    public ButtonGroup(Pane container, String ...buttonNames) {
        this.container = container;
        buttons = new LinkedHashMap<>(buttonNames.length);

        for (String buttonName : buttonNames) {
            Button button = new Button(buttonName);
            button.setMaxWidth(Double.MAX_VALUE);
            buttons.put(buttonName, button);
        }

        container.getChildren().addAll(buttons.values());
        getChildren().add(container);
    }

    /**
     * A method to get the container which holds the buttons.
     *
     * @return Button container
     */
    public Pane getContainer() {
        return container;
    }

    /**
     * A method to get a button by its name.
     *
     * @param name Button name
     * @return The button with the matching name, or null if none was found
     */
    public Button get(String name) {
        return buttons.get(name);
    }
}
