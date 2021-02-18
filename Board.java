import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Board implements GameInterface {
    private int[] bottomRow = {906, 855, 803, 751, 698, 645, 593, 540, 488, 436, 350};
    private int[] leftRow = {526, 476, 428, 381, 333, 285, 238, 191, 144, 70};
    private int[] topRow = {435, 487, 540, 592, 645, 698, 750, 802, 854, 906};
    private int[] rightRow = {145, 194, 240, 286, 333, 383, 432, 480, 527, 575};
    private int[] board = {-1, 0, 1, 0, 3, 0, 0, 2, 0, 0, -1, 0, 0, 0, 0, 0, 0, 1, 0, 0, -1, 0, 2, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 1, 0, 0, 2, 0, 5, 0};
    private ImageView cardBack;
    private Text cardText;
    private Queue<GameCard> chanceCards = new LinkedList<>();
    private Queue<GameCard> communityChestCards = new LinkedList<>();
    private HashMap<Integer, Property> properties = new HashMap<>();

    public Board() {
        getProperties();
        getCards();
    }

    public void getProperties() {
        try {
            Scanner s = new Scanner(new File("C:\\Users\\HP\\Game\\src\\properties.txt"));
            int i = 0;
            while (s.hasNextLine()) {
                Property p = new Property(s.nextLine());
                properties.put(p.getPos(), p);
                ++i;
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        }
    }

    public void getCards() {
        try {
            Scanner s = new Scanner(new File("C:\\Users\\HP\\Game\\src\\gamecards.txt"));
            int i = 0;
            while (s.hasNextLine()) {
                GameCard g = new GameCard(s.nextLine());
                if (g.getType().equals("Chance")) chanceCards.add(g);
                else communityChestCards.add(g);
                ++i;
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        }
    }

    public Property getProperty(int pos) {
        return properties.get(pos);
    }


    public void setProperty(Property p) {
        properties.replace(p.getPos(), p);
    }

    public GameCard getTopCommChest() {
        return communityChestCards.peek();
    }

    public GameCard getTopChance() {
        return chanceCards.peek();
    }

    public int getNextUtil(int pos) {
        while (true) {
            if (board[pos] == PROPERTY) {
                if (properties.get(pos).getColor().equals("Utility")) {
                    break;
                }
            }
            if (pos == 39) pos = 0;
            else ++pos;
        }
        return pos;
    }

    public int getNextRail(int pos) {
        while (true) {
            if (board[pos] == PROPERTY) {
                if (properties.get(pos).getColor().equals("White")) {
                    break;
                }
            }
            if (pos == 39) pos = 0;
            else ++pos;
        }
        return pos;
    }

    public ImageView getCardBack() {
        return cardBack;
    }

    public Text getCardText() {
        return cardText;
    }

    //This method is used to draw the rectangle underneath the player
    public Rectangle getPlayerRect(Player p, int pos) {
        int rectPos; //The position that the rectangle will be drawn at
        if (pos == -1) rectPos = p.getPlayerPos();
        else rectPos = pos;
        Rectangle rect = new Rectangle();
        //If the position is less than 10, then the starting x-value of the rectangle will be taken from the bottomRow array
        if (rectPos <= 10) {
            rect.setX(bottomRow[rectPos]);
            rect.setY(575);
            //The corner rectangles will have a longer width
            if (rectPos == 0) {
                rect.setWidth(85);
            } else if (rectPos == 10) rect.setWidth(85);
            else rect.setWidth(50);
            rect.setHeight(75);
        }
        //Must subtract 11 from the rectPos value because the leftRow Array starts from 0, so if you're at the 11th spot on the board, you will access the 0th x-value in the array
        else if (rectPos > 10 && rectPos <= 20) {
            rect.setX(350);
            rect.setY(leftRow[rectPos - 11]);
            if (rectPos == 20) rect.setHeight(75);
            else rect.setHeight(50);
            rect.setWidth(85);

        }
        //Must subtract 21 because of the same logic as above
        else if (rectPos > 20 && rectPos <= 30) {
            rect.setX(topRow[rectPos - 21]);
            rect.setY(70);
            if (rectPos == 30) rect.setWidth(85);
            else rect.setWidth(50);
            rect.setHeight(75);
        } else if (rectPos > 30 && rectPos <= 40) {
            rect.setX(906);
            rect.setY(rightRow[rectPos - 31]);
            if (rectPos == 40) {
                rect.setHeight(75);
            } else rect.setHeight(50);
            rect.setWidth(84);
        }
        rect.setFill(p.getC()); //Fill the rectangle with the color of the player
        return rect;
    }

    public int getSpot(int pos) {
        return board[pos];
    }

    //This really long method essentially takes care of the movement of the players around the board
    public SequentialTransition playerTransition(Player p, int oldPos, int newPos, ImageView img) {
        int nextTran;
        SequentialTransition s = new SequentialTransition();
        //The reason why the translations ArrayList is important is because the way that this method is structured is that all of the
        //TranslateTransitions are all actually made in opposite order. For example, if a player is moving from the 2nd row to Meditarrenean,
        //first TranslateTransition that is actually created is from Go to Mediterranean, then the transition from the Go to Jail spot to Go, then from
        //the Free Parking spot to the Go to Jail spot, and then the transition from the current spot to the Free Parking spot, therefore going in reverse order
        //instead of starting with the transaction of the current spot to the Free Parking spot. If we added it in this order to the SequentialTransaction, it would
        //result in an awkward reverse movement, so in the end of this method we add all the TranslateTransitions in the translations ArrayList starting from END of
        //the translations ArrayList
        ArrayList<TranslateTransition> translations = new ArrayList<>();
        TranslateTransition t = new TranslateTransition();
        t.setDuration(Duration.seconds(1.0));
        if (newPos <= 10) {
            //When the players reach the corner of the bottom row (the Just Visiting Jail spot), each piece is designated a
            //different x-value. The top piece goes all the way to the corner of the board, the piece underneath stops a little before that, etc. So
            //each player has a unique x-value for that spot that is accessed through the getLeftRowX() method if they are to move to the corner of the bottom row.
            if (newPos == 10) {
                t.setToX(-1 * ((img.getX() - p.getLeftRowX())));
                t.setOnFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        img.setRotate(img.getRotate() + 90); //Once you reach the corner, you need to rotate the piece 90 degrees
                    }
                });
            } else t.setToX(-1 * ((img.getX() - bottomRow[newPos]) - 10));
            t.setNode(img);
            translations.add(t);

            //This next series of transitions are for the case in which the player will have to pass Go in order to reach the
            //spot that they have rolled to
            if (oldPos <= 40 && oldPos > newPos) {
                t = fullMove(img, p, "right");
                translations.add(t);
            }

            if (oldPos < 30 && oldPos > newPos) {
                t = fullMove(img, p, "top");
                translations.add(t);
            }

            if (oldPos < 20 && oldPos > newPos) {
                t = fullMove(img, p, "left");
                translations.add(t);
            }

            if (oldPos < 10 && oldPos > newPos) {
                t = fullMove(img, p, "bottom");
                translations.add(t);
            }
        } else if (newPos > 10 && newPos <= 20) {
            nextTran = newPos - 11;
            t = new TranslateTransition();
            t.setDuration(Duration.seconds(1.0));
            if (newPos == 20) {
                t.setToY(-1 * ((img.getY() - p.getTopRowY())));
                t.setOnFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        img.setRotate(img.getRotate() + 90);
                    }
                });
            } else {
                t.setToY(-1 * ((img.getY() - leftRow[nextTran]) - 15));
            }
            t.setNode(img);
            translations.add(t);
            if (oldPos < 10 || oldPos > newPos) {
                t = fullMove(img, p, "bottom");
                translations.add(t);
            }
            if (oldPos <= 40 && oldPos > newPos) {
                t = fullMove(img, p, "right");
                translations.add(t);
            }
            if (oldPos < 30 && oldPos > newPos) {
                t = fullMove(img, p, "top");
                translations.add(t);
            }
            if (oldPos < 20 && oldPos > newPos) {
                t = fullMove(img, p, "left");
                translations.add(t);
            }
        } else if (newPos > 20 && newPos <= 30) {
            nextTran = newPos - 21;
            t = new TranslateTransition();
            t.setDuration(Duration.seconds(1.0));
            if (newPos == 30) {
                t.setToX(-1 * ((img.getX() - p.getRightRowX())));
                t.setOnFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        img.setRotate(img.getRotate() + 90);
                    }
                });
            } else t.setToX(-1 * ((img.getX() - topRow[nextTran]) - 15));
            t.setNode(img);
            translations.add(t);
            if (oldPos < 20 || oldPos > newPos) {
                t = fullMove(img, p, "left");
                translations.add(t);
            }
            if (oldPos < 10 || oldPos > newPos) {
                t = fullMove(img, p, "bottom");
                translations.add(t);
            }
            if (oldPos <= 40 && oldPos > newPos) {
                t = fullMove(img, p, "right");
                translations.add(t);
            }
            if (oldPos < 30 && oldPos > newPos) {
                t = fullMove(img, p, "top");
                translations.add(t);
            }
        } else if (newPos > 30 && newPos < 40) {
            nextTran = newPos - 31;
            t = new TranslateTransition();
            t.setDuration(Duration.seconds(1.0));
            t.setToY(-1 * ((img.getY() - rightRow[nextTran]) - 15));
            t.setNode(img);
            translations.add(t);
            if (oldPos < 30 || oldPos > newPos) {
                t = fullMove(img, p, "top");
                translations.add(t);
            }
            if (oldPos < 20 || oldPos > newPos) {
                t = fullMove(img, p, "left");
                translations.add(t);
            }
            if (oldPos < 10 || oldPos > newPos) {
                t = fullMove(img, p, "bottom");
                translations.add(t);
            }
            if (oldPos <= 40 && oldPos > newPos) {
                t = fullMove(img, p, "right");
                translations.add(t);
            }
        }
        //This is the part where we add the TranslateTransitions in reverse order
        for (int i = translations.size() - 1; i >= 0; i--) {
            s.getChildren().add(translations.get(i));
        }
        return s;
    }

    //If a player needs to move from a specific spot in a row to the corner of that row, this method is invoked
    public TranslateTransition fullMove(ImageView img, Player p, String row) {
        TranslateTransition t = new TranslateTransition();
        t.setDuration(Duration.seconds(1.5));
        if (row.equals("right")) t.setToY(-1 * ((img.getY() - p.getBottomRowY())));
        else if (row.equals("top")) t.setToX(-1 * ((img.getX() - p.getRightRowX())));
        else if (row.equals("left")) t.setToY(-1 * ((img.getY() - p.getTopRowY())));
        else t.setToX(-1 * ((img.getX() - p.getLeftRowX())));
        t.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                img.setRotate(img.getRotate() + 90);
            }
        });
        t.setNode(img);
        return t;
    }

    //This method draws the back of the community chest card on the game screen
    public ImageView drawCommChest(Group root) {
        ImageView frontCard = makeImg("communityFront.jpg", 500, 200, 350, 250);
        root.getChildren().add(frontCard);
        return frontCard;
    }

    public ImageView drawChance(Group root) {
        ImageView frontCard = makeImg("chanceFront.jpg", 500, 200, 350, 200);
        root.getChildren().add(frontCard);
        return frontCard;
    }

    //This method reveals the instruction on the top Community Chest and Chance card
    public Node[] revealCard(Group root, ImageView img, String type) {
        root.getChildren().remove(img);
        Node[] nodes = new Node[2];
        GameCard g;
        if (type.equals("Community Chest")) g = communityChestCards.peek();
        else g = chanceCards.peek();
        SequentialTransition s = new SequentialTransition();
        FadeTransition f = makeFade(0.2, 1.0, 0.0, 1);
        f.setNode(img);
        ParallelTransition p = new ParallelTransition();
        cardBack = makeImg("cardBack.jpg", 500, 200, 350, 250);
        FadeTransition f2 = makeFade(1.0, 0.0, 1.0, 1);
        f2.setNode(cardBack);
        p.getChildren().add(f2);
        root.getChildren().add(cardBack);
        String[] words = g.getText().split(" ");
        String text = "";
        int len = 0;
        //This is just splitting the string so that it is able to fit within the image of the card
        for (int i = 0; i < words.length; i++) {
            len = len + (words[i].length()) + 1;
            if (len > 27) {
                len = words[i].length() + 1;
                text = text.concat("\n " + words[i]);
            } else text = text.concat(" " + words[i]);
        }
        if (g.getText().contains("Advance token"))
            cardText = makeText(text, 530, 270, Font.font("Bodoni MT", 25), Color.BLACK);
        else cardText = makeText(text, 530, 300, Font.font("Bodoni MT", 25), Color.BLACK);
        FadeTransition f3 = makeFade(1.0, 0.0, 1.0, 1);
        f3.setNode(cardText);
        root.getChildren().add(cardText);
        p.getChildren().add(f3);
        s.getChildren().add(p);
        s.play();
        nodes[0] = cardBack;
        nodes[1] = cardText;
        return nodes;
    }

    public void removeTopCommCard() {
        GameCard g = communityChestCards.remove();
        if (!g.getText().equals("Get Out of Jail Free")) communityChestCards.add(g);
    }

    public void removeTopChanceCard() {
        GameCard g = chanceCards.remove();
        if (!g.getText().equals("Get Out of Jail Free")) chanceCards.add(g);
    }

    public void addJailToComm() {
        GameCard g = new GameCard("Get Out of Jail Free,Community Chest,0");
        communityChestCards.add(g);
    }

    public void addJailToChance() {
        GameCard g = new GameCard("Get Out of Jail Free,Chance,0");
        chanceCards.add(g);
    }

    public ParallelTransition moveCard(Group root, ImageView card, Text t, Button c) {
        root.getChildren().remove(c);
        ParallelTransition p = new ParallelTransition();
        TranslateTransition move = makeMove(1.2, 0, +100, 1);
        move.setNode(card);
        p.getChildren().add(move);
        ScaleTransition s = makeScale(1.2, 0.5, 0.5, 1);
        s.setNode(card);
        p.getChildren().add(s);
        TranslateTransition move2 = makeMove(1.2, 0, +100, 1);
        move2.setNode(t);
        p.getChildren().add(move2);
        ScaleTransition s2 = makeScale(1.2, 0.4, 0.4, 1);
        s2.setNode(t);
        p.getChildren().add(s2);
        return p;
    }


    public FadeTransition makeFade(double seconds, double fromVal, double toVal, int cycles) {
        FadeTransition f = new FadeTransition();
        f.setFromValue(fromVal);
        f.setToValue(toVal);
        f.setDuration(Duration.seconds(seconds));
        f.setCycleCount(cycles);
        return f;
    }

    public ScaleTransition makeScale(double seconds, double toX, double toY, int cycles) {
        ScaleTransition s = new ScaleTransition();
        s.setToX(toX);
        s.setToY(toY);
        s.setDuration(Duration.seconds(seconds));
        s.setCycleCount(cycles);
        return s;
    }

    public Text makeText(String t, int x, int y, Font f, Color c) {
        Text text = new Text(t);
        text.setX(x);
        text.setY(y);
        text.setFont(f);
        text.setFill(c);
        return text;
    }

    public ImageView makeImg(String url, int x, int y, int width, int height) {
        Image i = new Image(url);
        ImageView img = new ImageView(i);
        img.setX(x);
        img.setY(y);
        img.setFitWidth(width);
        img.setFitHeight(height);
        return img;
    }

    public Button makeBtn(String t, int x, int y, int width, int height) {
        Button b = new Button(t);
        b.setLayoutX(x);
        b.setLayoutY(y);
        b.setPrefSize(width, height);
        return b;
    }

    public TranslateTransition makeMove(double seconds, int toX, int toY, int cycles) {
        TranslateTransition t = new TranslateTransition();
        t.setToX(toX);
        t.setToY(toY);
        t.setDuration(Duration.seconds(seconds));
        t.setCycleCount(cycles);
        return t;
    }
}



