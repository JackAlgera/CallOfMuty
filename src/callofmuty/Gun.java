package callofmuty;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class Gun {
    
    public static final int NO_GUN = 0, PISTOL = 1;
    //private static BufferedImage pistolImage = ;
    
    private int ammunition, id;
    private Image image;
    private double damage, reloadTime, lastShotTimeStamp;
    
    public Gun(){
        ammunition = 0;
        id = NO_GUN;
    }
    
    public boolean isEmpty(){
        return ammunition==0;
    }
    
    public void setId(int id){
        this.id = id;
        switch(id){
            case PISTOL:
                ammunition = 3;
                //image = pistolImage
                reloadTime = 1000; //in milliseconds
                damage = 15;
                lastShotTimeStamp = System.currentTimeMillis()-reloadTime;
        }
    }
    
    public void draw(Graphics2D g2d, Player player){
        switch(id){
            case NO_GUN: //draw nothing
                break;
            default:
                g2d.drawImage(image, (int) player.getPosX(), (int) player.getPosY(), image.getWidth(null), image.getHeight(null), null);
        }
        
    }
    
    public boolean shoot(){ // if gun can shoot, shoots and returns true, else returns false
        boolean test = ammunition>0 && System.currentTimeMillis()-reloadTime>=lastShotTimeStamp;
        if (test){
            ammunition--;
            lastShotTimeStamp = System.currentTimeMillis();
            if(ammunition==0){
                setId(NO_GUN);
            }
        }
        return test;
    }
    
}
