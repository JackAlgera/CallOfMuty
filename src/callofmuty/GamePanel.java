package callofmuty;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class GamePanel extends JPanel{

    public static BufferedImage MenuBackground = Tools.loadImage("image/MenuBackground");
    public static BufferedImage EditorBackground = Tools.loadImage("image/EditorBackground");

    ImageIcon joinGameIcon = new ImageIcon("images/Buttons/JoinGame.png"),
            createGameIcon = new ImageIcon("images/Buttons/CreateGame.png"),
            leftArrowIcon = new ImageIcon("images/Buttons/LeftArrow.png"),
            rightArrowIcon = new ImageIcon("images/Buttons/rightArrow.png"),
            exitIcon = new ImageIcon("images/Buttons/Exit.png"),
            mapEditorIcon = new ImageIcon("images/Buttons/EditMaps.png"),
            saveMapIcon = new ImageIcon("images/buttons/Save.png"),
            loadMapIcon = new ImageIcon("images/buttons/Load.png"),
            doneIcon = new ImageIcon("images/buttons/Done.png"),
            gameModeIcon = new ImageIcon("images/Buttons/GameMode.png");

    private Map map;
    private TileSelector tileSelector;
    private Player player;
    private ArrayList <Player> listPlayers = new ArrayList();
    private int textureSize, mapWidth, mapHeight, panelWidth, panelHeight, gameState;
    private ArrayList pressedButtons, releasedButtons;
    private boolean isHost;
    private long playerListUpdateTime;
    private SQLManager sql; 
    private boolean isConnected;
    private ArrayList <JButton> MMbuttons, MEbuttons, PGbuttons;
    
    private int i=0;
    
    public static final int IFW = JPanel.WHEN_IN_FOCUSED_WINDOW, MAIN_MENU = 0, IN_GAME = 1, MAP_EDITOR = 2, PRE_GAME = 3;
    
    public GamePanel(int textureSize, int mapWidth, int mapHeight) throws IOException{
        super();
        gameState = MAIN_MENU;
        MenuBackground = ImageIO.read(new File("images/MenuBackground.png"));
        EditorBackground = ImageIO.read(new File("images/EditorBackground.png"));
        playerListUpdateTime = 0;
        this.textureSize = textureSize;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        panelWidth = textureSize*mapWidth;
        panelHeight = textureSize*mapHeight;
        isConnected = false;
        setPreferredSize(new Dimension(panelWidth, panelHeight));
        map = new Map(mapWidth, mapHeight, textureSize);
        tileSelector = new TileSelector(textureSize);
        map.setDrawingParameters(MAIN_MENU); // small map in main menu
        player = new Player(200,200);
        pressedButtons = new ArrayList();
        releasedButtons = new ArrayList();
        mapKeys();
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }@Override
            public void mouseEntered(MouseEvent e) {
            }@Override
            public void mouseExited(MouseEvent e) {
            }@Override
            public void mousePressed(MouseEvent e) {
                switch (gameState) {
                case IN_GAME:
                    double[] directionOfFire = new double[2];
                    directionOfFire[0] = e.getX() - player.getPosX() - textureSize / 2;
                    directionOfFire[1] = e.getY() - player.getPosY() - textureSize / 2;

                    double norme = Math.sqrt(directionOfFire[0] * directionOfFire[0] + directionOfFire[1] * directionOfFire[1]);
                    directionOfFire[0] = directionOfFire[0] / norme;
                    directionOfFire[1] = directionOfFire[1] / norme;

                    player.addBullet(player.getPosX() + textureSize / 4, player.getPosY() + textureSize / 4, directionOfFire, 0.5, sql);
                    break;
                case MAP_EDITOR:
                    int[] mapClicked = map.clickedTile(e.getX(), e.getY());
                    if (mapClicked[0]>-1){
                        map.setTile(mapClicked[1], mapClicked[2], tileSelector.getSelectedTile());
                    } else {
                        tileSelector.clickedTile(e.getX(), e.getY());
                    }
                    repaint();
                    break;
                default:
                }
            }@Override
            public void mouseReleased(MouseEvent e) {
            }
        });
	setFocusable(true);
        buildInterface();        
    }
    
    private void buildInterface(){
        setLayout(null);
        MMbuttons = new ArrayList(); //MM : Main menu
        MEbuttons = new ArrayList(); //ME : Map Editor
        PGbuttons = new ArrayList(); // Pre game
        
        
        JButton startButton = new JButton("Start");
        startButton.setVisible(false);
        startButton.setBounds(286, 300, joinGameIcon.getIconWidth(), joinGameIcon.getIconHeight());
        //connectButton.setPressedIcon(pressedJoinGameIcon);
        add(startButton);
        PGbuttons.add(startButton);
        
        
        
        JButton connectButton = new JButton();
        connectButton.setIcon(joinGameIcon);
        connectButton.setVisible(true);
        connectButton.setBounds(286, 300, joinGameIcon.getIconWidth(), joinGameIcon.getIconHeight());
        //connectButton.setPressedIcon(pressedJoinGameIcon);
        connectButton.setContentAreaFilled(false);
        connectButton.setBorderPainted(false);
        add(connectButton);
        MMbuttons.add(connectButton);
        
        JButton gameCreateButton = new JButton();
        gameCreateButton.setIcon(createGameIcon);
        gameCreateButton.setBounds(286, 227, createGameIcon.getIconWidth(), createGameIcon.getIconHeight());
        //gameCreateButton.setPressedIcon(pressedcreateGameIcon);
        gameCreateButton.setVisible(true);
        gameCreateButton.setContentAreaFilled(false);
        gameCreateButton.setBorderPainted(false);
        add(gameCreateButton);
        MMbuttons.add(gameCreateButton);
        
        JButton exitButton = new JButton();
        exitButton.setIcon(exitIcon);
        exitButton.setBounds(286, 373, exitIcon.getIconWidth(), exitIcon.getIconHeight());
        //exitButton.setPressedIcon(pressedExitIcon);
        exitButton.setVisible(true);
        exitButton.setContentAreaFilled(false);
        exitButton.setBorderPainted(false);
        add(exitButton);
        MMbuttons.add(exitButton);
        
        JButton gameModeButton = new JButton();
        gameModeButton.setIcon(gameModeIcon);
        gameModeButton.setBounds(286, 154, gameModeIcon.getIconWidth(), gameModeIcon.getIconHeight());
        //gameModeButton.setPressedIcon(pressedGameModeIcon);
        gameModeButton.setVisible(true);
        gameModeButton.setContentAreaFilled(false);
        gameModeButton.setBorderPainted(false);
        add(gameModeButton);
        MMbuttons.add(gameModeButton);
        
        JButton rightSkinArrow = new JButton();
        rightSkinArrow.setIcon(rightArrowIcon);
        rightSkinArrow.setBounds(181, 440, rightArrowIcon.getIconWidth(), rightArrowIcon.getIconHeight());
        //rightSkinArrow.setPressedIcon(pressedrightArrowIcon);
        rightSkinArrow.setVisible(true);
        rightSkinArrow.setContentAreaFilled(false);
        rightSkinArrow.setBorderPainted(false);
        add(rightSkinArrow);
        MMbuttons.add(rightSkinArrow);
        
        JButton leftSkinArrow = new JButton();
        leftSkinArrow.setIcon(leftArrowIcon);
        leftSkinArrow.setBounds(55, 440, leftArrowIcon.getIconWidth(), leftArrowIcon.getIconHeight());
        //leftSkinArrow.setPressedIcon(pressedleftArrowIcon);
        leftSkinArrow.setVisible(true);
        leftSkinArrow.setContentAreaFilled(false);
        leftSkinArrow.setBorderPainted(false);
        add(leftSkinArrow);
        MMbuttons.add(leftSkinArrow);
        
        JButton rightMapArrow = new JButton();
        rightMapArrow.setIcon(rightArrowIcon);
        rightMapArrow.setBounds(820, 440, rightArrowIcon.getIconWidth(), rightArrowIcon.getIconHeight());
        //rightMapArrow.setPressedIcon(pressedrightArrowIcon);
        rightMapArrow.setVisible(true);
        rightMapArrow.setContentAreaFilled(false);
        rightMapArrow.setBorderPainted(false);
        add(rightMapArrow);
        MMbuttons.add(rightMapArrow);
        
        JButton leftMapArrow = new JButton();
        leftMapArrow.setIcon(leftArrowIcon);
        leftMapArrow.setBounds(640, 440, leftArrowIcon.getIconWidth(), leftArrowIcon.getIconHeight());
        //leftMapArrow.setPressedIcon(pressedleftArrowIcon);
        leftMapArrow.setVisible(true);
        leftMapArrow.setContentAreaFilled(false);
        leftMapArrow.setBorderPainted(false);
        add(leftMapArrow);
        MMbuttons.add(leftMapArrow);
        
        JButton mapEditorButton = new JButton();
        mapEditorButton.setIcon(mapEditorIcon);
        mapEditorButton.setBounds(537, 140, mapEditorIcon.getIconWidth(), mapEditorIcon.getIconHeight());
        //mapEditorButton.setPressedIcon(pressedmapEditorIcon);
        mapEditorButton.setVisible(true);
        mapEditorButton.setContentAreaFilled(false);
        mapEditorButton.setBorderPainted(false);
        add(mapEditorButton);
        MMbuttons.add(mapEditorButton);
        
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                initialiseGame(false);
                map.setDrawingParameters(IN_GAME);
                for (JButton b : MMbuttons)
                {
                    b.setVisible(false);
                }
                startButton.setVisible(isHost);
            }
        });
        
        gameCreateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                initialiseGame(true);
                map.setDrawingParameters(IN_GAME);
                for (JButton b : MMbuttons)
                {
                    b.setVisible(false);
                }
                startButton.setVisible(isHost);
            }
        });
        
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                quitGame();
            }
        });
        
        gameModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // to do
            }
        });
        
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setState(IN_GAME);
            }
        });
        
        rightSkinArrow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int skinIndex = player.getSkinIndex();
                skinIndex = (skinIndex%5)+1;
                getPlayer().setSkin(skinIndex);
                repaint();
            }
        });
        
        leftSkinArrow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int skinIndex = player.getSkinIndex();
                skinIndex--;
                if (skinIndex<1){
                    skinIndex=5;
                }
                getPlayer().setSkin(skinIndex);
                repaint();
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
        
        mapEditorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setState(MAP_EDITOR);
            }
        });
        
        // Map Editor interface
        
        JButton saveMapButton = new JButton();
        saveMapButton.setIcon(saveMapIcon);
        saveMapButton.setBounds(7, 212, saveMapIcon.getIconWidth(), saveMapIcon.getIconHeight());
        //saveMapButton.setPressedIcon(pressedSaveMapIcon);
        saveMapButton.setVisible(false);
        //saveMapButton.setContentAreaFilled(false);
        saveMapButton.setBorderPainted(false);
        add(saveMapButton);
        MEbuttons.add(saveMapButton);
        
        JButton loadMapButton = new JButton();
        loadMapButton.setIcon(loadMapIcon);
        loadMapButton.setBounds(7, 162, loadMapIcon.getIconWidth(), loadMapIcon.getIconHeight());
        //loadMapButton.setPressedIcon(pressedLoadMapIcon);
        loadMapButton.setVisible(false);
        //loadMapButton.setContentAreaFilled(false);
        loadMapButton.setBorderPainted(false);
        add(loadMapButton);
        MEbuttons.add(loadMapButton);
        
        JButton doneButton = new JButton();
        doneButton.setIcon(doneIcon);
        doneButton.setBounds(7, 280, doneIcon.getIconWidth(), doneIcon.getIconHeight());
        //doneButton.setPressedIcon(presseddoneIcon);
        doneButton.setVisible(false);
        //doneButton.setContentAreaFilled(false);
        doneButton.setBorderPainted(false);
        add(doneButton);
        MEbuttons.add(doneButton);
        
        saveMapButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser("");
	
                if (fileChooser.showOpenDialog(null)== 
                    JFileChooser.APPROVE_OPTION) {
                    String adresse = fileChooser.getSelectedFile().getPath() + ".txt";
                    Tools.mapToTextFile(map, adresse);
                }
            }
        });
        
        loadMapButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser("");
	
                if (fileChooser.showOpenDialog(null)== 
                    JFileChooser.APPROVE_OPTION) {
                    String adresse = fileChooser.getSelectedFile().getPath();
                    map = new Map(Tools.textFileToIntMap(adresse), textureSize);
                    map.setDrawingParameters(MAP_EDITOR);
                }
                repaint();
            }
        });
        
        doneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setState(MAIN_MENU);
            }
        });
    }
    
    public void updateGame(long dT){
        updatePlayerMovement();
        player.update(dT, map);
        player.healthcheck();
        player.updateBulletImpact(dT, map, listPlayers);
        updatePositionPlayerList();
        sql.setPosition(player.getPosX(), player.getPosY(), player);
        ArrayList<Bullet> listOtherBullets = sql.getBulletListExceptPlayer(player);
        for(Bullet bullet : player.getBulletList())
        {
            sql.setPositionBullet(bullet.getPosX(),bullet.getPosY(),bullet);
        }
        
        if (pressedButtons.contains(KeyEvent.VK_ESCAPE)){
            int confirm = JOptionPane.showOptionDialog(
                null, "Do you want to disconnect from the game ?",
                "Disconnecting", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (confirm == 0) {
                endGame();
                setState(MAIN_MENU);
                if (pressedButtons.contains(KeyEvent.VK_ESCAPE)){
                    pressedButtons.remove(KeyEvent.VK_ESCAPE);
                }
            }
        }
        
        
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
    
    private void updatePlayerMovement(){
        if (pressedButtons.contains(KeyEvent.VK_DOWN)){
//            player.setFacedDirection(0);
            player.setAcceleration(1, 1);
            player.setDirectionOfTravel(1, 1);
        }
        if (pressedButtons.contains(KeyEvent.VK_UP)){
//            player.setFacedDirection(3);
            player.setAcceleration(1, -1);
            player.setDirectionOfTravel(1, -1);
        }
        if (pressedButtons.contains(KeyEvent.VK_LEFT)){
//            player.setFacedDirection(1);
            player.setAcceleration(0, -1);
            player.setDirectionOfTravel(0, -1);
        }
        if (pressedButtons.contains(KeyEvent.VK_RIGHT)){
//            player.setFacedDirection(2);
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
        this.getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), "escapePressed");
        this.getInputMap().put(KeyStroke.getKeyStroke("released ESCAPE"), "escapeReleased");
        this.getActionMap().put("upPressed", new KeyPressed(KeyEvent.VK_UP));
        this.getActionMap().put("upReleased", new KeyReleased(KeyEvent.VK_UP) );
        this.getActionMap().put("downPressed", new KeyPressed(KeyEvent.VK_DOWN));
        this.getActionMap().put("downReleased", new KeyReleased(KeyEvent.VK_DOWN) );
        this.getActionMap().put("leftPressed", new KeyPressed(KeyEvent.VK_LEFT));
        this.getActionMap().put("leftReleased", new KeyReleased(KeyEvent.VK_LEFT) );
        this.getActionMap().put("rightPressed", new KeyPressed(KeyEvent.VK_RIGHT));
        this.getActionMap().put("rightReleased", new KeyReleased(KeyEvent.VK_RIGHT) );
        this.getActionMap().put("escapePressed", new KeyPressed(KeyEvent.VK_ESCAPE));
        this.getActionMap().put("escapeReleased", new KeyReleased(KeyEvent.VK_ESCAPE) );
    }
    
@Override
public void paint(Graphics g) {
    super.paint(g);
    Graphics2D g2d = (Graphics2D) g;
    switch(gameState) {
        case PRE_GAME:
            for(JButton button : PGbuttons){
                button.repaint();
            }
            break;
        case MAIN_MENU:
            g2d.drawImage(MenuBackground, 0, 0, 16*64, 9*64, this);
            g2d.drawImage(player.getImage(), (180-player.getPlayerWidth())/2, (panelHeight-player.getPlayerHeight())/2, 160, 160, this);
            map.draw(g2d);
            for(JButton button : MMbuttons){
                button.repaint();
            }
            break;
        case IN_GAME:
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
            break;
        case MAP_EDITOR:
            g2d.drawImage(EditorBackground, 0, 0, 16*64, 9*64, this);     
            map.draw(g2d);
            tileSelector.draw(g2d);
            for(JButton button : MEbuttons){
                button.repaint();
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
        sql = new SQLManager();
        isConnected = true;
        setState(PRE_GAME);
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
    }
    
    public boolean isConnected(){
        return isConnected;
    }
    
    public void endGame() {
        if (isConnected){
            sql.removePlayer(player);
            sql.disconnect();
        }
        setState(MAIN_MENU);
    }
    
    public void initialisePlayerList()
    {
        int numberOfPlayers = sql.getNumberOfPlayers();
        for(int i=0;i<numberOfPlayers;i++)
        {
            if (i != player.getPlayerId())
            {
                double[] posNewPlayer = sql.getPositionWithPlayerId(i);
                Player newPlayer = new Player(posNewPlayer[0],posNewPlayer[1]);
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
    
    public void updatePlayerList() {
        int numberOfPlayers = sql.getNumberOfPlayers();
        listPlayers.clear();;
        for (int i = 0; i < numberOfPlayers; i++) {
            if (i != player.getPlayerId()) {
                double[] posNewPlayer = sql.getPositionWithPlayerId(i);
                Player newPlayer = new Player(posNewPlayer[0], posNewPlayer[1]);
                newPlayer.setSkin(4);
                newPlayer.setPlayerId(i);
                listPlayers.add(newPlayer);
            } else {
                listPlayers.add(player);
            }
        }
    }

    public int getState(){
        return gameState;
    }
    
    public Player getPlayer(){
        return player;
    }    
    
    public void quitGame() {
        int confirm = JOptionPane.showOptionDialog(
                null, "Are you sure you want to quit ?",
                "Quit the game", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (confirm == 0) {
            endGame();
            System.exit(0);
        }
    }
    
    public void setState(int newGameState){
        gameState = newGameState;
        map.setDrawingParameters(gameState);
        switch(gameState){
            case MAIN_MENU:
                for (JButton button : MMbuttons){
                    button.setVisible(true);
                }
                for (JButton button : MEbuttons){
                    button.setVisible(false);
                }
                for (JButton button : PGbuttons){
                    button.setVisible(false);
                }
                break;
            case MAP_EDITOR:
                for (JButton button : MMbuttons){
                    button.setVisible(false);
                }
                for (JButton button : MEbuttons){
                    button.setVisible(true);
                }
                for (JButton button : PGbuttons){
                    button.setVisible(false);
                }
                break;
            case PRE_GAME:
                for (JButton button : MMbuttons){
                    button.setVisible(false);
                }
                for (JButton button : MEbuttons){
                    button.setVisible(false);
                }
                for (JButton button : PGbuttons){
                    button.setVisible(isHost);
                }
                break;
            case IN_GAME:
                for (JButton button : MMbuttons){
                    button.setVisible(false);
                }
                for (JButton button : MEbuttons){
                    button.setVisible(false);
                }
                for (JButton button : PGbuttons){
                    button.setVisible(false);
                }
        }
        repaint();
    }
}
