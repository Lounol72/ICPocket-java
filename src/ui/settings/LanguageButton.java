package ui.settings;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

import static ui.settings.SettingsConfig.FONT_SIZE_LABEL;
import static ui.settings.SettingsConfig.TAB_ACTIVE_COLOR;
import static ui.settings.SettingsConfig.TAB_BORDER_COLOR;
import static ui.settings.SettingsConfig.TAB_HOVER_COLOR;
import static ui.settings.SettingsConfig.TAB_INACTIVE_COLOR;
import static ui.settings.SettingsConfig.TEXT_COLOR;
import static ui.settings.SettingsConfig.TEXT_SECONDARY_COLOR;
import static utilz.Constants.SetLanguage;
import static utilz.Constants.language;

/**
 * Bouton de sélection de langue pour les paramètres.
 * Affiche le nom de la langue et change la langue quand cliqué.
 */
public class LanguageButton {
    
    // === PROPRIÉTÉS ===
    private Rectangle bounds;
    private String languageCode; // "fr", "en", "de"
    private String displayName; // "Français", "English", "Deutsch"
    private boolean isSelected;
    private boolean isHovered;
    
    /**
     * Constructeur
     * @param x Position X
     * @param y Position Y
     * @param width Largeur
     * @param height Hauteur
     * @param languageCode Code de langue ("fr", "en", "de")
     * @param displayName Nom d'affichage ("Français", "English", "Deutsch")
     */
    public LanguageButton(int x, int y, int width, int height, String languageCode, String displayName) {
        this.bounds = new Rectangle(x, y, width, height);
        this.languageCode = languageCode;
        this.displayName = displayName;
        this.isSelected = languageCode.equals(language);
        this.isHovered = false;
    }
    
    /**
     * Dessine le bouton de langue
     * @param g Contexte graphique
     */
    public void draw(Graphics g) {
        Color bgColor;
        Color textColor;
        
        if (isSelected) {
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
        g.setFont(new Font("Arial", isSelected ? Font.BOLD : Font.PLAIN, FONT_SIZE_LABEL));
        int textX = bounds.x + bounds.width / 2 - g.getFontMetrics().stringWidth(displayName) / 2;
        int textY = bounds.y + bounds.height / 2 + g.getFontMetrics().getHeight() / 3;
        g.drawString(displayName, textX, textY);
    }
    
    /**
     * Met à jour l'état visuel
     */
    public void update() {
        // Vérifier si cette langue est actuellement sélectionnée
        isSelected = languageCode.equals(language);
    }
    
    /**
     * Gère le clic sur le bouton
     * @param x Position X du clic
     * @param y Position Y du clic
     * @return true si le clic était sur le bouton
     */
    public boolean handleClick(int x, int y) {
        if (bounds.contains(x, y)) {
            SetLanguage(languageCode);
            isSelected = true;
            return true;
        }
        return false;
    }
    
    /**
     * Gère le mouvement de la souris
     * @param x Position X
     * @param y Position Y
     */
    public void handleMouseMove(int x, int y) {
        // Réinitialiser l'état hover si la position est invalide ou si la souris n'est pas sur le bouton
        if (x < 0 || y < 0) {
            isHovered = false;
        } else {
            isHovered = bounds.contains(x, y);
        }
    }
    
    /**
     * Définit l'état hover manuellement
     * @param hovered true si survolé
     */
    public void setHovered(boolean hovered) {
        this.isHovered = hovered;
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
     * Retourne le code de langue
     * @return Code de langue
     */
    public String getLanguageCode() {
        return languageCode;
    }
}

