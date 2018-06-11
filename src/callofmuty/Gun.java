package callofmuty;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class Gun {
    
    public static final int NO_GUN = 0, PISTOL = 1, UZI = 2, SNIPER = 3, SHOTGUN = 4, AK = 5, MAGNUM = 6, MITRAILLEUSE = 7, LEGENDARY_WEAPON = 8, FLAMETHROWER = 9;
    private static final BufferedImage pistolImage = Tools.selectWeaponTile(Tools.WeaponTileset, 1, 1, 1),
            uziImage = Tools.selectWeaponTile(Tools.WeaponTileset, 2, 3, 1),
            sniperImage = Tools.selectWeaponTile(Tools.WeaponTileset, 3, 1, 2),
            shotgunImage = Tools.selectWeaponTile(Tools.WeaponTileset, 1, 7, 2),
            akImage = Tools.selectWeaponTile(Tools.WeaponTileset, 1, 3, 2),
            magnumImage = Tools.selectWeaponTile(Tools.WeaponTileset, 2, 1, 1),
            mitrailleuseImage = Tools.selectWeaponTile(Tools.WeaponTileset,2, 5, 2),
            legendaryWeaponImage = Tools.selectWeaponTile(Tools.WeaponTileset,2, 9, 1),
            flamethrowerWeaponImage = Tools.selectWeaponTile(Tools.WeaponTileset,3, 7, 2);
            
    private int ammunition,stockAmmo, id, startingAmmo, xImage, yImage, tailleGun, bulletType;
    private Image image;
    private double damage, rateOfFire, lastShotTimeStamp, reloadTime, bulletSpeed, initialRateOfFire, maxRange, bulletSpread;
    private SoundPlayer gunSound, uziSound, sniperSound,shotgunSound, legendaryWeaponSound,chickenThrowingSound;
    
    public Gun(){
        ammunition = 0;
        bulletType = 0;
        id = NO_GUN;
        lastShotTimeStamp = System.currentTimeMillis();
        gunSound = new SoundPlayer("shootingSound.mp3", false);
        uziSound = new SoundPlayer("uziSound.mp3", false);
        sniperSound = new SoundPlayer("sniperSound.mp3", false);
        shotgunSound = new SoundPlayer("shotgunSound.mp3", false);
        legendaryWeaponSound = new SoundPlayer("legendaryWeaponSound.mp3", false);
        chickenThrowingSound = new SoundPlayer("legendaryWeaponSound.mp3", false);
    }
    
    public double getDamage(){
        return damage;
    }
    
    public int getBulletType(){
        int type;
        if(id==LEGENDARY_WEAPON && stockAmmo == 0 && ammunition==1){
            type = Bullet.CHICKEN;
        } else {
            type = bulletType;
        }
        return type;
    }
    
    public void setId(int id, int numberOfCartridges){
        this.id = id;
        
        switch(this.id){
            case PISTOL:
                ammunition = 6;
                image = pistolImage;
                rateOfFire = 500; //in milliseconds
                damage = 15;
                reloadTime = 1000;
                bulletSpeed = 1.0;
                maxRange = 550;
                bulletSpread = 0.139;
                xImage = 1;
                yImage = 1;
                tailleGun = 1;
                bulletType = Bullet.NORMAL;
                break;
                
            case UZI:
                ammunition = 20;
                image = uziImage;
                rateOfFire = 150;
                damage = 5;
                reloadTime = 1000;
                bulletSpeed = 0.8;
                maxRange = 450;
                bulletSpread = 0.174;
                xImage = 2;
                yImage = 3;
                tailleGun = 1;
                bulletType = Bullet.NORMAL;
                break;
                
            case SNIPER:
                ammunition = 4;
                image = sniperImage;
                rateOfFire = 1000;
                damage = 35;
                reloadTime = 1000;
                bulletSpeed = 1.8;
                maxRange = 800;
                bulletSpread = 0.0017;
                xImage = 3;
                yImage = 1;
                tailleGun = 2;
                bulletType = Bullet.NORMAL;
                break;
                
            case SHOTGUN:
                ammunition = 5;
                image = shotgunImage;
                rateOfFire = 650;
                damage = 12;
                reloadTime = 1000;
                bulletSpeed = 1.2;
                maxRange = 400;
                bulletSpread = 0.017;
                xImage = 1;
                yImage = 7;
                tailleGun = 2;
                bulletType = Bullet.NORMAL;
                break;
                
            case AK:
                ammunition = 14;
                image = akImage;
                rateOfFire = 280;
                damage = 10;
                reloadTime = 1000;
                bulletSpeed = 1.0;
                maxRange = 500;
                bulletSpread = 0.037;
                xImage = 1;
                yImage = 3;
                tailleGun = 2;
                bulletType = Bullet.NORMAL;
                break;
                
            case MAGNUM:
                ammunition = 6;
                image = magnumImage;
                rateOfFire = 700;
                damage = 25;
                reloadTime = 1000;
                bulletSpeed = 1.8;
                maxRange = 700;
                bulletSpread = 0.034;
                xImage = 2;
                yImage = 1;
                tailleGun = 1;
                bulletType = Bullet.NORMAL;
                break;
                
            case MITRAILLEUSE:
                ammunition = 20;
                image = mitrailleuseImage;
                rateOfFire = 200;
                damage = 7;
                reloadTime = 1250;
                bulletSpeed = 1.0;
                maxRange = 550;
                bulletSpread = 0.139;
                xImage = 2;
                yImage = 5;
                tailleGun = 2;
                bulletType = Bullet.NORMAL;
                break;
            
            case LEGENDARY_WEAPON:
                ammunition = 5;
                image = legendaryWeaponImage;
                rateOfFire = 700;
                damage = 35;
                reloadTime = 1000;
                bulletSpeed = 1.8;
                maxRange = 700;
                bulletSpread = 0.034;
                xImage = 2;
                yImage = 9;
                tailleGun = 1;
                bulletType = Bullet.EGG;
                break;
                
            case FLAMETHROWER:
                ammunition = 50;
                image = flamethrowerWeaponImage;
                rateOfFire = 40;
                damage = 2;
                reloadTime = 1000;
                bulletSpeed = 1.8;
                maxRange = 200;
                bulletSpread = 0.200;
                xImage = 3;
                yImage = 7;
                tailleGun = 2;
                break;
                
            case NO_GUN:
                ammunition = 0;
                image = null;
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
    
    public double getMaxRange() {
        return maxRange;
    }
    
    public double getBulletSpread(){
        return bulletSpread;
    }
    
    public int getAmmunition(){
        return this.ammunition;
    }
    
    public int getStockAmmo(){
        return this.stockAmmo;
    }
    
    public double getReloadTime(){
        return this.reloadTime;
    }
    
    public void draw(Graphics2D g2d, Player player, GamePanel game){
        switch(id){
            case NO_GUN: //draw nothing
                break;
            default:
                double zoomRatio = game.getZoomRatio()*game.getScreenSizeZoomRatio();
                g2d.drawImage(image, game.getGameX()+(int)(getGunPositionX(player)*zoomRatio), (int)((player.getPosY()+18)*zoomRatio), (int)(image.getWidth(null)*zoomRatio), (int)(image.getHeight(null)*zoomRatio), null);
        }
        
    }
    
    public boolean shoot(boolean muteShootingSound){ // if gun can shoot, shoots and returns true, else returns false
        boolean test = (ammunition>0) && System.currentTimeMillis()-rateOfFire>=lastShotTimeStamp;
        if (test){
            if (id != SHOTGUN){
                ammunition--;
                rateOfFire=initialRateOfFire;
                lastShotTimeStamp = System.currentTimeMillis();
                if (!muteShootingSound){
                    playShootingSound();
                }
                if(ammunition==0){
                    if(stockAmmo !=0 ){
                        stockAmmo-=startingAmmo;
                        ammunition=startingAmmo;
                        rateOfFire+=reloadTime;
                    } else {
                        setId(NO_GUN, 0);
                    }
                }
            } else {
                ammunition--;
                rateOfFire=initialRateOfFire;
                lastShotTimeStamp = System.currentTimeMillis();
                if (!muteShootingSound){
                    playShootingSound();
                }
                if(ammunition==0){
                    if(stockAmmo !=0 ){
                        stockAmmo-=startingAmmo;
                        ammunition=startingAmmo;
                        rateOfFire+=reloadTime;
                    }
                }
            }
        }
        return test;
    }
    
    public void changeGunDirection(int k){
        if (k == 1){
            this.image = Tools.selectWeaponTile(Tools.WeaponTileset, this.xImage, this.yImage +k*this.tailleGun, this.tailleGun);
        } else {
            this.image = Tools.selectWeaponTile(Tools.WeaponTileset, this.xImage, this.yImage , this.tailleGun);
        }
    }
    
    public void playShootingSound() {
        switch (id) {
            case NO_GUN:
                break;
            case UZI:
                uziSound.play();
                break;
            case SNIPER:
                sniperSound.play();
                break;
            case SHOTGUN:
                shotgunSound.play();
                break;
            case LEGENDARY_WEAPON:
                if(ammunition==0 && stockAmmo==0){
                    chickenThrowingSound.play();
                } else {
                    legendaryWeaponSound.play();
                }
                break;
            default:
                gunSound.play();
            }
    }
    
    public int getGunPositionX(Player player)
    {
        int gunPosIncrease = 0;
        switch(player.getCurrentImage())
        {
            case 2:
                gunPosIncrease = 2;
                break;
            case 3:
                gunPosIncrease = 4;
                break;
            case 4:
                gunPosIncrease = 5;
                break;
            case 5:
                gunPosIncrease = 5;
                break;
            case 6:
                gunPosIncrease = 3;
                break;
            case 7:
                gunPosIncrease = 1;
                break;
        }
        
        int gunPos = (int)player.getPosX() - 6;
        if(player.getFacedDirection() == 2) //Facing right
        {
            switch(id)
            {
                case PISTOL :
                    gunPos = (int)player.getPosX() + 3;
                    break;
                case UZI :
                    gunPos = (int)player.getPosX() - 2;
                    break;
                case SNIPER :
                    gunPos = (int)player.getPosX() - 2;
                    break;
                case SHOTGUN :
                    gunPos = (int)player.getPosX() - 9;
                    break;
                case AK :
                    gunPos = (int)player.getPosX() - 11;
                    break;
                case MAGNUM :

                    break;
                case MITRAILLEUSE :
                    gunPos = (int)player.getPosX() - 11;
                    break;
            }
        }
        else
        {
            gunPosIncrease = -gunPosIncrease;
            switch(id)
            {
                case PISTOL :
                    gunPos = (int)player.getPosX() + 2;
                    break;
                case UZI :
                    gunPos = (int)player.getPosX() + 1;
                    break;
                case SNIPER :
                    gunPos = (int)player.getPosX() - 14;
                    break;
                case SHOTGUN :
                    gunPos = (int)player.getPosX() - 13;
                    break;
                case AK :
                    gunPos = (int)player.getPosX() - 14;
                    break;
                case MAGNUM :
                    break;
                case FLAMETHROWER :
                    gunPos = (int)player.getPosX() - 17;
                    break;
                case MITRAILLEUSE :
                    gunPos = (int)player.getPosX() - 14;
                    break;
            }
        }
        return (gunPos + gunPosIncrease);
    }

    public Image getImage() {
        Image imageToReturn = null;
        if(id!=NO_GUN && (ammunition != 0 || stockAmmo!=0)){
            imageToReturn = image;
        }
        return imageToReturn;
    }

    public String getAmmoString() {
        String text = "";
        if(id!=NO_GUN && (ammunition != 0 || stockAmmo!=0)){
            text = ""+ammunition+"/"+stockAmmo;
        }
        return text;
    }
    
}
