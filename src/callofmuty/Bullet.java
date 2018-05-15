
package callofmuty;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;

public class Bullet {
    
    public static final int NORMAL = 0, FIRE = 1, EGG = 2, MELEE = 3;
    private static final long TIME_BEFORE_REACTIVATING = 1000; // time before this bullet can be reactivated, ensuring other players will detect it as a new bullet and play shooting sound again
    
    private double posX, posY, speed, damage, travelledDistance, maxRange;
    private int numberOfBounces, ballWidth, ballHeight, playerId, bulletId, bulletType;
    private double[] direction;
    private ArrayList<Image> animationImages = new ArrayList<Image>();
    private Animation bulletAnimation;
    private Image image;
    private boolean isActive;
    private long timeOfDeactivation;
    
    public Bullet(int playerId, int bulletId) { //used in SQL updates and to initialize player's bullet List
        this(0.0,0.0,new double[]{0.0,0.0}, 0.0, playerId, bulletId, 0.0, 0,0, 0.0);
    }
    
    public Bullet(double posX, double posY, int playerId, int bulletId, int bulletType){ // used in otherPlayersBulletsList
        this(posX, posY,new double[]{0.0,0.0}, 0.0, playerId, bulletId, 0.0, bulletType,0, 0.0);
    }
    
    public Bullet(double posX, double posY, int bulletType) { // used for destroyed bullets animations
        this.posX = posX;
        this.posY = posY;
        this.bulletType = bulletType;
        speed = 0;
        isActive = true;
        
        this.bulletAnimation = new Animation(Animation.GUN);// in ms
        bulletAnimation.setAnimation(Animation.STILL_IMAGE);
        setAnimationRow();
        for (int i=0; i<bulletAnimation.getNumberOfImagesY(); i++)
        {
            for (int j=0; j<bulletAnimation.getNumberOfImagesX(); j++)
            {
                animationImages.add(Tools.selectTile(Tools.bulletTilesetAnimated, i+1, j+1));
            }
        }
    }
    
    public Bullet(double posX, double posY, double[] direction, double speed, int playerId, int bulletId, double damage, int bulletType, int numberOfBounces, double maxRange){
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
        travelledDistance = 0;
        timeOfDeactivation = System.currentTimeMillis()-TIME_BEFORE_REACTIVATING;
        this.maxRange = maxRange;
        bulletAnimation = new Animation(Animation.GUN);
        bulletAnimation.setAnimation(Animation.STILL_IMAGE);
        setAnimationRow();
        
        for (int i=0; i<bulletAnimation.getNumberOfImagesY(); i++){
            for (int j=0; j<bulletAnimation.getNumberOfImagesX(); j++){
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
    
    public void setBulletSpeed(double speed){
        this.speed = speed;
    }
    
    public int getBulletType(){
        return bulletType;
    }
    
    public void setBulletType(int bulletType){
        this.bulletType = bulletType;
        setAnimationRow();
    }
    
    public void update(double dT) {
        if (isActive) {
            posX += direction[0] * dT * speed;
            posY += direction[1] * dT * speed;
            bulletAnimation.update(dT);
            travelledDistance+= Math.sqrt(Math.pow(direction[0] * dT * speed, 2)+Math.pow(direction[1] * dT * speed, 2));
        }
    }
    
    public void draw(Graphics2D g2d, int texturesize, GamePanel game){
        if (isActive) {
            double zoomRatio = game.getZoomRatio()*game.getScreenSizeZoomRatio();
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
        boolean formerActive = this.isActive;
        this.isActive = isActive;
        if(formerActive && !isActive){
            timeOfDeactivation = System.currentTimeMillis();
        }
    }

    public void setDirection(double[] direction) {
        this.direction = direction;
    }
    
    public double getMaxRange(){
        return maxRange;
    }
    
    public void setMaxRange(double maxRange){
        this.maxRange = maxRange;
    }
    
    public void resetTravelledDistance(){
        travelledDistance = 0;
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
    
    public double getTravelledDistance(){
        return travelledDistance;
    }
    
    public void setTravelledDistance(double distance){
        this.travelledDistance = distance;
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

    private void setAnimationRow() {
        switch(bulletType){
            case Bullet.EGG:
                bulletAnimation.setRow(3);
                break;
            case Bullet.MELEE:
                bulletAnimation.setRow(4);
                break;
            default:
                bulletAnimation.setRow(2);
        }
    }

    public boolean isActivable() {
        return(!isActive && System.currentTimeMillis()-TIME_BEFORE_REACTIVATING>=timeOfDeactivation);
    }
}
