package ui.settings;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

import static ui.settings.SettingsConfig.BUTTON_HOVER_COLOR;
import static ui.settings.SettingsConfig.BUTTON_NORMAL_COLOR;
import static ui.settings.SettingsConfig.BUTTON_PRESSED_COLOR;
import static ui.settings.SettingsConfig.BUTTON_TEXT_COLOR;
import static ui.settings.SettingsConfig.FONT_SIZE_BUTTON;
import static utilz.HelpMethods.GetPhrase;

/**
 * Bouton d'action avec callback fonctionnel.
 * Gère les états visuels (normal, hover, pressed).
 */
public class ActionButton {
    
    // === ÉTATS ===
    public enum ButtonState {
        NORMAL,
        HOVER,
        PRESSED
    }
    
    // === PROPRIÉTÉS ===
    private final Rectangle bounds;
    private final String textKey;
    private final Runnable onClickAction;
    private ButtonState state;
    
    /**
     * Constructeur
     * @param x Position X
     * @param y Position Y
     * @param width Largeur
     * @param height Hauteur
     * @param textKey Clé de traduction
     * @param onClickAction Callback à exécuter au clic
     */
    public ActionButton(int x, int y, int width, int height, String textKey, Runnable onClickAction) {
        this.bounds = new Rectangle(x, y, width, height);
        this.textKey = textKey;
        this.onClickAction = onClickAction;
        this.state = ButtonState.NORMAL;
    }
    
    /**
     * Dessine le bouton
     * @param g Contexte graphique
     */
    public void draw(Graphics g) {
        Color bgColor;
        
        bgColor = switch (state) {
            case PRESSED -> BUTTON_PRESSED_COLOR;
            case HOVER -> BUTTON_HOVER_COLOR;
            default -> BUTTON_NORMAL_COLOR;
        };
        
        // Fond
        g.setColor(bgColor);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        
        // Bordure
        g.setColor(bgColor.darker());
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        
        // Texte
        g.setColor(BUTTON_TEXT_COLOR);
        g.setFont(new Font("Arial", Font.BOLD, FONT_SIZE_BUTTON));
        String text = GetPhrase(textKey);
        int textX = bounds.x + bounds.width / 2 - g.getFontMetrics().stringWidth(text) / 2;
        int textY = bounds.y + bounds.height / 2 + g.getFontMetrics().getHeight() / 3;
        g.drawString(text, textX, textY);
    }
    
    /**
     * Met à jour l'état visuel
     */
    public void update() {
        // L'état est géré par handleMouseMove et handleClick
    }
    
    /**
     * Gère le clic de souris
     * @param x Position X du clic
     * @param y Position Y du clic
     * @return true si le clic était sur le bouton
     */
    public boolean handleClick(int x, int y) {
        if (bounds.contains(x, y)) {
            state = ButtonState.PRESSED;
            if (onClickAction != null) {
                onClickAction.run();
            }
            return true;
        }
        return false;
    }
    
    /**
     * Gère le relâchement de la souris
     */
    public void handleRelease() {
        if (state == ButtonState.PRESSED) {
            state = ButtonState.HOVER;
        }
    }
    
    /**
     * Gère le mouvement de la souris
     * @param x Position X
     * @param y Position Y
     */
    public void handleMouseMove(int x, int y) {
        if (bounds.contains(x, y)) {
            if (state != ButtonState.PRESSED) {
                state = ButtonState.HOVER;
            }
        } else {
            state = ButtonState.NORMAL;
        }
    }
    
    /**
     * Vérifie si la souris est sur le bouton
     * @param x Position X
     * @param y Position Y
     * @return true si survol
     */
    public boolean isMouseOver(int x, int y) {
        return bounds.contains(x, y);
    }
    
    /**
     * Retourne les bounds du bouton
     * @return Rectangle des bounds
     */
    public Rectangle getBounds() {
        return bounds;
    }
    
    /**
     * Retourne la clé de traduction
     * @return Clé de traduction
     */
    public String getTextKey() {
        return textKey;
    }
}

