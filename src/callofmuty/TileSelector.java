package callofmuty;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class TileSelector extends Map {
    
    int selectedTile;
    
    public TileSelector(int textureSize){
        super(new int[10][1], textureSize);
        for (int i=0; i<getMapWidth(); i++){
            setTile(i,0,i);
        }
        selectedTile = 0;
        super.setDrawingParameters(91,13, textureSize*getMapWidth(), textureSize*getMapHeight());
    }
    
    public void draw(Graphics2D g2d, boolean setStartingTile){
        super.draw(g2d, false);
        int newXTextureSize = getDrawWidth()/getMapWidth(), newYTextureSize = getDrawHeight()/getMapHeight();
        if (!setStartingTile) { // do not draw the selection rectangle when setting the starting tile
            g2d.setStroke(new BasicStroke(5));
            g2d.setColor(Color.black);
            g2d.drawRect(getxPos() + selectedTile * newXTextureSize, getyPos(), newXTextureSize, newYTextureSize-1);
        }
    }
    
    @Override
    public int[] clickedTile(int drawnX, int drawnY){
        int[] clickedTile = super.clickedTile(drawnX, drawnY);
        if (clickedTile[0]>-1){
            selectedTile = clickedTile[0];
        }
        return clickedTile;
    }

    public int getSelectedTile() {
        return selectedTile;
    }
    
}
