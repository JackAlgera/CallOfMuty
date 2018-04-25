package callofmuty;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import javax.swing.JFrame;
import javazoom.jl.decoder.JavaLayerException;

public class CallOfMuty {
    
    public static void main(String[] args) throws InterruptedException, IOException, JavaLayerException {
        
        int textureSize, mapWidth, mapHeight, maxFPS;
        long dT, minUpdateTime;
        
        // Game variables   
        maxFPS = 60;
        String frameTitle = "Call of µty";
        
        // Map dimensions
        textureSize=64;
        mapWidth= 16;
        mapHeight= 9;
        
        // Game initialisation
        GameTimer timer = new GameTimer();
        GamePanel game = new GamePanel(textureSize, mapWidth, mapHeight, timer);
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
        minUpdateTime =(long) 1000/maxFPS;
        
        while(true){
            switch (game.getState()){
                case GamePanel.PRE_GAME:
                    game.preGameUpdate();
                    game.repaint();
                    Thread.sleep(50);
                    timer.update();
                break;
                        
                case GamePanel.IN_GAME:
                    dT = timer.update();
                    game.updateGame(dT);
                    
                    if (dT < minUpdateTime) {
                        Thread.sleep(minUpdateTime - dT);
                    }
                    game.repaint();
                break;
                
                default:
                    Thread.sleep(50);
                break;
            }
        }
        //game.endGame();
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