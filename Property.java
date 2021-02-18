import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

/*
This class tracks all of the variables that are for a property
 */
public class Property {
    private String color, name, img, mortgageImg;
    private int totalColors, price, rent, origRent, housePrice, hotelPrice, hotelRent;
    private int houses = 0, hotels = 0, monGained = 0, monSpent = 0, pos, mortgageVal;
    private int[] houseRents = new int[4];
    private boolean owned = false;
    private Player owner;
    private Rectangle propRect;
    private ImageView propImg;
    private boolean mortgaged = false;
    private ArrayList<ImageView> houseImgs = new ArrayList<>();

    public Property(String line) {
        String[] comps = line.split(",");
        this.name = comps[0];
        this.color = comps[1];
        this.price = Integer.parseInt(comps[2]);
        this.origRent = Integer.parseInt(comps[3]);
        this.totalColors = Integer.parseInt(comps[4]);
        this.housePrice = Integer.parseInt(comps[5]);
        this.hotelPrice = Integer.parseInt(comps[6]);
        houseRents[0] = Integer.parseInt(comps[7]);
        houseRents[1] = Integer.parseInt(comps[8]);
        houseRents[2] = Integer.parseInt(comps[9]);
        houseRents[3] = Integer.parseInt(comps[10]);
        this.hotelRent = Integer.parseInt(comps[11]);
        this.img = comps[12];
        this.pos = Integer.parseInt(comps[13]);
        this.mortgageVal = Integer.parseInt(comps[14]);
        this.mortgageImg = comps[15];
        rent = origRent;
    }

    public void addHouseImg(ImageView img) {
        houseImgs.add(img);
    }

    public ArrayList<ImageView> getHouseImgs() {
        return houseImgs;
    }

    public String getImg() {
        return img;
    }

    public String getMortgageImg() {
        return mortgageImg;
    }

    public void setMortgaged(boolean val) {
        mortgaged = val;
    }

    public boolean isMortgaged() {
        return mortgaged;
    }

    public int getMortgageVal() {
        return mortgageVal;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getPos() {
        return pos;
    }

    public void setHouses(int val) {
        houses = val;
        if (val == 0) rent = origRent;
        else rent = houseRents[val - 1];
    }

    public int getHouses() {
        return houses;
    }

    public int getHotels() {
        return hotels;
    }

    public void setHotels(int val) {
        hotels = val;
        if (hotels == 0) rent = houseRents[3];
        else rent = hotelRent;
    }

    public int getHotelRent() {
        System.out.println(hotelRent);
        return hotelRent;
    }

    public int getHotelPrice() {
        return hotelPrice;
    }

    public int getNumColors() {
        return totalColors;
    }

    public int getHouseRent(int pos) {
        return houseRents[pos - 1];
    }

    public int getHousePrice() {
        return housePrice;
    }

    public void setOwned(boolean val) {
        owned = val;
    }

    public boolean isOwned() {
        return owned;
    }

    public void setOwner(Player p) {
        owner = p;
    }

    public Player getOwner() {
        return owner;
    }

    public int getRent() {
        return rent;
    }

    public int getOrigRent() {
        return origRent;
    }

    public void setRent(int val) {
        rent = val;
    }

    public String getColor() {
        return color;
    }

    public int getDoubleRent() {
        return houseRents[0];
    }

    public void setMonGained(int val) {
        monGained += val;
    }

    public int getMonGained() {
        return monGained;
    }

    public void setMonSpent(int val) {
        monSpent += val;
    }

    public int getMonSpent() {
        return monSpent;
    }

    public void setPropImg(ImageView img) {
        propImg = img;
    }

    public ImageView getPropImg() {
        return propImg;
    }

    public void setPropRect(Rectangle r) {
        propRect = r;
    }

    public Rectangle getPropRect() {
        return propRect;
    }


}
