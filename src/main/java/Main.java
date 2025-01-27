import javafx.stage.Stage;

/**
 * A class used to launch the application from a jar file.
 *
 * @author Evan Razzaque
 */
public class Main {
    /**
     * A method to launch the application
     * @param args Command line args to change one or more options for the game. See {@link Game#start(Stage)} for
     * more details.
     */
    public static void main(String[] args) {
        Game.main(args);
    }
}
