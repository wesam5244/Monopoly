import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
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
import java.util.HashMap;

/*
This screen is for building houses on one's properties
 */
public class HouseScreen {
    private Player p;
    private Board b;
    private String[] colors = {"Purple", "White", "Light Blue", "Pink", "Utility", "Orange", "Red", "Yellow", "Green", "Dark Blue"};
    private Group houseRoot;
    private Text denyBuildText, denyRemoveText, showText, houseNum;
    private Button confirm, cancel, mortgageBtn;
    private Text newRentText = new Text();
    private Text priceText = new Text();
    private Rectangle houseRect, propertyBack;
    private ImageView board;
    //A HashMap that essentially associates each property with an ArrayList that contains images of all of the houses
    //that have built on that property
    private HashMap<Property, ArrayList<ImageView>> houses = new HashMap<>();
    private boolean transaction = false;
    private String type = "";

    public HouseScreen(Player p, Board b) {
        this.p = p;
        this.b = b;
        houseRoot = new Group();
        String t = p.getName() + ", you can't build any more\nhouses on this property based\n on the even building rule";
        showText = makeText("Any properties that you\npick will show up here", 25, 325, Font.font("Perpetua", 35), Color.BLACK);
        denyBuildText = makeText(t, 20, 585, Font.font("MS Gothic", 20), Color.BLACK);
        t = p.getName() + ", you can't remove any more\nhouses on this property based\n on the even building rule";
        denyRemoveText = makeText(t, 20, 585, Font.font("MS Gothic", 20), Color.BLACK);
        confirm = new Button("Confirm Purchase");
        confirm.setPrefSize(150, 40);
        confirm.setLayoutX(12);
        confirm.setLayoutY(580);
        cancel = new Button("Cancel Purchase");
        cancel.setPrefSize(150, 40);
        cancel.setLayoutX(192);
        cancel.setLayoutY(580);
        mortgageBtn = new Button("View Mortgage");
        mortgageBtn.setPrefSize(150, 40);
        mortgageBtn.setLayoutX(100);
        mortgageBtn.setLayoutY(530);
        //In this for loop, we go through all the color sets and, if for that color set there is more than one property
        //that the player owns, then we go through each of the properties that he owns for that color set and creates
        //a new ArrayList for the images of the houses of these properties
        for (int i = 0; i < colors.length; i++) {
            if (p.getProperties().get(colors[i]).size() > 0) {
                for (int x = 0; x < p.getProperties().get(colors[i]).size(); x++) {
                    houses.put(p.getProperties().get(colors[i]).get(x), new ArrayList<>());
                }
            }
        }
    }

    public String getType() {
        return type;
    }

    //This method draws the screen for building the houses
    public Group showScreen() {
        if (p.getDebt() != 0) type = "bankruptcy";
        ImageView background = makeImg("firstback.jpg", 0, 0, 1250, 650);
        houseRoot.getChildren().add(background);
        Text titleText = makeText("Click on one of the flashing properties below\nto choose what to do next", 360, 30, Font.font("Verdana", 27), Color.BLACK);
        houseRoot.getChildren().add(titleText);
        drawPlayerRect();
        //The Rectangle on the left side of the screen that all of the property info is displayed on top of
        propertyBack = makeRect(5, 110, 340, 530, Color.GOLDENROD);
        propertyBack.setStrokeWidth(2.0);
        propertyBack.setStroke(Color.BLACK);
        houseRoot.getChildren().add(propertyBack);
        houseRoot.getChildren().add(showText);
        board = makeImg("monopolyBoard.jpg", 350, 70, 640, 580);
        houseRoot.getChildren().add(board);
        HashMap<String, ArrayList<Property>> props = p.getProperties();
        //In this for loop, we go through each of the player's properties and draw any current houses that the properties
        //already has on the screen
        for (int i = 0; i < colors.length; i++) {
            for (int x = 0; x < props.get(colors[i]).size(); x++) {
                if (props.get(colors[i]).get(x).getHouses() > 0) {
                    for (int z = 0; z < p.getHouse(props.get(colors[i]).get(x)).size(); z++) {
                        houseRoot.getChildren().add(p.getHouse(props.get(colors[i]).get(x)).get(z));
                    }
                }
            }
        }
        String evenRule = "Even Building Rule:\nYou can not build a house\non a property until all\nof the other properties\nof that color set have\nthe same number of houses";
        Text rule = makeText(evenRule, 1000, 90, Font.font("Perpetua", 25), Color.BLACK);
        houseRoot.getChildren().add(rule);
        drawPropertyRects();
        return houseRoot;
    }

    //This method draws the rectangle on the top left corner of the screen that shows the player's name, piece, and money
    public void drawPlayerRect() {
        Rectangle rect = makeRect(0, 0, 200, 100, Color.LIGHTBLUE);
        houseRoot.getChildren().add(rect);
        ImageView playerPiece = makeImg(p.getIMG(), 50, 50, 100, 50);
        houseRoot.getChildren().add(playerPiece);
        String mon;
        if (p.getDebt() != 0) mon = p.getName() + ": $" + Integer.toString(p.getDebt()) + " (Debt)";
        else mon = p.getName() + ": $" + Integer.toString(p.getMoney());
        Text nameText = makeText(mon, 0, 20, Font.font("Comic Sans MS", 18), Color.BLACK);
        houseRoot.getChildren().add(nameText);
    }

    //This method draws rectangles on top of all of the properties that the player owns
    public void drawPropertyRects() {
        for (int i = 0; i < colors.length; i++) {
            ArrayList<Property> properties = p.getProperties().get(colors[i]);
            if (properties.size() > 0) {
                for (int x = 0; x < properties.size(); x++) {
                    Rectangle rectCurr = b.getPlayerRect(p, properties.get(x).getPos());
                    FadeTransition rectFade = makeFade(1.0, 1.0, 0.0, Animation.INDEFINITE);
                    rectFade.setNode(rectCurr);
                    rectFade.play();
                    final int curr = x;
                    rectCurr.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            displayPropInfo(properties, curr);
                        }
                    });
                    houseRoot.getChildren().add(rectCurr);
                }
            }
        }
    }

    //When a property is selected, this method is called which displays all of the possible options with the current property
    public void displayPropInfo(ArrayList<Property> props, int curr) {
        Property currProp = props.get(curr);
        propertyBack = makeRect(5, 110, 340, 530, Color.GOLDENROD);
        propertyBack.setStrokeWidth(2.0);
        propertyBack.setStroke(Color.BLACK);
        houseRoot.getChildren().add(propertyBack);
        //If the property is mortgaged, the mortgage can be lifted by the user
        if (currProp.isMortgaged()) {
            ImageView propImg = makeImg(currProp.getMortgageImg(), 50, 120, 275, 325);
            houseRoot.getChildren().add(propImg);
            Text unmortgage = new Text();
            //Since utilities and "white" properties (railroads) can not have houses built upon them, a property that is
            //a part of this set will not have this below message displayed
            if (!currProp.getColor().equals("Utility") && !currProp.getColor().equals("White")) {
                unmortgage = makeText(("You can not build any houses on\nthis property until you lift the\nmortgage at a price of $" + (int) (props.get(curr).getMortgageVal() * 1.10)), 30, 490, Font.font("Ebrima", 20), Color.BLACK);
            }
            houseRoot.getChildren().add(unmortgage);
            Button cancel = new Button("Lift Mortgage");
            cancel.setPrefSize(150, 40);
            cancel.setLayoutX(100);
            cancel.setLayoutY(580);
            Node[] nodes = {propImg, unmortgage, cancel};
            cancel.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    setHousePrice(currProp, -1 * ((int) (currProp.getMortgageVal() * 1.10)), nodes);
                }
            });
            houseRoot.getChildren().add(cancel);
        }
        //The case for when the property is not mortgaged
        else {
            ImageView propImg = makeImg(currProp.getImg(), 50, 120, 250, 325);
            houseRoot.getChildren().add(propImg);
            //If the currently selected property has less than 4 houses OR has exactly 4 houses THAT ALREADY EXIST, then
            //the number of houses are shown. The reason why in the second case the number of houses are shown is because
            //if 4 houses already exist, the player has the two options of removing a house AND adding a hotel, so you
            //need to show the number of houses.
            Text houses;
            if (currProp.getHouses() < 4 || (currProp.getHouses() == 4 && p.getHouse(currProp).size() == 4))
                houses = makeText("Houses: ", 10, 490, Font.font("Ebrima", 20), Color.BLACK);
            else houses = makeText("Hotels: ", 10, 490, Font.font("Ebrima", 20), Color.BLACK);
            houseRoot.getChildren().add(houses);
            Rectangle backRect = makeRect(97, 465, 40, 40, Color.MIDNIGHTBLUE);
            ImageView backArrow = makeImg("menuBack.png", 90, 460, 50, 50);
            backArrow.setRotate(backArrow.getRotate() + 180);
            //If there is more than one house present, then you have the choice of removing the house
            if (currProp.getHouses() != 0) {
                houseRoot.getChildren().add(backRect);
                houseRoot.getChildren().add(backArrow);
            }
            Rectangle houseRect = makeRect(145, 465, 81, 40, Color.SNOW);
            houseRoot.getChildren().add(houseRect);
            Text houseNum;
            if (currProp.getHouses() < 4 || (currProp.getHouses() == 4 && p.getHouse(currProp).size() == 4))
                houseNum = makeText(Integer.toString(currProp.getHouses()), 180, 490, Font.font("Ebrima", 20), Color.BLACK);
            else
                houseNum = makeText(Integer.toString(currProp.getHotels()), 180, 490, Font.font("Ebrima", 20), Color.BLACK);
            houseRoot.getChildren().add(houseNum);
            //If the property has 4 houses on it, the only thing they can further build on it is a hotel
            if (currProp.getHouses() == 4 && p.getHouse(currProp).size() == 4 && p.getMoney() > currProp.getHotelPrice()) {
                Button hotelBtn = new Button("Buy Hotel");
                hotelBtn.setLayoutX(240);
                hotelBtn.setLayoutY(465);
                hotelBtn.setPrefSize(90, 40);
                Rectangle backOption = makeRect(97, 465, 40, 40, Color.TRANSPARENT);
                backOption.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        houseRoot.getChildren().removeAll(backRect, backArrow);
                        Node[] nodes = {propImg, houses, houseRect, houseNum, hotelBtn};
                        removeHouses(props, curr, nodes);
                    }
                });
                hotelBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        houseRoot.getChildren().removeAll(houses, hotelBtn, backRect, backArrow);
                        Text houses = makeText("Hotels: ", 10, 490, Font.font("Ebrima", 20), Color.BLACK);
                        houseRoot.getChildren().add(houses);
                        Node[] nodes = {propImg, houses, houseRect, houseNum, hotelBtn};
                        addHouse(props, curr, nodes);
                    }
                });
                houseRoot.getChildren().add(hotelBtn);
                houseRoot.getChildren().add(backOption);
            }
            //For the case in which there are less than 4 houses
            else {
                Rectangle upRect = makeRect(234, 465, 40, 40, Color.MIDNIGHTBLUE);
                Rectangle addRect = makeRect(234, 465, 40, 40, Color.TRANSPARENT);
                ImageView frontArrow = makeImg("menuBack.png", 230, 460, 50, 50);
                if (p.getDebt() == 0 && p.getMoney() > currProp.getHousePrice()) {
                    if (currProp.getHotels() != 1) {
                        houseRoot.getChildren().add(upRect);
                        houseRoot.getChildren().add(frontArrow);
                        addRect.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                if (currProp.getHouses() == 0) houseRoot.getChildren().remove(mortgageBtn);
                                houseRoot.getChildren().removeAll(backRect, backArrow, upRect, frontArrow, addRect);
                                Node[] nodes = {propImg, houses, houseRect, houseNum};
                                addHouse(props, curr, nodes);
                            }
                        });
                        houseRoot.getChildren().add(addRect);
                    }
                }
                if (currProp.getHouses() != 0) {
                    Node[] nodes = {propImg, houses, backRect, backArrow, houseRect, houseNum};
                    Rectangle backOption = makeRect(97, 465, 40, 40, Color.TRANSPARENT);
                    backOption.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            if (currProp.getHotels() != 1)
                                houseRoot.getChildren().removeAll(backRect, backArrow, upRect, addRect, frontArrow);
                            else houseRoot.getChildren().removeAll(backRect, backArrow, backOption);
                            removeHouses(props, curr, nodes);
                        }
                    });
                    houseRoot.getChildren().add(backOption);
                }
                //If there aren't any houses built on the property, it can also be mortgaged as well
                else {
                    houseRoot.getChildren().add(mortgageBtn);
                    mortgageBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            viewMortgage(props.get(curr), curr);
                        }
                    });
                }
            }
        }
    }

    //This method lets a player view the mortgaged property and choose if they'd like to mortgage it or go back
    //to purchasing houses
    public void viewMortgage(Property prop, int curr) {
        propertyBack = makeRect(5, 110, 340, 530, Color.GOLDENROD);
        propertyBack.setStrokeWidth(2.0);
        propertyBack.setStroke(Color.BLACK);
        houseRoot.getChildren().add(propertyBack);
        ImageView propImg = makeImg(prop.getMortgageImg(), 40, 120, 275, 325);
        houseRoot.getChildren().add(propImg);
        Button buy = new Button("Mortgage Property");
        buy.setPrefSize(150, 40);
        buy.setLayoutX(100);
        buy.setLayoutY(510);
        houseRoot.getChildren().add(buy);
        Button cancel = new Button("Build Houses");
        cancel.setPrefSize(150, 40);
        cancel.setLayoutX(100);
        cancel.setLayoutY(560);
        cancel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                displayPropInfo(p.getProperties().get(prop.getColor()), curr);
            }
        });
        houseRoot.getChildren().add(cancel);
        buy.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                prop.setMortgaged(true);
                Node[] nodes = {propImg, buy, cancel};
                setHousePrice(prop, prop.getMortgageVal(), nodes);
            }
        });
    }

    public void addHouse(ArrayList<Property> props, int curr, Node[] nodes) {
        if (evenBuild(props, curr)) {
            if (props.get(curr).getHouses() < 4) props.get(curr).setHouses(props.get(curr).getHouses() + 1);
            else if (props.get(curr).getHotels() == 1) {
                Text unable = makeText("You can't place any more hotels on this property at this time", 125, 535, Font.font("Ebrima", 20), Color.BLACK);
                houseRoot.getChildren().add(unable);
                Timeline timeline = new Timeline();
                KeyFrame keyFrame = new KeyFrame(
                        Duration.seconds(2.5),
                        event -> {
                            houseRoot.getChildren().removeAll(newRentText, priceText, confirm, cancel, houseNum, houseRect);
                            for (int i = 0; i < nodes.length; i++) {
                                houseRoot.getChildren().remove(nodes[i]);
                            }
                        });
                timeline.getKeyFrames().add(keyFrame);
                timeline.setCycleCount(1);
                timeline.play();
                return;
            } else props.get(curr).setHotels(props.get(curr).getHotels() + 1);
            drawHouseNum(props.get(curr), "add");
            if (props.get(curr).getHouses() < 4) {
                String newRent = "New Rent: $" + props.get(curr).getHouseRent(props.get(curr).getHouses());
                newRentText = makeText(newRent, 125, 535, Font.font("Ebrima", 20), Color.RED);
            } else {
                String newRent = "New Rent: $" + props.get(curr).getHotelRent();
                newRentText = makeText(newRent, 125, 535, Font.font("Ebrima", 20), Color.RED);
            }
            houseRoot.getChildren().add(newRentText);
            if (props.get(curr).getHouses() <= 4) {
                String price = "Price: $" + props.get(curr).getHousePrice();
                priceText = makeText(price, 125, 560, Font.font("Ebrima", 20), Color.RED);
            } else {
                String price = "Price: $" + props.get(curr).getHotelPrice();
                priceText = makeText(price, 125, 560, Font.font("Ebrima", 20), Color.RED);
            }
            houseRoot.getChildren().add(priceText);
            confirm.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    houseRoot.getChildren().removeAll(newRentText, priceText, confirm, cancel, houseNum, houseRect);
                    for (int i = 0; i < nodes.length; i++) {
                        houseRoot.getChildren().remove(nodes[i]);
                    }
                    drawHouses(props.get(curr), "add");
                }
            });
            houseRoot.getChildren().add(confirm);
            cancel.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (props.get(curr).getHotels() == 0)
                        props.get(curr).setHouses(props.get(curr).getHouses() - 1);
                    else props.get(curr).setHotels(props.get(curr).getHotels() - 1);
                    houseRoot.getChildren().removeAll(newRentText, priceText, confirm, cancel, houseNum, houseRect);
                    for (int i = 0; i < nodes.length; i++) {
                        houseRoot.getChildren().remove(nodes[i]);
                    }
                }
            });
            houseRoot.getChildren().add(cancel);
        } else {
            houseRoot.getChildren().remove(confirm);
            houseRoot.getChildren().remove(cancel);
            houseRoot.getChildren().add(denyBuildText);
            Timeline timeline = new Timeline();
            KeyFrame keyFrame = new KeyFrame(
                    Duration.seconds(2.5),
                    event -> {
                        houseRoot.getChildren().remove(denyBuildText);
                        houseRoot.getChildren().remove(propertyBack);
                        propertyBack = makeRect(5, 110, 340, 530, Color.GOLDENROD);
                        propertyBack.setStrokeWidth(2.0);
                        propertyBack.setStroke(Color.BLACK);
                        houseRoot.getChildren().add(propertyBack);
                    });
            timeline.getKeyFrames().add(keyFrame);
            timeline.setCycleCount(1);
            timeline.play();
        }
    }

    //This method is used to remove any houses that the player has decided to destroy
    public void removeHouses(ArrayList<Property> props, int curr, Node[] nodes) {
        Property currProp = props.get(curr);
        //First we use the evenDestroy() method to check if the player can destroy the house
        if (evenDestroy(props, curr)) {
            transaction = true;
            if (currProp.getHouses() < 4 || (currProp.getHouses() == 4 && p.getHouse(currProp).size() == 4))
                currProp.setHouses(props.get(curr).getHouses() - 1);
            else if (props.get(curr).getHotels() == 1) {
                props.get(curr).setHotels(props.get(curr).getHotels() - 1);
            }
            drawHouseNum(props.get(curr), "remove");
            /*for (int i = 0; i < props.get(curr).getNumColors(); i++) {
                if (p.getProperties().get(props.get(curr).getColor()).get(i).getName().equals(props.get(curr).getName()))
                    System.out.println(p.getProperties().get(props.get(curr).getColor()).get(i).getName() + " " + p.getProperties().get(props.get(curr).getColor()).get(i).getRent());
            }*/
            newRentText = makeText("New Rent: $" + currProp.getRent(), 125, 535, Font.font("Ebrima", 20), Color.RED);
            houseRoot.getChildren().add(newRentText);
            priceText = makeText("Money Gained: $" + (currProp.getHousePrice() / 2), 125, 560, Font.font("Ebrima", 20), Color.RED);
            houseRoot.getChildren().add(priceText);
            confirm.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    houseRoot.getChildren().removeAll(newRentText, priceText, confirm, cancel, houseNum, houseRect);
                    for (int i = 0; i < nodes.length; i++) {
                        houseRoot.getChildren().remove(nodes[i]);
                    }
                    transaction = false;
                    deleteHouse(props.get(curr));
                }
            });
            houseRoot.getChildren().add(confirm);
            cancel.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (props.get(curr).getHotels() == 0)
                        props.get(curr).setHouses(props.get(curr).getHouses() + 1);
                    else props.get(curr).setHotels(props.get(curr).getHotels() - 1);
                    houseRoot.getChildren().removeAll(newRentText, priceText, confirm, cancel, houseNum, houseRect);
                    for (int i = 0; i < nodes.length; i++) {
                        houseRoot.getChildren().remove(nodes[i]);
                    }
                    transaction = false;
                }
            });
            houseRoot.getChildren().add(cancel);
        } else {
            houseRoot.getChildren().remove(confirm);
            houseRoot.getChildren().remove(cancel);
            houseRoot.getChildren().add(denyRemoveText);
            Timeline timeline = new Timeline();
            KeyFrame keyFrame = new KeyFrame(
                    Duration.seconds(2.5),
                    event -> {
                        houseRoot.getChildren().remove(denyRemoveText);
                        Rectangle transp = makeRect(20, 580, 340, 640 - 580, Color.TRANSPARENT);
                        houseRoot.getChildren().add(transp);
                        if (transaction) {
                            houseRoot.getChildren().add(confirm);
                            houseRoot.getChildren().add(cancel);
                        }
                    });
            timeline.getKeyFrames().add(keyFrame);
            timeline.setCycleCount(1);
            timeline.play();
        }
    }

    //This method draws the new house on the screen
    public void drawHouses(Property prop, String mode) {
        Rectangle rect = b.getPlayerRect(p, prop.getPos());
        /*for (int i = 0; i < 4; i++) {
            int x = (int) (rect.getX() + (i * 10));
            int y = (int) rect.getY() - 17;
            ImageView house = makeImg("monopolyHouse.png", x, y, 20, 20);
            houseRoot.getChildren().add(house);
        }*/
        ImageView house;
        //For the properties between Mediterranean Avenue and the Visiting Jail spot
        if (prop.getPos() < 10) {
            //Algorithm for calculating the spot of where the new house will go
            int x = (int) rect.getX() + ((prop.getHouses() - 1) * 10);
            int y = (int) rect.getY() - 14;
            //If a hotel is going to be added, then all of the houses first need to removed
            if (prop.getHotels() == 1) {
                for (int i = 0; i < 4; i++) {
                    houseRoot.getChildren().remove(p.getHouse(prop).get(0));
                    p.getHouse(prop).remove(0);
                }
                y = (int) rect.getY() - 17;
                house = makeImg("monopolyHotel.png", (int) rect.getX() + 15, y, 15, 15);
            } else house = makeImg("monopolyHouse.png", x, y, 10, 10);
        }
        //For the properties between the Visiting Jail spot and the Free Parking spot
        else if (prop.getPos() < 20) {
            //The problem with the 11th and the 19th spot is that they bump into the 9th and 21st spot respectively
            //which is why the houses are stacked on top of each other instead of in a row
            if (prop.getPos() == 11 && prop.getHouses() > 2) {
                int x = (int) (rect.getX() + rect.getWidth() + 17);
                int y = (int) rect.getY() + 3 + (((prop.getHouses() - 1) % 2) * 10);
                if (prop.getHotels() == 1) {
                    for (int i = 0; i < 4; i++) {
                        houseRoot.getChildren().remove(p.getHouse(prop).get(0));
                        p.getHouse(prop).remove(0);
                    }
                    house = makeImg("monopolyHotel.png", (int) (rect.getX() + rect.getWidth() + 2), (int) (rect.getY() + 7), 15, 15);
                } else house = makeImg("monopolyHouse.png", x, y, 10, 10);
                house.setRotate(house.getRotate() + 90);
            } else if (prop.getPos() == 19) {
                int x = (int) (rect.getX() + rect.getWidth() + 2 + (((prop.getHouses() - 1) / 2) * 15));
                int y = (int) (rect.getY() + rect.getHeight() - 12 - (((prop.getHouses() - 1) % 2) * 10));
                if (prop.getHotels() == 1) {
                    for (int i = 0; i < 4; i++) {
                        houseRoot.getChildren().remove(p.getHouse(prop).get(0));
                        p.getHouse(prop).remove(0);
                    }
                    house = makeImg("monopolyHotel.png", (int) (rect.getX() + rect.getWidth() + 2), (int) (rect.getY() + rect.getHeight() - 23), 15, 15);
                } else house = makeImg("monopolyHouse.png", x, y, 10, 10);
                house.setRotate(house.getRotate() + 90);
            } else {
                int y = (int) rect.getY() + 3 + ((prop.getHouses() - 1) * 10);
                int x = (int) (rect.getX() + rect.getWidth() + 2);
                if (prop.getHotels() == 1) {
                    for (int i = 0; i < 4; i++) {
                        houseRoot.getChildren().remove(p.getHouse(prop).get(0));
                        p.getHouse(prop).remove(0);
                    }
                    house = makeImg("monopolyHotel.png", x, (int) (rect.getY() + 15), 15, 15);
                } else house = makeImg("monopolyHouse.png", x, y, 10, 10);
                house.setRotate(house.getRotate() + 90);
            }
        } else if (prop.getPos() < 30) {
            int x = (int) rect.getX() + ((prop.getHouses() - 1) * 10);
            int y = (int) (rect.getY() + rect.getHeight() + 2);
            if (prop.getHotels() == 1) {
                for (int i = 0; i < 4; i++) {
                    houseRoot.getChildren().remove(p.getHouse(prop).get(0));
                    p.getHouse(prop).remove(0);
                }
                house = makeImg("monopolyHotel.png", (int) rect.getX() + 15, y, 15, 15);
            } else house = makeImg("monopolyHouse.png", x, y, 10, 10);
            house.setRotate(house.getRotate() + 180);
        } else {
            if (prop.getPos() == 31) {
                int x = (int) (rect.getX() - 10 - (((prop.getHouses() - 1) / 2) * 15));
                int y = (int) (rect.getY() + rect.getHeight() - 12 - (((prop.getHouses() - 1) % 2) * 10));
                if (prop.getHotels() == 1) {
                    for (int i = 0; i < 4; i++) {
                        houseRoot.getChildren().remove(p.getHouse(prop).get(0));
                        p.getHouse(prop).remove(0);
                    }
                    house = makeImg("monopolyHotel.png", x + 8, (int) (rect.getY() + rect.getHeight() - 22), 15, 15);
                } else house = makeImg("monopolyHouse.png", x, y, 10, 10);
            } else if (prop.getPos() == 39 && prop.getHouses() > 2) {
                int x = (int) (rect.getX() - 25);
                int y = (int) (rect.getY() + 5 + (((prop.getHouses() - 1) % 2) * 10));
                if (prop.getHotels() == 1) {
                    for (int i = 0; i < 4; i++) {
                        houseRoot.getChildren().remove(p.getHouse(prop).get(0));
                        p.getHouse(prop).remove(0);
                    }
                    house = makeImg("monopolyHotel.png", x + 8, (int) rect.getY() + 7, 15, 15);
                } else house = makeImg("monopolyHouse.png", x, y, 10, 10);
            } else {
                int x = (int) (rect.getX() - 10);
                int y = (int) (rect.getY() + 5 + ((prop.getHouses() - 1) * 10));
                if (prop.getHotels() == 1) {
                    for (int i = 0; i < 4; i++) {
                        houseRoot.getChildren().remove(p.getHouse(prop).get(0));
                        p.getHouse(prop).remove(0);
                    }
                    house = makeImg("monopolyHotel.png", x - 6, (int) rect.getY() + 15, 15, 15);
                } else house = makeImg("monopolyHouse.png", x, y, 10, 10);
            }
            house.setRotate(house.getRotate() - 90);
        }
        houseRoot.getChildren().add(house);
        p.addHouse(prop, house);
        //root.getChildren().add(house);
        //houseRoot.getChildren().add(showText);
        if (mode.equals("add")) setHousePrice(prop, -1 * prop.getHousePrice(), null);
    }

    //This method essentially removes the house from the screen
    public void deleteHouse(Property prop) {
        //Remove the current house or hotel
        houseRoot.getChildren().remove(p.getHouse(prop).get(p.getHouse(prop).size() - 1));
        p.removeHouse(prop);
        //This is for the case in which you remove a hotel. In that case, you are left with 4 houses that you need to
        //build again
        if (prop.getHouses() == 4 && p.getHouse(prop).size() == 0) {
            prop.setHouses(0);
            for (int i = 0; i < 4; i++) {
                prop.setHouses(prop.getHouses() + 1);
                drawHouses(prop, "bulk");
            }
        }
        setHousePrice(prop, (prop.getHousePrice() / 2), null);
    }


    public void setHousePrice(Property prop, int price, Node[] nodes) {
        if (prop.isMortgaged()) {
            for (int i = 0; i < nodes.length; i++) houseRoot.getChildren().remove(nodes[i]);
        }
        SequentialTransition s = new SequentialTransition();
        ColorAdjust c = new ColorAdjust(0, -0.5, -0.7, 0);
        GaussianBlur g = new GaussianBlur();
        c.setInput(g);
        board.setEffect(c);
        Text name = makeText(p.getName(), 470, 210, Font.font("Bodoni MT", 40), Color.WHITE);
        houseRoot.getChildren().add(name);
        Text debt = new Text();
        if (p.getDebt() != 0) {
            debt = makeText("DEBT", 470, 250, Font.font("Bodoni MT", 40), Color.WHITE);
            houseRoot.getChildren().add(debt);
        }
        String mon;
        if (p.getDebt() != 0) mon = "$" + Integer.toString(p.getDebt());
        else mon = "$" + Integer.toString(p.getMoney());
        Text money = makeText(mon, 480, 330, Font.font("Calisto MT", 35), Color.WHITE);
        houseRoot.getChildren().add(money);
        String deduction;
        if (price < 0) {
            deduction = "-$" + (price * -1);
            if (prop.isMortgaged()) {
                p.addTransaction("Lifted Mortgage off of " + prop.getName() + ": -$" + price + "\n\tTotal: $" + (p.getMoney() - price));
                prop.setMortgaged(false);
            } else if (prop.getHouses() < 4) {
                p.addTransaction("Built a house on " + prop.getName() + " at a price of $" + prop.getHousePrice() + ": -$" + prop.getHousePrice() + "\n\tTotal: $" + (p.getMoney() - prop.getHousePrice()));
            } else {
                p.addTransaction("Built a hotel on " + prop.getName() + " at a price of $" + prop.getHotelPrice() + ": -$" + prop.getHotelPrice() + "\n\tTotal: $" + (p.getMoney() - prop.getHotelPrice()));
            }
        } else {
            if (prop.isMortgaged())
                p.addTransaction("Mortgaged " + prop.getName() + ": +$" + prop.getMortgageVal() + "\n\tTotal: $" + (p.getMoney() + prop.getMortgageVal()));
            else if (prop.getHouses() < 4)
                p.addTransaction("Sold a house on " + prop.getName() + " at a price of $" + (prop.getHousePrice() / 2) + ": +$" + (prop.getHousePrice() / 2) + "\n\tTotal: $" + (p.getMoney() + (prop.getHousePrice() / 2)));
            else
                p.addTransaction("Sold a hotel on " + prop.getName() + " at a price of $" + (prop.getHotelPrice() / 2) + ": +$" + (prop.getHotelPrice() / 2) + "\n\tTotal: $" + (p.getMoney() + (prop.getHotelPrice() / 2)));
            if (p.getDebt() == 0) deduction = "+$" + price;
            else deduction = "-$" + price;
        }
        Text deduc = makeText(deduction, 470, 290, Font.font("Calisto MT", 35), Color.WHITE);
        FadeTransition f = makeFade(2.0, 0.0, 1.0, 1);
        f.setNode(deduc);
        houseRoot.getChildren().add(deduc);
        s.getChildren().add(f);
        Line l = makeLine(470, 345, 600, 345, Color.WHITE, 3);
        FadeTransition f2 = makeFade(0.3, 0.0, 1.0, 1);
        f2.setNode(l);
        houseRoot.getChildren().add(l);
        s.getChildren().add(f2);
        if (price < 0) prop.setMonSpent(-1 * price);
        else prop.setMonGained(price);
        if (p.getDebt() != 0) {
            if (price > p.getDebt()) {
                p.setMoney(price - p.getDebt());
                p.setDebt(0);
            } else {
                p.setDebt(p.getDebt() - price);
            }
        } else p.setMoney(price);
        if (p.getDebt() != 0) mon = "$" + Integer.toString(p.getDebt());
        else mon = "$" + Integer.toString(p.getMoney());
        Text newMon = makeText(mon, 470, 385, Font.font("Calisto MT", 35), Color.WHITE);
        FadeTransition f3 = makeFade(1.0, 0.0, 1.0, 1);
        f3.setNode(newMon);
        houseRoot.getChildren().add(newMon);
        s.getChildren().add(f3);
        Text finalDebt = debt;
        s.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                houseRoot.getChildren().removeAll(name, money, deduc, l, newMon);
                if (houseRoot.getChildren().contains(finalDebt)) houseRoot.getChildren().remove(finalDebt);
                board.setEffect(null);
                drawPlayerRect();
            }
        });
        s.play();
    }

    //This method draws the current number of hotels and houses that the property currently has. This will appear right
    //under the property on the right side of the screen
    public void drawHouseNum(Property p, String mode) {
        houseRect = makeRect(145, 465, 81, 40, Color.SNOW);
        houseRoot.getChildren().add(houseRect);
        if (mode.equals("remove")) {
            if (p.getHouses() == 4)
                houseNum = makeText(Integer.toString(p.getHotels()), 180, 490, Font.font("Ebrima", 20), Color.BLACK);
            else
                houseNum = makeText(Integer.toString(p.getHouses()), 180, 490, Font.font("Ebrima", 20), Color.BLACK);
        } else {
            if (p.getHotels() == 1)
                houseNum = makeText(Integer.toString(p.getHotels()), 180, 490, Font.font("Ebrima", 20), Color.BLACK);
            else
                houseNum = makeText(Integer.toString(p.getHouses()), 180, 490, Font.font("Ebrima", 20), Color.BLACK);
        }
        houseRoot.getChildren().add(houseNum);
    }

    //This method checks if the the player is following the even building rule when requesting to build a house
    public boolean evenBuild(ArrayList<Property> props, int curr) {
        Property p = props.get(curr);
        for (int i = 0; i < props.size(); i++) {
            if (p.getHouses() > props.get(i).getHouses()) return false;
        }
        return true;
    }

    //This method checks if a player is following the even destroying rule when requesting to destroy a house
    public boolean evenDestroy(ArrayList<Property> props, int curr) {
        Property p = props.get(curr);
        for (int i = 0; i < props.size(); i++) {
            //In order to satisfy the even destroying rule, every other property in the color set must have the same
            //number of houses or less. So if the current property has less houses than one of of the other properties,
            //then it has not satisfied this rule
            if (p.getHouses() < props.get(i).getHouses()) return false;
        }
        return true;
    }

    public ImageView makeImg(String url, int x, int y, int width, int height) {
        Image img = new Image(url);
        ImageView imgView = new ImageView(img);
        imgView.setX(x);
        imgView.setY(y);
        imgView.setFitWidth(width);
        imgView.setFitHeight(height);
        return imgView;
    }

    public Rectangle makeRect(int x, int y, int width, int height, Color c) {
        Rectangle rect = new Rectangle();
        rect.setX(x);
        rect.setY(y);
        rect.setWidth(width);
        rect.setHeight(height);
        rect.setFill(c);
        return rect;
    }

    public Text makeText(String text, int x, int y, Font f, Color c) {
        Text t = new Text(text);
        t.setX(x);
        t.setY(y);
        t.setFont(f);
        t.setFill(c);
        return t;
    }

    public FadeTransition makeFade(double seconds, double fromX, double toX, int cycles) {
        FadeTransition f = new FadeTransition();
        f.setFromValue(fromX);
        f.setToValue(toX);
        f.setDuration(Duration.seconds(seconds));
        f.setCycleCount(cycles);
        return f;
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
}
