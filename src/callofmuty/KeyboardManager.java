package callofmuty;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class KeyboardManager {

    private int[] moveUp, moveDown, moveRight, moveLeft;
    private int roll, taunt, quit;
    private GamePanel game;
    
    private ArrayList<Integer> pressedKeys, releasedKeys;
    
    public KeyboardManager(GamePanel game){
        this.game = game;
        moveUp = new int[]{KeyEvent.VK_Z, KeyEvent.VK_UP};
        moveDown = new int[]{KeyEvent.VK_S, KeyEvent.VK_DOWN};
        moveLeft = new int[]{KeyEvent.VK_Q, KeyEvent.VK_LEFT};
        moveRight = new int[]{KeyEvent.VK_D, KeyEvent.VK_RIGHT};
        roll = KeyEvent.VK_SPACE;
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
        return (keyCode==roll || keyCode==taunt || keyCode==quit || needToDetectRelease(keyCode));
    }
    
    public void keyPressed(KeyEvent e){
        int keyCode = e.getKeyCode();
        if(keyIsKnown(keyCode) && !pressedKeys.contains(keyCode)){
            pressedKeys.add(keyCode);
            if(keyCode==roll){
                game.roll();
            } else if(keyCode==taunt){
                game.taunt();
            } else if(keyCode==quit){
                game.quit();
            }
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
    
}
