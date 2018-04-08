package callofmuty;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;

public class Player {

    public static Image normalHealthBar = Tools.loadAndSelectaTile(new File("images/HudTileset.png"), 1, 2),
            lowHealthBar = Tools.loadAndSelectaTile(new File("images/HudTileset.png"), 1, 1);
    private double maxHealth = 100.0;
    
    private int playerId, playerWidth, playerHeight, facedDirection;
    private Image image, hpBar;
    private double maxSpeed, accelerationValue, posX, posY, wantedX, wantedY;
    private double[] speed, acceleration;
    private int[] directionOfTravel;
    private double health;
    private boolean isDead, isIdle;  
    private int[] skin;
    
    public ArrayList<Image> animationImages = new ArrayList();
    public Animation playerAnimation;
    
    private ArrayList<Bullet> bulletList = new ArrayList();

    public ArrayList<Bullet> getBulletList() {
        return bulletList;
    }
    private Guns Gun;

        
    public Player(double x,double y){
        isIdle = true;
        facedDirection = 0;
        this.posX=x;
        this.posY=y;
        this.playerWidth=35;
        this.playerHeight=55;
        skin = new int[2];
        this.skin[0]= 1;
        this.skin[1]= 1;
        image=Tools.loadAndSelectaTile(new File("images/PlayerTileset.png"), skin[0], skin[1]);
        
        this.playerAnimation = new Animation(135,8,12,4,0); // en ms
        
        for (int i=0; i<playerAnimation.getNumberOfImagesY(); i++)
        {
            for (int j=0; j<playerAnimation.getNumberOfImagesX(); j++)
            {
                animationImages.add(Tools.loadAndSelectaTile(new File("images/man.png"), i+1, j+1));
            }
        }
        
        maxSpeed = 0.3; //in pixel per ms
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
        isDead = false;
        health=maxHealth;
        hpBar = normalHealthBar;
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
    
    public int getSkinIndex(){
        return skin[1];
    }
    
    public void move(long dT){
        speed[0] += acceleration[0]*dT;
        speed[1] += acceleration[1]*dT;
        posX += speed[0]*dT;
        posY += speed[1]*dT;
    }
    
    public void draw(Graphics2D g){
        //g.drawImage(animationImages.get(playerAnimation.getCurrentImage(facedDirection, isIdle)),(int) posX,(int) posY, playerWidth, playerHeight, null);
        g.drawImage(image,(int) posX+playerWidth/2-image.getWidth(null),(int) posY+playerHeight/2-image.getHeight(null), image.getWidth(null)*2, image.getHeight(null)*2, null);
        g.drawImage(hpBar,(int) posX+playerWidth/2-image.getWidth(null),(int) posY+playerHeight/2-image.getHeight(null)-12, image.getWidth(null)*2, image.getHeight(null)*2, null);
        g.setColor(Color.RED);
        g.fillRect((int) posX+playerWidth/2-image.getWidth(null)+12, (int) posY+playerHeight/2-image.getHeight(null)-6,(int)((int)(image.getWidth(null)*2-6)*health/maxHealth)-18, 2);
    }
    
    public void drawBullets(Graphics2D g,int texturesize)
    {
        for (int i=0; i<bulletList.size(); i++)
        {
            bulletList.get(i).draw(g, texturesize, 0);
        }
    }
    
    public void update(long dT, Map map){
        if(!isDead){
            
            // Update animation
//            this.playerAnimation.update(dT);

            //Calculate speed vector
            speed[0] += acceleration[0]*dT;
            speed[1] += acceleration[1]*dT;

            double speedNorm = Math.sqrt(Math.pow(speed[0], 2) + Math.pow(speed[1], 2));
            double angle;

            if (speedNorm == 0) {
                angle = 0;
            } else {
                angle = Math.acos(speed[0]/speedNorm); //Angle between speed vector and [1,0]+
            }
            if (speedNorm>maxSpeed ){

                if (directionOfTravel[1] == -1) {
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
//            if (speed[0] == 0 && acceleration[0] == 0)
//            {
//                directionOfTravel[0] = 0;
//            }
//            if (speed[1] == 0 && acceleration[1] == 0)
//            {
//                directionOfTravel[1] = 0;
//            }
        } else {
            speed[0]=0;
            speed[1]=0;
        }
//        if (Math.abs(speed[0]) <= 0.000000001 && Math.abs(speed[1]) <= 0.000000001)
//        {
//            isIdle = true;
//        }
//        else
//        {
//            isIdle = false;
//        }
    }

    public void setFacedDirection(int facedDirection) {
        this.facedDirection = facedDirection;
    }

    public Animation getPlayerAnimation() {
        return playerAnimation;
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
        
    void setPlayerDeath(boolean isDead){
        this.isDead=isDead;
        if (isDead==true){
            this.image = Tools.loadAndSelectaTile(new File("images/PlayerTileset.png"), 2, 4);
            this.health=0;
        }else{
            this.chooseSkin(this.skin[0],this.skin[1]);
        }
    }
    
    boolean isPlayerDead(){
        return this.isDead;
    }
    
    void damagePlayer(double damage){
        if (health - damage <= 0){
            health = 0;
            setPlayerDeath(true);
        }else{
            health -= damage;
            if(health < 0.1*maxHealth){
                hpBar = lowHealthBar;
            }
        }
    }
    
    void setPlayerHealth(double health){
        this.health = health;
        if (this.isDead && health>0){
            this.setPlayerDeath(false);
        }
        if (health < 0.1*maxHealth){
            hpBar = lowHealthBar;
        } else {
            hpBar = normalHealthBar;
        }
    }
    
    double getPlayerHealth(){
        return this.health;
    }       
    
    void chooseSkin(int row, int column){
        this.skin[0]=row;
        this.skin[1]=column;
        this.image = Tools.loadAndSelectaTile(new File("images/PlayerTileset.png"), this.skin[0], this.skin[1]);
    }
    
    void addBullet(double initPosX, double initPosY, double[] direction, double speed, SQLManager sql)
    {
        // Max number of bullets
//        if (bulletList.size() > 25)
//        {
//            bulletList.remove(0);
//        }
        if (!this.isDead) {
            Bullet bullet = new Bullet(initPosX, initPosY, direction, speed, this.playerId);
            bullet.setBulletId(sql.getLastBulletId());
            bulletList.add(bullet);
            sql.addBullet(bullet);
        }
    }
    
    public Image getImage(){
        return image;
    }
    
    public void updateBulletImpact(long dT, Map map, ArrayList <Player> listPlayers, SQLManager sql)
    {
        // Update bullets
        for (int i=0; i<bulletList.size(); i++)
        {
            Bullet bullet = bulletList.get(i);
            bullet.update(dT);
            if (bullet.checkCollisionWithMap(map))
            {
                bulletList.remove(bullet);
                sql.removeBullet(bullet);
            }
//            for (int j=0; j<listPlayers.size(); j++)
//            {
//                if (j != playerId && b.checkCollisionWithPlayer(listPlayers.get(j)))
//                {
//                    bulletList.remove(b);
//                }
//            }
        }
    }
}
