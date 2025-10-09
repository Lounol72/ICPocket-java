package levels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import game.Game;
import static utilz.Constants.WORLD.TILES_DEFAULT_SIZE;
import static utilz.Constants.WORLD.TILES_SIZE;
import utilz.LoadSave;
import static utilz.LoadSave.GetSpriteAtlas;
import static utilz.LoadSave.LEVEL_ATLAS;

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
        // Dessiner les sprites du niveau
        for (int j = 0; j < levelOne.getLevelData().length; j++)
            for (int i = 0; i < levelOne.getLevelData()[0].length; i++) {
                int index = levelOne.getSpriteIndex(i, j);
                g.drawImage(levelSprite[index], (i * TILES_SIZE) - xLvlOffset, (j * TILES_SIZE) - yLvlOffset, TILES_SIZE, TILES_SIZE, null);
            }
        
        // Debug : Afficher les rectangles de collision AABB fusionnés
        drawCollisionRectangles(g, xLvlOffset, yLvlOffset);
    }
    
    /**
     * Dessine les rectangles de collision AABB pour le debug
     */
    private void drawCollisionRectangles(Graphics g, int xLvlOffset, int yLvlOffset) {
        // Dessiner les collisions solides en rouge
        g.setColor(new Color(255, 0, 0, 100)); // Rouge semi-transparent
        for (Rectangle2D.Float rect : levelOne.getSolidCollisions()) {
            g.fillRect(
                (int) (rect.x - xLvlOffset), 
                (int) (rect.y - yLvlOffset), 
                (int) rect.width, 
                (int) rect.height
            );
        }
        
        // Dessiner les plateformes one-way en bleu
        g.setColor(new Color(0, 0, 255, 100)); // Bleu semi-transparent
        for (Rectangle2D.Float rect : levelOne.getOneWayPlatformCollisions()) {
            g.fillRect(
                (int) (rect.x - xLvlOffset), 
                (int) (rect.y - yLvlOffset), 
                (int) rect.width, 
                (int) rect.height
            );
        }
        
        // Dessiner les contours des rectangles
        g.setColor(Color.RED);
        for (Rectangle2D.Float rect : levelOne.getSolidCollisions()) {
            g.drawRect(
                (int) (rect.x - xLvlOffset), 
                (int) (rect.y - yLvlOffset), 
                (int) rect.width, 
                (int) rect.height
            );
        }
        
        g.setColor(Color.BLUE);
        for (Rectangle2D.Float rect : levelOne.getOneWayPlatformCollisions()) {
            g.drawRect(
                (int) (rect.x - xLvlOffset), 
                (int) (rect.y - yLvlOffset), 
                (int) rect.width, 
                (int) rect.height
            );
        }
    }

    public void update() {
        // Update level logic here
    }

    public Level getCurrentLevel() {
        return levelOne;
    }
}