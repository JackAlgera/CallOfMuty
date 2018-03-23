package callofmuty;

import java.awt.image.BufferedImage;
import java.io.File;

public class TileType {
   
    private final boolean crossable;
    private final int colone;
    private final int line;  
    private BufferedImage image;
    File tileset = new File("images/Tileset.png");

    TileType( boolean isCrossable, int column, int row){
        this.crossable = isCrossable;
        this.colone=column;
        this.line=row;
        image = Tools.loadAndSelectaTile(new File("images/Tileset.png"), column, row);
    }

    public boolean isCrossable() {
        return crossable;
    }

    public BufferedImage getImage() {
       return this.image;
    }
}
