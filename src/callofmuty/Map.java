package callofmuty;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Map{
    
    private int[][] map;
    private int mapWidth,mapHeight, textureSize;
    private static TileType grass = new TileType(true,1,1);
    private static TileType dirt = new TileType(true,1,18);
    private static TileType rock= new TileType(false,1,12); 
    
// Grass = 0 ; Rock = 1 ; Dirt = 2;
    
    public Map(int[][] map, int textureSize){
        this.map=map;
        mapHeight=map.length;
        mapWidth=map[0].length;
        this.textureSize = textureSize;
    }
    
    public int getMapHeight(){
        return mapHeight;
    }
    
    public int getMapWidth(){
        return mapWidth;
    }
    
    public int getTextureSize(){
        return textureSize;
    }
    
    public Map(int mapWidth, int mapHeight, int tileSize){
        this.mapHeight = mapHeight;
        this.mapWidth = mapWidth;
        this.textureSize = tileSize;
        
        map = new int[mapWidth][mapHeight];
        for (int i = 1 ; i<mapWidth-1 ; i++){
            for (int j = 1; j<mapHeight-1; j++){
                map[i][j] = 0;
            }
        }
        for (int i = 0 ; i<mapWidth ; i++){
            map[i][0] = 1;
            map[i][mapHeight-1] = 1;
        }
        for (int j = 1 ; j<mapHeight-1 ; j++){
            map[0][j] = 1;
            map[mapWidth-1][j] = 1;
        }
    }
    
    public void draw(Graphics2D g2d){
        for (int i = 0 ; i<mapHeight ; i++){
            for (int j = 0; j<mapWidth; j++){
                switch(map[j][i]){
                    case 1:
                        g2d.drawImage(rock.getImage(),j*textureSize, i*textureSize, textureSize, textureSize, null);
                        break;
                    case 2:
                        g2d.drawImage(dirt.getImage(), j*textureSize, i*textureSize, textureSize, textureSize, null);
                        break;
                    default:
                        g2d.drawImage(grass.getImage(), j*textureSize, i*textureSize, textureSize, textureSize, null);
}
            }
        }
    }
    
    public TileType getTile(double x, double y){
        TileType tile;
        switch (map[(int)x/textureSize][(int)y/textureSize]){
            case 1:
                tile = rock;
                break;
            case 2:
                tile = dirt;
                break;
            default :
                tile = grass;
        }
        return tile;
    }
    
    public boolean pathIsCrossable(double x, double y, int playerWidth, int playerHeight){ // checks if a player is only on crossable tiles
        boolean pathIsCrossable  = getTile(x,y).isCrossable() && getTile(x + playerWidth, y).isCrossable() && getTile(x, y + playerHeight).isCrossable() && getTile(x + playerWidth, y + playerHeight).isCrossable();
        return pathIsCrossable;
    }
    
}
