package callofmuty;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class TileType {
   
    private boolean blocksPlayers, hasSubImage, blocksBullets;
    private BufferedImage image, subImage;
    private Effect effect;

    TileType(boolean blocksPlayers, boolean blocksBullets, int column, int row, Effect effect){
        this.blocksPlayers = blocksPlayers;
        image = Tools.selectTile(Tools.tileset, column, row);
        hasSubImage = false;
        subImage = null;
        this.blocksBullets = blocksBullets;
        this.effect = effect;
    }
    
    TileType(boolean blocksPlayers, boolean blocksBullets, int column, int row, int subImageColumn, int subImageRow, Effect effect){
        this.blocksPlayers = blocksPlayers;
        image = Tools.selectTile(Tools.tileset, column, row);
        hasSubImage = true;
        subImage = Tools.selectTile(Tools.tileset, subImageColumn, subImageRow);
        this.blocksBullets = blocksBullets;
        this.effect = effect;
    }

    public boolean blocksPlayers() {
        return blocksPlayers;
    }
    
    public boolean blocksBullets() {
        return blocksBullets;
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
    
    public Effect getEffect(){
        return effect;
    }
}
