package callofmuty;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javazoom.jl.decoder.JavaLayerException;

public class Gun {
    
    public static final int NO_GUN = 0, PISTOL = 1, UZI = 2, SNIPER = 3, SHOTGUN = 4, AK = 5, MAGNUM = 6, MITRAILLEUSE = 7;
    private static final BufferedImage pistolImage = Tools.selectWeaponTile(Tools.WeaponTileset, 1, 1, 1);
    private static final BufferedImage uziImage = Tools.selectWeaponTile(Tools.WeaponTileset, 2, 3, 1); 
    private static final BufferedImage sniperImage = Tools.selectWeaponTile(Tools.WeaponTileset, 3, 1, 2);  
    private static final BufferedImage shotgunImage = Tools.selectWeaponTile(Tools.WeaponTileset, 1, 7, 2); 
    private static final BufferedImage akImage = Tools.selectWeaponTile(Tools.WeaponTileset, 1, 3, 2); 
    private static final BufferedImage magnumImage = Tools.selectWeaponTile(Tools.WeaponTileset, 2, 1, 1);
    private static final BufferedImage mitrailleuseImage = Tools.selectWeaponTile(Tools.WeaponTileset,2, 5, 2);
    
    private int ammunition,stockAmmo, id, startingAmmo;
    private Image image;
    private double damage, rateOfFire, lastShotTimeStamp, reloadTime, bulletSpeed, initialRateOfFire, distanceMaxShoot;
    private SoundPlayer gunSound;
    
    public Gun(){
        ammunition = 0;
        id = NO_GUN;
        lastShotTimeStamp = System.currentTimeMillis();
    }
    
    public double getDamage(){
        return damage;
    }
    
    public void setId(int id, int numberOfCartridges)throws IOException, JavaLayerException{
        this.id = id;
        
        switch(this.id){
            case PISTOL:
                ammunition = 6;
                image = pistolImage;
                rateOfFire = 500; //in milliseconds
                damage = 15;
                reloadTime = 1000;
                bulletSpeed = 1.0;
                gunSound = new SoundPlayer("shootingSound.mp3", false);
                distanceMaxShoot = 550;
                break;
                
            case UZI:
                ammunition = 20;
                image = uziImage;
                rateOfFire = 150;
                damage = 5;
                reloadTime = 1000;
                bulletSpeed = 0.8;
                gunSound = new SoundPlayer("shootingSound.mp3", false);
                distanceMaxShoot = 450;
                break;
                
            case SNIPER:
                ammunition = 4;
                image = sniperImage;
                rateOfFire = 1000;
                damage = 35;
                reloadTime = 1000;
                bulletSpeed = 1.8;
                gunSound = new SoundPlayer("shootingSound.mp3", false);
                distanceMaxShoot = 800;
                break;
                
            case SHOTGUN:
                ammunition = 5;
                image = shotgunImage;
                rateOfFire = 650;
                damage = 22;
                reloadTime = 1000;
                bulletSpeed = 1.2;
                gunSound = new SoundPlayer("shootingSound.mp3", false);
                distanceMaxShoot = 400;
                break;
                
            case AK:
                ammunition = 14;
                image = akImage;
                rateOfFire = 280;
                damage = 10;
                reloadTime = 1000;
                bulletSpeed = 1.0;
                gunSound = new SoundPlayer("shootingSound.mp3", false);
                distanceMaxShoot = 500;
                break;
                
            case MAGNUM:
                ammunition = 6;
                image = magnumImage;
                rateOfFire = 700;
                damage = 25;
                reloadTime = 1000;
                bulletSpeed = 1.8;
                gunSound = new SoundPlayer("shootingSound.mp3", false);
                distanceMaxShoot = 700;
                break;
                
            case MITRAILLEUSE:
                ammunition = 20;
                image = mitrailleuseImage;
                rateOfFire = 200;
                damage = 7;
                reloadTime = 1250;
                bulletSpeed = 1.0;
                gunSound = new SoundPlayer("shootingSound.mp3", false);
                distanceMaxShoot = 550;
                break;
                
            case NO_GUN:
                ammunition = 0;       
        }
        stockAmmo = ammunition * numberOfCartridges;
        startingAmmo = ammunition;
        initialRateOfFire = rateOfFire;
    }
    
    public int getId(){
        return id;
    }
    
    public double getBulletSpeed(){
        return bulletSpeed;
    }
    
    public double getDistanceMaxShoot() {
        return distanceMaxShoot;
    }
    
    public void draw(Graphics2D g2d, Player player){
        switch(id){
            case NO_GUN: //draw nothing
                break;
            default:
                g2d.drawImage(image, (int) getGunPositionY(player), (int) player.getPosY() + 15, image.getWidth(null), image.getHeight(null), null);
        }
        
    }
    
    public boolean shoot(boolean unlimitedBullets, boolean muteShootingSound)throws IOException, JavaLayerException{ // if gun can shoot, shoots and returns true, else returns false
        boolean test = (unlimitedBullets || ammunition>0) && System.currentTimeMillis()-rateOfFire>=lastShotTimeStamp;
        if (test){
            if(!unlimitedBullets){
                ammunition--;
                rateOfFire=initialRateOfFire;
            }
            lastShotTimeStamp = System.currentTimeMillis();
            if(ammunition==0){
                if(stockAmmo !=0 ){
                    stockAmmo-=startingAmmo;
                    ammunition=startingAmmo;
                    rateOfFire+=reloadTime;
                } else {
                    setId(NO_GUN, 0);
                }
                
            }
            if (!muteShootingSound){
                playShootingSound();
            }
        }
        return test;
    }
    
    public void playShootingSound() {
        if (id != NO_GUN) {
            try {
                gunSound.play();
            } catch (FileNotFoundException | URISyntaxException ex) {
                Logger.getLogger(Gun.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JavaLayerException | IOException ex) {
                Logger.getLogger(Gun.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public int getGunPositionY(Player player)
    {
        int gunPos = (int)player.getPosX() - 6;
        switch(player.getCurrentImage())
        {
            case 1:
                gunPos += 2;
                break;
            case 2:
                gunPos += 4;
                break;
            case 3:
                gunPos += 6;
                break;
            case 4:
                gunPos += 6;
                break;
            case 5:
                gunPos += 2;
                break;
        }
        return gunPos;
    }
    
}
