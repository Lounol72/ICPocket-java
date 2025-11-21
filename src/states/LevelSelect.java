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
 * State pour sélectionner un niveau.
 * 
 * <p>Fonctionnalités :</p>
 * <ul>
 *   <li>Génère dynamiquement des boutons pour chaque niveau disponible</li>
 *   <li>Organise les boutons en grille (3 colonnes par défaut)</li>
 *   <li>Permet de revenir au menu principal</li>
 *   <li>Gère les interactions souris pour la sélection</li>
 * </ul>
 * 
 * @author ICPocket Team
 */
public class LevelSelect extends State implements StateMethods {

    /** Texte du titre affiché en haut de l'écran */
    private String titleString;
    
    /** Tableau des boutons de niveau (générés dynamiquement) */
    private LevelButton[] levelButtons;
    
    /** Bouton pour revenir au menu principal */
    private MenuButtons backButton;
    
    /** Image de fond de l'écran */
    private BufferedImage backgroundImage;
    
    // ================================
    // CONFIGURATION DE LA GRILLE
    // ================================
    
    /** Nombre de colonnes dans la grille de boutons */
    private static final int BUTTONS_PER_ROW = 3;
    
    /** Espacement horizontal entre les boutons (en pixels) */
    private static final int BUTTON_SPACING_X = 20;
    
    /** Espacement vertical entre les boutons (en pixels) */
    private static final int BUTTON_SPACING_Y = 20;
    
    /** Position Y de départ pour les boutons de niveau */
    private static final int START_Y = 150;
    
    /** Position Y du bouton retour (en bas de l'écran) */
    private static final int BACK_BUTTON_Y = GAME_HEIGHT - 100;

    /**
     * Constructeur du state de sélection de niveau.
     * 
     * @param game Instance du jeu
     */
    public LevelSelect(Game game) {
        super(game);
        initClasses();
    }

    /**
     * Initialise les classes et génère les boutons dynamiquement.
     * 
     * <p>Processus :</p>
     * <ol>
     *   <li>Charge le texte du titre depuis les traductions</li>
     *   <li>Crée un LevelManager temporaire pour obtenir le nombre de niveaux</li>
     *   <li>Génère un bouton pour chaque niveau disponible</li>
     *   <li>Organise les boutons en grille centrée</li>
     *   <li>Crée le bouton retour</li>
     *   <li>Charge l'image de fond</li>
     * </ol>
     */
    private void initClasses() {
        // Charger le titre traduit
        titleString = GetPhrase("select_level");
        
        // Créer un LevelManager temporaire pour obtenir le nombre de niveaux
        // Note: On crée un nouveau LevelManager car on ne peut pas accéder à celui du World
        // avant que LevelSelect soit initialisé
        LevelManager tempLevelManager = new LevelManager(game);
        int numLevels = tempLevelManager.getAmountOfLevels();
        
        // Générer les boutons de niveau dynamiquement
        // Gérer le cas où il n'y a pas de niveaux (tableau vide)
        if (numLevels > 0) {
            levelButtons = new LevelButton[numLevels];
        } else {
            levelButtons = new LevelButton[0];
        }
        
        // Calculer la position de départ pour centrer la grille horizontalement
        int totalButtonsWidth = (BUTTONS_PER_ROW * WIDTH) + ((BUTTONS_PER_ROW - 1) * BUTTON_SPACING_X);
        int startX = (GAME_WIDTH - totalButtonsWidth) / 2;
        
        // Créer les boutons en grille (seulement si on a des niveaux)
        for (int i = 0; i < numLevels; i++) {
            // Calculer la position dans la grille
            int row = i / BUTTONS_PER_ROW;  // Ligne (0-indexed)
            int col = i % BUTTONS_PER_ROW;  // Colonne (0-indexed)
            
            // Calculer les coordonnées X et Y du bouton
            int x = startX + (col * (WIDTH + BUTTON_SPACING_X));
            int y = START_Y + (row * (HEIGHT + BUTTON_SPACING_Y));
            
            // Créer le bouton avec l'index du niveau
            levelButtons[i] = new LevelButton(x, y, WIDTH, HEIGHT, 0, i, game);
        }
        
        // Créer le bouton retour (centré horizontalement, en bas de l'écran)
        backButton = new MenuButtons(
            GAME_WIDTH / 2 - WIDTH / 2, 
            BACK_BUTTON_Y, 
            WIDTH, 
            HEIGHT, 
            0, 
            "back", 
            GameState.MENU
        );
        
        // Charger l'image de fond (même que le menu principal)
        backgroundImage = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND);
    }

    /**
     * Dessine le state de sélection de niveau.
     * 
     * @param g Contexte graphique pour le dessin
     */
    @Override
    public void draw(Graphics g) {
        // Dessiner le fond (image ou couleur de fallback)
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, GAME_WIDTH, GAME_HEIGHT, null);
        } else {
            // Couleur de fond de secours si l'image n'est pas disponible
            g.setColor(new Color(30, 52, 62));
            g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        }
        
        // Dessiner le titre centré en haut
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, (int) (16 * SCALE)));
        int titleX = GAME_WIDTH / 2 - g.getFontMetrics().stringWidth(titleString) / 2;
        g.drawString(titleString, titleX, 80);
        
        // Dessiner tous les boutons de niveau
        if (levelButtons != null) {
            for (LevelButton lb : levelButtons) {
                lb.draw(g);
            }
        }
        
        // Dessiner le bouton retour
        if (backButton != null) {
            backButton.draw(g);
        }
    }

    /**
     * Met à jour le state (appelé chaque frame).
     * Met à jour l'état visuel de tous les boutons.
     */
    @Override
    public void update() {
        // Mettre à jour les boutons de niveau (états hover/pressed)
        if (levelButtons != null) {
            for (LevelButton lb : levelButtons) {
                lb.update();
            }
        }
        
        // Mettre à jour le bouton retour
        if (backButton != null) {
            backButton.update();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Aucune action clavier nécessaire pour ce state
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Aucune action clavier nécessaire pour ce state
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Aucune action clavier nécessaire pour ce state
        // La navigation se fait uniquement à la souris
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Réinitialiser l'état hover pour tous les boutons de niveau
        if (levelButtons != null) {
            for (LevelButton lb : levelButtons) {
                lb.setMouseOver(false);
            }
            
            // Vérifier si la souris est sur un bouton de niveau et activer le hover
            for (LevelButton lb : levelButtons) {
                if (isIn(e, lb)) {
                    lb.setMouseOver(true);
                    break; // Un seul bouton peut être en hover à la fois
                }
            }
        }
        
        // Gérer le bouton retour
        if (backButton != null) {
            backButton.setMouseOver(isIn(e, backButton));
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Aucune action de drag nécessaire pour ce state
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Les actions sont gérées dans mouseReleased pour une meilleure UX
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Gérer l'état "pressed" pour les boutons de niveau
        if (levelButtons != null) {
            for (LevelButton lb : levelButtons) {
                if (isIn(e, lb)) {
                    lb.setMousePressed(true);
                    break; // Un seul bouton peut être pressé à la fois
                }
            }
        }
        
        // Gérer l'état "pressed" pour le bouton retour
        if (backButton != null && isIn(e, backButton)) {
            backButton.setMousePressed(true);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Gérer les clics sur les boutons de niveau
        if (levelButtons != null) {
            for (LevelButton lb : levelButtons) {
                if (isIn(e, lb)) {
                    // Exécuter l'action du bouton (change le niveau et lance le jeu)
                    lb.action();
                }
                // Réinitialiser l'état du bouton
                lb.resetBools();
            }
        }
        
        // Gérer le clic sur le bouton retour
        if (backButton != null) {
            if (isIn(e, backButton)) {
                // Retourner au menu principal
                backButton.action();
            }
            // Réinitialiser l'état du bouton
            backButton.resetBools();
        }
    }

    @Override
    public void UpdateStrings() {
        // Mettre à jour le titre avec la traduction actuelle
        titleString = GetPhrase("select_level");
        
        // Mettre à jour le texte du bouton retour
        if (backButton != null) {
            backButton.setText(GetPhrase(backButton.getBaseText()));
        }
    }
}

