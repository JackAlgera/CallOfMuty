package callofmuty;

import java.awt.Image;
import javax.swing.ImageIcon;

public enum TileType {
    
    GRASS("images/grass.png", true), DIRT("images/dirt.png",true), ROCK("images/rock.png",false);
    
    private boolean isCrossable;
    private Image image;
    
    TileType(String file, boolean isCrossable){
        this.isCrossable = isCrossable;
        image = new ImageIcon(file).getImage();
    }

    public boolean isIsCrossable() {
        return isCrossable;
    }

    public Image getImage() {
        return image;
    }
    
    
}
