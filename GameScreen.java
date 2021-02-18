import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/*
This is the main class that takes care of the gameplay
 */
public class GameScreen implements GameInterface {
    private Group root;
    private int turn, playerNum, enterNum = 0; //The current turn, the number of players
    private String[] dicePics = {"dice1.png", "dice2.png", "dice3.png", "dice4.png", "dice5.png", "dice6.png"};
    private ArrayList<ImageView> playerPieces = new ArrayList<>(); //ArrayList that contains all the pieces of the player around the board
    private ImageView gameBoard; //The picture of the board
    private Board b; //Board object
    private Player[] players;
    private Text commentary = new Text();
    private String currCom = "";
    private ArrayList<String> inGameText = new ArrayList<>();
    private StackPane s;
    private Scene scene;
    private boolean enterPressed;
    private final int[] total = {0};

    public GameScreen(Group root, Player[] playerInfo, int playerNum, int startTurn, Scene scene) {
        this.root = root;
        root.getChildren().clear(); //clear the entire root
        this.playerNum = playerNum;
        turn = startTurn;
        players = playerInfo;
        this.scene = scene;
        b = new Board();
        inGameText.addAll(Arrays.asList("Hello, Uncle Pennybags here, and I'm going to be your in-game assistance today! (Press Enter to continue)", "My job is to make the gameplay as easy as possible for you! (Press Enter to continue)", "Bam! There are your windows! (Press Enter to continue)", "Every time it is one of your turns, a little menu will appear here from which you can choose your next move. (Press Enter to continue)", "Let's get started! Press Enter to start"));
        for (int i = 0; i < playerNum; i++) {
            players[i].setBottomRow(1000, 580 + (17 * i));
            players[i].setLeftRow(350 + (17 * i), 580 + (17 * i));
            players[i].setTopRowY(70 + (17 * i));
            players[i].setRightRowX(906 + (17 * i));
        }
    }

    public void controlGame() {
        drawScreen();
        drawTextList(0, 4);
    }

    public void drawScreen() {
        ImageView background = makeImg("table.jpg", 0, 0, 1250, 650);
        root.getChildren().add(background);
        gameBoard = makeImg("monopolyBoard.jpg", 350, 70, 640, 580);
        root.getChildren().add(gameBoard);
    }

    //Draws the initial text to start the game
    public void drawTextList(int curr, int end) {
        enterNum = 0;
        if (curr > 0) s.getChildren().remove(commentary);
        if (end > 0 && curr > end) return;
        String text = inGameText.get(curr);
        drawText(text);
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case ENTER:
                        enterPressed = true;
                }
            }
        });
        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case ENTER:
                        if (enterPressed && enterNum == 0) {
                            enterNum++;
                            if (curr == 1) drawPlayerRects();
                            else if (curr == 4) drawPieces();
                            drawText("");
                            drawTextList(curr + 1, end);
                        }

                }
            }
        });
    }

    //This method draws all of the player rectangles
    public void drawPlayerRects() {
        for (int i = 0; i < playerNum; i++) {
            String mon = players[i].getName() + ": $" + Integer.toString(players[i].getMoney());
            Rectangle rect = makeRect((i % 2) * 1050, (i / 2) * 550, 200, 100, Color.LIGHTBLUE);
            ImageView imgView = makeImg(players[i].getIMG(), 50 + ((i % 2) * 1050), 50 + ((i / 2) * 550), 100, 50);
            Text nameText = makeText(mon, (i % 2) * 1050, 20 + ((i / 2) * 550), Font.font("Comic Sans MS", 18), Color.BLACK);
            ImageView prison = makeImg("prisonBars.png", (i % 2) * 1050, (i / 2) * 550, 200, 100);
            root.getChildren().add(rect);
            root.getChildren().add(imgView);
            root.getChildren().add(nameText);
            if (players[i].isInJail()) root.getChildren().add(prison);
            else if (players[i].isBankrupt()) {
                Line l = new Line();
                l.setStartX(rect.getX());
                l.setEndX(rect.getX() + rect.getWidth());
                l.setStartY(rect.getY());
                l.setEndY(rect.getY() + rect.getHeight());
                l.setStrokeWidth(2.0);
                root.getChildren().add(l);
                Line l2 = new Line();
                l2.setStartX(rect.getX());
                l2.setEndX(rect.getX() + rect.getWidth());
                l2.setStartY(rect.getY() + rect.getHeight());
                l2.setEndY(rect.getY());
                l2.setStrokeWidth(2.0);
                root.getChildren().add(l2);
            }
        }
    }

    //This method creates the initial animation of puttng the players' pieces on the board
    public void drawPieces() {
        SequentialTransition s = new SequentialTransition();
        for (int i = 0; i < playerNum; i++) {
            ImageView carImg = makeImg(players[i].getIMG(), 1000, players[i].getBottomRowY(), 30, 15);
            carImg.setRotate(carImg.getRotate() + 180);
            FadeTransition fade = makeFade(1, 0.0, 10.0, 1);
            fade.setNode(carImg);
            TranslateTransition imgTran = makeMove(3, -80, 0);
            imgTran.setNode(carImg);
            s.getChildren().addAll(fade, imgTran);
            playerPieces.add(carImg);
            root.getChildren().add(playerPieces.get(i));
        }
        s.play();
        s.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                manageTurns();
            }
        });
    }

    //This method is essentially the turn manager
    public void manageTurns() {
        enterNum = 0;
        //First thing we check is if the player is in jail or not
        if (players[turn].isInJail()) {
            String t = players[turn].getName() + ", you're currently in jail! Press Enter to decide what to do next";
            drawText(t);
            scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    switch (event.getCode()) {
                        case ENTER:
                            enterPressed = true;
                    }
                }
            });
            scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    switch (event.getCode()) {
                        case ENTER:
                            if (enterNum == 0 && enterPressed) jailTurn();
                    }
                }
            });
        } else {
            startDiceRoll("regular");
        }
    }

    public void jailTurn() {
        enterNum = 0;
        players[turn].incJailTurn();
        JailScreen j = new JailScreen(players[turn]);
        Group jailRoot = j.drawScreen();
        Button buttons[] = new Button[3];
        ArrayList<Rectangle> rects = j.getAllRects();
        for (int i = 0; i < rects.size(); i++) {
            buttons[i] = makeBtn("Choose", (int) rects.get(i).getX() + 75, 510, 100, 50);
            jailRoot.getChildren().add(buttons[i]);
            if (rects.get(i).getX() == 125) {
                buttons[i].setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        scene.setRoot(root);
                        String t = players[turn].getName() + ", you've chosen to pay the $50 fee to get out of jail! Starting next turn, you will be back in the game.";
                        drawText(t);
                        players[turn].setInJail(false);
                        players[turn].setJailTurn(0);
                        drawPlayerRects();
                        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                            @Override
                            public void handle(KeyEvent event) {
                                switch (event.getCode()) {
                                    case ENTER:
                                        enterPressed = true;
                                }
                            }
                        });
                        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
                            @Override
                            public void handle(KeyEvent event) {
                                switch (event.getCode()) {
                                    case ENTER:
                                        if (enterPressed && enterNum == 0) {
                                            enterNum++;
                                            players[turn].setInJail(false);
                                            players[turn].setJailTurn(0);
                                            drawPlayerRects();
                                            players[turn].addTransaction("Paid $50 fee to get out of jail: $-50\nTotal: $" + (players[turn].getMoney() - 50) + "\n");
                                            transferMoney(null, turn, -50, "jail fee");
                                        }
                                }
                            }
                        });
                    }
                });
            } else if (rects.get(i).getX() == 500) {
                buttons[i].setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        scene.setRoot(root);
                        startDiceRoll("jail");
                    }
                });
            } else if (rects.get(i).getX() == 875) {
                buttons[i].setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        String val = players[turn].setJailFree("");
                        players[turn].setInJail(false);
                        players[turn].setJailTurn(0);
                        scene.setRoot(root);
                        drawText("So " + players[turn].getName() + ", you chose to use your Get Out of Jail Free card! Now you're out of jail, but you don't have the card anymore!");
                        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                            @Override
                            public void handle(KeyEvent event) {
                                switch (event.getCode()) {
                                    case ENTER:
                                        enterPressed = true;
                                }
                            }
                        });
                        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
                            @Override
                            public void handle(KeyEvent event) {
                                switch (event.getCode()) {
                                    case ENTER:
                                        if (enterPressed && enterNum == 0) {
                                            enterNum++;
                                            if (val.equals("Chance")) b.addJailToChance();
                                            else b.addJailToComm();
                                            makeMenu("regular");
                                        }
                                }
                            }
                        });
                    }
                });
            }
        }
        scene.setRoot(jailRoot);
    }

    public void rollOutOfJail(boolean out, int move, int newPos, ImageView[] dice, Rectangle r) {
        enterNum = 0;
        if (out) {
            drawText("Congratulations " + players[turn].getName() + ", you rolled doubles and now you're out of jail! Press Enter");
            scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    switch (event.getCode()) {
                        case ENTER:
                            enterPressed = true;
                    }
                }
            });
            scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    switch (event.getCode()) {
                        case ENTER:
                            if (enterPressed && enterNum == 0) {
                                enterNum++;
                                players[turn].setInJail(false);
                                players[turn].setJailTurn(0);
                                drawPlayerRects();
                                movePiece(move, newPos, dice, r, "regular");
                            }

                    }
                }
            });
        } else {
            String t = "Oh no, you didn't roll doubles! ";
            if (players[turn].getJailTurn() == 3) {
                drawText("Oh no, you didn't roll doubles! Since it's your third turn since you've landed on jail, you have to pay the $50 fee. Press Enter");
                scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        switch (event.getCode()) {
                            case ENTER:
                                enterPressed = true;
                        }
                    }
                });
                scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        switch (event.getCode()) {
                            case ENTER:
                                if (enterPressed && enterNum == 0) {
                                    enterNum++;
                                    players[turn].setInJail(false);
                                    players[turn].setJailTurn(0);
                                    drawPlayerRects();
                                    root.getChildren().remove(r);
                                    if (players[turn].getMoney() < 50) {
                                        players[turn].setDebt(50 - players[turn].getMoney());
                                        possBankruptcy("the bank");
                                    } else {
                                        players[turn].addTransaction("Paid $50 fee to get out of jail: $-50\n\tTotal: $" + (players[turn].getMoney() - 50) + "\n");
                                        transferMoney(null, turn, -50, "jail fee");
                                    }
                                }
                        }
                    }
                });
            } else {
                t = t.concat("Looks like you're gonna have to wait till next turn, better luck next time! Press Enter");
                drawText(t);
                scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        switch (event.getCode()) {
                            case ENTER:
                                enterPressed = true;
                        }
                    }
                });
                scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        switch (event.getCode()) {
                            case ENTER:
                                if (enterPressed) {
                                    root.getChildren().remove(r);
                                    makeMenu("regular");
                                }

                        }
                    }
                });
            }
        }
    }

    public void startDiceRoll(String mode) {
        //First we remove the current piece, draw a rectangle underneath it, and then add it back again
        for (int i = 0; i < playerNum; i++) {
            if (players[i].getPlayerPos() == players[turn].getPlayerPos())
                root.getChildren().remove(playerPieces.get(i));
        }
        Rectangle currRect = b.getPlayerRect(players[turn], -1);
        root.getChildren().add(currRect);
        for (int i = 0; i < playerNum; i++) {
            if (players[i].getPlayerPos() == players[turn].getPlayerPos())
                root.getChildren().add(playerPieces.get(i));
        }
        enterNum = 0;
        String currPlayer;
        if (!mode.equals("jail"))
            if (players[turn].getDoubles())
                currPlayer = players[turn].getName() + ", it's your turn again since you rolled doubles! Press enter to roll the dice!";
            else currPlayer = players[turn].getName() + ", it's your turn! Press enter to roll the dice!";
        else
            currPlayer = "So you've chosen to test your luck! If you roll doubles, you'll be out of jail and will be moved that many spaces forward. Press Enter";
        drawText(currPlayer);
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case ENTER:
                        enterPressed = true;
                }
            }
        });
        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case ENTER:
                        if (enterPressed && enterNum == 0) {
                            showDice(currRect, mode);
                        }
                }
            }
        });

    }

    //This method causes the dice to appear on the text rectangle
    public void showDice(Rectangle r, String mode) {
        Rectangle AIRect = makeRect(400, 0, 550, 70, Color.IVORY);
        AIRect.setStroke(Color.BLACK);
        root.getChildren().add(AIRect);
        Random rand = new Random();
        int choice = rand.nextInt(6);
        ImageView dicePic = makeImg(dicePics[choice], 600, 5, 55, 55);
        FadeTransition fade = makeFade(1, 0.0, 10.0, 1);
        fade.setNode(dicePic);
        fade.play();
        root.getChildren().add(dicePic);
        choice = rand.nextInt(6);
        ImageView secondDicePic = makeImg(dicePics[choice], 680, 5, 55, 55);
        fade = makeFade(1, 0.0, 10.0, 1);
        fade.setNode(secondDicePic);
        fade.play();
        root.getChildren().add(secondDicePic);
        Button rotate = makeBtn("Roll", 750, 20, 70, 30);
        root.getChildren().add(rotate);
        rotate.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                rollDice(dicePic, secondDicePic, rotate, r, mode);
            }
        });

    }

    //This method actually rolls the dice and decides the next step afterwards
    public void rollDice(ImageView dicePic, ImageView secondDicePic, Button b, Rectangle rect, String mode) {
        total[0] = 0; //Will track the total roll of the dice
        Random rand = new Random();
        final ImageView[] dice = {dicePic, secondDicePic};
        AtomicInteger choice = new AtomicInteger(); //Had to use AtomicInteger to solve an error
        RotateTransition r = makeRotation(1, Rotate.Z_AXIS, 360); //The rotation of the dice, "rolling it"
        r.setNode(dicePic);
        r.setOnFinished(event -> {
            root.getChildren().remove(dice[0]);
            choice.set(rand.nextInt(6));
            total[0] += (choice.get() + 1); //The get() function is because choice is an AtomicInteger
            dice[0] = makeImg(dicePics[choice.get()], 600, 5, 55, 55);
            root.getChildren().add(dice[0]);
        });
        RotateTransition r2 = makeRotation(1, Rotate.Z_AXIS, -360);
        r2.setNode(secondDicePic);
        r2.setOnFinished(event -> {
            root.getChildren().remove(dice[1]);
            choice.set(rand.nextInt(6));
            int firstRoll = total[0];
            total[0] += (choice.get() + 1);
            dice[1] = makeImg(dicePics[choice.get()], 680, 5, 55, 55);
            root.getChildren().add(dice[1]);
            if (mode.equals("jail")) {
                if ((choice.get() + 1) == firstRoll) {
                    Timeline timeline = new Timeline();
                    KeyFrame keyFrame = new KeyFrame(
                            Duration.seconds(1.5),
                            event1 -> {
                                rollOutOfJail(true, total[0], total[0] + players[turn].getPlayerPos(), dice, rect);
                            });
                    timeline.getKeyFrames().add(keyFrame);
                    timeline.setCycleCount(1);
                    timeline.play();
                } else {
                    Timeline timeline = new Timeline();
                    KeyFrame keyFrame = new KeyFrame(
                            Duration.seconds(1.5),
                            event1 -> {
                                rollOutOfJail(false, total[0], total[0] + players[turn].getPlayerPos(), dice, rect);
                            });
                    timeline.getKeyFrames().add(keyFrame);
                    timeline.setCycleCount(1);
                    timeline.play();
                }
            } else {
                //firstRoll tracks the first dice's number, so if the second dice's number is the same as the first dice, you've rolled a double
                if ((choice.get() + 1) == firstRoll) {
                    enterNum = 0;
                    players[turn].setDoubles(true);
                    players[turn].setDoubleCount(players[turn].getDoubleCount() + 1);
                    if (players[turn].getDoubleCount() == 3) {
                        String t = players[turn].getName() + ", you rolled doubles three times, and now you gotta go to jail! Press Enter";
                        drawText(t);
                        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                            @Override
                            public void handle(KeyEvent event) {
                                switch (event.getCode()) {
                                    case ENTER:
                                        enterPressed = true;
                                }
                            }
                        });
                        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
                            @Override
                            public void handle(KeyEvent event) {
                                switch (event.getCode()) {
                                    case ENTER:
                                        if (enterPressed && enterNum == 0) {
                                            enterNum++;
                                            players[turn].setDoubles(false);
                                            players[turn].setDoubleCount(0);
                                            goToJail();
                                        }

                                }
                            }
                        });
                    }
                } else {
                    players[turn].setDoubles(false);
                    players[turn].setDoubleCount(0);
                }
                if (!(players[turn].getDoubleCount() == 3))
                    movePiece(total[0], total[0] + players[turn].getPlayerPos(), dice, rect, "regular");
            }
        });
        r.play();
        r2.play();
        root.getChildren().remove(b);
    }

    //This method moves the current piece
    public void movePiece(int move, int newPos, ImageView[] dice, Rectangle r, String mode) {
        if (newPos >= 40) newPos = newPos - 40;
        //The Rectangle r is the flashing rectangle behind the current player's piece
        if (mode.equals("regular")) root.getChildren().remove(r);
        int oldPos = players[turn].getPlayerPos();
        players[turn].setPlayerPos(newPos); //Update the current player's position
        Rectangle endRect = b.getPlayerRect(players[turn], -1); //This rectangle flashes on the property that the player will be going to
        root.getChildren().add(endRect);
        FadeTransition rectFade = makeFade(1.2, 1.0, 0.0, Animation.INDEFINITE);
        rectFade.setNode(endRect);
        rectFade.play();
        root.getChildren().remove(playerPieces.get(turn)); //Remove the current piece as it will be moved
        SequentialTransition s = b.playerTransition(players[turn], oldPos, newPos, playerPieces.get(turn)); //This method will take care of the entire movement
        root.getChildren().add(playerPieces.get(turn));
        int finalNewPos = newPos;
        s.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                root.getChildren().remove(endRect);
                if (mode.equals("regular")) {
                    root.getChildren().remove(dice[0]);
                    root.getChildren().remove(dice[1]);
                }
                //These are for the cases in which you pull a chance card in which you are told to move the nearest Utility, Railroad, etc.
                if (mode.contains("comm card") || mode.contains("chance card")) {
                    if (mode.contains("comm card")) b.removeTopCommCard();
                    else b.removeTopChanceCard();
                }
                //Ex. if you come from spot 39 (BoardWalk) to spot 1 (Meditarranean Avenue), you have passed Go
                if (finalNewPos < oldPos) {
                    enterNum = 0;
                    String t = players[turn].getName() + ", you passed Go, so collect your $200! Press Enter";
                    drawText(t);
                    scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                        @Override
                        public void handle(KeyEvent event) {
                            switch (event.getCode()) {
                                case ENTER:
                                    enterPressed = true;
                            }
                        }
                    });
                    scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
                        @Override
                        public void handle(KeyEvent event) {
                            switch (event.getCode()) {
                                case ENTER:
                                    if (enterPressed && enterNum == 0) {
                                        enterNum++;
                                        String t = "Passed Go: +$200\n\tTotal: $" + (players[turn].getMoney() + 200);
                                        players[turn].addTransaction(t);
                                        transferMoney(null, turn, 200, "Go");
                                    }
                            }
                        }
                    });
                } else currentSpotMove(finalNewPos);
            }
        });
        s.play();
    }

    //This method looks at the current spot of the player and decides what to do next
    public void currentSpotMove(int pos) {
        enterNum = 0;
        PropertyScreen p = new PropertyScreen(players[turn], b);
        int spot = b.getSpot(pos); //Gets the type of the current spot (if it's a property spot, community chest spot, tax spot, etc.)
        if (spot == PROPERTY) {
            Property curr = b.getProperty(pos); //Gets the property at that specific spot
            String prop = players[turn].getName() + ", you landed on " + curr.getName();
            if (!curr.isOwned()) {
                if (curr.getColor().equals("Utility") && players[turn].getDoubleUtil()) {
                    addProperty(players[turn], b.getProperty(players[turn].getPlayerPos()));
                    players[turn].setDoubleUtil(false);
                    enterNum = 0;
                    prop = prop.concat(", and since it's not owned, you get it for free per the Chance card! Press Enter");
                    drawText(prop);
                    scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                        @Override
                        public void handle(KeyEvent event) {
                            switch (event.getCode()) {
                                case ENTER:
                                    enterPressed = true;
                            }
                        }
                    });
                    scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
                        @Override
                        public void handle(KeyEvent event) {
                            switch (event.getCode()) {
                                case ENTER:
                                    if (enterPressed && enterNum == 0) {
                                        enterNum++;
                                        propertyBought(curr, turn);
                                    }
                            }
                        }
                    });
                } else if (curr.getColor().equals("White") && players[turn].getDoubleRail()) {
                    players[turn].addProperty(b.getProperty(players[turn].getPlayerPos()));
                    b.getProperty(players[turn].getPlayerPos()).setOwned(true);
                    b.getProperty(players[turn].getPlayerPos()).setOwner(players[turn]);
                    players[turn].setDoubleRail(false);
                    enterPressed = false;
                    prop = prop.concat(", and since it's not owned, you get it for free per the Chance card! Press Enter");
                    drawText(prop);
                    scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                        @Override
                        public void handle(KeyEvent event) {
                            switch (event.getCode()) {
                                case ENTER:
                                    enterPressed = true;
                            }
                        }
                    });
                    scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
                        @Override
                        public void handle(KeyEvent event) {
                            switch (event.getCode()) {
                                case ENTER:
                                    if (enterPressed) {
                                        propertyBought(curr, turn);
                                    }
                            }
                        }
                    });
                } else {
                    if (curr.getPrice() > players[turn].getMoney())
                        prop = prop.concat(", and it hasn't been owned yet! However, since you don't have enough money to purchase it, this property will go straight to auction. Press Enter to continue");
                    else
                        prop = prop.concat(", and it hasn't been owned yet! Press Enter to determine what you'll do next!");
                    drawText(prop);
                    scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                        @Override
                        public void handle(KeyEvent event) {
                            switch (event.getCode()) {
                                case ENTER:
                                    enterPressed = true;
                            }
                        }
                    });
                    scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
                        @Override
                        public void handle(KeyEvent event) {
                            switch (event.getCode()) {
                                case ENTER:
                                    if (enterPressed && enterNum == 0) {
                                        enterNum++;
                                        if (curr.getPrice() > players[turn].getMoney()) auctionProperty(curr);
                                            //p is the PropertyScreen object and curr is the current property
                                        else propertyManage(p, curr);
                                    }
                            }
                        }
                    });
                }
            } else if (!curr.getOwner().getName().equals(players[turn].getName())) {
                prop = prop.concat(", and it is currently owned by " + curr.getOwner().getName());
                if (curr.getColor().equals("Utility")) {
                    if (players[turn].getDoubleUtil())
                        prop = prop.concat(". Since " + curr.getOwner().getName() + " owns it, you owe ten times what you rolled as per the Chance Card, so you owe him $" + total[0] * 10) + ". Pres Enter";
                    else
                        prop = prop.concat(". Since " + curr.getOwner().getName() + " owns " + curr.getOwner().getUtilities() + " utilities, you owe him " + curr.getRent() + " times what you rolled as rent. Since you rolled " + total[0] + ", you owe $" + (total[0] * curr.getRent()) + ". Press Enter");
                } else if (curr.getColor().equals("White")) {
                    if (players[turn].getDoubleRail())
                        prop = prop.concat(". Since " + curr.getOwner().getName() + " owns " + curr.getOwner().getRailRoads() + " railroads, the rent is $" + curr.getRent() + ", but you owe double that per the Chance card. Press Enter");
                    else
                        prop = prop.concat(". Since " + curr.getOwner().getName() + " owns " + curr.getOwner().getRailRoads() + " railroads, the rent is $" + curr.getRent() + ". Press Enter");
                } else if (curr.getOwner().ownsAllColors(curr) && curr.getHouses() == 0)
                    prop = prop.concat(", and he owns all properties of this color, so you owe him double the regular rent of $" + (curr.getRent() / 2) + ". Press Enter");
                else if (curr.getHouses() > 0 && curr.getHotels() != 1)
                    prop = prop.concat(". Since he owns " + curr.getHouses() + " houses, you owe him $" + (curr.getRent()) + ". Press Enter");
                else if (curr.getHotels() == 1)
                    prop = prop.concat(". Since he owns a hotel, you owe him $" + (curr.getRent()) + ". Press Enter");
                else prop = prop.concat(", so you owe him $" + curr.getRent() + ". Press Enter");
                drawText(prop);
                scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        switch (event.getCode()) {
                            case ENTER:
                                enterPressed = true;
                        }
                    }
                });
                scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        switch (event.getCode()) {
                            case ENTER:
                                if (enterPressed) {
                                    if (curr.getColor().equals("Utility")) {
                                        if (players[turn].getDoubleUtil()) {
                                            players[turn].setDoubleUtil(false);
                                            int rent = total[0] * 10;
                                            if (rent > players[turn].getMoney()) {
                                                players[turn].setDebt(rent - players[turn].getMoney());
                                                possBankruptcy(curr.getOwner().getName());
                                            } else {
                                                curr.setMonGained(rent);
                                                players[turn].addTransaction("Paid $" + rent + " rent to " + curr.getOwner().getName() + " for landing on " + curr.getName() + ": -$" + (rent) + "\n\tTotal: $" + (players[turn].getMoney() - rent));
                                                curr.getOwner().addTransaction("Received $" + rent + " in rent from " + players[turn].getName() + " for landing on " + curr.getName() + ": +$" + rent + "\n\tTotal: $" + (curr.getOwner().getMoney() + rent));
                                                transferMoney(curr, turn, rent, "rent");
                                            }
                                        } else {
                                            int rent = total[0] * curr.getRent();
                                            if (rent > players[turn].getMoney()) {
                                                players[turn].setDebt(rent - players[turn].getMoney());
                                                possBankruptcy(curr.getOwner().getName());

                                            } else {
                                                curr.setMonGained(total[0] * curr.getRent());
                                                players[turn].addTransaction("Paid $" + rent + " rent to " + curr.getOwner().getName() + " for landing on " + curr.getName() + ": -$" + rent + "\n\tTotal: $" + (players[turn].getMoney() - rent));
                                                curr.getOwner().addTransaction("Received $" + rent + " in rent from " + players[turn].getName() + " for landing on " + curr.getName() + ": +$" + rent + "\n\tTotal: $" + (curr.getOwner().getMoney() + rent));
                                                transferMoney(curr, turn, total[0] * curr.getRent(), "rent");
                                            }
                                        }
                                    } else if (curr.getColor().equals("White")) {
                                        if (players[turn].getDoubleRail()) {
                                            players[turn].setDoubleRail(false);
                                            int rent = curr.getRent() * 2;
                                            if (rent > players[turn].getMoney()) {
                                                players[turn].setDebt(rent - players[turn].getMoney());
                                                possBankruptcy(curr.getOwner().getName());
                                            } else {
                                                curr.setMonGained(curr.getRent() * 2);
                                                players[turn].addTransaction("Paid $" + rent + " rent to " + curr.getOwner().getName() + " for landing on " + curr.getName() + ": -$" + rent + "\n\tTotal: $" + (players[turn].getMoney() - rent));
                                                curr.getOwner().addTransaction("Received $" + rent + " in rent from " + players[turn].getName() + " for landing on " + curr.getName() + ": +$" + rent + "\n\tTotal: $" + (curr.getOwner().getMoney() + rent));
                                                transferMoney(curr, turn, curr.getRent() * 2, "rent");
                                            }
                                        } else {
                                            curr.setMonGained(curr.getRent());
                                            int rent = curr.getRent();
                                            if (rent > players[turn].getMoney()) {
                                                players[turn].setDebt(rent - players[turn].getMoney());
                                                possBankruptcy(curr.getOwner().getName());
                                            } else {
                                                players[turn].addTransaction("Paid $" + rent + " rent to " + curr.getOwner().getName() + " for landing on " + curr.getName() + ": -$" + rent + "\n\tTotal: $" + (players[turn].getMoney() - rent));
                                                curr.getOwner().addTransaction("Received $" + rent + " in rent from " + players[turn].getName() + " for landing on " + curr.getName() + ": +$" + rent + "\n\tTotal: $" + (curr.getOwner().getMoney() + rent));
                                                transferMoney(curr, turn, curr.getRent(), "rent");
                                            }
                                        }
                                    } else {
                                        curr.setMonGained(curr.getRent());
                                        int rent = curr.getRent();
                                        if (rent > players[turn].getMoney()) {
                                            players[turn].setDebt(rent - players[turn].getMoney());
                                            possBankruptcy(curr.getOwner().getName());
                                        } else {
                                            players[turn].addTransaction("Paid $" + rent + " rent to " + curr.getOwner().getName() + " for landing on " + curr.getName() + ": -$" + rent + "\n\tTotal: $" + (players[turn].getMoney() - rent));
                                            curr.getOwner().addTransaction("Received $" + rent + " in rent from " + players[turn].getName() + " for landing on " + curr.getName() + ": +$" + rent + "\n\tTotal: $" + (curr.getOwner().getMoney() + rent));
                                            transferMoney(curr, turn, curr.getRent(), "rent");
                                        }
                                    }
                                }

                        }
                    }
                });
            } else makeMenu("regular");
        } else if (spot == COMMCHEST) {
            takeCard(COMMCHEST);
        } else if (spot == CHANCE) takeCard(CHANCE);
        else if (spot == JAIL) goToJail();
        else if (spot == INCOMETAX) {
            String t = "Oh no " + players[turn].getName() + ", you have to pay $200 in Income Tax! Press Enter to continue";
            drawText(t);
            scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    switch (event.getCode()) {
                        case ENTER:
                            enterPressed = true;
                    }
                }
            });
            scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    switch (event.getCode()) {
                        case ENTER:
                            if (enterPressed) {
                                scene.setRoot(root);
                                if (players[turn].getMoney() < 200) {
                                    players[turn].setDebt(200 - players[turn].getMoney());
                                    possBankruptcy("the bank");
                                } else {
                                    players[turn].addTransaction("Paid $200 in Income Tax: -$200\n\tTotal: " + (players[turn].getMoney() - 200));
                                    transferMoney(null, turn, -200, "tax");
                                }
                            }

                    }
                }
            });
        } else if (spot == LUXURYTAX) {
            String t = "Oh no " + players[turn].getName() + ", you have to pay $100 in Luxury Tax! Press Enter to continue";
            drawText(t);
            scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    switch (event.getCode()) {
                        case ENTER:
                            enterPressed = true;
                    }
                }
            });
            scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    switch (event.getCode()) {
                        case ENTER:
                            if (enterPressed) {
                                scene.setRoot(root);
                                players[turn].addTransaction("Paid $100 in Luxury Tax: -$100\n\tTotal: " + (players[turn].getMoney() - 100));
                                transferMoney(null, turn, -100, "tax");
                            }

                    }
                }
            });
        } else {
            makeMenu("regular");
        }
    }

    //This method manages the buying/auctioning of a property that has been landed on and isn't owned
    public void propertyManage(PropertyScreen p, Property curr) {
        enterNum = 0;
        //We have to create a new Group object so that the scene can be set to this root and then changed back to the original root once this process is done
        Group propRoot = p.showProperty(curr); //This method in the PropertyScreen class displays the screen for the current property
        Button buy = p.makeBtn("Purchase", 50, 580, 120, 50); //Button to purchase the property that is on the PropertyScreen's root, not the original root
        EventHandler<ActionEvent> sButton = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                addProperty(players[turn], curr);
                scene.setRoot(root);
                String text = "Congratulations " + players[turn].getName() + ", you successfully bought " + curr.getName() + "! Press Enter to continue on";
                drawText(text);
                scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        switch (event.getCode()) {
                            case ENTER:
                                enterPressed = true;
                        }
                    }
                });
                scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        switch (event.getCode()) {
                            case ENTER:
                                if (enterPressed && enterNum == 0) {
                                    enterNum++;
                                    drawText("");
                                    curr.setMonSpent(curr.getPrice());
                                    players[turn].addTransaction("Paid $" + curr.getPrice() + " to purchase " + curr.getName() + ": -$" + curr.getPrice() + "\n\tTotal: $" + (players[turn].getMoney() - curr.getPrice()));
                                    transferMoney(curr, turn, -1 * curr.getPrice(), "buy");
                                }

                        }
                    }
                });
            }
        };
        buy.setOnAction(sButton);
        propRoot.getChildren().add(buy);
        Button auction = p.makeBtn("Auction", 200, 580, 120, 50);
        EventHandler<ActionEvent> auctionEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                auctionProperty(curr);
            }
        };
        auction.setOnAction(auctionEvent);
        propRoot.getChildren().add(auction);
        scene.setRoot(propRoot);

    }

    //This method shows the transfer of money animation on the board
    public void transferMoney(Property p, int pos, int price, String mode) {
        //This ArrayList contains all of the text, lines, and all the other nodes
        ArrayList<Node> nodes = new ArrayList<>();
        //The secondPlayer variable is used for things like paying rent or other transactions in which there are more than one players involved
        int secondPlayer = -1;
        //This is the part where we look for the second player in the transaction. In the case of paying rent, we look
        //for the owner of the current property
        if (mode.equals("rent")) {
            String name = p.getOwner().getName();
            for (int i = 0; i < playerNum; i++) {
                if (players[i].getName().equals(name)) secondPlayer = i;
            }
        } else if (mode.contains("each player")) secondPlayer = pos;
        SequentialTransition s = new SequentialTransition();
        //Creating the blur on the image of the board
        ColorAdjust c = new ColorAdjust(0, -0.5, -0.7, 0);
        GaussianBlur g = new GaussianBlur();
        c.setInput(g);
        gameBoard.setEffect(c);
        Text name;
        if (!mode.contains("each player"))
            name = makeText(players[pos].getName(), 470, 200, Font.font("Bodoni MT", 40), Color.WHITE);
        else name = makeText(players[turn].getName(), 470, 200, Font.font("Bodoni MT", 40), Color.WHITE);
        root.getChildren().add(name);
        nodes.add(name);
        String mon;
        if (!mode.contains("each player")) mon = "$" + Integer.toString(players[pos].getMoney());
        else mon = "$" + Integer.toString(players[turn].getMoney());
        Text money = makeText(mon, 480, 310, Font.font("Calisto MT", 35), Color.WHITE);
        root.getChildren().add(money);
        nodes.add(money);
        //This is for the case in which you have to add one more player to the transaction
        if (mode.equals("rent") || mode.contains("each player")) {
            Text secondName;
            if (!mode.contains("each player"))
                secondName = makeText(p.getOwner().getName(), 740, 200, Font.font("Bodoni MT", 40), Color.WHITE);
            else
                secondName = makeText(players[secondPlayer].getName(), 740, 200, Font.font("Bodoni MT", 40), Color.WHITE);
            root.getChildren().add(secondName);
            nodes.add(secondName);
            String secondMon = "$" + Integer.toString(players[secondPlayer].getMoney());
            Text secondMoney = makeText(secondMon, 750, 310, Font.font("Calisto MT", 35), Color.WHITE);
            root.getChildren().add(secondMoney);
            nodes.add(secondMoney);
        }
        String deduction;
        if (price < 0) {
            if (mode.equals("card")) deduction = "-$" + (-1 * price);
            else deduction = "-$" + (-1 * price);
        } else {
            if (mode.equals("rent") || mode.contains("each player")) deduction = "-$" + price;
            else deduction = "+$" + price;
        }
        Text deduc = makeText(deduction, 470, 270, Font.font("Calisto MT", 35), Color.WHITE);
        FadeTransition f = makeFade(2.0, 0.0, 1.0, 1);
        f.setNode(deduc);
        root.getChildren().add(deduc);
        s.getChildren().add(f);
        nodes.add(deduc);
        if (mode.equals("rent") || mode.contains("each player")) {
            String movePrice = "$" + price;
            Text move = makeText(movePrice, 480, 270, Font.font("Calisto MT", 35), Color.WHITE);
            TranslateTransition t = makeMove(1.5, 740 - 460, 0);
            t.setNode(move);
            s.getChildren().add(t);
            nodes.add(move);
            root.getChildren().add(move);
            Text plusSign = makeText("+", 735, 270, Font.font("Calisto MT", 35), Color.WHITE);
            FadeTransition plusFade = makeFade(0.5, 0.0, 1.0, 1);
            plusFade.setNode(plusSign);
            s.getChildren().add(plusFade);
            root.getChildren().add(plusSign);
            nodes.add(plusSign);
        }
        Line l = makeLine(470, 325, 600, 325, Color.WHITE, 3);
        FadeTransition f2 = makeFade(0.3, 0.0, 1.0, 1);
        f2.setNode(l);
        root.getChildren().add(l);
        s.getChildren().add(f2);
        nodes.add(l);
        if (mode.equals("rent") || mode.contains("each player")) {
            Line l2 = makeLine(740, 325, 900, 325, Color.WHITE, 3);
            FadeTransition f2b = makeFade(0.1, 0.0, 1.0, 1);
            f2b.setNode(l2);
            root.getChildren().add(l2);
            s.getChildren().add(f2b);
            nodes.add(l2);
            players[secondPlayer].setMoney(price);
        }
        if (price < 0 && !mode.equals("card")) players[pos].setMoney(price);
        else {
            if (mode.equals("rent")) players[pos].setMoney(-1 * price);
            else if (mode.contains("each player")) players[turn].setMoney(-1 * price);
            else players[pos].setMoney(price);
        }
        if (!mode.contains("each player")) mon = "$" + Integer.toString(players[pos].getMoney());
        else mon = "$" + Integer.toString(players[turn].getMoney());
        Text newMon = makeText(mon, 470, 365, Font.font("Calisto MT", 35), Color.WHITE);
        FadeTransition f3 = makeFade(1.0, 0.0, 1.0, 1);
        f3.setNode(newMon);
        root.getChildren().add(newMon);
        s.getChildren().add(f3);
        nodes.add(newMon);
        if (mode.equals("rent") || mode.contains("each player")) {
            mon = "$" + Integer.toString(players[secondPlayer].getMoney());
            Text newMon2 = makeText(mon, 740, 365, Font.font("Calisto MT", 35), Color.WHITE);
            FadeTransition f3b = makeFade(1.0, 0.0, 1.0, 1);
            f3b.setNode(newMon2);
            root.getChildren().add(newMon2);
            s.getChildren().add(f3b);
            nodes.add(newMon2);
        }
        final int currPos = pos;
        s.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event1) {
                Timeline timeline = new Timeline();
                KeyFrame keyFrame = new KeyFrame(
                        Duration.seconds(1.0),
                        event -> {
                            for (int i = 0; i < nodes.size(); i++) root.getChildren().remove(nodes.get(i));
                            int endPos;
                            if (turn == 0) endPos = playerNum - 1;
                            else endPos = turn - 1;
                            if (mode.contains("each player") && !(currPos == endPos)) {
                                if (currPos == (playerNum - 1)) {
                                    transferMoney(null, 0, price, mode);
                                } else {
                                    transferMoney(null, currPos + 1, price, mode);
                                }
                            } else {
                                if (mode.contains("each player")) {
                                    players[turn].addTransaction("You have been elected Chairman of the Board–Pay each player $50: -$" + (price * (playerNum - 1)) + "\n\tTotal: $" + (players[turn].getMoney()));
                                    b.removeTopChanceCard();
                                }
                                if (mode.equals("card")) {
                                    root.getChildren().remove(b.getCardBack());
                                    root.getChildren().remove(b.getCardText());
                                }
                                gameBoard.setEffect(null);
                                drawPlayerRects();
                                if (mode.equals("buy")) propertyBought(p, pos);
                                else if (mode.equals("Go")) currentSpotMove(players[turn].getPlayerPos());
                                else {
                                    makeMenu("regular");
                                }
                            }
                        });
                timeline.getKeyFrames().add(keyFrame);
                timeline.setCycleCount(1);
                timeline.play();
            }
        });
        s.play();
    }

    //This function officially transfers the property to the player by placing the property card's image on his side of
    //the table
    public void propertyBought(Property p, int newSpot) {
        root.getChildren().remove(p.getPropImg());
        ImageView propImg = makeImg(p.getImg(), players[newSpot].getNextPropertyX(newSpot), players[newSpot].getNextPropertyY(newSpot), 30, 50);
        if (newSpot == 0 || newSpot == 2) propImg.setRotate(propImg.getRotate() + 90);
        else propImg.setRotate(propImg.getRotate() - 90);
        SequentialTransition s = new SequentialTransition();

        FadeTransition f1 = makeFade(1.2, 0.0, 1.0, 1);
        f1.setNode(propImg);
        Rectangle r = b.getPlayerRect(players[newSpot], p.getPos());
        r.setStroke(Color.BLACK);
        //Above each owned property is a rectangle that has the same color as that of the owner, and this series of code
        //draws that very rectangle
        if (p.getPos() > 0 && p.getPos() < 10) {
            r.setHeight(15);
            r.setWidth(r.getWidth() - 2);
            r.setY(r.getY() - 15);
        } else if (p.getPos() > 10 && p.getPos() < 20) {
            r.setX(r.getX() + r.getWidth() + 1);
            if (p.getPos() == 11) r.setHeight(31);
            else if (p.getPos() == 19) {
                r.setY(r.getY() + 18);
                r.setHeight(r.getHeight() - 18);
            } else {
                r.setHeight(r.getHeight() - 4);
                r.setY(r.getY() + 3);
            }
            r.setWidth(15);
        } else if (p.getPos() > 20 && p.getPos() < 30) {
            r.setY(r.getY() + r.getHeight() + 1);
            r.setHeight(15);
        } else if (p.getPos() > 30 && p.getPos() < 40) {
            r.setX(r.getX() - 15);
            if (p.getPos() == 39) r.setHeight(31);
            else if (p.getPos() == 31) {
                r.setY(r.getY() + 18);
                r.setHeight(r.getHeight() - 18);
            } else {
                r.setHeight(r.getHeight() - 4);
                if (!(p.getPos() == 37)) r.setY(r.getY() + 3);
            }
            r.setWidth(15);
        }
        FadeTransition f = makeFade(1.2, 0.0, 1.0, 1);
        f.setNode(r);
        root.getChildren().add(r);
        root.getChildren().add(propImg);
        s.getChildren().addAll(f1, f);
        s.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //If the current player is bankrupt and this method has been invoked, then that means that we are in the
                //process of auctioning all of the bankrupt player's properties, and this method was invoked to add one
                //of those properties to the winning player. Therefore, we need to return to the bankruptcy function to
                //complete auctioning the rest. Otherwise, we just go straight to the menu function
                if (players[turn].isBankrupt()) {
                    players[turn].getPropImages().remove(0);
                    bankruptcy(players[turn]);
                } else makeMenu("regular");
            }
        });
        s.play();
        p.setPropImg(propImg);
        p.setPropRect(r);
    }

    //When a property needs to be auctioned, this is the function that is called
    public void auctionProperty(Property p) {
        AuctionScreen a = new AuctionScreen(players, p);
        Group aucRoot = a.drawScreen();
        int len = a.getLen();
        //CONSIDER THE CASE IN WHICH THE PLAYER WITH THE HIGHEST BID QUITS
        for (int i = 0; i < len; i++) {
            Button exitB = makeBtn("QUIT", 150 + (i * 300), 530, 100, 35);
            exitB.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    int pos = (int) ((exitB.getLayoutX() - 150) / 300);
                    a.setQuit(pos);
                    a.removePlayer(pos);
                    aucRoot.getChildren().remove(exitB);
                    if (a.getQuit() == (playerNum - 1)) {
                        int winPos = a.displayWinner(p);
                        tranisitionFromAuction(p, a.getHighestBid(), winPos);
                    } else if (a.getI() == pos) {
                        a.findNextPlayer();
                        a.setCurrBid("");
                        a.nextTurn();
                    }
                }
            });
            aucRoot.getChildren().add(exitB);
        }
        scene.setRoot(aucRoot);
    }

    public void tranisitionFromAuction(Property p, int price, int winPos) {
        enterNum = 0;
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case ENTER:
                        enterPressed = true;
                }
            }
        });
        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case ENTER:
                        if (enterPressed && enterNum == 0) {
                            enterNum++;
                            scene.setRoot(root);
                            drawText("");
                            p.setMonSpent(price);
                            players[winPos].addTransaction("Won " + p.getName() + " from an auction: -$" + price + "\n\tTotal: $" + (players[winPos].getMoney() - price));
                            transferMoney(p, winPos, -1 * price, "buy");
                        }

                }
            }
        });
    }

    //This method controls taking the top Community Chest and the Chance card
    public void takeCard(int choice) {
        enterNum = 0;
        ColorAdjust c = new ColorAdjust(0, -0.5, -0.7, 0);
        GaussianBlur g = new GaussianBlur();
        c.setInput(g);
        gameBoard.setEffect(c);
        if (choice == COMMCHEST) {
            ImageView frontCard = b.drawCommChest(root);
            Button revealCard = makeBtn("Reveal Card", 600, 460, 150, 35);
            revealCard.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    root.getChildren().remove(revealCard);
                    Node[] nodes = b.revealCard(root, frontCard, "Community Chest");
                    Button cont = makeBtn("Continue", 600, 460, 150, 35);
                    root.getChildren().add(cont);
                    cont.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            if (b.getTopCommChest().getText().contains("Go to Jail")) {
                                root.getChildren().remove(cont);
                                goToJail();
                            } else if (b.getTopCommChest().getNewPos() != 41) {
                                gameBoard.setEffect(null);
                                root.getChildren().remove(cont);
                                root.getChildren().remove(nodes[0]);
                                root.getChildren().remove(nodes[1]);
                                if (b.getTopCommChest().getNewPos() < players[turn].getPlayerPos())
                                    movePiece((40 - players[turn].getPlayerPos()) + b.getTopCommChest().getNewPos(), b.getTopCommChest().getNewPos(), null, null, "card");
                            } else if (b.getTopCommChest().getText().contains("Get Out of Jail Free")) {
                                gameBoard.setEffect(null);
                                root.getChildren().remove(cont);
                                root.getChildren().remove(nodes[0]);
                                root.getChildren().remove(nodes[1]);
                                players[turn].setJailFree("Community Chest");
                                String t = "Congratulations " + players[turn].getName() + ", you got a Get Out of Jail Free card! Next time you're in jail, you can use this card to get out! Press Enter";
                                drawText(t);
                                scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                                    @Override
                                    public void handle(KeyEvent event) {
                                        switch (event.getCode()) {
                                            case ENTER:
                                                enterPressed = true;
                                        }
                                    }
                                });
                                scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
                                    @Override
                                    public void handle(KeyEvent event) {
                                        switch (event.getCode()) {
                                            case ENTER:
                                                if (enterPressed && enterNum == 0) {
                                                    enterNum++;
                                                    b.removeTopCommCard();
                                                    makeMenu("regular");
                                                }

                                        }
                                    }
                                });
                            } else {
                                GameCard g = b.getTopCommChest();
                                if ((-1 * g.getMoney()) > players[turn].getMoney()) {
                                    root.getChildren().remove(nodes[0]);
                                    root.getChildren().remove(nodes[1]);
                                    root.getChildren().remove(cont);
                                    gameBoard.setEffect(null);
                                    players[turn].setDebt((-1 * g.getMoney()) - players[turn].getMoney());
                                    possBankruptcy("the bank");
                                } else {
                                    ParallelTransition p = b.moveCard(root, (ImageView) nodes[0], (Text) nodes[1], cont);
                                    p.setOnFinished(new EventHandler<ActionEvent>() {
                                        @Override
                                        public void handle(ActionEvent event1) {
                                            GameCard g = b.getTopCommChest();
                                            b.removeTopCommCard();
                                            if (g.getMoney() < 0) {
                                                players[turn].addTransaction(g.getText() + ": -$" + (-1 * g.getMoney()) + "\n\tTotal: $" + (players[turn].getMoney() + g.getMoney()));
                                                transferMoney(null, turn, g.getMoney(), "card");
                                            } else {
                                                players[turn].addTransaction(g.getText() + ": +$" + (g.getMoney()) + "\n\tTotal: $" + (players[turn].getMoney() + g.getMoney()));
                                                transferMoney(null, turn, g.getMoney(), "card");
                                            }
                                        }
                                    });
                                    p.play();
                                }
                            }
                        }
                    });
                }
            });
            root.getChildren().add(revealCard);
        } else {
            ImageView frontCard = b.drawChance(root);
            Button revealCard = makeBtn("Reveal Card", 600, 460, 150, 35);
            revealCard.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    root.getChildren().remove(revealCard);
                    Node[] nodes = b.revealCard(root, frontCard, "Chance");
                    Button cont = makeBtn("Continue", 600, 460, 150, 35);
                    root.getChildren().add(cont);
                    cont.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            if (b.getTopChance().getText().contains("Go to Jail")) {
                                root.getChildren().remove(cont);
                                goToJail();
                            } else if (b.getTopChance().getText().contains("Get Out of Jail Free")) {
                                gameBoard.setEffect(null);
                                root.getChildren().remove(cont);
                                root.getChildren().remove(nodes[0]);
                                root.getChildren().remove(nodes[1]);
                                players[turn].setJailFree("Chance");
                                drawText("Congratulations " + players[turn].getName() + ", you got a Get Out of Jail Free card! Next time you're in jail, you can use this card to get out! Press Enter");
                                scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                                    @Override
                                    public void handle(KeyEvent event) {
                                        switch (event.getCode()) {
                                            case ENTER:
                                                enterPressed = true;
                                        }
                                    }
                                });
                                scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
                                    @Override
                                    public void handle(KeyEvent event) {
                                        switch (event.getCode()) {
                                            case ENTER:
                                                if (enterPressed && enterNum == 0) {
                                                    enterNum++;
                                                    b.removeTopChanceCard();
                                                    makeMenu("regular");
                                                }

                                        }
                                    }
                                });
                            } else if ((b.getTopChance().getMoney() % 2) == 1) {
                                gameBoard.setEffect(null);
                                root.getChildren().remove(cont);
                                root.getChildren().remove(nodes[0]);
                                root.getChildren().remove(nodes[1]);
                                int pos;
                                if (turn == (playerNum - 1)) pos = 0;
                                else pos = turn + 1;
                                if (players[turn].getMoney() < ((playerNum - 1) * (b.getTopChance().getMoney() / 10))) {
                                    players[turn].setDebt(((playerNum - 1) * (b.getTopChance().getMoney() / 10)) - players[turn].getMoney());
                                    possBankruptcy("the bank");
                                } else {
                                    transferMoney(null, pos, b.getTopChance().getMoney() / 10, b.getTopChance().getText());
                                }
                            } else if (b.getTopChance().getNewPos() != 41) {
                                gameBoard.setEffect(null);
                                root.getChildren().remove(cont);
                                root.getChildren().remove(nodes[0]);
                                root.getChildren().remove(nodes[1]);
                                if (b.getTopChance().getNewPos() == 40) {
                                    players[turn].setDoubleUtil(true);
                                    int nextUtil = b.getNextUtil(players[turn].getPlayerPos());
                                    if (nextUtil < players[turn].getPlayerPos())
                                        movePiece((40 - players[turn].getPlayerPos()) + nextUtil, nextUtil, null, null, "utility chance card");
                                    else
                                        movePiece(nextUtil - players[turn].getPlayerPos(), nextUtil, null, null, "utility chance card");
                                } else if (b.getTopChance().getNewPos() == 42) {
                                    players[turn].setDoubleRail(true);
                                    int nextUtil = b.getNextRail(players[turn].getPlayerPos());
                                    if (nextUtil < players[turn].getPlayerPos())
                                        movePiece((40 - players[turn].getPlayerPos()) + nextUtil, nextUtil, null, null, "utility chance card");
                                    else
                                        movePiece(nextUtil - players[turn].getPlayerPos(), nextUtil, null, null, "utility chance card");
                                } else {
                                    if (b.getTopChance().getNewPos() < players[turn].getPlayerPos()) {
                                        movePiece((40 - players[turn].getPlayerPos()) + b.getTopChance().getNewPos(), b.getTopChance().getNewPos(), null, null, "chance card");
                                    } else {
                                        movePiece((b.getTopChance().getNewPos() - players[turn].getPlayerPos()), b.getTopChance().getNewPos(), null, null, "chance card");
                                    }
                                }
                            } else {
                                ParallelTransition p = b.moveCard(root, (ImageView) nodes[0], (Text) nodes[1], cont);
                                p.setOnFinished(new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent event1) {
                                        GameCard g = b.getTopChance();
                                        b.removeTopChanceCard();
                                        if (g.getMoney() < 0)
                                            players[turn].addTransaction(g.getText() + ": -$" + (-1 * g.getMoney()) + "\n\tTotal: $" + (players[turn].getMoney() + g.getMoney()));
                                        else
                                            players[turn].addTransaction(g.getText() + ": +$" + (g.getMoney()) + "\n\tTotal: $" + (players[turn].getMoney() + g.getMoney()));
                                        transferMoney(b.getProperty(1), turn, g.getMoney(), "card");
                                    }
                                });
                                p.play();
                            }
                        }
                    });
                }
            });
            root.getChildren().add(revealCard);
        }
    }


    public void goToJail() {
        enterNum = 0;
        players[turn].setDoubleCount(0);
        players[turn].setDoubles(false);
        enterPressed = false;
        if (b.getSpot(players[turn].getPlayerPos()) == COMMCHEST || b.getSpot(players[turn].getPlayerPos()) == CHANCE) {
            root.getChildren().remove(b.getCardText());
            root.getChildren().remove(b.getCardBack());
            gameBoard.setEffect(null);
            if (b.getSpot(players[turn].getPlayerPos()) == COMMCHEST) b.removeTopCommCard();
            else b.removeTopChanceCard();
        }
        players[turn].setInJail(true);
        players[turn].setPlayerPos(10);
        Rectangle r = b.getPlayerRect(players[turn], 10);
        Rectangle r1 = makeRect((int) r.getX(), (int) r.getY(), (int) r.getWidth() / 3, (int) r.getHeight(), Color.rgb(37, 0, 224));
        root.getChildren().add(r1);
        Rectangle r2 = makeRect((int) (r.getX() + r1.getWidth()), (int) r.getY(), (int) r.getWidth() / 3, (int) r.getHeight(), Color.rgb(246, 248, 247));
        root.getChildren().add(r2);
        Rectangle r3 = makeRect((int) (r2.getX() + r2.getWidth()), (int) r.getY(), (int) r.getWidth() / 3, (int) r.getHeight(), Color.rgb(227, 30, 51));
        root.getChildren().add(r3);
        root.getChildren().remove(playerPieces.get(turn));
        playerPieces.get(turn).setX(players[turn].getLeftRowX());
        ImageView img = makeImg(players[turn].getIMG(), (int) playerPieces.get(turn).getX(), (int) playerPieces.get(turn).getY(), 30, 15);
        playerPieces.set(turn, img);
        playerPieces.get(turn).setRotate(playerPieces.get(turn).getRotate() - 90);
        root.getChildren().add(playerPieces.get(turn));
        drawPlayerRects();
        String text = players[turn].getName() + ", go to jail! Wait until your next turn to determine what you need to do next (Press enter)";
        drawText(text);
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case ENTER:
                        enterPressed = true;
                }
            }
        });
        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case ENTER:
                        if (enterPressed && enterNum == 0) {
                            enterNum++;
                            root.getChildren().remove(r1);
                            root.getChildren().remove(r2);
                            root.getChildren().remove(r3);
                            makeMenu("regular");
                        }

                }
            }
        });
    }

    public void addHouses() {
        HouseScreen h = new HouseScreen(players[turn], b);
        Group houseRoot = h.showScreen();
        scene.setRoot(houseRoot);
        Button complete = makeBtn("Return to Game", 1055, 590, 130, 50);
        complete.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                scene.setRoot(root);
                drawPlayerRects();
                if (h.getType().equals("bankruptcy")) makeMenu("bankruptcy");
                String[] colors = {"Purple", "White", "Light Blue", "Pink", "Utility", "Orange", "Red", "Yellow", "Green", "Dark Blue"};
                HashMap<String, ArrayList<Property>> props = players[turn].getProperties();
                SequentialTransition s = new SequentialTransition();
                //In this for loop, we are going through each of the properties and, if it was mortgaged, then the
                //mortgaged side of the property is shown on the gamescreen, and all the houses of the property are
                //also drawn on it as well
                for (int i = 0; i < colors.length; i++) {
                    for (int x = 0; x < props.get(colors[i]).size(); x++) {
                        b.setProperty(props.get(colors[i]).get(x));
                        Property prop = props.get(colors[i]).get(x);
                        if (prop.isMortgaged()) {
                            prop.getPropImg().setImage(new Image(prop.getMortgageImg()));
                        } else {
                            if (prop.getHouses() > 0) {
                                ArrayList<ImageView> propHouses = players[turn].getHouse(prop);
                                for (int y = 0; y < propHouses.size(); y++) {
                                    if (propHouses.size() > 4 && y < 4 && root.getChildren().contains(propHouses.get(y))) {
                                        FadeTransition houseFade = makeFade(0.0, 1.0, 1.0, 1);
                                        houseFade.setNode(propHouses.get(y));
                                        s.getChildren().add(houseFade);
                                        root.getChildren().remove(propHouses.get(y));
                                    } else {
                                        FadeTransition houseFade = makeFade(0.5, 0.0, 1.0, 1);
                                        houseFade.setNode(propHouses.get(y));
                                        s.getChildren().add(houseFade);
                                        root.getChildren().add(propHouses.get(y));
                                    }
                                }
                            }
                        }
                    }
                }
                s.play();
            }
        });
        houseRoot.getChildren().add(complete);
    }

    //This method creates the menu at the end of each turn
    public void makeMenu(String mode) {
        currCom = "";
        Rectangle AIRect = makeRect(400, 0, 550, 70, Color.IVORY);
        AIRect.setStroke(Color.BLACK);
        root.getChildren().add(AIRect);
        Rectangle turnRect = makeRect(790, 10, 55, 60, Color.PALEGOLDENROD);
        turnRect.setStroke(Color.BLACK);
        root.getChildren().add(turnRect);
        Text t;
        if (mode.equals("regular")) {
            ImageView nextTurn = makeImg("nextTurn.png", 800, 15, 35, 35);
            root.getChildren().add(nextTurn);
            Rectangle turnButton = makeRect(790, 10, 55, 60, Color.TRANSPARENT);
            turnButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (players[turn].getDoubles()) manageTurns();
                    else {
                        int i = turn;
                        while (true) {
                            if (i == (players.length - 1)) i = 0;
                            else ++i;
                            if (!players[i].isBankrupt()) break;
                        }
                        turn = i;
                        manageTurns();
                    }
                }
            });
            t = makeText("Next Turn", 792, 58, Font.font("Consolas", 10), Color.BLACK);
            root.getChildren().add(t);
            root.getChildren().add(turnButton);
        } else {
            Text remainText = makeText(("Remaining debt:"), 405, 30, Font.font("Ebrima", 15), Color.BLACK);
            remainText.setUnderline(true);
            root.getChildren().add(remainText);
            Text debtText = makeText(("$" + players[turn].getDebt()), 405, 50, Font.font("Ebrima", 15), Color.BLACK);
            root.getChildren().add(debtText);
            if (players[turn].getDebt() == 0) {
                Rectangle payRect = makeRect(870, 10, 60, 60, Color.PALEGOLDENROD);
                payRect.setStroke(Color.BLACK);
                root.getChildren().add(payRect);
                Rectangle payButton = makeRect(870, 10, 60, 60, Color.TRANSPARENT);
                payButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        drawText(("Congratulations " + players[turn].getName() + ", you paid off your debt! Press Enter"));
                        enterNum = 0;
                        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                            @Override
                            public void handle(KeyEvent event) {
                                switch (event.getCode()) {
                                    case ENTER:
                                        enterPressed = true;
                                }
                            }
                        });
                        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
                            @Override
                            public void handle(KeyEvent event) {
                                switch (event.getCode()) {
                                    case ENTER:
                                        if (enterPressed && enterNum == 0) {
                                            enterNum++;
                                            makeMenu("regular");
                                        }
                                }
                            }
                        });
                    }
                });
                ImageView payIcon = makeImg("payDebt.png", 895, 13, 20, 35);
                root.getChildren().add(payIcon);
                Text payText = makeText("Pay Off", 880, 58, Font.font("Consolas", 10), Color.BLACK);
                root.getChildren().add(payText);
                Text payText2 = makeText("Debt", 890, 67, Font.font("Consolas", 10), Color.BLACK);
                root.getChildren().add(payText2);
                root.getChildren().add(payButton);

            }
            ImageView nextTurn = makeImg("bankruptcyIcon.png", 807, 15, 20, 35);
            root.getChildren().add(nextTurn);
            Rectangle turnButton = makeRect(790, 10, 55, 60, Color.TRANSPARENT);
            t = makeText("Declare", 800, 58, Font.font("Consolas", 10), Color.BLACK);
            root.getChildren().add(t);
            t = makeText("Bankruptcy", 791, 67, Font.font("Consolas", 10), Color.BLACK);
            root.getChildren().add(t);
            root.getChildren().add(turnButton);
            turnButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    players[turn].setBankrupt(true);
                    enterNum = 0;
                    if (isWinner()) {
                        declareWinner();
                    } else {
                        playerNum--;
                        root.getChildren().remove(playerPieces.get(turn));
                        if (b.getSpot(players[turn].getPlayerPos()) == LUXURYTAX || b.getSpot(players[turn].getPlayerPos()) == INCOMETAX || b.getSpot(players[turn].getPlayerPos()) == COMMCHEST || b.getSpot(players[turn].getPlayerPos()) == CHANCE) {
                            drawText(("Well " + players[turn].getName() + ", looks like you've unfortunately gone bankrupt. Since your debt is owed to the bank, all of your properties will be auctioned."));
                        } else {
                            drawText(("Well " + players[turn].getName() + ", looks like you've unfortunately gone bankrupt. Since your debt is owed to " + b.getProperty(players[turn].getPlayerPos()).getOwner().getName() + ", all of your properties will be transferred to him"));
                        }
                        enterNum = 0;
                        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                            @Override
                            public void handle(KeyEvent event) {
                                switch (event.getCode()) {
                                    case ENTER:
                                        enterPressed = true;
                                }
                            }
                        });
                        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
                            @Override
                            public void handle(KeyEvent event) {
                                switch (event.getCode()) {
                                    case ENTER:
                                        if (enterPressed && enterNum == 0) {
                                            enterNum++;
                                            bankruptcy(players[turn]);
                                        }
                                }
                            }
                        });
                    }
                }
            });
        }
        Rectangle buildRect = makeRect(700, 10, 60, 60, Color.PALEGOLDENROD);
        buildRect.setStroke(Color.BLACK);
        root.getChildren().add(buildRect);
        ImageView buildIcon = makeImg("buildIcon.png", 712, 10, 35, 35);
        root.getChildren().add(buildIcon);
        t = makeText("Manage", 712, 58, Font.font("Consolas", 10), Color.BLACK);
        root.getChildren().add(t);
        t = makeText("Properties", 702, 67, Font.font("Consolas", 10), Color.BLACK);
        root.getChildren().add(t);
        Rectangle buildButton = makeRect(700, 10, 60, 60, Color.TRANSPARENT);
        buildButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                addHouses();
            }
        });
        root.getChildren().add(buildButton);
        Rectangle tradeRect = makeRect(613, 10, 59, 60, Color.PALEGOLDENROD);
        tradeRect.setStroke(Color.BLACK);
        Rectangle tradeButton = makeRect(613, 10, 59, 60, Color.TRANSPARENT);
        tradeButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                chooseOpp();
            }
        });
        root.getChildren().add(tradeRect);
        ImageView trade = makeImg("tradeIcon.png", 623, 15, 35, 35);
        root.getChildren().add(trade);
        t = makeText("Make Trade", 615, 58, Font.font("Consolas", 10), Color.BLACK);
        root.getChildren().add(t);
        root.getChildren().add(tradeButton);
        Rectangle viewRect = makeRect(526, 10, 57, 60, Color.PALEGOLDENROD);
        viewRect.setStroke(Color.BLACK);
        root.getChildren().add(viewRect);
        ImageView viewInfo = makeImg("viewPlayer.png", 537, 15, 35, 35);
        root.getChildren().add(viewInfo);
        t = makeText("View Stats", 528, 58, Font.font("Consolas", 10), Color.BLACK);
        root.getChildren().add(t);
        Rectangle viewButton = makeRect(526, 10, 57, 60, Color.TRANSPARENT);
        viewButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                createViewScreen(players[turn]);
            }
        });
        root.getChildren().add(viewButton);
    }

    public void createViewScreen(Player p) {
        ViewScreen v = new ViewScreen(p, "regular", null);
        Group vRoot = v.drawScreen();
        scene.setRoot(vRoot);
        Button done = makeBtn("Return to Game", 1120, 595, 100, 50);
        done.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                scene.setRoot(root);
                drawPlayerRects();
                if (v.getType().equals("bankruptcy")) makeMenu("bankruptcy");
            }
        });
        vRoot.getChildren().add(done);

        Button newScreen = makeBtn("View Another Player's Stats", 850, 595, 200, 50);
        newScreen.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                vRoot.getChildren().clear();
                v.switchStats(players);
                for (int i = 0; i < players.length; i++) {
                    Button b = makeBtn("Choose", (250 + (210 * i)), 420, 100, 40);
                    int finalI = i;
                    b.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            createViewScreen(players[finalI]);
                        }
                    });
                    vRoot.getChildren().add(b);
                }
            }
        });
        vRoot.getChildren().add(newScreen);
    }

    //When a player decides to trade with another player, this method is invoked in which the player gets to choose
    //the player that they want to trade with
    public void chooseOpp() {
        TradeScreen t;
        if (players[turn].getDebt() == 0) t = new TradeScreen(players[turn], "regular");
        else t = new TradeScreen(players[turn], "bankruptcy");
        Group tradeRoot = t.chooseOpponent();
        int curr = 0;
        //This for loop allows the player to choose which player they will actually be trading with
        for (int i = 0; i < players.length; i++) {
            if (!players[i].getName().equals(players[turn].getName())) {
                Rectangle rect = makeRect(400 + (210 * curr), 350, 200, 100, Color.LIGHTBLUE);
                tradeRoot.getChildren().add(rect);
                ImageView imgView = makeImg(players[i].getIMG(), 450 + (210 * curr), 400, 100, 50);
                tradeRoot.getChildren().add(imgView);
                Text nameText;
                nameText = makeText((players[i].getName() + ": $" + Integer.toString(players[i].getMoney())), 400 + (210 * curr), 370, Font.font("Comic Sans MS", 18), Color.BLACK);
                tradeRoot.getChildren().add(nameText);
                Button b = makeBtn("Choose", 450 + (210 * curr), 460, 100, 40);
                int finalI = i;
                b.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        startTrade(players[finalI], tradeRoot, t);
                    }
                });
                tradeRoot.getChildren().add(b);
                curr++;
            }
        }
        scene.setRoot(tradeRoot);
    }

    //Once the player chooses which player they will be trading with, the trade can begin which is what this method takes
    //care of
    public void startTrade(Player opp, Group tradeRoot, TradeScreen t) {
        t.makeTrade(opp);
        Button propBtn = makeBtn("Choose Property", 50, 305, 130, 40);
        propBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ViewScreen v = new ViewScreen(players[turn], "trade", t.getCurrPlayerTransactions());
                Group vRoot = v.drawScreen();
                Button done = makeBtn("Return", 1120, 595, 100, 50);
                done.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        scene.setRoot(tradeRoot);
                        t.drawTransactions();
                    }
                });
                vRoot.getChildren().add(done);
                scene.setRoot(vRoot);
            }
        });
        tradeRoot.getChildren().add(propBtn);
        //Figure it out yourself
        Button propBtn2 = makeBtn("Choose Property", 1080, 305, 130, 40);
        propBtn2.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ViewScreen v = new ViewScreen(t.getOpp(), "trade", t.getOppTransactions());
                Group vRoot = v.drawScreen();
                Button done = makeBtn("Return", 1120, 595, 100, 50);
                done.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        scene.setRoot(tradeRoot);
                        t.drawTransactions();
                    }
                });
                vRoot.getChildren().add(done);
                scene.setRoot(vRoot);
            }
        });
        tradeRoot.getChildren().add(propBtn2);
        Button returnBack = makeBtn("Return to Game", 540, 200, 150, 50);
        returnBack.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                scene.setRoot(root);
            }
        });
        for (int i = 0; i < 2; i++) {
            Button accept = makeBtn("Accept Trade", 2 + (1035 * i), 610, 100, 35);
            accept.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (t.getAcceptTurn() != 0) {
                        SequentialTransition move = t.finishTrade(accept);
                        move.setOnFinished(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                SequentialTransition s2 = new SequentialTransition();
                                t.drawPlayerRects(opp);
                                t.swapBelongings(opp, players[turn], t.getOppTransactions(), 800, -500, 800, 300, -490, s2);
                                s2.setOnFinished(new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent event) {
                                        scene.setRoot(root);
                                        enterNum = 0;
                                        drawText(("Congratulations " + players[turn].getName() + " and " + opp.getName() + " on completing your trade! Press Enter to continue"));
                                        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                                            @Override
                                            public void handle(KeyEvent event) {
                                                switch (event.getCode()) {
                                                    case ENTER:
                                                        enterPressed = true;
                                                }
                                            }
                                        });
                                        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
                                            @Override
                                            public void handle(KeyEvent event) {
                                                switch (event.getCode()) {
                                                    case ENTER:
                                                        if (enterPressed && enterNum == 0) {
                                                            enterNum++;
                                                            completeTrade(t, players[turn], opp);
                                                        }
                                                }
                                            }
                                        });
                                    }
                                });
                                s2.play();
                            }
                        });
                        move.play();
                    } else t.finishTrade(accept);

                }
            });
            tradeRoot.getChildren().add(accept);
            Button decline = makeBtn("Decline Trade", 105 + (1035 * i), 610, 100, 35);
            int finalI = i;
            decline.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (t.getCurrPlayerTransactions().size() == 0 && t.getOppTransactions().size() == 0) {
                        t.getCurrPlayerTransactions().clear();
                        t.getOppTransactions().clear();
                        scene.setRoot(root);
                        tradeDeclined(players[finalI]);
                    }
                }
            });
            tradeRoot.getChildren().add(decline);
        }
        tradeRoot.getChildren().add(returnBack);
    }

    public void tradeDeclined(Player p) {
        enterPressed = false;
        drawText((p.getName() + " declined the trade! Press Enter to continue"));
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case ENTER:
                        enterPressed = true;
                }
            }
        });
        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case ENTER:
                        if (enterPressed) {
                            makeMenu("regular");
                        }

                }
            }
        });
    }

    public void completeTrade(TradeScreen t, Player p, Player opp) {
        drawPlayerRects();
        ArrayList<Transaction> currTransactions = t.getCurrPlayerTransactions();
        ArrayList<Transaction> oppTransactions = t.getOppTransactions();
        SequentialTransition s = new SequentialTransition();
        int pTurn = 0, oppTurn = 0;
        for (int i = 0; i < players.length; i++) {
            if (players[i].getName().equals(p.getName())) pTurn = i;
            if (players[i].getName().equals(opp.getName())) oppTurn = i;
        }

        for (int i = 0; i < currTransactions.size(); i++) {
            if (currTransactions.get(i).getType().equals("Property")) {
                p.removeProperty(currTransactions.get(i).getProp());
                opp.addProperty(currTransactions.get(i).getProp());
                currTransactions.get(i).getProp().setOwner(opp);
                currTransactions.get(i).getProp().setOwned(true);
                Rectangle r = currTransactions.get(i).getProp().getPropRect();
                root.getChildren().remove(r);
                FadeTransition f = makeFade(1.0, 0.0, 1.0, 1);
                r.setFill(currTransactions.get(i).getProp().getOwner().getColor());
                f.setNode(r);
                s.getChildren().add(f);
                root.getChildren().add(r);
                ImageView propImg = currTransactions.get(i).getProp().getPropImg();
                root.getChildren().remove(propImg);
                int imgPos = p.getPropPos(currTransactions.get(i).getProp());
                ArrayList<Property> imgProps = p.getPropImages();
                imgProps.remove(imgPos);
                for (int x = imgPos; x < (imgProps.size()); x++) {
                    root.getChildren().remove(imgProps.get(x).getPropImg());
                    if ((x % 5) == 0 && x > 0) {
                        if (pTurn == 0 || pTurn == 2) {
                            imgProps.get(x).getPropImg().setX(imgProps.get(x).getPropImg().getX() + 60);
                            if (pTurn == 0) imgProps.get(x).getPropImg().setY(100 + (35 * 5));
                            else imgProps.get(x).getPropImg().setY(320 + (35 * 5));
                        } else {
                            imgProps.get(x).getPropImg().setX(imgProps.get(x).getPropImg().getX() - 55);
                            if (pTurn == 1) imgProps.get(x).getPropImg().setY(100 + (35 * 5));
                            else imgProps.get(x).getPropImg().setY(320 + (35 * 5));
                        }

                    } else imgProps.get(x).getPropImg().setY(imgProps.get(x).getPropImg().getY() - 35);
                    root.getChildren().add(imgProps.get(x).getPropImg());
                }
                ImageView newImg = makeImg(currTransactions.get(i).getProp().getImg(), opp.getNextPropertyX(oppTurn), opp.getNextPropertyY(oppTurn), 30, 50);
                if ((oppTurn == 1 || oppTurn == 3) && (pTurn == 0 || pTurn == 2))
                    newImg.setRotate(propImg.getRotate() - 180);
                else if ((pTurn == 1 || pTurn == 3) && (oppTurn == 0 || oppTurn == 2))
                    newImg.setRotate(propImg.getRotate() - 180);
                else newImg.setRotate(propImg.getRotate());
                currTransactions.get(i).getProp().setPropImg(newImg);
                root.getChildren().add(newImg);
            }
        }
        for (int i = 0; i < oppTransactions.size(); i++) {
            if (oppTransactions.get(i).getType().equals("Property")) {
                opp.removeProperty(oppTransactions.get(i).getProp());
                p.addProperty(oppTransactions.get(i).getProp());
                oppTransactions.get(i).getProp().setOwner(p);
                oppTransactions.get(i).getProp().setOwned(true);
                Rectangle r = oppTransactions.get(i).getProp().getPropRect();
                root.getChildren().remove(r);
                FadeTransition f2 = makeFade(1.0, 0.0, 1.0, 1);
                r.setFill(oppTransactions.get(i).getProp().getOwner().getColor());
                f2.setNode(r);
                s.getChildren().add(f2);
                root.getChildren().add(r);
                ImageView propImg = oppTransactions.get(i).getProp().getPropImg();
                root.getChildren().remove(propImg);
                int imgPos = opp.getPropPos(oppTransactions.get(i).getProp());
                ArrayList<Property> imgProps = opp.getPropImages();
                imgProps.remove(imgPos);
                for (int x = imgPos; x < (imgProps.size()); x++) {
                    root.getChildren().remove(imgProps.get(x).getPropImg());
                    if ((x % 5) == 0 && x > 0) {
                        if (oppTurn == 0 || oppTurn == 2) {
                            imgProps.get(x).getPropImg().setX(imgProps.get(x).getPropImg().getX() + 60);
                            if (oppTurn == 0) imgProps.get(x).getPropImg().setY(100 + (35 * 5));
                            else imgProps.get(x).getPropImg().setY(320 + (35 * 5));
                        } else {
                            imgProps.get(x).getPropImg().setX(imgProps.get(x).getPropImg().getX() - 55);
                            if (oppTurn == 1) imgProps.get(x).getPropImg().setY(100 + (35 * 5));
                            else imgProps.get(x).getPropImg().setY(320 + (35 * 5));
                        }

                    } else imgProps.get(x).getPropImg().setY(imgProps.get(x).getPropImg().getY() - 35);
                    root.getChildren().add(imgProps.get(x).getPropImg());
                }
                ImageView newImg = makeImg(oppTransactions.get(i).getProp().getImg(), p.getNextPropertyX(pTurn), p.getNextPropertyY(pTurn), 30, 50);
                if ((pTurn == 1 || pTurn == 3) && (oppTurn == 0 || oppTurn == 2))
                    newImg.setRotate(propImg.getRotate() - 180);
                else if ((oppTurn == 1 || oppTurn == 3) && (pTurn == 0 || pTurn == 2))
                    newImg.setRotate(propImg.getRotate() - 180);
                else newImg.setRotate(propImg.getRotate());
                oppTransactions.get(i).getProp().setPropImg(newImg);
                root.getChildren().add(newImg);
            }
        }
        s.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                enterPressed = true;
                if (t.getType().equals("bankruptcy")) makeMenu("bankruptcy");
                else makeMenu("regular");
            }
        });
        s.play();
    }

    public void possBankruptcy(String debtor) {
        drawPlayerRects();
        players[turn].setMoney(-1 * players[turn].getMoney());
        enterNum = 0;
        drawText((players[turn].getName() + ", you owe a debt of $" + players[turn].getDebt() + " to " + debtor + " that you must pay or else you'll go bankrupt! Try to sell houses, mortgage properties, or make a trade to get money. Press Enter"));
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case ENTER:
                        enterPressed = true;
                }
            }
        });
        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case ENTER:
                        if (enterPressed && enterNum == 0) {
                            enterNum++;
                            drawPlayerRects();
                            makeMenu("bankruptcy");
                        }

                }
            }
        });
    }

    //When a player has officially gone bankrupt, this method is invoked
    public void bankruptcy(Player p) {
        if (b.getSpot(p.getPlayerPos()) == LUXURYTAX || b.getSpot(players[turn].getPlayerPos()) == INCOMETAX || b.getSpot(players[turn].getPlayerPos()) == COMMCHEST || b.getSpot(players[turn].getPlayerPos()) == CHANCE) {
            if (p.getPropImages().size() != 0) {
                if (p.getPropImages().get(0).getHouses() > 0) {
                    ArrayList<ImageView> propHouses = players[turn].getHouse(p.getPropImages().get(0));
                    for (int z = 0; z < propHouses.size(); z++) root.getChildren().remove(propHouses.get(z));
                }
                drawText(("Let's auction " + p.getPropImages().get(0).getName() + " now!"));
                enterNum = 0;
                scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        switch (event.getCode()) {
                            case ENTER:
                                enterPressed = true;
                        }
                    }
                });
                scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        switch (event.getCode()) {
                            case ENTER:
                                if (enterPressed && enterNum == 0) {
                                    enterNum++;
                                    auctionProperty(p.getPropImages().get(0));
                                }

                        }
                    }
                });
            } else {
                p.getProperties().clear();
                drawText(("Now that all of " + players[turn].getName() + "'s properties have been auctioned, he is officially out of the game"));
                enterNum = 0;
                scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        switch (event.getCode()) {
                            case ENTER:
                                enterPressed = true;
                        }
                    }
                });
                scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        switch (event.getCode()) {
                            case ENTER:
                                if (enterPressed && enterNum == 0) {
                                    enterNum++;
                                    drawPlayerRects();
                                    int i = turn;
                                    while (true) {
                                        if (i == (players.length - 1)) i = 0;
                                        else ++i;
                                        if (!players[i].isBankrupt()) break;
                                    }
                                    turn = i;
                                    manageTurns();
                                }
                        }
                    }
                });
            }
        } else {
            int oppTurn = 0;
            ArrayList<Property> mortgagedProps = new ArrayList<>();
            for (int i = 0; i < playerNum; i++) {
                if (players[i].getName().equals(b.getProperty(players[turn].getPlayerPos()).getName())) oppTurn = i;
            }
            for (int i = 0; i < players[turn].getPropImages().size(); i++) {
                Property curr = players[turn].getPropImages().get(i);
                if (curr.isMortgaged()) mortgagedProps.add(curr);
            }
            if (mortgagedProps.size() > 0) payMortgagedProps(mortgagedProps, oppTurn);
        }

    }

    public void payMortgagedProps(ArrayList<Property> props, int currTurn) {
        MortgageScreen m = new MortgageScreen(props, players[currTurn], b);
        Group mortgageRoot = m.drawScreen();
        scene.setRoot(mortgageRoot);
        Button end = makeBtn("Return", 1100, 600, 80, 40);
        end.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                scene.setRoot(root);
                int oppTurn = 0;
                for (int i = 0; i < players.length; i++) {
                    if (m.getP().getName().equals(players[i].getName())) oppTurn = i;
                }
                ArrayList<Property> props = m.getProps();
                for (int i = 0; i < props.size(); i++) {
                    Property curr = props.get(i);
                    Player opp = m.getP();
                    players[turn].removeProperty(curr);
                    opp.addProperty(curr);
                    curr.setOwner(opp);
                    curr.setOwned(true);
                    Rectangle r = curr.getPropRect();
                    root.getChildren().remove(r);
                    FadeTransition f = makeFade(1.0, 0.0, 1.0, 1);
                    r.setFill(curr.getOwner().getColor());
                    f.setNode(r);
                    f.play();
                    root.getChildren().add(r);
                    root.getChildren().remove(curr.getPropImg());
                    ImageView newImg;
                    if (curr.isMortgaged())
                        newImg = makeImg(curr.getMortgageImg(), opp.getNextPropertyX(oppTurn), opp.getNextPropertyY(oppTurn), 30, 50);
                    else {
                        newImg = makeImg(curr.getImg(), opp.getNextPropertyX(oppTurn), opp.getNextPropertyY(oppTurn), 30, 50);
                    }
                    if (oppTurn == 0 || oppTurn == 2) newImg.setRotate(newImg.getRotate() + 90);
                    else newImg.setRotate(newImg.getRotate() - 90);
                    curr.setPropImg(newImg);
                    root.getChildren().add(newImg);
                }
                if (m.getInterest() > 0) {
                    enterNum = 0;
                    drawText((m.getP().getName() + ", the total amount of interest that you owe the Bank is $" + m.getInterest() + ". Press Enter"));
                    scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                        @Override
                        public void handle(KeyEvent event) {
                            switch (event.getCode()) {
                                case ENTER:
                                    enterPressed = true;
                            }
                        }
                    });
                    int finalOppTurn = oppTurn;
                    scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
                        @Override
                        public void handle(KeyEvent event) {
                            switch (event.getCode()) {
                                case ENTER:
                                    if (enterPressed && enterNum == 0) {
                                        enterNum++;
                                        transferMoney(null, finalOppTurn, -1 * m.getInterest(), "random");
                                    }
                            }
                        }
                    });
                } else {
                    enterNum = 0;
                    drawText((m.getP().getName() + ", since you lifted all of the mortgages, you don't owe anything to the bank. Press Enter"));
                    scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                        @Override
                        public void handle(KeyEvent event) {
                            switch (event.getCode()) {
                                case ENTER:
                                    enterPressed = true;
                            }
                        }
                    });
                    scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
                        @Override
                        public void handle(KeyEvent event) {
                            switch (event.getCode()) {
                                case ENTER:
                                    if (enterPressed && enterNum == 0) {
                                        enterNum++;
                                        playerNum++;
                                        int i = turn;
                                        while (true) {
                                            if (i == (players.length - 1)) i = 0;
                                            else ++i;
                                            if (!players[i].isBankrupt()) break;
                                        }
                                        turn = i;
                                        manageTurns();
                                    }
                            }
                        }
                    });
                }

            }
        });
        mortgageRoot.getChildren().add(end);
    }

    public boolean isWinner() {
        int bankrupt = 0;
        for (int i = 0; i < playerNum; i++) {
            if (players[i].isBankrupt()) bankrupt++;
        }
        if (bankrupt == (players.length - 1)) return true;
        else return false;
    }

    public void declareWinner() {
        drawPlayerRects();
        int winner = 0;
        for (int i = 0; i < players.length; i++) {
            if (!players[i].isBankrupt()) winner = i;
        }
        drawText("Congratulations " + players[winner].getName() + ", you won!!! Press Enter to finish the game");
        enterNum = 0;
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case ENTER:
                        enterPressed = true;
                }
            }
        });
        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case ENTER:
                        if (enterPressed && enterNum == 0) {
                            enterNum++;
                            Platform.exit();
                        }
                }
            }
        });
    }

    public Line makeLine(int startX, int startY, int endX, int endY, Color c, int w) {
        Line l = new Line();
        l.setStartX(startX);
        l.setStartY(startY);
        l.setEndX(endX);
        l.setEndY(endY);
        l.setStroke(c);
        l.setStrokeWidth(w);
        return l;
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
        Text t = new Text(text);
        t.setX(x);
        t.setY(y);
        t.setFont(f);
        t.setFill(c);
        return t;
    }

    public ImageView makeImg(String url, int x, int y, int width, int height) {
        Image img = new Image(url);
        ImageView imgPic = new ImageView(img);
        imgPic.setX(x);
        imgPic.setY(y);
        imgPic.setFitWidth(width);
        imgPic.setFitHeight(height);
        return imgPic;
    }

    public Button makeBtn(String t, int x, int y, int width, int height) {
        Button b = new Button(t);
        b.setLayoutX(x);
        b.setLayoutY(y);
        b.setPrefSize(width, height);
        return b;
    }

    public FadeTransition makeFade(double seconds, double fromValue, double toValue, int cycles) {
        FadeTransition f = new FadeTransition();
        f.setDuration(Duration.seconds(seconds));
        f.setFromValue(fromValue);
        f.setToValue(toValue);
        if (cycles == -1) f.setCycleCount(Animation.INDEFINITE);
        else f.setCycleCount(cycles);
        return f;
    }

    public ScaleTransition makeScale(double seconds, double toX, double toY, int cycles) {
        ScaleTransition s = new ScaleTransition();
        s.setDuration(Duration.seconds(seconds));
        s.setFromX(1);
        s.setFromY(1);
        s.setToX(toX);
        s.setToY(toY);
        s.setCycleCount(cycles);
        return s;
    }

    public TranslateTransition makeMove(double seconds, double xMove, double yMove) {
        TranslateTransition imgTran = new TranslateTransition();
        imgTran.setDuration(Duration.seconds(seconds));
        if (xMove != -1) imgTran.setToX(xMove);
        if (yMove != -1) imgTran.setToY(yMove);
        imgTran.setAutoReverse(false);
        return imgTran;
    }

    public RotateTransition makeRotation(double seconds, Point3D p, int angle) {
        RotateTransition rotate = new RotateTransition();
        rotate.setAxis(p);
        rotate.setByAngle(angle);
        rotate.setDuration(Duration.seconds(seconds));
        return rotate;
    }

    public void drawText(String text) {
        if (!text.equals(inGameText.get(0))) s.getChildren().remove(commentary);
        currCom = "";
        Rectangle AIRect = new Rectangle();
        AIRect.setWidth(550);
        AIRect.setHeight(70);
        AIRect.setFill(Color.IVORY);
        AIRect.setStroke(Color.BLACK);
        s = new StackPane();
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

        commentary = new Text();
        commentary.setFont(Font.font("Segoe Print"));
        s.setLayoutX(400);
        s.setLayoutY(0);
        s.getChildren().addAll(AIRect, commentary);
        root.getChildren().add(s);
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

    public void addProperty(Player p, Property currProp) {
        p.addProperty(currProp);
        currProp.setOwner(p);
        currProp.setOwned(true);
    }
}
