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
    bullet : idBullet (int) ; idPlayer (int) ; posX (double); posY (double)
    grid : x (int) ; y (int) ; tileType (int) ; grid (varchar)
    players : id (int) ; name (varchar(50)) ; playerHp (double) ; posX (int) ; posY (int) : skinId (int) ; statePlayer (int);
*/
    public SQLManager(){         
        try {
            connexion=DriverManager.getConnection("jdbc:mysql://nemrod.ens2m.fr:3306/20172018_s2_vs2_tp4?serverTimezone=UTC","vs2tp4", "vs2tp4");
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void uploadPlayerAndBullets(Player player){
        PreparedStatement requete;
        ArrayList<Bullet> bulletList = player.getBulletList();
        if (!bulletList.isEmpty()) {
            String xStatement = "";
            String yStatement = "";
            for (Bullet bullet : bulletList) {
                xStatement += "WHEN " + bullet.getBulletId() + " THEN " + bullet.getPosX() + " ";
                yStatement += "WHEN " + bullet.getBulletId() + " THEN " + bullet.getPosY() + " ";
            }
            try {
                requete = connexion.prepareStatement("UPDATE players LEFT JOIN bullet ON players.id=bullet.idPlayer SET players.posX = " + player.getPosX() + ", players.posY = " + player.getPosY() + ", bullet.posX = CASE bullet.idBullet " + xStatement + "END, bullet.posY = CASE bullet.idBullet " + yStatement + "END WHERE players.id = " + player.getPlayerId());
                requete.executeUpdate();
                requete.close();
            } catch (SQLException ex) {
                Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                requete = connexion.prepareStatement("UPDATE players SET players.posX = " + player.getPosX() + ", players.posY = " + player.getPosY() + " WHERE players.id = " + player.getPlayerId());
                requete.executeUpdate();
                requete.close();
            } catch (SQLException ex) {
                Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    // Downloads other players' positions, health and bullet positions
    public void downloadPlayersAndBullets(Player player, ArrayList<Player> otherPlayersList, ArrayList<Bullet> otherBulletsList) {
        PreparedStatement requete;
        int playerId = -1;
        int playerIndex;
        int bulletId;
        int bulletIndex;
        double[] position = new double[2];
        try {
            requete = connexion.prepareStatement("SELECT players.id, players.posX, players.posY, players.playerHp, bullet.idBullet, bullet.posX, bullet.posY FROM players LEFT JOIN bullet ON players.id=bullet.idPlayer WHERE NOT players.id="+ player.getPlayerId() +" ORDER BY players.id");
            ResultSet resultat = requete.executeQuery();
            while (resultat.next()) {
                if(resultat.getInt("players.id")!=playerId){ //this player's position was not yet updated
                    // getting player to update
                    playerId = resultat.getInt("players.id");
                    playerIndex = otherPlayersList.indexOf(new Player(playerId)); //finding the right id in the list
                    // update player
                    position[0] = resultat.getInt("players.posX");
                    position[1] = resultat.getInt("players.posY");
                    otherPlayersList.get(playerIndex).setPosition(position);
                    otherPlayersList.get(playerIndex).setHealth(resultat.getDouble("players.playerHp"));
                    // update bullet
                    bulletId = resultat.getInt("bullet.idBullet");
                    bulletIndex = otherBulletsList.indexOf(new Bullet(playerId, bulletId));
                    if (bulletIndex==-1){
                        otherBulletsList.add(new Bullet(resultat.getInt("bullet.posX"), resultat.getInt("bullet.posY"), playerId, bulletId));
                    } else {
                        otherBulletsList.get(bulletIndex).setPosX(resultat.getInt("bullet.posX"));
                        otherBulletsList.get(bulletIndex).setPosY(resultat.getInt("bullet.posY"));
                    }
                } else { // this player's position was already updated, update only the bullet
                    bulletId = resultat.getInt("bullet.idBullet");
                    bulletIndex = otherBulletsList.indexOf(new Bullet(playerId, bulletId));
                    if (bulletIndex==-1){
                        otherBulletsList.add(new Bullet(resultat.getInt("bullet.posX"), resultat.getInt("bullet.posY"), playerId, bulletId));
                    } else {
                        otherBulletsList.get(bulletIndex).setPosX(resultat.getInt("bullet.posX"));
                        otherBulletsList.get(bulletIndex).setPosY(resultat.getInt("bullet.posY"));
                    }
                }
            }
            requete.close();
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addPlayer(Player player){
        PreparedStatement requete;
        
        try {
            requete = connexion.prepareStatement("INSERT INTO players VALUES (?,?,?,?,?,?,?)"); // Check Hp & Skin work
            requete.setInt(1,player.getPlayerId());
            requete.setString(2, player.getName());
            requete.setDouble(3, player.getPlayerHeight());
            requete.setDouble(4, player.getPosX());
            requete.setDouble(5, player.getPosY());
            requete.setInt(6, player.getSkinIndex());
            requete.setInt(7, 0);
            requete.executeUpdate();
            requete.close();  
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void removePlayer(Player player){
        PreparedStatement requete;
        
        try {
            requete = connexion.prepareStatement("DELETE FROM players WHERE id=" + player.getPlayerId());
            requete.executeUpdate();
            requete.close();  
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void removeBullet(Bullet bullet){
        PreparedStatement requete;
        
        try {
            requete = connexion.prepareStatement("DELETE FROM bullet WHERE idBullet=" + bullet.getBulletId());
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
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public Connection getConnection(){
        return this.connexion;
    }
     
    public void disconnect(){
        try {
            connexion.close();
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setPlayerHp(double playerHp, Player player){               
        PreparedStatement requete;
        
        try {
            requete = connexion.prepareStatement("UPDATE players SET playerHp=?,  WHERE id=" + player.getPlayerId());
            requete.setDouble(1, player.getPlayerHealth()); 
            requete.executeUpdate();
            requete.close();
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
    
    public void addBullet(Bullet bullet){        
        PreparedStatement requete;
        
        try {
            requete = connexion.prepareStatement("INSERT INTO bullet VALUES (?,?,?,?)");
            requete.setInt(1, bullet.getBulletId());
            requete.setInt(2, bullet.getPlayerId());
            requete.setDouble(3, bullet.getPosX());
            requete.setDouble(4, bullet.getPosY());
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
                newPlayer.setPlayerState(resultat.getInt("statePlayer"));
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
        try {
            requete = connexion.prepareStatement("SELECT * FROM players WHERE NOT id ="+ player.getPlayerId());
            ResultSet resultat = requete.executeQuery();
            while (resultat.next()) {
                newPlayer = new Player(resultat.getDouble("posX"),resultat.getDouble("posY"));
                newPlayer.setPlayerId(resultat.getInt("id"));
                index = otherPlayersList.indexOf(newPlayer);
                if (index > -1){ // Player is already in the list, update variables
                    newPlayer = otherPlayersList.get(index);
                    newPlayer.setSkin(resultat.getInt("skinId"));
                    newPlayer.setName(resultat.getString("name"));
                    newPlayer.setPlayerState(resultat.getInt("statePlayer"));
                } else { // Player was not yet in the list
                    newPlayer.setSkin(resultat.getInt("skinId"));
                    newPlayer.setName(resultat.getString("name"));
                    newPlayer.setPlayerState(resultat.getInt("statePlayer"));
                    newPlayer.setHealth(resultat.getDouble("playerHp"));
                    otherPlayersList.add(newPlayer);
                }
            }
            requete.close();
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
