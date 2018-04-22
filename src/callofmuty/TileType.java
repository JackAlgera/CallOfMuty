package callofmuty;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class TileType {
   
    private final boolean crossable, hasSubImage;
    private BufferedImage image, subImage;

    TileType( boolean isCrossable, int column, int row){
        this.crossable = isCrossable;
        image = Tools.selectTile(Tools.tileset, column, row);
        hasSubImage = false;
        subImage = null;
    }
    
    TileType(boolean isCrossable, int column, int row, int subImageColumn, int subImageRow){
        this.crossable = isCrossable;
        image = Tools.selectTile(Tools.tileset, column, row);
        hasSubImage = true;
        subImage = Tools.selectTile(Tools.tileset, subImageColumn, subImageRow);
    }

    public boolean isCrossable() {
        return crossable;
    }

    public BufferedImage getImage() {
       return this.image;
    }
    public void draw(Graphics2D g2d, int x, int y, int xTextureSize, int yTextureSize){
        if(hasSubImage){
            g2d.drawImage(subImage,x, y, xTextureSize, yTextureSize, null);
        }
        g2d.drawImage(image,x, y, xTextureSize, yTextureSize, null);
    }
}
