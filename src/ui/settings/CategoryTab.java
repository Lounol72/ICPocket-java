package ui.settings;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

import static ui.settings.SettingsConfig.FONT_SIZE_CATEGORY;
import static ui.settings.SettingsConfig.TAB_ACTIVE_COLOR;
import static ui.settings.SettingsConfig.TAB_BORDER_COLOR;
import static ui.settings.SettingsConfig.TAB_HOVER_COLOR;
import static ui.settings.SettingsConfig.TAB_INACTIVE_COLOR;
import static ui.settings.SettingsConfig.TEXT_COLOR;
import static ui.settings.SettingsConfig.TEXT_SECONDARY_COLOR;
import static utilz.HelpMethods.GetPhrase;

/**
 * Onglet de catégorie pour la navigation dans les paramètres.
 * Gère les états visuels (actif, hover, inactif).
 */
public class CategoryTab {
    
    // === PROPRIÉTÉS ===
    private final Rectangle bounds;
    private final String textKey;
    private boolean isActive;
    private boolean isHovered;
    
    /**
     * Constructeur
     * @param x Position X
     * @param y Position Y
     * @param width Largeur
     * @param height Hauteur
     * @param textKey Clé de traduction
     */
    public CategoryTab(int x, int y, int width, int height, String textKey) {
        this.bounds = new Rectangle(x, y, width, height);
        this.textKey = textKey;
        this.isActive = false;
        this.isHovered = false;
    }
    
    /**
     * Dessine l'onglet
     * @param g Contexte graphique
     */
    public void draw(Graphics g) {
        Color bgColor;
        Color textColor;
        
        if (isActive) {
            bgColor = TAB_ACTIVE_COLOR;
            textColor = TEXT_COLOR;
        } else if (isHovered) {
            bgColor = TAB_HOVER_COLOR;
            textColor = TEXT_COLOR;
        } else {
            bgColor = TAB_INACTIVE_COLOR;
            textColor = TEXT_SECONDARY_COLOR;
        }
        
        // Fond
        g.setColor(bgColor);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        
        // Bordure
        g.setColor(TAB_BORDER_COLOR);
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        
        // Texte
        g.setColor(textColor);
        g.setFont(new Font("Arial", isActive ? Font.BOLD : Font.PLAIN, FONT_SIZE_CATEGORY));
        String text = GetPhrase(textKey);
        int textX = bounds.x + bounds.width / 2 - g.getFontMetrics().stringWidth(text) / 2;
        int textY = bounds.y + bounds.height / 2 + g.getFontMetrics().getHeight() / 3;
        g.drawString(text, textX, textY);
    }
    
    /**
     * Met à jour l'état visuel
     */
    public void update() {
        // L'état est géré par setActive et isMouseOver
    }
    
    /**
     * Vérifie si la souris est sur l'onglet
     * @param x Position X
     * @param y Position Y
     * @return true si survol
     */
    public boolean isMouseOver(int x, int y) {
        isHovered = bounds.contains(x, y);
        return isHovered;
    }
    
    /**
     * Définit l'état actif
     * @param active true si actif
     */
    public void setActive(boolean active) {
        this.isActive = active;
    }
    
    /**
     * Vérifie si l'onglet est actif
     * @return true si actif
     */
    public boolean isActive() {
        return isActive;
    }
    
    /**
     * Retourne les bounds de l'onglet
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

