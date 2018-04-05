
package callofmuty;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;

public class Bullet {
    public double posX, posY, speed;
    public int ballWidth, ballHeight, playerId, bulletId;
    public double[] direction;
    public ArrayList<Image> animationImages = new ArrayList();
    public Animation bulletAnimation;
    private Image image;
    
    public Bullet(double posX, double posY)
    {
        this.posX = posX;
        this.posY = posY;
        ballWidth = 10;
        ballHeight = 10;
        this.speed = 0;
        this.direction = new double[2];
        this.playerId = 0;
        
        image = Tools.loadAndSelectaTile(new File("images/BulletsTileset.png"), 1, 1);
        
//        this.bulletAnimation = new Animation(250,1,4,4,0);// en ms
        
        for (int i=0; i<bulletAnimation.getNumberOfImagesY(); i++)
        {
            for (int j=0; j<bulletAnimation.getNumberOfImagesX(); j++)
            {
                animationImages.add(Tools.loadAndSelectaTile(new File("images/BulletsTileset.png"), i+1, j+1));
            }
        }
    }
    
    public Bullet(double posX, double posY, double[] direction, double speed, int playerId)
    {
        this.posX = posX;
        this.posY = posY;
        ballWidth = 10;
        ballHeight = 10;
        this.speed = speed;
        this.direction = direction;
        this.playerId = playerId;
        
        image = Tools.loadAndSelectaTile(new File("images/BulletsTileset.png"), 1, 1);
        
        this.bulletAnimation = new Animation(250,1,4,4,0);// en ms
        
        for (int i=0; i<bulletAnimation.getNumberOfImagesY(); i++)
        {
            for (int j=0; j<bulletAnimation.getNumberOfImagesX(); j++)
            {
                animationImages.add(Tools.loadAndSelectaTile(new File("images/BulletsTileset.png"), i+1, j+1));
            }
        }
    }
    
    public void update(double dT)
    {
        posX += direction[0]*dT*speed;
        posY += direction[1]*dT*speed;
//        bulletAnimation.update(dT);
    }
    
    public void draw(Graphics2D g2d, int texturesize, int row)
    {
//        g2d.drawImage(animationImages.get(bulletAnimation.getCurrentImage(row, false)),(int) posX,(int) posY, texturesize/2, texturesize/2, null);
        g2d.drawImage(image,(int) posX,(int) posY, texturesize/2, texturesize/2, null);
    }
    
    public boolean checkCollisionWithMap(Map map)
    {
        if(!Tools.isMapCrossable(posX, posY, ballWidth, ballHeight, map))
        {
            return true;
        }
        else return false;
    }
    
    public boolean checkCollisionWithPlayer(Player p)
    {
        if(!Tools.isPlayerHit(posX, posY, ballWidth, ballHeight, p))
        {
            return true;
        }
        else return false;
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
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

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
    
    public int getBulletId()
    {
        return bulletId;
    }

    public void setBulletId(int bulletId) {
        this.bulletId = bulletId;
    }
}
