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
    private int[] skin;
    private ArrayList<Bullet> bulletList = new ArrayList();

        
    public Player(double x,double y, int playerWidth, int playerHeight){
        this.posX=x;
        this.posY=y;
        this.image=image;
        this.playerWidth=playerWidth;
        this.playerHeight=playerHeight;
        skin = new int[2];
        this.skin[0]= 1;
        this.skin[1]= 1;
        image=Tools.loadAndSelectaTile(new File("images/PlayerTileset.png"), skin[0], skin[1]);
        maxSpeed = 0.4; //in pixel per ms
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

    public int getPlayerWidth() {
        return playerWidth;
    }

    public int getPlayerHeight() {
        return playerHeight;
    }
    
    public void setSkin(int skinIndex){
        skin[1]=skinIndex;
        image=Tools.loadAndSelectaTile(new File("images/PlayerTileset.png"), skin[0], skin[1]);
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
    
    public void drawBullets(Graphics2D g,int texturesize)
    {
        for (Bullet b : bulletList)
        {
            b.draw(g,texturesize);
        }
    }
    
    public void update(long dT, Map map){
        if(!this.isdead){
        //Calculate speed vector
        speed[0] += acceleration[0]*dT;
        speed[1] += acceleration[1]*dT;
        
        double speedNorm = Math.sqrt(Math.pow(speed[0], 2) + Math.pow(speed[1], 2));
        double angle;
        
        if (speedNorm == 0)
        {
            angle = 0;
        }
        else 
        {
            angle = Math.acos(speed[0]/speedNorm);
        }
        
        if (speedNorm>maxSpeed ){
            if (acceleration[1] < 0)
            {
                angle = -angle;
            }
            speed[0] = maxSpeed*Math.cos(angle);
            speed[1] = maxSpeed*Math.sin(angle);
        }
        
        // Deceleration
        if (directionOfTravel[0] == 1 && acceleration[0] < 0 && speed[0]<0){
            speed[0] = 0;
            acceleration[0] = 0;
        }
        if (directionOfTravel[0] == -1 && acceleration[0] > 0 && speed[0]>0){
            speed[0] = 0;
            acceleration[0] = 0;
        }
        if (directionOfTravel[1] == 1 && acceleration[1] < 0 && speed[1]<0){
            speed[1] = 0;
            acceleration[1] = 0;
        }
        if (directionOfTravel[1] == -1 && acceleration[1] > 0 && speed[1]>0){
            speed[1] = 0;
            acceleration[1] = 0;
        }
        
        // check if player is still in the map
        wantedX = posX + speed[0]*dT;
        wantedY = posY + speed[1]*dT;
        if (wantedX<0 || wantedX+playerWidth>map.getMapWidth()*map.getTextureSize()){ 
            wantedX = posX;
            speed[0] = 0;
        }
        if (wantedY<0 || wantedY+playerHeight>map.getMapHeight()*map.getTextureSize()){
            wantedY = posY;
            speed[1] = 0;
        }
        // check if able to move in given direction (not trying to cross uncrossable tile)
        if(!Tools.isMapCrossable(wantedX, wantedY, playerWidth, playerHeight, map)){ // test if the tile the player is going to is crossable
            if (Tools.isMapCrossable(posX, wantedY, playerWidth, playerHeight, map)){ //try to block x movement
                wantedX = posX;
                speed[0] = 0;
            } else {
                if (Tools.isMapCrossable(wantedX, posY, playerWidth, playerHeight,map)){ // try to block y movement
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
        }else{
            speed[0]=0;
            speed[1]=0;
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
        
    void setplayerdeath(boolean isdead){
        this.isdead=isdead;
        if (isdead==true){
            this.image = Tools.loadAndSelectaTile(new File("images/PlayerTileset.png"), 2, 4);
            this.health=0;
        }else{
            this.chooseskin(this.skin[0],this.skin[1]);
        }
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
        this.skin[0]=row;
        this.skin[1]=column;
        this.image = Tools.loadAndSelectaTile(new File("images/PlayerTileset.png"), this.skin[0], this.skin[1]);
    }
    void healthcheck(){
        if(this.isdead==true){
            this.hpbar= Tools.loadAndSelectaTile(new File("images/HudTileset.png"), 2, 11);
        }else{
        int cursor = (int)Math.floor(this.health/10)+1;
        this.hpbar = Tools.loadAndSelectaTile(new File("images/HudTileset.png"), 1, cursor);
        }
    }
    
    void addBullet(double initPosX, double initPosY, double[] direction, double speed)
    {
        if (bulletList.size() > 25)
        {
            bulletList.remove(0);
        }
        
        if (!this.isdead) {
            bulletList.add(new Bullet(initPosX, initPosY, direction, speed, this.playerId));
        }
    }
    
    public Image getImage(){
        return image;
    }
    
    public void updateBulletImpact(long dT, Map map, ArrayList <Player> listPlayers)
    {
        // Update bullets
        for (int i=0; i<bulletList.size(); i++)
        {
            Bullet b = bulletList.get(i);
            b.update(dT);
            if (b.checkCollisionWithMap(map))
            {
                bulletList.remove(b);
            }
            for (int j=0; j<listPlayers.size(); j++)
            {
                if (j != playerId && b.checkCollisionWithPlayer(listPlayers.get(j)))
                {
                    bulletList.remove(b);
                }
            }
            
        }
    }
}
