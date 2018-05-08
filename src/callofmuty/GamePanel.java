package callofmuty;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
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
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
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
            startingTileIcon = Tools.loadIcon("StartingTiles.png"),
            checkedIcon = Tools.loadIcon("check.png"),
            uncheckedIcon = Tools.loadIcon("Uncheck.png");    
    
    public static final int IFW = JPanel.WHEN_IN_FOCUSED_WINDOW,
            MAIN_MENU = 0, IN_GAME = 1, MAP_EDITOR = 2, PRE_GAME = 3, ENDING = 4, GAME_MODE = 5;
    private static final int FONTSIZE = 18; // Font size for textFields (gets scaled with zoomFactor)
    private static long gunGenerationTime = 100; //in milliseconds
    
    private SoundPlayer menuMusicPlayer, gameMusicPlayer, clicSoundPlayer, victorySoundPlayer, defeatSoundPlayer;
    
    private Map map;
    private TileSelector tileSelector;
    private Player player;
    private ArrayList <Player> otherPlayersList;
    private int textureSize, mapWidth, mapHeight, panelWidth, panelHeight, gameState, originalWidth, originalHeight;
    private GameMode gameMode;
    private ArrayList<Integer> pressedButtons, releasedButtons;
    private boolean isHost, interfaceBuilt = false, setStartingTile, isConnected, muteMusic, muteSounds, leftMousePressed, rightMousePressed, endShowed;
    private long lastGunGeneration;
    private SQLManager sql;
    private ArrayList <JComponent> MMbuttons, MEbuttons, PGbuttons, Ebuttons, GMbuttons;
    private ArrayList <Rectangle> MMoriginalBounds, MEoriginalBounds, PGoriginalBounds, EoriginalBounds, GMoriginalBounds;
    private ArrayList <ImageIcon> MMicons, MEicons, PGicons, Eicons, GMicons, MMpressedIcons, MEpressedIcons;
    private ArrayList<Bullet> otherPlayersBullets;
    private GameTimer timer;
    private int[] mousePosition;
    private int numberOfSkins = 5;
    private double wantedWidthByHeightRatio;
    private JFrame frame;
    
    public GamePanel(int textureSize, int mapWidth, int mapHeight, GameTimer timer){
        super();
        menuMusicPlayer = new SoundPlayer("menuMusic.mp3", true);
        gameMusicPlayer = new SoundPlayer("gameMusic.mp3", true);
        clicSoundPlayer = new SoundPlayer("clicSound.mp3", false);
        victorySoundPlayer = new SoundPlayer("victorySound.mp3", false);
        defeatSoundPlayer = new SoundPlayer("defeatSound.mp3", false);
        muteMusic = false;
        muteSounds = false;
        menuMusicPlayer.play();
        gameState = MAIN_MENU;
        gameMode = new GameMode();
        lastGunGeneration = System.currentTimeMillis();
        this.textureSize = textureSize;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        originalWidth = textureSize*(mapWidth+2);
        originalHeight = textureSize*(mapHeight+1);
        panelWidth = originalWidth;
        panelHeight = originalHeight;
        wantedWidthByHeightRatio = (double)panelWidth/panelHeight;
        isConnected = false;
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(panelWidth, panelHeight));
        map = new Map(mapWidth, mapHeight, textureSize, this);
        player = new Player(0,0);
        pressedButtons = new ArrayList<>();
        releasedButtons = new ArrayList<>();
        otherPlayersBullets = new ArrayList<>();
        otherPlayersList = new ArrayList<>();
        this.timer = timer;
        mapKeys();
        leftMousePressed = false;
        rightMousePressed = false;
        mousePosition = new int[]{0,0};
        endShowed = false;
        
        setFocusable(true);
        
        // handling mouse inputs
        addMouseMotionListener(new MouseAdapter(){
            @Override
            public void mouseDragged(MouseEvent e){
                mousePosition[0] = (int)((e.getX()-getGameX())/getZoomRatio());
                mousePosition[1] =(int)(e.getY()/getZoomRatio());
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
                if(e.getButton()==MouseEvent.BUTTON1){
                    leftMousePressed = true;
                }
                if(e.getButton()==MouseEvent.BUTTON3){
                    rightMousePressed = true;
                }
                mousePosition[0] = (int)((e.getX()-getGameX())/getZoomRatio());
                mousePosition[1] =(int)(e.getY()/getZoomRatio());
                if(gameState==MAP_EDITOR){
                    mapClicked();
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.getButton()==MouseEvent.BUTTON1){
                    leftMousePressed = false;
                }
                if(e.getButton()==MouseEvent.BUTTON3){
                    rightMousePressed = false;
                }
            }
        });  
        
        // Defining the thread which will handle mouse dragging
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    switch (gameState) {
                        case IN_GAME:
                            if (leftMousePressed) {
                                playershoot();
                            } else if(rightMousePressed){
                            meleeAttack();
                            }
                            break;
                        case MAP_EDITOR:
                            if (leftMousePressed) {
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
    
    public void setFrame(JFrame frame){
        this.frame = frame;
    }

    public int getOriginalWidth() {
        return originalWidth;
    }

    public int getOriginalHeight() {
        return originalHeight;
    }

    public void setOriginalValues(double ratio){
        originalWidth /= ratio;
        originalHeight /= ratio;
        setPreferredSize(new Dimension(originalWidth, originalHeight));
    }
    
    public void buildInterface(){
        tileSelector = new TileSelector(textureSize, originalWidth, originalHeight); // needs to be done here, since original dimensions might change after GamePanel creation
        setLayout(null);
        MMbuttons = new ArrayList<>(); //MM : Main menu
        MEbuttons = new ArrayList<>(); //ME : Map Editor
        PGbuttons = new ArrayList<>(); // Pre game
        Ebuttons = new ArrayList<>(); // Ending (Victory or Defeat)
        GMbuttons = new ArrayList<>(); //GM : Game Mode
        MMoriginalBounds = new ArrayList<>(); // contains original buttons sizes (used when resizing)
        MEoriginalBounds = new ArrayList<>();
        PGoriginalBounds = new ArrayList<>();
        EoriginalBounds = new ArrayList<>();
        GMoriginalBounds = new ArrayList<>();
        MMicons = new ArrayList<>(); // contains original icons
        MEicons = new ArrayList<>();
        PGicons = new ArrayList<>();
        Eicons = new ArrayList<>();
        GMicons = new ArrayList<>();
        MMpressedIcons = new ArrayList<>();
        MEpressedIcons = new ArrayList<>();
        /*
        ----------------------------------------------------------------------------------------------------------------
        
        Buttons used during the game
        
        ----------------------------------------------------------------------------------------------------------------
        */

        //---------------------------------------------- Connect button ------------------------------------------------       
        Rectangle bounds;
        JButton connectButton = new JButton();
        connectButton.setVisible(true);
        bounds = new Rectangle((getWidth()-panelWidth)/2 + (int)(0.28027*panelWidth),(int)(0.5208*panelHeight), (int)(0.1953125*panelWidth), (int)(0.1181*panelHeight));
        connectButton.setBounds(bounds);
        connectButton.setIcon(new ImageIcon(joinGameIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        //connectButton.setPressedIcon(pressedJoinGameIcon);
        connectButton.setContentAreaFilled(false);
        connectButton.setBorderPainted(true);
        add(connectButton);
        MMbuttons.add(connectButton);
        MMoriginalBounds.add(bounds);
        MMicons.add(joinGameIcon);
        MMpressedIcons.add(null);
        
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                initialiseGame(false, false);
            }
        });
        
        //---------------------------------------------- Create game Button ------------------------------------------             
        
        JButton gameCreateButton = new JButton();
        bounds = new Rectangle((int)(0.28027*panelWidth)+(getWidth()-panelWidth)/2,(int)(0.3941*panelHeight), (int)(0.1953125*panelWidth), (int)(0.1181*panelHeight));
        gameCreateButton.setBounds(bounds);
        gameCreateButton.setIcon(new ImageIcon(createGameIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        //gameCreateButton.setPressedIcon(pressedcreateGameIcon);
        gameCreateButton.setVisible(true);
        gameCreateButton.setContentAreaFilled(false);
        gameCreateButton.setBorderPainted(true);
        add(gameCreateButton);
        MMbuttons.add(gameCreateButton);
        MMoriginalBounds.add(bounds);
        MMicons.add(createGameIcon);
        MMpressedIcons.add(null);
        
        gameCreateButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton()==MouseEvent.BUTTON1){ // left mouse button
                    playClicSound();
                    initialiseGame(true, false);
                }
                if (e.getButton() == MouseEvent.BUTTON3) { // right mouse button
                    playClicSound();
                    initialiseGame(true, true);
                }
            }
        });  
        
        //---------------------------------------------- Exit button --------------------------------------------    
        
        JButton exitButton = new JButton();
        bounds = new Rectangle((int)(0.28027*panelWidth)+(getWidth()-panelWidth)/2,(int)(0.6476*panelHeight), (int)(0.1953125*panelWidth), (int)(0.1181*panelHeight));
        exitButton.setBounds(bounds);
        exitButton.setIcon(new ImageIcon(exitIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        //exitButton.setPressedIcon(pressedExitIcon);
        exitButton.setVisible(true);
        exitButton.setContentAreaFilled(false);
        exitButton.setBorderPainted(true);
        add(exitButton);
        MMbuttons.add(exitButton);
        MMoriginalBounds.add(bounds);
        MMicons.add(exitIcon);
        MMpressedIcons.add(null);
        
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                quitGame();
            }
        });
        
        //---------------------------------------------- Game mode button --------------------------------------------   
        
        JButton gameModeButton = new JButton();
        bounds = new Rectangle((int)(0.28027*panelWidth)+(getWidth()-panelWidth)/2,(int)(0.2674*panelHeight), (int)(0.1953125*panelWidth), (int)(0.1181*panelHeight));
        gameModeButton.setBounds(bounds);
        gameModeButton.setIcon(new ImageIcon(gameModeIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        //gameModeButton.setPressedIcon(pressedGameModeIcon);
        gameModeButton.setVisible(true);
        gameModeButton.setContentAreaFilled(false);
        gameModeButton.setBorderPainted(true);
        add(gameModeButton);
        MMbuttons.add(gameModeButton);
        MMoriginalBounds.add(bounds);
        MMicons.add(gameModeIcon);
        MMpressedIcons.add(null);
        
        gameModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                setState(GAME_MODE);
            }
            }
        );
        
        //--------------------------------------- Right arrow for skin selection ------------------------------------   
        
        JButton rightSkinArrow = new JButton();
        bounds = new Rectangle((int)(0.1777*panelWidth)+(getWidth()-panelWidth)/2,(int)(0.7639*panelHeight), (int)(0.0469*panelWidth), (int)(0.0833*panelHeight));
        rightSkinArrow.setBounds(bounds);
        rightSkinArrow.setIcon(new ImageIcon(rightArrowIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        rightSkinArrow.setPressedIcon(new ImageIcon(pressedrightArrowIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        MMoriginalBounds.add(bounds);
        MMicons.add(rightArrowIcon);
        MMpressedIcons.add(pressedrightArrowIcon);
        rightSkinArrow.setVisible(true);
        rightSkinArrow.setContentAreaFilled(false);
        rightSkinArrow.setBorderPainted(false);
        add(rightSkinArrow);
        MMbuttons.add(rightSkinArrow);
        
        rightSkinArrow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                getPlayer().setSkin((player.getSkinIndex() % numberOfSkins) + 1);
                repaint();
            }
        });
        
        //--------------------------------------- Left arrow for skin selection ------------------------------------    
        
        JButton leftSkinArrow = new JButton();
        bounds = new Rectangle((int)(0.0527*panelWidth)+(getWidth()-panelWidth)/2,(int)(0.7639*panelHeight), (int)(0.0469*panelWidth), (int)(0.0833*panelHeight));
        leftSkinArrow.setBounds(bounds);
        leftSkinArrow.setIcon(new ImageIcon(leftArrowIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        leftSkinArrow.setPressedIcon(new ImageIcon(pressedleftArrowIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        MMoriginalBounds.add(bounds);
        MMicons.add(leftArrowIcon);
        MMpressedIcons.add(pressedleftArrowIcon);
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
                if (skinIndex < 1){
                    skinIndex = numberOfSkins;
                }
                getPlayer().setSkin(skinIndex);
                repaint();
            }
        });
        
        //--------------------------------------- Up arrow for the map editor ------------------------------------  
        
        JButton MEtopSkinArrow = new JButton();
        bounds = new Rectangle((int)(0.0283*panelWidth)+(getWidth()-panelWidth)/2,(int)(0.0278*panelHeight), (int)(0.0391*panelWidth), (int)(0.0972*panelHeight));
        MEtopSkinArrow.setBounds(bounds);
        MEtopSkinArrow.setIcon(new ImageIcon(topArrowIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        MEtopSkinArrow.setPressedIcon(new ImageIcon(pressedtopArrowIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        MEoriginalBounds.add(bounds);
        MEicons.add(topArrowIcon);
        MEpressedIcons.add(pressedtopArrowIcon);
        MEtopSkinArrow.setVisible(false);
        MEtopSkinArrow.setContentAreaFilled(false);
        MEtopSkinArrow.setBorderPainted(false);
        add(MEtopSkinArrow);
        MEbuttons.add(MEtopSkinArrow);
        
        MEtopSkinArrow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                tileSelector.changeSelectedRow(-1);
                repaint();
            }
        });
        
        //--------------------------------------- Down arrow for the map editor ------------------------------------ 
        
        JButton MEBottomSkinArrow = new JButton();
        bounds = new Rectangle((int)(0.0283*panelWidth)+(getWidth()-panelWidth)/2,(int)(0.1354*panelHeight), (int)(0.0391*panelWidth), (int)(0.0972*panelHeight));
        MEBottomSkinArrow.setBounds(bounds);
        MEBottomSkinArrow.setIcon(new ImageIcon(bottomArrowIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        MEBottomSkinArrow.setPressedIcon(new ImageIcon(pressedbottomArrowIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        MEoriginalBounds.add(bounds);
        MEicons.add(bottomArrowIcon);
        MEpressedIcons.add(pressedbottomArrowIcon);
        MEBottomSkinArrow.setVisible(false);
        MEBottomSkinArrow.setContentAreaFilled(false);
        MEBottomSkinArrow.setBorderPainted(false);
        add(MEBottomSkinArrow);
        MEbuttons.add(MEBottomSkinArrow);        
        
        MEBottomSkinArrow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                tileSelector.changeSelectedRow(1);
                repaint();
            }
        });
        //--------------------------------------- Right arrow map selection ------------------------------------  
        
        JButton rightMapArrow = new JButton();
        bounds = new Rectangle((int)(0.8047*panelWidth)+(getWidth()-panelWidth)/2,(int)(0.7639*panelHeight), (int)(0.0469*panelWidth), (int)(0.0833*panelHeight));
        rightMapArrow.setBounds(bounds);
        rightMapArrow.setIcon(new ImageIcon(rightArrowIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        rightMapArrow.setPressedIcon(new ImageIcon(pressedrightArrowIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        MMoriginalBounds.add(bounds);
        MMicons.add(rightArrowIcon);
        MMpressedIcons.add(pressedrightArrowIcon);
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
        bounds = new Rectangle((int)(0.6221*panelWidth)+(getWidth()-panelWidth)/2,(int)(0.7639*panelHeight), (int)(0.0469*panelWidth), (int)(0.0833*panelHeight));
        leftMapArrow.setBounds(bounds);
        leftMapArrow.setIcon(new ImageIcon(leftArrowIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        leftMapArrow.setPressedIcon(new ImageIcon(pressedleftArrowIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        MMoriginalBounds.add(bounds);
        MMicons.add(leftArrowIcon);
        MMpressedIcons.add(pressedleftArrowIcon);
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
        bounds = new Rectangle((int)(0.5244*panelWidth)+(getWidth()-panelWidth)/2,(int)(0.2431*panelHeight), (int)(0.1709*panelWidth), (int)(0.0729*panelHeight)-10);
        mapEditorButton.setBounds(bounds);
        mapEditorButton.setIcon(new ImageIcon(mapEditorIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        //mapEditorButton.setPressedIcon(new ImageIcon(pressedleftArrowIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        MMoriginalBounds.add(bounds);
        MMicons.add(mapEditorIcon);
        MMpressedIcons.add(null);
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
        bounds = new Rectangle((int)(0.0068*panelWidth)+(getWidth()-panelWidth)/2,(int)(0.3681*panelHeight), (int)(0.0820*panelWidth), (int)(0.0729*panelHeight));
        saveMapButton.setBounds(bounds);
        saveMapButton.setIcon(new ImageIcon(saveMapIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        //saveMapButton.setPressedIcon(new ImageIcon(pressedleftArrowIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        MEoriginalBounds.add(bounds);
        MEicons.add(saveMapIcon);
        MEpressedIcons.add(null);
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
        bounds = new Rectangle((int)(0.0068*panelWidth)+(getWidth()-panelWidth)/2,(int)(0.2830*panelHeight), (int)(0.0820*panelWidth), (int)(0.0729*panelHeight));
        loadMapButton.setBounds(bounds);
        loadMapButton.setIcon(new ImageIcon(loadMapIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        //loadMapButton.setPressedIcon(new ImageIcon(pressedleftArrowIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        MEoriginalBounds.add(bounds);
        MEicons.add(loadMapIcon);
        MEpressedIcons.add(null);
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
                    setState(MAP_EDITOR);
                }
                repaint();
            }
        });
        
        //-------------------------------------- Done button for the map editor ------------------------------------------  
        
        JButton doneButton = new JButton();
        bounds = new Rectangle((int)(0.0068*panelWidth)+(getWidth()-panelWidth)/2,(int)(0.4844*panelHeight), (int)(0.0820*panelWidth), (int)(0.0729*panelHeight));
        doneButton.setBounds(bounds);
        doneButton.setIcon(new ImageIcon(doneIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        //doneButton.setPressedIcon(new ImageIcon(pressedleftArrowIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        MEoriginalBounds.add(bounds);
        MEicons.add(doneIcon);
        MEpressedIcons.add(null);
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
        bounds = new Rectangle((int)(0.7354*panelWidth)+(getWidth()-panelWidth)/2,(int)(0.0417*panelHeight), (int)(0.2588*panelWidth), (int)(0.0729*panelHeight));
        setStartingTileButton.setBounds(bounds);
        setStartingTileButton.setIcon(new ImageIcon(startingTileIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        //setStartingTileButton.setPressedIcon(new ImageIcon(pressedleftArrowIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        MEoriginalBounds.add(bounds);
        MEicons.add(startingTileIcon);
        MEpressedIcons.add(null);
        setStartingTileButton.setName("setStartingTileButton");
        setStartingTileButton.setVisible(false);
        setStartingTileButton.setBorderPainted(true);
        add(setStartingTileButton);
        MEbuttons.add(setStartingTileButton);
        
        setStartingTileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                setStartingTile = true;
                repaint();
            }
        });
        
        //------------------------------------- Start game button during lobby ----------------------------- 
        
        JButton startButton = new JButton();
        bounds = new Rectangle((int)(0.3516*panelWidth)+(getWidth()-panelWidth)/2,(int)(0.4063*panelHeight), (int)(0.3223*panelWidth), (int)(0.1233*panelHeight));
        startButton.setBounds(bounds);
        startButton.setIcon(new ImageIcon(startGameIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        //startButton.setPressedIcon(new ImageIcon(pressedleftArrowIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        PGoriginalBounds.add(bounds);
        PGicons.add(startGameIcon);
        startButton.setVisible(false);
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
                
        JButton muteSoundsButton = new JButton();
        muteSoundsButton.setName("muteSoundsButton");
        bounds = new Rectangle((int)(0.8203*panelWidth)+(getWidth()-panelWidth)/2,(int)(0.0347*panelHeight), (int)(0.0391*panelWidth), (int)(0.0694*panelHeight));
        muteSoundsButton.setBounds(bounds);
        muteSoundsButton.setIcon(new ImageIcon(SoundsIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        //muteSoundsButton.setPressedIcon(new ImageIcon(pressedleftArrowIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        MMoriginalBounds.add(bounds);
        MMicons.add(null); // since it has 2 icons, they are dealt with using component's name
        MMpressedIcons.add(null);
        muteSoundsButton.setVisible(true);
        muteSoundsButton.setBorderPainted(false);
        add(muteSoundsButton);
        MMbuttons.add(muteSoundsButton);
        
        muteSoundsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                muteSounds = !muteSounds;
                player.setMuteSounds(muteSounds);
                Rectangle bounds = muteSoundsButton.getBounds();
                if(muteSounds){
                    muteSoundsButton.setIcon(new ImageIcon(muteSoundsIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
                }else{
                    muteSoundsButton.setIcon(new ImageIcon(SoundsIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
                    playClicSound();
                }
            }
        });
        
        //--------------------------------------------- Mute music button ------------------------------------------  
        
        JButton muteMusicButton = new JButton();
        muteMusicButton.setName("muteMusicButton");
        bounds = new Rectangle((int)(0.8789*panelWidth)+(getWidth()-panelWidth)/2,(int)(0.0347*panelHeight), (int)(0.0391*panelWidth), (int)(0.0694*panelHeight));
        muteMusicButton.setBounds(bounds);
        muteMusicButton.setIcon(new ImageIcon(MusicIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        //muteMusicButton.setPressedIcon(new ImageIcon(pressedleftArrowIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        MMoriginalBounds.add(bounds);
        MMicons.add(null); // since it has 2 icons, they are dealt with using component's name
        MMpressedIcons.add(null);
        muteMusicButton.setVisible(true);
        muteMusicButton.setBorderPainted(false);
        add(muteMusicButton);
        MMbuttons.add(muteMusicButton);
                
        muteMusicButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                muteMusic = !muteMusic;
                Rectangle bounds = muteMusicButton.getBounds();
                if(muteMusic){
                    muteMusicButton.setIcon(new ImageIcon(muteMusicIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
                    menuMusicPlayer.stop();
                } else {
                    muteMusicButton.setIcon(new ImageIcon(MusicIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
                    menuMusicPlayer.play();
                }
            }
        });
        
        //-------------------------------- Username input area --------------------------------- 
        
        JTextField usernameField = new JTextField("Username");
        bounds = new Rectangle((int)(0.0547*panelWidth)+(getWidth()-panelWidth)/2,(int)(0.2431*panelHeight), (int)(0.1680*panelWidth), (int)(0.1040*panelHeight));
        usernameField.setBounds(bounds);
        MMoriginalBounds.add(bounds);
        MMicons.add(null);
        MMpressedIcons.add(null);
        usernameField.setEditable(true);
        usernameField.setName("textField");
        usernameField.setHorizontalAlignment(JTextField.CENTER);
        usernameField.setFont(new Font("Stencil", Font.BOLD, (int)(FONTSIZE*getZoomRatio())));
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
        bounds = new Rectangle((int)(getWidth()-0.1563*panelWidth)/2,(int)(0.8681*panelHeight), (int)(0.1563*panelWidth), (int)(0.0729*panelHeight));
        mainMenuButton.setBounds(bounds);
        mainMenuButton.setIcon(new ImageIcon(mainMenuIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        //mainMenuButton.setPressedIcon(new ImageIcon(pressedleftArrowIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        EoriginalBounds.add(bounds);
        Eicons.add(mainMenuIcon);
        mainMenuButton.setName("mainMenuButton");
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
        bounds = new Rectangle((int)(getWidth()-0.2588*panelWidth)/2,(int)(0.6944*panelHeight), (int)(0.2588*panelWidth), (int)(0.0729*panelHeight));
        spectateGameButton.setBounds(bounds);
        spectateGameButton.setIcon(new ImageIcon(spectateIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        //spectateGameButton.setPressedIcon(new ImageIcon(pressedleftArrowIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        EoriginalBounds.add(bounds);
        Eicons.add(spectateIcon);
        spectateGameButton.setName("spectateGameButton");
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
//--------------------------------------------- Game mode menu buttons ------------------------------------------  
// buttons need to be added in the same order as gameMode ids

        JTextArea descriptionText = new JTextArea((int)(20*getZoomRatio()),(int) (50*getZoomRatio())); // needs to be declared before buttons because they use it, but needs to be added to GMbuttons after gameMode buttons
        descriptionText.setSize((int)(20*getZoomRatio()),(int) (50*getZoomRatio()));
        
        JButton defaultButton = new JButton("Default");
        //bounds = new Rectangle((int)(0.0547*panelWidth)+(getWidth()-panelWidth)/2,(int)(0.2431*panelHeight), (int)(0.1680*panelWidth), (int)(0.1040*panelHeight));
        bounds = new Rectangle(100, 350, doneIcon.getIconWidth(), doneIcon.getIconHeight());
        defaultButton.setBounds(bounds);
        //defaultButton.setIcon(new ImageIcon(spectateIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        //defaultButton.setPressedIcon(new ImageIcon(pressedleftArrowIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        GMoriginalBounds.add(bounds);
        GMicons.add(null);
        defaultButton.setVisible(false);
        defaultButton.setBorderPainted(true);
        add(defaultButton);
        GMbuttons.add(defaultButton);
        
        defaultButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                gameMode.setId(GameMode.DEFAULT);
                descriptionText.setText(gameMode.getDescription());
                repaint();
            }
        });
        
        JButton royalButton = new JButton("Royal");
        //bounds = new Rectangle((int)(0.0547*panelWidth)+(getWidth()-panelWidth)/2,(int)(0.2431*panelHeight), (int)(0.1680*panelWidth), (int)(0.1040*panelHeight));
        bounds = new Rectangle(100, 400, doneIcon.getIconWidth(), doneIcon.getIconHeight());
        royalButton.setBounds(bounds);
        //defaultButton.setIcon(new ImageIcon(spectateIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        //defaultButton.setPressedIcon(new ImageIcon(pressedleftArrowIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        GMoriginalBounds.add(bounds);
        GMicons.add(null);
        royalButton.setVisible(false);
        royalButton.setBorderPainted(true);
        add(royalButton);
        GMbuttons.add(royalButton);
        
        royalButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                gameMode.setId(GameMode.ROYAL);
                descriptionText.setText(gameMode.getDescription());
                repaint();
            }
        });
        
        JButton teamButton = new JButton("Team");
        //bounds = new Rectangle((int)(0.0547*panelWidth)+(getWidth()-panelWidth)/2,(int)(0.2431*panelHeight), (int)(0.1680*panelWidth), (int)(0.1040*panelHeight));
        bounds = new Rectangle(100, 450, doneIcon.getIconWidth(), doneIcon.getIconHeight());
        teamButton.setBounds(bounds);
        //teamButton.setIcon(new ImageIcon(spectateIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        //teamButton.setPressedIcon(new ImageIcon(pressedleftArrowIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        GMoriginalBounds.add(bounds);
        GMicons.add(null);
        teamButton.setVisible(false);
        teamButton.setBorderPainted(true);
        add(teamButton);
        GMbuttons.add(teamButton);
        
        teamButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                gameMode.setId(GameMode.TEAM);
                descriptionText.setText(gameMode.getDescription());
                repaint();
            }
        });
        
        JButton aloneButton = new JButton("Alone");
        //bounds = new Rectangle((int)(0.0547*panelWidth)+(getWidth()-panelWidth)/2,(int)(0.2431*panelHeight), (int)(0.1680*panelWidth), (int)(0.1040*panelHeight));
        bounds = new Rectangle(100, 500, doneIcon.getIconWidth(), doneIcon.getIconHeight());
        aloneButton.setBounds(bounds);
        //teamButton.setIcon(new ImageIcon(spectateIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        //teamButton.setPressedIcon(new ImageIcon(pressedleftArrowIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        GMoriginalBounds.add(bounds);
        GMicons.add(null);
        aloneButton.setVisible(false);
        aloneButton.setBorderPainted(true);
        add(aloneButton);
        GMbuttons.add(aloneButton);
        
        aloneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                gameMode.setId(GameMode.ALONE);
                descriptionText.setText(gameMode.getDescription());
                repaint();
            }
        });
        
        bounds = new Rectangle((int)(0.4688*panelWidth)+(getWidth()-panelWidth)/2,(int)(0.3472*panelHeight), (int)(0.4590*panelWidth), (int)(0.2778*panelHeight));
        descriptionText.setBounds(bounds);
        GMoriginalBounds.add(bounds);
        GMicons.add(null);
        descriptionText.setText(gameMode.getDescription());
        descriptionText.setEditable(false);
        descriptionText.setName("textField");
        descriptionText.setFont(new Font("Stencil", Font.BOLD, (int)(FONTSIZE*getZoomRatio())));
        descriptionText.setBackground(new Color(230,226,211));
        descriptionText.setForeground(Color.DARK_GRAY);
        descriptionText.setBorder(null);
        descriptionText.setVisible(false);
        add(descriptionText);
        GMbuttons.add(descriptionText);
//--------------------------------------------- Options for the game mode ------------------------------------------  
    
        JTextField suggestedMapText = new JTextField("Use suggested map");
        bounds = new Rectangle((int)(0.3809*panelWidth)+(getWidth()-panelWidth)/2,(int)(0.7986*panelHeight), (int)(0.1758*panelWidth), (int)(0.0521*panelHeight));
        suggestedMapText.setBounds(bounds);
        GMoriginalBounds.add(bounds);
        GMicons.add(null);
        suggestedMapText.setEditable(false);
        suggestedMapText.setHorizontalAlignment(JTextField.CENTER);
        suggestedMapText.setFont(new Font("Stencil", Font.BOLD, (int)(FONTSIZE*getZoomRatio())));
        suggestedMapText.setBackground(new Color(230,226,211));
        suggestedMapText.setName("textField");
        suggestedMapText.setForeground(Color.DARK_GRAY);
        suggestedMapText.setBorder(null);
        suggestedMapText.setVisible(false);
        add(suggestedMapText);
        GMbuttons.add(suggestedMapText);

        JButton suggestedMapButton = new JButton();
        suggestedMapButton.setName("checkButton0");
        bounds = new Rectangle((int)(bounds.x + (bounds.width-0.0391*panelWidth)/2),(int)(0.8507*panelHeight), (int)(0.0391*panelWidth), (int)(0.0694*panelHeight));
        suggestedMapButton.setBounds(bounds);
        suggestedMapButton.setIcon(new ImageIcon(uncheckedIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        GMoriginalBounds.add(bounds);
        GMicons.add(null); // since it has 2 icons, they are dealt with using component's name
        suggestedMapButton.setVisible(false);
        suggestedMapButton.setBorderPainted(true);
        add(suggestedMapButton);
        GMbuttons.add(suggestedMapButton);
        
        suggestedMapButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                Rectangle bounds = suggestedMapButton.getBounds();
                if(gameMode.getOption(0)){
                    gameMode.setOption(0, false);
                    suggestedMapButton.setIcon(new ImageIcon(uncheckedIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
                } else {
                    gameMode.setOption(0, true);
                    suggestedMapButton.setIcon(new ImageIcon(checkedIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
                }
            }
        });
        
        JTextField rubberBallsText = new JTextField("Rubber balls");
        bounds = new Rectangle((int)(0.5664*panelWidth)+(getWidth()-panelWidth)/2,(int)(0.7986*panelHeight), (int)(0.1172*panelWidth), (int)(0.0521*panelHeight));
        rubberBallsText.setBounds(bounds);
        GMoriginalBounds.add(bounds);
        GMicons.add(null);
        rubberBallsText.setEditable(false);
        rubberBallsText.setHorizontalAlignment(JTextField.CENTER);
        rubberBallsText.setFont(new Font("Stencil", Font.BOLD, (int)(FONTSIZE*getZoomRatio())));
        rubberBallsText.setBackground(new Color(230,226,211));
        rubberBallsText.setName("textField");
        rubberBallsText.setForeground(Color.DARK_GRAY);
        rubberBallsText.setBorder(null);
        rubberBallsText.setVisible(false);
        add(rubberBallsText);
        GMbuttons.add(rubberBallsText);

        JButton rubberBallsButton = new JButton();
        rubberBallsButton.setName("checkButton1");
        bounds = new Rectangle((int)(bounds.x + (bounds.width-0.0391*panelWidth)/2),(int)(0.8507*panelHeight), (int)(0.0391*panelWidth), (int)(0.0694*panelHeight));
        rubberBallsButton.setBounds(bounds);
        rubberBallsButton.setIcon(new ImageIcon(uncheckedIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        GMoriginalBounds.add(bounds);
        GMicons.add(null); // since it has 2 icons, they are dealt with using component's name
        rubberBallsButton.setVisible(false);
        rubberBallsButton.setBorderPainted(true);
        add(rubberBallsButton);
        GMbuttons.add(rubberBallsButton);
        
        rubberBallsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                Rectangle bounds = rubberBallsButton.getBounds();
                if(gameMode.getOption(1)){
                    gameMode.setOption(1, false);
                    rubberBallsButton.setIcon(new ImageIcon(uncheckedIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
                } else {
                    gameMode.setOption(1, true);
                    rubberBallsButton.setIcon(new ImageIcon(checkedIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
                }
            }
        });
        
        JTextField bonusItemsText = new JTextField("Bonus items");
        bounds = new Rectangle((int)(0.6934*panelWidth)+(getWidth()-panelWidth)/2,(int)(0.7986*panelHeight), (int)(0.1172*panelWidth), (int)(0.0521*panelHeight));
        bonusItemsText.setBounds(bounds);
        GMoriginalBounds.add(bounds);
        GMicons.add(null);
        bonusItemsText.setEditable(false);
        bonusItemsText.setHorizontalAlignment(JTextField.CENTER);
        bonusItemsText.setFont(new Font("Stencil", Font.BOLD, (int)(FONTSIZE*getZoomRatio())));
        bonusItemsText.setBackground(new Color(230,226,211));
        bonusItemsText.setName("textField");
        bonusItemsText.setForeground(Color.DARK_GRAY);
        bonusItemsText.setBorder(null);
        bonusItemsText.setVisible(false);
        add(bonusItemsText);
        GMbuttons.add(bonusItemsText);

        JButton bonusItemsButton = new JButton();
        bonusItemsButton.setName("checkButton2");
        bounds = new Rectangle((int)(bounds.x + (bounds.width-0.0391*panelWidth)/2),(int)(0.8507*panelHeight), (int)(0.0391*panelWidth), (int)(0.0694*panelHeight));
        bonusItemsButton.setBounds(bounds);
        bonusItemsButton.setIcon(new ImageIcon(uncheckedIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        GMoriginalBounds.add(bounds);
        GMicons.add(null); // since it has 2 icons, they are dealt with using component's name
        bonusItemsButton.setVisible(false);
        bonusItemsButton.setBorderPainted(true);
        add(bonusItemsButton);
        GMbuttons.add(bonusItemsButton);
        
        bonusItemsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                Rectangle bounds = bonusItemsButton.getBounds();
                if(gameMode.getOption(2)){
                    gameMode.setOption(2, false);
                    bonusItemsButton.setIcon(new ImageIcon(uncheckedIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
                } else {
                    gameMode.setOption(2, true);
                    bonusItemsButton.setIcon(new ImageIcon(checkedIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
                }
            }
        });
        
        JTextField fastModeText = new JTextField("Fast mode");
        bounds = new Rectangle((int)(0.8203*panelWidth)+(getWidth()-panelWidth)/2,(int)(0.7986*panelHeight), (int)(0.1172*panelWidth), (int)(0.0521*panelHeight));
        fastModeText.setBounds(bounds);
        GMoriginalBounds.add(bounds);
        GMicons.add(null);
        fastModeText.setEditable(false);
        fastModeText.setHorizontalAlignment(JTextField.CENTER);
        fastModeText.setFont(new Font("Stencil", Font.BOLD, (int)(FONTSIZE*getZoomRatio())));
        fastModeText.setBackground(new Color(230,226,211));
        fastModeText.setName("textField");
        fastModeText.setForeground(Color.DARK_GRAY);
        fastModeText.setBorder(null);
        fastModeText.setVisible(false);
        add(fastModeText);
        GMbuttons.add(fastModeText);

        JButton fastModeButton = new JButton();
        fastModeButton.setName("checkButton3");
        bounds = new Rectangle((int)(bounds.x + (bounds.width-0.0391*panelWidth)/2),(int)(0.8507*panelHeight), (int)(0.0391*panelWidth), (int)(0.0694*panelHeight));
        fastModeButton.setBounds(bounds);
        fastModeButton.setIcon(new ImageIcon(uncheckedIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        GMoriginalBounds.add(bounds);
        GMicons.add(null); // since it has 2 icons, they are dealt with using component's name
        fastModeButton.setVisible(false);
        fastModeButton.setBorderPainted(true);
        add(fastModeButton);
        GMbuttons.add(fastModeButton);
        
        fastModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                Rectangle bounds = fastModeButton.getBounds();
                if(gameMode.getOption(3)){
                    gameMode.setOption(3, false);
                    fastModeButton.setIcon(new ImageIcon(uncheckedIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
                } else {
                    gameMode.setOption(3, true);
                    fastModeButton.setIcon(new ImageIcon(checkedIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
                }
                timer.setMultiplier(gameMode.getTimerMultiplier());
            }
        });

//--------------------------------------------- Done button for the game mode ------------------------------------------  
        
        JButton GMdoneButton = new JButton();
        bounds = new Rectangle((int)(0.0576*panelWidth)+(getWidth()-panelWidth)/2,(int)(0.8889*panelHeight), (int)(0.0820*panelWidth), (int)(0.0729*panelHeight));
        GMdoneButton.setBounds(bounds);
        GMdoneButton.setIcon(new ImageIcon(doneIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
        GMoriginalBounds.add(bounds);
        GMicons.add(doneIcon);
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
        map.setDrawingParameters(MAIN_MENU, this);
        interfaceBuilt = true;
    }
    
    public void updateSize() {
        if (frame.getContentPane().getWidth() > frame.getContentPane().getHeight() * wantedWidthByHeightRatio) {
            panelHeight = frame.getContentPane().getSize().height;
            panelWidth = (int) (frame.getContentPane().getHeight() * wantedWidthByHeightRatio);
        } else {
            panelWidth = frame.getContentPane().getSize().width;
            panelHeight = (int) (frame.getContentPane().getWidth() / wantedWidthByHeightRatio);
        }
        reScaleComponents();
        repaint();
    }

    public void reScaleComponents(){ // used to set bounds and icons when resizing
        if (interfaceBuilt) {
            Rectangle bounds;
            JComponent component;
            double zoomRatio = getZoomRatio();
            map.setDrawingParameters(gameState, this);
            for (int i = 0; i < MMbuttons.size(); i++) {
                bounds = MMoriginalBounds.get(i);
                bounds = new Rectangle((getWidth() - panelWidth) / 2 + (int) (bounds.x * zoomRatio), (int) (bounds.y * zoomRatio), (int) (bounds.width * zoomRatio), (int) (bounds.height * zoomRatio));
                component = MMbuttons.get(i);
                MMbuttons.get(i).setBounds(bounds);
                if ("muteMusicButton".equals(component.getName())) {
                    JButton muteMusicButton = (JButton) component;
                    if (muteMusic) {
                        muteMusicButton.setIcon(new ImageIcon(muteMusicIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
                    } else {
                        muteMusicButton.setIcon(new ImageIcon(MusicIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
                    }
                } else if ("muteSoundsButton".equals(component.getName())) {
                    JButton muteSoundsButton = (JButton) component;
                    if (muteSounds) {
                        muteSoundsButton.setIcon(new ImageIcon(muteSoundsIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
                    } else {
                        muteSoundsButton.setIcon(new ImageIcon(SoundsIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
                    }
                } else if ("textField".equals(component.getName())){
                    component.setFont(component.getFont().deriveFont((float)(FONTSIZE*zoomRatio)));
                } else if (MMicons.get(i) != null) {
                    JButton button = (JButton) component;
                    button.setIcon(new ImageIcon(MMicons.get(i).getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
                    if (MMpressedIcons.get(i) != null) {
                        button.setPressedIcon(new ImageIcon(MMpressedIcons.get(i).getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
                    }
                }
            }
            for (int i = 0; i < MEbuttons.size(); i++) {
                bounds = MEoriginalBounds.get(i);
                bounds = new Rectangle((getWidth() - panelWidth) / 2 + (int) (bounds.x * zoomRatio), (int) (bounds.y * zoomRatio), (int) (bounds.width * zoomRatio), (int) (bounds.height * zoomRatio));
                component = MEbuttons.get(i);
                MEbuttons.get(i).setBounds(bounds);
                if (MEicons.get(i) != null) {
                    JButton button = (JButton) component;
                    button.setIcon(new ImageIcon(MEicons.get(i).getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
                    if (MEpressedIcons.get(i) != null) {
                        button.setPressedIcon(new ImageIcon(MEpressedIcons.get(i).getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
                    }
                }
            }
            for (int i = 0; i < GMbuttons.size(); i++) {
                bounds = GMoriginalBounds.get(i);
                bounds = new Rectangle((getWidth() - panelWidth) / 2 + (int) (bounds.x * zoomRatio), (int) (bounds.y * zoomRatio), (int) (bounds.width * zoomRatio), (int) (bounds.height * zoomRatio));
                component = GMbuttons.get(i);
                GMbuttons.get(i).setBounds(bounds);
                for (int j = 0; j < gameMode.NUMBER_OF_OPTIONS; j++) {
                    if (("checkButton" + j).equals(component.getName())) {
                        JButton button = (JButton) component;
                        if (gameMode.getOption(j)) {
                            button.setIcon(new ImageIcon(checkedIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
                        } else {
                            button.setIcon(new ImageIcon(uncheckedIcon.getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
                        }
                    } else if ("textField".equals(component.getName())){
                        component.setFont(component.getFont().deriveFont((float)(FONTSIZE*zoomRatio)));
                    }
                }
                if (GMicons.get(i) != null) {
                    JButton button = (JButton) component;
                    button.setIcon(new ImageIcon(GMicons.get(i).getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
                }
            }
            for (int i = 0; i < PGbuttons.size(); i++) {
                bounds = PGoriginalBounds.get(i);
                bounds = new Rectangle((getWidth() - panelWidth) / 2 + (int) (bounds.x * zoomRatio), (int) (bounds.y * zoomRatio), (int) (bounds.width * zoomRatio), (int) (bounds.height * zoomRatio));
                component = PGbuttons.get(i);
                PGbuttons.get(i).setBounds(bounds);
                if (PGicons.get(i) != null) {
                    JButton button = (JButton) component;
                    button.setIcon(new ImageIcon(PGicons.get(i).getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
                }
            }
            for (int i = 0; i < Ebuttons.size(); i++) {
                bounds = EoriginalBounds.get(i);
                bounds = new Rectangle((getWidth() - panelWidth) / 2 + (int) (bounds.x * zoomRatio), (int) (bounds.y * zoomRatio), (int) (bounds.width * zoomRatio), (int) (bounds.height * zoomRatio));
                component = Ebuttons.get(i);
                Ebuttons.get(i).setBounds(bounds);
                if (Eicons.get(i) != null) {
                    JButton button = (JButton) component;
                    button.setIcon(new ImageIcon(Eicons.get(i).getImage().getScaledInstance(bounds.width, bounds.height, Image.SCALE_DEFAULT)));
                }
            }
        }
    }
    
    
    /*
    ----------------------------------------------------------------------------------------------------------------

    Main game updating function to handle the SQL server and player input

    ----------------------------------------------------------------------------------------------------------------
    */
    
    public void updateGame(long dT){
        boolean printTime = false; // Set to true if you want to print the time taken by each method in updateGame
        long time = System.currentTimeMillis();
        // Update player movement 
        updatePlayerMovement();
        
        player.update(dT, map);
        for(Player player : otherPlayersList)
        {
            player.updateAnimation(dT);
        }
        
        if(printTime){
            System.out.println("Player movement & update : " + (System.currentTimeMillis()-time));
            time = System.currentTimeMillis();
        }
        // sql downloads
        sql.downloadPlayersAndBullets(player, otherPlayersList, otherPlayersBullets, map);
        boolean TeamWasKilled = player.isTeamkilled(otherPlayersList, false); // used to check if team died
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
        } else if(TeamWasKilled && !endShowed){ // team just died : show defeat screen
            setState(ENDING);
            endShowed = true;
            if (!muteSounds){
                    defeatSoundPlayer.play();
                }
        }
        if(player.isTeamkilled(otherPlayersList, true)){ // Game is ended
            if (!TeamWasKilled || !player.isDead()){ // Local team/player won : show victory screen
                setState(ENDING);
                if (!muteSounds){
                    victorySoundPlayer.play();
                }
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
            player.setFacedDirection(1);
            player.setAcceleration(0, -1);
            player.setDirectionOfTravel(0, -1);
        }
        if (pressedButtons.contains(KeyEvent.VK_D) || pressedButtons.contains(KeyEvent.VK_RIGHT)){
            player.setFacedDirection(2);
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
    
    public void mapClicked() {
        int[] mapClicked = map.clickedTile(mousePosition[0], mousePosition[1]);
        if (mapClicked[0] > -1) { // map was clicked
            playClicSound();
            if (!setStartingTile) {
                map.setTile(mapClicked[1], mapClicked[2], tileSelector.getSelectedTile());
            } else {
                map.addStartTile(new int[]{mapClicked[1], mapClicked[2]});
            }
        } else { // check if tileSelector was clicked and select the tile if so
            if (tileSelector.clickedTile(mousePosition[0], mousePosition[1])[0] > -1) {
                playClicSound();
                setStartingTile = false;
            }
        }
        repaint();
    }
    
    public void mapKeys(){
        ArrayList<String> keyStrokeList = new ArrayList<>();
        keyStrokeList.add("S"); keyStrokeList.add("Z"); keyStrokeList.add("Q"); keyStrokeList.add("D");
        keyStrokeList.add("DOWN"); keyStrokeList.add("UP"); keyStrokeList.add("LEFT"); keyStrokeList.add("RIGHT"); keyStrokeList.add("ESCAPE");
        for (String key : keyStrokeList){
            getInputMap().put(KeyStroke.getKeyStroke(key), key + "Pressed");
            getInputMap().put(KeyStroke.getKeyStroke("released " + key), key + "Released");
        }
        getInputMap().put(KeyStroke.getKeyStroke("F"), "FPressed");
        getInputMap().put(KeyStroke.getKeyStroke(' '), "spacePressed");
        
        
        getActionMap().put("ZPressed", new KeyPressed(KeyEvent.VK_Z));
        getActionMap().put("ZReleased", new KeyReleased(KeyEvent.VK_Z) );
        getActionMap().put("SPressed", new KeyPressed(KeyEvent.VK_S));
        getActionMap().put("SReleased", new KeyReleased(KeyEvent.VK_S) );
        getActionMap().put("QPressed", new KeyPressed(KeyEvent.VK_Q));
        getActionMap().put("QReleased", new KeyReleased(KeyEvent.VK_Q) );
        getActionMap().put("DPressed", new KeyPressed(KeyEvent.VK_D));
        getActionMap().put("DReleased", new KeyReleased(KeyEvent.VK_D) );
        getActionMap().put("UPPressed", new KeyPressed(KeyEvent.VK_UP));
        getActionMap().put("UPReleased", new KeyReleased(KeyEvent.VK_UP) );
        getActionMap().put("DOWNPressed", new KeyPressed(KeyEvent.VK_DOWN));
        getActionMap().put("DOWNReleased", new KeyReleased(KeyEvent.VK_DOWN) );
        getActionMap().put("LEFTPressed", new KeyPressed(KeyEvent.VK_LEFT));
        getActionMap().put("LEFTReleased", new KeyReleased(KeyEvent.VK_LEFT) );
        getActionMap().put("RIGHTPressed", new KeyPressed(KeyEvent.VK_RIGHT));
        getActionMap().put("RIGHTReleased", new KeyReleased(KeyEvent.VK_RIGHT) );
        getActionMap().put("ESCAPEPressed", new EscapePressed());
        getActionMap().put("FPressed", new Taunt());
        getActionMap().put("spacePressed", new spaceBarPressed());
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
    
    private class Taunt extends AbstractAction{
        
        public Taunt(){
        }
        
        @Override
        public void actionPerformed( ActionEvent tf ){
            if(gameState==IN_GAME){
                player.taunt();
            }
        }
    }
    
    private class spaceBarPressed extends AbstractAction{
        
        public spaceBarPressed(){
        }
        
        @Override
        public void actionPerformed( ActionEvent tf ){
            if(gameState==IN_GAME){
                player.roll();
            }
        }
    }
    
    /*
    ----------------------------------------------------------------------------------------------------------------

    Functions to properly start and end a game

    ----------------------------------------------------------------------------------------------------------------
    */
    public void initialiseGame(boolean isHost, boolean forceDatabaseClear) {
        this.isHost = isHost;
        sql = new SQLManager();
        int[] sqlGame = sql.getGame();
        if (isHost) {
            // Try to create a game
            ArrayList<Player> playerList = sql.getPlayerList();
            if (playerList.size()<2) { // No game is currently on
                sql.clearTable(); //Clear previous game on SQL server
                sql.createGame(map, gameMode);
                player.reset(map, muteSounds);
                player.setPlayerId(1);
                player.setTeamId(1);
                player.addPlayer(sql, gameMode.getNumberOfBounces());
                isConnected = true;
                setState(PRE_GAME);
            } else {
                if (forceDatabaseClear) {
                    int confirm = JOptionPane.showOptionDialog(
                            null, "Do you really want to clear the database (this will cancel any game currently running)?",
                            "Clear the database", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, null, null);
                    if (confirm == 0) {
                        sql.clearTable();
                        initialiseGame(true, false);
                    }
                } else {
                    if (sqlGame[0] == PRE_GAME) {
                        int confirm = JOptionPane.showOptionDialog(
                                null, "A game is already being created, to you want to join it ?",
                                "Join the game ?", JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE, null, null, null);
                        if (confirm == 0) {
                            initialiseGame(false, false);
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
            }
        } else { // Try to join a Pre_game
            if (sqlGame[0] == PRE_GAME) {
                otherPlayersList = sql.getPlayerList();
                map = sql.getMap(textureSize);
                player.reset(map, muteSounds);
                gameMode.setId(sqlGame[1]);
                gameMode.setOption(1, sqlGame[2]==1);
                gameMode.setOption(2, sqlGame[3]==1);
                gameMode.setOption(3, sqlGame[4]==1);
                timer.setMultiplier(gameMode.getTimerMultiplier());
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
                        } else {
                            player.setTeamId(1);
                        }
                        break;
                }
                player.addPlayer(sql, gameMode.getNumberOfBounces());
                isConnected = true;
                setState(PRE_GAME);
            } else {
                if(sql.getPlayerList().isEmpty()){ // No game created
                    int confirm = JOptionPane.showOptionDialog(
                            null, "There is no game to join, do you want to create one ?",
                            "Create the game ?", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, null, null);
                    if (confirm == 0) {
                        initialiseGame(true, false);
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
        endShowed =false;
        int formerGameState = gameState;
        setState(MAIN_MENU);
        if (isConnected){
            if(!player.isDead()){
                sql.removePlayer(player);
            }
            if (sql.getPlayerList().isEmpty() || (isHost && formerGameState == PRE_GAME)) {
                sql.clearTable();
            }
            sql.disconnect();
        }
        isConnected = false;
    }
    
    //-------------------------------------------------------------------------------------------------------------------------
    
    public void playershoot(){
        double[] wantedDirection = new double[2];
        wantedDirection[0] = mousePosition[0] - player.getPosX() - textureSize / 2;
        wantedDirection[1] = mousePosition[1] - player.getPosY() - textureSize / 2;

        double norme = Math.sqrt(wantedDirection[0] * wantedDirection[0] + wantedDirection[1] * wantedDirection[1]);
        wantedDirection[0] = wantedDirection[0] / norme;
        wantedDirection[1] = wantedDirection[1] / norme;
        player.shoot(wantedDirection, sql, gameMode.getNumberOfBounces());
    }
    
    public void meleeAttack(){
        double[] directionOfFire = new double[2];
        directionOfFire[0] = mousePosition[0] - player.getPosX() - textureSize / 2;
        directionOfFire[1] = mousePosition[1] - player.getPosY() - textureSize / 2;

        double norme = Math.sqrt(directionOfFire[0] * directionOfFire[0] + directionOfFire[1] * directionOfFire[1]);
        directionOfFire[0] = directionOfFire[0] / norme;
        directionOfFire[1] = directionOfFire[1] / norme;
        
        player.kick(directionOfFire, sql);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        switch(gameState) {
            case PRE_GAME:
                g2d.drawImage(PreGameBackground, (getWidth()-panelWidth)/2, 0, panelWidth, panelHeight, this);
                g2d.setFont(new Font("Stencil", Font.BOLD, (int)(FONTSIZE*getZoomRatio())));
                map.draw(g2d, false, this);
                if (isHost) {
                    g2d.drawString(player.getName(),(getWidth()-panelWidth)/2 + (int)(90*getZoomRatio()), (int)(140*getZoomRatio()));
                    for (int i = 0; i < otherPlayersList.size(); i++) {
                        g2d.drawString(otherPlayersList.get(i).getName(), (getWidth()-panelWidth)/2 + (int)(90*getZoomRatio()), (int)((140 + i * 50)*getZoomRatio()));
                    }
                } else {
                    for (int i = 0; i < otherPlayersList.size(); i++) {
                        g2d.drawString(otherPlayersList.get(i).getName(), (getWidth()-panelWidth)/2 + (int)(90*getZoomRatio()), (int)((140 + i * 50)*getZoomRatio()));
                    }
                    g2d.drawString(player.getName(), (getWidth()-panelWidth)/2 + (int)(90*getZoomRatio()), (int)(140+(otherPlayersList.size())*50*getZoomRatio()));
                }
                break;

            case MAIN_MENU:
                g2d.drawImage(MenuBackground, (getWidth()-panelWidth)/2, 0, panelWidth, panelHeight, this);
                g2d.drawImage(player.getImage(), (getWidth()-panelWidth)/2 +(int)((180-player.getPlayerWidth())/2*getZoomRatio()), (panelHeight-player.getPlayerHeight())/2, (int)(160*getZoomRatio()), (int)(160*getZoomRatio()), this);
                map.draw(g2d, false, this);
                break;

            case IN_GAME:
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.RED.darker());
                g2d.fillRect(getGameX(), 0, panelWidth, panelHeight);
                map.draw(g2d, false, this);
                player.draw(g2d, this);
                player.drawBullets(g2d, map.getTextureSize(), this);

                for (Player otherPlayer : otherPlayersList) {
                    otherPlayer.draw(g2d, this);
                }
                for (int i=0; i<otherPlayersBullets.size(); i++){
                    otherPlayersBullets.get(i).draw(g2d, textureSize, this);
                }
                break;

            case MAP_EDITOR:
                g2d.drawImage(EditorBackground, (getWidth()-panelWidth)/2, 0, panelWidth, panelHeight, this);     
                map.draw(g2d, true, this);
                tileSelector.draw(g2d, setStartingTile, this);
                if (setStartingTile) { // draw a rectangle around setStartingTileButton
                    int index = MEbuttons.size()-1;
                    while(!MEbuttons.get(index).getName().equals("setStartingTileButton")){ //find the button
                        index--;
                    }
                    if(index>-1){
                        g2d.setStroke(new BasicStroke(5));
                        g2d.setColor(Color.lightGray);
                        g2d.drawRect(MEbuttons.get(index).getX(), MEbuttons.get(index).getY(), MEbuttons.get(index).getWidth(), MEbuttons.get(index).getHeight());
                    }
                }
                break;

            case ENDING:
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                map.draw(g2d, false, this);
                player.draw(g2d, this);
                player.drawBullets(g2d, map.getTextureSize(), this);

                for (Player otherPlayer : otherPlayersList) {
                    otherPlayer.draw(g2d, this);
                }
                for (int i=0; i<otherPlayersBullets.size(); i++){
                    otherPlayersBullets.get(i).draw(g2d, textureSize, this);
                }
                if (player.isTeamkilled(otherPlayersList, true)) {
                    g2d.drawImage(victoryScreen, getGameX()+(int)((panelWidth-victoryScreen.getWidth())/2*getZoomRatio()), 0, (int)(victoryScreen.getWidth()*getZoomRatio()), (int)(victoryScreen.getHeight()*getZoomRatio()), null);
                } else {
                    g2d.drawImage(defeatScreen, getGameX()+(int)((panelWidth - defeatScreen.getWidth())/2*getZoomRatio()), 0, (int)(defeatScreen.getWidth()*getZoomRatio()), (int)(defeatScreen.getHeight()*getZoomRatio()), null);
                }
                break;

            case GAME_MODE:
                g2d.drawImage(GameModeBackground, (getWidth()-panelWidth)/2, 0, panelWidth, panelHeight, this);
                Rectangle bounds = GMbuttons.get(gameMode.getId()).getBounds();
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(5));
                g2d.drawRect(bounds.x-3, bounds.y-3, bounds.width+5, bounds.height+5);
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
    
    public int getGameX(){
        return (getWidth()-panelWidth)/2;
    }
    
    public int getGameWidth(){
        return panelWidth;
    }
    
    public int getGameHeight(){
        return panelHeight;
    }
    
    public double getZoomRatio(){
        return (double)panelWidth/originalWidth;
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
        map.setDrawingParameters(gameState, this);
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
                        gameMusicPlayer.stop();
                        menuMusicPlayer.play();
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
                    gameMusicPlayer.play();
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
            clicSoundPlayer.play();
        }
    }
    
    public void updatePlayerAnimation(long dT){
        player.updateAnimation(dT);
    }
}
