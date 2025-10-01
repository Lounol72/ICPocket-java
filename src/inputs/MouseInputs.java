package inputs;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.function.BiConsumer;

import game.GamePanel;
import states.StateMethods;

import static states.GameState.currentState;

public class MouseInputs implements MouseListener, MouseMotionListener {

    private GamePanel gamePanel;

    public MouseInputs(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    /**
     * Méthode générique pour gérer tous les événements souris
     */
    private void handleMouseEvent(MouseEvent e, BiConsumer<StateMethods, MouseEvent> action) {
        StateMethods state = null;

        switch(currentState) {
            case START -> state = gamePanel.getGame().getStart();
            case MENU -> state = gamePanel.getGame().getMenu();
            case BATTLE -> state = gamePanel.getGame().getBattle();
            case WORLD -> state = gamePanel.getGame().getWorld();
            case SETTINGS -> state = gamePanel.getGame().getSettings();
            case TEAM -> { /* à implémenter */ }
            case INFOS -> { /* à implémenter */ }
            default -> throw new IllegalStateException("État de jeu non géré");
        }

        if (state != null) {
            action.accept(state, e);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // implémentation future si nécessaire
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        handleMouseEvent(e, (state, event) -> state.mouseMoved(event));
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        handleMouseEvent(e, (state, event) -> state.mouseClicked(event));
    }

    @Override
    public void mousePressed(MouseEvent e) {
        handleMouseEvent(e, (state, event) -> state.mousePressed(event));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        handleMouseEvent(e, (state, event) -> state.mouseReleased(event));
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // implémentation future si nécessaire
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // implémentation future si nécessaire
    }
}