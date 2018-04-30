package callofmuty;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.JOptionPane;

public class Map{
    
    private static TileType dirt = new TileType(false, false,1,1,new Effect()),
            // map borders
            woodt= new TileType(true, true,1,12,new Effect()), woodb= new TileType(true, true,3,12,new Effect()), woodl= new TileType(true, true,2,11,1,1,new Effect()), woodr= new TileType(true, true,2,13,1,1,new Effect()), woodtl= new TileType(true, true,1,11,new Effect()), woodbl= new TileType(true, true,3,11,new Effect()), woodtr= new TileType(true, true,1,13,new Effect()), woodbr= new TileType(true, true,3,13,new Effect()),
            // obstacles
            box = new TileType(true, true,2,6,1,1,new Effect()),
            // bad effects
            hole = new TileType(false, false,3,7,new Effect(Effect.FALL_TO_DEATH, 1000000, 0)), mud = new TileType(false, false, 3,6,new Effect(Effect.SLOWED, 300, 0.5)), hotGround = new TileType(false, false, 4,6,new Effect(Effect.BURNING, 500, 10)),
            // other
            teleporter = new TileType(false, false, 5,6, new Effect());
    public static int TELEPORTER_ID = 13, NUMBER_OF_TILETYPES = 14;
        
    private int[][] map;
    private int mapWidth,mapHeight, textureSize, xPos, yPos, drawWidth, drawHeight;
    private ArrayList<int[]> startTile;
    private ArrayList<int[]> teleporters;
    
    public Map(int[][] map, int textureSize){
        this.map=map;
        mapWidth=map.length;
        mapHeight=map[0].length;
        this.textureSize = textureSize;
        xPos=0;
        yPos=0;
        drawWidth = mapWidth*textureSize;
        drawHeight = mapHeight*textureSize;
        teleporters = new ArrayList<>();
        for (int i = 0; i < mapWidth; i++){
            for (int j = 0; j < mapHeight; j++){
                if (map[i][j]==TELEPORTER_ID){
                    teleporters.add(new int[]{i,j});
                }
            }
        }
        startTile = new ArrayList<>();
    }
    
    public void addStartTile(int[] newStartTile){
        if (newStartTile.length==2){
            if(!getTile(newStartTile[0],newStartTile[1]).blocksPlayers()){
                if (startTileIndex(newStartTile)>-1){
                    if (startTile.size()>1){
                        startTile.remove(startTileIndex(newStartTile));
                    } else{
                        JOptionPane.showMessageDialog(null, "There must be at least one starting tile");
                    }
                } else {
                    startTile.add(newStartTile);
                }
            }
        }
    }
    
    public int startTileIndex(int[] tile){
        int index = -1;
        int i = 0;
        boolean tileFound = false;
        while(!tileFound && i<startTile.size()){
            tileFound = (tile[0]==startTile.get(i)[0]) && (tile[1]==startTile.get(i)[1]);
            i++;
        }
        if(tileFound){
            index = i-1;
        }
        return index;
    }
    
    public int teleportersIndex(int[] tile){
        int index = -1;
        int i = 0;
        boolean tileFound = false;
        while(!tileFound && i<teleporters.size()){
            tileFound = (tile[0]==teleporters.get(i)[0]) && (tile[1]==teleporters.get(i)[1]);
            i++;
        }
        if(tileFound){
            index = i-1;
        }
        return index;
    }
    
    public ArrayList<int[]> getStartTile(){
        return startTile;
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
    
    public void setDrawingParameters(int xPos, int yPos, int drawWidth, int drawHeight){
        this.xPos = xPos;
        this.yPos = yPos;
        this.drawHeight = drawHeight;
        this.drawWidth = drawWidth;
    }
    
    public void setDrawingParameters(int gameState){
        switch (gameState){
            case GamePanel.MAIN_MENU:
                setDrawingParameters(530, 185, 462, 260);
                break;
            case GamePanel.MAP_EDITOR:
                setDrawingParameters(100,100,mapWidth*textureSize - 100, mapHeight*textureSize-100);
                break;
            case GamePanel.IN_GAME:
                setDrawingParameters(0, 0, mapWidth*textureSize, mapHeight*textureSize);
                break;
            case GamePanel.PRE_GAME:
                setDrawingParameters(585, 330, 425, 230);
        }
    }

    public void setStartTile(ArrayList<int[]> startTile) {
        this.startTile = startTile;
    }
    
    public Map(int mapWidth, int mapHeight, int tileSize){
        this.mapHeight = mapHeight;
        this.mapWidth = mapWidth;
        this.textureSize = tileSize;
        xPos=0;
        yPos=0;
        drawWidth = mapWidth*textureSize;
        drawHeight = mapHeight*textureSize;
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
        map[4][1] = 13 ;map[4][2] = 11;map[1][4] = 10;map[2][4] = 12;map[4][6] = 9;map[4][7] = 13;
        startTile = new ArrayList<>();
        startTile.add(new int[]{1,1});
        teleporters = new ArrayList<>();
        teleporters.add(new int[]{4,1});
        teleporters.add(new int[]{4,7});
    }
    
    public TileType getTile(int i, int j){
        return getTile(map[i][j]);
    }
    
    public TileType getTile(int tileType){
        TileType tile;
        switch (tileType){
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
            case 10:
                tile = hole;
                break;
            case 11:
                tile = mud;
                break;
            case 12: 
                tile = hotGround;
                break;
            case 13:
                tile = teleporter;
                break;
            default :
                tile = dirt;
        }
        return tile;
    }
    
    public int getTileId(double x, double y){
        int i = (int)x/textureSize;
        int j = (int)y/textureSize;
        
        if (i > mapWidth-1)
            i = mapWidth-1;
        if (i < 0)
            i = 0;
        if (j > mapHeight-1)
            j = mapHeight-1;
        if (j < 0)
            j = 0;
        return map[i][j];
    }
    
    public TileType getTile(double x, double y){
        int i = (int)x/textureSize;
        int j = (int)y/textureSize;
        
        if (i > mapWidth-1)
            i = mapWidth-1;
        if (i < 0)
            i = 0;
        if (j > mapHeight-1)
            j = mapHeight-1;
        if (j < 0)
            j = 0;
        return getTile(i,j);
    }
    
    public int[][] getMap(){
        return map;
    }
    
    public void draw(Graphics2D g2d, boolean drawStartingTile){
        int newXTextureSize = drawWidth/mapWidth, newYTextureSize = drawHeight/mapHeight;
        for (int i = 0 ; i<mapWidth ; i++){
            for (int j = 0; j<mapHeight; j++){
                getTile(map[i][j]).draw(g2d, xPos+i*newXTextureSize, yPos+j*newYTextureSize, newXTextureSize, newYTextureSize);
            }
        }
        if(drawStartingTile){
            g2d.setStroke(new BasicStroke(5));
            g2d.setColor(Color.lightGray);
            for (int[] startingTile : startTile){
                g2d.drawRect(xPos+startingTile[0]*newXTextureSize, yPos+startingTile[1]*newYTextureSize, newXTextureSize, newYTextureSize);
            }
        }
    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public int getDrawWidth() {
        return drawWidth;
    }

    public int getDrawHeight() {
        return drawHeight;
    }
    
    public void setTile(int i, int j, int tileType){
        if (startTileIndex(new int[]{i,j})>-1){
            if (!getTile(tileType).blocksPlayers()){
                if (map[i][j]==TELEPORTER_ID){
                    teleporters.remove(teleportersIndex(new int[]{i,j}));
                }
                map[i][j] = tileType;
                if(map[i][j]==TELEPORTER_ID){
                    teleporters.add(new int[]{i,j});
                }
            }
        } else {
            if (map[i][j] == TELEPORTER_ID) {
                teleporters.remove(teleportersIndex(new int[]{i, j}));
            }
            map[i][j] = tileType;
            if (map[i][j] == TELEPORTER_ID) {
                teleporters.add(new int[]{i, j});
            }
        }
    }
    
    public int[] clickedTile(int clickedX, int clickedY){
        int tileType = -1; // -1 means that no tile was clicked on
        int i = (clickedX-xPos) * mapWidth/drawWidth;
        int j = (clickedY-yPos) * mapHeight/drawHeight;
        if (xPos <= clickedX && clickedX<xPos + drawWidth && yPos<=clickedY && clickedY < yPos + drawHeight){
            tileType = map[i][j];
        }
        return new int[]{tileType, i, j};
    }
    
    // checks if player is on a teleporter, returns destination if he is, else returns {-1,-1}
    public int[] teleporterDestination(double[] xValues, double[] yValues){
        int teleporterIndex;
        boolean isOnTeleporter = false;
        int[] destination = new int[]{-1,-1};
        int valuesIndex = 0;
        int i,j;
        while(!isOnTeleporter && valuesIndex < xValues.length){
            i = ((int)xValues[valuesIndex]-xPos) * mapWidth/drawWidth;
            j = ((int)yValues[valuesIndex]-yPos) * mapHeight/drawHeight;
            isOnTeleporter = map[i][j]==TELEPORTER_ID;
            valuesIndex++;
        }
        if(isOnTeleporter){
            valuesIndex--;
            i = ((int)xValues[valuesIndex]-xPos) * mapWidth/drawWidth;
            j = ((int)yValues[valuesIndex]-yPos) * mapHeight/drawHeight;
            teleporterIndex = teleportersIndex(new int[]{i,j});
            int[] entryTeleporter = teleporters.remove(teleporterIndex);
            int destinationIndex = ThreadLocalRandom.current().nextInt(0, teleporters.size());
            int[] destinationTile = teleporters.get(destinationIndex);
            destination[0] = destinationTile[0]*textureSize;
            destination[1] = destinationTile[1]*textureSize;
            teleporters.add(entryTeleporter);
        }
        return destination;
    }
        
}
