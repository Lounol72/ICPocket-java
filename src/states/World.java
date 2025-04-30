package states;

import entities.Player;
import game.Game;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import static utilz.Constants.SCALE;

public class World extends State implements StateMethods{

    private boolean paused;

    private Player player;

    public World( Game game){
        super(game);
        player = new Player( 20 ,20, (int) (64 * SCALE), (int) (64 * SCALE));
    }

    @Override
    public void draw( Graphics g ) {
        player.render(g,0,0);
    }

    @Override
    public void update() {
        if (!paused)
            player.update();
    }

    @Override
    public void keyTyped( KeyEvent e ) {

    }

    @Override
    public void keyReleased( KeyEvent e ) {
        switch(e.getKeyCode()){
            case KeyEvent.VK_Q ->{
                player.setLeft(false);
            }
            case KeyEvent.VK_D ->{
                player.setRight(false);
            }
            default -> System.out.println("Unexpected value: " + e.getKeyCode());
        }
    }

    @Override
    public void keyPressed( KeyEvent e ) {
        switch(e.getKeyCode()){
            case KeyEvent.VK_Q ->{
                player.setLeft(true);
            }
            case KeyEvent.VK_D ->{
                player.setRight(true);
            }
            default -> System.out.println("Unexpected value: " + e.getKeyCode());
        }
    }

    @Override
    public void mouseMoved( MouseEvent e ) {

    }

    @Override
    public void mouseClicked( MouseEvent e ) {

    }

    @Override
    public void mousePressed( MouseEvent e ) {

    }

    @Override
    public void mouseReleased( MouseEvent e ) {

    }
}
