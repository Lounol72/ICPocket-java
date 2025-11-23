package states;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

import entities.Player;
import game.Game;
import levels.Level;
import levels.LevelManager;
import static utilz.Constants.SCALE;
import static utilz.Constants.WORLD.ENVIRONMENT.BIG_CLOUDS_HEIGHT;
import static utilz.Constants.WORLD.ENVIRONMENT.BIG_CLOUDS_WIDTH;
import static utilz.Constants.WORLD.ENVIRONMENT.SMALL_CLOUD_1_HEIGHT;
import static utilz.Constants.WORLD.ENVIRONMENT.SMALL_CLOUD_1_WIDTH;
import static utilz.Constants.WORLD.GAME_HEIGHT;
import static utilz.Constants.WORLD.GAME_WIDTH;
import static utilz.Constants.WORLD.TILES_SIZE;
import utilz.LoadSave;

public class World extends State implements StateMethods {

    private boolean paused;

    private final Player player;
    private final LevelManager levels;

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


    private final BufferedImage backgroundImage;
    private final BufferedImage bigCloud;
    private final BufferedImage smallCloud1;
    private int[] smallCloudsPos;
    private Random rnd = new Random();

    public World(Game game) {
        super(game);
        levels = new LevelManager(game);
        calcOffsets();
        player = new Player(5 * TILES_SIZE, 5 * TILES_SIZE, (int) (64 * SCALE), (int) (64 * SCALE),
                levels.getCurrentLevel());
        player.loadLvlData(levels.getCurrentLevel().getLevelData());


        backgroundImage = LoadSave.GetSpriteAtlas(LoadSave.WORLD_BACKGROUND);
        bigCloud = LoadSave.GetSpriteAtlas(LoadSave.BIG_CLOUDS);
        smallCloud1 = LoadSave.GetSpriteAtlas(LoadSave.SMALL_CLOUD_1);
        smallCloudsPos = new int[8];

        for (int i = 0; i < smallCloudsPos.length; i++) {
            smallCloudsPos[i] = (int) (90 * SCALE) + rnd.nextInt((int) (100 * SCALE));
        }
    }

    /**
     * Calcule les offsets et limites du niveau actuel.
     * Utilisé pour déterminer les limites de déplacement de la caméra.
     */
    private void calcOffsets() {
        Level currentLevel = levels.getCurrentLevel();
        lvlTilesWide = currentLevel.getLvlTilesWide();
        lvlTilesHigh = currentLevel.getLvlTilesHigh();
        maxTilesOffsetX = currentLevel.getMaxTilesOffsetX();
        maxLvlOffsetX = currentLevel.getMaxLvlOffsetX();
        maxTilesOffsetY = currentLevel.getMaxTilesOffsetY();
        maxLvlOffsetY = currentLevel.getMaxLvlOffsetY();
    }

    /**
     * Change le niveau actuel et recharge toutes les données nécessaires.
     * 
     * <p>Actions effectuées :</p>
     * <ul>
     *   <li>Change le niveau dans le LevelManager</li>
     *   <li>Recalcule les offsets de caméra</li>
     *   <li>Met à jour les collisions AABB du joueur</li>
     *   <li>Réinitialise la position du joueur au point de départ</li>
     *   <li>Réinitialise la caméra</li>
     * </ul>
     * 
     * @param levelIndex Index du niveau à charger (0-indexed)
     */
    public void changeLevel(int levelIndex) {
        // Changer le niveau dans le LevelManager
        levels.setLevel(levelIndex);
        
        // Recalculer les offsets de caméra pour le nouveau niveau
        calcOffsets();
        
        // Charger le nouveau niveau avec le système AABB
        // Cela met à jour la référence currentLevel dans Player et les rectangles de collision
        player.loadLevel(levels.getCurrentLevel());
        
        // Réinitialiser la position du joueur au point de départ du niveau
        float startX = 5 * TILES_SIZE;
        float startY = 5 * TILES_SIZE;
        player.getHitbox().x = startX;
        player.getHitbox().y = startY;
        player.getPhysicsBody().setPosition(startX, startY);
        
        // Réinitialiser les offsets de caméra au début du niveau
        xLvlOffset = 0;
        yLvlOffset = 0;
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

    private void drawClouds(Graphics g){
        if (bigCloud != null) {
            for (int i = 0; i < 3; i++) {
                g.drawImage(bigCloud, (int)((i * BIG_CLOUDS_WIDTH) - (0.3 * xLvlOffset)), (int)(204 * SCALE), BIG_CLOUDS_WIDTH,  BIG_CLOUDS_HEIGHT,null);
            }
        }
        if (smallCloud1 != null) {
            for (int i = 0; i< smallCloudsPos.length; i++) {
                g.drawImage(smallCloud1, (int)((i * SMALL_CLOUD_1_WIDTH * 4) - (0.7 * xLvlOffset)) , smallCloudsPos[i], SMALL_CLOUD_1_WIDTH, SMALL_CLOUD_1_HEIGHT, null);
            }
            
        }
    }

    private void drawEnvironment(Graphics g){
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, GAME_WIDTH, GAME_HEIGHT, null);
        }
        drawClouds(g);
    }

    @Override
    public void draw(Graphics g) {
        drawEnvironment(g);
        
        levels.draw(g, xLvlOffset, yLvlOffset);
        player.render(g, xLvlOffset, yLvlOffset);

    }

    @Override
    public void update() {
        if (!paused) {
            checkCloseToBorder();
            player.update();
            levels.update();
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
    public Player getPlayer(){
        return player;
    }

    /**
     * Retourne l'instance du gestionnaire de niveaux.
     *
     * @return Le LevelManager du monde
     */
    public LevelManager getLevelManager() {
        return levels;
    }
}