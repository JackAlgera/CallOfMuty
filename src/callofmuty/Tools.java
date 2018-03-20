/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package callofmuty;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author tlaurend
 */
public class Tools {
    
    public static BufferedImage loadAndSelectaTile(File tilesetfile, int column, int row){
        BufferedImage image = null;
        try {
            BufferedImage imageFull;
            imageFull = ImageIO.read(tilesetfile);      
            int y =32*(column-1);
            int x =32*(row-1);
            image = imageFull.getSubimage(x, y, 32, 32);
        } catch (IOException error) {
            System.out.println("Error: cannot read tileset image.");  
        }
        return image;
    }
   
    
    
}
