package callofmuty;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestSQL {
    public static void main(String[] args){
        try {
            Connection connexion=DriverManager.getConnection("jdbc:mysql://nemrod.ens2m.fr:3306/20172018_s2_vs2_tp4?serverTimezone=UTC","vs2tp4", "vs2tp4");
            PreparedStatement requete = connexion.prepareStatement("SELECT players.id, players.posX, players.posY, players.playerHp, players.gunId, players.isTaunting, bullet.idBullet, bullet.posX, bullet.posY, bullet.bulletType, bullet.isActive, items.itemId, items.itemType, items.x, items.y, items.isActive FROM (players LEFT JOIN bullet ON players.id=bullet.idPlayer AND bullet.isActive=1 AND NOT players.id=1) left join items on players.id=items.playerId and items.isActive=1 WHERE players.playerState = 1 ORDER BY players.id ");
            ResultSet resultat = requete.executeQuery();
            if(resultat.next()){
            System.out.println("NULL is "+resultat.getInt("bullet.idBullet"));
            }
            requete.close();
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}