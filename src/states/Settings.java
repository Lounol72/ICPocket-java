package states;

// Java standard library imports
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import game.Game;
import ui.settings.ActionButton;
import ui.settings.DefaultValues;
import ui.settings.LanguageButton;
import ui.settings.SettingItem;
import ui.settings.SettingSlider;
import ui.settings.SettingsCategory;
import ui.settings.SettingsConfig;
import utilz.Constants;
import static utilz.Constants.WORLD.GAME_HEIGHT;
import static utilz.Constants.WORLD.GAME_WIDTH;
import static utilz.HelpMethods.GetPhrase;
import static utilz.HelpMethods.save_config;

/**
 * État des paramètres avec architecture orientée objet moderne.
 * Utilise des composants UI réutilisables pour une meilleure extensibilité.
 */
public class Settings extends State implements StateMethods {

    // === ÉTATS DE NAVIGATION ===
    private enum SettingsState {
        BROWSING,    // Navigation normale
        EDITING,     // Édition active d'un slider
        CONFIRMING   // Confirmation d'action
    }

    // === PROPRIÉTÉS ===
    private String languageString;
    
    // Composants UI
    private List<SettingsCategory> categories;
    private List<ActionButton> actionButtons;
    private List<LanguageButton> languageButtons;
    
    // État de navigation
    private SettingsCategory selectedCategory;
    private SettingItem selectedItem;
    private SettingSlider activeSlider;
    private SettingsState currentState;
    
    // Message de confirmation
    private String confirmMessage;
    private long confirmMessageTime;
    private boolean showConfirmMessage;

    /**
     * Constructeur
     * @param game Instance du jeu
     */
    public Settings(Game game) {
        super(game);
        initClasses();
    }

    /**
     * Initialise tous les composants UI
     */
    private void initClasses() {
        languageString = GetPhrase("settings");
        categories = new ArrayList<>();
        actionButtons = new ArrayList<>();
        languageButtons = new ArrayList<>();
        currentState = SettingsState.BROWSING;
        showConfirmMessage = false;
        
        // Créer les boutons de langue
        createLanguageButtons();
        
        // Créer les catégories et leurs paramètres
        createCategories();
        
        // Créer les boutons d'action
        createActionButtons();
        
        // Sélectionner la première catégorie par défaut
        if (!categories.isEmpty()) {
            selectedCategory = categories.get(0);
            selectedCategory.getTab().setActive(true);
            if (!selectedCategory.getItems().isEmpty()) {
                selectedItem = selectedCategory.getItems().get(0);
                activeSlider = selectedItem.getSlider();
                activeSlider.setSelected(true);
            }
        }
    }

    /**
     * Crée toutes les catégories et leurs paramètres
     */
    private void createCategories() {
        int tabX = SettingsConfig.TAB_START_X;
        int tabY = SettingsConfig.TAB_START_Y; // Déjà ajusté dans SettingsConfig
        
        // === CATÉGORIE GAMEPLAY ===
        SettingsCategory gameplayCategory = new SettingsCategory(
            "settings_gameplay", 
            tabX, tabY, 
            SettingsConfig.TAB_WIDTH, 
            SettingsConfig.TAB_HEIGHT
        );
        
        // Acceleration
        gameplayCategory.addItem(new SettingItem(
            "settings_acceleration",
            () -> Constants.PLAYER.ACCELERATION,
            (v) -> Constants.PLAYER.ACCELERATION = v,
            0f, 5f,
            DefaultValues.getDefaultAcceleration(),
            0.01f,
            SettingsConfig.SLIDER_START_X,
            SettingsConfig.SLIDER_START_Y,
            SettingsConfig.SLIDER_WIDTH,
            SettingsConfig.SLIDER_HEIGHT
        ));
        
        // Max Speed X
        gameplayCategory.addItem(new SettingItem(
            "settings_max_speed_x",
            () -> Constants.PLAYER.MAX_SPEED_X,
            (v) -> Constants.PLAYER.MAX_SPEED_X = v,
            0.1f, 50f,
            DefaultValues.getDefaultMaxSpeedX(),
            0.1f,
            SettingsConfig.SLIDER_START_X,
            SettingsConfig.SLIDER_START_Y + SettingsConfig.SLIDER_SPACING,
            SettingsConfig.SLIDER_WIDTH,
            SettingsConfig.SLIDER_HEIGHT
        ));
        
        // Jump Force
        gameplayCategory.addItem(new SettingItem(
            "settings_jump_force",
            () -> Constants.PLAYER.JUMP_FORCE,
            (v) -> Constants.PLAYER.JUMP_FORCE = v,
            -100f, 100f,
            DefaultValues.getDefaultJumpForce(),
            0.1f,
            SettingsConfig.SLIDER_START_X,
            SettingsConfig.SLIDER_START_Y + SettingsConfig.SLIDER_SPACING * 2,
            SettingsConfig.SLIDER_WIDTH,
            SettingsConfig.SLIDER_HEIGHT
        ));
        
        // Gravity
        gameplayCategory.addItem(new SettingItem(
            "settings_gravity",
            () -> Constants.PLAYER.GRAVITY,
            (v) -> Constants.PLAYER.GRAVITY = v,
            -5f, 50f,
            DefaultValues.getDefaultGravity(),
            0.01f,
            SettingsConfig.SLIDER_START_X,
            SettingsConfig.SLIDER_START_Y + SettingsConfig.SLIDER_SPACING * 3,
            SettingsConfig.SLIDER_WIDTH,
            SettingsConfig.SLIDER_HEIGHT
        ));
        
        // Dash Speed
        gameplayCategory.addItem(new SettingItem(
            "settings_dash_speed",
            () -> Constants.PLAYER.DASH_SPEED,
            (v) -> Constants.PLAYER.DASH_SPEED = v,
            0f, 500f,
            DefaultValues.getDefaultDashSpeed(),
            1f,
            SettingsConfig.SLIDER_START_X,
            SettingsConfig.SLIDER_START_Y + SettingsConfig.SLIDER_SPACING * 4,
            SettingsConfig.SLIDER_WIDTH,
            SettingsConfig.SLIDER_HEIGHT
        ));
        
        categories.add(gameplayCategory);
        tabX += SettingsConfig.TAB_WIDTH + SettingsConfig.TAB_SPACING;
        
        // === CATÉGORIE ADVANCED PHYSICS ===
        SettingsCategory advancedCategory = new SettingsCategory(
            "settings_advanced",
            tabX, tabY,
            SettingsConfig.TAB_WIDTH,
            SettingsConfig.TAB_HEIGHT
        );
        
        // Air Resistance
        advancedCategory.addItem(new SettingItem(
            "settings_air_resistance",
            () -> Constants.PLAYER.AIR_RESISTANCE,
            (v) -> Constants.PLAYER.AIR_RESISTANCE = v,
            0.5f, 1.0f,
            DefaultValues.getDefaultAirResistance(),
            0.01f,
            SettingsConfig.SLIDER_START_X,
            SettingsConfig.SLIDER_START_Y,
            SettingsConfig.SLIDER_WIDTH,
            SettingsConfig.SLIDER_HEIGHT
        ));
        
        // Ground Friction
        advancedCategory.addItem(new SettingItem(
            "settings_ground_friction",
            () -> Constants.PLAYER.GROUND_FRICTION,
            (v) -> Constants.PLAYER.GROUND_FRICTION = v,
            0.5f, 1.0f,
            DefaultValues.getDefaultGroundFriction(),
            0.01f,
            SettingsConfig.SLIDER_START_X,
            SettingsConfig.SLIDER_START_Y + SettingsConfig.SLIDER_SPACING,
            SettingsConfig.SLIDER_WIDTH,
            SettingsConfig.SLIDER_HEIGHT
        ));
        
        // Fast Fall Multiplier
        advancedCategory.addItem(new SettingItem(
            "settings_fast_fall",
            () -> Constants.PLAYER.FAST_FALL_MULT,
            (v) -> Constants.PLAYER.FAST_FALL_MULT = v,
            1.0f, 2.0f,
            DefaultValues.getDefaultFastFallMultiplier(),
            0.05f,
            SettingsConfig.SLIDER_START_X,
            SettingsConfig.SLIDER_START_Y + SettingsConfig.SLIDER_SPACING * 2,
            SettingsConfig.SLIDER_WIDTH,
            SettingsConfig.SLIDER_HEIGHT
        ));
        
        // Coyote Time Frames
        advancedCategory.addItem(new SettingItem(
            "settings_coyote_time",
            () -> (float) Constants.PLAYER.COYOTE_TIME_FRAMES,
            (v) -> Constants.PLAYER.COYOTE_TIME_FRAMES = Math.round(v),
            0f, 10f,
            DefaultValues.getDefaultCoyoteTimeFrames(),
            1f,
            SettingsConfig.SLIDER_START_X,
            SettingsConfig.SLIDER_START_Y + SettingsConfig.SLIDER_SPACING * 3,
            SettingsConfig.SLIDER_WIDTH,
            SettingsConfig.SLIDER_HEIGHT
        ));
        
        // Apex Gravity Multiplier
        advancedCategory.addItem(new SettingItem(
            "settings_apex_gravity",
            () -> Constants.PLAYER.APEX_GRAVITY_MULT,
            (v) -> Constants.PLAYER.APEX_GRAVITY_MULT = v,
            0.1f, 1.0f,
            DefaultValues.getDefaultApexGravityMultiplier(),
            0.05f,
            SettingsConfig.SLIDER_START_X,
            SettingsConfig.SLIDER_START_Y + SettingsConfig.SLIDER_SPACING * 4,
            SettingsConfig.SLIDER_WIDTH,
            SettingsConfig.SLIDER_HEIGHT
        ));
        
        // Apex Acceleration Multiplier
        advancedCategory.addItem(new SettingItem(
            "settings_apex_accel",
            () -> Constants.PLAYER.APEX_ACCEL_MULT,
            (v) -> Constants.PLAYER.APEX_ACCEL_MULT = v,
            1.0f, 3.0f,
            DefaultValues.getDefaultApexAccelerationMultiplier(),
            0.1f,
            SettingsConfig.SLIDER_START_X,
            SettingsConfig.SLIDER_START_Y + SettingsConfig.SLIDER_SPACING * 5,
            SettingsConfig.SLIDER_WIDTH,
            SettingsConfig.SLIDER_HEIGHT
        ));
        
        categories.add(advancedCategory);
        tabX += SettingsConfig.TAB_WIDTH + SettingsConfig.TAB_SPACING;
        
        // === CATÉGORIE PERFORMANCE ===
        SettingsCategory performanceCategory = new SettingsCategory(
            "settings_performance",
            tabX, tabY,
            SettingsConfig.TAB_WIDTH,
            SettingsConfig.TAB_HEIGHT
        );
        
        // Target FPS
        performanceCategory.addItem(new SettingItem(
            "settings_target_fps",
            () -> (float) Constants.PERFORMANCE.TARGET_FPS,
            (v) -> {
                // Clamper la valeur entre MIN_FPS et MAX_FPS
                int fps = Math.round(v);
                fps = Math.max(Constants.PERFORMANCE.MIN_FPS, Math.min(Constants.PERFORMANCE.MAX_FPS, fps));
                Constants.PERFORMANCE.TARGET_FPS = fps;
            },
            (float) Constants.PERFORMANCE.MIN_FPS,
            (float) Constants.PERFORMANCE.MAX_FPS,
            DefaultValues.getDefaultFPS(),
            1f, // Step de 1 FPS
            SettingsConfig.SLIDER_START_X,
            SettingsConfig.SLIDER_START_Y,
            SettingsConfig.SLIDER_WIDTH,
            SettingsConfig.SLIDER_HEIGHT
        ));
        
        categories.add(performanceCategory);
        tabX += SettingsConfig.TAB_WIDTH + SettingsConfig.TAB_SPACING;
        
        // === CATÉGORIE DEBUG ===
        // Note: Pour les booléens, on pourrait créer un ToggleButton plus tard
        // Pour l'instant, on les laisse de côté ou on les gère différemment
    }

    /**
     * Crée les boutons de sélection de langue
     */
    private void createLanguageButtons() {
        String[] langCodes = {"fr", "en", "de"};
        String[] langNames = {"Français", "English", "Deutsch"};
        
        for (int i = 0; i < langCodes.length; i++) {
            languageButtons.add(new LanguageButton(
                SettingsConfig.LANGUAGE_BUTTON_START_X + i * (SettingsConfig.LANGUAGE_BUTTON_WIDTH + SettingsConfig.LANGUAGE_BUTTON_SPACING),
                SettingsConfig.LANGUAGE_BUTTON_START_Y,
                SettingsConfig.LANGUAGE_BUTTON_WIDTH,
                SettingsConfig.LANGUAGE_BUTTON_HEIGHT,
                langCodes[i],
                langNames[i]
            ));
        }
    }

    /**
     * Crée les boutons d'action
     */
    private void createActionButtons() {
        int buttonX = SettingsConfig.BUTTON_START_X;
        int buttonY = SettingsConfig.BUTTON_START_Y;
        
        // Bouton Apply
        actionButtons.add(new ActionButton(
            buttonX, buttonY,
            SettingsConfig.BUTTON_WIDTH, SettingsConfig.BUTTON_HEIGHT,
            "settings_apply",
            this::applySettings
        ));
        
        buttonX += SettingsConfig.BUTTON_WIDTH + SettingsConfig.BUTTON_SPACING;
        
        // Bouton Cancel
        actionButtons.add(new ActionButton(
            buttonX, buttonY,
            SettingsConfig.BUTTON_WIDTH, SettingsConfig.BUTTON_HEIGHT,
            "settings_cancel",
            this::cancelSettings
        ));
        
        buttonX += SettingsConfig.BUTTON_WIDTH + SettingsConfig.BUTTON_SPACING;
        
        // Bouton Reset All
        actionButtons.add(new ActionButton(
            buttonX, buttonY,
            SettingsConfig.BUTTON_WIDTH, SettingsConfig.BUTTON_HEIGHT,
            "settings_reset_all",
            this::resetAllSettings
        ));
    }

    /**
     * Dessine l'interface
     * @param g Contexte graphique
     */
    @Override
    public void draw(Graphics g) {
        // Fond
        drawBackground(g);
        
        // Titre
        drawTitle(g);
        
        // Panneau principal
        drawPanel(g);
        
        // Onglets de catégories
        drawCategoryTabs(g);
        
        // Paramètres de la catégorie active
        if (selectedCategory != null) {
            drawSettingsPanel(g);
        }
        
        // Boutons de langue (en dessous des sliders)
        drawLanguageButtons(g);
        
        // Boutons d'action
        drawActionButtons(g);
        
        // Message de confirmation
        if (showConfirmMessage) {
            drawConfirmMessage(g);
        }
    }

    /**
     * Dessine le fond
     * @param g Contexte graphique
     */
    private void drawBackground(Graphics g) {
        g.setColor(SettingsConfig.BACKGROUND_COLOR);
        g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
    }

    /**
     * Dessine le titre
     * @param g Contexte graphique
     */
    private void drawTitle(Graphics g) {
        g.setColor(SettingsConfig.TEXT_COLOR);
        g.setFont(new Font("Arial", Font.BOLD, SettingsConfig.FONT_SIZE_TITLE));
        g.drawString(languageString, SettingsConfig.TITLE_X, SettingsConfig.TITLE_Y);
    }

    /**
     * Dessine le panneau principal
     * @param g Contexte graphique
     */
    private void drawPanel(Graphics g) {
        // Fond du panneau
        g.setColor(SettingsConfig.PANEL_COLOR);
        g.fillRect(SettingsConfig.PANEL_X, SettingsConfig.PANEL_Y,
                   SettingsConfig.PANEL_WIDTH, SettingsConfig.PANEL_HEIGHT);
        
        // Bordure
        g.setColor(SettingsConfig.PANEL_BORDER_COLOR);
        g.drawRect(SettingsConfig.PANEL_X, SettingsConfig.PANEL_Y,
                   SettingsConfig.PANEL_WIDTH, SettingsConfig.PANEL_HEIGHT);
    }

    /**
     * Dessine les boutons de langue
     * @param g Contexte graphique
     */
    private void drawLanguageButtons(Graphics g) {
        // Label "Langue"
        g.setColor(SettingsConfig.TEXT_COLOR);
        g.setFont(new Font("Arial", Font.BOLD, SettingsConfig.FONT_SIZE_LABEL));
        String label = GetPhrase("settings_language");
        int labelX = SettingsConfig.LABEL_X;
        int labelY = SettingsConfig.LANGUAGE_BUTTON_START_Y + SettingsConfig.FONT_SIZE_LABEL;
        g.drawString(label, labelX, labelY);
        
        // Boutons de langue
        for (LanguageButton button : languageButtons) {
            button.draw(g);
        }
    }

    /**
     * Dessine les onglets de catégories
     * @param g Contexte graphique
     */
    private void drawCategoryTabs(Graphics g) {
        for (SettingsCategory category : categories) {
            category.getTab().draw(g);
        }
    }

    /**
     * Dessine le panneau de paramètres
     * @param g Contexte graphique
     */
    private void drawSettingsPanel(Graphics g) {
        selectedCategory.draw(g, SettingsConfig.SLIDER_START_Y);
    }

    /**
     * Dessine les boutons d'action
     * @param g Contexte graphique
     */
    private void drawActionButtons(Graphics g) {
        for (ActionButton button : actionButtons) {
            button.draw(g);
        }
    }

    /**
     * Dessine le message de confirmation
     * @param g Contexte graphique
     */
    private void drawConfirmMessage(Graphics g) {
        g.setColor(SettingsConfig.CONFIRM_COLOR);
        g.setFont(new Font("Arial", Font.BOLD, SettingsConfig.FONT_SIZE_CONFIRM));
        g.drawString(confirmMessage, 
                    SettingsConfig.CONFIRM_MESSAGE_X, 
                    SettingsConfig.CONFIRM_MESSAGE_Y);
    }

    /**
     * Met à jour l'état
     */
    @Override
    public void update() {
        // Mettre à jour toutes les catégories
        for (SettingsCategory category : categories) {
            category.update();
        }
        
        // Mettre à jour les boutons de langue
        for (LanguageButton button : languageButtons) {
            button.update();
        }
        
        // Mettre à jour les boutons d'action
        for (ActionButton button : actionButtons) {
            button.update();
        }
        
        // Gérer le message de confirmation
        if (showConfirmMessage) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - confirmMessageTime > SettingsConfig.CONFIRM_MESSAGE_DURATION) {
                showConfirmMessage = false;
            }
        }
    }

    /**
     * Gère les touches pressées
     * @param e Événement clavier
     */
    @Override
    public void keyPressed(KeyEvent e) {
        boolean shiftPressed = e.isShiftDown();
        
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
            case KeyEvent.VK_A:
                cancelSettings();
                break;
                
            case KeyEvent.VK_TAB:
                if (e.isShiftDown()) {
                    selectPreviousCategory();
                } else {
                    selectNextCategory();
                }
                break;
                
            case KeyEvent.VK_UP:
                selectPreviousItem();
                break;
                
            case KeyEvent.VK_DOWN:
                selectNextItem();
                break;
                
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                if (activeSlider != null && selectedItem != null) {
                    activeSlider.handleKeyboard(e.getKeyCode(), shiftPressed);
                    // Synchroniser la valeur lors de l'ajustement clavier
                    selectedItem.updateFromSlider();
                }
                break;
                
            case KeyEvent.VK_R:
                if (selectedItem != null) {
                    selectedItem.reset();
                    showConfirm(GetPhrase("settings_reset") + ": " + selectedItem.getDisplayName());
                }
                break;
                
            case KeyEvent.VK_ENTER:
                applySettings();
                break;
        }
    }

    /**
     * Sélectionne la catégorie suivante
     */
    private void selectNextCategory() {
        if (categories.isEmpty()) return;
        
        int currentIndex = categories.indexOf(selectedCategory);
        int nextIndex = (currentIndex + 1) % categories.size();
        
        selectedCategory.getTab().setActive(false);
        selectedCategory = categories.get(nextIndex);
        selectedCategory.getTab().setActive(true);
        
        // Sélectionner le premier item de la nouvelle catégorie
        if (!selectedCategory.getItems().isEmpty()) {
            selectItem(selectedCategory.getItems().get(0));
        }
    }

    /**
     * Sélectionne la catégorie précédente
     */
    private void selectPreviousCategory() {
        if (categories.isEmpty()) return;
        
        int currentIndex = categories.indexOf(selectedCategory);
        int prevIndex = (currentIndex - 1 + categories.size()) % categories.size();
        
        selectedCategory.getTab().setActive(false);
        selectedCategory = categories.get(prevIndex);
        selectedCategory.getTab().setActive(true);
        
        // Sélectionner le premier item de la nouvelle catégorie
        if (!selectedCategory.getItems().isEmpty()) {
            selectItem(selectedCategory.getItems().get(0));
        }
    }

    /**
     * Sélectionne l'item suivant
     */
    private void selectNextItem() {
        if (selectedCategory == null || selectedCategory.getItems().isEmpty()) return;
        
        int currentIndex = selectedCategory.getItems().indexOf(selectedItem);
        int nextIndex = (currentIndex + 1) % selectedCategory.getItems().size();
        
        selectItem(selectedCategory.getItems().get(nextIndex));
    }

    /**
     * Sélectionne l'item précédent
     */
    private void selectPreviousItem() {
        if (selectedCategory == null || selectedCategory.getItems().isEmpty()) return;
        
        int currentIndex = selectedCategory.getItems().indexOf(selectedItem);
        int prevIndex = (currentIndex - 1 + selectedCategory.getItems().size()) % selectedCategory.getItems().size();
        
        selectItem(selectedCategory.getItems().get(prevIndex));
    }

    /**
     * Sélectionne un item
     * @param item Item à sélectionner
     */
    private void selectItem(SettingItem item) {
        if (activeSlider != null) {
            activeSlider.setSelected(false);
        }
        
        selectedItem = item;
        activeSlider = item.getSlider();
        activeSlider.setSelected(true);
    }

    /**
     * Gère le mouvement de la souris
     * @param e Événement souris
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        
        // Mettre à jour les boutons de langue et réinitialiser l'état de survol
        for (LanguageButton button : languageButtons) {
            boolean isOver = button.isMouseOver(x, y);
            button.setHovered(isOver);
        }
        
        // Mettre à jour les onglets
        for (SettingsCategory category : categories) {
            category.getTab().isMouseOver(x, y);
        }
        
        // Mettre à jour les sliders (hover uniquement, pas le drag)
        if (selectedCategory != null) {
            for (SettingItem item : selectedCategory.getItems()) {
                SettingSlider slider = item.getSlider();
                slider.handleMouseMove(x, y);
            }
        }
        
        // Mettre à jour les boutons d'action
        for (ActionButton button : actionButtons) {
            button.handleMouseMove(x, y);
        }
    }

    /**
     * Gère le glisser de souris (drag)
     * Inspiré du code C : SDL_MOUSEMOTION avec slider->dragging == 1
     * @param e Événement souris
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        
        // Gérer le drag des sliders (comme dans le code C : SDL_MOUSEMOTION avec dragging == 1)
        if (selectedCategory != null) {
            for (SettingItem item : selectedCategory.getItems()) {
                SettingSlider slider = item.getSlider();
                // Si le slider est en train d'être dragué, mettre à jour la valeur
                // On continue le drag même si la souris sort des bounds (comme dans le code C)
                if (slider.isDragging()) {
                    slider.handleMouseDrag(x, y);
                    item.updateFromSlider();
                }
            }
        }
    }

    /**
     * Gère le clic de souris
     * @param e Événement souris
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        // Géré par mousePressed/mouseReleased
    }

    /**
     * Gère l'appui de souris
     * @param e Événement souris
     */
    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        
        // Vérifier les boutons de langue
        for (LanguageButton button : languageButtons) {
            if (button.handleClick(x, y)) {
                // Langue changée, mettre à jour tous les textes
                UpdateStrings();
                game.UpdateEveryStrings();
                return;
            }
        }
        
        // Vérifier les onglets
        for (SettingsCategory category : categories) {
            if (category.getTab().isMouseOver(x, y)) {
                // Changer de catégorie
                if (selectedCategory != null) {
                    selectedCategory.getTab().setActive(false);
                }
                selectedCategory = category;
                category.getTab().setActive(true);
                
                // Sélectionner le premier item
                if (!category.getItems().isEmpty()) {
                    selectItem(category.getItems().get(0));
                }
                return;
            }
        }
        
        // Vérifier les sliders
        if (selectedCategory != null) {
            for (SettingItem item : selectedCategory.getItems()) {
                SettingSlider slider = item.getSlider();
                if (slider.handleMouseClick(x, y)) {
                    // Arrêter le drag de tous les autres sliders pour garantir qu'un seul est actif
                    for (SettingItem otherItem : selectedCategory.getItems()) {
                        if (otherItem != item) {
                            otherItem.getSlider().handleMouseRelease();
                        }
                    }
                    selectItem(item);
                    // Synchroniser la valeur lors du clic initial
                    item.updateFromSlider();
                    return;
                }
            }
        }
        
        // Vérifier les boutons
        for (ActionButton button : actionButtons) {
            button.handleClick(x, y);
        }
    }

    /**
     * Gère le relâchement de souris
     * @param e Événement souris
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        // Relâcher le drag des sliders
        if (selectedCategory != null) {
            for (SettingItem item : selectedCategory.getItems()) {
                item.getSlider().handleMouseRelease();
            }
        }
        
        // Relâcher les boutons
        for (ActionButton button : actionButtons) {
            button.handleRelease();
        }
    }

    /**
     * Applique les paramètres et sauvegarde
     */
    private void applySettings() {
        // Sauvegarder la configuration
        boolean success = save_config();
        if (success) {
            showConfirm(GetPhrase("settings_changes_applied"));
        } else {
            showConfirm(GetPhrase("save_error"));
        }
    }

    /**
     * Annule et retourne au menu
     * Recharge la configuration sauvegardée pour annuler les modifications
     */
    private void cancelSettings() {
        // Recharger la configuration sauvegardée pour annuler les modifications
        if (utilz.HelpMethods.detect_save()) {
            utilz.HelpMethods.charger_config();
            // Mettre à jour tous les sliders avec les valeurs rechargées
            for (SettingsCategory category : categories) {
                for (SettingItem item : category.getItems()) {
                    item.updateSlider();
                }
            }
        }
        GameState.setState(GameState.MENU);
    }

    /**
     * Réinitialise tous les paramètres
     */
    private void resetAllSettings() {
        for (SettingsCategory category : categories) {
            category.resetAll();
        }
        showConfirm(GetPhrase("settings_reset_all"));
    }

    /**
     * Affiche un message de confirmation
     * @param message Message à afficher
     */
    private void showConfirm(String message) {
        confirmMessage = message;
        confirmMessageTime = System.currentTimeMillis();
        showConfirmMessage = true;
    }

    /**
     * Gère les touches relâchées
     * @param e Événement clavier
     */
    @Override
    public void keyReleased(KeyEvent e) {
        // Pas d'action nécessaire
    }

    /**
     * Gère les touches tapées
     * @param e Événement clavier
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // Pas d'action nécessaire
    }

    /**
     * Met à jour les chaînes de traduction
     */
    public void UpdateStrings() {
        languageString = GetPhrase("settings");
        // Les autres traductions sont gérées dynamiquement via GetPhrase dans les composants
    }
}
