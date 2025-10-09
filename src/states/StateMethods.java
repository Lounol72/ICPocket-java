package states;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public interface StateMethods {

    public void draw( Graphics g);
    public void update();
    
    // Keyboard inputs
    public void keyTyped( KeyEvent e);
    public void keyReleased(KeyEvent e);
    public void keyPressed(KeyEvent e);
    
    // Mouse Inputs

    public void mouseMoved(MouseEvent e) ;
    public void mouseClicked(MouseEvent e) ;
    public void mousePressed(MouseEvent e) ;
    public void mouseReleased(MouseEvent e) ;
    
    // update strings
    public void UpdateStrings();
}
