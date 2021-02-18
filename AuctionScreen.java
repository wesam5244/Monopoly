import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/*
When a property is to be auctioned, this is the class that is invoked
 */
public class AuctionScreen {
    private Group aucRoot;
    private String currBid = "", currCom;
    private int highestBid = 0, digits = 0, i = 0, highestBidPlayer = 0;
    private Text currPlayer, highestBidText;
    private Rectangle[] playerRects;
    private Player[] players;
    private Property property;
    private boolean[] quitPlayers;
    private ArrayList<Integer> highestBids = new ArrayList<>();
    private ArrayList<Integer> highestBidPlayers = new ArrayList<>();

    public AuctionScreen(Player[] players, Property property) {
        aucRoot = new Group();
        this.players = players;
        this.property = property;
        int len = 0;
        //Only the players who are not bankrupt are able to participate in the auction, so we need
        //to go through the players Array and see how many are eligible
        for (int i = 0; i < players.length; i++) {
            if (!players[i].isBankrupt()) len++;
        }
        playerRects = new Rectangle[len];
        quitPlayers = new boolean[len];
    }

    public int getI() {
        return i;
    }

    public void setCurrBid(String s) {
        currBid = s;
    }

    public String getCurrBid() {
        return currBid;
    }

    public void setQuit(int i) {
        quitPlayers[i] = true;
    }

    public int getQuit() {
        int done = 0;
        for (int i = 0; i < quitPlayers.length; i++) {
            if (quitPlayers[i]) done++;
        }
        return done;
    }

    //This method looks for the players who's turn will be next
    public void findNextPlayer() {
        //The current player's rectangle is filled back to the original color
        playerRects[i].setFill(Color.BEIGE);
        //Keep on looking through the players until you find one who has not quit
        while (true) {
            if (i == (playerRects.length - 1)) i = 0;
            else ++i;
            if (!quitPlayers[i]) break;
        }
    }

    public int getHighestBid() {
        return highestBid;
    }

    public int getLen() {
        return playerRects.length;
    }

    public Group drawScreen() {
        ImageView background = makeImg("auctionBack.jpg", 0, 0, 1250, 650);
        aucRoot.getChildren().add(background);

        ImageView propImg = makeImg(property.getImg(), 75, 100, 325, 400);
        aucRoot.getChildren().add(propImg);
        drawPlayerRects();
        Rectangle rect = makeRegRect(640, 170, 200, 44, Color.DARKGOLDENROD);
        aucRoot.getChildren().add(rect);

        showHighest();
        showCurr();
        String currText = "Current Bid: $" + currBid;
        Text currBidText = makeText(currText, 500, 200, Font.font("Trebuchet MS", 25), Color.KHAKI);
        aucRoot.getChildren().add(currBidText);

        Rectangle passRect = makeRegRect(900, 170, 130, 44, Color.LIGHTSALMON);
        Text passText = makeText("PASS", 935, 200, Font.font("Trebuchet MS", 25), Color.BLACK);
        Rectangle ontopRect = makeRegRect(900, 170, 130, 44, Color.TRANSPARENT);
        ontopRect.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                findNextPlayer();
                currBid = "";
                nextTurn();
            }
        });
        aucRoot.getChildren().add(passRect);
        aucRoot.getChildren().add(passText);
        aucRoot.getChildren().add(ontopRect);

        String endCondition = "*Once " + (players.length - 1) + " players quit, the remaining player will be the winner of the auction";
        Text endCon = makeText(endCondition, 450, 510, Font.font("Dubai", 25), Color.KHAKI);
        aucRoot.getChildren().add(endCon);

        for (int i = 0; i < 9; i++) {
            String t = Integer.toString(i + 1);
            makeRect(t, 500 + ((i % 3) * 200), 250 + ((i / 3) * 60), 150, 50, currBidText, players);
        }
        makeRect("Submit", 500, 430, 150, 50, currBidText, players);
        makeRect("0", 700, 430, 150, 50, currBidText, players);
        makeRect("", 900, 430, 150, 50, currBidText, players);
        return aucRoot;
    }


    public void drawPlayerRects() {
        int count = 0;
        for (int i = 0; i < players.length; i++) {
            if (!players[i].isBankrupt()) {
                playerRects[count] = makeRegRect(100 + (300 * count), 575, 200, 75, Color.BEIGE);
                aucRoot.getChildren().add(playerRects[count]);

                String text = players[i].getName() + ":$" + players[i].getMoney();
                Text t = makeText(text, 110 + (300 * count), 600, Font.font("Trebuchet MS", 20), Color.DARKCYAN);
                aucRoot.getChildren().add(t);

                ImageView playerImg = makeImg(players[i].getIMG(), 150 + (300 * count), 610, 75, 40);
                aucRoot.getChildren().add(playerImg);
                count++;
            }
        }
    }

    //This method is invoked whenever a player quits the auction
    public void removePlayer(int pos) {
        //This is for the case in which the player with the highest bid quits
        if (highestBidPlayer == pos) {
            //If there is no highest bid or there is only one highest bid, then the next available player will have the
            //"highest bid" which will be 0
            if (highestBids.size() == 0 || highestBids.size() == 1) {
                findNextPlayer();
                highestBidPlayer = i;
            }
            //If more than one highest bid has been placed in the past, then the next lowest highest bid that was not
            //placed by the player who is going to quit will be the new highest bid
            else {
                for (int i = highestBids.size() - 1; i >= 0; i--) {
                    if (highestBidPlayers.get(i) == pos) {
                        highestBids.remove(i);
                        highestBidPlayers.remove(i);
                    } else {
                        highestBid = highestBids.get(i);
                        highestBidPlayer = highestBidPlayers.get(i);
                        break;
                    }
                }
            }
            aucRoot.getChildren().remove(highestBidText);
            showHighest();
        }
        Line l = new Line();
        l.setStartX(playerRects[pos].getX());
        l.setStartY(playerRects[pos].getY());
        l.setEndX(playerRects[pos].getX() + playerRects[pos].getWidth());
        l.setEndX(playerRects[pos].getX() + playerRects[pos].getWidth());
        l.setEndY(playerRects[pos].getY() + playerRects[pos].getHeight());
        aucRoot.getChildren().add(l);

        Line l2 = new Line();
        l2.setStartX(playerRects[pos].getX());
        l2.setStartY(playerRects[pos].getY() + playerRects[pos].getHeight());
        l2.setEndX(playerRects[pos].getX() + playerRects[pos].getWidth());
        l2.setEndY(playerRects[pos].getY());
        aucRoot.getChildren().add(l2);
    }

    //This method displays the current bid that is the highest
    public void showHighest() {
        String t;
        //If the highest bid is 0, that means that there hasn't been a bid placed yet
        if (highestBid == 0) t = "Highest Bid: None";
        else t = "Highest Bid: $" + highestBid + "(" + players[highestBidPlayer].getName() + ")";
        highestBidText = makeText(t, 600, 75, Font.font("SimSun", 50), Color.MOCCASIN);
        aucRoot.getChildren().add(highestBidText);
    }

    //This method displays who's turn it is to place the bid
    public void showCurr() {
        playerRects[i].setFill(Color.BURLYWOOD);
        String t = players[i].getName() + ", make your bid!";
        currPlayer = makeText(t, 650, 150, Font.font("Trebuchet MS", 30), Color.KHAKI);
        aucRoot.getChildren().add(currPlayer);
    }

    public void repeatBid(String t) {
        if (t.contains("invalid")) currPlayer = makeText(t, 550, 150, Font.font("Trebuchet MS", 30), Color.KHAKI);
        else currPlayer = makeText(t, 430, 150, Font.font("Trebuchet MS", 25), Color.KHAKI);
        aucRoot.getChildren().add(currPlayer);
    }

    //This is the method that transitions between one player to another player's turn
    public void nextTurn() {
        aucRoot.getChildren().remove(currPlayer);
        showCurr();
        digits = 0;
        Rectangle rect = makeRegRect(640, 170, 200, 44, Color.DARKGOLDENROD);
        aucRoot.getChildren().add(rect);
    }

    //This is the method that is primarily used to create the buttons for the bid amount
    public void makeRect(String text, int x, int y, int w, int h, Text removeText, Player[] players) {
        Rectangle rect = makeRegRect(x, y, w, h, Color.AQUAMARINE);
        rect.setStrokeType(StrokeType.INSIDE);
        rect.setStroke(Color.BLACK);
        aucRoot.getChildren().add(rect);
        //Drawing the text on top of each of the buttons
        if (!text.isEmpty()) {
            Text t;
            if (text.equals("Submit")) {
                t = makeText(text, x + 20, y + 35, Font.font("Trebuchet MS", 30), Color.BLACK);
            } else t = makeText(text, x + ((w / 2) - 10), y + 35, Font.font("Trebuchet MS", 30), Color.BLACK);
            aucRoot.getChildren().add(t);
        } else {
            ImageView backImg = makeImg("backarrow.png", 950, 430, 50, 50);
            aucRoot.getChildren().add(backImg);
        }

        Rectangle r2 = makeRegRect(x, y, w, h, Color.TRANSPARENT);
        r2.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //If the text is empty, that means that this is the back button. In this case, the final digit of the
                //bid is removed
                if (text.isEmpty()) {
                    if (!currBid.isEmpty()) {
                        if (digits == 1) {
                            currBid = "";
                            --digits;
                            drawCurrBid(removeText);

                        } else {
                            currBid = currBid.substring(0, --digits);
                            drawCurrBid(removeText);
                        }
                    }
                } else if (text.equals("Submit")) {
                    aucRoot.getChildren().remove(currPlayer);
                    if (currBid == "") repeatBid(players[i].getName() + ", this is an invalid bid! Try again!");
                    else if (Integer.parseInt(currBid) <= highestBid)
                        repeatBid(players[i].getName() + ", you must place a bid greater than the current bid! Try again!");
                    else if ((highestBidPlayer == i || Integer.parseInt(currBid) > highestBid) && Integer.parseInt(currBid) <= players[i].getMoney()) {
                        highestBidPlayer = i;
                        if (Integer.parseInt(currBid) > highestBid) {
                            highestBid = Integer.parseInt(currBid);
                            highestBids.add(highestBid);
                            highestBidPlayers.add(highestBidPlayer);
                        }
                        findNextPlayer();
                        showCurr();
                        aucRoot.getChildren().remove(highestBidText);
                        showHighest();
                    }
                    currBid = "";
                    digits = 0;
                    drawCurrBid(removeText);
                } else if (((text.equals("0")) && (!currBid.isEmpty())) || (!text.equals("0"))) {
                    String test = currBid.concat(text);
                    if (Integer.parseInt(test) <= players[i].getMoney()) {
                        currBid = currBid.concat(text);
                        drawCurrBid(removeText);
                        ++digits;
                    }
                }
            }
        });
        aucRoot.getChildren().add(r2);

    }

    //This method displays the current bid that the player is offering
    public void drawCurrBid(Text curr) {
        aucRoot.getChildren().remove(curr);
        Rectangle rect = makeRegRect(640, 170, 200, 44, Color.DARKGOLDENROD);
        aucRoot.getChildren().add(rect);

        String currText = "Current Bid: $" + currBid;
        Text currBidText = makeText(currText, 500, 200, Font.font("Trebuchet MS", 25), Color.KHAKI);
        aucRoot.getChildren().add(currBidText);
    }

    public int displayWinner(Property property) {
        aucRoot.getChildren().clear();
        ImageView background = makeImg("auctionBack.jpg", 0, 0, 1250, 650);
        aucRoot.getChildren().add(background);

        ImageView propImg = makeImg(property.getImg(), 500, 100, 325, 400);
        aucRoot.getChildren().add(propImg);

        String text = "Congratulations " + players[highestBidPlayer].getName() + ", you won the auction! Press Enter to return back to the game";
        drawText(text, 0, 0);
        players[highestBidPlayer].addProperty(property);
        property.setOwned(true);
        property.setOwner(players[highestBidPlayer]);
        return highestBidPlayer;
    }

    public void drawText(String text, int x, int y) {
        currCom = "";
        Rectangle AIRect = new Rectangle();
        AIRect.setWidth(550);
        AIRect.setHeight(70);
        AIRect.setFill(Color.IVORY);
        AIRect.setStroke(Color.BLACK);
        StackPane s = new StackPane();
        String[] textWords = text.split(" ");
        for (int i = 0; i < textWords.length; i++) {
            int newLine = currCom.lastIndexOf("\n");
            if (newLine == -1) {
                int currSize = currCom.length() + textWords[i].length() + 1;
                if (currSize > 80) currCom = currCom.concat("\n");
            } else {
                int currSize = (currCom.length() - newLine) + textWords[i].length() + 1;
                if (currSize > 80) currCom = currCom.concat("\n");
            }
            currCom = currCom.concat(" " + textWords[i]);
        }

        Text commentary = new Text();
        commentary.setFont(Font.font("Segoe Print"));
        s.setLayoutX(400);
        s.setLayoutY(0);
        s.getChildren().addAll(AIRect, commentary);
        aucRoot.getChildren().add(s);
        AtomicInteger i = new AtomicInteger(1);
        Timeline timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(
                Duration.seconds(0.05),
                event -> {
                    if (i.get() > currCom.length()) {
                        timeline.stop();
                    } else {
                        s.getChildren().remove(commentary);
                        commentary.setText(currCom.substring(0, i.get()));
                        i.set(i.get() + 1);
                        s.getChildren().add(commentary);
                    }
                });
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public ImageView makeImg(String url, int x, int y, int width, int height) {
        ImageView img = new ImageView(new Image(url));
        img.setX(x);
        img.setY(y);
        img.setFitHeight(height);
        img.setFitWidth(width);
        return img;
    }

    public Rectangle makeRegRect(int x, int y, int width, int height, Color c) {
        Rectangle rect = new Rectangle();
        rect.setX(x);
        rect.setY(y);
        rect.setWidth(width);
        rect.setHeight(height);
        rect.setFill(c);
        return rect;
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
