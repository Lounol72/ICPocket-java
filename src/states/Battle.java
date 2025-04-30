/**
 * Classe représentant l'état de combat dans le jeu.
 * Gère les interactions et le rendu spécifiques à cet état.
 */
package states;

import duel.Team;
import game.Game;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Random;

import static utilz.Constants.WORLD.GAME_HEIGHT;
import static utilz.Constants.WORLD.GAME_WIDTH;

/**
 * Classe Battle qui implémente les méthodes spécifiques à l'état de combat.
 */
public class Battle extends State implements StateMethods {

    private Team team;
    private boolean paused=false;
    Random rnd = new Random();

    public Battle( Game game){
        super(game);
        team = new Team(3);
        System.out.println("level :" + team.getTeam()[0].getLvl());
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
        team.draw(g);
        if(paused){
            g.setColor(new Color(255,255,255,128));
            g.fillRect(0,0,GAME_WIDTH,GAME_HEIGHT);
        }
    }

    /**
     * Met à jour la logique de l'état de combat.
     */
    @Override
    public void update() {
        if (!paused)
            team.update();
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
            case KeyEvent.VK_Z ->{
                GameState.setState(GameState.MENU);
            }
            case  KeyEvent.VK_S ->{
                GameState.setState(GameState.WORLD);
            }
            case KeyEvent.VK_ESCAPE->{
                paused = !paused;
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
        switch(e.getButton()) {
            case MouseEvent.BUTTON1 -> {
                if (!paused) {
                    team.gainExp(team.getTeam()[0]);
                }
            }
            case MouseEvent.BUTTON2 -> {
                // TODO: Implémenter la logique pour le bouton molette ou supprimer ce case
            }
            case MouseEvent.BUTTON3 -> {
                if (!paused) {
                    team.swapActualAttacker(1);
                    // TODO System.out.println("Switching");
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + e.getButton());
        }
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