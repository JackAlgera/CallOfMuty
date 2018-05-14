package callofmuty;

public class Effect {
    
    public final static int NO_EFFECT = 0, HEALING = 6, FASTER = 1,
                    BURNING = 2, SLOWED = 3, STUNED = 4, FALL_TO_DEATH = 5;
    
    private int id;
    private double value;
    private long timeLeft, duration; // in ms
    
    // Values in Health/second (Hurting, healing) or % (Speed)
    
    public Effect(){
        this.id = NO_EFFECT;
    }
    
    public Effect(int id, long duration, double value){
        this.id=id;
        this.duration = duration;
        timeLeft=duration;
        this.value = value;
    }

    public boolean update (long dT, Player player) { // Returns true if this effect is still active
        timeLeft -= dT;
        if (timeLeft > 0) {
            switch (id) {
                case HEALING:
                    double correctedValue = Math.min(Player.maxHealth-player.getPlayerHealth(), dT*value/1000);
                    player.hurtSelf(-correctedValue); // Player can't heal above max health (would cause issues with health bar)
                    break;
                case FASTER:
                case SLOWED:
                    double[] formerSpeed = player.getSpeed();
                    player.setSpeed(new double[]{formerSpeed[0] * value, formerSpeed[1] * value});
                    break;
                case BURNING:
                    player.hurtSelf(dT * value / 1000);
                    break;
                case FALL_TO_DEATH:
                    player.dieByFall();
                    timeLeft = 0;
            }
        }
        return timeLeft>0;
    }

    void resetDuration() {
        timeLeft = duration;
    }
    
    public int getId(){
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Effect other = (Effect) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }
    
    
}

