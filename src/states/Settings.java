package states;

import game.Game;

import static utilz.Constants.WORLD.GAME_HEIGHT;
import static utilz.Constants.WORLD.GAME_WIDTH;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Settings extends State implements StateMethods {

    public Settings( Game game){
        super(game);
        initClasses();
    }

    private void initClasses(){

    }

    /**
     * @param g
     */
    @Override
    public void draw( Graphics g ) {
            g.setColor(Color.RED);
            g.fillRect(0,0,GAME_WIDTH,GAME_HEIGHT);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Settings", 100, 100);
            
    }

    /**
     *
     */
    @Override
    public void update() {

    }

    /**
     * @param e
     */
    @Override
    public void keyTyped( KeyEvent e ) {

    }

    /**
     * @param e
     */
    @Override
    public void keyReleased( KeyEvent e ) {

    }

    /**
     * @param e
     */
    @Override
    public void keyPressed( KeyEvent e ) {
        switch (e.getKeyCode()){
            case KeyEvent.VK_A ->{
                GameState.setState(GameState.MENU);
            }
        }
    }

    /**
     * @param e
     */
    @Override
    public void mouseMoved( MouseEvent e ) {

    }

    /**
     * @param e
     */
    @Override
    public void mouseClicked( MouseEvent e ) {

    }

    /**
     * @param e
     */
    @Override
    public void mousePressed( MouseEvent e ) {

    }

    /**
     * @param e
     */
    @Override
    public void mouseReleased( MouseEvent e ) {

    }
}
