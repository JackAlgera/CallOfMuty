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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javazoom.jl.decoder.JavaLayerException;

public class GamePanel extends JPanel{
    
    public static BufferedImage MenuBackground = Tools.loadImage("MenuBackground.png"),
            EditorBackground = Tools.loadImage("EditorBackground.png"),
            PreGameBackground = Tools.loadImage("PreGameBackground.png"),
            victoryScreen = Tools.loadImage("Victory.png"),
            defeatScreen = Tools.loadImage("Defeat.png"),
            GameModeBackground = Tools.loadImage("GamemodeBackground.png");

    public static ImageIcon joinGameIcon = Tools.loadIcon("JoinGame.png"),
            createGameIcon = Tools.loadIcon("CreateGame.png"),
            leftArrowIcon = Tools.loadIcon("LeftArrow.png"),
            pressedleftArrowIcon = Tools.loadIcon("LeftArrowP.png"),
            rightArrowIcon = Tools.loadIcon("RightArrow.png"),
            pressedrightArrowIcon = Tools.loadIcon("RightArrowP.png"),
            topArrowIcon = Tools.loadIcon("TopArrow.png"),
            pressedtopArrowIcon = Tools.loadIcon("TopArrowP.png"),
            bottomArrowIcon = Tools.loadIcon("BottomArrow.png"),
            pressedbottomArrowIcon = Tools.loadIcon("BottomArrowP.png"),            
            exitIcon = Tools.loadIcon("Exit.png"),
            mapEditorIcon = Tools.loadIcon("EditMaps.png"),
            saveMapIcon = Tools.loadIcon("Save.png"),
            loadMapIcon = Tools.loadIcon("Load.png"),
            doneIcon = Tools.loadIcon("Done.png"),
            gameModeIcon = Tools.loadIcon("GameMode.png"),
            SoundsIcon = Tools.loadIcon("Sound.png"),
            muteSoundsIcon = Tools.loadIcon("Mute.png"),
            MusicIcon = Tools.loadIcon("Music.png"),
            muteMusicIcon = Tools.loadIcon("muteMusic.png"),
            startGameIcon = Tools.loadIcon("StartGame.png"),
            mainMenuIcon = Tools.loadIcon("MainMenu.png"),
            spectateIcon = Tools.loadIcon("Spectate.png"),
            startingTileIcon = Tools.loadIcon("StartingTiles.png");    
    
    public static final int IFW = JPanel.WHEN_IN_FOCUSED_WINDOW,
            MAIN_MENU = 0, IN_GAME = 1, MAP_EDITOR = 2, PRE_GAME = 3, ENDING = 4, GAME_MODE = 5;
    
    private static long gunGenerationTime = 100; //in milliseconds
    
    private SoundPlayer menuMusicPlayer, gameMusicPlayer, clicSoundPlayer;
    
    private Map map;
    private TileSelector tileSelector;
    private Player player;
    private ArrayList <Player> otherPlayersList;
    private int textureSize, mapWidth, mapHeight, panelWidth, panelHeight, gameState;
    private GameMode gameMode;
    private ArrayList<Integer> pressedButtons, releasedButtons;
    private boolean isHost, setStartingTile, isConnected, muteMusic, muteSounds, mousePressed;
    private long lastGunGeneration;
    private SQLManager sql;
    private ArrayList <JComponent> MMbuttons, MEbuttons, PGbuttons, Ebuttons, GMbuttons;
    private ArrayList<Bullet> otherPlayersBullets;
    private GameTimer timer;
    private int[] mousePosition;
    
    public GamePanel(int textureSize, int mapWidth, int mapHeight, GameTimer timer) throws IOException, JavaLayerException{
        super();
        menuMusicPlayer = new SoundPlayer("menuMusic.mp3", true);
        gameMusicPlayer = new SoundPlayer("gameMusic.mp3", true);
        clicSoundPlayer = new SoundPlayer("clicSound.mp3", false);
        muteMusic = false;
        muteSounds = false;
        try {
            menuMusicPlayer.play();
        } catch (URISyntaxException ex) {
            Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        gameState = MAIN_MENU;
        gameMode = new GameMode();
        lastGunGeneration = System.currentTimeMillis();
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
        mousePressed = false;
        mousePosition = new int[]{0,0};
        
        setFocusable(true);
        buildInterface(); 
        
        // handling mouse inputs
        addMouseMotionListener(new MouseAdapter(){
            @Override
            public void mouseDragged(MouseEvent e){
                mousePosition[0] = e.getX();
                mousePosition[1] = e.getY();
            }
        });
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }@Override
            public void mouseEntered(MouseEvent e) {
            }@Override
            public void mouseExited(MouseEvent e) {
            }@Override
            public void mousePressed(MouseEvent e) {
                mousePressed = true;
                mousePosition[0] = e.getX();
                mousePosition[1] = e.getY();
                switch (gameState) {
                    case IN_GAME:
                        break;
                    case MAP_EDITOR:
                        int[] mapClicked = map.clickedTile(e.getX(), e.getY());
                        if (mapClicked[0] > -1) { // map was clicked
                            playClicSound();
                            if (!setStartingTile) {
                                map.setTile(mapClicked[1], mapClicked[2], tileSelector.getSelectedTile());
                            } else {
                                map.addStartTile(new int[]{mapClicked[1], mapClicked[2]});
                            }
                        } else { // check if tileSelector was clicked and select the tile if so
                            if (tileSelector.clickedTile(e.getX(), e.getY())[0] > -1) {
                                playClicSound();
                                setStartingTile = false;
                            }
                        }
                        repaint();
                        break;
                    default:
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                mousePressed = false;
            }
        });  
        
        // Defining the thread which will handle mouse dragging
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    switch (gameState) {
                        case IN_GAME:
                            if (mousePressed) {
                                playershoot();
                            }
                            break;
                        case MAP_EDITOR:
                            if (mousePressed) {
                                int[] mapClicked = map.clickedTile(mousePosition[0], mousePosition[1]);
                                if (mapClicked[0] > -1) { // map was clicked
                                    if (!setStartingTile) {
                                        map.setTile(mapClicked[1], mapClicked[2], tileSelector.getSelectedTile());
                                    }
                                } else { // check if tileSelector was clicked and select the tile if so
                                    if (tileSelector.clickedTile(mousePosition[0], mousePosition[1])[0] > -1) {
                                        setStartingTile = false;
                                    }
                                }
                                repaint();
                            }
                            break;
                        default:
                    }
                    try {
                        Thread.sleep(0, 500000); // Thread sleeps for 500µs, to not overcharge program
                    } catch (InterruptedException ex) {
                        Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }.start();     
    }
    
    private void buildInterface(){
        setLayout(null);
        MMbuttons = new ArrayList<JComponent>(); //MM : Main menu
        MEbuttons = new ArrayList<JComponent>(); //ME : Map Editor
        PGbuttons = new ArrayList<JComponent>(); // Pre game
        Ebuttons = new ArrayList<JComponent>(); // Ending (Victory or Defeat)
        GMbuttons = new ArrayList<JComponent>(); //GM : Game Mode
        /*
        ----------------------------------------------------------------------------------------------------------------
        
        Buttons used during the game
        
        ----------------------------------------------------------------------------------------------------------------
        */

        //---------------------------------------------- Connect button ------------------------------------------------       
        
        JButton connectButton = new JButton();
        connectButton.setIcon(joinGameIcon);
        connectButton.setVisible(true);
        connectButton.setBounds(287, 300, joinGameIcon.getIconWidth(), joinGameIcon.getIconHeight());
        //connectButton.setPressedIcon(pressedJoinGameIcon);
        connectButton.setContentAreaFilled(false);
        connectButton.setBorderPainted(true);
        add(connectButton);
        MMbuttons.add(connectButton);
        
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                try {
                    initialiseGame(false);
                } catch (IOException ex) {
                    Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (JavaLayerException ex) {
                    Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        //---------------------------------------------- Create game Button ------------------------------------------             
        
        JButton gameCreateButton = new JButton();
        gameCreateButton.setIcon(createGameIcon);
        gameCreateButton.setBounds(287, 227, createGameIcon.getIconWidth(), createGameIcon.getIconHeight());
        //gameCreateButton.setPressedIcon(pressedcreateGameIcon);
        gameCreateButton.setVisible(true);
        gameCreateButton.setContentAreaFilled(false);
        gameCreateButton.setBorderPainted(true);
        add(gameCreateButton);
        MMbuttons.add(gameCreateButton);
        
        gameCreateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                try{
                initialiseGame(true);
                } catch (IOException ex) {
                    Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (JavaLayerException ex) {
                    Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        //---------------------------------------------- Exit button --------------------------------------------    
        
        JButton exitButton = new JButton();
        exitButton.setIcon(exitIcon);
        exitButton.setBounds(287, 373, exitIcon.getIconWidth(), exitIcon.getIconHeight());
        //exitButton.setPressedIcon(pressedExitIcon);
        exitButton.setVisible(true);
        exitButton.setContentAreaFilled(false);
        exitButton.setBorderPainted(true);
        add(exitButton);
        MMbuttons.add(exitButton);
        
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                quitGame();
            }
        });
        
        //---------------------------------------------- Game mode button --------------------------------------------   
        
        JButton gameModeButton = new JButton();
        gameModeButton.setIcon(gameModeIcon);
        gameModeButton.setBounds(287, 154, gameModeIcon.getIconWidth(), gameModeIcon.getIconHeight());
        //gameModeButton.setPressedIcon(pressedGameModeIcon);
        gameModeButton.setVisible(true);
        gameModeButton.setContentAreaFilled(false);
        gameModeButton.setBorderPainted(true);
        add(gameModeButton);
        MMbuttons.add(gameModeButton);
        
        gameModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                setState(GAME_MODE);
            }
            }
        );
        
        //--------------------------------------- Right arrow for skin selection ------------------------------------   
        
        JButton rightSkinArrow = new JButton();
        rightSkinArrow.setIcon(rightArrowIcon);
        rightSkinArrow.setBounds(182, 440, rightArrowIcon.getIconWidth(), rightArrowIcon.getIconHeight());
        rightSkinArrow.setPressedIcon(pressedrightArrowIcon);
        rightSkinArrow.setVisible(true);
        rightSkinArrow.setContentAreaFilled(false);
        rightSkinArrow.setBorderPainted(false);
        add(rightSkinArrow);
        MMbuttons.add(rightSkinArrow);
        
        rightSkinArrow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                int skinIndex = player.getSkinIndex();
                skinIndex = (skinIndex%5)+1;
                getPlayer().setSkin(skinIndex);
                repaint();
            }
        });
        
        //--------------------------------------- Left arrow for skin selection ------------------------------------    
        
        JButton leftSkinArrow = new JButton();
        leftSkinArrow.setIcon(leftArrowIcon);
        leftSkinArrow.setBounds(54, 440, leftArrowIcon.getIconWidth(), leftArrowIcon.getIconHeight());
        leftSkinArrow.setPressedIcon(pressedleftArrowIcon);
        leftSkinArrow.setVisible(true);
        leftSkinArrow.setContentAreaFilled(false);
        leftSkinArrow.setBorderPainted(false);
        add(leftSkinArrow);
        MMbuttons.add(leftSkinArrow);
        
        leftSkinArrow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                int skinIndex = player.getSkinIndex();
                skinIndex--;
                if (skinIndex<1){
                    skinIndex=5;
                }
                getPlayer().setSkin(skinIndex);
                repaint();
            }
        });
        
        //--------------------------------------- Up arrow for the map editor ------------------------------------  
        
        JButton MEtopSkinArrow = new JButton();
        MEtopSkinArrow.setIcon(topArrowIcon);
        MEtopSkinArrow.setBounds(29, 16, topArrowIcon.getIconWidth(), topArrowIcon.getIconHeight());
        MEtopSkinArrow.setPressedIcon(pressedtopArrowIcon);
        MEtopSkinArrow.setVisible(false);
        MEtopSkinArrow.setContentAreaFilled(false);
        MEtopSkinArrow.setBorderPainted(false);
        add(MEtopSkinArrow);
        MEbuttons.add(MEtopSkinArrow);
        
        //--------------------------------------- Down arrow for the map editor ------------------------------------ 
        
        JButton MEBottomSkinArrow = new JButton();
        MEBottomSkinArrow.setIcon(bottomArrowIcon);
        MEBottomSkinArrow.setBounds(29, 78, bottomArrowIcon.getIconWidth(), bottomArrowIcon.getIconHeight());
        MEBottomSkinArrow.setPressedIcon(pressedbottomArrowIcon);
        MEBottomSkinArrow.setVisible(false);
        MEBottomSkinArrow.setContentAreaFilled(false);
        MEBottomSkinArrow.setBorderPainted(false);
        add(MEBottomSkinArrow);
        MEbuttons.add(MEBottomSkinArrow);        
        
        //--------------------------------------- Right arrow map selection ------------------------------------  
        
        JButton rightMapArrow = new JButton();
        rightMapArrow.setIcon(rightArrowIcon);
        rightMapArrow.setBounds(824, 440, rightArrowIcon.getIconWidth(), rightArrowIcon.getIconHeight());
        rightMapArrow.setPressedIcon(pressedrightArrowIcon);
        rightMapArrow.setVisible(true);
        rightMapArrow.setContentAreaFilled(false);
        rightMapArrow.setBorderPainted(false);
        add(rightMapArrow);
        MMbuttons.add(rightMapArrow);
        
        rightMapArrow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // to do
            }
        });
        
        //--------------------------------------- Left arrow map selection ------------------------------------   
        
        JButton leftMapArrow = new JButton();
        leftMapArrow.setIcon(leftArrowIcon);
        leftMapArrow.setBounds(637, 440, leftArrowIcon.getIconWidth(), leftArrowIcon.getIconHeight());
        leftMapArrow.setPressedIcon(pressedleftArrowIcon);
        leftMapArrow.setVisible(true);
        leftMapArrow.setContentAreaFilled(false);
        leftMapArrow.setBorderPainted(false);
        add(leftMapArrow);
        MMbuttons.add(leftMapArrow);
        
        leftMapArrow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // toudou
            }
        });
        
        //--------------------------------------------- Map editor button ------------------------------------------  
        
        JButton mapEditorButton = new JButton();
        mapEditorButton.setIcon(mapEditorIcon);
        mapEditorButton.setBounds(537, 140, mapEditorIcon.getIconWidth(), mapEditorIcon.getIconHeight());
        //mapEditorButton.setPressedIcon(pressedmapEditorIcon);
        mapEditorButton.setVisible(true);
        mapEditorButton.setContentAreaFilled(false);
        mapEditorButton.setBorderPainted(true);
        add(mapEditorButton);
        MMbuttons.add(mapEditorButton);
        
        mapEditorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                setState(MAP_EDITOR);
            }
        });
        
        //-------------------------------------- Save map button for the map editor ------------------------------------------  
        
        JButton saveMapButton = new JButton();
        saveMapButton.setIcon(saveMapIcon);
        saveMapButton.setBounds(7, 212, saveMapIcon.getIconWidth(), saveMapIcon.getIconHeight());
        saveMapButton.setVisible(false);
        saveMapButton.setBorderPainted(true);
        add(saveMapButton);
        MEbuttons.add(saveMapButton);
        
        saveMapButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                JFileChooser fileChooser = new JFileChooser("");
	
                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
                    String address = fileChooser.getSelectedFile().getPath();
                    if (!address.endsWith(".txt")){
                        address+=".txt";
                    }
                    if (Files.exists(Paths.get(address))) {
                        int confirm = JOptionPane.showOptionDialog(
                                null, address + " already exists, do you want to replace it ?",
                                "File already exists", JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE, null, null, null);
                        if (confirm == 0) {
                            Tools.mapToTextFile(map, address);
                        }
                    } else {
                        Tools.mapToTextFile(map, address);
                    }
                }
            }
        });
        
        //-------------------------------------- Load map button for the map editor ------------------------------------------  
        
        JButton loadMapButton = new JButton();
        loadMapButton.setIcon(loadMapIcon);
        loadMapButton.setBounds(7, 163, loadMapIcon.getIconWidth(), loadMapIcon.getIconHeight());
        loadMapButton.setVisible(false);
        loadMapButton.setBorderPainted(true);
        add(loadMapButton);
        MEbuttons.add(loadMapButton);
        
        loadMapButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                JFileChooser fileChooser = new JFileChooser(new File("src/resources/maps"));
                if (fileChooser.showOpenDialog(null)== 
                    JFileChooser.APPROVE_OPTION) {
                    String adresse = fileChooser.getSelectedFile().getPath();
                    map = Tools.textFileToMap(adresse, textureSize);
                    map.setDrawingParameters(MAP_EDITOR);
                }
                repaint();
            }
        });
        
        //-------------------------------------- Done button for the map editor ------------------------------------------  
        
        JButton doneButton = new JButton();
        doneButton.setIcon(doneIcon);
        doneButton.setBounds(7, 279, doneIcon.getIconWidth(), doneIcon.getIconHeight());
        doneButton.setVisible(false);
        doneButton.setBorderPainted(true);
        add(doneButton);
        MEbuttons.add(doneButton);
        
        doneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                setState(MAIN_MENU);
            }
        });
        
        //--------------------------- Button to choose the starting positions for the map editor ----------------------------- 
        
        JButton setStartingTileButton = new JButton();
        setStartingTileButton.setName("setStartingTileButton");
        setStartingTileButton.setIcon(startingTileIcon);
        setStartingTileButton.setBounds(753, 24, startingTileIcon.getIconWidth(), startingTileIcon.getIconHeight());
        setStartingTileButton.setVisible(false);
        setStartingTileButton.setBorderPainted(true);
        add(setStartingTileButton);
        MEbuttons.add(setStartingTileButton);
        
        setStartingTileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                setStartingTile = true;
            }
        });
        
        //------------------------------------- Start game button during lobby ----------------------------- 
        
        JButton startButton = new JButton();
        startButton.setVisible(false);
        startButton.setIcon(startGameIcon);
        startButton.setBounds(360, 234, startGameIcon.getIconWidth(), startGameIcon.getIconHeight());
        add(startButton);
        PGbuttons.add(startButton);
        
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                sql.setGameState(IN_GAME);
                setState(IN_GAME);
            }
        });        
        
        //-------------------------------------------- Mute sound button ------------------------------------------  
                
        JToggleButton muteSoundsButton = new JToggleButton();
        muteSoundsButton.setIcon(SoundsIcon);
        muteSoundsButton.setBounds(840, 20, SoundsIcon.getIconWidth(), SoundsIcon.getIconHeight());
        muteSoundsButton.setVisible(true);
        muteSoundsButton.setBorderPainted(false);
        add(muteSoundsButton);
        MMbuttons.add(muteSoundsButton);
        
        muteSoundsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                muteSounds = !muteSounds;
                player.setMuteSounds(muteSounds);
                if(muteSounds){
                    muteSoundsButton.setIcon(muteSoundsIcon);      
                }else{
                    muteSoundsButton.setIcon(SoundsIcon);
                    playClicSound();
                }
            }
        });
        
        //--------------------------------------------- Mute music button ------------------------------------------  
        
        JToggleButton muteMusicButton = new JToggleButton();
        muteMusicButton.setIcon(MusicIcon);
        muteMusicButton.setBounds(900, 20, MusicIcon.getIconWidth(), MusicIcon.getIconHeight());
        muteMusicButton.setVisible(true);
        muteMusicButton.setBorderPainted(false);
        add(muteMusicButton);
        MMbuttons.add(muteMusicButton);
                
        muteMusicButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                muteMusic = !muteMusic;
                if(muteMusic){
                    muteMusicButton.setIcon(muteMusicIcon);
                    menuMusicPlayer.stop();
                } else {
                    muteMusicButton.setIcon(MusicIcon);              
                    try {
                        menuMusicPlayer.play();
                    } catch (JavaLayerException | IOException | URISyntaxException ex) {
                        Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        
        //-------------------------------- Username input area --------------------------------- 
        
        JTextField usernameField = new JTextField("Username");
        usernameField.setBounds(56, 140, 172, mapEditorIcon.getIconHeight()+1);
        usernameField.setEditable(true);
        usernameField.setHorizontalAlignment(JTextField.CENTER);
        usernameField.setFont(new Font("Stencil", Font.BOLD, 18));
        usernameField.setBackground(new Color(230,226,211));//(new Color(221,214,192));
        usernameField.setForeground(Color.DARK_GRAY);
        usernameField.setBorder(null);
        usernameField.setVisible(true);
        add(usernameField);
        MMbuttons.add(usernameField);
                
        usernameField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                player.setName(usernameField.getText());
            }
        });
        
        usernameField.addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent fe) {
            }

            @Override
            public void focusLost(FocusEvent fe) {
                player.setName(usernameField.getText());
            }
        });

//--------------------------------------------- Ending buttons : Return to menu ------------------------------------------  
        JButton mainMenuButton = new JButton();
        mainMenuButton.setName("mainMenuButton");
        mainMenuButton.setIcon(mainMenuIcon);
        mainMenuButton.setBounds((panelWidth-mainMenuIcon.getIconWidth())/2, 500, mainMenuIcon.getIconWidth(), mainMenuIcon.getIconHeight());
        mainMenuButton.setVisible(false);
        mainMenuButton.setBorderPainted(true);
        add(mainMenuButton);
        Ebuttons.add(mainMenuButton);

        mainMenuButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                endGame();
            }
        });
        
//--------------------------------------------- Ending buttons : Spectate game ------------------------------------------  
        JButton spectateGameButton = new JButton();
        spectateGameButton.setName("spectateGameButton");
        spectateGameButton.setIcon(spectateIcon);
        spectateGameButton.setBounds((panelWidth-spectateIcon.getIconWidth())/2, 400, spectateIcon.getIconWidth(), spectateIcon.getIconHeight());
        spectateGameButton.setVisible(false);
        spectateGameButton.setBorderPainted(true);
        add(spectateGameButton);
        Ebuttons.add(spectateGameButton);

        spectateGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                setState(IN_GAME);
            }
        });
    
//--------------------------------------------- Done button for the game mode ------------------------------------------  
        
        JButton GMdoneButton = new JButton();
        GMdoneButton.setIcon(doneIcon);
        GMdoneButton.setBounds(59, 512, doneIcon.getIconWidth(), doneIcon.getIconHeight());
        GMdoneButton.setVisible(false);
        GMdoneButton.setBorderPainted(true);
        add(GMdoneButton);
        GMbuttons.add(GMdoneButton);
        
        GMdoneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                setState(MAIN_MENU);
            }
        });
    }

    /*
    ----------------------------------------------------------------------------------------------------------------

    Main game updating function to handle the SQL server and player input

    ----------------------------------------------------------------------------------------------------------------
    */
    
    public void updateGame(long dT) throws JavaLayerException, IOException{
        boolean printTime = false; // Set to true if you want to print the time taken by each method in updateGame
        long time = System.currentTimeMillis();
        boolean playerWasDead = player.isDead(); // used to check if player died
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
        if (!player.isDead()) {
            // Update bullets
            player.updateBulletList(dT, map, otherPlayersList);
            if (printTime) {
                System.out.println("Bullet updates : " + (System.currentTimeMillis() - time));
                time = System.currentTimeMillis();
            }
            // gun generation
            if (System.currentTimeMillis() - gunGenerationTime > lastGunGeneration) {
                lastGunGeneration = System.currentTimeMillis();
                player.generateGun(otherPlayersList.size() + 1, gunGenerationTime, gameMode); // has a probability to give local player a gun that decreases with number of players
            }

            // sql uploads
            sql.uploadPlayerAndBullets(player);
            if (printTime) {
                System.out.println("Uploads : " + (System.currentTimeMillis() - time));
            }
        } else if(!playerWasDead){ // player just died : show defeat screen
            setState(ENDING);
        }
        if(otherPlayersList.isEmpty()){ // Game is ended
            if (!player.isDead()){ // Local player won : show victory screen
                setState(ENDING);
            } else { // quit game
                endGame();
            }
        }
    }
    
    /*
    ----------------------------------------------------------------------------------------------------------------

    Player keyboard input updates

    ----------------------------------------------------------------------------------------------------------------
    */
    
    private void updatePlayerMovement(){
        // Acceleration
        if (pressedButtons.contains(KeyEvent.VK_S) || pressedButtons.contains(KeyEvent.VK_DOWN)){
//            player.setFacedDirection(0);
            player.setAcceleration(1, 1);
            player.setDirectionOfTravel(1, 1);
        }
        if (pressedButtons.contains(KeyEvent.VK_Z) || pressedButtons.contains(KeyEvent.VK_UP)){
//            player.setFacedDirection(3);
            player.setAcceleration(1, -1);
            player.setDirectionOfTravel(1, -1);
        }
        if (pressedButtons.contains(KeyEvent.VK_Q) || pressedButtons.contains(KeyEvent.VK_LEFT)){
//            player.setFacedDirection(1);
            player.setAcceleration(0, -1);
            player.setDirectionOfTravel(0, -1);
        }
        if (pressedButtons.contains(KeyEvent.VK_D) || pressedButtons.contains(KeyEvent.VK_RIGHT)){
//            player.setFacedDirection(2);
            player.setAcceleration(0, 1);
            player.setDirectionOfTravel(0, 1);
        }
        
//        Deceleration
        if (releasedButtons.contains(KeyEvent.VK_S)){
            player.reverseAcceleration(1);
            releasedButtons.remove((Integer)KeyEvent.VK_S);
        }
        if (releasedButtons.contains(KeyEvent.VK_Z)){
            player.reverseAcceleration(1);
            releasedButtons.remove((Integer)KeyEvent.VK_Z);
        }
        if (releasedButtons.contains(KeyEvent.VK_Q)){
            player.reverseAcceleration(0);
            releasedButtons.remove((Integer)KeyEvent.VK_Q);
        }
        if (releasedButtons.contains(KeyEvent.VK_D)){
            player.reverseAcceleration(0);
            releasedButtons.remove((Integer)KeyEvent.VK_D);
        }
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
    
    public void mapKeys(){
        ArrayList<String> keyStrokeList = new ArrayList();
        keyStrokeList.add("S"); keyStrokeList.add("Z"); keyStrokeList.add("Q"); keyStrokeList.add("D");
        keyStrokeList.add("DOWN"); keyStrokeList.add("UP"); keyStrokeList.add("LEFT"); keyStrokeList.add("RIGHT"); keyStrokeList.add("ESCAPE");
        for (String key : keyStrokeList)
        {
            this.getInputMap().put(KeyStroke.getKeyStroke(key), key + "Pressed");
            this.getInputMap().put(KeyStroke.getKeyStroke("released " + key), key + "Released");
        }
        
        this.getActionMap().put("ZPressed", new KeyPressed(KeyEvent.VK_Z));
        this.getActionMap().put("ZReleased", new KeyReleased(KeyEvent.VK_Z) );
        this.getActionMap().put("SPressed", new KeyPressed(KeyEvent.VK_S));
        this.getActionMap().put("SReleased", new KeyReleased(KeyEvent.VK_S) );
        this.getActionMap().put("QPressed", new KeyPressed(KeyEvent.VK_Q));
        this.getActionMap().put("QReleased", new KeyReleased(KeyEvent.VK_Q) );
        this.getActionMap().put("DPressed", new KeyPressed(KeyEvent.VK_D));
        this.getActionMap().put("DReleased", new KeyReleased(KeyEvent.VK_D) );
        this.getActionMap().put("UPPressed", new KeyPressed(KeyEvent.VK_UP));
        this.getActionMap().put("UPReleased", new KeyReleased(KeyEvent.VK_UP) );
        this.getActionMap().put("DOWNPressed", new KeyPressed(KeyEvent.VK_DOWN));
        this.getActionMap().put("DOWNReleased", new KeyReleased(KeyEvent.VK_DOWN) );
        this.getActionMap().put("LEFTPressed", new KeyPressed(KeyEvent.VK_LEFT));
        this.getActionMap().put("LEFTReleased", new KeyReleased(KeyEvent.VK_LEFT) );
        this.getActionMap().put("RIGHTPressed", new KeyPressed(KeyEvent.VK_RIGHT));
        this.getActionMap().put("RIGHTReleased", new KeyReleased(KeyEvent.VK_RIGHT) );
        this.getActionMap().put("ESCAPEPressed", new EscapePressed());
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
    
    /*
    ----------------------------------------------------------------------------------------------------------------

    Functions to properly start and end a game

    ----------------------------------------------------------------------------------------------------------------
    */
    public void initialiseGame(boolean isHost) throws IOException, JavaLayerException {
        this.isHost = isHost;
        sql = new SQLManager();
        int[] sqlGame = sql.getGame();
        if (isHost) {
            // Try to create a game
            ArrayList<Player> playerList = sql.getPlayerList();
            if (playerList.size()<2) { // No game is currently on
                sql.clearTable(); //Clear previous game on SQL server
                sql.createGame(map, gameMode.getId());
                player.setGunId(Gun.NO_GUN);
                player.setPlayerId(1);
                player.setTeamId(1);
                player.setToMaxHealth();
                player.setPosition(map);
                player.addPlayer(sql);
                isConnected = true;
                setState(PRE_GAME);
            } else {
                if (sqlGame[0] == PRE_GAME) {
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
            if (sqlGame[0] == PRE_GAME) {
                otherPlayersList = sql.getPlayerList();
                player.setToMaxHealth();
                player.setGunId(Gun.NO_GUN);
                map = sql.getMap(textureSize);
                player.setPosition(map);
                gameMode.setId(sqlGame[1]);
                player.setPlayerId(1); // 0 means "null", ids start at 1            
                while (otherPlayersList.contains(player)) {
                    player.incrementId();
                }
                switch (gameMode.getTeam()) {
                    case GameMode.ALLVSALL:
                        player.setTeamId(player.getPlayerId());
                        break;
                    case GameMode.ALLVSONE:
                        player.setTeamId(2);
                        break;
                    case GameMode.TEAMVSTEAM:
                        if (player.getPlayerId() % 2 == 0) {
                            player.setTeamId(2);
                            System.out.println("rejoint eq 2");
                        } else {
                            player.setTeamId(1);
                            System.out.println("rejoint eq 1");

                        }
                        break;
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
    
    public void endGame() {
        int formerGameState = gameState;
        setState(MAIN_MENU);
        if (isConnected){
            if(!player.isDead()){
                sql.removePlayer(player);
            }
            try {
                if(sql.getPlayerList().isEmpty() || (isHost && formerGameState==PRE_GAME) ){
                    sql.clearTable();
                }
            } catch (IOException | JavaLayerException ex) {
                Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            sql.disconnect();
        }
        isConnected = false;
    }
    
    //-------------------------------------------------------------------------------------------------------------------------
    
    public void playershoot(){
        double[] directionOfFire = new double[2];
        directionOfFire[0] = mousePosition[0] - player.getPosX() - textureSize / 2;
        directionOfFire[1] = mousePosition[1] - player.getPosY() - textureSize / 2;

        double norme = Math.sqrt(directionOfFire[0] * directionOfFire[0] + directionOfFire[1] * directionOfFire[1]);
        directionOfFire[0] = directionOfFire[0] / norme;
        directionOfFire[1] = directionOfFire[1] / norme;

        {
            try {
                player.shoot(directionOfFire, sql, false);
            } catch (JavaLayerException | IOException ex) {
                Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        switch(gameState) {
            case PRE_GAME:
                g2d.drawImage(PreGameBackground, 0, 0, 16*64, 9*64, this);
                g2d.setFont(new Font("Stencil", Font.BOLD, 20));
                map.draw(g2d, false);
                if (isHost) {
                    g2d.drawString(player.getName(), 80, 90);
                    for (int i = 0; i < otherPlayersList.size(); i++) {
                        g2d.drawString(otherPlayersList.get(i).getName(), 80, 140 + i * 50);
                    }
                } else {
                    for (int i = 0; i < otherPlayersList.size(); i++) {
                        g2d.drawString(otherPlayersList.get(i).getName(), 80, 90 + i * 50);
                    }
                    g2d.drawString(player.getName(), 80, 90+(otherPlayersList.size())*50);
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
                    otherPlayersBullets.get(i).draw(g2d, textureSize);
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

            case ENDING:
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                map.draw(g2d, false);
                player.draw(g2d);
                player.drawBullets(g2d, map.getTextureSize());

                for (Player otherPlayer : otherPlayersList) {
                    otherPlayer.draw(g2d);
                }
                for (int i=0; i<otherPlayersBullets.size(); i++){
                    otherPlayersBullets.get(i).draw(g2d, textureSize);
                }
                if (player.isDead()){
                    g2d.drawImage(defeatScreen, (panelWidth-defeatScreen.getWidth())/2, 0, defeatScreen.getWidth(), defeatScreen.getHeight(), null);
                } else {
                    g2d.drawImage(victoryScreen, (panelWidth-victoryScreen.getWidth())/2, 0, victoryScreen.getWidth(), victoryScreen.getHeight(), null);
                }
                break;
                
            case GAME_MODE:
                g2d.drawImage(GameModeBackground, 0, 0, 16 * 64, 9 * 64, this);
                break;

        }
    }
    
    public boolean isConnected(){
        return isConnected;
    }
    
    public void preGameUpdate() {
        try {
            sql.updatePlayerList(player, otherPlayersList);
        } catch (IOException | JavaLayerException ex) {
            Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(!isHost){
            int newGameState = sql.getGame()[0];
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
        int formerGameState = gameState;
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
                for (JComponent component : PGbuttons) {
                    component.setVisible(false);
                }
                for (JComponent component : Ebuttons) {
                    component.setVisible(false);
                }
                for (JComponent component : GMbuttons) {
                    component.setVisible(false);
                }                
                if ((formerGameState==IN_GAME || formerGameState==ENDING) && !muteMusic) {
                    try {
                        gameMusicPlayer.stop();
                        menuMusicPlayer.play();
                    } catch (JavaLayerException | IOException | URISyntaxException ex) {
                        Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
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
                for (JComponent component : Ebuttons) {
                    component.setVisible(false);
                }
                for (JComponent component : GMbuttons) {
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
                    component.setVisible(true);
                    component.setEnabled(isHost);
                }
                for (JComponent component : Ebuttons) {
                    component.setVisible(false);
                }
                for (JComponent component : GMbuttons) {
                    component.setVisible(false);
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
                for (JComponent component : Ebuttons) {
                    component.setVisible(false);
                }
                for (JComponent component : GMbuttons) {
                    component.setVisible(false);
                }                
                if (!muteMusic && formerGameState != ENDING) {
                    menuMusicPlayer.stop();
                    try {
                        gameMusicPlayer.play();
                    } catch (JavaLayerException | IOException | URISyntaxException ex) {
                        Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                timer.update();
                break;
            case ENDING:
                for (JComponent component : MMbuttons){
                    component.setVisible(false);
                }
                for (JComponent component : MEbuttons){
                    component.setVisible(false);
                }
                for (JComponent component : PGbuttons){
                    component.setVisible(false);
                }
                for (JComponent component :GMbuttons){
                    component.setVisible(false);
                }
                for (JComponent component : Ebuttons) {
                    if(component.getName().equals("spectateGameButton")){
                        component.setVisible(player.isDead());
                    } else {
                        component.setVisible(true);
                    }
                   
                }
                break;
            case GAME_MODE:
                for (JComponent component : MMbuttons){
                    component.setVisible(false);
                }
                for (JComponent component : MEbuttons){
                    component.setVisible(false);
                }
                for (JComponent component : PGbuttons){
                    component.setVisible(false);
                }
                for (JComponent component : Ebuttons) {
                    component.setVisible(false);
                }
                for (JComponent component : GMbuttons) {
                    component.setVisible(true);
                }
        }
        repaint();
    }
    
    public void playClicSound(){
        if (!muteSounds) {
            try {
                clicSoundPlayer.play();
            } catch (JavaLayerException | IOException | URISyntaxException ex) {
                Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
