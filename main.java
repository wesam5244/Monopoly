import javafx.application.Application;
import javafx.stage.Stage;


public class main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Monopoly");
        //Start of the game
        MainMenu m = new MainMenu(primaryStage);
        m.drawScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
