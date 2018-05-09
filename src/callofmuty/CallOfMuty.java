package callofmuty;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

public class CallOfMuty {
    
    public static void main(String[] args) {

        int textureSize, mapWidth, mapHeight, maxFPS;
        long dT, minUpdateTime;

        // Game variables   
        maxFPS = 60;
        String frameTitle = "Call of µty";

        // Map dimensions
        textureSize = 64;
        mapWidth = 24;
        mapHeight = 13;

        // Game initialisation
        GameTimer timer = new GameTimer();
        GamePanel game = new GamePanel(textureSize, mapWidth, mapHeight, timer);
        JFrame frame = createJFrame(frameTitle, game);
        game.setFrame(frame);
        
        // window listeners
        WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                game.quitGame();
            }
        };
        frame.addWindowListener(exitListener);

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if(frame.isResizable()){
                    game.updateSize();
                }
            }
        });
        
        frame.addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged(WindowEvent e) {
                if ((e.getNewState() & Frame.ICONIFIED) == Frame.ICONIFIED) { // minimized
                    
                } else {
                    if(frame.isResizable()){
                        game.updateSize();
                    }
                }
            }
        });
        
        // correct frame size if too big for screen
        // Commented out because buggy and not really needed, just resize the frame manually
        /*Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double widthRatio = (double)game.getOriginalWidth()/0.9/screenSize.width;
        double heightRatio = (double)game.getOriginalHeight()/0.9/screenSize.height;
        if(widthRatio>1 || heightRatio>1){
            double ratio = Math.max(widthRatio, heightRatio);
            game.setPreferredDimensions(ratio);
        }
        */
        
        // last steps
        
        game.requestFocusInWindow();
        frame.pack();
        frame.setLocationRelativeTo(null); // appears at the centre of the screen
        game.buildInterface();
        frame.setVisible(true);
        minUpdateTime = (long) 1000 / maxFPS;

        while (true) {
            dT = timer.update();
            switch (game.getState()) {
                case GamePanel.PRE_GAME:
                    game.preGameUpdate();
                    game.repaint();
                     {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(CallOfMuty.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    timer.update();
                    break;

                case GamePanel.IN_GAME:
                    game.updateGame(dT);
                    long timeSinceUpdate = timer.getDT(false);
                    if (timeSinceUpdate < minUpdateTime) {
                        try {
                            Thread.sleep(minUpdateTime - timeSinceUpdate);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(CallOfMuty.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    game.repaint();
                    break;
                    
                case GamePanel.MAIN_MENU: //Animation during main menu skin selecing 
                    game.updatePlayerAnimation(dT);
                    game.repaint();
                    break;
                    
                default:
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(CallOfMuty.class.getName()).log(Level.SEVERE, null, ex);
                    }
                break;
            }
        }
        //game.endGame();
    }
    
    private static JFrame createJFrame(String frameTitle, GamePanel game) {
        JFrame frame = new JFrame();
        frame.setTitle(frameTitle);
        frame.setBackground(Color.black);
        frame.setResizable(true);
        //frame.setUndecorated(true); // takes off the border of the frame
        frame.setVisible(false);
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLocationRelativeTo(null); // appears at the centre of the screen
        return frame;
    }
}