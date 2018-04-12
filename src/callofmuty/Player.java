package callofmuty;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Player {

    public static Image normalHealthBar = Tools.selectTile(Tools.hudTileset, 1, 2),
            lowHealthBar = Tools.selectTile(Tools.hudTileset, 1, 1);
    private static double maxHealth = 100.0;
    private static int initialBulletNumber = 5;
    public static int PLAYING = 1,DEAD = 2;
    
    private int playerId, playerWidth, playerHeight, facedDirection, playerState;
    private Image image, hpBar;
    private double maxSpeed, accelerationValue, posX, posY, wantedX, wantedY;
    private double[] speed, acceleration;
    private int[] directionOfTravel;
    private double health;
    private boolean isDead, isIdle;  
    private int[] skin;
    private String name;
    public ArrayList<Image> animationImages = new ArrayList<Image>();
    public Animation playerAnimation;
    private ArrayList<Player> hurtPlayers;
    
    private ArrayList<Bullet> bulletList;
    private Gun Gun;
        
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
        image=Tools.selectTile(Tools.playerTileset, skin[0], skin[1]);
        
        this.playerAnimation = new Animation(135,8,12,4,0); // en ms
        
        BufferedImage manTileset = Tools.loadImage("man.png"); // Temporary image, animations need to be put in playerTileset
        for (int i=0; i<playerAnimation.getNumberOfImagesY(); i++){
            for (int j=0; j<playerAnimation.getNumberOfImagesX(); j++){
                
                animationImages.add(Tools.selectTile(manTileset, i+1, j+1));
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
        name = "Username";
        playerState = 0; 
        hurtPlayers = new ArrayList<Player>();
        bulletList = new ArrayList<>();
    }

    public String getName(){
        return name;
    }
    
    public ArrayList<Player> getHurtPlayers(){
        return hurtPlayers;
    }
    
    public void setMaxHealth(){
        health = maxHealth;
    }

    public void addPlayer(SQLManager sql){
        bulletList = new ArrayList<Bullet>();
        for (int i = 1; i<=initialBulletNumber; i++){ //bulletId starts at 1, 0 is SQL's "null"
            bulletList.add(new Bullet(playerId, i));
        }
        sql.addPlayer(this);
        sql.addBulletList(bulletList);
    }
    
    public void resetHurtPlayers(){
        hurtPlayers = new ArrayList<Player>();
    }
    
    public void setPlayerState(int playerState) {
        this.playerState = playerState;
    }

    public void setHealth(double health) {
        this.health = health;
        if (health<=0){
            isDead = true;
        }
        if (health < 0.15*maxHealth){
            hpBar = lowHealthBar;
        } else {
            hpBar = normalHealthBar;
        }
    }
    
    public ArrayList<Bullet> getBulletList() {
        return bulletList;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPlayerWidth() {
        return playerWidth;
    }

    public int getPlayerHeight() {
        return playerHeight;
    }
    
    public void setSkin(int skinIndex){
        skin[1]=skinIndex;
        image=Tools.selectTile(Tools.playerTileset, skin[0], skin[1]);
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
    
    public void draw(Graphics2D g) {
        if (!isDead) {
            //g.drawImage(animationImages.get(playerAnimation.getCurrentImage(facedDirection, isIdle)),(int) posX,(int) posY, playerWidth, playerHeight, null);
            g.drawImage(image, (int) posX + playerWidth / 2 - image.getWidth(null), (int) posY + playerHeight / 2 - image.getHeight(null), image.getWidth(null) * 2, image.getHeight(null) * 2, null);
            g.drawImage(hpBar, (int) posX + playerWidth / 2 - image.getWidth(null), (int) posY + playerHeight / 2 - image.getHeight(null) - 12, image.getWidth(null) * 2, image.getHeight(null) * 2, null);
            g.setColor(Color.RED);
            g.fillRect((int) posX + playerWidth / 2 - image.getWidth(null) + 12, (int) posY + playerHeight / 2 - image.getHeight(null) - 6, (int) ((int) (image.getWidth(null) * 2 - 24) * health / maxHealth), 2);
        }
    }
    
    public void drawBullets(Graphics2D g,int texturesize) {
        for (Bullet bullet : bulletList) {
            bullet.draw(g, texturesize, 0);
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
        }
    }

    public void setFacedDirection(int facedDirection) {
        this.facedDirection = facedDirection;
    }

    public Animation getPlayerAnimation() {
        return playerAnimation;
    }
    
    public void setDirectionOfTravel(int axis, int direction)
    {
        this.directionOfTravel[axis] = direction;
    }
    
    public void reverseAcceleration(int axis)
    {
        this.acceleration[axis] = -this.acceleration[axis];
    }
    
    public void setAcceleration(int axis, double accelerationSign)
    {
        this.acceleration[axis] = accelerationSign*this.accelerationValue;
    }
    
    public void setPosition(double[] newPosition){
        if(newPosition.length==2){
            posX = newPosition[0];
            posY = newPosition[1];
        }
    }
    
    public void setPosition(Map map){
        posX = map.getStartTile()[0]*map.getTextureSize();
        posY = map.getStartTile()[1]*map.getTextureSize();
    }
    
    public void setPlayerId(int playerId)
    {
        this.playerId = playerId;
    }
    
    public int getPlayerId()
    {
        return this.playerId;
    }
    
    public double getPosX(){
        return this.posX ;
    }
    
    public double getPosY(){
        return this.posY;
    }
    
    public boolean isPlayerDead(){
        return this.isDead;
    }
    
    public double getPlayerHealth(){
        return this.health;
    }       
    
    public void chooseSkin(int row, int column){
        this.skin[0]=row;
        this.skin[1]=column;
        this.image = Tools.selectTile(Tools.playerTileset, this.skin[0], this.skin[1]);
    }
    
    public void addBullet(double initPosX, double initPosY, double[] direction, double speed, SQLManager sql){
        if (!this.isDead) {
            boolean inactiveBulletFound = false;
            int bulletIndex = 0;
            while(bulletIndex < bulletList.size() && !inactiveBulletFound){
                inactiveBulletFound = !bulletList.get(bulletIndex).isActive();
                bulletIndex++;
            }
            if(!inactiveBulletFound){
                bulletList.add(new Bullet(initPosX, initPosY, direction, speed, playerId, bulletIndex+1));
                bulletList.get(bulletIndex).setActive(true);
                sql.addBullet(bulletList.get(bulletIndex));
            } else {
                Bullet bullet = bulletList.get(bulletIndex-1);
                bullet.setActive(true);
                bullet.setSpeed(speed);
                bullet.setDirection(direction);
                bullet.setPosX(initPosX);
                bullet.setPosY(initPosY);
            }
        }
    }
    
    public Image getImage(){
        return image;
    }
    
    public void updateBulletList(long dT, Map map, ArrayList<Player> otherPlayersList) {
        Bullet bullet;
        Player hurtPlayer;
        for (int i = 0; i<bulletList.size(); i++) {
            bullet = bulletList.get(i);
            if (bullet.isActive()) {
                bullet.update(dT);
                if (bullet.checkCollisionWithMap(map)) {
                    bullet.setActive(false);
                } else {
                    for (Player otherPlayer : otherPlayersList) {
                        if (Tools.isPlayerHit(otherPlayer, bullet)) {
                            bullet.setActive(false);
                            hurtPlayer = new Player(otherPlayer.getPlayerId());
                            hurtPlayer.setHealth(bullet.getDamage());
                            hurtPlayers.add(hurtPlayer);
                        }
                    }
                }
            }
        }
    }
    
    public Player(int playerId){ //usefull constructor for SQL updates
        this.playerId = playerId;
    }
    
    public void incrementId(){
        playerId++;
    }
    
    @Override
    public boolean equals(Object object) {
        boolean test = false;

        if (object != null && object instanceof Player) { // compare 2 Players by their playerId
            test = playerId == ((Player) object).getPlayerId();
        }
        return test;
    }
}
