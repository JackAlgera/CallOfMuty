package callofmuty;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class Gun {
    
    public static final int NO_GUN = 0, PISTOL = 1;
    private static final BufferedImage pistolImage = Tools.selectTile(Tools.gunTileset, 1, 1);
    
    private int ammunition, id;
    private Image image;
    private double damage, reloadTime, lastShotTimeStamp;
    
    public Gun(){
        ammunition = 0;
        id = NO_GUN;
        lastShotTimeStamp = System.currentTimeMillis();
    }
    
    public boolean isEmpty(){
        return ammunition==0;
    }
    
    public double getDamage(){
        return damage;
    }
    
    public void setId(int id){
        this.id = id;
        switch(id){
            case PISTOL:
                ammunition = 30;
                image = pistolImage;
                reloadTime = 50; //in milliseconds
                damage = 22;
        }
    }
    
    public int getId(){
        return id;
    }
    
    public void draw(Graphics2D g2d, Player player){
        switch(id){
            case NO_GUN: //draw nothing
                break;
            default:
                g2d.drawImage(image, (int) player.getPosX()+15, (int) player.getPosY(), image.getWidth(null)*2, image.getHeight(null)*2, null);
        }
        
    }
    
    public boolean shoot(boolean unlimitedBullets){ // if gun can shoot, shoots and returns true, else returns false
        boolean test = (unlimitedBullets || ammunition>0) && System.currentTimeMillis()-reloadTime>=lastShotTimeStamp;
        if (test){
            if(!unlimitedBullets){
                ammunition--;
            }
            lastShotTimeStamp = System.currentTimeMillis();
            if(ammunition==0){
                setId(NO_GUN);
            }
        }
        return test;
    }
    
}
