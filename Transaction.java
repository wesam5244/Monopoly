/*
This class tracks Transaction objects that are used to track the transactions in a trade s
 */

public class Transaction {
    private String type;
    private Property p;
    private String jailType;
    private int money;

    public Transaction(String type) {
        this.type = type;
    }

    public void setProp(Property p) {
        this.p = p;
    }

    public void setJailType(String s) {
        jailType = s;
    }

    public void setMoney(int m) {
        money = m;
    }

    public Property getProp() {
        return p;
    }

    public String getType() {
        return type;
    }

    public String getJailType() {
        return jailType;
    }

    public int getMoney() {
        return money;
    }
}
