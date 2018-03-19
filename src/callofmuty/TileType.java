package callofmuty;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public enum TileType {
    
    GRASS(true,1,1), DIRT(true,1,2), ROCK(false,1,3);
    
    private final boolean isCrossable;
    private int colone;
    private int line;
    
    
    private BufferedImage image;
    File tileset = new File("images/Tileset.png");


    TileType( boolean isCrossable, int colone, int line){
        this.isCrossable = isCrossable;
        this.colone=colone;
        this.line=line;
    }

    public boolean IsCrossable() {
        return isCrossable;
    }

    public BufferedImage loadAndSelectaTile(File tilesetfile, int colone, int line){
        try {
            image = ImageIO.read(tilesetfile);
        } catch (IOException error) {
            System.out.println("Error: cannot read tileset image.");        }
        int x =32*(colone-1);
        int y =32*(line-1);
        return image.getSubimage(x, y, 32, 32);
    }
    public BufferedImage getImage() {
       return loadAndSelectaTile(tileset, this.colone, this.line);
    }
}
