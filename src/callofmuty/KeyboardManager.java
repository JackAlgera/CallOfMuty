package callofmuty;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class KeyboardManager {

    public static final int PLAYING = 0, CHANGING_MOVE_UP = 1, CHANGING_MOVE_DOWN = 2, CHANGING_MOVE_LEFT = 3, CHANGING_MOVE_RIGHT = 4, CHANGING_TAUNT = 5, CHANGING_DASH = 6;
    
    private int[] moveUp, moveDown, moveRight, moveLeft;
    private int dash, taunt, quit, state;
    private GamePanel game;
    
    private ArrayList<Integer> pressedKeys, releasedKeys;
    
    public KeyboardManager(GamePanel game){
        this.game = game;
        state = PLAYING;
        moveUp = new int[]{KeyEvent.VK_Z, KeyEvent.VK_UP};
        moveDown = new int[]{KeyEvent.VK_S, KeyEvent.VK_DOWN};
        moveLeft = new int[]{KeyEvent.VK_Q, KeyEvent.VK_LEFT};
        moveRight = new int[]{KeyEvent.VK_D, KeyEvent.VK_RIGHT};
        dash = KeyEvent.VK_SPACE;
        taunt = KeyEvent.VK_F;
        quit = KeyEvent.VK_ESCAPE;
        pressedKeys = new ArrayList<>();
        releasedKeys = new ArrayList<>();
    }
    
    public boolean movingUp(){
        boolean test =  false;
        for(int i=0; i<moveUp.length; i++){
            test |= pressedKeys.contains(moveUp[i]);
        }
        return test;
    }
    
    public boolean stoppedMovingUp(){
        boolean test =  false;
        for(int i=0; i<moveUp.length; i++){
            if (releasedKeys.contains(moveUp[i])){
                test = true;
                releasedKeys.remove((Integer)moveUp[i]);
            }
        }
        return test;
    }
    
    public boolean movingDown(){
        boolean test =  false;
        for(int i=0; i<moveDown.length; i++){
            test |= pressedKeys.contains(moveDown[i]);
        }
        return test;
    }
    
    public boolean stoppedMovingDown(){
        boolean test =  false;
        for(int i=0; i<moveDown.length; i++){
            if (releasedKeys.contains(moveDown[i])){
                test = true;
                releasedKeys.remove((Integer)moveDown[i]);
            }
        }
        return test;
    }
    
    public boolean movingLeft(){
        boolean test =  false;
        for(int i=0; i<moveUp.length; i++){
            test |= pressedKeys.contains(moveLeft[i]);
        }
        return test;
    }
    
    public boolean stoppedMovingLeft(){
        boolean test =  false;
        for(int i=0; i<moveUp.length; i++){
            if (releasedKeys.contains(moveLeft[i])){
                test = true;
                releasedKeys.remove((Integer)moveLeft[i]);
            }
        }
        return test;
    }
    
    public boolean movingRight(){
        boolean test =  false;
        for(int i=0; i<moveUp.length; i++){
            test |= pressedKeys.contains(moveRight[i]);
        }
        return test;
    }
    
    public boolean stoppedMovingRight(){
        boolean test =  false;
        for(int i=0; i<moveUp.length; i++){
            if (releasedKeys.contains(moveRight[i])){
                test = true;
                releasedKeys.remove((Integer)moveRight[i]);
            }
        }
        return test;
    }
    
    private boolean needToDetectRelease(int keyCode){
        boolean test = false;
        ArrayList<int[]> heldKeys = new ArrayList<>();
        heldKeys.add(moveUp);heldKeys.add(moveDown);heldKeys.add(moveLeft);heldKeys.add(moveRight);
        for (int[] array : heldKeys){
            for(int i = 0; i<array.length; i++){
                test |= keyCode==array[i];
            }
        }
        return test;
    }
    
    private boolean keyIsKnown(int keyCode){
        return (keyCode==dash || keyCode==taunt || keyCode==quit || needToDetectRelease(keyCode));
    }
    
    public void setState(int state){
        this.state = state;
    }
    
    public String getText(int index){
        String text = "";
        switch(index){
            case CHANGING_MOVE_UP:
                text = KeyEvent.getKeyText(moveUp[0]);
                break;
            case CHANGING_MOVE_DOWN:
                text = KeyEvent.getKeyText(moveDown[0]);
                break;
            case CHANGING_MOVE_LEFT:
                text = KeyEvent.getKeyText(moveLeft[0]);
                break;
            case CHANGING_MOVE_RIGHT:
                text = KeyEvent.getKeyText(moveRight[0]);
                break;
            case CHANGING_TAUNT:
                text = KeyEvent.getKeyText(taunt);
                break;
            case CHANGING_DASH:
                text = KeyEvent.getKeyText(dash);
                break;
        }
        return text;
    }
    
    public int getState(){
        return state;
    }
    
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (state) {
            case PLAYING:
                if (keyIsKnown(keyCode) && !pressedKeys.contains(keyCode)) {
                    pressedKeys.add(keyCode);
                    if (keyCode == dash) {
                        game.dash();
                    } else if (keyCode == taunt) {
                        game.taunt();
                    } else if (keyCode == quit) {
                        game.quit();
                    }
                }
                break;
            case CHANGING_MOVE_UP:
                if(keyCode!=KeyEvent.VK_ESCAPE){
                    setUpKey(keyCode);
                }
                state = PLAYING;
                break;
            case CHANGING_MOVE_DOWN:
                if(keyCode!=KeyEvent.VK_ESCAPE){
                    setDownKey(keyCode);
                }
                state = PLAYING;
                break;
            case CHANGING_MOVE_LEFT:
                if(keyCode!=KeyEvent.VK_ESCAPE){
                    setLeftKey(keyCode);
                }
                state = PLAYING;
                break;
            case CHANGING_MOVE_RIGHT:
                if(keyCode!=KeyEvent.VK_ESCAPE){
                    setRightKey(keyCode);
                }
                state = PLAYING;
                break;
            case CHANGING_TAUNT:
                if(keyCode!=KeyEvent.VK_ESCAPE){
                    setTauntKey(keyCode);
                }
                state = PLAYING;
                break;
            case CHANGING_DASH:
                if(keyCode!=KeyEvent.VK_ESCAPE){
                    setDashKey(keyCode);
                }
                state = PLAYING;
                break;
        }
    }
    
    public void keyReleased(KeyEvent e){
        int keyCode = e.getKeyCode();
        if(needToDetectRelease(keyCode) && !releasedKeys.contains(keyCode)){
            releasedKeys.add(keyCode);
        }
        if(pressedKeys.contains(keyCode)){
            pressedKeys.remove((Integer)keyCode);
        }
    }
    
    public void reset(){
        pressedKeys = new ArrayList<>();
        releasedKeys = new ArrayList<>();
    }
    
    private void setUpKey(int keyCode){
        if(!keyIsKnown(keyCode)){
            moveUp[0] = keyCode;
        } else {
            JOptionPane.showMessageDialog(null, "This key is already used");
        }
    }
    
    private void setDownKey(int keyCode){
        if(!keyIsKnown(keyCode)){
            moveDown[0] = keyCode;
        } else {
            JOptionPane.showMessageDialog(null, "This key is already used");
        }
    }
    
    private void setLeftKey(int keyCode){
        if(!keyIsKnown(keyCode)){
            moveLeft[0] = keyCode;
        } else {
            JOptionPane.showMessageDialog(null, "This key is already used");
        }
    }
    
    private void setRightKey(int keyCode){
        if(!keyIsKnown(keyCode)){
            moveRight[0] = keyCode;
        } else {
            JOptionPane.showMessageDialog(null, "This key is already used");
        }
    }
    
    private void setTauntKey(int keyCode){
        if(!keyIsKnown(keyCode)){
            taunt = keyCode;
        } else {
            JOptionPane.showMessageDialog(null, "This key is already used");
        }
    }
    
    private void setDashKey(int keyCode){
        if(!keyIsKnown(keyCode)){
            dash = keyCode;
        } else {
            JOptionPane.showMessageDialog(null, "This key is already used");
        }
    }
    
}
