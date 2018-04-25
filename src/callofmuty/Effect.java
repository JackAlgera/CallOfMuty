/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package callofmuty;

import static com.oracle.jrockit.jfr.FlightRecorder.isActive;
import java.io.IOException;
import javazoom.jl.decoder.JavaLayerException;

/**
 *
 * @author cfache
 */
public class Effect {
    
    public int id;
    public double timeDuration;
    
    public Effect(int id, double timeDuration){
        this.id=id;
        this.timeDuration=timeDuration;
    }
            
    
    public void burning(Player player1,double dT) throws IOException, JavaLayerException{
       double health=player1.getPlayerHealth();
       health=health-1*dT;
       player1.setHealth(health);
                   }
    public void healing(Player player1,double dT) throws JavaLayerException, IOException{
       double health=player1.getPlayerHealth();
       health=health+20*dT;
       player1.setHealth(health);
        
    }
    public void speedBoost(Player player1,double dT){
        double [] speed= player1.getSpeed();
        speed[0]=speed[0]+0.3*dT;
        speed[1]=speed[1]+0.3*dT;
        player1.setSpeed(speed);
    }
    public void speedReduction(Player player1,double dT){
        double [] speed= player1.getSpeed();
        
        speed[0]=speed[0]-speed[0]/(2*dT);
        speed[1]=speed[1]-speed[1]/(2*dT);
        player1.setSpeed(speed);
    }
    public void stun(Player player1){
        double [] speed= player1.getSpeed();
        speed[0]=0;
        speed[1]=0;
        player1.setSpeed(speed); 
    }
    
   

    public void update(Player player1, double dT) throws IOException, JavaLayerException{
        timeDuration = timeDuration - dT;
        
        if(this.timeDuration>0){
            
            if(this.id==0){
                burning(player1,dT);
            }
            if(this.id==1){
                healing(player1,dT);
            }
            if(this.id==2){
                healing(player1,dT);
            }
            if(this.id==3){
               healing(player1,dT);
            }
            if(this.id==4){
               speedBoost(player1,dT);
            }
            if(this.id==5){
                speedReduction(player1,dT);
            }
            if(this.id==6){
                stun(player1);            
            }
        
         }
        if(dT==0){
            if(this.id==0){
                burning(player1,1);
            }
            if(this.id==1){
                healing(player1,1);
            }
            if(this.id==2){
                healing(player1,1);
            }
            if(this.id==3){
                healing(player1,1);
            }
            if(this.id==4){
                speedBoost(player1,1);
            }
            if(this.id==5){
                speedReduction(player1,1);
            }
            if(this.id==6){
                stun(player1);            
            }
        }
        
    }
    public boolean endEffect(Effect effect1){
        boolean endOfTime=false;
        if(effect1.timeDuration==0){
             endOfTime= true ;
        }
        return endOfTime;
    }

}

