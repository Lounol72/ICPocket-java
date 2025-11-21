package states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import game.Game;
import levels.LevelManager;
import ui.LevelButton;
import ui.MenuButtons;
import static utilz.Constants.SCALE;
import static utilz.Constants.UI.BUTTONS.HEIGHT;
import static utilz.Constants.UI.BUTTONS.WIDTH;
import static utilz.Constants.WORLD.GAME_HEIGHT;
import static utilz.Constants.WORLD.GAME_WIDTH;
import static utilz.HelpMethods.GetPhrase;
import utilz.LoadSave;

/**
 * State pour sélectionner un niveau
 * Génère dynamiquement des boutons pour chaque niveau disponible
 */
public class LevelSelect extends State implements StateMethods {

    private String titleString;
    private LevelButton[] levelButtons;
    private MenuButtons backButton;
    private BufferedImage backgroundImage;
    
    // Configuration de la grille de boutons
    private static final int BUTTONS_PER_ROW = 3; // Nombre de colonnes
    private static final int BUTTON_SPACING_X = 20; // Espacement horizontal entre boutons
    private static final int BUTTON_SPACING_Y = 20; // Espacement vertical entre boutons
    private static final int START_Y = 150; // Position Y de départ pour les boutons de niveau
    private static final int BACK_BUTTON_Y = GAME_HEIGHT - 100; // Position Y du bouton retour

    /**
     * Constructeur
     */
    public LevelSelect(Game game) {
        super(game);
        initClasses();
    }

    /**
     * Initialise les classes et génère les boutons dynamiquement
     */
    private void initClasses() {
        titleString = GetPhrase("select_level");
        
        // Créer un LevelManager temporaire pour obtenir le nombre de niveaux
        LevelManager tempLevelManager = new LevelManager(game);
        int numLevels = tempLevelManager.getAmountOfLevels();
        
        // Générer les boutons de niveau dynamiquement
        levelButtons = new LevelButton[numLevels];
        
        // Calculer la position de départ pour centrer la grille horizontalement
        int totalButtonsWidth = (BUTTONS_PER_ROW * WIDTH) + ((BUTTONS_PER_ROW - 1) * BUTTON_SPACING_X);
        int startX = (GAME_WIDTH - totalButtonsWidth) / 2;
        
        // Créer les boutons en grille
        for (int i = 0; i < numLevels; i++) {
            int row = i / BUTTONS_PER_ROW;
            int col = i % BUTTONS_PER_ROW;
            
            int x = startX + (col * (WIDTH + BUTTON_SPACING_X));
            int y = START_Y + (row * (HEIGHT + BUTTON_SPACING_Y));
            
            levelButtons[i] = new LevelButton(x, y, WIDTH, HEIGHT, 0, i, game);
        }
        
        // Créer le bouton retour
        backButton = new MenuButtons(
            GAME_WIDTH / 2 - WIDTH / 2, 
            BACK_BUTTON_Y, 
            WIDTH, 
            HEIGHT, 
            0, 
            "back", 
            GameState.MENU
        );
        
        // Charger l'image de fond
        backgroundImage = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND);
    }

    /**
     * Dessine le state de sélection de niveau
     */
    @Override
    public void draw(Graphics g) {
        // Dessiner le fond
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, GAME_WIDTH, GAME_HEIGHT, null);
        } else {
            // Fallback background
            g.setColor(new Color(30, 52, 62));
            g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        }
        
        // Dessiner le titre
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, (int) (16 * SCALE)));
        int titleX = GAME_WIDTH / 2 - g.getFontMetrics().stringWidth(titleString) / 2;
        g.drawString(titleString, titleX, 80);
        
        // Dessiner les boutons de niveau
        for (LevelButton lb : levelButtons) {
            lb.draw(g);
        }
        
        // Dessiner le bouton retour
        backButton.draw(g);
    }

    /**
     * Met à jour le state
     */
    @Override
    public void update() {
        // Mettre à jour les boutons de niveau
        for (LevelButton lb : levelButtons) {
            lb.update();
        }
        
        // Mettre à jour le bouton retour
        backButton.update();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Pas d'action au clavier nécessaire
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Pas d'action au clavier nécessaire
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Pas d'action au clavier nécessaire
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Réinitialiser l'état hover pour tous les boutons de niveau
        for (LevelButton lb : levelButtons) {
            lb.setMouseOver(false);
        }
        
        // Vérifier si la souris est sur un bouton de niveau
        for (LevelButton lb : levelButtons) {
            if (isIn(e, lb)) {
                lb.setMouseOver(true);
            }
        }
        
        // Gérer le bouton retour
        backButton.setMouseOver(false);
        if (isIn(e, backButton)) {
            backButton.setMouseOver(true);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Pas de drag nécessaire
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Pas d'action nécessaire
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Gérer les boutons de niveau
        for (LevelButton lb : levelButtons) {
            if (isIn(e, lb)) {
                lb.setMousePressed(true);
            }
        }
        
        // Gérer le bouton retour
        if (isIn(e, backButton)) {
            backButton.setMousePressed(true);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Gérer les boutons de niveau
        for (LevelButton lb : levelButtons) {
            if (isIn(e, lb)) {
                lb.action();
            }
            lb.resetBools();
        }
        
        // Gérer le bouton retour
        if (isIn(e, backButton)) {
            backButton.action();
        }
        backButton.resetBools();
    }

    @Override
    public void UpdateStrings() {
        titleString = GetPhrase("select_level");
        // Mettre à jour le texte du bouton retour
        backButton.setText(GetPhrase(backButton.getBaseText()));
    }
}

