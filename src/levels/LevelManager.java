package levels;

import game.Game;
import utilz.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static game.Game.*;
import static utilz.Constants.WORLD.TILES_SIZE;
import static utilz.HelpMethods.GetLevelData;
import static utilz.LoadSave.*;

public class LevelManager {

    private Game game;
    private BufferedImage[] levelSprite;
    private Level levelOne;

    public LevelManager(Game game) {
        this.game = game;
        importOutsideSprites();
        levelOne = new Level(GetLevelData());
    }

    public void importOutsideSprites() {
        levelSprite = new BufferedImage[28];
        BufferedImage img = GetSpriteAtlas(LEVEL_ATLAS);
        for (int i = 0; i<4; i++) {
            for (int j = 0; j<7; j++) {
                int index = i*7+ j;
                levelSprite[index] = img.getSubimage(j*16, i*16, 16, 16);
            }
        }

    }

    public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
        for (int j = 0; j < levelOne.getLevelData().length; j++)
            for (int i = 0; i < levelOne.getLevelData()[0].length; i++) {
                int index = levelOne.getSpriteIndex(i, j);
                if (index != -1)
                    g.drawImage(levelSprite[index], (i * TILES_SIZE) - xLvlOffset, (j * TILES_SIZE) - yLvlOffset, TILES_SIZE, TILES_SIZE, null);
            }
//        g.setColor(Color.blue);
//        drawNumbers(g, xLvlOffset, yLvlOffset);
    }

    public void drawNumbers(Graphics g,  int xLvlOffset, int yLvlOffset){
        for (int j = 0; j < levelOne.getLevelData().length; j++)
            for (int i = 0; i < levelOne.getLevelData()[0].length; i++) {
                int index = levelOne.getSpriteIndex(i, j);
                g.drawString(String.valueOf(index), (i * TILES_SIZE) - xLvlOffset, (j * TILES_SIZE) - yLvlOffset);

            }
    }

    public void update() {
        // Update level logic here
    }

    public Level getCurrentLevel() {
        return levelOne;
    }
}
