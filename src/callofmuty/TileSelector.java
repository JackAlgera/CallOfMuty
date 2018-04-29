package callofmuty;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class TileSelector extends Map {
    
    private static int TILES_PER_ROW = 10;
    
    int selectedTile;
    int selectedRow;
    
    public TileSelector(int textureSize){
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
        super.setDrawingParameters(91,13, textureSize*getMapWidth(), textureSize*getMapHeight());
    }
    
    @Override
    public void setTile(int i, int j, int tileType){
        super.getMap()[i][j] = tileType;
    }
    
    @Override
    public void draw(Graphics2D g2d, boolean setStartingTile){
        int newXTextureSize = getDrawWidth()/getMapWidth(), newYTextureSize = getDrawHeight()/getMapHeight();
        for (int i = 0; i < getMapWidth(); i++) {
            if (selectedRow*TILES_PER_ROW+i < NUMBER_OF_TILETYPES) {
                getTile(i, selectedRow).draw(g2d, getxPos() + i * newXTextureSize, getyPos(), newXTextureSize, newYTextureSize);
            }
        }
        if (!setStartingTile) { // do not draw the selection rectangle when setting the starting tile
            g2d.setStroke(new BasicStroke(5));
            g2d.setColor(Color.black);
            g2d.drawRect(getxPos() + selectedTile%TILES_PER_ROW * newXTextureSize, getyPos(), newXTextureSize, newYTextureSize);
        }
    }
    
    @Override
    public int[] clickedTile(int clickedX, int clickedY){
        int tileType = -1; // -1 means that no tile was clicked on
        int i = (clickedX-getxPos()) * getMapWidth()/getDrawWidth();
        int j = (clickedY-getyPos()) * getMapHeight()/getDrawHeight();
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
