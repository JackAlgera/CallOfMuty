package callofmuty;

public class GameTimer {
    
    private long LastUpdate, totalTime;
    private float multiplier;
    private boolean isPaused;
    
    public GameTimer(){
        totalTime = 0;
        isPaused = false;
        multiplier = 1;
        LastUpdate = System.currentTimeMillis();
    }
    
    public long update(){
        long dT = System.currentTimeMillis()-LastUpdate;
        LastUpdate += dT;
        totalTime += dT;
        return dT;
    }
    
    public long getDT(){
        return System.currentTimeMillis()-LastUpdate;
    }
    
    public void pause(){
        isPaused=true;
    } 
    
    public void unpause(){
        isPaused=false;
        LastUpdate=System.currentTimeMillis();
    }
    
    
}
