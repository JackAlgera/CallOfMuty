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
        int idPlayer = player.getPlayerId(); 
        double[] stockagePosition = {0,0};
        
        PreparedStatement requete;
        try {
            requete = connexion.prepareStatement("SELECT x,y FROM players WHERE id="+idPlayer);
            ResultSet resultat = requete.executeQuery();
            while (resultat.next()) {
                stockagePosition[0] = resultat.getDouble("x");
                stockagePosition[1] = resultat.getDouble("y");
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
            requete = connexion.prepareStatement("SELECT COUNT(id) FROM players ");
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
    
    public void setPosition(double posX, double posY, Player player){
        int idPlayer = player.getPlayerId(); 
               
        PreparedStatement requete;
        try {
            requete = connexion.prepareStatement("UPDATE players SET x=?, y=? WHERE id=" + idPlayer);
            requete.setDouble(1, posX);
            requete.setDouble(2, posY);  
            requete.executeUpdate();
            requete.close();
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
    
    public void addPlayer(Player player){
        int idPlayer = player.getPlayerId(); 
        double playerHp = player.getplayerhealth();
        int playerSkin=player.getSkinIndex();
        PreparedStatement requete;
        try {
            requete = connexion.prepareStatement("INSERT INTO players VALUES (?,?,?,?,?,?)");//a VERIFIER FONCTIONNEMENT pour hp ET SKIN
            requete.setInt(1,idPlayer);
            requete.setString(2, "player" +idPlayer);
            requete.setDouble(3, playerHp);
            requete.setDouble(4, player.getPosX());
            requete.setDouble(5, player.getPosY());
            requete.setInt(6,playerSkin);
            requete.executeUpdate();

            requete.close();  
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void removePlayer(Player player){
        int idPlayer = player.getPlayerId(); 
        PreparedStatement requete;
        try {
            requete = connexion.prepareStatement("DELETE FROM players WHERE id="+idPlayer);//("INSERT INTO players VALUES (?,?,?,?,?)");
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

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    Connection getConnection(){
        
        return this.connexion;
    }
     
    public void disconnect(){
        try {
            connexion.close();
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public double getplayerHp(Player player) {  
        int idPlayer;
        idPlayer= player.getPlayerId(); 
        double stockagePlayerHp = 0;
        
        PreparedStatement requete;
        try {
            double positionAbscisse, positionOrdonnee;
            requete = connexion.prepareStatement("SELECT playerHp FROM players WHERE id="+idPlayer);
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
        int idPlayer = player.getPlayerId(); 
               
        PreparedStatement requete;
        try {
            requete = connexion.prepareStatement("UPDATE players SET playerHp=?,  WHERE id=" + idPlayer);
            requete.setDouble(1, playerHp); 
            requete.executeUpdate();
            requete.close();
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
    
    public double[] getPositionBullet(Bullet bullet){
        int idBullet = bullet.getBulletId(); 
        double positionAbscisse, positionOrdonnee;
        PreparedStatement requete;
        double[] stockagePosition = {0,0};
        
        try {
            requete = connexion.prepareStatement("SELECT posX,posY FROM bullet WHERE id="+idBullet);
            ResultSet resultat = requete.executeQuery();
            while (resultat.next()){
                positionAbscisse = resultat.getDouble("posX");
                positionOrdonnee = resultat.getDouble("posY");
                stockagePosition[0] = positionAbscisse ;
                stockagePosition[1] = positionOrdonnee;
            }
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return stockagePosition;  
    }
            
    public double[] getPositionWithPlayerId(int idPlayer){
        double[] stockagePosition = {0,0};
        PreparedStatement requete;
        
        try {
            requete = connexion.prepareStatement("SELECT x,y FROM players WHERE id="+idPlayer);
            ResultSet resultat = requete.executeQuery();
            while (resultat.next()) {
                stockagePosition[0] = resultat.getDouble("x");
                stockagePosition[1] = resultat.getDouble("y");
            }
        requete.close();
              
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return stockagePosition;    
    }
        
    public void setPositionBullet(double posX, double posY, Bullet bullet){
        int idBullet = bullet.getBulletId(); 
               
        PreparedStatement requete;
        try {
            requete = connexion.prepareStatement("UPDATE players SET posX=?, posY=? WHERE id=" + idBullet);
            requete.setDouble(1, posX);
            requete.setDouble(2, posY);  
            requete.executeUpdate();
            requete.close();
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
    
    public void addBullet(Bullet bullet){
        int idBullet = bullet.getBulletId(); 
        int idOwner = bullet.getPlayerId(); //voir comment sappelle cette fonction
        double abscisseBullet=bullet.getPosX();
        double ordonneBullet=bullet.getPosY(); 
        
        PreparedStatement requete;
        try {
            requete = connexion.prepareStatement("INSERT INTO bullet VALUES (?,?,?,?)");//
            requete.setInt(1,idBullet);
            requete.setInt(2,idOwner);
            requete.setDouble(3, abscisseBullet);
            requete.setDouble(4, ordonneBullet);
            requete.executeUpdate();

            requete.close();  
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public Bullet[] getBulletListOfPlayer(Player player){
         
        int idPlayer = player.getPlayerId(); //voir comment sappelle cette fonction
        Bullet[] listBullets = null;
        int i=0;
        
        PreparedStatement requete;
        
          try {
            requete = connexion.prepareStatement("SELECT * FROM Bullet WHERE id="+idPlayer);
            ResultSet resultat = requete.executeQuery();
            while (resultat.next()) {
                listBullets[i] = new Bullet(resultat.getDouble("posX"),resultat.getDouble("posY"),resultat.getDouble("speedX"),resultat.getDouble("speedY"),0.5,resultat.getInt("playerId"));
                i++;
            }
        requete.close();
              
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return listBullets;
    }
    
    public double[] getBulletListExceptPlayer(Player player){
         
        int idPlayer = player.getPlayerId(); //voir comment sappelle cette fonction
        double[] listPositionBullets = null;
        int i=0;
        
        PreparedStatement requete;
        
          try {
            requete = connexion.prepareStatement("SELECT * FROM Bullet WHERE id!="+idPlayer);
            ResultSet resultat = requete.executeQuery();
            while (resultat.next()) {
                listPositionBullets[2*i] = resultat.getDouble("posX");
                listPositionBullets[2*i+1] = resultat.getDouble("posY");
                i++;
            }
        requete.close();
              
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return listPositionBullets;
    }
}
