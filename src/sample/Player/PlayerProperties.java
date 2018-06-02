package sample.Player;

import sample.Player.Player;

import java.io.Serializable;

public class PlayerProperties implements Serializable {
    public int x;
    public int y;

    public PlayerProperties(Player player){
        this.x = (int)player.getPosition().getX();
        this.y = (int)player.getPosition().getY();
    }
}
