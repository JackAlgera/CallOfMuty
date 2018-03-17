package callofmuty;

import java.awt.Graphics2D;
import java.awt.Image;

public class Player {
    
    private int playerId;
    private int playerWidth,playerHeight;
    private float posX,posY;
    private Image image;
    private double maxSpeed;
    private double[] speed;
    
    public Player(int x,int y, int playerWidth, int playerHeight,Image image){
        this.posX=x;
        this.posY=y;
        this.image=image;
        this.playerWidth=playerWidth;
        this.playerHeight=playerHeight;
        maxSpeed = 0.2; //in pixel per ms
        speed = new double[2];
        speed[0] = 0.0; //x speed
        speed[1] = 0.0; // y speed
        
    }
    
    public void move(long dT){
        posX += speed[0]*dT;
        posY += speed[1]*dT;
    }
    
    public void draw(Graphics2D g){
        g.drawImage(image,(int) posX,(int) posY, playerWidth, playerHeight, null);
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
    
    void setPosition(float[] newPos)
    {
        posX = newPos[1];
        posY = newPos[2];
    }
    
    void setPlayerId(int playerId)
    {
        this.playerId = playerId;
    }
    
    int getPlayerId()
    {
        return this.playerId;
    }
    
}
