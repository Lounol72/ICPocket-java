package inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import game.GamePanel;
import static states.GameState.INFOS;
import static states.GameState.MENU;
import static states.GameState.SETTINGS;
import static states.GameState.SPLASH;
import static states.GameState.START;
import static states.GameState.WORLD;
import static states.GameState.currentState;

public class KeyboardInputs implements KeyListener {

	private GamePanel gamePanel;

	public KeyboardInputs(GamePanel gamePanel) {
		this.gamePanel = gamePanel;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
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
