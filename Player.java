import javafx.scene.image.ImageView;import javafx.scene.paint.Color;import java.util.ArrayList;import java.util.HashMap;public class Player {    private String name;    private String img;    private int money;    private HashMap<String, ArrayList<Property>> playerProperties = new HashMap<>();    private HashMap<Property, ArrayList<ImageView>> houses = new HashMap<>();    private int numProperties = 0;    private int playerPos = 0;    private Color c;    private ArrayList<String> transactions = new ArrayList<>();    private int[] bottomRow = new int[2];    private int[] leftRow = new int[2];    private int topRowY;    private int rightRowX;    private boolean inJail = false, doubleUtil = false, doubleRail = false;    private String[] jailFree = {"", ""};    private int jailTurn = 0;    private int doubleCount = 0;    private boolean rollDouble = false;    private boolean bankrupt = false;    private ArrayList<Property> propImages = new ArrayList<>();    private int debt = 0;    public Player() {        this.name = "";        this.img = "";        playerProperties.put("Purple", new ArrayList<Property>());        playerProperties.put("White", new ArrayList<Property>());        playerProperties.put("Light Blue", new ArrayList<Property>());        playerProperties.put("Pink", new ArrayList<Property>());        playerProperties.put("Utility", new ArrayList<Property>());        playerProperties.put("Orange", new ArrayList<Property>());        playerProperties.put("Red", new ArrayList<Property>());        playerProperties.put("Yellow", new ArrayList<Property>());        playerProperties.put("Green", new ArrayList<Property>());        playerProperties.put("Dark Blue", new ArrayList<Property>());        money = 1500;    }    public Player(String name, String img, Color c) {        this.name = name;        this.img = img;        this.c = c;    }    public Player(Player player) {    }    public void setBankrupt(boolean val) {        bankrupt = val;    }    public boolean isBankrupt() {        return bankrupt;    }    public void setColor(Color c) {        this.c = c;    }    public Color getColor() {        return c;    }    public void addTransaction(String t) {        transactions.add(t);    }    public ArrayList<String> getTransactions() {        return transactions;    }    public void setIMG(String img) {        this.img = img;    }    public String getIMG() {        return img;    }    public void setName(String name) {        this.name = name;    }    public String getName() {        return name;    }    public int getMoney() {        return money;    }    public void setMoney(int mon) {        money += mon;    }    public int getDebt() {        return debt;    }    public void setDebt(int mon) {        debt = mon;    }    public void setDoubleUtil(boolean val) {        doubleUtil = val;    }    public void setDoubleRail(boolean val) {        doubleRail = val;    }    public boolean getDoubleUtil() {        return doubleUtil;    }    public boolean getDoubleRail() {        return doubleRail;    }    public void setDoubles(boolean val) {        rollDouble = val;    }    public boolean getDoubles() {        return rollDouble;    }    public void setDoubleCount(int val) {        doubleCount = val;    }    public int getDoubleCount() {        return doubleCount;    }    public void addHouse(Property p, ImageView house) {        if (p.getHouses() == 1) houses.put(p, new ArrayList<ImageView>());        houses.get(p).add(house);        //System.out.println(p.getName() + " " + houses.get(p).size());    }    public void removeHouse(Property p) {        houses.get(p).remove(houses.get(p).size() - 1);    }    public ArrayList<ImageView> getHouse(Property p) {        return houses.get(p);    }    public void addProperty(Property prop) {        playerProperties.get(prop.getColor()).add(prop);        propImages.add(prop);        numProperties++;        if ((prop.getColor().equals("White") || prop.getColor().equals("Utility")) && playerProperties.get(prop.getColor()).size() > 1) {            for (int i = 0; i < playerProperties.get(prop.getColor()).size(); i++) {                playerProperties.get(prop.getColor()).get(i).setRent(prop.getHouseRent(playerProperties.get(prop.getColor()).size() - 1));            }        } else {            if (playerProperties.get(prop.getColor()).size() == prop.getNumColors()) {                for (int i = 0; i < playerProperties.get(prop.getColor()).size(); i++) {                    int oldRent = playerProperties.get(prop.getColor()).get(i).getRent();                    if (playerProperties.get(prop.getColor()).get(i).getHouses() == 0)                        playerProperties.get(prop.getColor()).get(i).setRent(oldRent * 2);                }            }        }    }    public void removeProperty(Property p) {        playerProperties.get(p.getColor()).remove(p);        numProperties--;        if (p.getColor().equals("White") || p.getColor().equals("Utility")) {            if (playerProperties.get(p.getColor()).size() == 1) {                for (int i = 0; i < playerProperties.get(p.getColor()).size(); i++) {                    playerProperties.get(p.getColor()).get(i).setRent(playerProperties.get(p.getColor()).get(i).getOrigRent());                }            } else {                for (int i = 0; i < playerProperties.get(p.getColor()).size(); i++) {                    playerProperties.get(p.getColor()).get(i).setRent(playerProperties.get(p.getColor()).get(i).getHouseRent(playerProperties.get(p.getColor()).size() - 1));                }            }        } else {            for (int i = 0; i < playerProperties.get(p.getColor()).size(); i++) {                if (playerProperties.get(p.getColor()).get(i).getHouses() == 0)                    playerProperties.get(p.getColor()).get(i).setRent(playerProperties.get(p.getColor()).get(i).getOrigRent());            }        }    }    public Property getLastPropImg() {        return propImages.get(propImages.size() - 1);        /*String[] colors = {"Purple", "White", "Light Blue", "Pink", "Utility", "Orange", "Red", "Yellow", "Green", "Dark Blue"};        int curr = 0;        for (int i = 0; i < colors.length; i++) {            ArrayList<Property> props = playerProperties.get(colors[i]);            for (int x = 0; x < props.size(); x++) {                curr++;                if (curr == numProperties) {                    return props.get(x);                }            }        }        return null;*/    }    public int getPropPos(Property p) {        for (int i = 0; i < propImages.size(); i++) {            if (propImages.get(i).getName().equals(p.getName())) {                return i;            }        }        return -1;    }    public ArrayList<Property> getPropImages() {        return propImages;    }    public int getRailRoads() {        return playerProperties.get("White").size();    }    public int getUtilities() {        return playerProperties.get("Utility").size();    }    public HashMap<String, ArrayList<Property>> getProperties() {        return playerProperties;    }    public boolean ownsAllColors(Property p) {        if (playerProperties.get(p.getColor()).size() == p.getNumColors()) return true;        return false;    }    public int getPlayerPos() {        return playerPos;    }    public void setPlayerPos(int pos) {        playerPos = pos;    }    public Color getC() {        return c;    }    public void setBottomRow(int x, int y) {        bottomRow[0] = x;        bottomRow[1] = y;    }    public void setLeftRow(int x, int y) {        leftRow[0] = x;        leftRow[1] = y;    }    public void setTopRowY(int y) {        topRowY = y;    }    public int getTopRowY() {        return topRowY;    }    public int getBottomRowY() {        return bottomRow[1];    }    public int getLeftRowX() {        return leftRow[0];    }    public void setRightRowX(int x) {        rightRowX = x;    }    public int getRightRowX() {        return rightRowX;    }    public void setInJail(boolean val) {        inJail = val;    }    public boolean isInJail() {        return inJail;    }    public String setJailFree(String val) {        for (int i = 0; i < 2; i++) {            if (val.isEmpty()) {                if (!jailFree[i].isEmpty()) {                    String oldVal = jailFree[i];                    jailFree[i] = "";                    return oldVal;                }            } else {                if (jailFree[i].equals("")) {                    jailFree[i] = val;                    return jailFree[i];                }            }        }        return "";    }    public String getJailFree(int pos) {        return jailFree[pos];    }    public String hasJailFree() {        for (int i = 0; i < 2; i++) {            if (!jailFree[i].isEmpty()) return jailFree[i];        }        return "";    }    public void incJailTurn() {        ++jailTurn;    }    public int getJailTurn() {        return jailTurn;    }    public void setJailTurn(int num) {        jailTurn = num;    }    public void setNumProperties(int val) {        numProperties = val;    }    public int getNumProperties() {        return numProperties;    }    public int getNextPropertyX(int turn) {        int x = 0;        if (turn == 0 || turn == 2) {            x = 300 - (((numProperties - 1) / 6) * 60);        } else {            x = 1010 + (((numProperties - 1) / 6) * 55);        }        return x;    }    public int getNextPropertyY(int turn) {        int currProp = numProperties - 1;        int y = 0;        if (turn == 0 || turn == 1) {            y = 100 + (35 * (currProp % 6));        } else {            y = 320 + (35 * (currProp % 6));        }        return y;    }}