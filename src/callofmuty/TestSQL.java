/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package callofmuty;

import java.sql.SQLException;
import javax.swing.ImageIcon;

/**
 *
 * @author cfache
 */
public class TestSQL {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {
        SQLManager jackscrummaster= new SQLManager();
        Player player = new Player(100,100,32,32,new ImageIcon("images/sans.png").getImage());
        Player player1 = new Player(50,100,32,32,new ImageIcon("images/sans.png").getImage());
        
        player1.setPlayerId(6);
        jackscrummaster.setPosition(60.0,20.0,player1);
        jackscrummaster.addPlayer(player1);
        
        double[] pos = jackscrummaster.getPosition(player1);
        System.out.println(pos[0]);
        //jackscrummaster.clearTable();
        jackscrummaster.getConnection().close();
        
        // TODO code application logic here
    }
    
}
