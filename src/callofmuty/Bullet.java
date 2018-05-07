
package callofmuty;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;

public class Bullet {
    
    public static final int NORMAL = 0, FIRE = 1, EGG = 2, MELEE = 3;
    
    public double posX, posY, speed, damage, distanceTravelled;
    private int numberOfBounces, ballWidth, ballHeight, playerId, bulletId, bulletType;
    public double[] direction;
    public ArrayList<Image> animationImages = new ArrayList<Image>();
    public Animation bulletAnimation;
    private Image image;
    private boolean isActive;
    
    public Bullet(int playerId, int bulletId, int numberOfBounces) { //usefull constructor for SQL updates
        this(0.0,0.0,new double[]{0.0,0.0}, 0.0, playerId, bulletId, 0.0, 0,numberOfBounces);
    }
    
    public Bullet(double posX, double posY, int playerId, int bulletId, int bulletType){
        this(posX, posY,new double[]{0.0,0.0}, 0.0, playerId, bulletId, 0.0, bulletType,0);
    }
    
    public Bullet(double posX, double posY, int bulletType) { //usefull constructor bullet animations
        this.posX = posX;
        this.posY = posY;
        this.bulletType = bulletType;
        speed = 0;
        isActive = true;
        
        this.bulletAnimation = new Animation(75,6,2,5,2,1);// in ms
        bulletAnimation.setRow(2);
        
        for (int i=0; i<bulletAnimation.getNumberOfImagesY(); i++)
        {
            for (int j=0; j<bulletAnimation.getNumberOfImagesX(); j++)
            {
                animationImages.add(Tools.selectTile(Tools.bulletTilesetAnimated, i+1, j+1));
            }
        }
    }
    
    public Bullet(double posX, double posY, double[] direction, double speed, int playerId, int bulletId, double damage, int bulletType, int numberOfBounces){
        this.damage = damage;
        this.posX = posX;
        this.posY = posY;
        this.bulletType = bulletType;
        this.numberOfBounces = numberOfBounces;
        ballWidth = 10;
        ballHeight = 10;
        this.speed = speed;
        this.direction = direction;
        this.playerId = playerId;
        this.bulletId = bulletId;
        isActive = false;
        image = Tools.selectTile(Tools.bulletTileset, 1, 2);
        distanceTravelled = 0;
        
        this.bulletAnimation = new Animation(130,6,2,5,2,2);// in ms
        bulletAnimation.setRow(2);
        
        for (int i=0; i<bulletAnimation.getNumberOfImagesY(); i++)
        {
            for (int j=0; j<bulletAnimation.getNumberOfImagesX(); j++)
            {
                animationImages.add(Tools.selectTile(Tools.bulletTilesetAnimated, i+1, j+1));
            }
        }
    }
    
    public boolean isActive(){
        return isActive;
    }
    
    public double getDamage(){
        return damage;
    }
    
    public int getBulletType(){
        return bulletType;
    }
    
    public void setBulletType(int bulletType){
        this.bulletType = bulletType;
    }
    
    public void update(double dT) {
        if (isActive) {
            posX += direction[0] * dT * speed;
            posY += direction[1] * dT * speed;
            bulletAnimation.update(dT);
            distanceTravelled+= Math.sqrt(Math.pow(direction[0] * dT * speed, 2)+Math.pow(direction[1] * dT * speed, 2));
        }
    }
    
    public void draw(Graphics2D g2d, int texturesize, GamePanel game){
        if (isActive) {
            double zoomRatio = game.getZoomRatio();
            g2d.drawImage(animationImages.get(bulletAnimation.getCurrentImage()),game.getGameX()+(int)(posX*zoomRatio),(int)(posY*zoomRatio),(int)(texturesize/2*zoomRatio),(int)(texturesize/2*zoomRatio), null);
        }
    }
    
    public boolean destroyedByMap(Map map){
        int collisionDirection = collisionDirection(map);
        boolean destroyBullet = false;
        if (collisionDirection != -1){
            numberOfBounces--;
            if(numberOfBounces>-1){
                direction[collisionDirection] *=-1;
            } else {
                destroyBullet = true;
            }
            System.out.println("collision detected, number of bounces left = "+numberOfBounces);
        }
        return destroyBullet;
    }
    
    public int collisionDirection(Map map){
        int bounceDirection = -1; // -1 means no bounce, 0 means horizontally, 1 means vertically
        
        if(map.getTile(posX+ballWidth/2, posY+ballHeight/2*(1+direction[1])).blocksBullets()){ // check block above / below
            bounceDirection = 1;
        } else if(map.getTile(posX+ballWidth/2*(1+direction[0]), posY+ballHeight/2).blocksBullets()){
            bounceDirection =0;
        } else if(map.getTile(posX, posY).blocksBullets() || map.getTile(posX+ballWidth, posY).blocksBullets() || map.getTile(posX, posY+ballHeight).blocksBullets() || map.getTile(posX+ballWidth, posY+ballHeight).blocksBullets()){
            bounceDirection = 1;
        }
        return bounceDirection;
    }

    public double getPosX() {
        return posX;
    }
    
    public void setActive(boolean isActive){
        this.isActive = isActive;
    }

    public void setDirection(double[] direction) {
        this.direction = direction;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public int getBallWidth() {
        return ballWidth;
    }

    public int getBallHeight() {
        return ballHeight;
    }

    public int getPlayerId() {
        return playerId;
    }
    
    public double getDistanceTravelled(){
        return distanceTravelled;
    }
    
    public void setDistanceTravelled(double distance){
        this.distanceTravelled = distance;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
    
    public void setSpeed(double speed){
        this.speed = speed;
    }
    
    public void setDamage(double damage){
        this.damage = damage;
    }
    
    public int getBulletId(){
        return bulletId;
    }

    public void setBulletId(int bulletId){
        this.bulletId = bulletId;
    }
    
    public void incrementId(){ //used to find first free id for a new bullet
        bulletId++;
    }
    
    public void setAnimation(boolean state)
    {
        if(state)
            bulletAnimation.setAnimation(1);
        else
            bulletAnimation.setAnimation(2);
    }
    
    public boolean endOfAnimation()
    {
        return bulletAnimation.endOfAnimation();
    }
    
    public void updateBulletAnimation(double dT)
    {
        bulletAnimation.update(dT);
    }
    
    @Override
    public boolean equals(Object object) {
        boolean test = false;

        if (object != null && object instanceof Bullet) {
            test = ((playerId == ((Bullet) object).getPlayerId()) && (bulletId ==((Bullet) object).getBulletId()));
        }

        return test;
    }

    void setNumberOfBounces(int numberOfBounces) {
        this.numberOfBounces = numberOfBounces;
    }
}
