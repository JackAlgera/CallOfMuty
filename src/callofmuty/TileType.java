package callofmuty;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class TileType {
   
    private boolean blocksPlayers, hasSubImage, blocksBullets;
    private BufferedImage image, subImage;

    TileType( boolean blocksPlayers, int column, int row){
        this.blocksPlayers = blocksPlayers;
        image = Tools.selectTile(Tools.tileset, column, row);
        hasSubImage = false;
        subImage = null;
    }
    
    TileType(boolean blocksPlayers, int column, int row, int subImageColumn, int subImageRow){
        this.blocksPlayers = blocksPlayers;
        image = Tools.selectTile(Tools.tileset, column, row);
        hasSubImage = true;
        subImage = Tools.selectTile(Tools.tileset, subImageColumn, subImageRow);
    }

    public boolean blocksPlayers() {
        return blocksPlayers;
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
