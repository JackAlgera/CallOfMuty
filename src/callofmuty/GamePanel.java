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
import java.awt.event.MouseMotionListener;
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
            PreGameBackground = Tools.loadImage("PreGameBackground.png");

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
            startGameIcon = Tools.loadIcon("StartGame.png");
            
    
    
    public static final int IFW = JPanel.WHEN_IN_FOCUSED_WINDOW,
            MAIN_MENU = 0, IN_GAME = 1, MAP_EDITOR = 2, PRE_GAME = 3,
            RANDOMLY_GIVEN_GUNS = 0;
    
    private static long gunGenerationTime = 1000; //in milliseconds
    
    private SoundPlayer menuMusicPlayer, gameMusicPlayer, clicSoundPlayer;
    
    private Map map;
    private TileSelector tileSelector;
    private Player player;
    private ArrayList <Player> otherPlayersList;
    private int textureSize, mapWidth, mapHeight, panelWidth, panelHeight, gameState, gameMode;
    private ArrayList<Integer> pressedButtons, releasedButtons;
    private boolean isHost, setStartingTile, mousehold;
    private long lastGunGeneration;
    private SQLManager sql; 
    private boolean isConnected, muteMusic, muteSounds;
    private ArrayList <JComponent> MMbuttons, MEbuttons, PGbuttons;
    private ArrayList<Bullet> otherPlayersBullets;
    private GameTimer timer;
    
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
        gameMode = RANDOMLY_GIVEN_GUNS;
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
        mousehold = false;
        
        addMouseMotionListener(new MouseAdapter(){
            @Override
            public void mouseDragged(MouseEvent e){
                switch (gameState) {
                case IN_GAME:
                    mousehold = false;
                    mousehold = true;
                    initshootThread(e);                    
                    break;
                case MAP_EDITOR:
                    
                    break;
                default:
                }
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
                switch (gameState) {
                case IN_GAME:
                    mousehold = true;
                    initshootThread(e);
                    break;
                case MAP_EDITOR:
                    int[] mapClicked = map.clickedTile(e.getX(), e.getY());
                    if (mapClicked[0]>-1){ // map was clicked
                        playClicSound();
                        if(!setStartingTile){
                            map.setTile(mapClicked[1], mapClicked[2], tileSelector.getSelectedTile());
                        } else {
                            map.addStartTile(new int[]{mapClicked[1], mapClicked[2]});
                        }
                    } else { // check if tileSelector was clicked and select the tile if so
                        if(tileSelector.clickedTile(e.getX(), e.getY())[0]>-1){
                            playClicSound();
                            setStartingTile = false;
                        }
                    }
                    repaint();
                    break;
                default:
                }
            }@Override
            public void mouseReleased(MouseEvent e) {
                mousehold = false;
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
        connectButton.setBorderPainted(true);
        add(connectButton);
        MMbuttons.add(connectButton);
        
        JButton gameCreateButton = new JButton();
        gameCreateButton.setIcon(createGameIcon);
        gameCreateButton.setBounds(287, 227, createGameIcon.getIconWidth(), createGameIcon.getIconHeight());
        //gameCreateButton.setPressedIcon(pressedcreateGameIcon);
        gameCreateButton.setVisible(true);
        gameCreateButton.setContentAreaFilled(false);
        gameCreateButton.setBorderPainted(true);
        add(gameCreateButton);
        MMbuttons.add(gameCreateButton);
        
        JButton exitButton = new JButton();
        exitButton.setIcon(exitIcon);
        exitButton.setBounds(287, 373, exitIcon.getIconWidth(), exitIcon.getIconHeight());
        //exitButton.setPressedIcon(pressedExitIcon);
        exitButton.setVisible(true);
        exitButton.setContentAreaFilled(false);
        exitButton.setBorderPainted(true);
        add(exitButton);
        MMbuttons.add(exitButton);
        
        JButton gameModeButton = new JButton();
        gameModeButton.setIcon(gameModeIcon);
        gameModeButton.setBounds(287, 154, gameModeIcon.getIconWidth(), gameModeIcon.getIconHeight());
        //gameModeButton.setPressedIcon(pressedGameModeIcon);
        gameModeButton.setVisible(true);
        gameModeButton.setContentAreaFilled(false);
        gameModeButton.setBorderPainted(true);
        add(gameModeButton);
        MMbuttons.add(gameModeButton);
        
        JButton rightSkinArrow = new JButton();
        rightSkinArrow.setIcon(rightArrowIcon);
        rightSkinArrow.setBounds(182, 440, rightArrowIcon.getIconWidth(), rightArrowIcon.getIconHeight());
        rightSkinArrow.setPressedIcon(pressedrightArrowIcon);
        rightSkinArrow.setVisible(true);
        rightSkinArrow.setContentAreaFilled(false);
        rightSkinArrow.setBorderPainted(false);
        add(rightSkinArrow);
        MMbuttons.add(rightSkinArrow);
        
        JButton leftSkinArrow = new JButton();
        leftSkinArrow.setIcon(leftArrowIcon);
        leftSkinArrow.setBounds(54, 440, leftArrowIcon.getIconWidth(), leftArrowIcon.getIconHeight());
        leftSkinArrow.setPressedIcon(pressedleftArrowIcon);
        leftSkinArrow.setVisible(true);
        leftSkinArrow.setContentAreaFilled(false);
        leftSkinArrow.setBorderPainted(false);
        add(leftSkinArrow);
        MMbuttons.add(leftSkinArrow);
        
        JButton MEtopSkinArrow = new JButton();
        MEtopSkinArrow.setIcon(topArrowIcon);
        MEtopSkinArrow.setBounds(29, 16, topArrowIcon.getIconWidth(), topArrowIcon.getIconHeight());
        MEtopSkinArrow.setPressedIcon(pressedtopArrowIcon);
        MEtopSkinArrow.setVisible(true);
        MEtopSkinArrow.setContentAreaFilled(false);
        MEtopSkinArrow.setBorderPainted(false);
        add(MEtopSkinArrow);
        MEbuttons.add(MEtopSkinArrow);
        
        JButton MEBottomSkinArrow = new JButton();
        MEBottomSkinArrow.setIcon(bottomArrowIcon);
        MEBottomSkinArrow.setBounds(29, 78, bottomArrowIcon.getIconWidth(), bottomArrowIcon.getIconHeight());
        MEBottomSkinArrow.setPressedIcon(pressedbottomArrowIcon);
        MEBottomSkinArrow.setVisible(true);
        MEBottomSkinArrow.setContentAreaFilled(false);
        MEBottomSkinArrow.setBorderPainted(false);
        add(MEBottomSkinArrow);
        MEbuttons.add(MEBottomSkinArrow);        
        
        JButton rightMapArrow = new JButton();
        rightMapArrow.setIcon(rightArrowIcon);
        rightMapArrow.setBounds(824, 440, rightArrowIcon.getIconWidth(), rightArrowIcon.getIconHeight());
        rightMapArrow.setPressedIcon(pressedrightArrowIcon);
        rightMapArrow.setVisible(true);
        rightMapArrow.setContentAreaFilled(false);
        rightMapArrow.setBorderPainted(false);
        add(rightMapArrow);
        MMbuttons.add(rightMapArrow);
        
        JButton leftMapArrow = new JButton();
        leftMapArrow.setIcon(leftArrowIcon);
        leftMapArrow.setBounds(637, 440, leftArrowIcon.getIconWidth(), leftArrowIcon.getIconHeight());
        leftMapArrow.setPressedIcon(pressedleftArrowIcon);
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
        mapEditorButton.setBorderPainted(true);
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
        
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                quitGame();
            }
        });
        
        gameModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                // to do
            }
        });
        
        rightSkinArrow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                int skinIndex = player.getSkinIndex();
                skinIndex = (skinIndex%5)+1;
                getPlayer().setSkin(skinIndex);
                repaint();
            }
        });
        
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
                playClicSound();
                setState(MAP_EDITOR);
            }
        });
        
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
                
        JToggleButton muteSoundsButton = new JToggleButton();
        muteSoundsButton.setIcon(SoundsIcon);
        muteSoundsButton.setBounds(840, 20, SoundsIcon.getIconWidth(), SoundsIcon.getIconHeight());
        muteSoundsButton.setVisible(true);
        muteSoundsButton.setBorderPainted(false);
        add(muteSoundsButton);
        MMbuttons.add(muteSoundsButton);
        
        JToggleButton  muteMusicButton = new JToggleButton();
        muteMusicButton.setIcon(MusicIcon);
        muteMusicButton.setBounds(900, 20, MusicIcon.getIconWidth(), MusicIcon.getIconHeight());
        muteMusicButton.setVisible(true);
        muteMusicButton.setBorderPainted(false);
        add(muteMusicButton);
        MMbuttons.add(muteMusicButton);
        
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
        
        // Map Editor interface
        JButton saveMapButton = new JButton();
        saveMapButton.setIcon(saveMapIcon);
        saveMapButton.setBounds(7, 212, saveMapIcon.getIconWidth(), saveMapIcon.getIconHeight());
        saveMapButton.setVisible(false);
        saveMapButton.setBorderPainted(true);
        add(saveMapButton);
        MEbuttons.add(saveMapButton);
        
        JButton loadMapButton = new JButton();
        loadMapButton.setIcon(loadMapIcon);
        loadMapButton.setBounds(7, 163, loadMapIcon.getIconWidth(), loadMapIcon.getIconHeight());
        loadMapButton.setVisible(false);
        loadMapButton.setBorderPainted(true);
        add(loadMapButton);
        MEbuttons.add(loadMapButton);
        
        JButton doneButton = new JButton();
        doneButton.setIcon(doneIcon);
        doneButton.setBounds(7, 279, doneIcon.getIconWidth(), doneIcon.getIconHeight());
        doneButton.setVisible(false);
        doneButton.setBorderPainted(true);
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
        
        doneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                setState(MAIN_MENU);
            }
        });
        
        setStartingTileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playClicSound();
                setStartingTile = true;
            }
        });
        
        //Pre game interface
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
    }
    
    public void playershoot(MouseEvent e){
        double[] directionOfFire = new double[2];
                    directionOfFire[0] = e.getX() - player.getPosX() - textureSize / 2;
                    directionOfFire[1] = e.getY() - player.getPosY() - textureSize / 2;

                    double norme = Math.sqrt(directionOfFire[0] * directionOfFire[0] + directionOfFire[1] * directionOfFire[1]);
                    directionOfFire[0] = directionOfFire[0] / norme;
                    directionOfFire[1] = directionOfFire[1] / norme;

                {
                    try {
                        player.shoot(directionOfFire, sql, false);
                    } catch (JavaLayerException ex) {
                        Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
    }
    
    public void initshootThread(MouseEvent e){
        if (mousehold) {
        new Thread() {
            public void run() {
                do {
                    playershoot(e);
                } while (mousehold);
            }
        }.start();
    }
                
    }
    
    public void updateGame(long dT) throws JavaLayerException, IOException{
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
        // gun generation
        if(System.currentTimeMillis()-gunGenerationTime > lastGunGeneration){
            lastGunGeneration = System.currentTimeMillis();
            player.generateGun(otherPlayersList.size()+1); // has a probability to give local player a gun that decreases with number of players
        }
        
        // sql uploads
        sql.uploadPlayerAndBullets(player);
        if(printTime){
            System.out.println("Uploads : " + (System.currentTimeMillis()-time));
        }
        if(otherPlayersList.isEmpty()){ // Game is ended
            if (!player.isDead()){ // Local player won
                //endGame();
            } else {
                //endGame();
            }
        }
    }
    
    private void updatePlayerMovement(){
        if (pressedButtons.contains(KeyEvent.VK_S)){
//            player.setFacedDirection(0);
            player.setAcceleration(1, 1);
            player.setDirectionOfTravel(1, 1);
        }
        if (pressedButtons.contains(KeyEvent.VK_Z)){
//            player.setFacedDirection(3);
            player.setAcceleration(1, -1);
            player.setDirectionOfTravel(1, -1);
        }
        if (pressedButtons.contains(KeyEvent.VK_Q)){
//            player.setFacedDirection(1);
            player.setAcceleration(0, -1);
            player.setDirectionOfTravel(0, -1);
        }
        if (pressedButtons.contains(KeyEvent.VK_D)){
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
        this.getInputMap().put(KeyStroke.getKeyStroke("S"), "sPressed");
        this.getInputMap().put(KeyStroke.getKeyStroke("released S"), "sReleased");
        this.getInputMap().put(KeyStroke.getKeyStroke("Z"), "zPressed");
        this.getInputMap().put(KeyStroke.getKeyStroke("released Z"), "zReleased");
        this.getInputMap().put(KeyStroke.getKeyStroke("Q"), "qPressed");
        this.getInputMap().put(KeyStroke.getKeyStroke("released Q"), "qReleased");
        this.getInputMap().put(KeyStroke.getKeyStroke("D"), "dPressed");
        this.getInputMap().put(KeyStroke.getKeyStroke("released D"), "dReleased");
        this.getActionMap().put("zPressed", new KeyPressed(KeyEvent.VK_Z));
        this.getActionMap().put("zReleased", new KeyReleased(KeyEvent.VK_Z) );
        this.getActionMap().put("sPressed", new KeyPressed(KeyEvent.VK_S));
        this.getActionMap().put("sReleased", new KeyReleased(KeyEvent.VK_S) );
        this.getActionMap().put("qPressed", new KeyPressed(KeyEvent.VK_Q));
        this.getActionMap().put("qReleased", new KeyReleased(KeyEvent.VK_Q) );
        this.getActionMap().put("dPressed", new KeyPressed(KeyEvent.VK_D));
        this.getActionMap().put("dReleased", new KeyReleased(KeyEvent.VK_D) );
        
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
            g2d.drawImage(PreGameBackground, 0, 0, 16*64, 9*64, this);
            map.draw(g2d, false);
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
                otherPlayersBullets.get(i).draw(g2d, textureSize);// To do : Only 1 SQL line to modifie every bullet's position
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
    
    public void initialiseGame(boolean isHost) throws IOException, JavaLayerException {
        this.isHost = isHost;
        sql = new SQLManager();
        int currentGameState = sql.getGameState();
        if (isHost) {
            // Try to create a game
            ArrayList<Player> playerList = sql.getPlayerList();
            if (playerList.isEmpty()) { // No game is currently on
                sql.clearTable(); //Clear previous game on SQL server
                sql.createGame(map);
                player.setGunId(Gun.NO_GUN);
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
                player.setGunId(Gun.NO_GUN);
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
            if(!player.isDead()){
                sql.removePlayer(player);
            }
            try {
                if(sql.getPlayerList().isEmpty() || (isHost && formerGameState==PRE_GAME) ){
                    sql.clearTable();
                }
            } catch (IOException ex) {
                Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JavaLayerException ex) {
                Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            sql.disconnect();
        }
        isConnected = false;
    }
    
    public void preGameUpdate() {
        try {
            sql.updatePlayerList(player, otherPlayersList);
        } catch (IOException | JavaLayerException ex) {
            Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
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
                if (formerGameState==IN_GAME && !muteMusic) {
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
                if (!muteMusic) {
                    menuMusicPlayer.stop();
                    try {
                        gameMusicPlayer.play();
                    } catch (JavaLayerException | IOException | URISyntaxException ex) {
                        Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                timer.update();
        }
        repaint();
    }
    
    public void playClicSound(){
        if (!muteSounds) {
            try {
                clicSoundPlayer.play();
            } catch (JavaLayerException ex) {
                Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (URISyntaxException ex) {
                Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
