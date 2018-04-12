package callofmuty;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class GamePanel extends JPanel{

    public static BufferedImage MenuBackground = Tools.loadImage("MenuBackground.png"),
            EditorBackground = Tools.loadImage("EditorBackground.png");

    public static ImageIcon joinGameIcon = Tools.loadIcon("JoinGame.png"),
            createGameIcon = Tools.loadIcon("CreateGame.png"),
            leftArrowIcon = Tools.loadIcon("LeftArrow.png"),
            rightArrowIcon = Tools.loadIcon("RightArrow.png"),
            exitIcon = Tools.loadIcon("Exit.png"),
            mapEditorIcon = Tools.loadIcon("EditMaps.png"),
            saveMapIcon = Tools.loadIcon("Save.png"),
            loadMapIcon = Tools.loadIcon("Load.png"),
            doneIcon = Tools.loadIcon("Done.png"),
            gameModeIcon = Tools.loadIcon("GameMode.png");

    private double bulletSpeed = 0.5;
    
    private Map map;
    private TileSelector tileSelector;
    private Player player;
    private ArrayList <Player> otherPlayersList;
    private int textureSize, mapWidth, mapHeight, panelWidth, panelHeight, gameState;
    private ArrayList<Integer> pressedButtons, releasedButtons;
    private boolean isHost, setStartingTile;
    private long playerListUpdateTime;
    private SQLManager sql; 
    private boolean isConnected;
    private ArrayList <JComponent> MMbuttons, MEbuttons, PGbuttons;
    private ArrayList<Bullet> otherPlayersBullets;
    GameTimer timer;
    
    public static final int IFW = JPanel.WHEN_IN_FOCUSED_WINDOW, MAIN_MENU = 0, IN_GAME = 1, MAP_EDITOR = 2, PRE_GAME = 3;
    
    public GamePanel(int textureSize, int mapWidth, int mapHeight, GameTimer timer) throws IOException{
        super();
        gameState = MAIN_MENU;
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
        pressedButtons = new ArrayList<Integer>();
        releasedButtons = new ArrayList<Integer>();
        otherPlayersBullets = new ArrayList<Bullet>();
        otherPlayersList = new ArrayList<Player>();
        this.timer = timer;
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

                    player.addBullet(player.getPosX() + textureSize / 4, player.getPosY() + textureSize / 4, directionOfFire, bulletSpeed, sql);
                    break;
                case MAP_EDITOR:
                    int[] mapClicked = map.clickedTile(e.getX(), e.getY());
                    if (mapClicked[0]>-1){ // map was clicked
                        if(!setStartingTile){
                            map.setTile(mapClicked[1], mapClicked[2], tileSelector.getSelectedTile());
                        } else {
                            map.setStartTile(new int[]{mapClicked[1], mapClicked[2]});
                        }
                    } else { // check if tileSelector was clicked and select the tile if so
                        if(tileSelector.clickedTile(e.getX(), e.getY())[0]>-1){
                            setStartingTile = false;
                        }
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
        MMbuttons = new ArrayList<JComponent>(); //MM : Main menu
        MEbuttons = new ArrayList<JComponent>(); //ME : Map Editor
        PGbuttons = new ArrayList<JComponent>(); // Pre game
        
        // Main menu interface
        JButton connectButton = new JButton();
        connectButton.setIcon(joinGameIcon);
        connectButton.setVisible(true);
        connectButton.setBounds(287, 300, joinGameIcon.getIconWidth(), joinGameIcon.getIconHeight());
        //connectButton.setPressedIcon(pressedJoinGameIcon);
        connectButton.setContentAreaFilled(false);
        connectButton.setBorderPainted(false);
        add(connectButton);
        MMbuttons.add(connectButton);
        
        JButton gameCreateButton = new JButton();
        gameCreateButton.setIcon(createGameIcon);
        gameCreateButton.setBounds(287, 227, createGameIcon.getIconWidth(), createGameIcon.getIconHeight());
        //gameCreateButton.setPressedIcon(pressedcreateGameIcon);
        gameCreateButton.setVisible(true);
        gameCreateButton.setContentAreaFilled(false);
        gameCreateButton.setBorderPainted(false);
        add(gameCreateButton);
        MMbuttons.add(gameCreateButton);
        
        JButton exitButton = new JButton();
        exitButton.setIcon(exitIcon);
        exitButton.setBounds(287, 373, exitIcon.getIconWidth(), exitIcon.getIconHeight());
        //exitButton.setPressedIcon(pressedExitIcon);
        exitButton.setVisible(true);
        exitButton.setContentAreaFilled(false);
        exitButton.setBorderPainted(false);
        add(exitButton);
        MMbuttons.add(exitButton);
        
        JButton gameModeButton = new JButton();
        gameModeButton.setIcon(gameModeIcon);
        gameModeButton.setBounds(287, 154, gameModeIcon.getIconWidth(), gameModeIcon.getIconHeight());
        //gameModeButton.setPressedIcon(pressedGameModeIcon);
        gameModeButton.setVisible(true);
        gameModeButton.setContentAreaFilled(false);
        gameModeButton.setBorderPainted(false);
        add(gameModeButton);
        MMbuttons.add(gameModeButton);
        
        JButton rightSkinArrow = new JButton();
        rightSkinArrow.setIcon(rightArrowIcon);
        rightSkinArrow.setBounds(182, 440, rightArrowIcon.getIconWidth(), rightArrowIcon.getIconHeight());
        //rightSkinArrow.setPressedIcon(pressedrightArrowIcon);
        rightSkinArrow.setVisible(true);
        rightSkinArrow.setContentAreaFilled(false);
        rightSkinArrow.setBorderPainted(false);
        add(rightSkinArrow);
        MMbuttons.add(rightSkinArrow);
        
        JButton leftSkinArrow = new JButton();
        leftSkinArrow.setIcon(leftArrowIcon);
        leftSkinArrow.setBounds(54, 440, leftArrowIcon.getIconWidth(), leftArrowIcon.getIconHeight());
        //leftSkinArrow.setPressedIcon(pressedleftArrowIcon);
        leftSkinArrow.setVisible(true);
        leftSkinArrow.setContentAreaFilled(false);
        leftSkinArrow.setBorderPainted(false);
        add(leftSkinArrow);
        MMbuttons.add(leftSkinArrow);
        
        JButton rightMapArrow = new JButton();
        rightMapArrow.setIcon(rightArrowIcon);
        rightMapArrow.setBounds(824, 440, rightArrowIcon.getIconWidth(), rightArrowIcon.getIconHeight());
        //rightMapArrow.setPressedIcon(pressedrightArrowIcon);
        rightMapArrow.setVisible(true);
        rightMapArrow.setContentAreaFilled(false);
        rightMapArrow.setBorderPainted(false);
        add(rightMapArrow);
        MMbuttons.add(rightMapArrow);
        
        JButton leftMapArrow = new JButton();
        leftMapArrow.setIcon(leftArrowIcon);
        leftMapArrow.setBounds(637, 440, leftArrowIcon.getIconWidth(), leftArrowIcon.getIconHeight());
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
        
        JTextField usernameField = new JTextField("Username");
        usernameField.setBounds(56, 140, 172, mapEditorIcon.getIconHeight()+1);
        usernameField.setEditable(true);
        usernameField.setHorizontalAlignment(JTextField.CENTER);
        usernameField.setFont(new Font("TimesRoman", Font.BOLD+Font.ITALIC, 18));
        usernameField.setBackground(new Color(230,226,211));//(new Color(221,214,192));
        usernameField.setForeground(Color.DARK_GRAY);
        usernameField.setBorder(null);
        usernameField.setVisible(true);
        add(usernameField);
        MMbuttons.add(usernameField);
        
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                initialiseGame(false);
            }
        });
        
        gameCreateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                initialiseGame(true);
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
        
        usernameField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                player.setName(usernameField.getText());
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
        loadMapButton.setBounds(7, 163, loadMapIcon.getIconWidth(), loadMapIcon.getIconHeight());
        //loadMapButton.setPressedIcon(pressedLoadMapIcon);
        loadMapButton.setVisible(false);
        //loadMapButton.setContentAreaFilled(false);
        loadMapButton.setBorderPainted(false);
        add(loadMapButton);
        MEbuttons.add(loadMapButton);
        
        JButton doneButton = new JButton();
        doneButton.setIcon(doneIcon);
        doneButton.setBounds(7, 279, doneIcon.getIconWidth(), doneIcon.getIconHeight());
        //doneButton.setPressedIcon(presseddoneIcon);
        doneButton.setVisible(false);
        //doneButton.setContentAreaFilled(false);
        doneButton.setBorderPainted(false);
        add(doneButton);
        MEbuttons.add(doneButton);
        
        JButton setStartingTileButton = new JButton("Set starting tile");
        setStartingTileButton.setName("setStartingTileButton");
        //setStartingTileButton.setIcon(startingTileIcon);
        setStartingTileButton.setBounds(700, 30, doneIcon.getIconWidth(), doneIcon.getIconHeight());
        //setStartingTileButton.setPressedIcon(presseddoneIcon);
        setStartingTileButton.setVisible(false);
        //setStartingTileButton.setContentAreaFilled(false);
        //setStartingTileButton.setBorderPainted(false);
        add(setStartingTileButton);
        MEbuttons.add(setStartingTileButton);
        
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
        
        setStartingTileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setStartingTile = true;
            }
        });
        
        //Pre game interface
        JButton startButton = new JButton("Start");
        startButton.setVisible(false);
        startButton.setBounds(286, 300, joinGameIcon.getIconWidth(), joinGameIcon.getIconHeight());
        //connectButton.setPressedIcon(pressedJoinGameIcon);
        add(startButton);
        PGbuttons.add(startButton);
        
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sql.setGameState(IN_GAME);
                setState(IN_GAME);
                repaint();
            }
        });
    }
    
    public void updateGame(long dT){
        boolean printTime = false; // Set to true if you want to print the time taken by each method in updateGame
        long time = System.currentTimeMillis();
        // Update player movement 
        updatePlayerMovement();
        player.update(dT, map);
        if(printTime){
            System.out.println("Player movement & update : " + (System.currentTimeMillis()-time));
            time = System.currentTimeMillis();
        }
        // sql downloads
        sql.downloadPlayersAndBullets(player, otherPlayersList, otherPlayersBullets);
        if(printTime){
            System.out.println("Downloads : " + (System.currentTimeMillis()-time));
            time = System.currentTimeMillis();
        }
        // Update bullets
        player.updateBulletList(dT, map, otherPlayersList);
        if(printTime){
            System.out.println("Bullet updates : " + (System.currentTimeMillis()-time));
            time = System.currentTimeMillis();
        }
        // sql uploads
        sql.uploadPlayerAndBullets(player);
        if(printTime){
            System.out.println("Uploads : " + (System.currentTimeMillis()-time));
        }
        if(otherPlayersList.isEmpty()){ // Game is ended
            if (!player.isPlayerDead()){ // Local player won
                endGame();
            } else {
                endGame();
            }
        }
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
        this.getActionMap().put("upPressed", new KeyPressed(KeyEvent.VK_UP));
        this.getActionMap().put("upReleased", new KeyReleased(KeyEvent.VK_UP) );
        this.getActionMap().put("downPressed", new KeyPressed(KeyEvent.VK_DOWN));
        this.getActionMap().put("downReleased", new KeyReleased(KeyEvent.VK_DOWN) );
        this.getActionMap().put("leftPressed", new KeyPressed(KeyEvent.VK_LEFT));
        this.getActionMap().put("leftReleased", new KeyReleased(KeyEvent.VK_LEFT) );
        this.getActionMap().put("rightPressed", new KeyPressed(KeyEvent.VK_RIGHT));
        this.getActionMap().put("rightReleased", new KeyReleased(KeyEvent.VK_RIGHT) );
        this.getActionMap().put("escapePressed", new EscapePressed());
    }
    
@Override
public void paint(Graphics g) {
    super.paint(g);
    Graphics2D g2d = (Graphics2D) g;
    switch(gameState) {
        case PRE_GAME:
            for (int i=0; i<otherPlayersList.size(); i++){
                g2d.drawString(otherPlayersList.get(i).getName(), 100, 100+i*50);
            }
            break;
            
        case MAIN_MENU:
            g2d.drawImage(MenuBackground, 0, 0, 16*64, 9*64, this);
            g2d.drawImage(player.getImage(), (180-player.getPlayerWidth())/2, (panelHeight-player.getPlayerHeight())/2, 160, 160, this);
            map.draw(g2d, false);
            break;
            
        case IN_GAME:
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            map.draw(g2d, false);
            player.draw(g2d);
            player.drawBullets(g2d, map.getTextureSize());

            for (Player otherPlayer : otherPlayersList) {
                otherPlayer.draw(g2d);
            }
            for (int i=0; i<otherPlayersBullets.size(); i++){
                otherPlayersBullets.get(i).draw(g2d, textureSize, 0);// To do : Only 1 SQL line to modifie every bullet's position
            }
            break;
            
        case MAP_EDITOR:
            g2d.drawImage(EditorBackground, 0, 0, 16*64, 9*64, this);     
            map.draw(g2d, true);
            tileSelector.draw(g2d, setStartingTile);
            if (setStartingTile) { // draw a rectangle around setStartingTileButton
                int index = MEbuttons.size()-1;
                while(!MEbuttons.get(index).getName().equals("setStartingTileButton")){ //find the button
                    index--;
                }
                if(index>-1){ // else, something went wrong, do nothing
                    g2d.setStroke(new BasicStroke(5));
                    g2d.setColor(Color.lightGray);
                    g2d.drawRect(MEbuttons.get(index).getX(), MEbuttons.get(index).getY(), MEbuttons.get(index).getWidth(), MEbuttons.get(index).getHeight());
                }
            }
            break;
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
            if(pressedButtons.contains(key)){
                pressedButtons.remove((Integer)key);
                releasedButtons.add(key);
            }
        }
    }
    
    private class EscapePressed extends AbstractAction{
        
        @Override
        public void actionPerformed(ActionEvent tf) {
            if (gameState == IN_GAME || (gameState == PRE_GAME && !isHost)) {
                int confirm = JOptionPane.showOptionDialog(
                        null, "Do you want to disconnect from the game ?",
                        "Disconnecting", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == 0) {
                    endGame();
                }
            } else {
                if (gameState == PRE_GAME && isHost) {
                    int confirm = JOptionPane.showOptionDialog(
                            null, "Do you want to cancel this game ?",
                            "Cancelling", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, null, null);
                    if (confirm == 0) {
                        endGame();
                    }
                }
            }
        }
    }
    
    public void initialiseGame(boolean isHost) {
        this.isHost = isHost;
        sql = new SQLManager();
        int currentGameState = sql.getGameState();
        if (isHost) { // Try to create a game
            if (sql.getPlayerList().isEmpty()) { // No game is currently on
                sql.clearTable(); //Clear previous game on SQL server
                sql.createGame(map);
                player.setPlayerId(1);
                player.setMaxHealth();
                player.setPosition(map);
                player.addPlayer(sql);
                isConnected = true;
                setState(PRE_GAME);
            } else {
                if (currentGameState == PRE_GAME) {
                    int confirm = JOptionPane.showOptionDialog(
                            null, "A game is already being created, to you want to join it ?",
                            "Join the game ?", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, null, null);
                    if (confirm == 0) {
                        initialiseGame(false);
                    } else {
                        sql.disconnect();
                    }
                } else { // A game is already on
                    int confirm = JOptionPane.showOptionDialog(
                            null, "A game is already on, do you want to spectate it ?",
                            "Spectate the game ?", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, null, null);
                    if (confirm == 0) {
                        otherPlayersList = sql.getPlayerList();
                        player.setHealth(0);
                        map = sql.getMap(textureSize);
                        setState(IN_GAME);
                        isConnected = true;
                    } else {
                        sql.disconnect();
                    }
                }
            }
        } else { // Try to join a Pre_game
            if (currentGameState == PRE_GAME) {
                otherPlayersList = sql.getPlayerList();
                player.setMaxHealth();
                map = sql.getMap(textureSize);
                player.setPosition(map);
                player.setPlayerId(1); // 0 means "null", ids start at 1
                while (otherPlayersList.contains(player)) {
                    player.incrementId();
                }
                player.addPlayer(sql);
                isConnected = true;
                setState(PRE_GAME);
            } else {
                if(sql.getPlayerList().isEmpty()){ // No game created
                    int confirm = JOptionPane.showOptionDialog(
                            null, "There is no game to join, do you want to create one ?",
                            "Create the game ?", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, null, null);
                    if (confirm == 0) {
                        initialiseGame(true);
                    } else {
                        sql.disconnect();
                    }
                } else { // game already started
                    int confirm = JOptionPane.showOptionDialog(
                            null, "A game is already on, do you want to spectate it ?",
                            "Spectate the game ?", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, null, null);
                    if (confirm == 0) {
                        otherPlayersList = sql.getPlayerList();
                        player.setHealth(0);
                        map = sql.getMap(textureSize);
                        setState(IN_GAME);
                        isConnected = true;
                    } else {
                        sql.disconnect();
                    }
                }
            }
        }
    }
    
    public boolean isConnected(){
        return isConnected;
    }
    
    public void endGame() {
        int formerGameState = gameState;
        setState(MAIN_MENU);
        if (isConnected){
            if(!player.isPlayerDead()){
                sql.removePlayer(player);
            }
            if(sql.getPlayerList().isEmpty() || (isHost && formerGameState==PRE_GAME) ){
                sql.clearTable();
            }
            sql.disconnect();
        }
        isConnected = false;
    }
    
    public void preGameUpdate() {
        sql.updatePlayerList(player, otherPlayersList);
        if(!isHost){
            int newGameState = sql.getGameState();
            if (newGameState==IN_GAME){
                setState(IN_GAME);
            } else {
                if(newGameState==-1){ // Host cancelled the game
                    JOptionPane.showMessageDialog(null, "The host cancelled this game");
                    endGame();
                }
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
                for (JComponent component : MMbuttons){
                    component.setVisible(true);
                }
                for (JComponent component : MEbuttons){
                    component.setVisible(false);
                }
                for (JComponent component : PGbuttons){
                    component.setVisible(false);
                }
                break;
            case MAP_EDITOR:
                for (JComponent component : MMbuttons){
                    component.setVisible(false);
                }
                for (JComponent component : MEbuttons){
                    component.setVisible(true);
                }
                for (JComponent component : PGbuttons){
                    component.setVisible(false);
                }
                setStartingTile = false;
                break;
            case PRE_GAME:
                for (JComponent component : MMbuttons){
                    component.setVisible(false);
                }
                for (JComponent component : MEbuttons){
                    component.setVisible(false);
                }
                for (JComponent component : PGbuttons){
                    component.setVisible(isHost);
                }
                break;
            case IN_GAME:
                for (JComponent component : MMbuttons){
                    component.setVisible(false);
                }
                for (JComponent component : MEbuttons){
                    component.setVisible(false);
                }
                for (JComponent component : PGbuttons){
                    component.setVisible(false);
                }
                timer.update();
        }
        repaint();
    }
}
