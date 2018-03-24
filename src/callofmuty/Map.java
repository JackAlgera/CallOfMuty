package callofmuty;

import java.awt.Graphics2D;

public class Map{
    
    private int[][] map;
    private int mapWidth,mapHeight, textureSize;
    private static TileType grass = new TileType(true,1,1);
    private static TileType dirt = new TileType(true,1,1);
    private static TileType woodt= new TileType(false,1,12), woodb= new TileType(false,3,12), woodl= new TileType(false,2,11), woodr= new TileType(false,2,13), woodtl= new TileType(false,1,11), woodbl= new TileType(false,3,11), woodtr= new TileType(false,1,13), woodbr= new TileType(false,3,13);
    private static TileType box = new TileType(false,2,6);

    
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
        for (int i = 1 ; i<mapWidth-1 ; i++){
            map[i][0] = 1;
        }
        for (int i = 1 ; i<mapWidth-1 ; i++){
            map[i][mapHeight-1] = 2;
        }
        for (int j = 1 ; j<mapHeight-1 ; j++){
            map[0][j] = 3;
        }
        for (int j = 1 ; j<mapHeight-1 ; j++){
             map[mapWidth-1][j] = 4;
        }
        map[0][0] = 5;
        map[0][mapHeight-1] = 6;
        map[mapWidth-1][0] = 7;
        map[mapWidth-1][mapHeight-1] = 8;
        map[4][1] = 9 ;map[4][2] = 9;map[1][4] = 9;map[2][4] = 9;map[4][6] = 9;map[4][7] = 9;
        
    }
    
    public void draw(Graphics2D g2d){
        for (int i = 0 ; i<mapHeight ; i++){
            for (int j = 0; j<mapWidth; j++){
                switch(map[j][i]){
                    case 1:
                        g2d.drawImage(woodt.getImage(),j*textureSize, i*textureSize, textureSize, textureSize, null);
                        break;
                    case 2:
                        g2d.drawImage(woodb.getImage(), j*textureSize, i*textureSize, textureSize, textureSize, null);
                        break;
                    case 3:
                        g2d.drawImage(dirt.getImage(), j*textureSize, i*textureSize, textureSize, textureSize, null);
                        g2d.drawImage(woodl.getImage(), j*textureSize, i*textureSize, textureSize, textureSize, null);
                        break;
                    case 4:
                        g2d.drawImage(dirt.getImage(), j*textureSize, i*textureSize, textureSize, textureSize, null);
                        g2d.drawImage(woodr.getImage(), j*textureSize, i*textureSize, textureSize, textureSize, null);
                        break;
                    case 5:
                        g2d.drawImage(woodtl.getImage(), j*textureSize, i*textureSize, textureSize, textureSize, null);
                        break;
                    case 6:
                        g2d.drawImage(woodbl.getImage(), j*textureSize, i*textureSize, textureSize, textureSize, null);
                        break;
                    case 7:                        
                        g2d.drawImage(woodtr.getImage(), j*textureSize, i*textureSize, textureSize, textureSize, null);
                        break;
                    case 8:
                        g2d.drawImage(woodbr.getImage(), j*textureSize, i*textureSize, textureSize, textureSize, null);
                        break;                                        
                    case 9:
                        g2d.drawImage(dirt.getImage(), j*textureSize, i*textureSize, textureSize, textureSize, null);
                        g2d.drawImage(box.getImage(), j * textureSize, i * textureSize, textureSize, textureSize, null);
                        break;
                    default:
                        g2d.drawImage(dirt.getImage(), j*textureSize, i*textureSize, textureSize, textureSize, null);
}
            }
        }
    }
    
    public TileType getTile(double x, double y){
        TileType tile;
        switch (map[(int)x/textureSize][(int)y/textureSize]){
            case 1:
                tile = woodt;
                break;
            case 2:
                tile = woodb;
                break;
            case 3:
                tile = woodl;
                break;
            case 4:
                tile = woodr;
                break;
            case 5:
                tile = woodtl;
                break;
            case 6:
                tile = woodbl;
                break;
            case 7:
                tile = woodtr;
                break;
            case 8:
                tile = woodbr;
                break;
            case 9:
                tile = box;
                break;
            default :
                tile = dirt;
        }
        return tile;
    }
    
    public boolean pathIsCrossable(double x, double y, int playerWidth, int playerHeight){ // checks if a player is only on crossable tiles
        boolean pathIsCrossable  = getTile(x,y).isCrossable() && getTile(x + playerWidth, y).isCrossable() && getTile(x, y + playerHeight).isCrossable() && getTile(x + playerWidth, y + playerHeight).isCrossable();
        return pathIsCrossable;
    }
    
}
