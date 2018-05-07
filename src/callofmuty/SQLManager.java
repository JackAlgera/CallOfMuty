package callofmuty;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javazoom.jl.decoder.JavaLayerException;

public class SQLManager {
    private Connection connexion;

/* SQL table structure
    bullet : idBullet (int) ; idPlayer (int) ; posX (int); posY (int); active (tinyint)
    grid : x (int) ; y (int) ; tileType (int) ; startingTile (tinyint)
    players : id (int) ; name (String(50)) ; playerHp (double) ; posX (int) ; posY (int) : skinId (int) ; playerState (int), isTaunting (tinyint);
    game : id (int)(useless, primaryKey), gameState (int)
*/
    public SQLManager(){         
        try {
            connexion=DriverManager.getConnection("jdbc:mysql://nemrod.ens2m.fr:3306/20172018_s2_vs2_tp4?serverTimezone=UTC","vs2tp4", "vs2tp4");
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void uploadPlayerAndBullets(Player player) { //updates player's position & bullets + hurt players' health
        if (!player.isDead()) {
            PreparedStatement requete;
            String xStatement = "", yStatement = "", isActiveStatement = "", healthStatement = "", bulletTypeStatement = "";
            int taunt;
            if (player.isTaunting()){
                taunt = 1;
            } else {
                taunt = 0;
            }
            ArrayList<Bullet> bulletList = player.getBulletList();
            for (Bullet bullet : bulletList) {
                if (bullet.isActive()) {
                    xStatement += "WHEN " + bullet.getBulletId() + " THEN " + (int) bullet.getPosX() + " \n";
                    yStatement += "WHEN " + bullet.getBulletId() + " THEN " + (int) bullet.getPosY() + " \n";
                    bulletTypeStatement+="WHEN " + bullet.getBulletId() + " THEN " + bullet.getBulletType() + " \n";
                    isActiveStatement += "WHEN " + bullet.getBulletId() + " THEN 1 \n";
                }
            }
            for (Player hurtPlayer : player.getHurtPlayers()) {
                healthStatement += "WHEN " + hurtPlayer.getPlayerId() + " THEN players.playerHp - " + hurtPlayer.getPlayerHealth() + " \n";
            }
            player.resetHurtPlayers();
            try {
                if (xStatement.isEmpty()) { //there are no bullets to update
                    if (healthStatement.isEmpty()) { //there are no players to hurt
                        requete = connexion.prepareStatement("UPDATE players LEFT JOIN bullet ON players.id=bullet.idPlayer SET players.posX = " + player.getPosX() + ", players.posY = " + player.getPosY() + ", players.gunId = " + player.getGunId() + ", players.isTaunting = " + taunt + ", bullet.isActive = 0 WHERE players.id = " + player.getPlayerId());
                    } else { //there are players to hurt
                        requete = connexion.prepareStatement("UPDATE players LEFT JOIN bullet ON players.id=bullet.idPlayer AND players.id = " + player.getPlayerId() + " SET players.posX = CASE players.id WHEN " + player.getPlayerId() + " THEN " + player.getPosX() + " ELSE players.posX END, players.posY = CASE players.id WHEN " + player.getPlayerId() + " THEN " + player.getPosY() + " ELSE players.posY END, players.isTaunting = CASE players.id WHEN " + player.getPlayerId() + " THEN " + taunt + " ELSE players.isTaunting END, players.gunId = CASE players.id WHEN " + player.getPlayerId() + " THEN " + player.getGunId() + " ELSE players.gunId END, players.playerHp = CASE players.id " + healthStatement + " ELSE players.playerHp END, bullet.isActive = CASE players.id WHEN " + player.getPlayerId() + " THEN 0 ELSE bullet.isActive END");
                    }
                } else { //there are bullets to update
                    if (healthStatement.isEmpty()) { //there are no players to hurt
                        requete = connexion.prepareStatement("UPDATE players LEFT JOIN bullet ON players.id=bullet.idPlayer SET players.posX = " + player.getPosX() + ", players.posY = " + player.getPosY() + ", players.gunId = " + player.getGunId() + ", players.isTaunting = " + taunt + ", bullet.posX = CASE bullet.idBullet " + xStatement + " ELSE 0 END, bullet.bulletType = CASE bullet.idBullet " + bulletTypeStatement + " ELSE 0 END, bullet.posY = CASE bullet.idBullet " + yStatement + " ELSE 0 END, bullet.isActive = CASE bullet.idBullet " + isActiveStatement + " ELSE 0 END WHERE players.id = " + player.getPlayerId());
                    } else { //there are players to hurt
                        requete = connexion.prepareStatement("UPDATE players LEFT JOIN bullet ON players.id=bullet.idPlayer AND players.id = " + player.getPlayerId() + " SET bullet.posX = CASE players.id WHEN " + player.getPlayerId() + " THEN CASE bullet.idBullet " + xStatement + " ELSE 0 END ELSE bullet.posX END, bullet.posY = CASE players.id WHEN " + player.getPlayerId() + " THEN CASE bullet.idBullet " + yStatement + " ELSE 0 END ELSE bullet.posY END, bullet.bulletType = CASE players.id WHEN " + player.getPlayerId() + " THEN CASE bullet.idBullet " + bulletTypeStatement + " ELSE 0 END ELSE bullet.bulletType END, bullet.isActive = CASE players.id WHEN " + player.getPlayerId() + " THEN CASE bullet.idBullet " + isActiveStatement + " ELSE 0 END ELSE bullet.isActive END, players.posX = CASE players.id WHEN " + player.getPlayerId() + " THEN " + player.getPosX() + " ELSE players.posX END, players.posY = CASE players.id WHEN " + player.getPlayerId() + " THEN " + player.getPosY() + " ELSE players.posY END, players.isTaunting = CASE players.id WHEN " + player.getPlayerId() + " THEN " + taunt + " ELSE players.isTaunting END, players.gunId = CASE players.id WHEN " + player.getPlayerId() + " THEN " + player.getGunId() + " ELSE players.gunId END, players.playerHp = CASE players.id " + healthStatement + " ELSE players.playerHp END, bullet.isActive = CASE players.id WHEN " + player.getPlayerId() + " THEN 0 ELSE bullet.isActive END");
                    }
                }
                requete.executeUpdate();
                requete.close();
            } catch (SQLException ex) {
                Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    // Downloads other players' positions, health and bullet positions and local player's health
    public void downloadPlayersAndBullets(Player player, ArrayList<Player> otherPlayersList, ArrayList<Bullet> otherBulletsList, Map map) {
        PreparedStatement requete;
        int playerId = -1, playerIndex, bulletId, bulletIndex;
        double formerX, formerY;
        double[] position = new double[2];
        ArrayList<Bullet> updatedBullets = new ArrayList<>(); //saves which bullets were updated, others will be erased
        ArrayList<Player> updatedPlayers = new ArrayList<>(); //same
        try {
            requete = connexion.prepareStatement("SELECT players.id, players.posX, players.posY, players.playerHp, players.gunId, players.isTaunting, bullet.idBullet, bullet.posX, bullet.posY, bullet.bulletType FROM players LEFT JOIN bullet ON players.id=bullet.idPlayer AND bullet.isActive=1 AND NOT players.id="+ player.getPlayerId() +" WHERE players.playerState = "+ Player.PLAYING +" ORDER BY players.id");
            ResultSet resultat = requete.executeQuery();
            if (resultat!=null) {
                while (resultat.next()) {
                    if (otherPlayersList.contains(new Player(resultat.getInt("players.id")))) { // check if the player is "known"
                        if (resultat.getInt("players.id") != playerId) { //this player's position was not yet updated
                            // getting player to update
                            playerId = resultat.getInt("players.id");
                            playerIndex = otherPlayersList.indexOf(new Player(playerId)); //finding the right id in the list
                            // update player
                            position[0] = resultat.getInt("players.posX");
                            position[1] = resultat.getInt("players.posY");
                            if(resultat.getInt("isTaunting")!=0){
                                otherPlayersList.get(playerIndex).taunt();
                            }
                            formerX = otherPlayersList.get(playerIndex).getPosX();
                            formerY = otherPlayersList.get(playerIndex).getPosY();
                            otherPlayersList.get(playerIndex).setPosition(position);
                            if(Math.sqrt(Math.pow(position[0]-formerX, 2)+Math.pow(position[1]-formerY, 2))>map.getTextureSize() && otherPlayersList.get(playerIndex).isCloseToTeleporter(map)){ // if player seems to have teleported, play teleport sound
                                player.playTeleportSound();
                            }
                            otherPlayersList.get(playerIndex).setHealth(resultat.getDouble("players.playerHp"));
                            otherPlayersList.get(playerIndex).setGunId(resultat.getInt("players.gunId"));
                            otherPlayersList.get(playerIndex).setMuteSounds(player.getMuteSounds());
                            updatedPlayers.add(new Player(playerId));
                            // update bullet
                            bulletId = resultat.getInt("bullet.idBullet");
                            if (bulletId > 0) { // 0 means null
                                bulletIndex = otherBulletsList.indexOf(new Bullet(playerId, bulletId, 0));
                                updatedBullets.add(new Bullet(playerId, bulletId, 0));
                                if (bulletIndex == -1) { // bullet was not already in the list
                                    otherBulletsList.add(new Bullet(resultat.getInt("bullet.posX"), resultat.getInt("bullet.posY"), playerId, bulletId, resultat.getInt("bulletType")));
                                    otherBulletsList.get(otherBulletsList.size() - 1).setActive(true);
                                    otherPlayersList.get(playerIndex).playShootSound();
                                } else { // bullet was already in the list
                                    otherBulletsList.get(bulletIndex).setPosX(resultat.getInt("bullet.posX"));
                                    otherBulletsList.get(bulletIndex).setPosY(resultat.getInt("bullet.posY"));
                                }
                            }
                        } else { // this player's position was already updated, update only the bullet
                            bulletId = resultat.getInt("bullet.idBullet");
                            if (bulletId > 0) { // 0 means null
                                bulletIndex = otherBulletsList.indexOf(new Bullet(playerId, bulletId, 0));
                                updatedBullets.add(new Bullet(playerId, bulletId, 0));
                                if (bulletIndex == -1) {
                                    otherBulletsList.add(new Bullet(resultat.getInt("bullet.posX"), resultat.getInt("bullet.posY"), playerId, bulletId, resultat.getInt("bulletType")));
                                    otherBulletsList.get(otherBulletsList.size() - 1).setActive(true);
                                } else {
                                    otherBulletsList.get(bulletIndex).setPosX(resultat.getInt("bullet.posX"));
                                    otherBulletsList.get(bulletIndex).setPosY(resultat.getInt("bullet.posY"));
                                }
                            }
                        }
                    } else {
                        if (resultat.getInt("players.id") == player.getPlayerId()) { // update local player's health
                            player.setHealth(resultat.getDouble("players.playerHp"));
                            if (player.isDead()) {
                                setPlayerDead(player);
                            }
                        } else { // if player was not "known" : isn't supposed to happen, deal with it here if needed

                        }
                    }
                }
            }
            requete.close();
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        int index = 0;
        while (index<otherBulletsList.size()){ //remove bullets that were not updated (ie are no longer active)
            if (!updatedBullets.contains(otherBulletsList.get(index))){
                otherBulletsList.remove(index);
            } else {
                index++;
            }
        }
        while (index<otherPlayersList.size()){ //remove players that were not updated (ie died)
            if (!updatedPlayers.contains(otherPlayersList.get(index))){
                if(otherPlayersList.remove(index).isCloseToHole(map)){
                    player.playFallSound();
                } else {
                    player.playDieSound();
                }
            } else {
                index++;
            }
        }
    }
    
    public void addPlayer(Player player){
        PreparedStatement requete;
        String value = "("+player.getPlayerId()+",'"+player.getName()+"',"+player.getPlayerHealth()+","
                    +(int)player.getPosX()+","+(int)player.getPosY()+","+player.getSkinIndex()+","
                    +Player.PLAYING+","+player.getGunId()+","+player.getTeamId()+" , 0)";
        try {
            requete = connexion.prepareStatement("INSERT INTO players VALUES " + value);
            requete.executeUpdate();
            requete.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void addBulletList(ArrayList<Bullet> bulletList){ //removes a player and his bullets from database
        if (!bulletList.isEmpty()) {
            PreparedStatement requete;
            String values = "";
            Bullet bullet;
            for (int i = 0; i<bulletList.size()-1; i++) {
                bullet = bulletList.get(i);
                values += "("+bullet.getBulletId()+","+bullet.getPlayerId()+","+bullet.getPosX()+","+bullet.getPosY()+",0,0), ";
            }
            bullet = bulletList.get(bulletList.size()-1);
            values += "("+bullet.getBulletId()+","+bullet.getPlayerId()+","+bullet.getPosX()+","+bullet.getPosY()+",0,0)";
            try {
                requete = connexion.prepareStatement("INSERT INTO bullet VALUES "+values);
                requete.executeUpdate();
                requete.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void removePlayer(Player player){ //removes a player and his bullets from database
        PreparedStatement requete;        
        try {
            requete = connexion.prepareStatement("DELETE players, bullet FROM players LEFT JOIN bullet ON players.id=bullet.idPlayer WHERE players.id=" + player.getPlayerId());
            requete.executeUpdate();
            requete.close();  
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void clearTable(){
        PreparedStatement requete;
        try {
            requete = connexion.prepareStatement("DELETE FROM players" );
            requete.executeUpdate();
            requete.close();
            requete = connexion.prepareStatement("DELETE FROM grid" );
            requete.executeUpdate();
            requete.close();
            requete = connexion.prepareStatement("DELETE FROM game" );
            requete.executeUpdate();
            requete.close();
            requete = connexion.prepareStatement("DELETE FROM bullet" );
            requete.executeUpdate();
            requete.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void createMap(Map map){
        PreparedStatement requete;
        String values = "";
        int[][] intMap = map.getMap();
        if (map.startTileIndex(new int[]{0, 0})>-1) {
            values += "(0,0," + intMap[0][0] + ",1)";
        } else {
            values += "(0,0," + intMap[0][0] + ",0)";
        }
        for (int i = 0 ; i<map.getMapWidth() ; i++){
            for (int j = 0; j<map.getMapHeight(); j++){
                if (i != 0 || j != 0) {
                    if (map.startTileIndex(new int[]{i, j})>-1) {
                        values += ", (" + i + "," + j + "," + intMap[i][j] + ",1)";
                    } else {
                        values += ", (" + i + "," + j + "," + intMap[i][j] + ",0)";
                    }
                }
            }
        }
        try {
            requete = connexion.prepareStatement("INSERT INTO grid VALUES " + values);
            requete.executeUpdate();
            requete.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public Map getMap(int textureSize){
        Map map = null;
        ArrayList<int[]> startingTile = new ArrayList<>();
        PreparedStatement requete;
        try {
            requete = connexion.prepareStatement("SELECT * FROM grid ORDER BY i DESC, j DESC");
            ResultSet resultat = requete.executeQuery();
            if(resultat.next()){ // first result is used to set map dimensions, hence the "desc" order
                int[][] intMap = new int[resultat.getInt("i")+1][resultat.getInt("j")+1];
                intMap[resultat.getInt("i")][resultat.getInt("j")] = resultat.getInt("tileType");                
                if (resultat.getInt("startingTile")==1){
                    startingTile.add(new int[]{resultat.getInt("i"),resultat.getInt("j")});
                }
                while (resultat.next()) {
                    intMap[resultat.getInt("i")][resultat.getInt("j")] = resultat.getInt("tileType");
                    if (resultat.getInt("startingTile") == 1) {
                        startingTile.add(new int[]{resultat.getInt("i"),resultat.getInt("j")});
                    }
                }
                map = new Map(intMap, textureSize);
                map.setStartTile(startingTile);
            }
            requete.close();
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return map;
    }
    
    public void createGame(Map map, int gameModeId){
        PreparedStatement requete;
        try {
            requete = connexion.prepareStatement("INSERT INTO game VALUES (1,"+ GamePanel.PRE_GAME +","+ gameModeId +")");
            requete.executeUpdate();
            requete.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        createMap(map);
    }
    
    public void setPlayerDead(Player player){               
        PreparedStatement requete;
        try {
            requete = connexion.prepareStatement("UPDATE players SET playerState = " + Player.DEAD + " WHERE id = "+player.getPlayerId());
            requete.executeUpdate();
            requete.close();
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setGameState(int gameState){               
        PreparedStatement requete;
        try {
            requete = connexion.prepareStatement("UPDATE game SET gameState = "+gameState);
            requete.executeUpdate();
            requete.close();
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int[] getGame(){               
        PreparedStatement requete;
        int gameState = -1;
        int gameMode = -1;
        try {
            requete = connexion.prepareStatement("SELECT gameState, gameMode FROM game");
            ResultSet resultat = requete.executeQuery();
            while (resultat.next()) { 
                gameState = resultat.getInt("gameState");
                gameMode = resultat.getInt("gameMode");
            }
            requete.close();
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new int[]{gameState,gameMode};
    }
     
    public void disconnect(){
        try {
            connexion.close();
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addBullet(Bullet bullet){        
        PreparedStatement requete;
        
        try {
            requete = connexion.prepareStatement("INSERT INTO bullet VALUES (?,?,?,?,?,?)");
            requete.setInt(1, bullet.getBulletId());
            requete.setInt(2, bullet.getPlayerId());
            requete.setDouble(3, bullet.getPosX());
            requete.setDouble(4, bullet.getPosY());
            int isActive;
            if(bullet.isActive()){
                isActive = 1;
            } else {
                isActive = 0;
            }
            requete.setInt(5, isActive);
            requete.setInt(6, bullet.getBulletType());
            requete.executeUpdate();
            requete.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public ArrayList<Player> getPlayerList(){ // used only before adding local player to database, when connecting to the game
        ArrayList<Player> players = new ArrayList<>();
        PreparedStatement requete;
        Player newPlayer;
        try {
            requete = connexion.prepareStatement("SELECT * FROM players WHERE playerState = "+Player.PLAYING+" ORDER BY id");
            ResultSet resultat = requete.executeQuery();
            while (resultat.next()) {
                newPlayer = new Player(resultat.getDouble("posX"),resultat.getDouble("posY"));
                newPlayer.setPlayerId(resultat.getInt("id"));
                newPlayer.setTeamId(resultat.getInt("teamId"));
                newPlayer.setSkin(resultat.getInt("skinId"));
                newPlayer.setName(resultat.getString("name"));
                newPlayer.setPlayerState(resultat.getInt("playerState"));
                newPlayer.setHealth(resultat.getDouble("playerHp"));
                players.add(newPlayer);
            }
            requete.close();
              
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return players;
    }
    
    public void updatePlayerList(Player player, ArrayList<Player> otherPlayersList) throws IOException, JavaLayerException{ //used while PRE_GAME state
        PreparedStatement requete;
        Player newPlayer;
        int index;
        ArrayList<Player> updatedPlayers = new ArrayList<>(); // Used to remove players that disconnect
        try {
            requete = connexion.prepareStatement("SELECT * FROM players WHERE playerState = "+Player.PLAYING+" AND NOT id ="+ player.getPlayerId());
            ResultSet resultat = requete.executeQuery();
            while (resultat.next()) {
                newPlayer = new Player(resultat.getDouble("posX"),resultat.getDouble("posY"));
                newPlayer.setPlayerId(resultat.getInt("id"));
                updatedPlayers.add(newPlayer);
                index = otherPlayersList.indexOf(newPlayer);
                if (index > -1){ // Player is already in the list, update variables
                    newPlayer = otherPlayersList.get(index);
                    newPlayer.setSkin(resultat.getInt("skinId"));
                    newPlayer.setName(resultat.getString("name"));
                    newPlayer.setPlayerState(resultat.getInt("playerState"));
                } else { // Player was not yet in the list
                    newPlayer.setSkin(resultat.getInt("skinId"));
                    newPlayer.setTeamId(resultat.getInt("TeamId"));
                    newPlayer.setName(resultat.getString("name"));
                    newPlayer.setPlayerState(resultat.getInt("playerState"));
                    newPlayer.setHealth(resultat.getDouble("playerHp"));
                    otherPlayersList.add(newPlayer);
                }
            }
            requete.close();
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        int i=0;
        while (i<otherPlayersList.size()){ //remove players that were not updated (ie died)
            if (!updatedPlayers.contains(otherPlayersList.get(i))){
                otherPlayersList.remove(i);
            } else {
                i++;
            }
        }
    }
}
