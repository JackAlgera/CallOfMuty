package callofmuty;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class CallOfMuty {
    
    public static void main(String[] args) throws InterruptedException {
        
        int textureSize, mapWidth, mapHeight, maxFPS;
        long dT, minUpdateTime;
        
        // Game variables   
        maxFPS = 60; 
        boolean isHost = true;
        
        // Map dimensions
        textureSize=64;
        mapWidth= 16;
        mapHeight= 9;
        String frameTitle = "Call of µty";
                
        // Game initialisation
        GameTimer timer = new GameTimer();
        GamePanel game = new GamePanel(textureSize, mapWidth, mapHeight, isHost);
        JFrame frame = createJFrame(frameTitle, game);
        WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showOptionDialog(
                        null, "Are you sure you want to quit ?",
                        "Exit Confirmation", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == 0) {
                    game.endGame();
                    System.exit(0);
                }
            }
        };
        frame.addWindowListener(exitListener);
        game.requestFocusInWindow();
        
        minUpdateTime =(long) 1000/maxFPS;
        
        game.initialiseGame();
        game.initialisePlayerList();
        timer.update();
        
        while(!game.isGameDone()){
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
