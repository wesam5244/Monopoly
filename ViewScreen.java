import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;

/*
This screen displays everything that a player owns
 */
public class ViewScreen {
    private Group viewRoot = new Group();
    private Player p;
    private String[] colors = {"Purple", "White", "Light Blue", "Pink", "Utility", "Orange", "Red", "Yellow", "Green", "Dark Blue"}; //Color sets of the properties
    private Rectangle propRect = makeRect(2, 145, 780, 640 - 140, Color.LIGHTSKYBLUE);
    private String mode;
    private ArrayList<Transaction> transactions;
    private int numProps;
    private String type = "";

    public ViewScreen(Player p, String mode, ArrayList<Transaction> t) {
        this.p = p;
        this.mode = mode;
        this.transactions = t;
    }

    public String getType() {
        return type;
    }

    //Draws the actual screen itself
    public Group drawScreen() {
        //If the current player is in debt, there are going to be a few changes to the screen
        if (p.getDebt() > 0) type = "bankruptcy";
        ImageView background = makeImg("secondBack.jpg", 0, 0, 1250, 650);
        viewRoot.getChildren().add(background);
        drawPlayerRect();
        if (mode.equals("regular")) {
            Text title = makeText("Your Stats", 600, 50, Font.font("Verdana", 35), Color.BLACK);
            viewRoot.getChildren().add(title);
        } else {
            Text title = makeText("Your Properties", 590, 50, Font.font("Verdana", 35), Color.BLACK);
            viewRoot.getChildren().add(title);
        }
        Text propertiesTitle = makeText("Properties (Cards with background belong to a complete set)", 5, 140, Font.font("Tw Cen MT", 30), Color.BLACK);
        viewRoot.getChildren().add(propertiesTitle);
        drawProperties(0, null);
        if (mode.equals("regular")) {
            showTransactions();
            showJailCards();
        } else {
            tradeTransactions();
        }
        return viewRoot;
    }

    //This method displays all of the properties that the player is currently offering up for trade on the right side of
    //the screen
    public void tradeTransactions() {
        Text tText = makeText("Properties You've Chosen to Trade", 800, 140, Font.font("Tw Cen MT", 30), Color.BLACK);
        viewRoot.getChildren().add(tText);
        Rectangle tRect = makeRect(800, 170, 400, 420, Color.WHEAT);
        tRect.setStroke(Color.BLACK);
        viewRoot.getChildren().add(tRect);
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getType().equals("Property")) {
                String text = (Integer.toString(i + 1) + ". " + transactions.get(i).getProp().getName());
                Text propText = makeText(text, 810, (200 + ((numProps) * 30)), Font.font("Verdana", 20), Color.BLACK);
                ImageView cancelIcon = makeImg("cancelIcon.png", 900, (int) propText.getY() - 20, 25, 25);
                Rectangle rect = makeRect((int) cancelIcon.getX(), (int) cancelIcon.getY(), 25, 25, Color.TRANSPARENT);
                int finalI = i;
                rect.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        transactions.remove(transactions.get(finalI));
                        numProps = 0;
                        tradeTransactions();
                    }
                });
                viewRoot.getChildren().add(propText);
                viewRoot.getChildren().add(cancelIcon);
                viewRoot.getChildren().add(rect);
                numProps++;
            }
        }
    }

    public void drawPlayerRect() {
        Rectangle rect = makeRect(0, 0, 200, 100, Color.LIGHTBLUE);
        viewRoot.getChildren().add(rect);
        ImageView playerPiece = makeImg(p.getIMG(), 50, 50, 100, 50);
        viewRoot.getChildren().add(playerPiece);
        String mon;
        if (p.getDebt() > 0) mon = p.getName() + ": $" + Integer.toString(p.getDebt()) + "(Debt)";
        else mon = p.getName() + ": $" + Integer.toString(p.getMoney());
        Text nameText = makeText(mon, 0, 20, Font.font("Comic Sans MS", 18), Color.BLACK);
        viewRoot.getChildren().add(nameText);
    }

    //This method displays all of the properties that you own on the screen
    public void drawProperties(int choice, Button b) {
        HashMap<String, ArrayList<Property>> props = p.getProperties();
        int curr = 0;
        //This is where the colors Array comes into play. The way that the HashMap is set up so that each color set
        //has an ArrayList associated to it that contains all of the properties of that color set that the player owns.
        //The color Array contains all of the color sets, so we go through the color sets and extract all the ArrayLists for those colors
        for (int i = 0; i < colors.length; i++) {
            //Check if the ArrayList for that color set is not empty
            if (props.get(colors[i]).size() > 0) {
                //Go through the ArrayList and draw each property in it
                for (int x = 0; x < props.get(colors[i]).size(); x++) {
                    ImageView propImg;
                    if (props.get(colors[i]).get(x).isMortgaged())
                        propImg = makeImg(props.get(colors[i]).get(x).getMortgageImg(), 5 + (110 * (curr % 7)), 150 + (120 * (curr / 7)), 100, 110);
                    else
                        propImg = makeImg(props.get(colors[i]).get(x).getImg(), 5 + (110 * (curr % 7)), 150 + (120 * (curr / 7)), 100, 110);
                    //If you own all of the properties of that color set, a rectangle of the same color as the color set
                    //is drawn behind the property to show that all of the properties of that color set are owned. Since
                    //we have already checked if the ArrayList is not empty, there must be at least one property in the
                    //ArrayList, so we can check the NumColors variable of that first property which contains the total
                    //number of colors in that color set, and then check if the ArrayList's size is equal to the total
                    //number of colors in that set found through the numColors variable
                    if (props.get(colors[i]).get(0).getNumColors() == props.get(colors[i]).size()) {
                        Rectangle propRect;
                        if (colors[i].equals("Purple"))
                            propRect = makeRect((int) propImg.getX() - 4, (int) propImg.getY() - 4, (int) propImg.getFitWidth() + 8, (int) propImg.getFitHeight() + 8, Color.rgb(86, 13, 59));
                        else if (colors[i].equals("White"))
                            propRect = makeRect((int) propImg.getX() - 4, (int) propImg.getY() - 4, (int) propImg.getFitWidth() + 8, (int) propImg.getFitHeight() + 8, Color.WHITE);
                        else if (colors[i].equals("Light Blue"))
                            propRect = makeRect((int) propImg.getX() - 4, (int) propImg.getY() - 4, (int) propImg.getFitWidth() + 8, (int) propImg.getFitHeight() + 8, Color.rgb(134, 164, 214));
                        else if (colors[i].equals("Pink"))
                            propRect = makeRect((int) propImg.getX() - 4, (int) propImg.getY() - 4, (int) propImg.getFitWidth() + 8, (int) propImg.getFitHeight() + 8, Color.rgb(240, 55, 122));
                        else if (colors[i].equals("Utility"))
                            propRect = makeRect((int) propImg.getX() - 4, (int) propImg.getY() - 4, (int) propImg.getFitWidth() + 8, (int) propImg.getFitHeight() + 8, Color.BLACK);
                        else if (colors[i].equals("Orange"))
                            propRect = makeRect((int) propImg.getX() - 4, (int) propImg.getY() - 4, (int) propImg.getFitWidth() + 8, (int) propImg.getFitHeight() + 8, Color.rgb(246, 127, 35));
                        else if (colors[i].equals("Red"))
                            propRect = makeRect((int) propImg.getX() - 4, (int) propImg.getY() - 4, (int) propImg.getFitWidth() + 8, (int) propImg.getFitHeight() + 8, Color.rgb(213, 0, 0));
                        else if (colors[i].equals("Yellow"))
                            propRect = makeRect((int) propImg.getX() - 4, (int) propImg.getY() - 4, (int) propImg.getFitWidth() + 8, (int) propImg.getFitHeight() + 8, Color.rgb(255, 204, 1));
                        else if (colors[i].equals("Green"))
                            propRect = makeRect((int) propImg.getX() - 4, (int) propImg.getY() - 4, (int) propImg.getFitWidth() + 8, (int) propImg.getFitHeight() + 8, Color.rgb(8, 134, 50));
                        else
                            propRect = makeRect((int) propImg.getX() - 4, (int) propImg.getY() - 4, (int) propImg.getFitWidth() + 8, (int) propImg.getFitHeight() + 8, Color.rgb(40, 78, 159));
                        viewRoot.getChildren().add(propRect);
                    }
                    int finalI = i;
                    int finalX = x;
                    //If the property is clicked, the property's stats are shown
                    propImg.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            displayProp(props.get(colors[finalI]).get(finalX));
                        }
                    });
                    viewRoot.getChildren().add(propImg);
                    curr++;
                }
            }
        }
        if (choice == 1) viewRoot.getChildren().remove(b);
    }

    //When a property is clicked, this method is invoked which shows the property itself
    public void displayProp(Property prop) {
        //The huge rectangle on which everything will be displayed
        propRect.setStroke(Color.BLACK);
        propRect.setStrokeWidth(2.0);
        viewRoot.getChildren().add(propRect);
        ImageView propImg;
        if (prop.isMortgaged()) {
            propImg = makeImg(prop.getMortgageImg(), 10, 200, 300, 410);
        } else {
            propImg = makeImg(prop.getImg(), 10, 200, 300, 410);
        }
        viewRoot.getChildren().add(propImg);
        Text propName = makeText(prop.getName(), 320, 230, Font.font("Verdana", 35), Color.BLACK);
        propName.setUnderline(true);
        viewRoot.getChildren().add(propName);
        Text rent;
        //For utilities, the rent is not a set amount, but rather a multiplication of the number rolled on the dice
        if (prop.getColor().equals("Utility"))
            rent = makeText("Rent: " + Integer.toString(prop.getRent()) + " times the roll", 320, 270, Font.font("Ebrima", 25), Color.BLACK);
        else
            rent = makeText("Rent: $" + Integer.toString(prop.getRent()), 320, 270, Font.font("Ebrima", 25), Color.BLACK);
        viewRoot.getChildren().add(rent);
        if (!prop.getColor().equals("Utility") && !prop.getColor().equals("White")) {
            Text houses = makeText("Houses: " + Integer.toString(prop.getHouses()), 320, 310, Font.font("Ebrima", 25), Color.BLACK);
            viewRoot.getChildren().add(houses);
            Text hotels = makeText("Hotels: " + Integer.toString(prop.getHotels()), 320, 350, Font.font("Ebrima", 25), Color.BLACK);
            viewRoot.getChildren().add(hotels);
            ArrayList<Property> props = p.getProperties().get(prop.getColor());
            Text ownProps = makeText("Properties of this Color Set you Own: " + props.size() + "/" + prop.getNumColors(), 320, 390, Font.font("Ebrima", 25), Color.BLACK);
            viewRoot.getChildren().add(ownProps);
            Text monGained = makeText("Money Gained: $" + prop.getMonGained(), 320, 430, Font.font("Ebrima", 25), Color.BLACK);
            viewRoot.getChildren().add(monGained);
            Text monSpent = makeText("Money Spent: $" + prop.getMonSpent(), 320, 470, Font.font("Ebrima", 25), Color.BLACK);
            viewRoot.getChildren().add(monSpent);
            Text mortgageVal;
            //If the property is mortgaged, you can give them the option to life the mortgage
            if (prop.isMortgaged())
                mortgageVal = makeText("Price to Lift Mortgage: $" + (int) (prop.getMortgageVal() * 1.10), 320, 510, Font.font("Ebrima", 25), Color.BLACK);
            else
                mortgageVal = makeText("Mortgage Value: $" + prop.getMortgageVal(), 320, 510, Font.font("Ebrima", 25), Color.BLACK);
            viewRoot.getChildren().add(mortgageVal);
            Button addTransaction = makeBtn("Add to Trade", 380, 550, 100, 40);
            //If we're in the mode of trading, you can add the current property to your trade block. Basically when we're
            //in the trade screen, there is a button to add properties, and that button calls this class which shows
            //particular options for the trading mode
            if (!mode.equals("regular")) {
                if (!inTransactions(prop) && prop.getHouses() == 0) {
                    addTransaction.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            addTransaction(prop, addTransaction);
                        }
                    });
                    viewRoot.getChildren().add(addTransaction);
                }
            }
            Button remove = makeBtn("Return", 505, 550, 100, 40);
            viewRoot.getChildren().add(remove);
            Button mortgage = makeBtn("Mortgage", 400, 550, 100, 40);
            //If the player isn't currently trading, then they can mortgage the property if it is not currently mortgaged,
            //and if the property is mortgaged, then they can lift the mortgage
            if (prop.getHouses() == 0 && !mode.equals("trade")) {
                if (prop.isMortgaged()) mortgage = makeBtn("Lift Mortgage", 400, 550, 100, 40);
                else mortgage = makeBtn("Mortgage", 400, 550, 100, 40);
                Button finalMortgage1 = mortgage;
                mortgage.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        viewRoot.getChildren().removeAll(propRect, mortgageVal, propImg, propName, rent, houses, hotels, ownProps, monGained, monSpent, remove);
                        mortgageProp(prop, finalMortgage1);
                    }
                });
                viewRoot.getChildren().add(mortgage);
            }
            Button finalMortgage = mortgage;
            remove.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (prop.getHouses() == 0)
                        viewRoot.getChildren().removeAll(propRect, mortgageVal, addTransaction, finalMortgage, propImg, propName, rent, houses, hotels, ownProps, monGained, monSpent, remove);
                    else
                        viewRoot.getChildren().removeAll(propRect, mortgageVal, addTransaction, propImg, propName, rent, houses, hotels, ownProps, monGained, monSpent, remove);
                    drawProperties(1, remove);
                }
            });
        } else {
            ArrayList<Property> props = p.getProperties().get(prop.getColor());
            Text ownProps = makeText("Properties of this Color Set you Own: " + props.size() + "/" + prop.getNumColors(), 320, 310, Font.font("Ebrima", 25), Color.BLACK);
            viewRoot.getChildren().add(ownProps);
            Text monGained = makeText("Money Gained: $" + prop.getMonGained(), 320, 350, Font.font("Ebrima", 25), Color.BLACK);
            viewRoot.getChildren().add(monGained);
            Text monSpent = makeText("Money Spent: $" + prop.getMonSpent(), 320, 390, Font.font("Ebrima", 25), Color.BLACK);
            viewRoot.getChildren().add(monSpent);
            Button remove = makeBtn("Return", 505, 550, 100, 40);
            Button addTransaction = makeBtn("Add to Trade", 380, 550, 100, 40);
            if (!mode.equals("regular")) {
                if (!inTransactions(prop) && prop.getHouses() == 0) {
                    addTransaction.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            addTransaction(prop, addTransaction);
                        }
                    });
                    viewRoot.getChildren().add(addTransaction);
                }
            }
            remove.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    viewRoot.getChildren().removeAll(propRect, propImg, addTransaction, propName, rent, ownProps, monGained, monSpent, remove);
                    drawProperties(1, remove);
                }
            });
            viewRoot.getChildren().add(remove);
        }
    }

    //This method is invoked when a player wants to mortgage a property
    public void mortgageProp(Property prop, Button b) {
        //If the property is mortaged, lift it
        if (prop.isMortgaged()) {
            prop.setMortgaged(false);
            p.setMoney(-1 * (int) (prop.getMortgageVal() * 1.10));
            p.addTransaction("Lifted mortgage off of " + prop.getName() + ": -$" + ((int) (prop.getMortgageVal() * 1.10)) + "\n\tTotal: $" + (p.getMoney() - ((int) (prop.getMortgageVal() * 1.10))));
        } else {
            prop.setMortgaged(true);
            //If the current player is in say, debt of $30, and the mortgage value is $40, then the debt is cleared
            //and the amount of money they have is set to $10 (this nested if statement inside of the below if statement)f
            if (p.getDebt() > 0) {
                if (prop.getMortgageVal() > p.getDebt()) {
                    p.setMoney(prop.getMortgageVal() - p.getDebt());
                    p.setDebt(0);
                } else p.setDebt(p.getDebt() - prop.getMortgageVal());
            } else p.setMoney(prop.getMortgageVal());
            p.addTransaction("Mortgaged " + prop.getName() + ": +$" + prop.getMortgageVal() + "\n\tTotal: $" + (p.getMoney() + prop.getMortgageVal()));
        }
        viewRoot.getChildren().remove(b);
        drawPlayerRect();
        drawProperties(0, null);
    }

    public boolean inTransactions(Property p) {
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getType().equals("Property") && transactions.get(i).getProp().getName().equals(p.getName()))
                return true;
        }
        return false;
    }

    //This property adds a proeprty to the list of transactions that each player offers
    public void addTransaction(Property prop, Button b) {
        //Create a new Transaction object
        Transaction t = new Transaction("Property");
        t.setProp(prop);
        transactions.add(t);
        String text = (Integer.toString(numProps + 1) + ". " + prop.getName());
        Text propText = makeText(text, 810, (200 + ((numProps) * 30)), Font.font("Verdana", 20), Color.BLACK);
        ImageView cancelIcon = makeImg("cancelIcon.png", 1150, (int) propText.getY() - 20, 25, 25);
        Rectangle rect = makeRect((int) cancelIcon.getX(), (int) cancelIcon.getY(), 25, 25, Color.TRANSPARENT);
        //If the player clicks the cancel icon, that transaction is removed
        rect.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                transactions.remove(t);
                //numProps is the number of properties that are up for trade. We set it to 0 so that it becomes easy to
                //draw them again
                numProps = 0;
                tradeTransactions();
            }
        });
        viewRoot.getChildren().add(propText);
        viewRoot.getChildren().add(cancelIcon);
        viewRoot.getChildren().add(rect);
        viewRoot.getChildren().remove(b);
        numProps++;
    }

    public void showJailCards() {
        Text transactionText = makeText("Get Out of Jail Free Cards", 870, 140, Font.font("Tw Cen MT", 30), Color.BLACK);
        transactionText.setUnderline(true);
        viewRoot.getChildren().add(transactionText);
        for (int i = 0; i < 2; i++) {
            if (!p.getJailFree(i).isEmpty()) {
                if (p.getJailFree(i).equals("Community Chest")) {
                    ImageView card = makeImg("communityFront.jpg", 785 + (230 * i), 170, 220, 130);
                    viewRoot.getChildren().add(card);
                } else if (p.getJailFree(i).equals("Chance")) {
                    ImageView card = makeImg("chanceFront.jpg", 785 + (230 * i), 170, 220, 130);
                    viewRoot.getChildren().add(card);
                }
            }
        }
    }

    public void showTransactions() {
        Text transactionText = makeText("Transaction History", 920, 360, Font.font("Tw Cen MT", 30), Color.BLACK);
        transactionText.setUnderline(true);
        viewRoot.getChildren().add(transactionText);
        FlowPane list = new FlowPane();
        ScrollPane sp = new ScrollPane();
        sp.setLayoutX(800);
        sp.setLayoutY(370);
        sp.setPrefSize(440, 220);
        ArrayList<String> transactions = p.getTransactions();
        int curr = 1;
        for (int i = transactions.size() - 1; i >= 0; i--) {
            String t = curr + ". " + transactions.get(i) + "\n";
            Text text = new Text(t);
            text.setFont(Font.font("Verdana", 16));
            text.setFill(Color.BLACK);
            list.getChildren().add(text);
            curr++;
        }
        sp.setContent(list);
        viewRoot.getChildren().add(sp);
    }

    //When the player clicks the "View another player's stats" button, this method is invoked where they can choose
    //whose stats they would like to see next
    public void switchStats(Player[] players) {
        ImageView background = makeImg("secondBack.jpg", 0, 0, 1250, 650);
        viewRoot.getChildren().add(background);
        Text chooseText = makeText("Whose stats would you like to see?", 400, 250, Font.font("Tw Cen MT", 40), Color.BLACK);
        viewRoot.getChildren().add(chooseText);
        for (int i = 0; i < players.length; i++) {
            Rectangle rect = makeRect(200 + (210 * i), 300, 200, 100, Color.LIGHTBLUE);
            viewRoot.getChildren().add(rect);
            ImageView imgView = makeImg(players[i].getIMG(), 250 + (210 * i), 350, 100, 50);
            viewRoot.getChildren().add(imgView);
            Text nameText;
            if (players[i].getName().equals(p.getName()))
                nameText = makeText(("You: $" + Integer.toString(players[i].getMoney())), 200 + (210 * i), 320, Font.font("Comic Sans MS", 18), Color.BLACK);
            else
                nameText = makeText((players[i].getName() + ": $" + Integer.toString(players[i].getMoney())), 200 + (210 * i), 320, Font.font("Comic Sans MS", 18), Color.BLACK);
            viewRoot.getChildren().add(nameText);
        }
    }

    public Button makeBtn(String text, int x, int y, int w, int h) {
        Button b = new Button(text);
        b.setLayoutX(x);
        b.setLayoutY(y);
        b.setPrefSize(w, h);
        return b;
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
        ImageView i = new ImageView(new Image(url));
        i.setX(x);
        i.setY(y);
        i.setFitWidth(width);
        i.setFitHeight(height);
        return i;
    }
}
