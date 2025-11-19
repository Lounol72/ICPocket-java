package ui.settings;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import static ui.settings.SettingsConfig.FONT_SIZE_LABEL;
import static ui.settings.SettingsConfig.FONT_SIZE_VALUE;
import static ui.settings.SettingsConfig.LABEL_X;
import static ui.settings.SettingsConfig.SLIDER_HEIGHT;
import static ui.settings.SettingsConfig.SLIDER_SPACING;
import static ui.settings.SettingsConfig.SLIDER_START_Y;
import static ui.settings.SettingsConfig.TEXT_COLOR;
import static ui.settings.SettingsConfig.VALUE_X;

/**
 * Gère une catégorie de paramètres avec sa collection de SettingItem.
 * Coordonne le rendu et la mise à jour de tous les items de la catégorie.
 */
public class SettingsCategory {
    
    // === PROPRIÉTÉS ===
    private String nameKey;
    private List<SettingItem> items;
    private CategoryTab tab;
    
    /**
     * Constructeur
     * @param nameKey Clé de traduction pour le nom de la catégorie
     * @param tabX Position X de l'onglet
     * @param tabY Position Y de l'onglet
     * @param tabWidth Largeur de l'onglet
     * @param tabHeight Hauteur de l'onglet
     */
    public SettingsCategory(String nameKey, int tabX, int tabY, int tabWidth, int tabHeight) {
        this.nameKey = nameKey;
        this.items = new ArrayList<>();
        this.tab = new CategoryTab(tabX, tabY, tabWidth, tabHeight, nameKey);
    }
    
    /**
     * Dessine tous les items de la catégorie
     * @param g Contexte graphique
     * @param startY Position Y de départ
     */
    public void draw(Graphics g, int startY) {
        int currentY = startY;
        
        for (SettingItem item : items) {
            // Dessiner le label à gauche
            g.setColor(TEXT_COLOR);
            g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, FONT_SIZE_LABEL));
            String label = item.getDisplayName();
            int labelY = currentY + SLIDER_HEIGHT / 2 + g.getFontMetrics().getHeight() / 3;
            g.drawString(label, LABEL_X, labelY);
            
            // Dessiner le slider
            item.getSlider().draw(g);
            
            // Dessiner la valeur à droite du slider
            g.setColor(TEXT_COLOR);
            g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, FONT_SIZE_VALUE));
            String valueStr = String.format("%.3f", item.getValue());
            int valueY = currentY + SLIDER_HEIGHT / 2 + g.getFontMetrics().getHeight() / 3;
            g.drawString(valueStr, VALUE_X, valueY);
            
            // Passer au suivant
            currentY += SLIDER_SPACING;
        }
    }
    
    /**
     * Met à jour tous les items
     * Note: Ne synchronise PAS les valeurs depuis le slider pour permettre
     * les modifications externes des constantes de persister.
     * La synchronisation se fait uniquement lors des interactions actives.
     */
    public void update() {
        for (SettingItem item : items) {
            item.getSlider().update();
            // Synchroniser le slider depuis la valeur actuelle (pour l'affichage)
            // mais ne PAS synchroniser la valeur depuis le slider (évite d'écraser les modifications externes)
            item.updateSlider();
        }
        tab.update();
    }
    
    /**
     * Réinitialise tous les items de la catégorie
     */
    public void resetAll() {
        for (SettingItem item : items) {
            item.reset();
        }
    }
    
    /**
     * Vérifie si des changements ont été faits
     * @return true si au moins un item n'est pas à sa valeur par défaut
     */
    public boolean hasChanges() {
        for (SettingItem item : items) {
            if (!item.isDefault()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Retourne la liste des items
     * @return Liste des SettingItem
     */
    public List<SettingItem> getItems() {
        return items;
    }
    
    /**
     * Ajoute un paramètre à la catégorie
     * @param item SettingItem à ajouter
     */
    public void addItem(SettingItem item) {
        items.add(item);
        // Ajuster la position Y du slider
        int index = items.size() - 1;
        int sliderY = SLIDER_START_Y + index * SLIDER_SPACING;
        item.getSlider().setY(sliderY);
    }
    
    /**
     * Retourne l'onglet associé
     * @return CategoryTab
     */
    public CategoryTab getTab() {
        return tab;
    }
    
    /**
     * Retourne la clé de traduction
     * @return Clé de traduction
     */
    public String getNameKey() {
        return nameKey;
    }
}

