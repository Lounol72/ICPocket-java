package states;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import game.Game;
import ui.MenuButtons;
import static utilz.Constants.UI.BUTTONS.HEIGHT;
import static utilz.Constants.UI.BUTTONS.WIDTH;
import static utilz.Constants.WORLD.GAME_HEIGHT;
import static utilz.Constants.WORLD.GAME_WIDTH;
import static utilz.HelpMethods.GetPhrase;

/**
 * État de pause du jeu.
 * 
 * FONCTIONNALITÉS:
 * - Affiche un overlay semi-transparent avec dégradé
 * - Affiche le texte "PAUSE" avec effet de glow
 * - Affiche un panneau central avec les boutons (Retour, Paramètres, Quitter)
 * - Peut être activé/désactivé avec ESC
 * 
 * ARCHITECTURE:
 * - Le monde WORLD n'est ni mis à jour ni affiché en pause
 * - Overlay avec dégradé et effet de vignette
 * - Panneau central pour les boutons de navigation
 * 
 * @author Lounol72
 * @version 1.0
 */
public class Pause extends State implements StateMethods {

    // === OVERLAY ===
    private static final float OVERLAY_ALPHA = 0.8f; // Opacité de l'overlay augmentée pour meilleur contraste (0.0 = transparent, 1.0 = opaque)

    // === COULEURS (adjusted for sunset background) ===
    private static final Color OVERLAY_COLOR = new Color(0, 0, 0, (int)(OVERLAY_ALPHA * 255)); // Noir semi-transparent
    private static final Color OVERLAY_GRADIENT_START = new Color(40, 20, 10, 200); // Warm brown pour dégradé
    private static final Color OVERLAY_GRADIENT_END = new Color(20, 10, 5, 240); // Dark brown pour dégradé
    private static final Color PAUSE_TEXT_COLOR = new Color(255, 255, 255, 255); // Blanc pur
    private static final Color PAUSE_GLOW_COLOR = new Color(255, 200, 150, 150); // Warm glow pour cohérence avec sunset

    // === OVERLAY DES BOUTONS ===
    private static final int BUTTON_SPACING = 20; // Espacement vertical entre les boutons
    private static final int BUTTONS_PANEL_Y_OFFSET = 100; // Décalage vertical du panneau de boutons par rapport au centre
    private static final int PANEL_WIDTH = 300; // Largeur du panneau central
    private static final int PANEL_HEIGHT = 220; // Hauteur du panneau central
    private static final int PANEL_CORNER_RADIUS = 15; // Rayon des coins arrondis
    private static final Color PANEL_BACKGROUND = new Color(30, 30, 50, 200); // Fond du panneau
    private static final Color PANEL_BORDER = new Color(100, 150, 255, 255); // Bordure du panneau
    
    // === TEXTE ===
    private String pauseText;
    private Font pauseFont;

    // === BOUTONS ===
    private MenuButtons[] buttons;


    /**
     * Constructeur de l'état de pause
     * @param game Instance du jeu
     */
    public Pause(Game game) {
        super(game);
        pauseFont = new Font("Arial", Font.BOLD, 48);
        initClasses();
    }

    private void initClasses() {
        buttons = new MenuButtons[]{
            new MenuButtons(GAME_WIDTH / 2 - WIDTH / 2, GAME_HEIGHT / 2 + BUTTONS_PANEL_Y_OFFSET - HEIGHT - BUTTON_SPACING, WIDTH, HEIGHT, 1, "back", GameState.WORLD),
            new MenuButtons(GAME_WIDTH / 2 - WIDTH / 2, GAME_HEIGHT / 2 + BUTTONS_PANEL_Y_OFFSET, WIDTH, HEIGHT, 2, "settings", GameState.SETTINGS),
            new MenuButtons(GAME_WIDTH / 2 - WIDTH / 2, GAME_HEIGHT / 2 + BUTTONS_PANEL_Y_OFFSET + HEIGHT + BUTTON_SPACING, WIDTH, HEIGHT, 0, "quit", GameState.MENU),
        };
        UpdateStrings();
    }


    /**
     * Dessine l'état de pause.
     * 
     * ORDRE DE RENDU:
     * 1. Dessine l'overlay avec dégradé
     * 2. Applique un effet de vignette
     * 3. Dessine le texte "PAUSE" avec effet de glow
     * 4. Dessine le panneau central
     * 5. Dessine les boutons
     * 
     * @param g Contexte graphique pour le dessin
     */
    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // === ÉTAPE 4: DESSINER L'OVERLAY AVEC DÉGRADÉ ===
        GradientPaint gradient = new GradientPaint(
            0, 0, OVERLAY_GRADIENT_START,
            0, GAME_HEIGHT, OVERLAY_GRADIENT_END
        );
        g2d.setPaint(gradient);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, OVERLAY_ALPHA));
        g2d.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        // Effet de vignette simplifié
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
        g2d.setColor(new Color(0, 0, 0, 40));
        g2d.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); // Réinitialiser

        // === ÉTAPE 5: DESSINER LE TEXTE "PAUSE" AVEC GLOW ===
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setFont(pauseFont);

        // Centrer le texte
        int textWidth = g2d.getFontMetrics().stringWidth(pauseText);
        int textX = (GAME_WIDTH - textWidth) / 2;
        int textY = GAME_HEIGHT / 2 - 50; // Remonter le texte pour laisser place aux boutons

        // Dessiner le glow (couches optimisées)
        g2d.setColor(PAUSE_GLOW_COLOR);
        for (int i = 1; i <= 3; i += 2) { // Seulement 2 itérations (i=1, i=3)
            g2d.drawString(pauseText, textX - i, textY);
            g2d.drawString(pauseText, textX + i, textY);
            g2d.drawString(pauseText, textX, textY - i);
            g2d.drawString(pauseText, textX, textY + i);
        }

        // Dessiner le texte principal
        g2d.setColor(PAUSE_TEXT_COLOR);
        g2d.drawString(pauseText, textX, textY);

        // === DESSINER LE PANNEAU CENTRAL ===
        drawPanel(g);

        // === DESSINER LES BOUTONS ===
        for (MenuButtons mb : buttons)
            mb.draw(g);
    }


    /**
     * Dessine le panneau central derrière les boutons
     * @param g Contexte graphique pour le dessin
     */
    private void drawPanel(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int panelX = GAME_WIDTH / 2 - PANEL_WIDTH / 2;
        int panelY = GAME_HEIGHT / 2 + BUTTONS_PANEL_Y_OFFSET - PANEL_HEIGHT / 2;

        // Dessiner l'ombre du panneau (simplifiée)
        g2d.setColor(new Color(0, 0, 0, 80));
        g2d.fillRoundRect(panelX + 2, panelY + 2, PANEL_WIDTH, PANEL_HEIGHT, PANEL_CORNER_RADIUS, PANEL_CORNER_RADIUS);

        // Dessiner le fond du panneau (couleur unie pour performance)
        g2d.setColor(PANEL_BACKGROUND);
        g2d.fillRoundRect(panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT, PANEL_CORNER_RADIUS, PANEL_CORNER_RADIUS);

        // Bordure simple (sans glow animé pour performance)
        g2d.setColor(PANEL_BORDER);
        g2d.setStroke(new java.awt.BasicStroke(2));
        g2d.drawRoundRect(panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT, PANEL_CORNER_RADIUS, PANEL_CORNER_RADIUS);
        g2d.setStroke(new java.awt.BasicStroke(1)); // Réinitialiser
    }


    @Override
    public void update() {
        for (MenuButtons mb : buttons)
            mb.update();
    }

    /**
     * Gère les entrées clavier en pause.
     * ESC pour reprendre le jeu.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE -> {
                // Reprendre le jeu
                GameState.setState(GameState.WORLD);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Rien à faire
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Rien à faire
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        for (MenuButtons mb : buttons)
            mb.setMouseOver(false);
        for (MenuButtons mb : buttons)
            if (isIn(e, mb))
                mb.setMouseOver(true);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Rien à faire
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Rien à faire
    }

    @Override
    public void mousePressed(MouseEvent e) {
        for (MenuButtons mb : buttons)
            if (isIn(e, mb))
                mb.setMousePressed(true);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        for (MenuButtons mb : buttons) {
            if (isIn(e, mb)) {
                mb.action();
            }
        }
        for (MenuButtons mb : buttons)
            mb.resetBools();
    }

    @Override
    public void UpdateStrings() {
        pauseText = GetPhrase("pause");
        // mettre à jour le texte des boutons
        for (MenuButtons mb : buttons)
            mb.setText(GetPhrase(mb.getBaseText()));
    }
}

