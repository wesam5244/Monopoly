import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/*
This class is the main menu for the game from which you can start the game
 */
public class MainMenu {
    private Group root; //Will be the root used throughout the game
    private Stage primaryStage;
    private Scene scene;

    public MainMenu(Stage primaryStage) {
        this.primaryStage = primaryStage;
        root = new Group();
    }

    public void drawScreen() {
        ImageView background = makeImg("secondBack.jpg", 0, 0, 1250, 650); //Background of the starting menu
        root.getChildren().add(background);
        ImageView logo = makeImg("monopolySign.png", 425, 50, 475, 200); //Monopoly logo
        root.getChildren().add(logo);
        Rectangle startGame = new Rectangle();
        startGame.setX(550);
        startGame.setY(300);
        startGame.setWidth(250);
        startGame.setHeight(70);
        startGame.setStroke(Color.BLACK);
        startGame.setFill(Color.CORNFLOWERBLUE);
        root.getChildren().add(startGame);
        Text startText = new Text("Start Game");
        startText.setX(590);
        startText.setY(345);
        startText.setFont(Font.font("Arial", 35));
        startText.setFill(Color.BLACK);
        root.getChildren().add(startText);
        Rectangle startButton = new Rectangle();
        startButton.setX(550);
        startButton.setY(300);
        startButton.setWidth(250);
        startButton.setHeight(70);
        startButton.setFill(Color.TRANSPARENT);
        root.getChildren().add(startButton);
        scene = new Scene(root, 1250, 650);
        startButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                StartWindow s = new StartWindow(root, scene);
                s.startScreen();
            }
        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public ImageView makeImg(String url, int x, int y, int w, int h) {
        ImageView img = new ImageView(new Image(url));
        img.setX(x);
        img.setY(y);
        img.setFitWidth(w);
        img.setFitHeight(h);
        return img;
    }
}
