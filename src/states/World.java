package states;

import static utilz.Constants.SCALE;
import static utilz.Constants.WORLD.GAME_HEIGHT;
import static utilz.Constants.WORLD.GAME_WIDTH;
import static utilz.Constants.WORLD.TILES_IN_HEIGHT;
import static utilz.Constants.WORLD.TILES_IN_WIDTH;
import static utilz.Constants.WORLD.TILES_SIZE;
import static utilz.LoadSave.GetLevelData;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import entities.Player;
import game.Game;
import levels.LevelManager;

public class World extends State implements StateMethods{

    private boolean paused;

    private Player player;
    private LevelManager level;

    private int xLvlOffset;
	private int yLvlOffset;
	private int leftBorder = (int) (0.2 * GAME_WIDTH);
	private int rightBorder = (int) (0.8 * GAME_WIDTH);
	private int topBorder = (int) (0.3 * GAME_HEIGHT);
	private int bottomBorder = (int) (0.90 * GAME_HEIGHT);
	private int lvlTilesWide = GetLevelData()[0].length;
	private int lvlTilesHigh = GetLevelData().length;
	private int maxTilesOffsetX = lvlTilesWide - TILES_IN_WIDTH;
	private int maxLvlOffsetX = maxTilesOffsetX * TILES_SIZE;
	private int maxTilesOffsetY = lvlTilesHigh - TILES_IN_HEIGHT;
	private int maxLvlOffsetY = maxTilesOffsetY * TILES_SIZE;

    public World( Game game){
        super(game);
        level = new LevelManager(game);
        player = new Player( 5 * TILES_SIZE ,5 * TILES_SIZE, (int) (64 * SCALE), (int) (64 * SCALE));
        player.loadLvlData(level.getCurrentLevel().getLevelData());

    }

    private void checkCloseToBorder() {
		int playerX = (int) player.getHitbox().x;
		int playerY = (int) player.getHitbox().y;
		int diffX = playerX - xLvlOffset;
		int diffY = playerY - yLvlOffset;

		if (diffX > rightBorder)
			xLvlOffset += diffX - rightBorder;
		else if (diffX < leftBorder)
			xLvlOffset += diffX - leftBorder;

		if (xLvlOffset > maxLvlOffsetX)
			xLvlOffset = maxLvlOffsetX;
		else if (xLvlOffset < 0)
			xLvlOffset = 0;

		if (diffY > bottomBorder)
			yLvlOffset += diffY - bottomBorder;
		else if (diffY < topBorder)
			yLvlOffset += diffY - topBorder;

		if (yLvlOffset > maxLvlOffsetY)
			yLvlOffset = maxLvlOffsetY;
		else if (yLvlOffset < 0)
			yLvlOffset = 0;
	}

    @Override
    public void draw( Graphics g ) {
        level.draw(g,xLvlOffset,yLvlOffset);
        player.render(g,xLvlOffset,yLvlOffset);

    }

    @Override
    public void update() {
        if (!paused){
            checkCloseToBorder();
            player.update();
            level.update();
        }
            
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
            case KeyEvent.VK_S ->{
                player.setDown(false);
            }
            case KeyEvent.VK_SPACE->{
                player.setJump(false);
            }
            //default -> System.out.println("Unexpected value: " + e.getKeyCode());
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
            case KeyEvent.VK_S ->{
                player.setDown(true);
            }
            case KeyEvent.VK_SPACE->{
                player.setJump(true);
            }
            case KeyEvent.VK_A ->{
                GameState.setState(GameState.MENU);
            }
            //default -> System.out.println("Unexpected value: " + e.getKeyCode());
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