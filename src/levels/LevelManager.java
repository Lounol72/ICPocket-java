package levels;

import game.Game;
import utilz.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static game.Game.*;
import static utilz.Constants.WORLD.TILES_SIZE;
import static utilz.LoadSave.*;

public class LevelManager {

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
        for (int i = 0; i<4; i++) {
            for (int j = 0; j<12; j++) {
                int index = i*12+ j;
                levelSprite[index] = img.getSubimage(j*32, i*32, 32, 32);
            }
        }

    }

    public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
        for (int j = 0; j < levelOne.getLevelData().length; j++)
            for (int i = 0; i < levelOne.getLevelData()[0].length; i++) {
                int index = levelOne.getSpriteIndex(i, j);
                g.drawImage(levelSprite[index], (i * TILES_SIZE) - xLvlOffset, (j * TILES_SIZE) - yLvlOffset, TILES_SIZE, TILES_SIZE, null);

            }
    }

    public void update() {
        // Update level logic here
    }

    public Level getCurrentLevel() {
        return levelOne;
    }
}
