package states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import game.Game;
import ui.MenuButtons;
import static utilz.Constants.SCALE;
import static utilz.Constants.WORLD.GAME_HEIGHT;
import static utilz.Constants.WORLD.GAME_WIDTH;
import utilz.HelpMethods;
import static utilz.HelpMethods.GetPhrase;
import utilz.LoadSave;
public class Menu extends State implements StateMethods{

    private String languageString;
    private MenuButtons[] buttons;
    private BufferedImage backgroundImage;
    
    // Message de confirmation/erreur pour la sauvegarde
    private String saveMessage;
    private long saveMessageTime;
    private boolean showSaveMessage;
    private boolean saveSuccess;

    /**
     * Constructor
     * */
    public Menu( Game game){
        super(game);
        initClasses();
        
    }

    private void initClasses() {
        languageString = GetPhrase("menu");
        buttons = new MenuButtons[]{
            new MenuButtons(GAME_WIDTH / 2 - 100, GAME_HEIGHT / 2 - 100, 200, 50, 0, "start", GameState.WORLD),
            new MenuButtons(GAME_WIDTH / 2 - 100, GAME_HEIGHT / 2 - 50, 200, 50, 1, "settings", GameState.SETTINGS),
            new MenuButtons(GAME_WIDTH / 2 - 100, GAME_HEIGHT / 2, 200, 50, 2, "save_game", GameState.MENU),
            new MenuButtons(GAME_WIDTH / 2 - 100, GAME_HEIGHT / 2 + 50, 200, 50, 3, "quit", GameState.QUIT),

        };

        showSaveMessage = false;
        saveSuccess = false;
        
        // Load background image
        backgroundImage = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND);
    }

    /**
     * @param g
     */
    @Override
    public void draw( Graphics g ) {

        
        // Draw background image if available, otherwise fallback to solid color
        if (backgroundImage != null) {
            // Scale background to fit screen
            g.drawImage(backgroundImage, 0, 0, GAME_WIDTH, GAME_HEIGHT, null);
        } else {
            // Fallback background
            g.setColor(new Color(30, 52, 62));
            g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        }
        
        // Draw menu title
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, (int) (16 * SCALE)));
        g.drawString(languageString, GAME_WIDTH / 2 - g.getFontMetrics().stringWidth(languageString) / 2, 100);
        
        // Draw buttons
        for (MenuButtons mb : buttons)
            mb.draw(g);
        
        // Draw save message if visible
        if (showSaveMessage) {
            drawSaveMessage(g);
        }
    }
    
    /**
     * Dessine le message de sauvegarde (succès ou erreur)
     * @param g Contexte graphique
     */
    private void drawSaveMessage(Graphics g) {
        Color messageColor = saveSuccess ? new Color(100, 200, 100) : new Color(200, 100, 100);
        g.setColor(messageColor);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        
        int messageX = GAME_WIDTH / 2 - g.getFontMetrics().stringWidth(saveMessage) / 2;
        int messageY = GAME_HEIGHT / 2 + 120;
        g.drawString(saveMessage, messageX, messageY);
    }

    /**
     *
     */
    @Override
    public void update() {
        for (MenuButtons mb : buttons)
            mb.update();
        
        // Gérer le message de sauvegarde
        if (showSaveMessage) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - saveMessageTime > 2000) { // 2 secondes
                showSaveMessage = false;
            }
        }
    }

    /**
     * @param e
     */
    @Override
    public void keyTyped( KeyEvent e ) {

    }

    /**
     * @param e
     */
    @Override
    public void keyReleased( KeyEvent e ) {

    }

    /**
     * @param e
     */
    @Override
    public void keyPressed( KeyEvent e ) {
        
    }

    /**
     * @param e
     */
    @Override
    public void mouseMoved( MouseEvent e ) {
        for (MenuButtons mb : buttons)
            mb.setMouseOver(false);
        for (MenuButtons mb : buttons)
            if (isIn(e,mb))
                mb.setMouseOver(true);
    }

    /**
     * @param e
     */
    @Override
    public void mouseDragged( MouseEvent e ) {
        // Pas de drag nécessaire pour Menu
    }

    /**
     * @param e
     */
    @Override
    public void mouseClicked( MouseEvent e ) {

    }

    /**
     * @param e
     */
    @Override
    public void mousePressed( MouseEvent e ) {
        for (MenuButtons mb : buttons)
            if (isIn(e,mb))
                mb.setMousePressed(true);

    }

    /**
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        
        for (MenuButtons mb : buttons) {
            if (isIn(e,mb)) {
                // Si c'est le bouton de sauvegarde, appeler save_all() au lieu de changer d'état
                if ("save_game".equals(mb.getBaseText())) {
                    boolean success = HelpMethods.save_all();
                    showSaveMessage(success);
                } else {
                    mb.action();
                }
            }
        }
        for (MenuButtons mb : buttons)
            mb.resetBools();
    }

    /**
     * Affiche un message de sauvegarde (succès ou erreur)
     * @param success true si la sauvegarde a réussi, false sinon
     */
    private void showSaveMessage(boolean success) {
        saveSuccess = success;
        if (success) {
            saveMessage = GetPhrase("save_success");
        } else {
            saveMessage = GetPhrase("save_error");
        }
        saveMessageTime = System.currentTimeMillis();
        showSaveMessage = true;
    }

    @Override
    public void UpdateStrings() {
        languageString = GetPhrase("menu");
        // mettre à jour le texte des boutons
        for (MenuButtons mb : buttons)
            mb.setText(GetPhrase(mb.getBaseText()));
    }
}
