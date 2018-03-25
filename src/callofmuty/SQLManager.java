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
        PreparedStatement requete;
        try {
            requete = connexion.prepareStatement("INSERT INTO players VALUES (?,?,?,?,?)");
            requete.setInt(1,idPlayer);
            requete.setString(2, "player" + idPlayer);
            requete.setDouble(3, 5);
            requete.setDouble(4, player.getPosX());
            requete.setDouble(5, player.getPosY());
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
}
