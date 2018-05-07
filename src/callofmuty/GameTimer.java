package callofmuty;

public class GameTimer {
    
    private long LastUpdate, totalTime;
    private double multiplier;
    private boolean isPaused;
    
    public GameTimer(){
        totalTime = 0;
        isPaused = false;
        multiplier = 1.0;
        LastUpdate = System.currentTimeMillis();
    }
    
    public long update(){
        long dT =(long) System.currentTimeMillis()-LastUpdate;
        LastUpdate += dT;
        totalTime += dT*multiplier;
        return (long)(dT*multiplier);
    }
    
    public long getDT(boolean useMultiplier){
        long dT = System.currentTimeMillis()-LastUpdate;
        if(useMultiplier){
            dT *=multiplier;
        }
        return dT;
    }
    
    public void pause(){
        isPaused=true;
    } 
    
    public void unpause(){
        isPaused=false;
        LastUpdate=System.currentTimeMillis();
    }
    
    public void setMultiplier(double multiplier){
        this.multiplier = multiplier;
    }
}
