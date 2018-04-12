package callofmuty;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Tools {

    // Load every TileSet only once
    public static BufferedImage tileset = loadImage("Tileset.png"),
                hudTileset = loadImage("HudTileset.png"),
                bulletTileset = loadImage("BulletTileset.png"),
                playerTileset = loadImage("PlayerTileset.png");
    public static int tileSize = 32; //Size of a tile in a tileset image

    
    public static BufferedImage selectTile(BufferedImage tileset, int column, int row){
        return tileset.getSubimage(tileSize*(row-1),tileSize*(column-1), tileSize, tileSize);
    }
   
   public static BufferedImage loadImage(String name){
        BufferedImage image = null;
        try {
            image = ImageIO.read(Tools.class.getResource("/resources/images/"+name));
        } catch (IOException error) {
            System.out.println("Error: cannot read image : /resources/images/"+ name + " : "+ error);  
        }
        return image;
    } 
    
   public static ImageIcon loadIcon(String name){
        BufferedImage image = loadImage(name);
        return new ImageIcon(image);
    }
   
    public static Map textFileToMap(String address, int textureSize){
        int [][] intMap = null;
        int[] startingTile = null;
        try {
            BufferedReader file = new BufferedReader (new FileReader(address));
            String[] line = file.readLine().split(" ");
            file.close();
            int mapWidth, mapHeight;
            if (line.length > 3) {
                mapWidth = Integer.parseInt(line[0]);
                mapHeight = Integer.parseInt(line[1]);
                startingTile = new int[]{Integer.parseInt(line[2]),Integer.parseInt(line[3])};
                intMap = new int[mapWidth][mapHeight];
                if (line.length == 4 + mapWidth * mapHeight) {
                    for (int i = 0; i < mapWidth; i++) {
                        for (int j = 0; j < mapHeight; j++) {
                            intMap[i][j] = Integer.parseInt(line[i*mapHeight + j+4]);
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
        Map map = new Map(intMap, textureSize);
        map.setStartTile(startingTile);
        return map;
    }
    
    public static void mapToTextFile(Map map, String address){
        int[][] intMap = map.getMap();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(address));
            writer.write("" + map.getMapWidth()+ " " + map.getMapHeight() + " " + map.getStartTile()[0]+ " " + map.getStartTile()[1]);
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
    
    public static boolean isMapCrossable(double x, double y, int objectWidth, int objectHeight, Map map) {
        boolean mapIsCrossable = map.getTile(x,y).isCrossable() 
                                    && map.getTile(x + objectWidth, y).isCrossable() 
                                    && map.getTile(x, y + objectHeight).isCrossable() 
                                    && map.getTile(x + objectWidth, y + objectHeight).isCrossable();
        return mapIsCrossable;
    }
    
    public static boolean isPlayerHit(Player player, Bullet bullet){
        boolean test = bullet.posX < player.getPosX() + player.getPlayerWidth()
                        && bullet.posX + bullet.getBallWidth() > player.getPosX()
                        && bullet.posY < player.getPosY() + player.getPlayerHeight()
                        && bullet.posY + bullet.getBallHeight() > player.getPosY();
        return test;
    }
}
