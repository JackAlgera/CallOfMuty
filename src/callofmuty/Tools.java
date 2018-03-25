/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package callofmuty;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
   
    public static int[][] textFileToIntMap(String address){
        int [][] intMap = null;
        try {
            BufferedReader file = new BufferedReader (new FileReader(address));
            String[] line = file.readLine().split(" ");
            file.close();
            int mapWidth, mapHeight;
            if (line.length > 1) {
                mapWidth = Integer.parseInt(line[0]);
                mapHeight = Integer.parseInt(line[1]);
                intMap = new int[mapWidth][mapHeight];
                if (line.length == 2 + mapWidth * mapHeight) {
                    for (int i = 0; i < mapWidth; i++) {
                        for (int j = 0; j < mapHeight; j++) {
                            intMap[i][j] = Integer.parseInt(line[i*mapHeight + j+2]);
                        }
                    }
                } else {
                    System.out.println("Cannot load the map : file length is wrong");
                }
            } else {
                System.out.println("Cannot load the map : file (almost) empty");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Cannot load the map : unreadable file");
        }
        return intMap;
    }
    
    public static void mapToTextFile(Map map, String address){
        int[][] intMap = map.getMap();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(address));
            writer.write("" + map.getMapWidth()+ " " + map.getMapHeight());
            for (int i = 0; i < map.getMapWidth(); i++) {
                for (int j = 0; j < map.getMapHeight(); j++) {
                    writer.write(" "+intMap[i][j]);
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
