package callofmuty;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class CallOfMuty {
    
    public static void main(String[] args) throws InterruptedException, IOException {
        
        int textureSize, mapWidth, mapHeight, maxFPS;
        long dT, minUpdateTime;
        
        // Game variables   
        maxFPS = 60; 
        
        // Map dimensions
        textureSize=64;
        mapWidth= 16;
        mapHeight= 9;
        String frameTitle = "Call of µty";
        
        // Game initialisation
        GameTimer timer = new GameTimer();
        GamePanel game = new GamePanel(textureSize, mapWidth, mapHeight);
        JFrame frame = createJFrame(frameTitle, game);
        WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                game.quitGame();
            }
        };
        
        frame.addWindowListener(exitListener);
        game.requestFocusInWindow();
        game.revalidate();
        game.repaint();
        
        // Game loop
        while (!game.isConnected()){
            Thread.sleep(50);
        }
        
        minUpdateTime =(long) 1000/maxFPS;
        
        game.initialisePlayerList();
        timer.update();
        
        while(game.getState()==GamePanel.IN_GAME){
            dT = timer.update();
            game.updateGame(dT);
            if (dT<minUpdateTime){
                Thread.sleep(minUpdateTime-dT);
            }
//            game.updatePlayerList(dT);
            game.repaint();
        }
        game.endGame();
    }
    
    private static JFrame createJFrame(String frameTitle, GamePanel game) {
        JFrame frame = new JFrame();
        frame.setTitle(frameTitle);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLocationRelativeTo(null); // appears at the centre of the screen
        return frame;
    }
}
