package callofmuty;

import java.awt.Graphics2D;
import java.awt.Image;

public class Player {
    
    private int playerId;
    private int playerWidth,playerHeight;
    private Image image;
    private double maxSpeed, posX, posY, wantedX, wantedY;
    private double[] speed;
    
    public Player(int x,int y, int playerWidth, int playerHeight,Image image){
        this.posX=x;
        this.posY=y;
        this.image=image;
        this.playerWidth=playerWidth;
        this.playerHeight=playerHeight;
        maxSpeed = 0.3; //in pixel per ms
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
    
    public void update(int xDirection, int yDirection, long dT, Map map){
        //Calculate speed vector
        if (xDirection!=0 && yDirection!=0){ 
            speed[0] = maxSpeed/Math.sqrt(2)*xDirection;
            speed[1] = maxSpeed/Math.sqrt(2)*yDirection;
        }
        else {
            speed[0] = maxSpeed*xDirection;
            speed[1] = maxSpeed*yDirection;
        }
        // check if player is still in the map
        wantedX = posX + speed[0]*dT;
        wantedY = posY + speed[1]*dT;
        if (wantedX<0 || wantedX+playerWidth>map.getMapWidth()*map.getTextureSize()){ 
            wantedX = posX;
            speed[0] = 0;
            System.out.println("X movement blocked");
        }
        if (wantedY<0 || wantedY+playerHeight>map.getMapHeight()*map.getTextureSize()){
            wantedY = posY;
            speed[1] = 0;
            System.out.println("Y movement blocked");
        }
        // check if able to move in given direction (not trying to cross uncrossable tile)
        if(!map.pathIsCrossable(wantedX, wantedY, playerWidth, playerHeight)){ // test if the tile the player is going to is crossable
            if (map.pathIsCrossable(posX, wantedY, playerWidth, playerHeight)){ //try to block x movement
                wantedX = posX;
                speed[0] = 0;
            } else {
                if (map.pathIsCrossable(wantedX, posY, playerWidth, playerHeight)){ // try to block y movement
                    wantedY = posY;
                    speed[1] = 0;
                } else { // block movement
                    wantedX = posX;
                    speed[0] = 0;
                    wantedY = posY;
                    speed[1] = 0;
                }
            }
        }
        //move;
        posX = wantedX;
        posY = wantedY;
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
