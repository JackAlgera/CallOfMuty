
package callofmuty;

import java.awt.Graphics2D;

public class Bullet {
    public double posX, posY, speed;
    public double[] direction;
    public int playerId;
    
    public Bullet(double posX, double posY, double[] direction, double speed, int playerId)
    {
        this.posX = posX;
        this.posY = posY;
        this.speed = speed;
        this.direction = direction;
        this.playerId = playerId;
    }
    
    public void update(double dT)
    {
        posX += direction[0]*dT*speed;
        posY += direction[1]*dT*speed;
    }
    
    public void draw(Graphics2D g2d)
    {
        g2d.fillRect((int)posX, (int)posY, 25, 25);
    }
}
