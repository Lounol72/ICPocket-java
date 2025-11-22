package levels;

// Java standard library imports
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import static utilz.Constants.WORLD.ONE_WAY_PLATFORMS.ONE_WAY_TILE_IDS;
import static utilz.Constants.WORLD.TILES_IN_HEIGHT;
import static utilz.Constants.WORLD.TILES_IN_WIDTH;
import static utilz.Constants.WORLD.TILES_SIZE;

public class Level {
    private int [][] levelData;
    private int lvlTilesWide;
    private int lvlTilesHigh;
    private int maxTilesOffsetX;
    private int maxLvlOffsetX;
    private int maxTilesOffsetY;
    private int maxLvlOffsetY;


    // Collision rectangles for optimized AABB collision detection
    private List<Rectangle2D.Float> solidCollisions;
    private List<Rectangle2D.Float> oneWayPlatformCollisions;

    public Level(int[][] levelData) {
        this.levelData = levelData;
        calcOffsets();
        this.solidCollisions = new ArrayList<>();
        this.oneWayPlatformCollisions = new ArrayList<>();
        generateCollisionRectangles();
    }

    /**
     * Génère les rectangles de collision optimisés à partir de la matrice du niveau
     * Fusionne les rectangles adjacents avec le même ID de tile pour améliorer les performances
     */
    private void generateCollisionRectangles() {
        int height = levelData.length;
        int width = levelData[0].length;
        
        // Première passe : fusion horizontale
        List<Rectangle2D.Float> horizontalMerged = new ArrayList<>();
        
        for (int y = 0; y < height; y++) {
            Rectangle2D.Float currentRect = null;
            int currentTileId = -1;
            
            for (int x = 0; x < width; x++) {
                int tileId = levelData[y][x];
                
                // Vérifier si c'est une tile solide (pas vide)
                if (tileId != 21) {
                    // Si c'est la même tile que la précédente et adjacente, étendre le rectangle
                    if (currentRect != null && tileId == currentTileId) {
                        currentRect.width += TILES_SIZE;
                    } else {
                        // Sauvegarder le rectangle précédent s'il existe
                        if (currentRect != null) {
                            horizontalMerged.add(currentRect);
                        }
                        // Créer un nouveau rectangle
                        currentRect = new Rectangle2D.Float(
                            x * TILES_SIZE, 
                            y * TILES_SIZE, 
                            TILES_SIZE, 
                            TILES_SIZE
                        );
                        currentTileId = tileId;
                    }
                } else {
                    // Tile vide, sauvegarder le rectangle actuel s'il existe
                    if (currentRect != null) {
                        horizontalMerged.add(currentRect);
                        currentRect = null;
                        currentTileId = -1;
                    }
                }
            }
            
            // Ajouter le dernier rectangle de la ligne s'il existe
            if (currentRect != null) {
                horizontalMerged.add(currentRect);
            }
        }
        
        // Deuxième passe : fusion verticale
        List<Rectangle2D.Float> finalRectangles = new ArrayList<>();
        boolean[] processed = new boolean[horizontalMerged.size()];
        
        for (int i = 0; i < horizontalMerged.size(); i++) {
            if (processed[i]) continue;
            
            Rectangle2D.Float baseRect = horizontalMerged.get(i);
            List<Rectangle2D.Float> mergeGroup = new ArrayList<>();
            mergeGroup.add(baseRect);
            processed[i] = true;
            
            // Chercher des rectangles à fusionner verticalement
            for (int j = i + 1; j < horizontalMerged.size(); j++) {
                if (processed[j]) continue;
                
                Rectangle2D.Float candidateRect = horizontalMerged.get(j);
                
                // Vérifier si les rectangles peuvent être fusionnés verticalement
                if (canMergeVertically(baseRect, candidateRect)) {
                    mergeGroup.add(candidateRect);
                    processed[j] = true;
                }
            }
            
            // Fusionner le groupe en un seul rectangle
            Rectangle2D.Float mergedRect = mergeRectanglesVertically(mergeGroup);
            finalRectangles.add(mergedRect);
        }
        
        // Séparer les collisions solides des plateformes one-way
        for (Rectangle2D.Float rect : finalRectangles) {
            int tileX = (int) (rect.x / TILES_SIZE);
            int tileY = (int) (rect.y / TILES_SIZE);
            int tileId = levelData[tileY][tileX];
            
            if (isOneWayPlatform(tileId)) {
                oneWayPlatformCollisions.add(rect);
            } else {
                solidCollisions.add(rect);
            }
        }
    }
    
    /**
     * Vérifie si deux rectangles peuvent être fusionnés verticalement
     */
    private boolean canMergeVertically(Rectangle2D.Float rect1, Rectangle2D.Float rect2) {
        // Même position X et largeur
        boolean sameX = Math.abs(rect1.x - rect2.x) < 1;
        boolean sameWidth = Math.abs(rect1.width - rect2.width) < 1;
        
        // Adjacents verticalement
        boolean verticallyAdjacent = Math.abs((rect1.y + rect1.height) - rect2.y) < 1 ||
                                   Math.abs((rect2.y + rect2.height) - rect1.y) < 1;
        
        return sameX && sameWidth && verticallyAdjacent;
    }
    
    /**
     * Fusionne une liste de rectangles en un seul rectangle vertical
     */
    private Rectangle2D.Float mergeRectanglesVertically(List<Rectangle2D.Float> rectangles) {
        if (rectangles.isEmpty()) return null;
        
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;
        
        for (Rectangle2D.Float rect : rectangles) {
            minX = Math.min(minX, rect.x);
            minY = Math.min(minY, rect.y);
            maxX = Math.max(maxX, rect.x + rect.width);
            maxY = Math.max(maxY, rect.y + rect.height);
        }
        
        return new Rectangle2D.Float(minX, minY, maxX - minX, maxY - minY);
    }
    
    /**
     * Vérifie si un ID de tile correspond à une plateforme one-way
     */
    private boolean isOneWayPlatform(int tileId) {
        for (int oneWayId : ONE_WAY_TILE_IDS) {
            if (tileId == oneWayId) {
                return true;
            }
        }
        return false;
    }

    public int getSpriteIndex(int x, int y) {
        return levelData[y][x];
    }
    
    public int[][] getLevelData() {
        return levelData;
    }
    
    /**
     * Retourne la liste des rectangles de collision solides
     */
    public List<Rectangle2D.Float> getSolidCollisions() {
        return solidCollisions;
    }
    
    /**
     * Retourne la liste des rectangles de collision des plateformes one-way
     */
    public List<Rectangle2D.Float> getOneWayPlatformCollisions() {
        return oneWayPlatformCollisions;
    }

    private void calcOffsets() {
        lvlTilesWide = levelData[0].length;
        lvlTilesHigh = levelData.length;
        maxTilesOffsetX = lvlTilesWide - TILES_IN_WIDTH;
        maxLvlOffsetX = maxTilesOffsetX * TILES_SIZE;
        maxTilesOffsetY = lvlTilesHigh - TILES_IN_HEIGHT;
        maxLvlOffsetY = maxTilesOffsetY * TILES_SIZE;
    }

    public int getLvlTilesWide() {
        return lvlTilesWide;
    }

    public int getLvlTilesHigh() {
        return lvlTilesHigh;
    }

    public int getMaxTilesOffsetX() {
        return maxTilesOffsetX;
    }

    public int getMaxLvlOffsetX() {
        return maxLvlOffsetX;
    }

    public int getMaxTilesOffsetY() {
        return maxTilesOffsetY;
    }

    public int getMaxLvlOffsetY() {
        return maxLvlOffsetY;
    }
}
