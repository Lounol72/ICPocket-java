package states;

import game.Game;
import ui.Button;

import java.awt.*;
import java.awt.event.MouseEvent;

public abstract class State {
    protected Game game;

    public State(Game game) {
        this.game = game;
    }

    public State() {
    }

    public boolean isIn( MouseEvent e, Button mb){
        return mb.getBounds().contains(e.getX(), e.getY());
    }
    public boolean isIn( MouseEvent e, Rectangle rect){
        return rect.contains(e.getX(), e.getY());
    }

    public Game getGame() {return game;}
}

