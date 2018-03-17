package callofmuty;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class GamePanel extends JPanel{
    
    private Map map; 
    private Player player;
    ArrayList <Player> listPlayers = new ArrayList();
    private int textureSize, mapWidth, mapHeight, panelWidth, panelHeight;
    private ArrayList buttonsPressed;
    private boolean isHost;
    private SQLManager sql; 
    
    
    private static final int IFW = JPanel.WHEN_IN_FOCUSED_WINDOW; //usefull for the KeyBindings
    
    public void updateGame(long dT){
        
        int xDirection = 0, yDirection = 0;
        if (buttonsPressed.contains(KeyEvent.VK_DOWN)){
            yDirection +=1;
        }
        if (buttonsPressed.contains(KeyEvent.VK_LEFT)){
            xDirection +=-1;
        }
        if (buttonsPressed.contains(KeyEvent.VK_UP)){
            yDirection +=-1;
        }
        if (buttonsPressed.contains(KeyEvent.VK_RIGHT)){
            xDirection +=1;
        }
        player.update(xDirection, yDirection, dT); // need to place the player into the list of players
        updatePositionPlayerList();
    }
    
    /* // Use of KeyBindings
    public void mapKeys(){
        this.getInputMap().put(KeyStroke.getKeyStroke("UP"), "upPressed");
        this.getInputMap().put(KeyStroke.getKeyStroke("released UP"), "upReleased");
        this.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "downPressed");
        this.getInputMap().put(KeyStroke.getKeyStroke("released DOWN"), "downReleased");
        this.getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "leftPressed");
        this.getInputMap().put(KeyStroke.getKeyStroke("released LEFT"), "leftReleased");
        this.getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "rightPressed");
        this.getInputMap().put(KeyStroke.getKeyStroke("released RIGHT"), "rightReleased");
        this.getActionMap().put("upPressed", new KeyPressed(KeyEvent.VK_UP));
        this.getActionMap().put("upReleased", new KeyReleased(KeyEvent.VK_UP) );
        this.getActionMap().put("downPressed", new KeyPressed(KeyEvent.VK_DOWN));
        this.getActionMap().put("downReleased", new KeyReleased(KeyEvent.VK_DOWN) );
        this.getActionMap().put("leftPressed", new KeyPressed(KeyEvent.VK_LEFT));
        this.getActionMap().put("leftReleased", new KeyReleased(KeyEvent.VK_LEFT) );
        this.getActionMap().put("rightPressed", new KeyPressed(KeyEvent.VK_RIGHT));
        this.getActionMap().put("rightReleased", new KeyReleased(KeyEvent.VK_RIGHT) );
    }
    */

@Override
public void paint(Graphics g) {
    super.paint(g);
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
    RenderingHints.VALUE_ANTIALIAS_ON);
    map.draw(g2d);
    player.draw(g2d); // Need to put this player into the playerList then draw using the for loop 
    
    for(Player p : listPlayers)
    {
        if(p.getPlayerId() != player.getPlayerId())
        {
            p.draw(g2d);
        }
    }
}
    

    
    public GamePanel(int textureSize, int mapWidth, int mapHeight, boolean isHost){
        super();
        this.isHost = isHost;
        this.textureSize = textureSize;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        panelWidth = textureSize*mapWidth;
        panelHeight = textureSize*mapHeight;
        setPreferredSize(new Dimension(panelWidth, panelHeight));
        map = new Map(mapWidth, mapHeight, textureSize);
        player = new Player(100,100,textureSize,textureSize,new ImageIcon("images/sans.png").getImage());
        buttonsPressed = new ArrayList();
        //mapKeys();
        // Key listener : dooesn't work all the time. Use KeyBindings instead
        KeyListener listener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(!buttonsPressed.contains(e.getKeyCode())){
                    buttonsPressed.add(e.getKeyCode());
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(!buttonsPressed.contains(e.getKeyCode())){
                    buttonsPressed.add(e.getKeyCode());
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                buttonsPressed.remove((Integer)e.getKeyCode()); // (Integer) is necessary to remove the object instead of the index
            }
        };
        
        addKeyListener(listener);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Clicked on " + e.getX() + " ; " + e.getY());
            }@Override
            public void mouseEntered(MouseEvent e) {
                System.out.println("Entered on " + e.getX() + " ; " + e.getY());
            }@Override
            public void mouseExited(MouseEvent e) {
                System.out.println("Exited on " + e.getX() + " ; " + e.getY());
            }@Override
            public void mousePressed(MouseEvent e) {
                System.out.println("Pressed on " + e.getX() + " ; " + e.getY());
            }@Override
            public void mouseReleased(MouseEvent e) {
                System.out.println("Released on " + e.getX() + " ; " + e.getY());
            }
        });
	setFocusable(true);
    }
    
    /* //Use of KeyBindings
    private class KeyPressed extends AbstractAction{
        
        private int key;
        
        public KeyPressed(int key){
            this.key = key;
        }
        
        @Override
        public void actionPerformed( ActionEvent tf ){
            System.out.println("pressed");
            if(!buttonsPressed.contains(key)){
                buttonsPressed.add(key);
            }
        }
    }
    private class KeyReleased extends AbstractAction{
        
        private int key;
        
        public KeyReleased(int key){
            this.key = key;
        }
        
        @Override
        public void actionPerformed( ActionEvent tf ){
            System.out.println("released");
            if(buttonsPressed.contains(key)){
                buttonsPressed.remove((Integer)key);
            }
        }
    }
    */
    
    
    
    
    
    
    void updatePositionPlayerList()
    {
//        for (int i=0; i++; i<listPlayers.size())
//        {
//            if (i != player.getPlayerId())
//            {
//            float[] pos = sql.getPosition(i); // Get position of player with id=i
//            listPlayers.get(i).setPosition(pos); 
//            }
//        }
    }
    
    void initialiseGame()
    {
        if (isHost)
        {
            int playerId;
//            sql.restart(); //Clear previous game on SQL server
//            sql.addPlayer();
//            playerId = sql.getNumberOfPlayers(); //If host -> playerId = 0
//            player.setPlayerId(playerId);
        }
        else
        {
            int playerId;
//            sql.addPlayer();
//            playerId = sql.getNumberOfPlayers();
//            player.setPlayerId(playerId);
        }
    }
    
    void initialisePlayerList()
    {
//        int numberOfPlayers = sql.getNumberOfPlayers();
//        for (int i=0; i++; i<numberOfPlayers)
//        {
//            if (i != player.getPlayerId())
//            {
//                Player newPlayer = new Player(100,100,textureSize,textureSize,new ImageIcon("images/sans.png").getImage());
//                newPlayer.setPlayerId(i);
//                listPlayers.add(newPlayer);
//            } 
//            else
//            {
//                listPlayers.add(player);
//            }
//            
//        }
        
    }
    
}
