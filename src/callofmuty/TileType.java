package callofmuty;

import java.awt.image.BufferedImage;
import java.io.File;

public class TileType {
   
    private final boolean crossable;
    private final int colone;
    private final int line;  
    private BufferedImage image;

    TileType( boolean isCrossable, int column, int row){
        this.crossable = isCrossable;
        this.colone=column;
        this.line=row;
        image = Tools.selectTile(Tools.tileset, column, row);
    }

    public boolean isCrossable() {
        return crossable;
    }

    public BufferedImage getImage() {
       return this.image;
    }
}
