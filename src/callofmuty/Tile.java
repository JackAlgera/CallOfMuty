package callofmuty;

import java.awt.Graphics2D;
import java.awt.Image;

public class Tile{
    
    private int x,y, size;
    private TileType tileType;
    
    public Tile(int x, int y, int size, TileType tileType){
        this.x = x;
        this.y = y;
        this.size = size;
        this.tileType = tileType;
    }
 
    public Image getImage(){
        return tileType.getImage();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSize() {
        return size;
    }

    public TileType getTileType() {
        return tileType;
    }
    
    public void setTileType(TileType type){
        tileType=type;
    }
    
    public void draw(Graphics2D g2d){
        g2d.drawImage(tileType.getImage(), x, y, size, size, null);
    }
}
