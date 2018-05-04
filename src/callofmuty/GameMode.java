package callofmuty;

public class GameMode {
    
    private int id;
    private boolean[] Options; //Op 0 Suggested map, Op 1 Rubberballs, Op 2 Active Items, Op 3 Modefast
    private int gunGestion;
    private int Team;
    
    public static final int DEFAULT=0,ROYAL=1,TEAM=2, ALONE=3,
                RANDOM =0, ALWAYSON = 1,
                ALLVSALL = 0, ALLVSONE = 1, TEAMVSTEAM = 2;
    
    public GameMode(){
        Options= new boolean[] {false,false,false,false};
        setId(ROYAL);
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
            case ALONE:
                gunGestion = ALWAYSON;
                Team = ALLVSONE;
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
