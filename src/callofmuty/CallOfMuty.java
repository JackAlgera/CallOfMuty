package callofmuty;

import javax.swing.JFrame;

public class CallOfMuty {
    
    public static void main(String[] args) throws InterruptedException {
        GameTimer timer = new GameTimer();
        
        int textureSize, mapWidth, mapHeight, maxFPS;
        long dT, minUpdateTime;
        
        textureSize=64;
        mapWidth= 16;
        mapHeight= 9;
        String frameTitle = "Call of µty";
        maxFPS = 60;
        
        minUpdateTime =(long) 1000/maxFPS;
        
        GamePanel game = new GamePanel(textureSize, mapWidth, mapHeight);
        JFrame frame = createJFrame(frameTitle, game);
        
        timer.update();
        
        while(true){
            dT = timer.update();
            game.updateGame(dT);
            dT =timer.getDT();
            if (dT<minUpdateTime){
                Thread.sleep(minUpdateTime-dT);
            }
            game.repaint();
        }
    }
    
        private static JFrame createJFrame(String frameTitle, GamePanel game){
        JFrame frame = new JFrame();
        frame.setTitle(frameTitle);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // stops application when frame is closed
        frame.setResizable(false);
        frame.setVisible(true);
        frame.add(game);
        frame.pack();
        frame.setLocationRelativeTo(null); // appears at the centre of the screen
        return frame;
    }
}
