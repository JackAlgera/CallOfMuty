
package callofmuty;

public class Bullet {
    public double posX, posY;
    public double[] speed;
    public int playerId;
    
    public Bullet(double posX, double posY, double[] speed, int playerId)
    {
        this.posX = posX;
        this.posY = posY;
        this.speed = speed;
        this.playerId = playerId;
    }
    
    
    
}
