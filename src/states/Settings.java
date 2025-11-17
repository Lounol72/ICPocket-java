package states;

// Java standard library imports
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import game.Game;
import utilz.Constants;
import static utilz.Constants.WORLD.GAME_HEIGHT;
import static utilz.Constants.WORLD.GAME_WIDTH;
import static utilz.HelpMethods.GetPhrase;

public class Settings extends State implements StateMethods {

    private String languageString;

    // UI for adjusting movement parameters
    private final String[] options = new String[] { "Acceleration", "Max Speed X", "Jump Force", "Gravity",
            "Dash Speed" };
    private int selected = 0;

    public Settings(Game game) {
        super(game);
        initClasses();
    }

    private void initClasses() {
        languageString = GetPhrase("settings");
    }

    /**
     * @param g
     */
    @Override
    public void draw(Graphics g) {
        // Background
        g.setColor(new Color(30, 30, 40));
        g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.drawString(languageString, 40, 50);

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Utilise UP/DOWN pour sélectionner le paramètre et LEFT/RIGHT pour modifié la valeur", 40, 80);

        // Draw options and values
        int startY = 120;
        for (int i = 0; i < options.length; i++) {
            int y = startY + i * 40;
            if (i == selected) {
                g.setColor(new Color(100, 140, 220));
                g.fillRect(30, y - 24, 500, 30);
                g.setColor(Color.BLACK);
            } else {
                g.setColor(Color.WHITE);
            }
            g.drawString(options[i], 40, y);

            String val = getValueString(i);
            g.drawString(val, 320, y);
        }
    }

    private String getValueString(int idx) {
        switch (idx) {
            case 0:
                return String.format("%.3f", Constants.PLAYER.ACCELERATION);
            case 1:
                return String.format("%.3f", Constants.PLAYER.MAX_SPEED_X);
            case 2:
                return String.format("%.3f", Constants.PLAYER.JUMP_FORCE);
            case 3:
                return String.format("%.3f", Constants.PLAYER.GRAVITY);
            case 4:
                return String.format("%.3f", Constants.PLAYER.DASH_SPEED);
            default:
                return "";
        }
    }

    /**
     *
     */
    @Override
    public void update() {

    }

    /**
     * @param e
     */
    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * @param e
     */
    @Override
    public void keyReleased(KeyEvent e) {

    }

    /**
     * @param e
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A -> {
                GameState.setState(GameState.MENU);
            }
            case KeyEvent.VK_UP -> {
                selected = (selected - 1 + options.length) % options.length;
            }
            case KeyEvent.VK_DOWN -> {
                selected = (selected + 1) % options.length;
            }
            case KeyEvent.VK_LEFT -> {
                adjustSelected(-1);
            }
            case KeyEvent.VK_RIGHT -> {
                adjustSelected(+1);
            }
        }
    }

    private void adjustSelected(int dir) {
        // dir = -1 or +1
        switch (selected) {
            case 0: // Acceleration
                Constants.PLAYER.ACCELERATION = clamp(Constants.PLAYER.ACCELERATION + dir * 0.01f, 0f, 5f);
                break;
            case 1: // Max Speed X
                Constants.PLAYER.MAX_SPEED_X = clamp(Constants.PLAYER.MAX_SPEED_X + dir * 0.1f, 0.1f, 50f);
                break;
            case 2: // Jump Force
                Constants.PLAYER.JUMP_FORCE = clamp(Constants.PLAYER.JUMP_FORCE + dir * 0.1f, -100f, 100f);
                break;
            case 3: // Gravity
                Constants.PLAYER.GRAVITY = clamp(Constants.PLAYER.GRAVITY + dir * 0.01f, -5f, 50f);
                break;
            case 4: // Dash Speed
                Constants.PLAYER.DASH_SPEED = clamp(Constants.PLAYER.DASH_SPEED + dir * 1f, 0f, 500f);
                break;
        }
    }

    private float clamp(float v, float min, float max) {
        if (v < min)
            return min;
        if (v > max)
            return max;
        return v;
    }

    /**
     * @param e
     */
    @Override
    public void mouseMoved(MouseEvent e) {

    }

    /**
     * @param e
     */
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    /**
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {

    }

    /**
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {

    }

    public void UpdateStrings() {
        languageString = GetPhrase("settings");
    }
}
