/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package callofmuty;

import java.awt.Image;
/**
 *
 * @author sbonnet
 */
public class Guns {
    
    private int ammunition;
    private int damage;
    private Bullet bullet;
    //private int idGun;
    //private boolean isEmpty;
    private Image GunImage;
    private double speed;
    
    public Guns(int idGun){
        
    }
    
    public boolean isEmpty(){
        if (this.ammunition==0){
            return true;
        } 
        return false;
    }
    
    public void reloadGun(){
        
    }
    
}
