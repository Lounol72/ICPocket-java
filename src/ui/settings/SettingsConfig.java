package ui.settings;

import java.awt.Color;

/**
 * Configuration centralisée pour l'interface des paramètres.
 * Contient toutes les constantes de design : positions, couleurs, tailles, espacements.
 */
public class SettingsConfig {
    
    // === POSITIONS ET TAILLES ===
    
    // Panneau principal
    public static final int PANEL_X = 40;
    public static final int PANEL_Y = 100;
    public static final int PANEL_WIDTH = 800;
    public static final int PANEL_HEIGHT = 600;
    
    // Onglets de catégories
    public static final int TAB_START_X = PANEL_X;
    public static final int TAB_START_Y = PANEL_Y - 40;
    public static final int TAB_WIDTH = 150;
    public static final int TAB_HEIGHT = 35;
    public static final int TAB_SPACING = 10;
    
    // Labels (à gauche du slider)
    public static final int LABEL_X = PANEL_X + 20;
    public static final int LABEL_WIDTH = 180;
    public static final int LABEL_START_Y = PANEL_Y + 40;
    
    // Sliders (après le label)
    public static final int SLIDER_START_X = LABEL_X + LABEL_WIDTH + 20;
    public static final int SLIDER_START_Y = PANEL_Y + 40;
    public static final int SLIDER_WIDTH = 350;
    public static final int SLIDER_HEIGHT = 30;
    public static final int SLIDER_SPACING = 45;
    public static final int SLIDER_INDICATOR_SIZE = 18;
    public static final int SLIDER_TRACK_HEIGHT = 6;
    
    // Valeur (à droite du slider)
    public static final int VALUE_X = SLIDER_START_X + SLIDER_WIDTH + 15;
    public static final int VALUE_WIDTH = 80;
    
    // Boutons d'action
    public static final int BUTTON_WIDTH = 120;
    public static final int BUTTON_HEIGHT = 40;
    public static final int BUTTON_SPACING = 15;
    public static final int BUTTON_START_X = PANEL_X + PANEL_WIDTH - (BUTTON_WIDTH * 3 + BUTTON_SPACING * 2);
    public static final int BUTTON_START_Y = PANEL_Y + PANEL_HEIGHT - BUTTON_HEIGHT - 20;
    
    // Titre
    public static final int TITLE_X = PANEL_X;
    public static final int TITLE_Y = 50;
    
    // Message de confirmation
    public static final int CONFIRM_MESSAGE_X = PANEL_X;
    public static final int CONFIRM_MESSAGE_Y = PANEL_Y + PANEL_HEIGHT + 30;
    
    // === COULEURS ===
    
    // Fond
    public static final Color BACKGROUND_COLOR = new Color(30, 30, 40);
    public static final Color PANEL_COLOR = new Color(40, 40, 50);
    public static final Color PANEL_BORDER_COLOR = new Color(60, 60, 70);
    
    // Texte
    public static final Color TEXT_COLOR = Color.WHITE;
    public static final Color TEXT_SECONDARY_COLOR = new Color(200, 200, 200);
    public static final Color TEXT_DISABLED_COLOR = new Color(120, 120, 120);
    
    // Onglets
    public static final Color TAB_INACTIVE_COLOR = new Color(50, 50, 60);
    public static final Color TAB_ACTIVE_COLOR = new Color(70, 100, 150);
    public static final Color TAB_HOVER_COLOR = new Color(60, 80, 120);
    public static final Color TAB_BORDER_COLOR = new Color(80, 80, 90);
    
    // Sliders
    public static final Color SLIDER_TRACK_COLOR = new Color(50, 50, 60); // Fond de la barre
    public static final Color SLIDER_TRACK_SELECTED_COLOR = new Color(80, 80, 90); // Fond grisé quand sélectionné
    public static final Color SLIDER_FILL_COLOR = new Color(100, 140, 220); // Barre de progression
    public static final Color SLIDER_FILL_SELECTED_COLOR = new Color(120, 120, 140); // Barre grisée quand sélectionné
    public static final Color SLIDER_INDICATOR_COLOR = new Color(200, 200, 220); // Curseur normal
    public static final Color SLIDER_INDICATOR_SELECTED_COLOR = new Color(150, 150, 170); // Curseur grisé quand sélectionné
    public static final Color SLIDER_INDICATOR_HOVER_COLOR = new Color(220, 220, 240); // Curseur au survol
    public static final Color SLIDER_INDICATOR_DRAGGING_COLOR = new Color(255, 255, 255); // Curseur en drag
    public static final Color SLIDER_DEFAULT_MARKER_COLOR = new Color(255, 200, 50); // Marqueur valeur par défaut
    public static final Color SLIDER_BORDER_COLOR = new Color(70, 70, 80); // Bordure de la barre
    
    // Boutons
    public static final Color BUTTON_NORMAL_COLOR = new Color(60, 80, 120);
    public static final Color BUTTON_HOVER_COLOR = new Color(80, 100, 140);
    public static final Color BUTTON_PRESSED_COLOR = new Color(50, 70, 100);
    public static final Color BUTTON_TEXT_COLOR = Color.WHITE;
    
    // Highlight
    public static final Color HIGHLIGHT_COLOR = new Color(100, 140, 220, 100);
    public static final Color SELECTED_BACKGROUND_COLOR = new Color(100, 140, 220, 50);
    
    // Confirmation
    public static final Color CONFIRM_COLOR = new Color(100, 200, 100);
    
    // === TYPOGRAPHIE ===
    
    public static final int FONT_SIZE_TITLE = 28;
    public static final int FONT_SIZE_CATEGORY = 20;
    public static final int FONT_SIZE_LABEL = 16;
    public static final int FONT_SIZE_VALUE = 14;
    public static final int FONT_SIZE_BUTTON = 14;
    public static final int FONT_SIZE_CONFIRM = 16;
    
    // === ESPACEMENTS ===
    
    public static final int MARGIN_SMALL = 10;
    public static final int MARGIN_MEDIUM = 20;
    public static final int MARGIN_LARGE = 40;
    public static final int PADDING = 15;
    
    // === AUTRES ===
    
    public static final int CONFIRM_MESSAGE_DURATION = 2000; // millisecondes
    public static final float SLIDER_STEP_MULTIPLIER = 1.0f; // Multiplicateur pour ajustement rapide (Shift)
}

