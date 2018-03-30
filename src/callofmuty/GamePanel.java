package callofmuty;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class GamePanel extends JPanel{

    public static BufferedImage Background = Tools.loadImage("image/Background");
    
    private Map map; 
    private Player player;
    private ArrayList <Player> listPlayers = new ArrayList();
    private int textureSize, mapWidth, mapHeight, panelWidth, panelHeight;
    private ArrayList pressedButtons, releasedButtons;
    private boolean isHost;
    private long playerListUpdateTime;
    private SQLManager sql; 
    private boolean endGame = false, isConnected;
    
    private int i=0;
    
    private static final int IFW = JPanel.WHEN_IN_FOCUSED_WINDOW; //usefull for the KeyBindings
    
    public GamePanel(int textureSize, int mapWidth, int mapHeight) throws IOException{
        super();
        this.Background = ImageIO.read(new File("images/Background.png"));
        this.playerListUpdateTime = 0;
        this.textureSize = textureSize;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.sql = new SQLManager();
        panelWidth = textureSize*mapWidth;
        panelHeight = textureSize*mapHeight;
        isConnected = false;
        setPreferredSize(new Dimension(panelWidth, panelHeight));
        //map = new Map(Tools.textFileToIntMap("testMap.txt"),textureSize);
        map = new Map(mapWidth, mapHeight, textureSize);
        player = new Player(200,200,textureSize,textureSize);
        pressedButtons = new ArrayList();
        releasedButtons = new ArrayList();
        mapKeys();
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
//                System.out.println("Clicked");
            }@Override
            public void mouseEntered(MouseEvent e) {
//                System.out.println("Entered on " + e.getX() + " ; " + e.getY());
            }@Override
            public void mouseExited(MouseEvent e) {
//                System.out.println("Exited on " + e.getX() + " ; " + e.getY());
            }@Override
            public void mousePressed(MouseEvent e) {
                double[] directionOfFire = new double[2];
                directionOfFire[0] = e.getX() - player.getPosX()-textureSize/2;
                directionOfFire[1] = e.getY() - player.getPosY()-textureSize/2;
                
                double norme = Math.sqrt(directionOfFire[0]*directionOfFire[0] + directionOfFire[1]*directionOfFire[1]);
                directionOfFire[0] = directionOfFire[0]/norme;
                directionOfFire[1] = directionOfFire[1]/norme;
                
                player.addBullet(player.getPosX()+textureSize/4, player.getPosY()+textureSize/4, directionOfFire, 1);
                
            }@Override
            public void mouseReleased(MouseEvent e) {
//                System.out.println("Released on " + e.getX() + " ; " + e.getY());
            }
        });
	setFocusable(true);
        //Tools.mapToTextFile(map, "testMap.txt");
    }
    
    public void updateGame(long dT){
        
        if (pressedButtons.contains(KeyEvent.VK_DOWN)){
            player.setAcceleration(1, 1);
            player.setDirectionOfTravel(1, 1);
        }
        if (pressedButtons.contains(KeyEvent.VK_UP)){
            player.setAcceleration(1, -1);
            player.setDirectionOfTravel(1, -1);
        }
        if (pressedButtons.contains(KeyEvent.VK_LEFT)){
            player.setAcceleration(0, -1);
            player.setDirectionOfTravel(0, -1);
        }
        if (pressedButtons.contains(KeyEvent.VK_RIGHT)){
            player.setAcceleration(0, 1);
            player.setDirectionOfTravel(0, 1);
        }
        
//        Deceleration
        if (releasedButtons.contains(KeyEvent.VK_DOWN)){
            player.reverseAcceleration(1);
            releasedButtons.remove((Integer)KeyEvent.VK_DOWN);
        }
        if (releasedButtons.contains(KeyEvent.VK_UP)){
            player.reverseAcceleration(1);
            releasedButtons.remove((Integer)KeyEvent.VK_UP);
        }
        if (releasedButtons.contains(KeyEvent.VK_LEFT)){
            player.reverseAcceleration(0);
            releasedButtons.remove((Integer)KeyEvent.VK_LEFT);
        }
        if (releasedButtons.contains(KeyEvent.VK_RIGHT)){
            player.reverseAcceleration(0);
            releasedButtons.remove((Integer)KeyEvent.VK_RIGHT);
        }
        
        player.update(dT, map); // To do : need to place the player into the list of players
        player.healthcheck();
        player.updateBulletImpact(dT, map, listPlayers);
        updatePositionPlayerList();
        sql.setPosition(player.getPosX(), player.getPosY(), player);
        /*
        //test for the dead state, and the respawn
        if (player.getplayerdeath()){
            i+=1;
        }
        if(player.getplayerhealth()==0 && i==100){
            player.setplayerhealth(100);
            i=0;
        }
        player.damageplayer(0.5);*/
    }
    
    // Use of KeyBindings
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
    
@Override
public void paint(Graphics g) {
    super.paint(g);
    Graphics2D g2d = (Graphics2D) g;
    if (!isConnected) {
        //g2d.drawImage(Background, 0, 0, 16*64, 9*64, this);
        g2d.drawImage(player.getImage(), (180-player.getPlayerWidth())/2, (panelHeight-player.getPlayerHeight())/2, 160, 160, this);
    } else {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        map.draw(g2d);
        player.draw(g2d); // To do : Need to put this player into the playerList then draw using the for loop 
        player.drawBullets(g2d, map.getTextureSize());

        for (Player p : listPlayers) {
            if (p.getPlayerId() != player.getPlayerId()) {
                p.draw(g2d);
            }
        }
    }
}
    
      //Use of KeyBindings
    private class KeyPressed extends AbstractAction{
        
        private int key;
        
        public KeyPressed(int key){
            this.key = key;
        }
        
        @Override
        public void actionPerformed( ActionEvent tf ){
//            System.out.println(key);
            if(!pressedButtons.contains(key)){
                pressedButtons.add(key);
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
//            System.out.println(key);
            if(pressedButtons.contains(key)){
                pressedButtons.remove((Integer)key);
                releasedButtons.add(key);
            }
        }
    }
    
    public void updatePositionPlayerList()
    {
        for (int i=0; i<listPlayers.size() ;i++ )
        {
            if (i != player.getPlayerId())
            {
            double[] pos = sql.getPositionWithPlayerId(i); // Get position of player with id=i
            listPlayers.get(i).setPosition(pos); 
            }
        }
        
    }
    
    public void initialiseGame(boolean isHost){
        this.isHost = isHost;
        if (isHost){
            int playerId;
            sql.clearTable(); //Clear previous game on SQL server
            playerId = sql.getNumberOfPlayers(); //If host -> playerId = 0
            player.setPlayerId(playerId);
            sql.addPlayer(player);
        } else {
            int playerId;
            playerId = sql.getNumberOfPlayers();
            player.setPlayerId(playerId);
            sql.addPlayer(player);
        }
        isConnected = true;
    }
    
    public boolean isConnected(){
        return isConnected;
    }
    
    public void endGame() {
        sql.removePlayer(player);
        sql.disconnect();
    }
    
    public void initialisePlayerList()
    {
        int numberOfPlayers = sql.getNumberOfPlayers();
        for(int i=0;i<numberOfPlayers;i++)
        {
            if (i != player.getPlayerId())
            {
                double[] posNewPlayer = sql.getPositionWithPlayerId(i);
                Player newPlayer = new Player(posNewPlayer[0],posNewPlayer[1],textureSize,textureSize);
                newPlayer.setPlayerId(i);
                newPlayer.setSkin(4);
                listPlayers.add(newPlayer);
            } 
            else
            {
                listPlayers.add(player);
            }
        }
    }
    
    public void updatePlayerList(long dT)
    {
        playerListUpdateTime += dT;
        if (playerListUpdateTime > 1000)
        {
            playerListUpdateTime -= 1000;
            int numberOfPlayers = sql.getNumberOfPlayers();
            listPlayers.clear();;
            for(int i=0;i<numberOfPlayers;i++)
            {
                if (i != player.getPlayerId())
                {
                    double[] posNewPlayer = sql.getPositionWithPlayerId(i);
                    Player newPlayer = new Player(posNewPlayer[0],posNewPlayer[1],textureSize,textureSize);
                    newPlayer.setSkin(4);
                    newPlayer.setPlayerId(i);
                    listPlayers.add(newPlayer);
                } 
                else
                {
                    listPlayers.add(player);
                }
            }
        }
        else
        {
            playerListUpdateTime += dT;
        }
        
    }
    
    public boolean isGameDone()
    {
        return endGame;
    }
    
    public Player getPlayer(){
        return player;
    }
}
