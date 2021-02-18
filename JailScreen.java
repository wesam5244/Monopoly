import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;

/*
Whenever
 */
public class JailScreen {
    private Player player;
    private Group jailRoot;
    private ArrayList<Rectangle> rects;
    private String[] turns = {"1st", "2nd", "3rd"};

    public JailScreen(Player p) {
        player = p;
        jailRoot = new Group();
        rects = new ArrayList<>();
    }

    public int getRects() {
        return rects.size();
    }

    public ArrayList<Rectangle> getAllRects() {
        return rects;
    }

    public Group drawScreen() {
        Group jailRoot = new Group();
        ImageView background = makeImage("firstback.jpg", 0, 0, 1250, 650);
        jailRoot.getChildren().add(background);
        Text titleText = makeText("Now that you're in jail, choose one of the following options", 400, 50, Font.font("Gabriola", 35), Color.BLACK);
        jailRoot.getChildren().add(titleText);
        drawCurrPlayer();
        int count = 1;
        for (int i = 0; i < 3; i++) {
            Rectangle r = makeRect(125 + (375 * i), 100, 250, 400, Color.LIGHTGREEN);
            r.setStroke(Color.BLACK);
            if (i == 0) {
                if (player.getMoney() >= 50) {
                    rects.add(r);
                    jailRoot.getChildren().add(r);
                    Text firstChoice = makeText(count + ". Pay the fine of $50", 130 + (375 * i), 140, Font.font("Perpetua", 30), Color.BLACK);
                    jailRoot.getChildren().add(firstChoice);
                    count++;
                }
            } else if (i == 1) {
                rects.add(r);
                jailRoot.getChildren().add(r);
                Text secondChoice = makeText(count + ". Roll doubles on any\nof your three turns\nsince landing in jail.\n\nSo far, this is your " + turns[player.getJailTurn() - 1] + " \nturn since you've\nlanded in jail", 505, 140, Font.font("Perpetua", 30), Color.BLACK);
                jailRoot.getChildren().add(secondChoice);
                count++;
            } else {
                if (!player.hasJailFree().isEmpty()) {
                    rects.add(r);
                    jailRoot.getChildren().add(r);
                    Text thirdChoice = makeText(count + ". Use your Get Out\nof Jail Free card", 880, 140, Font.font("Perpetua", 30), Color.BLACK);
                    jailRoot.getChildren().add(thirdChoice);
                    count++;
                    ImageView card;
                    for (int x = 0; x < 2; x++) {
                        if (player.getJailFree(x).equals("Community Chest")) {
                            card = makeImage("communityFront.jpg", 900, 190 + (150 * x), 200, 140);
                            jailRoot.getChildren().add(card);
                        } else if (player.getJailFree(x).equals("Chance")) {
                            card = makeImage("chanceFront.jpg", 900, 190 + (150 * x), 200, 150);
                            jailRoot.getChildren().add(card);
                        }
                    }
                }
            }
        }
        return jailRoot;
    }

    public void drawCurrPlayer() {
        Rectangle rect = makeRect(0, 0, 200, 100, Color.LIGHTBLUE);
        jailRoot.getChildren().add(rect);
        ImageView playerPiece = makeImage(player.getIMG(), 50, 50, 100, 50);
        jailRoot.getChildren().add(playerPiece);
        String mon = player.getName() + ": $" + Integer.toString(player.getMoney());
        Text nameText = makeText(mon, 0, 20, Font.font("Comic Sans MS", 18), Color.BLACK);
        jailRoot.getChildren().add(nameText);
    }

    public ImageView makeImage(String url, int x, int y, int width, int height) {
        ImageView img = new ImageView(new Image(url));
        img.setX(x);
        img.setY(y);
        img.setFitWidth(width);
        img.setFitHeight(height);
        return img;
    }

    public Rectangle makeRect(int x, int y, int width, int height, Color c) {
        Rectangle r = new Rectangle();
        r.setX(x);
        r.setY(y);
        r.setWidth(width);
        r.setHeight(height);
        r.setFill(c);
        return r;
    }

    public Text makeText(String text, int x, int y, Font f, Color c) {
        Text t = new Text();
        t.setText(text);
        t.setX(x);
        t.setY(y);
        t.setFont(f);
        t.setFill(c);
        return t;
    }
}
