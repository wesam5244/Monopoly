import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.util.Random;

/*
This is the screen where each player rolls their dice in order to decide on the starting player
 */
public class DiceScreen {
    private Group root;
    private Scene scene;
    private String[] pics = {"dice1.png", "dice2.png", "dice3.png", "dice4.png", "dice5.png", "dice6.png"}; //The pictures of all of the faces of the dice
    private Random rand; //Used to randomly select the dice roll
    private int choice = 0, highestRoll = 0, startPlayer = 0; //The current face of the dice chosen, the highest roll out of all of the three, and the first player to start the game
    private ImageView[] dicePic = new ImageView[2]; //Array containing the images of the two randomly chosen faces of the dice
    private Rectangle[] playerInfoRects;
    private Player[] players;
    private TranslateTransition trans;
    private Text highestText;
    private StackPane s;

    public DiceScreen(Group root, Player[] p, Scene scene) {
        this.root = root;
        this.scene = scene;
        players = p; //Set the players Array in this class equal to the p Array passed into this constructor
        root.getChildren().clear(); //Clear the current root as the entire screen will change
        rand = new Random();
    }

    //This method draws the rectangles for each of the player's name and piece
    public void drawPlayerRects(Player[] players) {
        for (int i = 0; i < players.length; i++) {
            playerInfoRects[i] = new Rectangle();
            playerInfoRects[i].setX(100 + (300 * i));
            playerInfoRects[i].setY(575);
            playerInfoRects[i].setWidth(200);
            playerInfoRects[i].setHeight(150);
            playerInfoRects[i].setFill(Color.BEIGE);
            playerInfoRects[i].setStroke(Color.BLACK);
            playerInfoRects[i].setStrokeWidth(2);
            root.getChildren().add(playerInfoRects[i]);

            Text t = makeText(players[i].getName(), (110 + (300 * i)), 600, Font.font("Trebuchet MS", 20), Color.NAVY);
            root.getChildren().add(t);
            ImageView img = makeImg(players[i].getIMG(), (150 + 300 * i), 610, 75, 40);
            root.getChildren().add(img);
        }
    }

    public void startDiceScreen() {
        ImageView imageView = makeImg("firstback.jpg", 0, 0, 1250, 650);
        root.getChildren().add(imageView);
        playerInfoRects = new Rectangle[players.length];
        drawPlayerRects(players);
        Text start1 = makeText("Now it's time to choose who goes first!\n", 400, 250, Font.font("Trebuchet MS", 30), Color.BLACK);
        root.getChildren().add(start1);

        Text start2 = makeText("The player with the highest roll will go first!", 350, 300, Font.font("Trebuchet MS", 30), Color.BLACK);
        root.getChildren().add(start2);

        Button cont = makeBtn("Continue", 575, 350, 100, 50);
        root.getChildren().add(cont);
        cont.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                transitionScreen(start1, start2, cont);
            }
        });
    }

    //This method controls the initial moving of the dice from the corners of the screen to the middle of the screen
    public void transitionScreen(Text t1, Text t2, Button b) {
        root.getChildren().remove(t1);
        root.getChildren().remove(t2);
        root.getChildren().remove(b);
        choice = rand.nextInt(6);
        dicePic[0] = makeImg(pics[choice], -30, 0, 20, 20);
        //The translation in which the right dice moves from the corner of the screen to the middle
        TranslateTransition imgTran = makeMove(1.0, 550, 240, 1);
        imgTran.setNode(dicePic[0]);

        //As the right dice moves from the corner to the middle, the picture of the die gets larger in size,
        //and this transition takes care of that
        ScaleTransition translate = new ScaleTransition();
        translate.setToX(5);
        translate.setToY(5);
        translate.setDuration(Duration.seconds(1));
        translate.setCycleCount(1);
        translate.setNode(dicePic[0]);

        //Same as the above transition, but for the left dice
        choice = rand.nextInt(6);
        dicePic[1] = makeImg(pics[choice], 1250, 0, 20, 20);
        TranslateTransition imgTran2 = makeMove(1.0, -550, 240, 1);
        imgTran2.setNode(dicePic[1]);

        ScaleTransition translate2 = new ScaleTransition();
        translate2.setToX(5);
        translate2.setToY(5);
        translate2.setDuration(Duration.seconds(1));
        translate2.setCycleCount(1);
        translate2.setNode(dicePic[1]);
        imgTran.play();
        translate.play();
        imgTran2.play();
        translate2.play();
        root.getChildren().add(dicePic[0]);
        root.getChildren().add(dicePic[1]);
        chooseFirst(0);
    }

    public void chooseFirst(int curr) {
        if (curr > 0) {
            if (curr > 1) root.getChildren().remove(highestText);
            String text = "Highest roll: " + highestRoll + " (" + players[startPlayer].getName() + ")";
            highestText = makeText(text, 425, 100, Font.font("Segoe Print", 40), Color.BLACK);
            root.getChildren().add(highestText);
            playerInfoRects[curr - 1].setFill(Color.BEIGE); //Set the previous rectangle's color back to the original color
        }
        if (curr != players.length) {
            playerInfoRects[curr].setFill(Color.BURLYWOOD);
            trans = makeMove(1.2, 0, -5, -1);
            trans.setNode(playerInfoRects[curr]);
            trans.setAutoReverse(true);
            trans.setCycleCount(2);
            trans.setOnFinished((ActionEvent event) -> trans.play());
            trans.play();
            s = new StackPane();
            s.setLayoutX(0);
            s.setLayoutY(310);
            Rectangle rect = new Rectangle();
            rect.setWidth(1250);
            rect.setHeight(80);
            rect.setFill(Color.TRANSPARENT);
            String text = players[curr].getName() + ", roll the dice!";
            Text t = new Text(text);
            t.setFont(Font.font("Trebuchet MS", 30));
            t.setFill(Color.BLACK);
            s.getChildren().addAll(rect, t);
            root.getChildren().add(s);
            Button rotate = makeBtn("Rotate", 575, 390, 100, 50);
            root.getChildren().add(rotate);
            rotate.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    rotateDice(dicePic, curr, rotate, t);
                }
            });
        } else {
            root.getChildren().remove(dicePic[0]);
            root.getChildren().remove(dicePic[1]);
            root.getChildren().remove(highestText);
            String text = players[startPlayer].getName() + ", you're going to be going first!!!";
            Text winner = new Text(text);
            winner.setFont(Font.font("Trebuchet MS", 40));
            winner.setFill(Color.BLACK);
            s.getChildren().add(winner);
            Button cont = makeBtn("Continue", 575, 390, 100, 50);
            root.getChildren().add(cont);
            cont.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    GameScreen g = new GameScreen(root, players, players.length, startPlayer, scene);
                    g.controlGame();
                }
            });
        }
    }

    public void rotateDice(ImageView[] dicePic, int curr, Button b, Text t) {
        s.getChildren().remove(t);
        root.getChildren().remove(b);
        final int[] total = {0};
        RotateTransition r = new RotateTransition();
        r.setAxis(Rotate.Z_AXIS);
        r.setFromAngle(0);
        r.setToAngle(-360);
        r.setDuration(Duration.seconds(1));
        r.setNode(dicePic[0]);
        r.setOnFinished(event -> {
            root.getChildren().remove(dicePic[0]);
            choice = rand.nextInt(6);
            total[0] += (choice + 1);
            dicePic[0] = new ImageView(new Image(pics[choice]));
            dicePic[0].setX(480);
            dicePic[0].setY(200);
            dicePic[0].setFitHeight(100);
            dicePic[0].setFitWidth(100);
            root.getChildren().add(dicePic[0]);
        });
        RotateTransition r2 = new RotateTransition();
        r2.setAxis(Rotate.Z_AXIS);
        r2.setFromAngle(0);
        r2.setToAngle(360);
        r2.setDuration(Duration.seconds(1));
        r2.setNode(dicePic[1]);
        r2.setOnFinished(event -> {
            root.getChildren().remove(dicePic[1]);
            choice = rand.nextInt(6);
            total[0] += (choice + 1);
            //In order to make sure that two players don't roll the same number, we change it so that they aren't the same
            while (total[0] == highestRoll) {
                total[0] -= (choice + 1);
                choice = rand.nextInt(6);
                total[0] += (choice + 1);
            }
            dicePic[1] = new ImageView(new Image(pics[choice]));
            dicePic[1].setX(660);
            dicePic[1].setY(200);
            dicePic[1].setFitHeight(100);
            dicePic[1].setFitWidth(100);
            root.getChildren().remove(b);
            root.getChildren().add(dicePic[1]);
            if (total[0] > highestRoll) {
                highestRoll = total[0];
                startPlayer = curr;
            }
            String text = players[curr].getName() + ", you rolled a " + total[0] + "!";
            Text resultText = makeText(text, 500, 350, Font.font("Trebuchet MS", 30), Color.BLACK);
            s.getChildren().add(resultText);
            continueButton(curr, resultText, s);
        });
        r.play();
        r2.play();
    }

    public void continueButton(int curr, Text t, StackPane s) {
        Button cont = makeBtn("Continue", 575, 390, 100, 50);
        root.getChildren().add(cont);
        cont.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                s.getChildren().remove(t);
                root.getChildren().remove(cont);
                chooseFirst(curr + 1);
            }
        });
    }

    public ImageView makeImg(String url, int x, int y, int w, int h) {
        ImageView i = new ImageView(new Image(url));
        i.setX(x);
        i.setY(y);
        i.setFitWidth(w);
        i.setFitHeight(h);
        return i;
    }

    public Text makeText(String text, int x, int y, Font f, Color c) {
        Text t = new Text(text);
        t.setX(x);
        t.setY(y);
        t.setFont(f);
        t.setFill(c);
        return t;
    }

    public Button makeBtn(String t, int x, int y, int w, int h) {
        Button b = new Button(t);
        b.setLayoutX(x);
        b.setLayoutY(y);
        b.setPrefSize(w, h);
        return b;
    }

    public TranslateTransition makeMove(double seconds, int toX, int toY, int cycles) {
        TranslateTransition t = new TranslateTransition();
        t.setDuration(Duration.seconds(seconds));
        t.setToY(toY);
        t.setToX(toX);
        if (cycles != -1) t.setCycleCount(cycles);
        return t;
    }
}
