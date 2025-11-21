package ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import game.Game;
import static utilz.Constants.UI.BUTTONS.DEFAULT_HEIGHT;
import static utilz.Constants.UI.BUTTONS.DEFAULT_WIDTH;
import static utilz.HelpMethods.GetPhrase;
import utilz.LoadSave;

/**
 * Bouton pour sélectionner un niveau spécifique.
 * 
 * <p>Fonctionnalités :</p>
 * <ul>
 *   <li>Affiche "Level X" où X est le numéro du niveau (1-indexed pour l'utilisateur)</li>
 *   <li>Gère les états visuels (normal, hover, pressed)</li>
 *   <li>Change le niveau actuel lors du clic et lance le jeu</li>
 * </ul>
 * 
 * @author ICPocket Team
 */
public class LevelButton extends Button {
    /** Index de la ligne dans l'atlas de boutons */
    private int rowIndex = 0;
    
    /** Texte affiché sur le bouton (ex: "Level 1") */
    private String text;
    
    /** Index du niveau (0-indexed, sera affiché comme levelIndex + 1) */
    private int levelIndex;
    
    /** Instance du jeu pour accéder au World et changer le niveau */
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
        // Utilise le système de traduction pour récupérer le libellé de base "Level"
        // puis concatène le numéro (1-indexé) pour l'affichage utilisateur.
        this.text = GetPhrase("level") + " " + (levelIndex + 1);
        loadImages();
    }

    /**
     * Charge les images du bouton depuis l'atlas de boutons.
     * Extrait les 3 états du bouton (normal, hover, pressed) depuis l'atlas.
     */
    private void loadImages() {
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.BUTTONS);
        if (temp != null) {
            for (int i = 0; i < img.length; i++) {
                img[i] = temp.getSubimage(
                    (rowIndex * DEFAULT_WIDTH), 
                    i * DEFAULT_HEIGHT, 
                    DEFAULT_WIDTH, 
                    DEFAULT_HEIGHT
                );
            }
        }
    }

    /**
     * Met à jour l'état visuel du bouton selon les interactions de la souris.
     * Index 0 = normal, 1 = hover, 2 = pressed
     */
    public void update() {
        index = 0; // État normal par défaut
        if (isMouseOver) {
            index = 1; // Souris survol
        }
        if (isMousePressed) {
            index = 2; // Bouton pressé
        }
    }

    /**
     * Dessine le bouton et son texte centré.
     * 
     * @param g Contexte graphique pour le dessin
     */
    public void draw(Graphics g) {
        // Dessiner l'image du bouton si disponible
        if (img != null && img[index] != null) {
            g.drawImage(img[index], x, y, width, height, null);
        }
        
        // Dessiner le texte centré sur le bouton
        int textX = x + width / 2 - g.getFontMetrics().stringWidth(text) / 2;
        int textY = y + height / 2 + g.getFontMetrics().getHeight() / 2;
        g.drawString(text, textX, textY);
    }

    /**
     * Action effectuée lors du clic sur le bouton.
     * Change le niveau actuel dans le World et passe à l'état WORLD pour lancer le jeu.
     */
    public void action() {
        // Changer le niveau dans le World
        game.getWorld().changeLevel(levelIndex);
        // Passer à l'état WORLD pour lancer le jeu
        states.GameState.setState(states.GameState.WORLD);
    }

    /**
     * Retourne l'index du niveau associé à ce bouton.
     * 
     * @return Index du niveau (0-indexed)
     */
    public int getLevelIndex() {
        return levelIndex;
    }
}

