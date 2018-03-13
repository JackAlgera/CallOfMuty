package callofmuty;

import java.awt.Graphics2D;
import java.awt.Image;

public class Player {
    
    private int playerWidth,playerHeight;
    private float x,y;
    private Image image;
    private double maxSpeed;
    private double[] speed;
    
    public Player(int x,int y, int playerWidth, int playerHeight,Image image){
        this.x=x;
        this.y=y;
        this.image=image;
        this.playerWidth=playerWidth;
        this.playerHeight=playerHeight;
        maxSpeed = 0.2; //in pixel per ms
        speed = new double[2];
        speed[0] = 0.0; //x speed
        speed[1] = 0.0; // y speed
        
    }
    
    public void move(long dT){
        x += speed[0]*dT;
        y += speed[1]*dT;
    }
    
    public void draw(Graphics2D g){
        g.drawImage(image,(int) x,(int) y, playerWidth, playerHeight, null);
    }
    
    public void update(int xDirection, int yDirection, long dT){
        if (xDirection!=0 && yDirection!=0){
            speed[0] = maxSpeed/Math.sqrt(2)*xDirection;
            speed[1] = maxSpeed/Math.sqrt(2)*yDirection;
        }
        else {
            speed[0] = maxSpeed*xDirection;
            speed[1] = maxSpeed*yDirection;
        }
        // check if able to move in given direction
        move(dT);
    }
}
