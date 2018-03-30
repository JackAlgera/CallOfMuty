package callofmuty;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
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
        boolean isHost;
        
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
                int confirm = JOptionPane.showOptionDialog(
                        null, "Voules-vous vraiment quitter ?",
                        "Confirmation de fermeture", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == 0) {
                    game.endGame();
                    System.exit(0);
                }
            }
        };
        game.setLayout(null);
        JButton connectButton = new JButton();
        connectButton.setIcon(new ImageIcon("images/Buttons/JoinGame.png"));
        connectButton.setVisible(true);
        connectButton.setAlignmentX(200);
        connectButton.setAlignmentY(500);
        JButton gameCreateButton = new JButton("Créer une partie");
        gameCreateButton.setBounds(100, 100, 150, 40);
        gameCreateButton.setVisible(true);
        JButton skinButton = new JButton("Changer d'apparence");
        skinButton.setBounds(100, 200, 150, 40);
        skinButton.setVisible(true);
        
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                game.initialiseGame(false);
                connectButton.setVisible(false);
                gameCreateButton.setVisible(false);
            }
        });
        game.add(connectButton);
        
        gameCreateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                game.initialiseGame(true);
                connectButton.setVisible(false);
                gameCreateButton.setVisible(false);
            }
        });
        game.add(gameCreateButton);
        
        skinButton.addActionListener(new ActionListener() {
            int skinIndex = 1;
            public void actionPerformed(ActionEvent e) {
                skinIndex = (skinIndex%5)+1;
                game.getPlayer().setSkin(skinIndex);
                game.repaint();
            }
        });
        game.add(skinButton);
        
        frame.addWindowListener(exitListener);
        game.requestFocusInWindow();
        game.revalidate();
        
        while (!game.isConnected()){
            Thread.sleep(1000);
        }
        
        minUpdateTime =(long) 1000/maxFPS;
        
        game.initialisePlayerList();
        timer.update();
        
        while(!game.isGameDone()){
            dT = timer.update();
            game.updateGame(dT);
            if (dT<minUpdateTime){
                Thread.sleep(minUpdateTime-dT);
            }
            game.updatePlayerList(dT);
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
