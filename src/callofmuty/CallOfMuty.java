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
        
        ImageIcon joinGameIcon = new ImageIcon("images/Buttons/JoinGame.png"),
                createGameIcon = new ImageIcon("images/Buttons/CreateGame.png"),
                leftArrowIcon = new ImageIcon("images/Buttons/LeftArrow.png"),
                rightArrowIcon = new ImageIcon("images/Buttons/rightArrow.png"),
                exitIcon = new ImageIcon("images/Buttons/Exit.png"),
                gameModeIcon = new ImageIcon("images/Buttons/GameMode.png");
        
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
                quitGame(game);
            }
        };
        
        // Interface construction
        game.setLayout(null);
        
        JButton connectButton = new JButton();
        connectButton.setIcon(joinGameIcon);
        connectButton.setVisible(true);
        connectButton.setBounds(286, 300, joinGameIcon.getIconWidth(), joinGameIcon.getIconHeight());
        //connectButton.setPressedIcon(pressedJoinGameIcon);
        connectButton.setContentAreaFilled(false);
        connectButton.setBorderPainted(false);
        game.add(connectButton);
        
        JButton gameCreateButton = new JButton();
        gameCreateButton.setIcon(createGameIcon);
        gameCreateButton.setBounds(286, 227, createGameIcon.getIconWidth(), createGameIcon.getIconHeight());
        //gameCreateButton.setPressedIcon(pressedcreateGameIcon);
        gameCreateButton.setVisible(true);
        gameCreateButton.setContentAreaFilled(false);
        gameCreateButton.setBorderPainted(false);
        game.add(gameCreateButton);
        
        JButton exitButton = new JButton();
        exitButton.setIcon(exitIcon);
        exitButton.setBounds(286, 373, exitIcon.getIconWidth(), exitIcon.getIconHeight());
        //exitButton.setPressedIcon(pressedExitIcon);
        exitButton.setVisible(true);
        exitButton.setContentAreaFilled(false);
        exitButton.setBorderPainted(false);
        game.add(exitButton);
        
        JButton gameModeButton = new JButton();
        gameModeButton.setIcon(gameModeIcon);
        gameModeButton.setBounds(286, 154, gameModeIcon.getIconWidth(), gameModeIcon.getIconHeight());
        //gameModeButton.setPressedIcon(gameModeIcon);
        gameModeButton.setVisible(true);
        gameModeButton.setContentAreaFilled(false);
        gameModeButton.setBorderPainted(false);
        game.add(gameModeButton);
        
        JButton rightSkinArrow = new JButton();
        rightSkinArrow.setIcon(rightArrowIcon);
        rightSkinArrow.setBounds(181, 440, rightArrowIcon.getIconWidth(), rightArrowIcon.getIconHeight());
        //rightSkinArrow.setPressedIcon(pressedrightArrowIcon);
        rightSkinArrow.setVisible(true);
        rightSkinArrow.setContentAreaFilled(false);
        rightSkinArrow.setBorderPainted(false);
        game.add(rightSkinArrow);
        
        JButton leftSkinArrow = new JButton();
        leftSkinArrow.setIcon(leftArrowIcon);
        leftSkinArrow.setBounds(55, 440, leftArrowIcon.getIconWidth(), leftArrowIcon.getIconHeight());
        //leftSkinArrow.setPressedIcon(pressedleftArrowIcon);
        leftSkinArrow.setVisible(true);
        leftSkinArrow.setContentAreaFilled(false);
        leftSkinArrow.setBorderPainted(false);
        game.add(leftSkinArrow);
        
        JButton rightMapArrow = new JButton();
        rightMapArrow.setIcon(rightArrowIcon);
        rightMapArrow.setBounds(820, 440, rightArrowIcon.getIconWidth(), rightArrowIcon.getIconHeight());
        //rightMapArrow.setPressedIcon(pressedrightArrowIcon);
        rightMapArrow.setVisible(true);
        rightMapArrow.setContentAreaFilled(false);
        rightMapArrow.setBorderPainted(false);
        game.add(rightMapArrow);
        
        JButton leftMapArrow = new JButton();
        leftMapArrow.setIcon(leftArrowIcon);
        leftMapArrow.setBounds(640, 440, leftArrowIcon.getIconWidth(), leftArrowIcon.getIconHeight());
        //leftMapArrow.setPressedIcon(pressedleftArrowIcon);
        leftMapArrow.setVisible(true);
        leftMapArrow.setContentAreaFilled(false);
        leftMapArrow.setBorderPainted(false);
        game.add(leftMapArrow);
        
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                game.initialiseGame(false);
                connectButton.setVisible(false);
                gameCreateButton.setVisible(false);
            }
        });
        
        gameCreateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                game.initialiseGame(true);
                connectButton.setVisible(false);
                gameCreateButton.setVisible(false);
            }
        });
        
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                quitGame(game);
            }
        });
        
        gameModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // to do
            }
        });
        
        rightSkinArrow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int skinIndex = game.getPlayer().getSkinIndex();
                skinIndex = (skinIndex%5)+1;
                game.getPlayer().setSkin(skinIndex);
                game.repaint();
            }
        });
        
        leftSkinArrow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int skinIndex = game.getPlayer().getSkinIndex();
                skinIndex--;
                if (skinIndex<1){
                    skinIndex=5;
                }
                game.getPlayer().setSkin(skinIndex);
                game.repaint();
            }
        });
        
        rightMapArrow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // to do
            }
        });
        
        leftMapArrow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // toudou
            }
        });
        
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
            game.updatePlayerList(dT);
            game.repaint();
        }
        game.endGame();
    }
    
    private static void quitGame(GamePanel game) {
        int confirm = JOptionPane.showOptionDialog(
                null, "Are you sure you want to quit ?",
                "Quit the game", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (confirm == 0) {
            game.endGame();
            System.exit(0);
        }
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
