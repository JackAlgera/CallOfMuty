package callofmuty;

import java.awt.Graphics2D;
import java.awt.Image;

public class Player {
    
    private int playerWidth,playerHeight;
    private float x,y;
    private Image image;
    private double speed;
    
    public Player(int x,int y, int playerWidth, int playerHeight,Image image){
        this.x=x;
        this.y=y;
        this.image=image;
        this.playerWidth=playerWidth;
        this.playerHeight=playerHeight;
        speed = 0.2; //in pixel per ms
    }
    
    public void move(int xDirection, int yDirection, long dT){
        x += xDirection*speed*dT;
        y += yDirection*speed*dT;
    }
    
    public void draw(Graphics2D g){
        g.drawImage(image,(int) x,(int) y, playerWidth, playerHeight, null);
    }
    
    public void update(){
        
    }
}
