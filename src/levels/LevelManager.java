package levels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import game.Game;
import static utilz.Constants.WORLD.TILES_DEFAULT_SIZE;
import static utilz.Constants.WORLD.TILES_SIZE;
import utilz.LoadSave;
import static utilz.LoadSave.GetSpriteAtlas;
import static utilz.LoadSave.LEVEL_ATLAS;

/**
 * Gestionnaire des niveaux du jeu.
 * 
 * <p>Responsabilités :</p>
 * <ul>
 *   <li>Chargement et gestion de tous les niveaux disponibles</li>
 *   <li>Gestion du niveau actuellement joué</li>
 *   <li>Rendu des sprites du niveau</li>
 *   <li>Changement de niveau dynamique</li>
 * </ul>
 */
public class LevelManager {

    /** Nombre de tuiles en largeur dans l'atlas de tiles */
    private static final int NB_TILES_WIDTH = 7;
    
    /** Nombre de tuiles en hauteur dans l'atlas de tiles */
    private static final int NB_TILES_HEIGHT = 4;

    /** Instance du jeu (conservée pour usage futur) */
    private Game game;
    
    /** Tableau des sprites de tiles du niveau */
    private BufferedImage[] levelSprite;
    
    /** Liste de tous les niveaux chargés */
    private ArrayList<Level> levels;
    
    /** Index du niveau actuellement actif (0-indexed) */
    private int levelIdx = 0;

    /**
     * Constructeur du gestionnaire de niveaux.
     * 
     * @param game Instance du jeu
     */
    public LevelManager(Game game) {
        this.game = game;
        importOutsideSprites();
        levels = new ArrayList<>();
        buildAllLevels();
    }

    /**
     * Importe et découpe les sprites de tiles depuis l'atlas.
     * Crée un tableau de 48 sprites (7x4 + quelques extras).
     */
    public void importOutsideSprites() {
        levelSprite = new BufferedImage[48];
        BufferedImage img = GetSpriteAtlas(LEVEL_ATLAS);
        
        // Découper l'atlas en sprites individuels
        for (int i = 0; i < NB_TILES_HEIGHT; i++) {
            for (int j = 0; j < NB_TILES_WIDTH; j++) {
                int index = i * NB_TILES_WIDTH + j;
                levelSprite[index] = img.getSubimage(
                    j * TILES_DEFAULT_SIZE, 
                    i * TILES_DEFAULT_SIZE, 
                    TILES_DEFAULT_SIZE, 
                    TILES_DEFAULT_SIZE
                );
            }
        }
    }

    /**
     * Dessine le niveau actuel avec les offsets de caméra.
     * 
     * @param g Contexte graphique pour le dessin
     * @param xLvlOffset Offset horizontal de la caméra
     * @param yLvlOffset Offset vertical de la caméra
     */
    public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
        Level currentLevel = levels.get(levelIdx);
        int[][] levelData = currentLevel.getLevelData();
        
        // Dessiner chaque tile du niveau
        for (int j = 0; j < levelData.length; j++) {
            for (int i = 0; i < levelData[0].length; i++) {
                int spriteIndex = currentLevel.getSpriteIndex(i, j);
                int x = (i * TILES_SIZE) - xLvlOffset;
                int y = (j * TILES_SIZE) - yLvlOffset;
                g.drawImage(levelSprite[spriteIndex], x, y, TILES_SIZE, TILES_SIZE, null);
            }
        }
        
        // Debug : Décommenter pour afficher les rectangles de collision AABB
        // drawCollisionRectangles(g, xLvlOffset, yLvlOffset);
    }
    
    /**
     * Dessine les rectangles de collision AABB pour le debug.
     * Méthode utilisée uniquement pour le développement et le débogage.
     * 
     * @param g Contexte graphique pour le dessin
     * @param xLvlOffset Offset horizontal de la caméra
     * @param yLvlOffset Offset vertical de la caméra
     */
    private void drawCollisionRectangles(Graphics g, int xLvlOffset, int yLvlOffset) {
        Level currentLevel = levels.get(levelIdx);
        
        // Dessiner les collisions solides en rouge semi-transparent
        g.setColor(new Color(255, 0, 0, 100));
        for (Rectangle2D.Float rect : currentLevel.getSolidCollisions()) {
            g.fillRect(
                (int) (rect.x - xLvlOffset), 
                (int) (rect.y - yLvlOffset), 
                (int) rect.width, 
                (int) rect.height
            );
        }
        
        // Dessiner les plateformes one-way en bleu semi-transparent
        g.setColor(new Color(0, 0, 255, 100));
        for (Rectangle2D.Float rect : currentLevel.getOneWayPlatformCollisions()) {
            g.fillRect(
                (int) (rect.x - xLvlOffset), 
                (int) (rect.y - yLvlOffset), 
                (int) rect.width, 
                (int) rect.height
            );
        }
        
        // Dessiner les contours des rectangles de collision solides
        g.setColor(Color.RED);
        for (Rectangle2D.Float rect : currentLevel.getSolidCollisions()) {
            g.drawRect(
                (int) (rect.x - xLvlOffset), 
                (int) (rect.y - yLvlOffset), 
                (int) rect.width, 
                (int) rect.height
            );
        }
        
        // Dessiner les contours des plateformes one-way
        g.setColor(Color.BLUE);
        for (Rectangle2D.Float rect : currentLevel.getOneWayPlatformCollisions()) {
            g.drawRect(
                (int) (rect.x - xLvlOffset), 
                (int) (rect.y - yLvlOffset), 
                (int) rect.width, 
                (int) rect.height
            );
        }
    }

    /**
     * Met à jour la logique du niveau (appelé chaque frame).
     * Actuellement vide, réservé pour les fonctionnalités futures.
     */
    public void update() {
        // Logique de mise à jour du niveau à implémenter si nécessaire
    }

    /**
     * Retourne le niveau actuellement actif.
     * 
     * @return Le niveau actuel
     */
    public Level getCurrentLevel() {
        return levels.get(levelIdx);
    }

    /**
     * Change le niveau actuellement actif.
     * 
     * @param index Index du niveau à charger (0-indexed)
     * @throws IndexOutOfBoundsException si l'index est invalide (hors limites)
     */
    public void setLevel(int index) {
        if (index < 0 || index >= levels.size()) {
            throw new IndexOutOfBoundsException(
                "Index de niveau invalide: " + index + 
                ". Nombre de niveaux disponibles: " + levels.size()
            );
        }
        this.levelIdx = index;
    }

    /**
     * Construit tous les niveaux à partir des données chargées.
     * Charge tous les fichiers JSON de niveaux et crée les objets Level correspondants.
     */
    private void buildAllLevels() {
        int[][][] allData = LoadSave.GetAllLevelData();
        for (int i = 0; i < allData.length; i++) {
            levels.add(new Level(allData[i]));
        }
    }

    /**
     * Retourne le nombre total de niveaux disponibles.
     * 
     * @return Le nombre de niveaux chargés
     */
    public int getAmountOfLevels() {
        return levels.size();
    }
}