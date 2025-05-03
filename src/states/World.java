package states;

import entities.Player;
import game.Game;
import levels.LevelManager;
import utilz.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import static utilz.Constants.SCALE;
import static utilz.Constants.WORLD.*;
import static utilz.Constants.WORLD.PLAYER.PLAYER_HEIGHT;
import static utilz.Constants.WORLD.PLAYER.PLAYER_WIDTH;
import static utilz.HelpMethods.GetLevelData;

public class World extends State implements StateMethods{

    private Player player;
    private LevelManager levelManager;
    //private PauseOverlay pauseOverlay;
    public boolean paused = false;

    private int xLvlOffset;
    private int leftBorder = (int) (0.2 * GAME_WIDTH);
    private int rightBorder = (int) (0.8 * GAME_WIDTH);
    private int lvlTilesWide = GetLevelData()[0].length;
    private final int maxTilesOffsetX = lvlTilesWide - TILES_IN_WIDTH;
    private final int maxLvlOffsetX = maxTilesOffsetX * TILES_SIZE;
    private int yLvlOffset;
    private int bottomBorder = ( int ) ( GAME_HEIGHT * 0.8f );
    private int topBorder = ( int ) ( GAME_HEIGHT * 0.20f );
    private int lvlTilesHigh = GetLevelData().length;
    private int maxTilesOffsetY = lvlTilesHigh - TILES_IN_HEIGHT;
    private int maxLvlOffsetY = maxTilesOffsetY * TILES_SIZE;

    public World( Game game){
        super(game);
        initClasses();

    }

    private void initClasses() {
        levelManager = new LevelManager(game);
        player = new Player( GAME_WIDTH / 2 ,120 * SCALE, PLAYER_WIDTH, PLAYER_HEIGHT);
        player.loadLvlData(levelManager.getCurrentLevel().getLevelData());
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        levelManager.draw(g, xLvlOffset, yLvlOffset);
        player.render(g, xLvlOffset, yLvlOffset);
//        if (paused)
//            pauseOverlay.draw(g);
    }

    @Override
    public void update() {
        if (!paused) {
            levelManager.update();
            player.update();

            checkCloseToBorder();
        }
    }

    @Override
    public void keyTyped( KeyEvent e ) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_Q:
                this.player.setLeft(false);
                break;
            case KeyEvent.VK_D:
                this.player.setRight(false);
                break;
            case KeyEvent.VK_S:
                this.player.setDown(false);
                break;
            case KeyEvent.VK_SPACE:
                this.player.setJump(false);
                break;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_Q:
                this.player.setLeft(true);
                break;
            case KeyEvent.VK_D:
                this.player.setRight(true);
                break;
            case KeyEvent.VK_S:
                this.player.setDown(true);
                break;
            case KeyEvent.VK_SPACE:
                this.player.setJump(true);
                break;
//            case KeyEvent.VK_ENTER:
//                GameState.state = GameState.MENU;
//                player.resetDirBooleans();
//                break;
//            case KeyEvent.VK_ESCAPE:
//                paused = !paused;
//                player.resetDirBooleans();
//                break;
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

    private void checkCloseToBorder() {
        int playerX = (int) player.getHitbox().x;
        int playerY = (int) player.getHitbox().y;
        int xDiff = playerX - xLvlOffset;
        int yDiff = playerY - yLvlOffset;

        if (xDiff > rightBorder)
            xLvlOffset += xDiff - rightBorder;
        else if (xDiff < leftBorder)
            xLvlOffset += xDiff - leftBorder;

        if (yDiff > bottomBorder)
            yLvlOffset += yDiff - bottomBorder;
        else if (yDiff < topBorder)
            yLvlOffset += yDiff - topBorder;

        if (xLvlOffset > maxLvlOffsetX)
            xLvlOffset = maxLvlOffsetX;
        else if (xLvlOffset < 0)
            xLvlOffset = 0;

        if (yLvlOffset > maxLvlOffsetY)
            yLvlOffset = maxLvlOffsetY;
        else if (yLvlOffset < 0)
            yLvlOffset = 0;

    }
}
