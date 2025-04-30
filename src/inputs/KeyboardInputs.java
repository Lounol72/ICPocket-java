package inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import game.GamePanel;

import static states.GameState.*;

public class KeyboardInputs implements KeyListener {

	private GamePanel gamePanel;

	public KeyboardInputs(GamePanel gamePanel) {
		this.gamePanel = gamePanel;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		switch(currentState){
            case MENU -> {
				gamePanel.getGame().getMenu().keyTyped(e);
            }
            case BATTLE -> {
				gamePanel.getGame().getBattle().keyTyped(e);
            }
            case WORLD -> {
				gamePanel.getGame().getWorld().keyTyped(e);
            }
            case SETTINGS -> {
            }
            case TEAM -> {
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
			case MENU -> {
				gamePanel.getGame().getMenu().keyReleased(e);
			}
			case BATTLE -> {
				gamePanel.getGame().getBattle().keyReleased(e);
			}
			case WORLD -> {
				gamePanel.getGame().getWorld().keyReleased(e);
			}
			case SETTINGS -> {
			}
			case TEAM -> {
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
			case MENU -> {
				gamePanel.getGame().getMenu().keyPressed(e);
			}
			case BATTLE -> {
				gamePanel.getGame().getBattle().keyPressed(e);
			}
			case WORLD -> {
				gamePanel.getGame().getWorld().keyPressed(e);
			}
			case SETTINGS -> {
			}
			case TEAM -> {
			}
			case INFOS -> {
			}
			default ->{
				throw new IllegalStateException("État de jeu non géré");
			}
		}
	}
}
