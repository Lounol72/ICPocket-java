package states;

import game.Game;
import ui.MenuButtons;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import static game.Game.GAME_HEIGHT;
import static game.Game.GAME_WIDTH;
import static utilz.Constants.SCALE;
import static utilz.Constants.UI.BUTTONS.HEIGHT;
import static utilz.Constants.UI.BUTTONS.WIDTH;
import static utilz.HelpMethods.GetPhrase;

public class Menu extends State implements StateMethods{

    // Text position
    private static final int X_TEXT_POS_DEFAULT = GAME_WIDTH /2;
    private static final int Y_TEXT_POS_DEFAULT = (int) (GAME_HEIGHT  * 0.8f);

    // Buttons positions
    private static final int X_BUTTON_POS_DEFAULT = 250;
    private static final int Y_BUTTON_POS_DEFAULT = 160;
    private static final int X_BUTTON_POS = (int) (X_BUTTON_POS_DEFAULT * SCALE);
    private static final int Y_BUTTON_POS = (int) (Y_BUTTON_POS_DEFAULT * SCALE);
    private static final int X_BUTTON_OFFSET = (int)(150 *SCALE) + WIDTH;
    private static final int Y_BUTTON_OFFSET = (int)(15 *SCALE) + HEIGHT;

    MenuButtons[] buttons;
    private String languageString ;
    private int xText, yText ;
    private boolean textPosCalc = false;
    /**
     * Constructor
     * */
    public Menu( Game game){
        super(game);
        initClasses();
    }

    private void initClasses() {
        buttons = new MenuButtons[5];
        String[] lang = {"fr", "en", "ru", "chinois", "ger"};
        String[] text = {"Français","English","Русский", "中文", "Deutsch"};
        for (int i = 0; i < buttons.length ; i++)
            buttons[i] = new MenuButtons(X_BUTTON_POS + ((i % 2) * X_BUTTON_OFFSET), Y_BUTTON_POS + ((i / 2) * Y_BUTTON_OFFSET * 2),WIDTH, HEIGHT,0,lang[i],text[i]);
        languageString = GetPhrase("langue");
    }

    /**
     * @param g
     */
    @Override
    public void draw( Graphics g ) {
        g.setColor(new Color (171, 171, 171));
        g.fillRect(0,0,GAME_WIDTH, GAME_HEIGHT);
        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        if (!textPosCalc){

            FontMetrics fm = g.getFontMetrics();
            xText = X_TEXT_POS_DEFAULT - fm.stringWidth(languageString) / 2;
            yText = Y_TEXT_POS_DEFAULT - (fm.getHeight()) / 2 + fm.getAscent();
            textPosCalc = true;
        }

        g.drawString(languageString ,xText ,yText );
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
            default ->{
                GameState.setState(GameState.BATTLE);
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
                mb.action();
        for (MenuButtons mb : buttons)
            mb.resetBools();
        UpdateStrings();
    }


    private void UpdateStrings (){
        languageString = GetPhrase("langue");
        textPosCalc = false;
        game.UpdateEveryStrings();
    }
}
