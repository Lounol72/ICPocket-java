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
import static utilz.HelpMethods.GetPhrase;
import utilz.LoadSave;
public class Menu extends State implements StateMethods{

    private String languageString;
    private MenuButtons[] buttons;
    private BufferedImage backgroundImage;
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
            new MenuButtons(GAME_WIDTH / 2 - 100, GAME_HEIGHT / 2 - 50, 200, 50, 0, "start"),
            new MenuButtons(GAME_WIDTH / 2 - 100, GAME_HEIGHT / 2 , 200, 50, 1, "settings"),
            new MenuButtons(GAME_WIDTH / 2 - 100, GAME_HEIGHT / 2 + 50, 200, 50, 2, "quit"),
        };
        
        // Load background image
        backgroundImage = LoadSave.GetSpriteAtlas(LoadSave.UI + "menu_background.jpg");
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
        g.setFont(new Font("Arial", Font.BOLD, (int) (24 * SCALE)));
        g.drawString(languageString, GAME_WIDTH / 2 - g.getFontMetrics().stringWidth(languageString) / 2, 100);
        
        // Draw buttons
        for (MenuButtons mb : buttons)
            mb.draw(g);
    }

    /**
     *
     */
    @Override
    public void update() {
        for (MenuButtons mb : buttons)
            mb.update();
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
        switch (e.getKeyCode()){
            case KeyEvent.VK_A->{
                GameState.setState(GameState.SETTINGS);
            }
            case KeyEvent.VK_Z->{
                GameState.setState(GameState.WORLD);
            }

        }
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
    public void mouseReleased( MouseEvent e ) {
        for (MenuButtons mb : buttons)
            if (isIn(e,mb))
                mb.setMousePressed(false);
    }

    @Override
    public void UpdateStrings() {
        languageString = GetPhrase("menu");
        for (MenuButtons mb : buttons)
            mb.setText(GetPhrase(mb.getText()));
    }
}
