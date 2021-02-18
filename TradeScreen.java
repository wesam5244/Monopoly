import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;

/*
This screen is used to trade between players
 */
public class TradeScreen {
    private Group tradeRoot = new Group();
    private Player p;
    private Player opp;
    private ArrayList<Transaction> currPlayerTransactions = new ArrayList<>();
    private ArrayList<Transaction> oppTransactions = new ArrayList<>();
    private int acceptTurn = 0;
    private Rectangle rect1;
    private Rectangle rect2;
    private String type;

    public TradeScreen(Player p, String type) {
        this.p = p;
        this.type = type;
    }

    public Player getOpp() {
        return opp;
    }

    public int getAcceptTurn() {
        return acceptTurn;
    }

    public String getType() {
        return type;
    }

    public ArrayList<Transaction> getCurrPlayerTransactions() {
       /* Transaction t = new Transaction("Property");
        t.setProp(p.getProperties().get("Purple").get(0));
        currPlayerTransactions.add(t);
        t = new Transaction("Property");
        t.setProp(p.getProperties().get("Purple").get(1));
        currPlayerTransactions.add(t);
        t = new Transaction("Property");
        t.setProp(p.getProperties().get("Light Blue").get(1));
        currPlayerTransactions.add(t);*/
        return currPlayerTransactions;
    }

    public ArrayList<Transaction> getOppTransactions() {
        return oppTransactions;
    }

    public Group chooseOpponent() {
        ImageView background = makeImg("secondBack.jpg", 0, 0, 1250, 650);
        tradeRoot.getChildren().add(background);
        Text title = makeText("Trade", 600, 100, Font.font("Verdana", 40), Color.BLACK);
        tradeRoot.getChildren().add(title);
        Text choose = makeText("Who would you like to trade with?", 400, 300, Font.font("Tw Cen MT", 40), Color.BLACK);
        tradeRoot.getChildren().add(choose);
        return tradeRoot;
    }

    //This method draws the main trade screen and displays all of the options that they have
    public void makeTrade(Player opp) {
        this.opp = opp;
        tradeRoot.getChildren().clear();
        ImageView background = makeImg("tradeback.jpg", 0, 0, 1250, 650);
        tradeRoot.getChildren().add(background);
        Text title = makeText("Trade", 600, 100, Font.font("Verdana", 40), Color.BLACK);
        tradeRoot.getChildren().add(title);
        drawTransactions();
        for (int i = 0; i < 2; i++) {
            Text nameText;
            if (i == 0) nameText = makeText(p.getName(), 210, 185, Font.font("TW Cen MT", 35), Color.BLACK);
            else nameText = makeText(opp.getName(), 730, 185, Font.font("TW Cen MT", 35), Color.BLACK);
            nameText.setUnderline(true);
            tradeRoot.getChildren().add(nameText);
        }
        ImageView swap = makeImg("swapIcon.png", 540, 265, 150, 150);
        tradeRoot.getChildren().add(swap);
        drawPlayerRects(opp);
        drawOptions();
    }

    //This method displays all of the transactions on the screen
    public void drawTransactions() {
        if (tradeRoot.getChildren().contains(rect1) && tradeRoot.getChildren().contains(rect2)) {
            tradeRoot.getChildren().remove(rect1);
            tradeRoot.getChildren().remove(rect2);
        }
        //These are the rectangles on which the transactions will be displayed
        rect1 = makeRect(210, 200, 300, 430, Color.LIGHTBLUE);
        rect1.setStroke(Color.BLACK);
        tradeRoot.getChildren().add(rect1);
        rect2 = makeRect(730, 200, 300, 430, Color.LIGHTBLUE);
        rect2.setStroke(Color.BLACK);
        tradeRoot.getChildren().add(rect2);
        for (int i = 0; i < 2; i++) {
            if (i == 0) displayTransactions(currPlayerTransactions, 215, 482);
            else if (i == 1) displayTransactions(oppTransactions, 735, 1000);
        }
    }

    //This method displays each individual transaction
    public void displayTransactions(ArrayList<Transaction> transactions, int textX, int picX) {
        for (int x = 0; x < transactions.size(); x++) {
            if (transactions.get(x).getType().equals("Property")) {
                String text = (x + 1) + ". " + transactions.get(x).getProp().getName();
                Text t = makeText(text, textX, 230 + (30 * x), Font.font("Verdana", 21), Color.BLACK);
                ImageView cancelIcon = makeImg("cancelIcon.png", picX, (int) t.getY() - 20, 25, 25);
                Rectangle r = makeRect((int) cancelIcon.getX(), (int) cancelIcon.getY(), 25, 25, Color.TRANSPARENT);
                int finalX = x;
                r.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        transactions.remove(transactions.get(finalX));
                        drawTransactions();
                    }
                });
                tradeRoot.getChildren().add(t);
                tradeRoot.getChildren().add(cancelIcon);
                tradeRoot.getChildren().add(r);
            } else if (transactions.get(x).getType().equals("Jail")) {
                String text = (x + 1) + ". Get Out of Jail Card";
                Text t = makeText(text, textX, 230 + (30 * x), Font.font("Verdana", 21), Color.BLACK);
                ImageView cancelIcon = makeImg("cancelIcon.png", picX, (int) t.getY() - 20, 25, 25);
                Rectangle r = makeRect((int) cancelIcon.getX(), (int) cancelIcon.getY(), 25, 25, Color.TRANSPARENT);
                int finalX = x;
                r.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        transactions.remove(transactions.get(finalX));
                        drawCards();
                        drawTransactions();
                    }
                });
                tradeRoot.getChildren().add(t);
                tradeRoot.getChildren().add(cancelIcon);
                tradeRoot.getChildren().add(r);
            } else {
                String text = (x + 1) + ". Cash: $" + transactions.get(x).getMoney();
                Text t = makeText(text, textX, 230 + (30 * x), Font.font("Verdana", 21), Color.BLACK);
                ImageView cancelIcon = makeImg("cancelIcon.png", picX, (int) t.getY() - 20, 25, 25);
                Rectangle r = makeRect((int) cancelIcon.getX(), (int) cancelIcon.getY(), 25, 25, Color.TRANSPARENT);
                int finalX = x;
                r.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        transactions.remove(transactions.get(finalX));
                        drawTransactions();
                    }
                });
                tradeRoot.getChildren().add(t);
                tradeRoot.getChildren().add(cancelIcon);
                tradeRoot.getChildren().add(r);
            }
        }
    }

    public void drawPlayerRects(Player opp) {
        Rectangle rect = makeRect(0, 0, 200, 100, Color.LIGHTBLUE);
        tradeRoot.getChildren().add(rect);
        ImageView playerPiece = makeImg(p.getIMG(), 50, 50, 100, 50);
        tradeRoot.getChildren().add(playerPiece);
        String mon;
        if (p.getDebt() == 0) mon = p.getName() + ": $" + Integer.toString(p.getMoney());
        else mon = p.getName() + ": $" + Integer.toString(p.getDebt()) + " (Debt)";
        Text nameText = makeText(mon, 0, 20, Font.font("Comic Sans MS", 18), Color.BLACK);
        tradeRoot.getChildren().add(nameText);
        rect = makeRect(1050, 0, 200, 100, Color.LIGHTBLUE);
        tradeRoot.getChildren().add(rect);
        playerPiece = makeImg(opp.getIMG(), 1100, 50, 100, 50);
        tradeRoot.getChildren().add(playerPiece);
        if (opp.getDebt() == 0) mon = opp.getName() + ": $" + Integer.toString(opp.getMoney());
        else mon = opp.getName() + ": $" + Integer.toString(opp.getDebt()) + " (Debt)";
        nameText = makeText(mon, 1050, 20, Font.font("Comic Sans MS", 18), Color.BLACK);
        tradeRoot.getChildren().add(nameText);
    }

    //Drawing all of the possible things that the player can trade
    public void drawOptions() {
        for (int i = 0; i < 2; i++) {
            Text allOps = makeText("Options", 65 + (1030 * i), 230, Font.font("TW Cen MT", 25), Color.BLACK);
            tradeRoot.getChildren().add(allOps);
            Rectangle propRect = makeRect(5 + (1035 * i), 250, 200, 50, Color.AQUAMARINE);
            propRect.setStroke(Color.BLACK);
            tradeRoot.getChildren().add(propRect);
            Text propOp = makeText("Property", 10 + (1035 * i), 280, Font.font("Verdana", 20), Color.BLACK);
            tradeRoot.getChildren().add(propOp);
            Rectangle cardRect = makeRect(5 + (1035 * i), 350, 200, 50, Color.AQUAMARINE);
            cardRect.setStroke(Color.BLACK);
            tradeRoot.getChildren().add(cardRect);
            Text cardOp = makeText("Get Out of Jail", 10 + (1035 * i), 380, Font.font("Verdana", 20), Color.BLACK);
            tradeRoot.getChildren().add(cardOp);
            Player curr;
            if (i == 0) curr = p;
            else curr = opp;
            //If the player is not in debt, they can also provide money as well for trade
            if (curr.getDebt() == 0) {
                Rectangle monRect = makeRect(5 + (1035 * i), 510, 200, 50, Color.AQUAMARINE);
                cardRect.setStroke(Color.BLACK);
                tradeRoot.getChildren().add(monRect);
                Text monOp = makeText("Money", 10 + (1035 * i), 540, Font.font("Verdana", 20), Color.BLACK);
                tradeRoot.getChildren().add(monOp);
                Text mon = makeText("$", 10 + (1035 * i), 590, Font.font("Verdana", 20), Color.BLACK);
                tradeRoot.getChildren().add(mon);
                TextField t = new TextField("0");
                t.setLayoutX(30 + (1035 * i));
                t.setLayoutY(565);
                t.setPrefWidth(80);
                t.setPrefHeight(40);
                Button getMon = makeBtn("Add", 120 + (1035 * i), 565, 80, 40);
                int finalI = i;
                getMon.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        //This if statement is for if the player offering the money is the current player, and the else
                        //statement associated with this is for the player who the current player is trading with
                        if (finalI == 0) {
                            //If the player hasn't already offered money, add that as a new transaction
                            if (!jailInTransactions(currPlayerTransactions, "Money")) {
                                if ((Integer.parseInt(t.getText()) <= p.getMoney())) {
                                    Transaction tran = new Transaction("Money");
                                    tran.setMoney(Integer.parseInt(t.getText()));
                                    currPlayerTransactions.add(tran);
                                    drawTransactions();
                                }

                            }
                            //If it does exist, then we go through the transactions and look for the money transaction
                            //and replace the old value with the new value
                            else {
                                if ((Integer.parseInt(t.getText()) <= p.getMoney())) {
                                    for (int x = 0; x < currPlayerTransactions.size(); x++) {
                                        if (currPlayerTransactions.get(x).getType().equals("Money"))
                                            if (!(Integer.parseInt(t.getText()) > p.getMoney()))
                                                currPlayerTransactions.get(x).setMoney(Integer.parseInt(t.getText()));
                                    }
                                    drawTransactions();
                                }
                            }
                        } else {
                            if (!jailInTransactions(oppTransactions, "Money")) {
                                if ((Integer.parseInt(t.getText()) <= opp.getMoney())) {
                                    Transaction tran = new Transaction("Money");
                                    tran.setMoney(Integer.parseInt(t.getText()));
                                    oppTransactions.add(tran);
                                    drawTransactions();
                                }
                            } else {
                                if ((Integer.parseInt(t.getText()) <= opp.getMoney())) {
                                    for (int x = 0; x < oppTransactions.size(); x++) {
                                        if (oppTransactions.get(x).getType().equals("Money"))
                                            oppTransactions.get(x).setMoney(Integer.parseInt(t.getText()));
                                    }
                                    drawTransactions();
                                }
                            }
                        }
                    }
                });
                tradeRoot.getChildren().add(getMon);
                tradeRoot.getChildren().add(t);
            }
        }
        drawCards();
    }

    //This method draws the community chest and chance cards that the player currently has to offer
    public void drawCards() {
        for (int i = 0; i < 2; i++) {
            if (!p.getJailFree(i).isEmpty()) {
                if (p.getJailFree(i).equals("Community Chest") && !jailInTransactions(currPlayerTransactions, "Community Chest")) {
                    ImageView card = makeImg("communityFront.jpg", 2 + (105 * i), 410, 100, 60);
                    tradeRoot.getChildren().add(card);
                    Button c = makeBtn("Choose", 22 + (105 * i), 475, 60, 30);
                    c.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            removeJailCard(p, "Community Chest", card, c);
                        }
                    });
                    tradeRoot.getChildren().add(c);
                } else if (p.getJailFree(i).equals("Chance") && !jailInTransactions(currPlayerTransactions, "Chance")) {
                    ImageView card = makeImg("chanceFront.jpg", 2 + (105 * i), 410, 100, 60);
                    tradeRoot.getChildren().add(card);
                    Button c = makeBtn("Choose", 22 + (105 * i), 475, 60, 30);
                    c.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            removeJailCard(p, "Chance", card, c);
                        }
                    });
                    tradeRoot.getChildren().add(c);
                }
            }

            if (!opp.getJailFree(i).isEmpty()) {
                if (opp.getJailFree(i).equals("Community Chest") && !jailInTransactions(oppTransactions, "Community Chest")) {
                    ImageView card = makeImg("communityFront.jpg", 1040 + (105 * i), 410, 100, 60);
                    tradeRoot.getChildren().add(card);
                    Button c = makeBtn("Choose", 1060 + (105 * i), 475, 60, 30);
                    c.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            removeJailCard(opp, "Community Chest", card, c);
                        }
                    });
                    tradeRoot.getChildren().add(c);
                } else if (opp.getJailFree(i).equals("Chance") && !jailInTransactions(oppTransactions, "Chance")) {
                    ImageView card = makeImg("chanceFront.jpg", 1040 + (105 * i), 410, 100, 60);
                    tradeRoot.getChildren().add(card);
                    Button c = makeBtn("Choose", 1060 + (105 * i), 475, 60, 30);
                    c.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            removeJailCard(opp, "Chance", card, c);
                        }
                    });
                    tradeRoot.getChildren().add(c);
                }
            }
        }
    }

    //When a player chooses to offer one of their Get Out of Jail cards for trade, this method removes the offered card
    //from the player's belongings and draws it to the transaction screen
    public void removeJailCard(Player player, String type, ImageView c, Button b) {
        Transaction t = new Transaction("Jail");
        t.setJailType(type);
        if (p.getName().equals(player.getName())) {
            currPlayerTransactions.add(t);
        } else {
            oppTransactions.add(t);
        }
        tradeRoot.getChildren().remove(c);
        tradeRoot.getChildren().remove(b);
        drawTransactions();
    }

    //This method checks if a specific type of trade, specifically one of money and a get out of jail free card, has
    //already been offered up
    public boolean jailInTransactions(ArrayList<Transaction> t, String type) {
        for (int i = 0; i < t.size(); i++) {
            if (type.equals("Money")) {
                if (t.get(i).getMoney() > 0) return true;
            } else if (t.get(i).getJailType().equals(type)) return true;
        }
        return false;
    }

    public SequentialTransition finishTrade(Button b) {
        if (acceptTurn == 0) {
            acceptTurn++;
            tradeRoot.getChildren().remove(b);
            return null;
        } else {
            tradeRoot.getChildren().remove(rect1);
            tradeRoot.getChildren().remove(rect2);
            rect1 = makeRect(210, 200, 300, 430, Color.LIGHTBLUE);
            rect1.setStroke(Color.BLACK);
            tradeRoot.getChildren().add(rect1);
            rect2 = makeRect(730, 200, 300, 430, Color.LIGHTBLUE);
            rect2.setStroke(Color.BLACK);
            tradeRoot.getChildren().add(rect2);
            ColorAdjust c = new ColorAdjust(0, -0.5, -0.7, 0);
            GaussianBlur g = new GaussianBlur();
            c.setInput(g);
            rect1.setEffect(c);
            rect2.setEffect(c);
            tradeRoot.getChildren().remove(b);
            SequentialTransition s = new SequentialTransition();
            swapBelongings(p, opp, currPlayerTransactions, 300, 500, 300, 800, 510, s);
            return s;
        }
    }

    public void swapBelongings(Player p, Player opp, ArrayList<Transaction> transactions, int imgX, int imgToX, int textX, int textEndX, int endX, SequentialTransition s) {
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getType().equals("Property")) {
                ImageView img = makeImg(transactions.get(i).getProp().getImg(), imgX, 250, 150, 175);
                FadeTransition f = makeFade(1.0, 0.0, 1.0, 1);
                f.setNode(img);
                s.getChildren().add(f);
                TranslateTransition t = makeTrans(1.5, imgToX, 0, 1);
                t.setNode(img);
                s.getChildren().add(t);
                f = makeFade(1.0, 1.0, 0.0, 1);
                f.setNode(img);
                s.getChildren().add(f);
                tradeRoot.getChildren().add(img);
                /*p.removeProperty(transactions.get(i).getProp());
                opp.addProperty(transactions.get(i).getProp());
                transactions.get(i).getProp().setOwner(opp);
                transactions.get(i).getProp().setOwned(true);*/
            } else if (transactions.get(i).getType().equals("Jail")) {
                ImageView img;
                if (transactions.get(i).getJailType().equals("Community Chest"))
                    img = makeImg("communityFront.jpg", imgX, 250, 150, 100);
                else img = makeImg("chanceFront.jpg", imgX, 250, 150, 100);
                FadeTransition f = makeFade(1.0, 0.0, 1.0, 1);
                f.setNode(img);
                s.getChildren().add(f);
                TranslateTransition t = makeTrans(1.5, imgToX, 0, 1);
                t.setNode(img);
                s.getChildren().add(t);
                f = makeFade(1.0, 1.0, 0.0, 1);
                f.setNode(img);
                s.getChildren().add(f);
                tradeRoot.getChildren().add(img);
                p.setJailFree("");
                opp.setJailFree(transactions.get(i).getJailType());
            } else {
                String mon;
                mon = "$" + p.getMoney();
                Text currMon = makeText(mon, textX, 350, Font.font("Bodoni MT", 30), Color.WHITE);
                FadeTransition f = makeFade(1.0, 0.0, 1.0, 1);
                f.setNode(currMon);
                s.getChildren().add(f);
                tradeRoot.getChildren().add(currMon);
                if (type.equals("bankruptcy")) mon = "$" + opp.getDebt() + " (Debt)";
                else mon = "$" + opp.getMoney();
                Text oppCurrMon = makeText(mon, textEndX, 350, Font.font("Bodoni MT", 30), Color.WHITE);
                f = makeFade(1.0, 0.0, 1.0, 1);
                f.setNode(oppCurrMon);
                s.getChildren().add(f);
                tradeRoot.getChildren().add(oppCurrMon);
                String deduc = "-$" + transactions.get(i).getMoney();
                Text deduction = makeText(deduc, textX - 10, 320, Font.font("Bodoni MT", 30), Color.WHITE);
                f = makeFade(1.0, 0.0, 1.0, 1);
                f.setNode(deduction);
                s.getChildren().add(f);
                tradeRoot.getChildren().add(deduction);
                Text plus;
                if (type.equals("bankruptcy"))
                    plus = makeText("-", textEndX - 10, 320, Font.font("Bodoni MT", 30), Color.WHITE);
                else plus = makeText("+", textEndX - 10, 320, Font.font("Bodoni MT", 30), Color.WHITE);
                f = makeFade(1.0, 0.0, 1.0, 1);
                f.setNode(plus);
                s.getChildren().add(f);
                tradeRoot.getChildren().add(plus);
                mon = "$" + transactions.get(i).getMoney();
                Text oppAddition = makeText(mon, textX, 320, Font.font("Bodoni MT", 30), Color.WHITE);
                TranslateTransition t = makeTrans(1.5, endX, 0, 1);
                t.setNode(oppAddition);
                s.getChildren().add(t);
                tradeRoot.getChildren().add(oppAddition);
                Line l1 = new Line();
                l1.setStartX(textX);
                l1.setStartY(365);
                l1.setEndX(textX + 120);
                l1.setEndY(365);
                l1.setStroke(Color.WHITE);
                f = makeFade(1.0, 0.0, 1.0, 1);
                f.setNode(l1);
                s.getChildren().add(f);
                tradeRoot.getChildren().add(l1);
                Line l2 = new Line();
                l2.setStartX(textEndX - 10);
                l2.setStartY(365);
                l2.setEndX(textEndX + 120);
                l2.setEndY(365);
                l2.setStroke(Color.WHITE);
                f = makeFade(1.0, 0.0, 1.0, 1);
                f.setNode(l2);
                s.getChildren().add(f);
                tradeRoot.getChildren().add(l2);
                p.setMoney(-1 * transactions.get(i).getMoney());
                if (opp.getDebt() != 0) {
                    if (transactions.get(i).getMoney() > opp.getDebt()) {
                        opp.setMoney(transactions.get(i).getMoney() - opp.getDebt());
                        opp.setDebt(0);
                    } else opp.setDebt(opp.getDebt() - transactions.get(i).getMoney());
                } else opp.setMoney(transactions.get(i).getMoney());
                mon = "$" + p.getMoney();
                Text newPlayerMon = makeText(mon, textX, 395, Font.font("Bodoni MT", 30), Color.WHITE);
                f = makeFade(1.0, 0.0, 1.0, 1);
                f.setNode(newPlayerMon);
                s.getChildren().add(f);
                tradeRoot.getChildren().add(newPlayerMon);
                if (opp.getDebt() != 0) mon = "$" + opp.getDebt() + " (Debt)";
                else mon = "$" + opp.getMoney();
                Text newOpMoney = makeText(mon, textEndX - 10, 395, Font.font("Bodoni MT", 30), Color.WHITE);
                f = makeFade(1.0, 0.0, 1.0, 1);
                f.setNode(newOpMoney);
                s.getChildren().add(f);
                tradeRoot.getChildren().add(newOpMoney);
                //ParallelTransition parallel = new ParallelTransition();
                f = makeFade(0.1, 1.0, 0.0, 1);
                f.setNode(currMon);
                s.getChildren().add(f);
                f = makeFade(0.1, 1.0, 0.0, 1);
                f.setNode(oppCurrMon);
                s.getChildren().add(f);
                f = makeFade(0.1, 1.0, 0.0, 1);
                f.setNode(deduction);
                s.getChildren().add(f);
                f = makeFade(0.1, 1.0, 0.0, 1);
                f.setNode(plus);
                s.getChildren().add(f);
                f = makeFade(0.1, 1.0, 0.0, 1);
                f.setNode(oppAddition);
                s.getChildren().add(f);
                f = makeFade(0.1, 1.0, 0.0, 1);
                f.setNode(l1);
                s.getChildren().add(f);
                f = makeFade(0.1, 1.0, 0.0, 1);
                f.setNode(l2);
                s.getChildren().add(f);
                f = makeFade(0.1, 1.0, 0.0, 1);
                f.setNode(newPlayerMon);
                s.getChildren().add(f);
                f = makeFade(0.1, 1.0, 0.0, 1);
                f.setNode(newOpMoney);
                s.getChildren().add(f);
                //s.getChildren().add(parallel);
            }
        }
    }

    public FadeTransition makeFade(double seconds, double fromVal, double toVal, int cycles) {
        FadeTransition f = new FadeTransition();
        f.setDuration(Duration.seconds(seconds));
        f.setFromValue(fromVal);
        f.setToValue(toVal);
        f.setCycleCount(cycles);
        return f;
    }

    public TranslateTransition makeTrans(double seconds, int toX, int toY, int cycles) {
        TranslateTransition t = new TranslateTransition();
        t.setDuration(Duration.seconds(seconds));
        t.setToX(toX);
        t.setToY(toY);
        t.setCycleCount(cycles);
        return t;
    }

    public ImageView makeImg(String url, int x, int y, int w, int h) {
        ImageView img = new ImageView(new Image(url));
        img.setX(x);
        img.setY(y);
        img.setFitWidth(w);
        img.setFitHeight(h);
        return img;
    }

    public Button makeBtn(String text, int x, int y, int w, int h) {
        Button b = new Button(text);
        b.setLayoutX(x);
        b.setLayoutY(y);
        b.setPrefSize(w, h);
        return b;
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

    public Text makeText(String text, int x, int y, Font f, Color c) {
        Text t = new Text(text);
        t.setX(x);
        t.setY(y);
        t.setFont(f);
        t.setFill(c);
        return t;
    }
}
