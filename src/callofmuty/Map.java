package callofmuty;

import java.awt.Graphics2D;

public class Map{
    
    private Tile[][] map;
    private int mapWidth,mapHeight;
    
    public Map(Tile[][] map){
        this.map=map;
        mapHeight=map.length;
        mapWidth=map[0].length;
    }
    
    public Map(int[][] intMap, int tileSize){
        mapHeight=intMap.length;
        mapWidth=intMap[0].length;
        map = new Tile[mapWidth][mapHeight];
        for (int i = 0 ; i<mapWidth ; i++){
            for (int j = 0; j<mapHeight; j++){
                switch(intMap[j][i]){
                    case 1:
                        map[i][j] = new Tile(i*tileSize,j*tileSize,tileSize,TileType.DIRT);
                        break;
                    case 2:
                        map[i][j] = new Tile(i*tileSize,j*tileSize,tileSize,TileType.ROCK);
                        break;
                    default:
                        map[i][j] = new Tile(i*tileSize,j*tileSize,tileSize,TileType.GRASS);
                }
                    
            }
        }
    }
    
    public Map(int mapWidth, int mapHeight, int tileSize){
        this.mapHeight = mapHeight;
        this.mapWidth = mapWidth;
        map = new Tile[mapWidth][mapHeight];
        for (int i = 1 ; i<mapWidth-1 ; i++){
            for (int j = 1; j<mapHeight-1; j++){
                map[i][j] = new Tile(i*tileSize,j*tileSize,tileSize,TileType.GRASS);
            }
        }
        for (int i = 0 ; i<mapWidth ; i++){
            map[i][0] = new Tile(i*tileSize,0,tileSize,TileType.ROCK);
            map[i][mapHeight-1] = new Tile(i*tileSize,(mapHeight-1)*tileSize,tileSize,TileType.ROCK);
        }
        for (int j = 1 ; j<mapHeight-1 ; j++){
            map[0][j] = new Tile(0,j*tileSize,tileSize,TileType.ROCK);
            map[mapWidth-1][j] = new Tile((mapWidth-1)*tileSize,j*tileSize,tileSize,TileType.ROCK);
        }
    }
    
    public void draw(Graphics2D g2d){
        int x = map.length, y = map[0].length;
        for (int i = 0 ; i<x ; i++){
            for (int j = 0; j<y; j++){
                map[i][j].draw(g2d);
            }
        }
    }
    
    public Tile getTile(int x, int y){
        int tileSize = map[0][0].getSize();
        return map[x/tileSize][y/tileSize];
    }
    
}
