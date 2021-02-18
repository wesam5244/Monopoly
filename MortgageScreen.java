import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class MortgageScreen {
    private Group mortgageRoot;
    private ArrayList<Property> props = new ArrayList<>();
    private Player p;
    private ImageView board;
    private Board b;
    private int interest = 0;
    private boolean[] lifted;

    public MortgageScreen(ArrayList<Property> props, Player p, Board b) {
        this.props = props;
        this.p = p;
        board = new ImageView(new Image("monopolyBoard.jpg"));
        board.setX(350);
        board.setY(70);
        board.setFitWidth(640);
        board.setFitHeight(580);
        this.b = b;
        lifted = new boolean[props.size()];
        mortgageRoot = new Group();
    }

    public int getInterest() {
        return interest;
    }

    public Player getP() {
        return p;
    }

    public ArrayList<Property> getProps() {
        return props;
    }

    public Group drawScreen() {
        ArrayList<Property> newProps = new ArrayList<>();
        String curr = props.get(0).getName();
        for (int i = 0; i < props.size(); i++) {
            if ((i % 2) == 1) {
                newProps.add(props.get(i));
            }
        }
        props.clear();
        props.addAll(newProps);
        ImageView background = new ImageView(new Image("secondBack.jpg"));
        background.setX(0);
        background.setY(0);
        background.setFitWidth(1250);
        background.setFitHeight(650);
        mortgageRoot.getChildren().add(background);
        mortgageRoot.getChildren().add(board);
        drawPlayerRect();
        drawInterest();
        drawButtons();
        return mortgageRoot;
    }

    public void drawPlayerRect() {
        Rectangle rect = new Rectangle();
        rect.setX(0);
        rect.setY(0);
        rect.setHeight(100);
        rect.setWidth(200);
        rect.setFill(Color.LIGHTBLUE);
        ImageView playerPiece = makeImage(p.getIMG(), 50, 50, 100, 50);
        String mon = p.getName() + ": $" + Integer.toString(p.getMoney());
        Text nameText = new Text();
        nameText.setText(mon);
        nameText.setFont(Font.font("Comic Sans MS", 18));
        nameText.setX(0);
        nameText.setY(20);
        mortgageRoot.getChildren().add(rect);
        mortgageRoot.getChildren().add(playerPiece);
        mortgageRoot.getChildren().add(nameText);
    }

    public void drawInterest() {
        Rectangle interestRect = new Rectangle();
        interestRect.setX(5);
        interestRect.setY(130);
        interestRect.setWidth(302);
        interestRect.setHeight(400);
        interestRect.setFill(Color.GOLDENROD);
        mortgageRoot.getChildren().add(interestRect);
        Text nameText = new Text("If you don't lift the mortgage\noff these properties,\nyou'll have to pay the\ninterest of 10%\n\nClick on the buttons for\neach property to lift the\nmortgage off it for the\nindicated price");
        nameText.setFont(Font.font("Franklin Gothic Book", 25));
        nameText.setX(10);
        nameText.setY(150);
        mortgageRoot.getChildren().add(nameText);
        interest = 0;
        for (int i = 0; i < props.size(); i++) {
            if (!lifted[i]) interest += (int) (props.get(i).getMortgageVal() * 0.10);
        }
        Text currInterest = new Text("Current Interest Owed:\n$" + interest);
        currInterest.setFont(Font.font("Franklin Gothic Book", 25));
        currInterest.setX(10);
        currInterest.setY(420);
        mortgageRoot.getChildren().add(currInterest);
    }

    public void drawButtons() {
        for (int i = 0; i < props.size(); i++) {
            System.out.println(props.get(i).getName());
            Button btn = new Button("$" + (int) (props.get(i).getMortgageVal() * 1.10));
            btn.setStyle("-fx-font-size:12");
            int pos = props.get(i).getPos();
            Rectangle r = b.getPlayerRect(p, pos);
            if (pos <= 10) {
                btn.setLayoutX(r.getX());
                btn.setLayoutY(r.getY() - 35);
                btn.setPrefSize(r.getWidth(), 30);
            } else if (pos > 10 && pos <= 20) {
                btn.setLayoutX(r.getX() - 40);
                btn.setLayoutY(r.getY() + 12);
                btn.setPrefSize(r.getHeight(), 30);
                btn.setRotate(btn.getRotate() + 90);
            } else if (pos > 20 && pos <= 30) {
                btn.setLayoutX(r.getX());
                btn.setLayoutY(r.getY() - 35);
                btn.setPrefSize(r.getWidth(), 30);
            } else if (pos > 30 && pos <= 40) {
                btn.setLayoutX(r.getX() + r.getWidth());
                btn.setLayoutY(r.getY() + 13);
                btn.setPrefSize(r.getHeight(), 30);
                btn.setRotate(btn.getRotate() + 90);
            }
            int finalI = i;
            btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    p.setMoney((int) (-1 * (props.get(finalI).getMortgageVal() * 1.10)));
                    lifted[finalI] = true;
                    props.get(finalI).setMortgaged(false);
                    drawPlayerRect();
                    drawInterest();
                    mortgageRoot.getChildren().remove(btn);
                    //System.out.println("yES");
                }
            });
            mortgageRoot.getChildren().add(btn);
        }
    }

    public ImageView makeImage(String img, int x, int y, int width, int height) {
        ImageView i = new ImageView(new Image(img));
        i.setX(x);
        i.setY(y);
        i.setFitWidth(width);
        i.setFitHeight(height);
        return i;
    }
}
