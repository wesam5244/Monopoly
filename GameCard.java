public class GameCard {
    private String text;
    private String type;
    private int money;
    private int newPos;

    public GameCard(String line) {
        String[] words = line.split(",");
        this.text = words[0];
        this.type = words[1];
        this.money = Integer.parseInt(words[2]);
        this.newPos = Integer.parseInt(words[3]);
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public int getMoney() {
        return money;
    }

    public int getNewPos() {
        return newPos;
    }
}
