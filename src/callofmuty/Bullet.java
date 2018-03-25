
package callofmuty;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;

public class Bullet {
    public double posX, posY, speed;
    public double[] direction;
    public int playerId;
    public Image image;
    
    public Bullet(double posX, double posY, double[] direction, double speed, int playerId)
    {
        this.posX = posX;
        this.posY = posY;
        this.speed = speed;
        this.direction = direction;
        this.playerId = playerId;
        image=Tools.loadAndSelectaTile(new File("images/BulletsTileset.png"), 1, 1);
    }
    
    public void update(double dT)
    {
        posX += direction[0]*dT*speed;
        posY += direction[1]*dT*speed;
    }
    
    public void draw(Graphics2D g2d)
    {
        g2d.drawImage(image,(int) posX+16,(int) posY+16, 64, 64, null);
    }
}
