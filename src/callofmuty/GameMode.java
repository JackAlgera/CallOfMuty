/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package callofmuty;
    
import java.io.IOException;
import javazoom.jl.decoder.JavaLayerException;

/**
 *
 * @author tlaurend
 */
public class GameMode {
    
    private int id;
    private boolean[] Options; //Op 0 Suggested map, Op 1 Rubberballs, Op 2 Active Items, Op 3 Modefast
    private int gunGestion;
    private int Team;
    
    public static final int DEFAULT=0,ROYAL=1,TEAM=2,
                RANDOM =0, ALWAYSON = 1,
                ALLVSALL = 0, ALLVSONE = 1, TEAMVSTEAM = 2;
    
    public GameMode(){
        Options= new boolean[] {false,false,false,false};
        setId(DEFAULT);
    }

    public void setId(int id) {
        this.id = id;
        switch (this.id) {
            case DEFAULT:
                gunGestion = RANDOM;
                Team = ALLVSALL;
            break;
            case ROYAL:
                gunGestion = ALWAYSON;
                Team = ALLVSALL;
            break;
            case TEAM:
                gunGestion = ALWAYSON;
                Team = TEAMVSTEAM;
            break;
        }
    }
    
    public int getId() {
        return this.id;
    }
    public int getGunGestion() {
        return this.gunGestion;
    }    
    public boolean getOption(int i) {
        return this.Options[i];
    }
    public int getTeam(){
        return this.Team;
    }
}
