package game;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;

import states.GameState;
import static states.GameState.setState;
import static utilz.Constants.WORLD.GAME_HEIGHT;
import static utilz.Constants.WORLD.GAME_WIDTH;

public class ScreenFader {

    private enum Phase { IDLE, FADING_OUT, FADING_IN }

    private Phase phase = Phase.IDLE;
    private float alpha = 0f; // 0..1
    private long phaseStartMs = 0L;
    private long fadeOutMs = 400L;
    private long fadeInMs = 400L;
    private Color overlayColor = Color.black;
    private GameState targetState;

    public boolean isActive() {
        return phase != Phase.IDLE || alpha > 0f;
    }

    public void start(GameState target, long fadeOutMs, long fadeInMs, Color color) {
        this.targetState = target;
        this.fadeOutMs = Math.max(1L, fadeOutMs);
        this.fadeInMs = Math.max(1L, fadeInMs);
        this.overlayColor = color == null ? Color.black : color;
        this.phase = Phase.FADING_OUT;
        this.phaseStartMs = System.currentTimeMillis();
    }

    public void update(Game game) {
        if (phase == Phase.IDLE) return;

        long now = System.currentTimeMillis();
        long elapsed = now - phaseStartMs;

        switch (phase) {
            case FADING_OUT -> {
                float t = Math.min(1f, elapsed / (float) fadeOutMs);
                alpha = t;
                if (t >= 1f) {
                    // switch state at full black
                    if (targetState != null) {
                        setState(targetState);
                    }
                    phase = Phase.FADING_IN;
                    phaseStartMs = now;
                }
            }
            case FADING_IN -> {
                float t = Math.min(1f, elapsed / (float) fadeInMs);
                alpha = 1f - t;
                if (t >= 1f) {
                    alpha = 0f;
                    phase = Phase.IDLE;
                }
            }
            default -> {}
        }
    }

    public void draw(Graphics2D g2d, Game game) {
        if (!isActive()) return;
        Composite prev = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, clamp(alpha, 0f, 1f)));
        g2d.setColor(overlayColor);
        g2d.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        g2d.setComposite(prev);
    }

    private static float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }
}


