package ui.settings;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import static ui.settings.SettingsConfig.SLIDER_BORDER_COLOR;
import static ui.settings.SettingsConfig.SLIDER_DEFAULT_MARKER_COLOR;
import static ui.settings.SettingsConfig.SLIDER_FILL_COLOR;
import static ui.settings.SettingsConfig.SLIDER_FILL_SELECTED_COLOR;
import static ui.settings.SettingsConfig.SLIDER_INDICATOR_COLOR;
import static ui.settings.SettingsConfig.SLIDER_INDICATOR_DRAGGING_COLOR;
import static ui.settings.SettingsConfig.SLIDER_INDICATOR_HOVER_COLOR;
import static ui.settings.SettingsConfig.SLIDER_INDICATOR_SELECTED_COLOR;
import static ui.settings.SettingsConfig.SLIDER_INDICATOR_SIZE;
import static ui.settings.SettingsConfig.SLIDER_TRACK_COLOR;
import static ui.settings.SettingsConfig.SLIDER_TRACK_HEIGHT;
import static ui.settings.SettingsConfig.SLIDER_TRACK_SELECTED_COLOR;

/**
 * Composant slider interactif pour ajuster des valeurs numériques.
 * Supporte les interactions souris (clic + glisser) et clavier (flèches).
 */
public class SettingSlider {
    
    // === PROPRIÉTÉS ===
    private final Rectangle bounds;
    private final float minValue;
    private final float maxValue;
    private float currentValue;
    private final float step;
    private final float defaultValue;
    
    // États visuels
    private boolean isHovered;
    private boolean isDragging;
    private boolean isSelected;
    
    /**
     * Constructeur
     * @param x Position X
     * @param y Position Y
     * @param width Largeur
     * @param height Hauteur
     * @param minValue Valeur minimale
     * @param maxValue Valeur maximale
     * @param defaultValue Valeur par défaut
     * @param step Incrément pour ajustement clavier
     */
    public SettingSlider(int x, int y, int width, int height, 
                        float minValue, float maxValue, float defaultValue, float step) {
        this.bounds = new Rectangle(x, y, width, height);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.defaultValue = defaultValue;
        this.currentValue = defaultValue;
        this.step = step;
        this.isHovered = false;
        this.isDragging = false;
        this.isSelected = false;
    }
    
    /**
     * Dessine le slider
     * @param g Contexte graphique
     */
    public void draw(Graphics g) {
        int sliderX = bounds.x;
        int sliderY = bounds.y;
        int sliderWidth = bounds.width;
        int sliderHeight = bounds.height;
        int trackY = sliderY + sliderHeight / 2 - SLIDER_TRACK_HEIGHT / 2;
        
        // === BARRE DE FOND (track) ===
        Color trackColor = isSelected ? SLIDER_TRACK_SELECTED_COLOR : SLIDER_TRACK_COLOR;
        g.setColor(trackColor);
        g.fillRoundRect(sliderX, trackY, sliderWidth, SLIDER_TRACK_HEIGHT, 3, 3);
        
        // Bordure de la barre
        g.setColor(SLIDER_BORDER_COLOR);
        g.drawRoundRect(sliderX, trackY, sliderWidth, SLIDER_TRACK_HEIGHT, 3, 3);
        
        // === BARRE DE PROGRESSION (fill) ===
        float progress = (currentValue - minValue) / (maxValue - minValue);
        int progressWidth = (int) (sliderWidth * progress);
        
        if (progressWidth > 0) {
            Color fillColor = isSelected ? SLIDER_FILL_SELECTED_COLOR : SLIDER_FILL_COLOR;
            g.setColor(fillColor);
            g.fillRoundRect(sliderX, trackY, progressWidth, SLIDER_TRACK_HEIGHT, 3, 3);
        }
        
        // === INDICATEUR DE VALEUR PAR DÉFAUT ===
        if (Math.abs(defaultValue - currentValue) > 0.001f) {
            float defaultProgress = (defaultValue - minValue) / (maxValue - minValue);
            int defaultX = sliderX + (int) (sliderWidth * defaultProgress);
            g.setColor(SLIDER_DEFAULT_MARKER_COLOR);
            // Ligne verticale fine
            g.fillRect(defaultX - 1, sliderY, 2, sliderHeight);
            // Petit triangle au-dessus
            int[] xPoints = {defaultX, defaultX - 3, defaultX + 3};
            int[] yPoints = {sliderY, sliderY + 5, sliderY + 5};
            g.fillPolygon(xPoints, yPoints, 3);
        }
        
        // === INDICATEUR (curseur) ===
        int indicatorX = sliderX + (int) (sliderWidth * progress) - SLIDER_INDICATOR_SIZE / 2;
        int indicatorY = sliderY + sliderHeight / 2 - SLIDER_INDICATOR_SIZE / 2;
        
        // Déterminer la couleur du curseur selon l'état
        Color indicatorColor;
        if (isDragging) {
            indicatorColor = SLIDER_INDICATOR_DRAGGING_COLOR;
        } else if (isSelected) {
            indicatorColor = SLIDER_INDICATOR_SELECTED_COLOR;
        } else if (isHovered) {
            indicatorColor = SLIDER_INDICATOR_HOVER_COLOR;
        } else {
            indicatorColor = SLIDER_INDICATOR_COLOR;
        }
        
        // Ombre du curseur (effet de profondeur)
        g.setColor(new Color(0, 0, 0, 80));
        g.fillOval(indicatorX + 1, indicatorY + 1, SLIDER_INDICATOR_SIZE, SLIDER_INDICATOR_SIZE);
        
        // Curseur principal
        g.setColor(indicatorColor);
        g.fillOval(indicatorX, indicatorY, SLIDER_INDICATOR_SIZE, SLIDER_INDICATOR_SIZE);
        
        // Bordure du curseur
        Color borderColor = isSelected ? new Color(100, 100, 120) : new Color(180, 180, 200);
        g.setColor(borderColor);
        g.drawOval(indicatorX, indicatorY, SLIDER_INDICATOR_SIZE, SLIDER_INDICATOR_SIZE);
        
        // Point central pour plus de précision
        if (isSelected || isHovered) {
            g.setColor(new Color(80, 80, 100));
            g.fillOval(indicatorX + SLIDER_INDICATOR_SIZE / 2 - 2, 
                      indicatorY + SLIDER_INDICATOR_SIZE / 2 - 2, 4, 4);
        }
        
        // === VALEUR NUMÉRIQUE ===
        // La valeur est maintenant dessinée par SettingsCategory pour éviter le chevauchement
    }
    
    /**
     * Met à jour l'état visuel
     */
    public void update() {
        // L'état est géré par les méthodes handleMouse* et handleKeyboard
    }
    
    /**
     * Gère le clic de souris
     * @param x Position X du clic
     * @param y Position Y du clic
     * @return true si le clic était sur le slider
     */
    public boolean handleMouseClick(int x, int y) {
        if (bounds.contains(x, y)) {
            float newValue = calculateValueFromX(x);
            setValue(newValue);
            isDragging = true;
            return true;
        }
        return false;
    }
    
    /**
     * Gère le glisser de souris
     * @param x Position X actuelle
     * @param y Position Y actuelle
     */
    public void handleMouseDrag(int x, int y) {
        if (isDragging) {
            // Calculer la valeur même si la souris est en dehors des bounds (comme dans le code C)
            // On clamp la position X pour rester dans les limites du slider
            int clampedX = Math.max(bounds.x, Math.min(bounds.x + bounds.width, x));
            float newValue = calculateValueFromX(clampedX);
            setValue(newValue);
        }
    }
    
    /**
     * Vérifie si le slider est en train d'être dragué
     * @return true si en train d'être dragué
     */
    public boolean isDragging() {
        return isDragging;
    }
    
    /**
     * Gère le relâchement de la souris
     */
    public void handleMouseRelease() {
        isDragging = false;
    }
    
    /**
     * Gère le survol de la souris
     * @param x Position X
     * @param y Position Y
     */
    public void handleMouseMove(int x, int y) {
        isHovered = bounds.contains(x, y);
    }
    
    /**
     * Gère les touches du clavier
     * @param keyCode Code de la touche
     * @param shiftPressed Si Shift est pressé (ajustement rapide)
     * @return true si la touche a été traitée
     */
    public boolean handleKeyboard(int keyCode, boolean shiftPressed) {
        if (!isSelected) {
            return false;
        }
        
        float adjustment = shiftPressed ? step * 5 : step;
        
        switch (keyCode) {
            case java.awt.event.KeyEvent.VK_LEFT:
                setValue(currentValue - adjustment);
                return true;
            case java.awt.event.KeyEvent.VK_RIGHT:
                setValue(currentValue + adjustment);
                return true;
            default:
                return false;
        }
    }
    
    /**
     * Définit la valeur (avec clamp automatique et arrondi aux incréments de step)
     * @param value Nouvelle valeur
     */
    public void setValue(float value) {
        // Clamp la valeur entre min et max
        float clampedValue = Math.max(minValue, Math.min(maxValue, value));
        
        // Arrondir à l'incrément de step le plus proche
        if (step > 0) {
            float steps = Math.round((clampedValue - minValue) / step);
            currentValue = minValue + steps * step;
            // Re-clamp après arrondi pour éviter les erreurs d'arrondi
            currentValue = Math.max(minValue, Math.min(maxValue, currentValue));
        } else {
            currentValue = clampedValue;
        }
    }
    
    /**
     * Retourne la valeur actuelle
     * @return Valeur actuelle
     */
    public float getValue() {
        return currentValue;
    }
    
    /**
     * Réinitialise à la valeur par défaut
     */
    public void reset() {
        currentValue = defaultValue;
    }
    
    /**
     * Vérifie si la valeur est à la valeur par défaut
     * @return true si valeur = défaut
     */
    public boolean isDefault() {
        return Math.abs(currentValue - defaultValue) < 0.001f;
    }
    
    /**
     * Définit l'état sélectionné
     * @param selected true si sélectionné
     */
    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }
    
    /**
     * Vérifie si la souris est sur le slider
     * @param x Position X
     * @param y Position Y
     * @return true si survol
     */
    public boolean isMouseOver(int x, int y) {
        return bounds.contains(x, y);
    }
    
    /**
     * Retourne les bounds du slider
     * @return Rectangle des bounds
     */
    public Rectangle getBounds() {
        return bounds;
    }
    
    /**
     * Met à jour la position Y
     * @param newY Nouvelle position Y
     */
    public void setY(int newY) {
        this.bounds.y = newY;
    }
    
    // === MÉTHODES PRIVÉES ===
    
    /**
     * Convertit une position X en valeur
     * @param x Position X
     * @return Valeur correspondante
     */
    private float calculateValueFromX(int x) {
        float relativeX = x - bounds.x;
        float progress = Math.max(0, Math.min(1, relativeX / (float) bounds.width));
        return minValue + progress * (maxValue - minValue);
    }
}

