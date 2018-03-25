package callofmuty;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;

public class Player {
    
    private int playerId;
    private int playerWidth,playerHeight;
    private Image image;
    private Image hpbar;
    private double maxSpeed, accelerationValue, posX, posY, wantedX, wantedY;
    private double[] speed;
    private double[] acceleration;
    private int[] directionOfTravel;
    private double health;
    private boolean isdead;  
    private int skin;
    private ArrayList<Bullet> bulletList = new ArrayList();

        
    public Player(double x,double y, int playerWidth, int playerHeight,Image image){
        this.posX=x;
        this.posY=y;
        this.image=image;
        this.playerWidth=playerWidth;
        this.playerHeight=playerHeight;
        maxSpeed = 0.5; //in pixel per ms
        speed = new double[2];
        speed[0] = 0.0; //x speed
        speed[1] = 0.0; // y speed
        acceleration = new double[2];
        acceleration[0] = 0.0; // x acceleration
        acceleration[1] = 0.0; // y acceleration
        directionOfTravel = new int[2];
        directionOfTravel[0] = 0; // =-1 -> wants to go left, =+1 -> wants to go right, =0 -> stands still on x axis
        directionOfTravel[1] = 0; // =-1 -> wants to go up, =+1 -> wants to go down, =0 -> stands still on y axis
        this.accelerationValue = 0.002;
        isdead = false;
        health=100.0;
    }
    
    public void move(long dT){
        speed[0] += acceleration[0]*dT;
        speed[1] += acceleration[1]*dT;
        posX += speed[0]*dT;
        posY += speed[1]*dT;
    }
    
    public void draw(Graphics2D g){
        g.drawImage(image,(int) posX,(int) posY, playerWidth, playerHeight, null);
        g.drawImage(hpbar,(int) posX,(int) posY-12, playerWidth, playerHeight, null);
    }
    
    public void drawBullets(Graphics2D g)
    {
        for (Bullet b : bulletList)
        {
            b.draw(g);
        }
    }
    
    public void update(long dT, Map map){
        //Calculate speed vector
        speed[0] += acceleration[0]*dT;
        speed[1] += acceleration[1]*dT;
        
        if (Math.abs(speed[0])>maxSpeed ){
            speed[0] = Math.signum(speed[0])*maxSpeed;
        }
        if (Math.abs(speed[1])>maxSpeed ){
            speed[1] = Math.signum(speed[1])*maxSpeed;
        }
        
        // Deceleration
        if (directionOfTravel[0] == 1 && acceleration[0] < 0 && speed[0]<0){
            speed[0] = 0;
        }
        if (directionOfTravel[0] == -1 && acceleration[0] > 0 && speed[0]>0){
            speed[0] = 0;
        }
        if (directionOfTravel[1] == 1 && acceleration[1] < 0 && speed[1]<0){
            speed[1] = 0;
        }
        if (directionOfTravel[1] == -1 && acceleration[1] > 0 && speed[1]>0){
            speed[1] = 0;
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
        posX = wantedX;
        posY = wantedY;
        
        // Update bullets
        for (Bullet b : bulletList)
        {
            b.update(dT);
        }
    }
    
    void setDirectionOfTravel(int axis, int direction)
    {
        this.directionOfTravel[axis] = direction;
    }
    
    void reverseAcceleration(int axis)
    {
        this.acceleration[axis] = -this.acceleration[axis];
    }
    
    void setAcceleration(int axis, double accelerationSign)
    {
        this.acceleration[axis] = accelerationSign*this.accelerationValue;
    }
    
    void setPosition(double[] newPos)
    {
        posX = newPos[0];
        posY = newPos[1];
    }
    
    void setPlayerId(int playerId)
    {
        this.playerId = playerId;
    }
    
    int getPlayerId()
    {
        return this.playerId;
    }
    double getPosX(){
        return this.posX ;
    }
    double getPosY(){
        return this.posY;
    }
        
    void setplayerdeath(boolean isdeath){
        this.isdead=isdeath;
    }
    boolean getplayerdeath(){
        return this.isdead;
    }
    void damageplayer(double damage){
        if (this.health-damage<=0){
            this.health=0;
            this.setplayerdeath(true);
        }else{
            this.health-=damage;
        }
    }
    void setplayerhealth(double life){
        this.health=life;
        if (this.getplayerdeath()&& life>0){
            this.setplayerdeath(false);
        }
    }
    double getplayerhealth(){
        return this.health;
    }       
    void chooseskin(int row, int column){
        this.image = Tools.loadAndSelectaTile(new File("images/PlayerTileset.png"), row, column);
    }
    void healthcheck(){
        int cursor = (int)Math.floor(this.health/10)+1;
        this.hpbar = Tools.loadAndSelectaTile(new File("images/HudTileset.png"), 1, cursor);
    }
    
    void addBullet(double initPosX, double initPosY, double[] direction, double speed)
    {
        if (bulletList.size() > 25)
        {
            bulletList.remove(0);
        }
        
        bulletList.add(new Bullet(initPosX, initPosY, direction, speed, this.playerId));
    }
}
