package callofmuty;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;

public class KeyManager extends AbstractAction{

    public static void addKey(int e, ArrayList buttonsPressed){
        if(!buttonsPressed.contains(e)){
            buttonsPressed.add(e);
        }
    }
    
    public static void removeKey(int e, ArrayList buttonsPressed){
        if(buttonsPressed.contains(e)){
            buttonsPressed.remove((Integer)e);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
