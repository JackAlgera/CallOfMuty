package callofmuty;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Tools {

    // Load every TileSet only once
    public static BufferedImage tileset = loadImage("Tileset.png"),
                hudTileset = loadImage("HudTileset.png"),
                bulletTileset = loadImage("BulletTileset.png"),
                bulletTilesetAnimated = loadImage("BulletsTilesetAnimated.png"),
                playerTileset = loadImage("PlayerTileset.png"),
                PlayerTilesetAnimated = loadImage("PlayerTilesetAnimated.png"),
                WeaponTileset = loadImage("WeaponTileset.png");
    
    public static int tileSize = 32; //Size of a tile in a tileset image
    public static int playerTileSize = 64; //Size of a tile in a tileset image

    
    public static BufferedImage selectTile(BufferedImage tileset, int column, int row){
        return tileset.getSubimage(tileSize*(row-1),tileSize*(column-1), tileSize, tileSize);
    }
    
    public static BufferedImage selectPlayerTile(BufferedImage tileset, int column, int row){
        return tileset.getSubimage(playerTileSize*(row-1),playerTileSize*(column-1), playerTileSize, playerTileSize);
    }
    
    public static BufferedImage selectWeaponTile(BufferedImage tileset, int column, int row, int size){
        BufferedImage gunImage;
        switch(size) {
            case 1:
                    gunImage = tileset.getSubimage(tileSize*(row-1),tileSize*(column-1), tileSize, tileSize);
                    break;
                            
            case 2:
                    gunImage = tileset.getSubimage(tileSize*(row-1),tileSize*(column-1), tileSize*2, tileSize);
                    break;
            
            default :
                    gunImage = tileset.getSubimage(tileSize*(row-1),tileSize*(column-1), tileSize, tileSize);
                    
        }
        return gunImage;
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
        return stringToMap(loadTextFile(address), textureSize);
    }
    
    private static Map stringToMap(String text, int textureSize){
        int[][] intMap = null;
        ArrayList<int[]> startingTile = new ArrayList<>();
        String[] line = text.split(" ");
        int mapWidth, mapHeight;
        if (line.length >= 2) {
            mapWidth = Integer.parseInt(line[0]);
            mapHeight = Integer.parseInt(line[1]);
            intMap = new int[mapWidth][mapHeight];
            if (line.length >= 4 + mapWidth * mapHeight) {
                for (int i = 0; i < mapWidth; i++) {
                    for (int j = 0; j < mapHeight; j++) {
                        intMap[i][j] = Integer.parseInt(line[i * mapHeight + j + 2]);
                    }
                }
                for (int i = 2 + mapWidth * mapHeight; i + 1 < line.length; i += 2) {
                    startingTile.add(new int[]{Integer.parseInt(line[i]), Integer.parseInt(line[i + 1])});
                }
            } else {
                System.out.println("Cannot load the map : file length is wrong");
            }
        } else {
            System.out.println("Cannot load the map : file (almost) empty");
        }
        Map map = new Map(intMap, textureSize);
        map.setStartTile(startingTile);
        return map;
    }
    
    private static String loadTextFile(String address){
        String text = "";
        try {
            BufferedReader file = new BufferedReader (new FileReader(address));
            text = file.readLine();
            file.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unreadable file");
        }
        return text;
    }
    
    public static Map loadResourceMap(int i, int textureSize){
        String text = "";
        try {//Z means: "The end of the input but for the final terminator, if any"
            text = new java.util.Scanner(new File(Tools.class.getResource("/resources/maps/map"+i+".txt").toURI()),"UTF8").useDelimiter("\\Z").next();
        } catch (URISyntaxException | FileNotFoundException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }
        return stringToMap(text, textureSize);
    }
    
    public static void mapToTextFile(Map map, String address){
        int[][] intMap = map.getMap();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(address));
            writer.write("" + map.getMapWidth() + " " + map.getMapHeight());
            for (int i = 0; i < map.getMapWidth(); i++) {
                for (int j = 0; j < map.getMapHeight(); j++) {
                    writer.write(" " + intMap[i][j]);
                }
            }
            for (int[] startTile : map.getStartTile()) {
                writer.write(" " + startTile[0] + " " + startTile[1]);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static boolean playerCanCross(double x, double y, int objectWidth, int objectHeight, Map map) {
        boolean playerCanCross = !map.getTile(x,y).blocksPlayers() 
                                    && !map.getTile(x + objectWidth, y).blocksPlayers() 
                                    && !map.getTile(x, y + objectHeight).blocksPlayers() 
                                    && !map.getTile(x + objectWidth, y + objectHeight).blocksPlayers();
        return playerCanCross;
    }
    
    public static boolean isPlayerHit(Player player, Bullet bullet){
        boolean test = bullet.posX < player.getPosX() + player.getPlayerWidth()
                        && bullet.posX + bullet.getBallWidth() > player.getPosX()
                        && bullet.posY < player.getPosY() + player.getPlayerHeight()
                        && bullet.posY + bullet.getBallHeight() > player.getPosY();
        return test;
    }
    
    public static void playRandomSoundFromList(ArrayList<SoundPlayer> list){
        list.get(ThreadLocalRandom.current().nextInt(0, list.size())).play();
    }
    
    public static int getNumberOfAvailableTeams(Player player, ArrayList<Player> otherPlayersList){ // returns the number of teams + 1, except if player is alone in the team with biggest id, then return number of teams
        int numberOfAvailableTeams;
        ArrayList<Integer> teamIds = new ArrayList<>();
        teamIds.add(player.getTeamId());
        boolean playerIsAloneInTeam = true;
        boolean playerTeamIsMax = true;
        for (Player otherPlayer : otherPlayersList){
            if(otherPlayer.getTeamId()==player.getTeamId()){
                playerIsAloneInTeam = false;
            } else if(otherPlayer.getTeamId()>player.getTeamId()){
                playerTeamIsMax = false;
            }
            if(!teamIds.contains((Integer)otherPlayer.getTeamId())){
                teamIds.add(otherPlayer.getTeamId());
            }
        }
        if(playerIsAloneInTeam && teamIds.size()==2){
            numberOfAvailableTeams = -1;
        } else if(playerIsAloneInTeam && playerTeamIsMax){
            numberOfAvailableTeams = teamIds.size();
        } else {
            numberOfAvailableTeams = teamIds.size()+1;
        }
        
        return numberOfAvailableTeams;
    }
}
