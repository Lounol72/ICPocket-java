package states;

// Java standard library imports
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import game.Game;
import static states.GameState.MENU;
import ui.StartButtons;
import static utilz.Constants.SCALE;
import static utilz.Constants.UI.BUTTONS.HEIGHT;
import static utilz.Constants.UI.BUTTONS.WIDTH;
import static utilz.Constants.WORLD.GAME_HEIGHT;
import static utilz.Constants.WORLD.GAME_WIDTH;
import utilz.HelpMethods;
import static utilz.HelpMethods.GetPhrase;

public class Start extends State implements StateMethods{

     // Text position
     private static final int X_TEXT_POS_DEFAULT = GAME_WIDTH /2;
     private static final int Y_TEXT_POS_DEFAULT = (int) (GAME_HEIGHT  * 0.8f);
 
     // Buttons positions
     private static final int X_BUTTON_POS = (int) (GAME_WIDTH *0.196);
     private static final int Y_BUTTON_POS = (int) (GAME_HEIGHT *0.174);
     private static final int X_BUTTON_OFFSET = (int)(Y_BUTTON_POS *SCALE) + WIDTH;
     private static final int Y_BUTTON_OFFSET = (int)((GAME_HEIGHT * 0.016) *SCALE) + HEIGHT;
 
     StartButtons[] buttons;
     private String languageString ;
     private int xText, yText ;
     private boolean textPosCalc = false;
 
    // private ScrollingText scrollText;

    public Start(Game game){
        super(game);

        initClasses();

        if (HelpMethods.detect_save()){
            HelpMethods.charger_config();
            UpdateStrings();
            GameState.setState(MENU);
        }
    }

    private void initClasses(){
        buttons = new StartButtons[3];
        String[] lang = {"fr", "en", "de"};
        String[] text = {"Français","English", "Deutsch"};
        for (int i = 0; i < buttons.length ; i++)
            buttons[i] = new StartButtons(X_BUTTON_POS + ((i % 2) * X_BUTTON_OFFSET), Y_BUTTON_POS + ((i / 2) * Y_BUTTON_OFFSET * 2),WIDTH, HEIGHT,0,lang[i],text[i]);
        languageString = GetPhrase("langue");
    }

    @Override
    public void draw(Graphics g) {
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
        for (StartButtons mb : buttons)
            mb.draw(g);
        // scrollText.draw(g);
    }

    @Override
    public void update() {
        for (StartButtons mb : buttons)
            mb.update();
        // scrollText.update();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        for (StartButtons mb : buttons)
            mb.setMouseOver(false);
        for (StartButtons mb : buttons)
            if (isIn(e,mb))
                mb.setMouseOver(true);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Pas de drag nécessaire pour Start
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        for (StartButtons mb : buttons)
        if (isIn(e,mb))
            mb.setMousePressed(true);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        boolean actionPerformed = false;
        for (StartButtons mb : buttons) {
            if (isIn(e,mb)) {
                mb.action();
                actionPerformed = true;
            }
        }
        for (StartButtons mb : buttons)
            mb.resetBools();
        
        if (actionPerformed) {
            UpdateStrings();
        }  
    }

    @Override
    public void UpdateStrings() {
        languageString = GetPhrase("langue");
        // scrollText.reset(GetPhrase("no_very_eff"));
        textPosCalc = false;
        game.UpdateEveryStrings();
    }
    
}
