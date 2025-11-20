package states;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import entities.Player;
import game.Game;
import levels.LevelManager;
import static utilz.Constants.SCALE;
import static utilz.Constants.WORLD.GAME_HEIGHT;
import static utilz.Constants.WORLD.GAME_WIDTH;
import static utilz.Constants.WORLD.TILES_IN_HEIGHT;
import static utilz.Constants.WORLD.TILES_IN_WIDTH;
import static utilz.Constants.WORLD.TILES_SIZE;
import static utilz.LoadSave.GetLevelData;

public class World extends State implements StateMethods {

    private boolean paused;

    private final Player player;
    private final LevelManager level;

    private int xLvlOffset;
    private int yLvlOffset;
    private final int LEFT_BORDER = (int) (0.45f * GAME_WIDTH);
    private final int RIGHT_BORDER = (int) (0.55f * GAME_WIDTH);
    private final int TOP_BORDER = (int) (0.4f * GAME_HEIGHT);
    private final int BOTTOM_BORDER = (int) (0.90 * GAME_HEIGHT);
    private int lvlTilesWide;
    private int lvlTilesHigh;
    private int maxTilesOffsetX;
    private int maxLvlOffsetX;
    private int maxTilesOffsetY;
    private int maxLvlOffsetY;

    public World(Game game) {
        super(game);
        loadLevel();
        level = new LevelManager(game);
        player = new Player(5 * TILES_SIZE, 5 * TILES_SIZE, (int) (64 * SCALE), (int) (64 * SCALE),
                level.getCurrentLevel());
        player.loadLvlData(level.getCurrentLevel().getLevelData());

    }

    private void loadLevel(){
        lvlTilesWide = GetLevelData()[0].length;
        lvlTilesHigh = GetLevelData().length;
        maxTilesOffsetX = lvlTilesWide - TILES_IN_WIDTH;
        maxLvlOffsetX = maxTilesOffsetX * TILES_SIZE;
        maxTilesOffsetY = lvlTilesHigh - TILES_IN_HEIGHT;
        maxLvlOffsetY = maxTilesOffsetY * TILES_SIZE;
    }


    private void checkCloseToBorder() {
        int playerX = (int) player.getHitbox().x;
        int playerY = (int) player.getHitbox().y;
        int diffX = playerX - xLvlOffset;
        int diffY = playerY - yLvlOffset;

        if (diffX > RIGHT_BORDER)
            xLvlOffset += diffX - RIGHT_BORDER;
        else if (diffX < LEFT_BORDER)
            xLvlOffset += diffX - LEFT_BORDER;

        if (xLvlOffset > maxLvlOffsetX)
            xLvlOffset = maxLvlOffsetX;
        else if (xLvlOffset < 0)
            xLvlOffset = 0;

        if (diffY > BOTTOM_BORDER)
            yLvlOffset += diffY - BOTTOM_BORDER;
        else if (diffY < TOP_BORDER)
            yLvlOffset += diffY - TOP_BORDER;

        if (yLvlOffset > maxLvlOffsetY)
            yLvlOffset = maxLvlOffsetY;
        else if (yLvlOffset < 0)
            yLvlOffset = 0;
    }

    @Override
    public void draw(Graphics g) {
        level.draw(g, xLvlOffset, yLvlOffset);
        player.render(g, xLvlOffset, yLvlOffset);

    }

    @Override
    public void update() {
        if (!paused) {
            checkCloseToBorder();
            player.update();
            level.update();
        }

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_Q -> {
                player.setLeft(false);
            }
            case KeyEvent.VK_D -> {
                player.setRight(false);
            }
            case KeyEvent.VK_S -> {
                player.setDown(false);
            }
            case KeyEvent.VK_SPACE -> {
                player.setJump(false);
            }
            case KeyEvent.VK_E -> {
                player.setAttack(false);
            }
            // default -> System.out.println("Unexpected value: " + e.getKeyCode());
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_Q -> {
                player.setLeft(true);
            }
            case KeyEvent.VK_D -> {
                player.setRight(true);
            }
            case KeyEvent.VK_RIGHT -> {
                // Touche flèche droite déclenche un dash

                player.startDash();
            }
            case KeyEvent.VK_S -> {
                player.setDown(true);
            }
            case KeyEvent.VK_SPACE -> {
                player.setJump(true);
            }
            case KeyEvent.VK_A -> {
                GameState.setState(GameState.MENU);
            }
            case KeyEvent.VK_E -> {
                player.setAttack(true);
            }
            // default -> System.out.println("Unexpected value: " + e.getKeyCode());
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void UpdateStrings() {

    }
}