package callofmuty;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Player {

    public static Image normalHealthBar = Tools.selectTile(Tools.hudTileset, 1, 2),
            lowHealthBar = Tools.selectTile(Tools.hudTileset, 1, 1);
    public static double maxHealth = 100.0;
    private static double rollSpeedMultiplier = 3;
    private static long timeBetweenHurtSounds = 300, rollTime = 150; // in milliseconds
    private static int initialBulletNumber = 10;
    public static int PLAYING = 1,DEAD = 2;
    
    private int playerId, playerWidth, playerHeight, facedDirection, playerState, teamId;
    private Image image, hpBar;
    private double maxSpeed, accelerationValue, posX, posY, wantedX, wantedY;
    private double[] speed, acceleration;
    private int[] directionOfTravel;
    private double health, timeSinceLastHurtSound;
    private boolean isDead, muteSounds, justTeleported, isRolling;  
    private int[] skin;
    private String name;
    public ArrayList<Image> animationImages = new ArrayList<>();
    public Animation playerAnimation;
    private ArrayList<Player> hurtPlayers;
    private ArrayList<Effect> effects = new ArrayList<>();
    private long currentRollTime;
    
    private ArrayList<Bullet> bulletList, destroyedBullets;
    private Gun gun;
    private SoundPlayer fallingSoundPlayer;
    private ArrayList<SoundPlayer>  hurtSoundPlayer, dyingSoundPlayer, tauntSoundPlayer;
        
    public Player(double x,double y){
        muteSounds = false;
        timeSinceLastHurtSound = System.currentTimeMillis();
        facedDirection = 0;
        this.posX=x;
        this.posY=y;
        this.playerWidth=35;
        this.playerHeight=55;
        skin = new int[2];
        this.skin[0]= 1;
        this.skin[1]= 1;
        teamId= 0;
        justTeleported = false;
        isRolling = false;
        image=Tools.selectTile(Tools.playerTileset, skin[0], skin[1]);
        currentRollTime = 0;
        destroyedBullets = new ArrayList<>();
        
        this.playerAnimation = new Animation(160,7,4,6,1,0); // en ms
        for (int i=0; i<playerAnimation.getNumberOfImagesY(); i++){
            for (int j=0; j<playerAnimation.getNumberOfImagesX(); j++){
                animationImages.add(Tools.selectTile(Tools.PlayerTilesetAnimated, i+1, j+1));
            }
        }
        playerAnimation.setRow(3);
        
        maxSpeed = 0.3; //in pixel per ms
        speed = new double[2];
        speed[0] = 0.0; //x speed
        speed[1] = 0.0; // y speed
        acceleration = new double[2];
        acceleration[0] = 0.0; // x acceleration
        acceleration[1] = 0.0; // y acceleration
        directionOfTravel = new int[2];
        directionOfTravel[0] = 0; // =-1 -> wants to go left, =+1 -> wants to go right, =0 -> stands still on x axis
        directionOfTravel[1] = 0; // =-1 -> wants to go up, =+1 -> wants to go down, =0 -> stands still on y axis
        this.accelerationValue = 0.002;
        isDead = false;
        health=maxHealth;
        hpBar = normalHealthBar;
        name = "Username";
        playerState = 0; 
        hurtPlayers = new ArrayList<>();
        bulletList = new ArrayList<>();
        gun = new Gun();
        fillSoundPlayers();
    }

    public void fillSoundPlayers(){
        hurtSoundPlayer = new ArrayList<>();
        hurtSoundPlayer.add(new SoundPlayer("hurtSound.mp3", false));
        hurtSoundPlayer.add(new SoundPlayer("hurtSound2.mp3", false));
        hurtSoundPlayer.add(new SoundPlayer("hurtSound3.mp3", false));
        dyingSoundPlayer = new ArrayList<>();
        dyingSoundPlayer.add(new SoundPlayer("dyingSound.mp3", false));
        dyingSoundPlayer.add(new SoundPlayer("dyingSound2.mp3", false));
        fallingSoundPlayer = new SoundPlayer("fallingSound.mp3", false);
        tauntSoundPlayer = new ArrayList<>();
        tauntSoundPlayer.add(new SoundPlayer("taunt.mp3", false));
        tauntSoundPlayer.add(new SoundPlayer("taunt2.mp3", false));
        tauntSoundPlayer.add(new SoundPlayer("taunt3.mp3", false));
        tauntSoundPlayer.add(new SoundPlayer("taunt4.mp3", false));
    }
    
    public void reset(Map map, boolean muteSounds) {
        setHealth(Player.maxHealth);
        setMuteSounds(muteSounds);
        setGunId(Gun.NO_GUN);
        resetEffects();
        setPosition(map);
        resetHurtPlayers();
    }
    
    public void taunt(){
        if (!isDead && !muteSounds){
            tauntSoundPlayer.get(ThreadLocalRandom.current().nextInt(0, tauntSoundPlayer.size())).play();
        }
    }
    
    public void setMuteSounds(Boolean muteSounds){
        this.muteSounds = muteSounds;
    }
    
    public boolean getMuteSounds(){
        return muteSounds;
    }
    
    public String getName(){
        return name;
    }
    
    public void setGunId(int gunId){
        gun.setId(gunId, 0);
    }
    
    public int getGunId(){
        return gun.getId();
    }
    
    public ArrayList<Player> getHurtPlayers(){
        return hurtPlayers;
    }
    

    public void addPlayer(SQLManager sql){
        bulletList = new ArrayList<>();
        for (int i = 1; i<=initialBulletNumber; i++){ //bulletId starts at 1, 0 is SQL's "null"
            bulletList.add(new Bullet(playerId, i));
        }
        sql.addPlayer(this);
        sql.addBulletList(bulletList);
    }
    
    public void resetHurtPlayers(){
        hurtPlayers = new ArrayList<>();
    }
    
    public void resetEffects(){
        effects = new ArrayList<>();
    }
    
    public void setPlayerState(int playerState) {
        this.playerState = playerState;
    }

    public void setHealth(double health) {
        double formerHealth = this.health;
        this.health = health;
        
        if (health <= 0) {
            isDead = true;
            if (!muteSounds) {
                dyingSoundPlayer.get(ThreadLocalRandom.current().nextInt(0, dyingSoundPlayer.size())).play();
            }
        } else {
            isDead = false;
            if (formerHealth > health && !muteSounds && System.currentTimeMillis()-timeBetweenHurtSounds > timeSinceLastHurtSound) {
                hurtSoundPlayer.get(ThreadLocalRandom.current().nextInt(0, hurtSoundPlayer.size())).play();
                timeSinceLastHurtSound = System.currentTimeMillis();
            }
        }
        if (health < 0.15*maxHealth){
            hpBar = lowHealthBar;
        } else {
            hpBar = normalHealthBar;
        }
    }
    
    public ArrayList<Bullet> getBulletList() {
        return bulletList;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPlayerWidth() {
        return playerWidth;
    }

    public int getPlayerHeight() {
        return playerHeight;
    }
    
    public void setSkin(int skinIndex){
        skin[1]=skinIndex;
        image=Tools.selectTile(Tools.playerTileset, skin[0], skin[1]);
    }
    
    public int getSkinIndex(){
        return skin[1];
    }
    
    public void move(long dT){
        speed[0] += acceleration[0]*dT;
        speed[1] += acceleration[1]*dT;
        posX += speed[0]*dT;
        posY += speed[1]*dT;
    }
    
    public void draw(Graphics2D g) {
        if (!isDead) {
            g.drawImage(animationImages.get(playerAnimation.getCurrentImage()), (int) posX + playerWidth / 2 - image.getWidth(null), (int) posY + playerHeight / 2 - image.getHeight(null), image.getWidth(null) * 2, image.getHeight(null) * 2, null);
            //g.drawImage(image, (int) posX + playerWidth / 2 - image.getWidth(null), (int) posY + playerHeight / 2 - image.getHeight(null), image.getWidth(null) * 2, image.getHeight(null) * 2, null);
            g.drawImage(hpBar, (int) posX + playerWidth / 2 - image.getWidth(null), (int) posY + playerHeight / 2 - image.getHeight(null) - 12, image.getWidth(null) * 2, image.getHeight(null) * 2, null);
            gun.draw(g, this);
            g.setColor(Color.RED);
            g.fillRect((int) posX + playerWidth / 2 - image.getWidth(null) + 12, (int) posY + playerHeight / 2 - image.getHeight(null) - 6, (int) ((int) (image.getWidth(null) * 2 - 24) * health / maxHealth), 2);
        }
    }
    
    public void drawBullets(Graphics2D g,int texturesize) {
        for (Bullet bullet : bulletList) {
            bullet.draw(g, texturesize);
        }
        for (Bullet bullet : destroyedBullets){
            bullet.draw(g, texturesize);
        }
    }
    
    public void update(long dT, Map map){
        if(!isDead){
            
            // Update animation
            this.playerAnimation.update(dT);
            
            // Update bullets
            
            for(int i=0; i<destroyedBullets.size(); i++){
                destroyedBullets.get(i).updateBulletAnimation(dT);
                if(destroyedBullets.get(i).endOfAnimation()){
                    destroyedBullets.remove(i);
                }
            }

            //Calculate speed vector
            if (!isRolling) {
                speed[0] += acceleration[0] * dT;
                speed[1] += acceleration[1] * dT;
                // Deceleration
                if (directionOfTravel[0] == 1 && acceleration[0] < 0 && speed[0] < 0) {
                    speed[0] = 0;
                    acceleration[0] = 0;
                }
                if (directionOfTravel[0] == -1 && acceleration[0] > 0 && speed[0] > 0) {
                    speed[0] = 0;
                    acceleration[0] = 0;
                }
                if (directionOfTravel[1] == 1 && acceleration[1] < 0 && speed[1] < 0) {
                    speed[1] = 0;
                    acceleration[1] = 0;
                }
                if (directionOfTravel[1] == -1 && acceleration[1] > 0 && speed[1] > 0) {
                    speed[1] = 0;
                    acceleration[1] = 0;
                }

                double speedNorm = Math.sqrt(Math.pow(speed[0], 2) + Math.pow(speed[1], 2));
                double angle;

                if (speedNorm == 0) {
                    angle = 0;
                } else {
                    angle = Math.acos(speed[0] / speedNorm); //Angle between speed vector and [1,0]+
                }
                if (speedNorm > maxSpeed) {

                    if (directionOfTravel[1] == -1) {
                        angle = -angle;
                    }

                    speed[0] = maxSpeed * Math.cos(angle);
                    speed[1] = maxSpeed * Math.sin(angle);
                }
            } else {
                currentRollTime += dT;
                if (currentRollTime > rollTime){
                    isRolling = false;
                    speed[0] /= rollSpeedMultiplier;
                    speed[1] /= rollSpeedMultiplier;
                }
            }

            // update & activate effects
            int i = 0;
            while(i<effects.size()){
                if (effects.get(i).update(dT, this)){
                    i++;
                } else {
                    effects.remove(i);
                }
            }
            
            // Check tile effects
            if(!isRolling){
                updateTileEffects(map);
            }
            
            // check if player is still in the map
            wantedX = posX + speed[0]*dT;
            wantedY = posY + speed[1]*dT;
            if (wantedX<0){
                wantedX = 0;
                speed[0] = 0;
            }
            if (wantedX+playerWidth>map.getMapWidth()*map.getTextureSize()){
                wantedX = map.getMapWidth()*map.getTextureSize()-playerWidth;
                speed[0] = 0;
            }
            if (wantedY<0){
                wantedY = posY;
                speed[1] = 0;
            }
            if(wantedY+playerHeight>map.getMapHeight()*map.getTextureSize()){
                wantedY = map.getMapHeight()*map.getTextureSize()-playerHeight;
                speed[1] = 0;
            }
            // check if able to move in given direction (not trying to cross uncrossable tile)
            if(!Tools.playerCanCross(wantedX, wantedY, playerWidth, playerHeight, map)){ // test if the tile the player is going to is crossable
                if (Tools.playerCanCross(posX, wantedY, playerWidth, playerHeight, map)){ //try to block x movement
                    wantedX = posX;
                    speed[0] = 0;
                } else {
                    if (Tools.playerCanCross(wantedX, posY, playerWidth, playerHeight,map)){ // try to block y movement
                        wantedY = posY;
                        speed[1] = 0;
                    } else { // block movement
                        wantedX = posX;
                        speed[0] = 0;
                        wantedY = posY;
                        speed[1] = 0;
                    }
                }
            }
            if(!isRolling){
                checkTeleports(map);
            }
            posX = wantedX;
            posY = wantedY;
            
                        
            if(Math.abs(speed[0]) <= 0.0001 && Math.abs(speed[1]) <= 0.0001){
                playerAnimation.setIsIdle(true);
            }
            else{
                playerAnimation.setIsIdle(false);
            }
        }
    }
    
    public void updateTileEffects(Map map){
        double[] xValues = new double[]{posX+playerWidth*0.05, posX+playerWidth*0.05, posX+playerWidth*0.95, posX+playerWidth*0.95};
        double[] yValues = new double[]{posY+playerHeight*0.65, posY+playerHeight, posY+playerHeight*0.65, posY+playerHeight};
        Effect effect;
        for (int i = 0; i<4; i++ ) {
            effect = map.getTile(xValues[i], yValues[i]).getEffect();
            if (effect.getId()!=Effect.NO_EFFECT) {
                addEffect(effect);
            }
        }
    }
    
    public void checkTeleports(Map map){
        double[] xValues = new double[]{posX+playerWidth*0.05, posX+playerWidth*0.05, posX+playerWidth*0.95, posX+playerWidth*0.95};
        double[] yValues = new double[]{posY+playerHeight*0.65, posY+playerHeight, posY+playerHeight*0.65, posY+playerHeight};
        int[] destination = map.teleporterDestination(xValues, yValues); // returns {-1, -1} if not on a teleporter, else returns new position
        if(justTeleported){
            justTeleported = destination[0]!=-1;
        } else if(destination[0]!=-1){
            wantedX = destination[0];
            wantedY = destination[1];
            justTeleported = true;
        }
    }
    
    public void addEffect(Effect newEffect){
        int i = effects.indexOf(newEffect);
        if (i>-1){
            effects.get(i).resetDuration();
        } else {
            newEffect.resetDuration();
            effects.add(newEffect);
        }
    }

    public void setFacedDirection(int facedDirection) {
        this.facedDirection = facedDirection;
    }

    public Animation getPlayerAnimation() {
        return playerAnimation;
    }
    
    public void setDirectionOfTravel(int axis, int direction)
    {
        this.directionOfTravel[axis] = direction;
    }
    
    public void reverseAcceleration(int axis)
    {
        this.acceleration[axis] = -this.acceleration[axis];
    }
    
    public void setAcceleration(int axis, double accelerationSign)
    {
        this.acceleration[axis] = accelerationSign*this.accelerationValue;
    }
    
    public void setPosition(double[] newPosition){
        if(newPosition.length==2){
            posX = newPosition[0];
            posY = newPosition[1];
        }
    }
    
    public void setPosition(Map map){
        int index = ThreadLocalRandom.current().nextInt(0, map.getStartTile().size());
        posX = map.getStartTile().get(index)[0]*map.getTextureSize();
        posY = map.getStartTile().get(index)[1]*map.getTextureSize();
    }
    
    public void setPlayerId(int playerId)
    {
        this.playerId = playerId;
    }
    
    public int getPlayerId()
    {
        return this.playerId;
    }
    
    public double getPosX(){
        return this.posX ;
    }
    
    public double getPosY(){
        return this.posY;
    }
    
    public boolean isDead(){
        return this.isDead;
    }
    
    public boolean isTeamkilled(ArrayList <Player> otherPlayerList, boolean otherTeam){
        for(int i=0; i <otherPlayerList.size();i++){
            if(otherTeam^(otherPlayerList.get(i).getTeamId()==this.getTeamId()) && !otherPlayerList.get(i).isDead){
                return false;
            }
        }
        return true;
    }
    
    public double getPlayerHealth(){
        return this.health;
    }       
    
    public int getTeamId(){
        return this.teamId;
    }
    
    public void setTeamId(int i){
        this.teamId=i;
    }
    
    public void chooseSkin(int row, int column){
        this.skin[0]=row;
        this.skin[1]=column;
        this.image = Tools.selectTile(Tools.playerTileset, this.skin[0], this.skin[1]);
    }
    
    public void addBullet(double initPosX, double initPosY, double[] direction, double speed, SQLManager sql, double damage){
        if (!isDead) {
            boolean inactiveBulletFound = false;
            int bulletIndex = 0;
            while(bulletIndex < bulletList.size() && !inactiveBulletFound){
                inactiveBulletFound = !bulletList.get(bulletIndex).isActive();
                bulletIndex++;
            }
            if(!inactiveBulletFound){
                bulletList.add(new Bullet(initPosX, initPosY, direction, speed, playerId, bulletIndex+1, damage));
                bulletList.get(bulletIndex).setActive(true);
                sql.addBullet(bulletList.get(bulletIndex));
            } else {
                Bullet bullet = bulletList.get(bulletIndex-1);
                bullet.setActive(true);
                bullet.setSpeed(speed);
                bullet.setDirection(direction);
                bullet.setPosX(initPosX);
                bullet.setPosY(initPosY);
                bullet.setDamage(damage);
            }
        }
    }
    
    public Image getImage(){
        return image;
    }
    
    public void updateBulletList(long dT, Map map, ArrayList<Player> otherPlayersList){
        Bullet bullet;
        Player hurtPlayer;
        for (int i = 0; i<bulletList.size(); i++) {
            bullet = bulletList.get(i);
            if (bullet.isActive()) {
                bullet.update(dT);
                if (bullet.checkCollisionWithMap(map)) {
                    bullet.setActive(false);
                    destroyedBullets.add(new Bullet(bullet.getPosX(), bullet.getPosY()));
                    bullet.setDistanceTravelled(0);
                } else if(bullet.getDistanceTravelled()>gun.getDistanceMaxShoot()){
                    bullet.setActive(false);
                    destroyedBullets.add(new Bullet(bullet.getPosX(), bullet.getPosY()));
                    bullet.setDistanceTravelled(0);
                } else {
                    for (Player otherPlayer : otherPlayersList) {
                        if (Tools.isPlayerHit(otherPlayer, bullet) && !this.isFriend(otherPlayer)) {
                            bullet.setActive(false);
                            hurtPlayer = new Player(otherPlayer.getPlayerId());
                            hurtPlayer.setHealth(bullet.getDamage());
                            hurtPlayers.add(hurtPlayer);
                            bullet.setDistanceTravelled(0);
                        }
                    }
                    
                }
            }
        }
    }
    
    public void hurtSelf(double damage){ // Damage needs to go through SQL server to work properly, hence this method
        Player player = new Player(playerId);
        player.setMuteSounds(true);
        player.setHealth(damage);
        hurtPlayers.add(player);
    }
    
    public Player(int playerId){ //usefull constructor for SQL updates
        this.playerId = playerId;
        muteSounds = false;
        timeSinceLastHurtSound = System.currentTimeMillis()-timeBetweenHurtSounds;
    }
    
    public boolean isFriend(Player otherPlayer){
        return otherPlayer.teamId==teamId;
    }
    
    public void incrementId(){
        playerId++;
    }
    
    @Override
    public boolean equals(Object object) {
        boolean test = false;

        if (object != null && object instanceof Player) { // compare 2 Players by their playerId
            test = playerId == ((Player) object).getPlayerId();
        }
        return test;
    }


    public void generateGun(int numberOfPlayers, long gunGenerationTime, GameMode gameMode) {
        switch (gameMode.getGunGestion()) {
            case GameMode.RANDOM:
                if (gun.getId() == 0 && Math.random() < (double) gunGenerationTime / (1000 * numberOfPlayers * 4)) { // In average, one player gets a gun every 4 seconds
                    double gunRandom = Math.random();
                    int numberOfCartridges = Math.round((float) Math.random()); // player can get 0 or 1 cartridge
                    if (gunRandom < 0.20) {
                        gun.setId(Gun.PISTOL, numberOfCartridges);
                    } else if (gunRandom < 0.40) {
                        gun.setId(Gun.UZI, numberOfCartridges);
                    } else if (gunRandom < 0.47) {
                        gun.setId(Gun.SNIPER, numberOfCartridges);
                    } else if (gunRandom < 0.55) {
                        gun.setId(Gun.SHOTGUN, numberOfCartridges);
                    } else if (gunRandom < 0.70) {
                        gun.setId(Gun.AK, numberOfCartridges);
                    } else if (gunRandom < 0.85) {
                        gun.setId(Gun.MAGNUM, numberOfCartridges);
                    } else {
                        gun.setId(Gun.MITRAILLEUSE, numberOfCartridges);
                    }
                }
                break;
            case GameMode.ALWAYSON:
                if (gun.getId() == 0) { 
                    double gunRandom = Math.random();
                    int numberOfCartridges = Math.round((float) Math.random()); // player can get 0 or 1 cartridge
                    if (gunRandom < 0.20) {
                        gun.setId(Gun.PISTOL, numberOfCartridges);
                    } else if (gunRandom < 0.40) {
                        gun.setId(Gun.UZI, numberOfCartridges);
                    } else if (gunRandom < 0.47) {
                        gun.setId(Gun.SNIPER, numberOfCartridges);
                    } else if (gunRandom < 0.55) {
                        gun.setId(Gun.SHOTGUN, numberOfCartridges);
                    } else if (gunRandom < 0.70) {
                        gun.setId(Gun.AK, numberOfCartridges);
                    } else if (gunRandom < 0.85) {
                        gun.setId(Gun.MAGNUM, numberOfCartridges);
                    } else {
                        gun.setId(Gun.MITRAILLEUSE, numberOfCartridges);
                    }
                }
                break;

        }
    }
    
    public void shoot(double[] directionOfFire, SQLManager sql, boolean unlimitedBullets){
        if (gun.shoot(unlimitedBullets, muteSounds)){
            if (gun.getId()==400){ //spread shotgun in progress
                int spreadDir;
                double [] secondBullet = directionOfFire;
                double [] thirdBullet = directionOfFire;
                double dispersionShotgun = 0.523;
                double signe = Math.abs(directionOfFire[0])/directionOfFire[0];
                if (Math.random()<0.5){
                    spreadDir = 1;
                } else {
                    spreadDir = -1;
                }
                double angleTirRandom = Math.random()*spreadDir*gun.getBulletSpread();
                double Gamma = Math.atan(directionOfFire[1]/directionOfFire[0]);
                directionOfFire[0]=Math.cos(angleTirRandom+Gamma)*signe;
                directionOfFire[1]=Math.sin(angleTirRandom+Gamma)*signe;
                
                double alpha = Math.atan(directionOfFire[1]/directionOfFire[0]);
                
                secondBullet[0]=Math.cos(alpha+dispersionShotgun)*signe;
                secondBullet[1]=Math.cos(alpha+dispersionShotgun)*signe;
                thirdBullet[0]=Math.cos(alpha-dispersionShotgun)*signe;
                thirdBullet[1]=Math.cos(alpha-dispersionShotgun)*signe;
                addBullet(getPosX() + image.getWidth(null) / 4, getPosY() + image.getHeight(null) / 4, directionOfFire, gun.getBulletSpeed(), sql, gun.getDamage());
                addBullet(getPosX() + image.getWidth(null) / 4, getPosY() + image.getHeight(null) / 4, secondBullet, gun.getBulletSpeed(), sql, gun.getDamage());
                addBullet(getPosX() + image.getWidth(null) / 4, getPosY() + image.getHeight(null) / 4, thirdBullet, gun.getBulletSpeed(), sql, gun.getDamage());

            } else { //Fonction spreadBullet
                int spreadDir;
                double signe = Math.abs(directionOfFire[0])/directionOfFire[0];
                if (Math.random()<0.5){
                    spreadDir = 1;
                } else {
                    spreadDir = -1;
                }
                double angleTirRandom = Math.random()*spreadDir*gun.getBulletSpread();
                double Gamma = Math.atan(directionOfFire[1]/directionOfFire[0]);
                directionOfFire[0]=Math.cos(angleTirRandom+Gamma)*signe;
                directionOfFire[1]=Math.sin(angleTirRandom+Gamma)*signe;

                addBullet(getPosX() + image.getWidth(null) / 4, getPosY() + image.getHeight(null) / 4, directionOfFire, gun.getBulletSpeed(), sql, gun.getDamage());
                if(directionOfFire[0]<0){
                    gun.changeGunDirection(1);
                } else {
                    gun.changeGunDirection(0);
                }
            }
            
            
        }
    }

    public void playShootSound() {
        if(!muteSounds){
            gun.playShootingSound();
        }
    }
    
    public int getCurrentImage()
    {
        return playerAnimation.getCurrentImageValue();
    }
    public double[] getSpeed(){
        return speed;
    }
    public void setSpeed(double[] speed1){
        speed[0]=speed1[0];
        speed[1] =speed1[1];       
    }

    public void dieByFall() {
        if(!muteSounds){
            fallingSoundPlayer.play();
            setMuteSounds(true);
        }
        hurtSelf(health+1);
    }

    public void roll() {
        if(!isDead && !isRolling){
            currentRollTime = 0;
            isRolling = true;
            speed[0] *=rollSpeedMultiplier;
            speed[1] *=rollSpeedMultiplier;
        }
    }
}
