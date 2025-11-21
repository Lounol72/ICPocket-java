package inputs;

// Java standard library imports
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import game.GamePanel;
import static states.GameState.currentState;

/**
 * Gestionnaire des entrées clavier pour le jeu ICPocket.
 * Cette classe implémente KeyListener et délègue les événements clavier
 * aux différents états du jeu selon l'état actuel.
 * 
 * <p><b>Fonctionnalités :</b></p>
 * <ul>
 *   <li>Gestion des événements keyTyped, keyPressed, keyReleased</li>
 *   <li>Délégation des événements selon l'état du jeu</li>
 *   <li>Support de tous les états : SPLASH, START, MENU, WORLD, SETTINGS, INFOS</li>
 * </ul>
 * 
 * @author Lounol72
 * @version 1.0.0
 * @since 1.0.0
 */
public class KeyboardInputs implements KeyListener {

	/**
	 * Panneau de jeu associé à ce gestionnaire d'entrées.
	 */
	private GamePanel gamePanel;

	/**
	 * Constructeur du gestionnaire d'entrées clavier.
	 * 
	 * @param gamePanel Le panneau de jeu à associer
	 */
	public KeyboardInputs(GamePanel gamePanel) {
		this.gamePanel = gamePanel;
	}

	/**
	 * Gère l'événement de frappe de touche (keyTyped).
	 * Délègue l'événement à l'état de jeu approprié.
	 * 
	 * @param e L'événement de frappe de touche
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		switch(currentState){
			case SPLASH -> {
				gamePanel.getGame().getSplash().keyTyped(e);
			}
			case START -> {
				gamePanel.getGame().getStart().keyTyped(e);
			}
            case MENU -> {
				gamePanel.getGame().getMenu().keyTyped(e);
            }
            case LEVEL_SELECT -> {
				gamePanel.getGame().getLevelSelect().keyTyped(e);
            }
            case WORLD -> {
				gamePanel.getGame().getWorld().keyTyped(e);
            }
            case SETTINGS -> {
				gamePanel.getGame().getSettings().keyTyped(e);
            }

            case INFOS -> {
            }
			default ->{
				throw new IllegalStateException("État de jeu non géré");
            }
        }
	}

	/**
	 * Gère l'événement de relâchement de touche (keyReleased).
	 * Délègue l'événement à l'état de jeu approprié.
	 * 
	 * @param e L'événement de relâchement de touche
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		switch(currentState){
			case SPLASH -> {
				gamePanel.getGame().getSplash().keyReleased(e);
			}
			case START -> {
				gamePanel.getGame().getStart().keyReleased(e);
			}
			case MENU -> {
				gamePanel.getGame().getMenu().keyReleased(e);
			}
			case LEVEL_SELECT -> {
				gamePanel.getGame().getLevelSelect().keyReleased(e);
			}
			case WORLD -> {
				gamePanel.getGame().getWorld().keyReleased(e);
			}
			case SETTINGS -> {
				gamePanel.getGame().getSettings().keyReleased(e);
			}

			case INFOS -> {
			}
			default ->{
				throw new IllegalStateException("État de jeu non géré");
			}
		}
	}

	/**
	 * Gère l'événement d'appui sur une touche (keyPressed).
	 * Délègue l'événement à l'état de jeu approprié.
	 * 
	 * @param e L'événement d'appui sur une touche
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		switch(currentState){
			case SPLASH -> {
				gamePanel.getGame().getSplash().keyPressed(e);
			}
			case START -> {
				gamePanel.getGame().getStart().keyPressed(e);
			}
			case MENU -> {
				gamePanel.getGame().getMenu().keyPressed(e);
			}
			case LEVEL_SELECT -> {
				gamePanel.getGame().getLevelSelect().keyPressed(e);
			}
			case WORLD -> {
				gamePanel.getGame().getWorld().keyPressed(e);
			}
			case SETTINGS -> {
				gamePanel.getGame().getSettings().keyPressed(e);
			}
			case INFOS -> {
			}
			default ->{
				throw new IllegalStateException("État de jeu non géré");
			}
		}
	}
}
