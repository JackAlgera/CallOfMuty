package callofmuty;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLManager {
    private Connection connexion;

/* SQL table structure
    bullet : idBullet (int) ; idPlayer (int) ; posX (int); posY (int); active (boolean)
    grid : x (int) ; y (int) ; tileType (int) ; grid (varchar)
    players : id (int) ; name (String(50)) ; playerHp (double) ; posX (int) ; posY (int) : skinId (int) ; playerState (int);
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
        if (!player.isPlayerDead()) {
            PreparedStatement requete;
            String xStatement = "";
            String yStatement = "";
            String isActiveStatement = "";
            String healthStatement = "";
            ArrayList<Bullet> bulletList = player.getBulletList();
            for (Bullet bullet : bulletList) {
                if (bullet.isActive()) {
                    xStatement += "WHEN " + bullet.getBulletId() + " THEN " + (int) bullet.getPosX() + " \n";
                    yStatement += "WHEN " + bullet.getBulletId() + " THEN " + (int) bullet.getPosY() + " \n";
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
                        requete = connexion.prepareStatement("UPDATE players LEFT JOIN bullet ON players.id=bullet.idPlayer SET players.posX = " + player.getPosX() + ", players.posY = " + player.getPosY() + ", bullet.isActive = 0 WHERE players.id = " + player.getPlayerId());
                    } else { //there are players to hurt
                        requete = connexion.prepareStatement("UPDATE players LEFT JOIN bullet ON players.id=bullet.idPlayer AND players.id = " + player.getPlayerId() + " SET players.posX = CASE players.id WHEN " + player.getPlayerId() + " THEN " + player.getPosX() + " ELSE players.posX END, players.posY = CASE players.id WHEN " + player.getPlayerId() + " THEN " + player.getPosY() + " ELSE players.posY END, players.playerHp = CASE players.id " + healthStatement + " ELSE players.playerHp END, bullet.isActive = CASE players.id WHEN " + player.getPlayerId() + " THEN 0 ELSE bullet.isActive END");
                    }
                } else { //there are bullets to update
                    if (healthStatement.isEmpty()) { //there are no players to hurt
                        requete = connexion.prepareStatement("UPDATE players LEFT JOIN bullet ON players.id=bullet.idPlayer SET players.posX = " + player.getPosX() + ", players.posY = " + player.getPosY() + ", bullet.posX = CASE bullet.idBullet " + xStatement + " ELSE 0 END, bullet.posY = CASE bullet.idBullet " + yStatement + " ELSE 0 END, bullet.isActive = CASE bullet.idBullet " + isActiveStatement + " ELSE 0 END WHERE players.id = " + player.getPlayerId());
                    } else { //there are players to hurt
                        requete = connexion.prepareStatement("UPDATE players LEFT JOIN bullet ON players.id=bullet.idPlayer AND players.id = " + player.getPlayerId() + " SET bullet.posX = CASE players.id WHEN " + player.getPlayerId() + " THEN CASE bullet.idBullet " + xStatement + " ELSE 0 END ELSE bullet.posX END, bullet.posY = CASE players.id WHEN " + player.getPlayerId() + " THEN CASE bullet.idBullet " + yStatement + " ELSE 0 END ELSE bullet.posY END, bullet.isActive = CASE players.id WHEN " + player.getPlayerId() + " THEN CASE bullet.idBullet " + isActiveStatement + " ELSE 0 END ELSE bullet.isActive END, players.posX = CASE players.id WHEN " + player.getPlayerId() + " THEN " + player.getPosX() + " ELSE players.posX END, players.posY = CASE players.id WHEN " + player.getPlayerId() + " THEN " + player.getPosY() + " ELSE players.posY END, players.playerHp = CASE players.id " + healthStatement + " ELSE players.playerHp END, bullet.isActive = CASE players.id WHEN " + player.getPlayerId() + " THEN 0 ELSE bullet.isActive END");
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
    public void downloadPlayersAndBullets(Player player, ArrayList<Player> otherPlayersList, ArrayList<Bullet> otherBulletsList) {
        PreparedStatement requete;
        int playerId = -1;
        int playerIndex;
        int bulletId;
        int bulletIndex;
        double[] position = new double[2];
        ArrayList<Bullet> updatedBullets = new ArrayList<>(); //saves which bullets were updated, others will be erased
        ArrayList<Player> updatedPlayers = new ArrayList<>(); //same
        try {
            requete = connexion.prepareStatement("SELECT players.id, players.posX, players.posY, players.playerHp, bullet.idBullet, bullet.posX, bullet.posY FROM players LEFT JOIN bullet ON players.id=bullet.idPlayer AND bullet.isActive=1 AND NOT players.id="+ player.getPlayerId() +" WHERE players.playerState = "+ Player.PLAYING +" ORDER BY players.id");
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
                            otherPlayersList.get(playerIndex).setPosition(position);
                            otherPlayersList.get(playerIndex).setHealth(resultat.getDouble("players.playerHp"));
                            updatedPlayers.add(new Player(playerId));
                            // update bullet
                            bulletId = resultat.getInt("bullet.idBullet");
                            if (bulletId > 0) { // 0 means null
                                bulletIndex = otherBulletsList.indexOf(new Bullet(playerId, bulletId));
                                updatedBullets.add(new Bullet(playerId, bulletId));
                                if (bulletIndex == -1) { // bullet was not already in the list
                                    otherBulletsList.add(new Bullet(resultat.getInt("bullet.posX"), resultat.getInt("bullet.posY"), playerId, bulletId));
                                    otherBulletsList.get(otherBulletsList.size() - 1).setActive(true);
                                } else { // bullet was already in the list
                                    otherBulletsList.get(bulletIndex).setPosX(resultat.getInt("bullet.posX"));
                                    otherBulletsList.get(bulletIndex).setPosY(resultat.getInt("bullet.posY"));
                                }
                            } else {
                            }
                        } else { // this player's position was already updated, update only the bullet
                            bulletId = resultat.getInt("bullet.idBullet");
                            if (bulletId > 0) { // 0 means null
                                bulletIndex = otherBulletsList.indexOf(new Bullet(playerId, bulletId));
                                updatedBullets.add(new Bullet(playerId, bulletId));
                                if (bulletIndex == -1) {
                                    otherBulletsList.add(new Bullet(resultat.getInt("bullet.posX"), resultat.getInt("bullet.posY"), playerId, bulletId));
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
                            if (player.isPlayerDead()) {
                                setPlayerDead(player);
                            }
                        } else { // if player was not "known" : isn't supposed to happen, deal with it here if it does

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
                otherPlayersList.remove(index);
            } else {
                index++;
            }
        }
    }
    
    public void addPlayer(Player player){
        PreparedStatement requete;    
        try {
            requete = connexion.prepareStatement("INSERT INTO players VALUES (?,?,?,?,?,?,?)");
            requete.setInt(1,player.getPlayerId());
            requete.setString(2, player.getName());
            requete.setDouble(3, player.getPlayerHealth());
            requete.setInt(4, (int) player.getPosX());
            requete.setInt(5, (int) player.getPosY());
            requete.setInt(6, player.getSkinIndex());
            requete.setInt(7, Player.PLAYING);
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
                values += "("+bullet.getBulletId()+","+bullet.getPlayerId()+","+bullet.getPosX()+","+bullet.getPosY()+",0), ";
            }
            bullet = bulletList.get(bulletList.size()-1);
            values += "("+bullet.getBulletId()+","+bullet.getPlayerId()+","+bullet.getPosX()+","+bullet.getPosY()+",0)";
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
            requete = connexion.prepareStatement("DELETE FROM bullet" );
            requete.executeUpdate();
            requete.close();
            requete = connexion.prepareStatement("DELETE FROM grid" );
            requete.executeUpdate();
            requete.close();
            requete = connexion.prepareStatement("DELETE FROM game" );
            requete.executeUpdate();
            requete.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void createGame(){
        PreparedStatement requete;
        try {
            requete = connexion.prepareStatement("INSERT INTO game VALUES (1,"+ GamePanel.PRE_GAME +")");
            requete.executeUpdate();
            requete.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
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
    
    public int getGameState(){               
        PreparedStatement requete;
        int gameState = -1;
        try {
            requete = connexion.prepareStatement("SELECT gameState FROM game");
            ResultSet resultat = requete.executeQuery();
            while (resultat.next()) { 
                gameState = resultat.getInt("gameState");
            }
            requete.close();
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return gameState;
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
            requete = connexion.prepareStatement("INSERT INTO bullet VALUES (?,?,?,?,?)");
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
            requete.executeUpdate();
            requete.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public ArrayList<Player> getPlayerList(){ // used only before adding local player to database, when connecting to the game
        ArrayList<Player> players = new ArrayList<Player>();
        PreparedStatement requete;
        Player newPlayer;
        try {
            requete = connexion.prepareStatement("SELECT * FROM players ORDER BY id");
            ResultSet resultat = requete.executeQuery();
            while (resultat.next()) {
                newPlayer = new Player(resultat.getDouble("posX"),resultat.getDouble("posY"));
                newPlayer.setPlayerId(resultat.getInt("id"));
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
    
    public void updatePlayerList(Player player, ArrayList<Player> otherPlayersList){ //used while PRE_GAME state
        PreparedStatement requete;
        Player newPlayer;
        int index;
        ArrayList<Player> updatedPlayers = new ArrayList<>(); // Used to remove players that disconnect
        try {
            requete = connexion.prepareStatement("SELECT * FROM players WHERE NOT id ="+ player.getPlayerId());
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
