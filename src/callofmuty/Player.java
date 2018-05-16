package callofmuty;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Player implements Comparable<Player>{

    public static Image normalHealthBar = Tools.selectTile(Tools.hudTileset, 1, 2),
            lowHealthBar = Tools.selectTile(Tools.hudTileset, 1, 1);
    public static double maxHealth = 100.0;
    private static double rollSpeedMultiplier = 3, meleeDamage = 25, feetHeight = 0.4;
    private static long timeBetweenHurtSounds = 300, timeBetweenMeleeAttacks= 1000, meleeAttacksDuration = 150, meleeRange = 25, rollTime = 150, timeBetweenTaunts = 1000, timeBetweenRolls = 1000; // in milliseconds
    private static int initialBulletNumber = 10, initialItemNumber = 3,MAX_NUMBER_OF_ITEMS = 5, playerWidth= 35, playerHeight = 55;
    public static int PLAYING = 1,DEAD = 2;
    
    private int playerId, facedDirection, playerState, teamId, lifeCounter;
    private Image hpBar;
    private double maxSpeed, accelerationValue, posX, posY, wantedX, wantedY; 
    private double[] speed, acceleration;
    private int[] directionOfTravel;
    private double health;
    private boolean isDead, muteSounds, justTeleported, isRolling, isTaunting, fellToDeath = false;
    private int skinId, numberOfSkins;
    private String name;
    public ArrayList<Image> animationImages = new ArrayList<>();
    
    public int imageWidth, imageHeight;
    
    public Animation playerAnimation;
    private ArrayList<Player> hurtPlayers;
    private ArrayList<Effect> effects = new ArrayList<>();
    private ArrayList<BonusItem> itemList = new ArrayList<>(), pickedItems = new ArrayList<>();
    private long lastTauntTimeStamp, lastMeleeAttackTimeStamp, timeSinceLastHurtSound, lastRollTimeStamp;
    
    private ArrayList<Bullet> bulletList, destroyedBullets;
    private Gun gun;
    private SoundPlayer fallingSoundPlayer;
    private ArrayList<SoundPlayer>  hurtSoundPlayer, dyingSoundPlayer, tauntSoundPlayer, teleportSoundPlayer;
        
    public Player(double x,double y){
        muteSounds = false;
        timeSinceLastHurtSound = System.currentTimeMillis();
        facedDirection = 2;
        this.posX=x;
        this.posY=y;
        this.skinId = 1;
        teamId= 0;
        justTeleported = false;
        isRolling = false;
        isTaunting = false;
        destroyedBullets = new ArrayList<>();
        lifeCounter = 5;
        
        this.playerAnimation = new Animation(Animation.PLAYER); // en ms
        
        for (int i=0; i<playerAnimation.getNumberOfImagesY(); i++){
            for (int j=0; j<playerAnimation.getNumberOfImagesX(); j++){
                animationImages.add(Tools.selectPlayerTile(Tools.PlayerTilesetAnimated, i+1, j+1));
            }
        }
        
        lastMeleeAttackTimeStamp = System.currentTimeMillis();
        lastRollTimeStamp = System.currentTimeMillis();
        imageHeight = animationImages.get(0).getHeight(null)/2; // 32x32
        imageWidth = animationImages.get(0).getWidth(null)/2;   // 32x32
        numberOfSkins = 3;
        playerAnimation.setRow((skinId - 1) * 4 + numberOfSkins);
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
        lastTauntTimeStamp = System.currentTimeMillis() - timeBetweenTaunts;
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
        teleportSoundPlayer = new ArrayList<>();
        teleportSoundPlayer.add(new SoundPlayer("teleportSound.mp3", false));
        teleportSoundPlayer.add(new SoundPlayer("teleportSound2.mp3", false));
    }
    
    public void reset(Map map, boolean muteSounds) {
        setHealth(Player.maxHealth);
        setMuteSounds(muteSounds);
        fellToDeath = false;
        setGunId(Gun.NO_GUN);
        resetEffects();
        pickedItems = new ArrayList<>();
        setPosition(map);
        resetHurtPlayers();
        speed = new double[]{0.0,0.0};
    }
    
    public void taunt(){
        if (!isDead && System.currentTimeMillis() - lastTauntTimeStamp > timeBetweenTaunts){
            lastTauntTimeStamp = System.currentTimeMillis();
            isTaunting = true;
            if(!muteSounds){
                Tools.playRandomSoundFromList(tauntSoundPlayer);
            }
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
        itemList = new ArrayList<>();
        for (int i = 1; i<=initialItemNumber; i++){
            itemList.add(new BonusItem(-i, playerId));
        }
        sql.addPlayer(this);
        sql.addBulletList(bulletList);
        sql.addItemList(itemList);
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
            setBulletListInactive();
            if (!muteSounds) {
                if(fellToDeath){
                    playFallSound();
                } else {
                    Tools.playRandomSoundFromList(dyingSoundPlayer);
                }
            }
        } else {
            isDead = false;
            if (formerHealth > health && !muteSounds && System.currentTimeMillis()-timeBetweenHurtSounds > timeSinceLastHurtSound) {
                Tools.playRandomSoundFromList(hurtSoundPlayer);
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
    
    public ArrayList<BonusItem> getItemList(){
        return itemList;
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
        skinId = skinIndex;
        playerAnimation.setRow((skinId - 1) * 4 + 3);
        playerAnimation.setSkinId(skinId);
    }
    
    public int getSkinIndex(){
        return skinId;
    }
    
    public void move(long dT){
        speed[0] += acceleration[0]*dT;
        speed[1] += acceleration[1]*dT;
        posX += speed[0]*dT;
        posY += speed[1]*dT;
    }
    
    public void draw(Graphics2D g, GamePanel game) {
        if (!isDead) {
            double zoomRatio = game.getZoomRatio()*game.getScreenSizeZoomRatio();
            g.drawImage(animationImages.get(playerAnimation.getCurrentImage()), game.getGameX()+(int)((posX + playerWidth/2 - imageWidth)*zoomRatio), (int)((posY + playerHeight / 2 - imageHeight)*zoomRatio), (int)(imageWidth * 2*zoomRatio), (int)(imageHeight * 2*zoomRatio), null);
            g.drawImage(hpBar, game.getGameX()+(int)((posX + playerWidth/2 - imageWidth)*zoomRatio), (int)((posY + playerHeight / 2 - imageHeight - 12)*zoomRatio), (int)(imageWidth * 2*zoomRatio), (int)(imageHeight * 2*zoomRatio), null);
            gun.draw(g, this, game);
            g.setColor(Color.RED);
            g.fillRect(game.getGameX()+(int)((posX + playerWidth / 2 - imageWidth + 12)*zoomRatio), (int)((posY + playerHeight / 2 - imageHeight - 6)*zoomRatio), (int) ((imageWidth * 2 - 24) * health / maxHealth*zoomRatio), (int)(2*zoomRatio));
            // drawing hitbox
            /*Rectangle hitbox = getHitBox();
            g.drawRect(game.getGameX()+hitbox.x, hitbox.y, hitbox.width, hitbox.height);
            g.setColor(Color.GREEN);
            hitbox = getFeetHitbox();
            g.drawRect(game.getGameX()+hitbox.x, hitbox.y, hitbox.width, hitbox.height);
            g.setColor(Color.RED);*/
        }
    }
    
    public Rectangle getHitBox(){
        return new Rectangle((int)(posX),(int)(posY),(int)(playerWidth),(int)(playerHeight));
    }
    
    public Rectangle getFeetHitbox(){
        return new Rectangle((int)(posX),(int)(posY+(1-feetHeight)*playerHeight),(int)(playerWidth),(int)(feetHeight*playerHeight));
    }
    
    public void drawBullets(Graphics2D g, int textureSize, GamePanel game) {
        for (int i=0; i<bulletList.size(); i++)
        {
            bulletList.get(i).draw(g, textureSize, game);
        }
        
        for (int i=0; i<destroyedBullets.size(); i++)
        {
            destroyedBullets.get(i).draw(g, textureSize, game);
        }
    }
    
    public void drawItems(Graphics2D g, int textureSize, GamePanel game) {
        for (BonusItem item : itemList){
            item.draw(g, textureSize, game);
        }
    }
    
    public void updateAnimation(long dT){
        this.playerAnimation.update(dT);
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
            } else if (System.currentTimeMillis()-lastRollTimeStamp > rollTime) {
                isRolling = false;
                speed[0] /= rollSpeedMultiplier;
                speed[1] /= rollSpeedMultiplier;

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
                playerAnimation.setIsIdle(true, facedDirection);
            }
            else{
                playerAnimation.setIsIdle(false, facedDirection);
            }
            if(isTaunting){
                if(System.currentTimeMillis() - lastTauntTimeStamp > timeBetweenTaunts){
                    isTaunting = false;
                }
            }
        }
    }
    
    public boolean isTaunting(){
        return isTaunting;
    }
    
    public void updateTileEffects(Map map){
        Rectangle feet = getFeetHitbox();
        double[] xValues = new double[]{feet.x, feet.x, feet.x+feet.width, feet.x+feet.height};
        double[] yValues = new double[]{feet.y, feet.y+feet.width, feet.y, feet.y+feet.width};
        Effect effect;
        for (int i = 0; i<4; i++ ) {
            effect = map.getTile(xValues[i], yValues[i]).getEffect();
            if (effect.getId()!=Effect.NO_EFFECT && Tools.hitboxCollision(feet, map.getTileHitbox(xValues[i], yValues[i]))) {
                addEffect(effect);
            }
        }
    }
    
    public void checkTeleports(Map map){
        int[] destination = map.teleporterDestination(getFeetHitbox()); // returns {-1, -1} if not on a teleporter, else returns new position
        if(justTeleported){
            justTeleported = destination[0]!=-1;
        } else if(destination[0]!=-1){
            if(!muteSounds){
                Tools.playRandomSoundFromList(teleportSoundPlayer);
            }
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
            switch(newEffect.getId()){
                case Effect.HEALING:
                    break;
                case Effect.FASTER:
                    break;
            }
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
    
    public void setPlayerId(int playerId){
        this.playerId = playerId;
    }
    
    public int getPlayerId(){
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
    
    public void addBullet(double initPosX, double initPosY, double[] direction, double speed, SQLManager sql, double damage, int bulletType, int numberOfBounces, double maxRange){
        if (!isDead) {
            boolean inactiveBulletFound = false;
            int bulletIndex = 0;
            while(bulletIndex < bulletList.size() && !inactiveBulletFound){
                inactiveBulletFound = bulletList.get(bulletIndex).isActivable();
                bulletIndex++;
            }
            if(!inactiveBulletFound){
                bulletList.add(new Bullet(initPosX, initPosY, direction, speed, playerId, bulletIndex+1, damage, bulletType, numberOfBounces, maxRange));
                bulletList.get(bulletIndex).setActive(true);
                sql.addBullet(bulletList.get(bulletIndex));
            } else {
                Bullet bullet = bulletList.get(bulletIndex-1);
                bullet.setActive(true);
                bullet.setBulletType(bulletType);
                bullet.setSpeed(speed);
                bullet.setDirection(direction);
                bullet.setPosX(initPosX);
                bullet.setPosY(initPosY);
                bullet.setDamage(damage);
                bullet.setNumberOfBounces(numberOfBounces);
                bullet.setMaxRange(maxRange);
                bullet.resetTravelledDistance();
            }
        }
    }
    
    public Image getImage(){
        return animationImages.get(playerAnimation.getCurrentImage());
    }
    
    public void updateBulletList(long dT, Map map, ArrayList<Player> otherPlayersList){
        Bullet bullet;
        Player hurtPlayer;
        for (int i = 0; i<bulletList.size(); i++) {
            bullet = bulletList.get(i);
            if (bullet.isActive()) {
                bullet.update(dT);
                if(bullet.getBulletType()==Bullet.MELEE){
                    if (bullet.getTravelledDistance()>meleeRange){
                        bullet.setBulletSpeed(0);
                    }
                    if(System.currentTimeMillis()-lastMeleeAttackTimeStamp > meleeAttacksDuration){
                        bullet.setActive(false);
                    } else {
                        for (Player otherPlayer : otherPlayersList) {
                            if (Tools.hitboxCollision(otherPlayer.getHitBox(), bullet.getHitBox()) && !this.isFriend(otherPlayer)) {
                                bullet.setActive(false);
                                hurtPlayer = new Player(otherPlayer.getPlayerId());
                                hurtPlayer.setHealth(bullet.getDamage());
                                hurtPlayers.add(hurtPlayer);
                            }
                        }
                    }
                } else if (bullet.destroyedByMap(map)) {
                    bullet.setActive(false);
                    Bullet animatedBullet = new Bullet(bullet.getPosX(), bullet.getPosY(), bullet.getBulletType());
                    animatedBullet.setAnimationState(Animation.GUN);
                    destroyedBullets.add(animatedBullet);
                } else if(bullet.getTravelledDistance()>bullet.getMaxRange()){
                    bullet.setActive(false);
                    Bullet animatedBullet = new Bullet(bullet.getPosX(), bullet.getPosY(), bullet.getBulletType());
                    animatedBullet.setAnimationState(Animation.GUN);
                    destroyedBullets.add(animatedBullet);
                } else {
                    for (Player otherPlayer : otherPlayersList) {
                        if (Tools.hitboxCollision(otherPlayer.getHitBox(), bullet.getHitBox()) && !this.isFriend(otherPlayer)) {
                            bullet.setActive(false);
                            hurtPlayer = new Player(otherPlayer.getPlayerId());
                            hurtPlayer.setHealth(bullet.getDamage());
                            hurtPlayers.add(hurtPlayer);
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
        isTaunting = false;
        timeSinceLastHurtSound = System.currentTimeMillis()-timeBetweenHurtSounds;
        lastTauntTimeStamp = System.currentTimeMillis()-timeBetweenTaunts;
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

    public void generateGun(int numberOfPlayers, long dT, GameMode gameMode) {
        boolean generateWeapon;
        switch (gameMode.getGunGestion()) {
            case GameMode.RANDOM:
                generateWeapon = gun.getId() == 0 && Math.random() < (double) dT / (1000 * numberOfPlayers * 4); // In average, one player gets a gun every 4 seconds
                break;
            case GameMode.ALWAYSON:
                generateWeapon = true;
                break;
            default:
                generateWeapon = true;
        }
        if(generateWeapon && this.gun.getId()==0){
            double gunRandom = Math.random();
            int gunId;
            if(gunRandom<0.15){
               gunId = Gun.PISTOL; // 15%
            } else if(gunRandom<0.3){
                gunId = Gun.UZI; // 15%
            } else if(gunRandom<0.50){
                gunId = Gun.AK; // 20%
            } else if(gunRandom<0.65){
                gunId = Gun.MITRAILLEUSE; // 15%
            } else if(gunRandom<0.75){
                gunId = Gun.SHOTGUN; // 10%
            } else if(gunRandom<0.85){
                gunId = Gun.MAGNUM; // 10%
            } else if(gunRandom<0.92){
                gunId = Gun.SNIPER; // 7%
            } else if(gunRandom<0.96){
                gunId = Gun.FLAMETHROWER; // 4%
            } else {
                gunId = Gun.LEGENDARY_WEAPON; // 4%sssssss
            }
            int numberOfCartridges = Math.round((float) Math.random()); // player can get 0 or 1 cartridge
            gun.setId(gunId, numberOfCartridges);
        }
    }
    
    public void meleeAttack(double[] directionOfFire, SQLManager sql) {
        if (System.currentTimeMillis()-timeBetweenMeleeAttacks>=lastMeleeAttackTimeStamp){
            lastMeleeAttackTimeStamp = System.currentTimeMillis();
            addBullet(getPosX() + imageWidth / 4, getPosY() + imageHeight / 4, directionOfFire, 3.0 , sql, meleeDamage , Bullet.MELEE, 0, meleeRange);
        }
    }
    
    public void shoot(double[] wantedDirection, SQLManager sql, int numberOfBounces){
        if (gun.shoot(muteSounds)){
            double sign = Math.signum(wantedDirection[0]);
            double randomAngle = Math.random() * Math.signum(Math.random() - 0.5) * gun.getBulletSpread();
            double Gamma = Math.atan(wantedDirection[1] / wantedDirection[0]);
            double[] realDirection = new double[]{};
            if (gun.getId() == Gun.SHOTGUN) {
                double[] angle = new double[]{0.0872665, 0.0872665 * 2, -0.0872665, -0.0872665 * 2, 0};
                for (int i = 0; i<angle.length; i++){
                    realDirection = new double[]{Math.cos(randomAngle + Gamma + angle[i]) * sign, Math.sin(randomAngle + Gamma + angle[i]) * sign};
                    addBullet(getPosX() + imageWidth / 4, getPosY() + imageHeight / 4, realDirection, gun.getBulletSpeed(), sql, gun.getDamage(), gun.getBulletType(), numberOfBounces, gun.getMaxRange());
                }
            } else {
                realDirection = new double[]{Math.cos(randomAngle + Gamma) * sign, Math.sin(randomAngle + Gamma) * sign};
                addBullet(getPosX() + imageWidth / 4, getPosY() + imageHeight / 4, realDirection, gun.getBulletSpeed(), sql, gun.getDamage(), gun.getBulletType(), numberOfBounces, gun.getMaxRange());
            }
            if (realDirection [0] < 0) {
                gun.changeGunDirection(1);
            } else {
                gun.changeGunDirection(0);
            }
        }
    }
    
    public void playTeleportSound(){
        if(!muteSounds){
            Tools.playRandomSoundFromList(teleportSoundPlayer);
        }
    }

    public void playShootSound() {
        if(!muteSounds){
            gun.playShootingSound();
        }
    }
    
    public void playDieSound() {
        if(!muteSounds){
            Tools.playRandomSoundFromList(dyingSoundPlayer);
        }
    }
    
    public void playFallSound() {
        if(!muteSounds){
            fallingSoundPlayer.play();
        }
    }
    
    public boolean isCloseToHole(Map map){
        boolean test = false;
        double[] xValues = new double[]{posX-playerWidth*0.5, posX-playerWidth*0.5, posX-playerWidth*0.5, posX+playerWidth*0.5, posX+playerWidth*0.5, posX+playerWidth*0.5, posX+playerWidth*1.5, posX+playerWidth*1.5, posX+playerWidth*1.5};
        double[] yValues = new double[]{posY, posY+playerHeight*0.75, posY+playerHeight*1.5, posY, posY+playerHeight*0.75, posY+playerHeight*1.5, posY, posY+playerHeight*0.75, posY+playerHeight*1.5};
        for (int i = 0; i<xValues.length; i++ ) {
            if (map.getTile(xValues[i], yValues[i]).getEffect().getId()==Effect.FALL_TO_DEATH) {
                test = true;
            }
        }
        return test;
    }
    
    public boolean isCloseToTeleporter(Map map){
        boolean test = false;
        double[] xValues = new double[]{posX-playerWidth*0.5, posX-playerWidth*0.5, posX-playerWidth*0.5, posX+playerWidth*0.5, posX+playerWidth*0.5, posX+playerWidth*0.5, posX+playerWidth*1.5, posX+playerWidth*1.5, posX+playerWidth*1.5};
        double[] yValues = new double[]{posY, posY+playerHeight*0.75, posY+playerHeight*1.5, posY, posY+playerHeight*0.75, posY+playerHeight*1.5, posY, posY+playerHeight*0.75, posY+playerHeight*1.5};
        for (int i = 0; i<xValues.length; i++ ) {
            if (map.getTileId(xValues[i], yValues[i])==Map.TELEPORTER_ID) {
                test = true;
            }
        }
        return test;
    }
    
    public int getCurrentImage(){
        return playerAnimation.getCurrentImageValue();
    }
    public double[] getSpeed(){
        return speed;
    }
    
    public void setLifeCounter(int life){
        this.lifeCounter = life;
    }
    
    public int getLifeCounter(){
        return this.lifeCounter;
    }
    
    public void setSpeed(double[] speed1){
        speed[0]=speed1[0];
        speed[1] =speed1[1];       
    }

    public void dieByFall() {
        if(!muteSounds){
            fallingSoundPlayer.play();
            fellToDeath = true;
        }
        hurtSelf(health+1);
    }

    public void dash() {
        if(!isDead && System.currentTimeMillis()-lastRollTimeStamp>timeBetweenRolls){
            lastRollTimeStamp = System.currentTimeMillis();
            isRolling = true;
            speed[0] *=rollSpeedMultiplier;
            speed[1] *=rollSpeedMultiplier;
        }
    }
    
    public void setAnimationSkinId(int skinId){
        this.playerAnimation.setSkinId(skinId);
    }

    @Override
    public int compareTo(Player otherPlayer) {
        int result = 0;
        if(teamId < otherPlayer.getTeamId() || (teamId==otherPlayer.getTeamId() && playerId < otherPlayer.getPlayerId())){
            result = -1;
        } else if(teamId > otherPlayer.getTeamId() || (teamId==otherPlayer.getTeamId() && playerId > otherPlayer.getPlayerId())){
            result = 1;
        }
        return result;
    }

    private void setBulletListInactive() {
        if (bulletList != null) {
            for (Bullet bullet : bulletList) {
                bullet.setActive(false);
            }
        }
    }

    public void updateItemList(ArrayList<Player> otherPlayersList, ArrayList<BonusItem> otherPlayersItems) {
        BonusItem item;
        for (int i = 0; i<itemList.size(); i++) {
            item = itemList.get(i);
            if (item.isActive()) {
                if(Tools.hitboxCollision(this.getHitBox(), item.getHitBox())){
                    addEffect(item.getEffect());
                    item.setActive(false);
                } else {
                    for (Player otherPlayer : otherPlayersList) {
                        if (Tools.hitboxCollision(otherPlayer.getHitBox(), item.getHitBox())) {
                            item.setActive(false);
                        }
                    }
                }
            }
        }
        updateOtherItems(otherPlayersItems);
    }
    
    public void updateOtherItems(ArrayList<BonusItem> otherPlayersItems){
        int index = 0;
        BonusItem item;
        while(index < otherPlayersItems.size()){
            item = otherPlayersItems.get(index);
            if (Tools.hitboxCollision(this.getHitBox(), item.getHitBox())) {
                addEffect(item.getEffect());
                pickedItems.add(item);
                otherPlayersItems.remove(index);
            } else {
                index++;
            }
        }
    }

    public ArrayList<BonusItem> getPickedItems(){
        return pickedItems;
    }
    
    void generateItem(int numberOfPlayers, long dT, Map map, SQLManager sql) {
        if(!isDead && numberOfActiveItems() < MAX_NUMBER_OF_ITEMS && Math.random() < (double) dT / (1000 * numberOfPlayers * 6)){ // In average, one player generates an item every 6 seconds
            int itemType = ThreadLocalRandom.current().nextInt(0, BonusItem.NUMBER_OF_ITEMS);
            int[] position = map.randomItemPosition();
            boolean inactiveItemFound = false;
            int itemIndex = 0;
            while(itemIndex < itemList.size() && !inactiveItemFound){
                inactiveItemFound = itemList.get(itemIndex).isActivable();
                itemIndex++;
            }
            if(!inactiveItemFound){
                itemList.add(new BonusItem(position[0], position[1], itemType, -(itemIndex+1), playerId));
                itemList.get(itemIndex).setActive(true);
                sql.addItem(itemList.get(itemIndex));
            } else {
                BonusItem item = itemList.get(itemIndex-1);
                item.setType(itemType);
                item.setPosition(position);
                item.setActive(true);
            }
        }
    }

    private int numberOfActiveItems() {
        int counter = 0;
        for (BonusItem item : itemList){
            if(item.isActive()){
                counter++;
            }
        }
        return counter;
    }
}
