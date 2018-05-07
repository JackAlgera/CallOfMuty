package callofmuty;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class TileSelector extends Map {
    
    private static int TILES_PER_ROW = 10;
    
    int selectedTile;
    int selectedRow;
    
    public TileSelector(int textureSize, int originalGameWidth, int originalGameHeight){
        super(new int[TILES_PER_ROW][NUMBER_OF_TILETYPES/TILES_PER_ROW + 1], textureSize);
        for (int j = 0; j<getMapHeight(); j++){
            for (int i=0; i<getMapWidth(); i++){
                if(TILES_PER_ROW*j+i < Map.NUMBER_OF_TILETYPES){
                    setTile(i,j,TILES_PER_ROW*j+i);
                } else {
                    setTile(i,j,-1);
                }
            }
        }
        selectedTile = 0;
        selectedRow = 0;
        super.setDrawingParameters(0.0895*originalGameWidth,0.0205*originalGameHeight, 0.63*originalGameWidth, 0.23*originalGameHeight);
    }
    
    @Override
    public void setTile(int i, int j, int tileType){
        super.getMap()[i][j] = tileType;
    }
    
    @Override
    public void draw(Graphics2D g2d, boolean setStartingTile, GamePanel game){
        double zoomRatio = game.getZoomRatio();
        int newXTextureSize = (int)(getDrawWidth()/getMapWidth()*zoomRatio), newYTextureSize = (int)(getDrawHeight()/getMapHeight()*zoomRatio);
        for (int i = 0; i < getMapWidth(); i++) {
            if (selectedRow*TILES_PER_ROW+i < NUMBER_OF_TILETYPES) {
                getTile(i, selectedRow).draw(g2d, game.getGameX()+(int)(getxPos()*zoomRatio) + i * newXTextureSize, (int)(getyPos()*zoomRatio), newXTextureSize, newYTextureSize);
            }
        }
        if (!setStartingTile) { // do not draw the selection rectangle when setting the starting tile
            g2d.setStroke(new BasicStroke(5));
            g2d.setColor(Color.black);
            g2d.drawRect(game.getGameX()+(int)(getxPos()*zoomRatio) + selectedTile%TILES_PER_ROW * newXTextureSize,(int) (getyPos()*zoomRatio), newXTextureSize, newYTextureSize);
        }
    }
    
    @Override
    public int[] clickedTile(int clickedX, int clickedY){
        int tileType = -1; // -1 means that no tile was clicked on
        int i = (int)((clickedX-getxPos()) * getMapWidth()/getDrawWidth());
        int j = (int)((clickedY-getyPos()) * getMapHeight()/getDrawHeight());
        if (getxPos() <= clickedX && clickedX<getxPos() + getDrawWidth() && getyPos()<=clickedY && clickedY < getyPos() + getDrawHeight()/getMapHeight()){
            tileType = getMap()[i][selectedRow];
        }
        if (tileType>-1){
            selectedTile = tileType;
        }
        return new int[]{tileType, i, j};
    }

    public int getSelectedTile() {
        return selectedTile;
    }
    
    public void changeSelectedRow(int sign){
        if (sign==1 && selectedRow+1<getMapHeight()){
            selectedRow++;
        } else if (sign==-1 && selectedRow>0){
            selectedRow--;
        }
    }
    
}
