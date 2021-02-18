import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/*
This is the screen where the players choose their names and the pieces that they will use throughout the game
 */
public class StartWindow {
    private Group root;
    private Scene scene;
    private Rectangle[] carRects = new Rectangle[4]; //The rectangles that is used for each piece
    private String[] pics = {"redcar.png", "yellowcar.png", "whitecar.png", "orangecar.png"}; //the pieces themselves
    private Color[] colors = {Color.CRIMSON, Color.GOLD, Color.GHOSTWHITE, Color.CORAL}; //the colors that are associated with each piece
    private boolean[] chosen = {false, false, false, false}; //keeps track of which of the pieces have been chosen o far
    private Rectangle selection = new Rectangle();
    private int playerNum;
    private Player[] players;


    public StartWindow(Group root, Scene scene) {
        this.root = root;
        root.getChildren().add(selection);
        this.scene = scene;
    }

    public void startScreen() {
        Image image = new Image("secondBack.jpg");
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(1250);
        imageView.setFitHeight(650);
        imageView.setX(0);
        imageView.setY(0);
        root.getChildren().add(imageView);

        Text welcomeText = makeText("Welcome!", 540, 150, Font.font("Verdana", 50), Color.BLACK);
        root.getChildren().add(welcomeText);
        choosePlayers();
    }

    //This method takes care of choosing how many players will play in the game
    public void choosePlayers() {
        Text nameText = makeText("How many players are going to play?", 440, 250, Font.font("Verdana", 25), Color.BLACK);
        root.getChildren().add(nameText);
        //This is the creation of the 3 buttons that the user will choose the number of players from: 2, 3, or 4 players in the game
        for (int i = 0; i < 3; i++) {
            Rectangle r = makeRect(400 + (180 * i), 300, 140, 60, Color.SPRINGGREEN);
            r.setStroke(Color.BLACK);
            Text oneText = makeText(Integer.toString(i + 2), (int) r.getX() + 62, 337, Font.font("Verdana", 25), Color.BLACK);
            Rectangle oneButton = makeRect(400 + (180 * i), 300, 140, 60, Color.TRANSPARENT);
            int finalI = i;
            oneButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    playerNum = finalI + 2; //Set the number of the players
                    getPlayers(); //Start getting the info for each player
                }
            });
            root.getChildren().add(r);
            root.getChildren().add(oneText);
            root.getChildren().add(oneButton);
        }
    }

    public void getPlayers() {
        players = new Player[playerNum]; //Now create the players Array that will be used throughout the game
        playerInfo(0);
    }

    public void playerInfo(int count) {
        if (count == playerNum) {
            DiceScreen d = new DiceScreen(root, players, scene);
            d.startDiceScreen();
        } else {
            root.getChildren().clear(); //Had to clear the root due to difficulties with removing the previous buttons from the current root
            Image image = new Image("secondBack.jpg");
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(1250);
            imageView.setFitHeight(650);
            imageView.setX(0);
            imageView.setY(0);
            root.getChildren().add(imageView);
            Text welcomeText = makeText("Welcome!", 540, 150, Font.font("Verdana", 50), Color.BLACK);
            root.getChildren().add(welcomeText);

            drawPlayerRects(count);
            players[count] = new Player(); //First create a new Player object at the current position
            String text = "Player " + (count + 1) + ", enter your name: ";
            Text playerText = makeText(text, 480, 240, Font.font("Verdana", 25), Color.BLACK);
            root.getChildren().add(playerText);
            TextField playerField = new TextField("Name");
            playerField.setLayoutX(565);
            playerField.setLayoutY(260);
            playerField.setPrefHeight(40);
            //When Enter is pressed, the current player's name is set to the text in the TextField
            EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    players[count].setName(playerField.getText());
                }
            };
            playerField.setOnAction(event);
            root.getChildren().add(playerField);
            drawPieces(playerText, playerField, count);
        }
    }

    //This method draws all the pieces that the player can choose from to use throughout the game
    public void drawPieces(Text t, TextField f, int count) {
        for (int i = 0; i < 4; i++) {
            //First check if the current piece is chosen by another player already
            if (!chosen[i]) {
                Rectangle rect = makeRect(400 + (120 * i), 320, 120, 120, Color.TRANSPARENT);
                carRects[i] = rect;
                rect.setStroke(Color.BLACK);
                root.getChildren().add(rect);

                ImageView piecePic = new ImageView(new Image(pics[i]));
                piecePic.setFitHeight(50);
                piecePic.setFitWidth(100);
                piecePic.setX(410 + (120 * i));
                piecePic.setY(350);
                root.getChildren().add(piecePic);

                Rectangle button = makeRect(400 + (120 * i), 320, 120, 120, Color.TRANSPARENT);
                root.getChildren().add(button);
                Button choose = new Button("Choose");
                choose.setLayoutX(button.getX() + 30);
                choose.setLayoutY(450);
                choose.setPrefSize(60, 30);
                int finalI = i;
                choose.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        checkBox(finalI, rect, count);
                    }
                });
                root.getChildren().add(choose);
            }
        }
    }

    //When the Choose button is pressed, this method is invoked to create the Continue button and go ahead with then next steps
    public void checkBox(int choice, Rectangle r, int count) {
        for (int i = 0; i < 4; i++) {
            if (carRects[i].getFill().equals(Color.TURQUOISE)) carRects[i].setFill(Color.TRANSPARENT);
        }
        r.setFill(Color.TURQUOISE);
        Button start = new Button("Continue");
        start.setLayoutX(580);
        start.setLayoutY(500);
        start.setPrefSize(100, 50);
        EventHandler<ActionEvent> sButton = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //If the text in the TextField is not empty, we first check if that name is not used already. If it is used
                //already, then the user is prompted to enter a new name. Otherwise, the current name is used.
                if (!players[count].getName().isEmpty()) {
                    if (isSameName(players[count].getName(), count)) {
                        Text reminder = makeText("That name has already been used, choose a new one!", 690, 530, Font.font("Verdana", 20), Color.BLACK);
                        root.getChildren().add(reminder);
                    } else {
                        chosen[choice] = true;
                        players[count].setIMG(pics[choice]);
                        players[count].setColor(colors[choice]);
                        playerInfo(count + 1); //Invoke the method that gets the next player's info
                    }
                } else {
                    Text reminder = makeText("Enter a name for your player!", 710, 535, Font.font("Verdana", 25), Color.BLACK);
                    root.getChildren().add(reminder);
                }
            }
        };
        start.setOnAction(sButton);
        root.getChildren().add(start);
    }

    //This method checks if the current name has already been used by another player
    public boolean isSameName(String name, int count) {
        for (int i = 0; i < count; i++) {
            if (players[i].getName().equals(name)) return true;
        }
        return false;
    }

    //After a player is created, this method draws a rectangle at the bottom of the screen with the info on
    //their name and the piece that they have chosen to use
    public void drawPlayerRects(int count) {
        for (int i = 0; i < count; i++) {
            Rectangle r = new Rectangle();
            r.setX(100 + (300 * i));
            r.setY(575);
            r.setWidth(200);
            r.setHeight(150);
            r.setFill(Color.BEIGE);
            r.setStroke(Color.BLACK);
            r.setStrokeWidth(2);
            root.getChildren().add(r);

            Text t = makeText(players[i].getName(), (110 + (300 * i)), 600, Font.font("Trebuchet MS", 20), Color.NAVY);
            root.getChildren().add(t);

            ImageView img = new ImageView(new Image(players[i].getIMG()));
            img.setX(150 + (300 * i));
            img.setY(610);
            img.setFitWidth(75);
            img.setFitHeight(40);
            root.getChildren().add(img);
        }
    }

    public Text makeText(String text, int x, int y, Font f, Color c) {
        Text t = new Text(text);
        t.setX(x);
        t.setY(y);
        t.setFont(f);
        t.setFill(c);
        return t;
    }

    public Rectangle makeRect(int x, int y, int w, int h, Color c) {
        Rectangle r = new Rectangle();
        r.setX(x);
        r.setY(y);
        r.setWidth(w);
        r.setHeight(h);
        r.setFill(c);
        return r;
    }
}
