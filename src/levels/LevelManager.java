package levels;

import game.Game;
import utilz.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static utilz.Constants.WORLD.TILES_DEFAULT_SIZE;
import static utilz.Constants.WORLD.TILES_SIZE;
import static utilz.LoadSave.*;

public class LevelManager {

    private static final int NB_TILES_WIDTH = 7;
    private static final int NB_TILES_HEIGHT = 4;

    private Game game;
    private BufferedImage[] levelSprite;
    private Level levelOne;

    public LevelManager(Game game) {
        this.game = game;
        importOutsideSprites();
        levelOne = new Level(LoadSave.GetLevelData());
    }

    public void importOutsideSprites() {
        levelSprite = new BufferedImage[48];
        BufferedImage img = GetSpriteAtlas(LEVEL_ATLAS);
        for (int i = 0; i<NB_TILES_HEIGHT; i++) {
            for (int j = 0; j<NB_TILES_WIDTH; j++) {
                int index = i*NB_TILES_WIDTH+ j;
                levelSprite[index] = img.getSubimage(j*TILES_DEFAULT_SIZE, i*TILES_DEFAULT_SIZE, TILES_DEFAULT_SIZE, TILES_DEFAULT_SIZE);
            }
        }

    }

    public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
        for (int j = 0; j < levelOne.getLevelData().length; j++)
            for (int i = 0; i < levelOne.getLevelData()[0].length; i++) {
                int index = levelOne.getSpriteIndex(i, j);
                g.drawImage(levelSprite[index], (i * TILES_SIZE) - xLvlOffset, (j * TILES_SIZE) - yLvlOffset, TILES_SIZE, TILES_SIZE, null);

                // Afficher le contour de chaque case
                g.setColor(Color.RED);
                g.drawRect((i * TILES_SIZE) - xLvlOffset, (j * TILES_SIZE) - yLvlOffset, TILES_SIZE, TILES_SIZE);
                
            }
    }

    public void update() {
        // Update level logic here
    }

    public Level getCurrentLevel() {
        return levelOne;
    }
}