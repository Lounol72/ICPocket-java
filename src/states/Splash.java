package states;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import game.Game;
import static states.GameState.START;
import static utilz.Constants.WORLD.GAME_HEIGHT;
import static utilz.Constants.WORLD.GAME_WIDTH;
import utilz.LoadSave;

public class Splash extends State implements StateMethods {

    // Assets
    private BufferedImage logo;
    private String gameName;
    private String pressEnterText;

    // Logo scaling
    private static final int MAX_LOGO_WIDTH = (int) (GAME_WIDTH * 0.6f);  // Max 60% of screen width
    private static final int MAX_LOGO_HEIGHT = (int) (GAME_HEIGHT * 0.4f); // Max 40% of screen height

    // Fade parameters
    private float alpha = 0f; // 0..1
    private int phase = 0; // 0: fade-in, 1: hold, 2: fade-out
    private long phaseStartMs = System.currentTimeMillis();

    private static final long HOLD_DURATION_MS = 800; // time fully visible
    private static final float FADE_SECONDS = 1.0f;   // fade in/out duration
    
    // Text positioning
    private int promptTextX, promptTextY;
    private boolean promptPosCalc = false;

    public Splash(Game game) {
        super(game);

        // Try to load logo; fall back if missing
        logo = LoadSave.GetSpriteAtlas(LoadSave.UI + "logo.png");
        loadStrings();
    }
    
    private void loadStrings() {
        gameName = "Pocket";
        pressEnterText = utilz.HelpMethods.GetPhrase("press_enter");
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(30, 52, 62));
        g2d.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        // Apply alpha composite for fade effect
        Composite prev = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, clamp(alpha, 0f, 1f)));

        int centerX = GAME_WIDTH / 2;
        int centerY = GAME_HEIGHT / 2 - 24;

        if (logo != null) {
            // Calculate scaled dimensions maintaining aspect ratio
            int originalWidth = logo.getWidth();
            int originalHeight = logo.getHeight();
            
            float scaleX = (float) MAX_LOGO_WIDTH / originalWidth;
            float scaleY = (float) MAX_LOGO_HEIGHT / originalHeight;
            float scale = Math.min(scaleX, scaleY); // Use smaller scale to fit within bounds
            
            int scaledWidth = (int) (originalWidth * scale);
            int scaledHeight = (int) (originalHeight * scale);
            
            int x = centerX - scaledWidth / 2;
            int y = centerY - scaledHeight / 2;
            g2d.drawImage(logo, x, y, scaledWidth, scaledHeight, null);
        }

        g2d.setComposite(prev);

        // Draw title (always visible, not affected by fade animation)
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.setColor(Color.WHITE);
        int textX = (int) (GAME_WIDTH * 0.1f);
        int textY = (int) (GAME_HEIGHT * 0.1f);
        g.drawString(gameName, textX, textY);

        
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        if (!promptPosCalc) {
            FontMetrics pfm = g2d.getFontMetrics();
            promptTextX = centerX - pfm.stringWidth(pressEnterText) / 2;
            promptTextY = (int) (GAME_HEIGHT * 0.9);
            promptPosCalc = true;
        }
        g2d.drawString(pressEnterText, promptTextX, promptTextY);
    }

    @Override
    public void update() {
        float fadePerUpdate = (float) (1.0 / (FADE_SECONDS * 200.0)); // UPS ~200
        long now = System.currentTimeMillis();

        switch (phase) {
            case 0 -> { // fade-in
                alpha += fadePerUpdate;
                if (alpha >= 1f) {
                    alpha = 1f;
                    phase = 1;
                    phaseStartMs = now;
                }
            }
            case 1 -> { // hold
                if (now - phaseStartMs >= HOLD_DURATION_MS) {
                    phase = 2;
                }
            }
            case 2 -> { // fade-out
                alpha -= fadePerUpdate;
                if (alpha <= 0f) {
                    alpha = 0f;
                    phase = 0; // loop the animation
                }
            }
            default -> {}
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            game.startTransition(START, Color.BLACK);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) { }

    @Override
    public void mouseDragged(MouseEvent e) { }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    public void UpdateStrings() {
        loadStrings();
        promptPosCalc = false; // Force recalculation of prompt position
    }

    private static float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }
}


