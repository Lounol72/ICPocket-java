package ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import game.Game;
import static utilz.Constants.UI.BUTTONS.DEFAULT_HEIGHT;
import static utilz.Constants.UI.BUTTONS.DEFAULT_WIDTH;
import utilz.LoadSave;

/**
 * Bouton pour sélectionner un niveau spécifique
 * Affiche "Level X" où X est le numéro du niveau (1-indexed)
 */
public class LevelButton extends Button {
    private int rowIndex = 0;
    private String text;
    private int levelIndex; // Index du niveau (0-indexed)
    private Game game;

    /**
     * Constructeur du bouton de niveau
     * @param x Position X
     * @param y Position Y
     * @param width Largeur
     * @param height Hauteur
     * @param rowIndex Index de la ligne dans l'atlas de boutons
     * @param levelIndex Index du niveau (0-indexed, sera affiché comme levelIndex + 1)
     * @param game Instance du jeu pour accéder au LevelManager
     */
    public LevelButton(int x, int y, int width, int height, int rowIndex, int levelIndex, Game game) {
        super(x, y, width, height);
        this.rowIndex = rowIndex;
        this.levelIndex = levelIndex;
        this.game = game;
        this.text = "Level " + (levelIndex + 1); // Afficher 1-indexed pour l'utilisateur
        loadImages();
    }

    /**
     * Charge les images du bouton depuis l'atlas
     */
    private void loadImages() {
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.BUTTONS);
        for (int i = 0; i < img.length; i++)
            img[i] = temp.getSubimage((rowIndex * DEFAULT_WIDTH), i * DEFAULT_HEIGHT, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Met à jour l'état visuel du bouton
     */
    public void update() {
        index = 0;
        if (isMouseOver)
            index = 1;
        if (isMousePressed)
            index = 2;
    }

    /**
     * Dessine le bouton et son texte
     */
    public void draw(Graphics g) {
        g.drawImage(img[index], x, y, width, height, null);
        g.drawString(text, x + width / 2 - g.getFontMetrics().stringWidth(text) / 2, 
                     y + height / 2 + g.getFontMetrics().getHeight() / 2);
    }

    /**
     * Action effectuée lors du clic sur le bouton
     * Change le niveau dans LevelManager et passe à l'état WORLD
     */
    public void action() {
        // Obtenir le LevelManager depuis World et changer le niveau
        game.getWorld().changeLevel(levelIndex);
        states.GameState.setState(states.GameState.WORLD);
    }

    /**
     * Retourne l'index du niveau
     */
    public int getLevelIndex() {
        return levelIndex;
    }
}

