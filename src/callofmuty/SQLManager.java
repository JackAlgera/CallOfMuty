package callofmuty;

import java.util.ArrayList;
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
    
    public SQLManager(){         
        try {
            connexion=DriverManager.getConnection("jdbc:mysql://nemrod.ens2m.fr:3306/20172018_s2_vs2_tp4?serverTimezone=UTC","vs2tp4", "vs2tp4");
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public double[] getPosition(Player player){
        double[] stockagePosition = new double[2];
        
        PreparedStatement requete;
        try {
            requete = connexion.prepareStatement("SELECT posX,posY FROM players WHERE id=" + player.getPlayerId());
            ResultSet resultat = requete.executeQuery();
            while (resultat.next()) {
                stockagePosition[0] = resultat.getDouble("posX");
                stockagePosition[1] = resultat.getDouble("posY");
            }
            requete.close();
              
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return stockagePosition;    
    }
    
    public int getNumberOfPlayers(){
        PreparedStatement requete;
        int numberOfPlayers=0;
        
        try {
            requete = connexion.prepareStatement("SELECT COUNT(id) FROM players");
            ResultSet resultat = requete.executeQuery();
            while (resultat.next()) {
                numberOfPlayers = resultat.getInt("COUNT(id)");
            }
            requete.close();
              
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return numberOfPlayers;
    }
    
    public void setPlayerPosition(Player player){
        PreparedStatement requete;
        
        try {
            requete = connexion.prepareStatement("UPDATE players SET posX=?, posY=? WHERE id=" + player.getPlayerId());
            requete.setDouble(1, player.getPosX());
            requete.setDouble(2, player.getPosY());  
            requete.executeUpdate();
            requete.close();
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
    
    public void addPlayer(Player player){
        PreparedStatement requete;
        
        try {
            requete = connexion.prepareStatement("INSERT INTO players VALUES (?,?,?,?,?,?,?)");//a VERIFIER FONCTIONNEMENT pour hp ET SKIN
            requete.setInt(1,player.getPlayerId());
            requete.setString(2, "player_" + player.getPlayerId());
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
    
    public double getPlayerHp(Player player) {  
        double stockagePlayerHp = 0;
        PreparedStatement requete;
        
        try {
            requete = connexion.prepareStatement("SELECT playerHp FROM players WHERE id=" + player.getPlayerId());
            ResultSet resultat = requete.executeQuery();
            while (resultat.next()) {
                stockagePlayerHp = resultat.getDouble("playerHp");
            }
            requete.close();
              
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return stockagePlayerHp;    
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
    
    public double[] getPositionBullet(Bullet bullet){
        PreparedStatement requete;
        double[] stockagePosition = new double[2];
        
        try {
            requete = connexion.prepareStatement("SELECT posX,posY FROM bullet WHERE idBullet=" + bullet.getBulletId());
            ResultSet resultat = requete.executeQuery();
            while (resultat.next()){
                stockagePosition[0] = resultat.getDouble("posX");
                stockagePosition[1] = resultat.getDouble("posY");
            }
            requete.close();
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return stockagePosition;  
    }
            
    public double[] getPositionWithPlayerId(int idPlayer){
        double[] stockagePosition = new double[2];
        PreparedStatement requete;
        
        try {
            requete = connexion.prepareStatement("SELECT posX,posY FROM players WHERE id=" + idPlayer);
            ResultSet resultat = requete.executeQuery();
            while (resultat.next()) {
                stockagePosition[0] = resultat.getDouble("posX");
                stockagePosition[1] = resultat.getDouble("posY");
            }
            requete.close();
              
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return stockagePosition;    
    }
        
    public void setPositionBullet(Bullet bullet){
        PreparedStatement requete;
        
        try {
            requete = connexion.prepareStatement("UPDATE bullet SET posX=?, posY=? WHERE idBullet=" + bullet.getBulletId());
            requete.setDouble(1, bullet.getPosX());
            requete.setDouble(2, bullet.getPosY());  
            requete.executeUpdate();
            requete.close();
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
    
    public void addBullet(Bullet bullet){        
        PreparedStatement requete;
        
        try {
            requete = connexion.prepareStatement("INSERT INTO bullet VALUES (?,?,?,?)");//
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
    
    public int getNumberOfTotalBullets()
    {
        PreparedStatement requete;
        int numberOfBullets = 0;
        
        try {
            requete = connexion.prepareStatement("SELECT COUNT(idBullet) FROM bullet");
            ResultSet resultat = requete.executeQuery();
            while (resultat.next()) {
                numberOfBullets = resultat.getInt("COUNT(idBullet)");
            }
            requete.close();
              
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return numberOfBullets;
    }
    
    public int getLastBulletId()
    {
        PreparedStatement requete;
        int bulletId = 0;
        
        try {
            requete = connexion.prepareStatement("SELECT MAX(idBullet) FROM bullet");
            ResultSet resultat = requete.executeQuery();
            while (resultat.next()) {
                bulletId = resultat.getInt("MAX(idBullet)") + 1;
            }
            requete.close();
              
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bulletId;
    }
    
    public ArrayList<Bullet> getListOfOtherPlayersBullets(Player player){
        ArrayList<Bullet> listPositionBullets = new ArrayList();
        PreparedStatement requete;
        
        try {
            requete = connexion.prepareStatement("SELECT posX,posY FROM bullet WHERE idPlayer!=" + player.getPlayerId());
            ResultSet resultat = requete.executeQuery();
            while (resultat.next()) {
                listPositionBullets.add(new Bullet(resultat.getDouble("posX"),resultat.getDouble("posY")));
            }
            requete.close();
              
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listPositionBullets;
    }
}
