package callofmuty;

public class GameMode {
    
    private int id;
    private boolean[] Options; // 0 : No guns, 1 : Bouncing balls, 2 : Bonus Items, 3 : Fast mode
    private int gunGestion;
    private int Team;
    private String description;
    
    public static final int DEFAULT=0,ROYAL=1,TEAM_MODE=2, // game mode
                RANDOM =0, ALWAYSON = 1, // gun generation
                NO_TEAMS = 0, TEAMS = 1, // teams
                NUMBER_OF_OPTIONS = 4;
    private static final double FAST_MODE_MULTIPLIER = 1.7;
    private static final int NUMBER_OF_BOUNCES = 1;
    
    private static String DEFAULT_DESCRIPTION = "Players randomly get guns.\n\nYour goal : be the last one standing !",
            ROYAL_DESCRIPTION = "Everyone has guns, all the time.\n\nShoot everywhere, kill everyone !",
            TEAM_DESCRIPTION = "This time, you will have teammates to help you in your cleaning task";
    
    public GameMode(){
        Options= new boolean[] {false,false,false,false};
        setId(DEFAULT);
    }

    public void setId(int id) {
        this.id = id;
        switch (this.id) {
            case DEFAULT:
                gunGestion = RANDOM;
                Team = NO_TEAMS;
                description = DEFAULT_DESCRIPTION;
                break;
            case ROYAL:
                gunGestion = ALWAYSON;
                Team = NO_TEAMS;
                description = ROYAL_DESCRIPTION;
                break;
            case TEAM_MODE:
                gunGestion = ALWAYSON;
                Team = TEAMS;
                description = TEAM_DESCRIPTION;

                break;
        }
    }

    public int getId() {
        return this.id;
    }

    
    public String getName(){
        String name = "";
        switch(id){
            case DEFAULT:
                name = "Default mode";
                break;
            case ROYAL:
                name = "Royal battle";
                break;
            case TEAM_MODE:
                name = "Team battle";
                break;
        }
        return name;
    }
    
    public int getNumberOfBounces(){
        int numberOfBounces;
        if(Options[1]){
            numberOfBounces = NUMBER_OF_BOUNCES;
        } else {
            numberOfBounces = 0;
        }
        return numberOfBounces;
    }
    
    public String getDescription(){
        return description;
    }

    public int getGunGestion() {
        return this.gunGestion;
    }

    public boolean getOption(int i) {
        return this.Options[i];
    }

    public void setOption(int i, boolean option) {
        Options[i] = option;
    }

    public int getTeam() {
        return Team;
    }


    public double getTimerMultiplier() {
        double multiplier;
        if(Options[3]){
            multiplier = FAST_MODE_MULTIPLIER;
        } else {
            multiplier = 1;
        }
        return multiplier;
    }

    public String getOptionName(int i) {
        String name = "";
        switch(i){
            case 0:
                name = "Default map";
                break;
            case 1:
                name = "Bouncing balls";
                break;
            case 2:
                name = "Bonus items";
                break;
            case 3:
                name = "Fast mode !";
                break;
        }
        return name;
    }
}
