package sample.Player;

import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Player {
    private ImageView character;
    private Position position;
    private double size;
    private int id;

    public Player(){
        size = 34;
        position = new Position(size+1, size+1);
        character = new ImageView(
                new Image("http://icons.iconarchive.com/icons/custom-icon-design/flatastic-6/72/Circle-icon.png",
                        size, size, false, false));
        character.setX(size+1);
        character.setY(size+1);
        character.toFront();
    }

    public synchronized void update(PlayerProperties younger){
        position = new Position(younger.x, younger.y);
    }

    public ImageView getCharacter() {
        return character;
    }

    public int getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    public PlayerProperties getProperties(){
        return new PlayerProperties(this);
    }

    public double getX(){
        return position.getX();
    }

    public double getY(){
        return position.getY();
    }

    public double getSize(){
        return size;
    }

    public synchronized void setY(double y){
        //character.setY(y);
        position.setY(y);
    }

    public synchronized void setX(double x){
        position.setX(x);
        //position.add(x, 0);
    }
}

class Position {
    private double x;
    private double y;

    Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}