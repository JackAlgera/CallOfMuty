package callofmuty;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class TileType {
   
    private final boolean isCrossable;
    private final int colone;
    private final int line;  
    private BufferedImage image;
    File tileset = new File("images/Tileset.png");

    TileType( boolean isCrossable, int column, int row){
        this.isCrossable = isCrossable;
        this.colone=column;
        this.line=row;
        loadAndSelectaTile(tileset, column, row);
    }

    public boolean IsCrossable() {
        return isCrossable;
    }

    public void loadAndSelectaTile(File tilesetfile, int column, int row){
        try {
            BufferedImage imageFull;
            imageFull = ImageIO.read(tilesetfile);      
            int y =32*(column-1);
            int x =32*(row-1);
            this.image = imageFull.getSubimage(x, y, 32, 32);
        } catch (IOException error) {
            System.out.println("Error: cannot read tileset image.");  
        }
    }
    
    public BufferedImage getImage() {
       return this.image;
    }
}
