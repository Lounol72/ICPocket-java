/**
 * Classe représentant l'état de combat dans le jeu.
 * Gère les interactions et le rendu spécifiques à cet état.
 */
package states;

import game.Game;
import icmon.ICMon;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import static game.Game.GAME_HEIGHT;
import static game.Game.GAME_WIDTH;

/**
 * Classe Battle qui implémente les méthodes spécifiques à l'état de combat.
 */
public class Battle extends State implements StateMethods {

    private ICMon icMon;

    public Battle( Game game){
        super(game);
        icMon = new ICMon(1);
    }


     /**
     * Dessine les éléments graphiques spécifiques à l'état de combat.
     *
     * @param g Contexte graphique utilisé pour le dessin
     */
    @Override
    public void draw( Graphics g ) {
        g.setColor(new Color(32,24,32));
        g.fillRect(0,0,GAME_WIDTH,GAME_HEIGHT);
        icMon.draw(g);

    }

    /**
     * Met à jour la logique de l'état de combat.
     */
    @Override
    public void update() {
        icMon.update();
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
        GameState.setState(GameState.MENU);
    }

    /**
     * @param e
     */
    @Override
    public void mouseMoved( MouseEvent e ) {
        icMon.setMouseOver(false);
        if (isIn(e,icMon.getRect()))
            icMon.setMouseOver(true);
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
