package utilz;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

import static utilz.Constants.SCALE;
import static utilz.Constants.WORLD.*;
import static utilz.Constants.MENU_SUNSET.*;
import static utilz.Constants.WORLD.ENVIRONMENT.*;
import static utilz.Constants.PERFORMANCE;

/**
 * Animated parallax background system for menu states.
 * Features warm sunset gradient with drifting clouds and optional warm overlay.
 */
public class MenuParallaxBackground {
    
    // Cloud assets
    private BufferedImage bigCloud;
    private BufferedImage smallCloud;
    
    // Cloud positions
    private float[] bigCloudsXPos;
    private int[] bigCloudsYPos;
    private float[] smallCloudsXPos;
    private int[] smallCloudsYPos;
    
    // Animation control
    private boolean animationEnabled;
    private Random random;
    
    /**
     * Creates a new menu parallax background with sunset theme.
     * Loads cloud assets and initializes randomized vertical positions.
     */
    public MenuParallaxBackground() {
        this.random = new Random();
        loadAssets();
        initializeCloudPositions();
        this.animationEnabled = PERFORMANCE.PARALLAX_ENABLED;
    }
    
    /**
     * Loads cloud sprite assets from resources.
     */
    private void loadAssets() {
        bigCloud = LoadSave.GetSpriteAtlas(LoadSave.BIG_CLOUDS);
        smallCloud = LoadSave.GetSpriteAtlas(LoadSave.SMALL_CLOUD_1);
        
        if (bigCloud == null) {
            System.err.println("Warning: Failed to load big clouds for menu background");
        }
        if (smallCloud == null) {
            System.err.println("Warning: Failed to load small clouds for menu background");
        }
    }
    
    /**
     * Initializes randomized positions for all clouds.
     */
    private void initializeCloudPositions() {
        // Initialize big clouds (3 clouds)
        bigCloudsXPos = new float[3];
        bigCloudsYPos = new int[3];
        for (int i = 0; i < bigCloudsXPos.length; i++) {
            bigCloudsXPos[i] = i * BIG_CLOUDS_WIDTH;
            bigCloudsYPos[i] = BIG_CLOUD_Y;
        }
        
        // Initialize small clouds (8 clouds)
        smallCloudsXPos = new float[8];
        smallCloudsYPos = new int[8];
        int spacing = SMALL_CLOUD_1_WIDTH * 4;
        
        for (int i = 0; i < smallCloudsYPos.length; i++) {
            smallCloudsXPos[i] = i * spacing;
            // Range: 120px to 360px (higher in sky for sunset aesthetic)
            smallCloudsYPos[i] = SMALL_CLOUD_BASE_Y + random.nextInt(SMALL_CLOUD_VARIANCE);
        }
    }
    
    /**
     * Updates animation state. Call once per frame in state's update() method.
     * Only scrolls clouds if animation is enabled.
     * When a cloud exits the left side, it's repositioned to the right with a new Y position.
     */
    public void update() {
        // Check current setting
        animationEnabled = PERFORMANCE.PARALLAX_ENABLED;
        
        if (!animationEnabled) {
            return;
        }
        
        // Update big clouds (slower parallax)
        float bigCloudSpeed = SCROLL_SPEED * 0.5f;
        for (int i = 0; i < bigCloudsXPos.length; i++) {
            bigCloudsXPos[i] -= bigCloudSpeed;
            
            // When right edge exits left side of screen, reposition to the right
            if (bigCloudsXPos[i] + BIG_CLOUDS_WIDTH < 0) {
                // Find the rightmost cloud
                float maxX = bigCloudsXPos[0];
                for (float x : bigCloudsXPos) {
                    if (x > maxX) maxX = x;
                }
                // Place this cloud to the right of the rightmost cloud
                bigCloudsXPos[i] = maxX + BIG_CLOUDS_WIDTH;
                // Keep Y position the same for big clouds
            }
        }
        
        // Update small clouds (faster, visible movement)
        for (int i = 0; i < smallCloudsXPos.length; i++) {
            smallCloudsXPos[i] -= SCROLL_SPEED;
            
            // When right edge exits left side of screen, reposition to the right
            if (smallCloudsXPos[i] + SMALL_CLOUD_1_WIDTH < 0) {
                // Find the rightmost cloud
                float maxX = smallCloudsXPos[0];
                for (float x : smallCloudsXPos) {
                    if (x > maxX) maxX = x;
                }
                // Place this cloud to the right of the rightmost cloud
                smallCloudsXPos[i] = maxX + (SMALL_CLOUD_1_WIDTH * 4);
                // Randomize Y position for variety
                smallCloudsYPos[i] = SMALL_CLOUD_BASE_Y + random.nextInt(SMALL_CLOUD_VARIANCE);
            }
        }
    }
    
    /**
     * Renders the complete parallax background.
     * Draws in order: gradient → big clouds → small clouds → warm overlay
     * 
     * @param g Graphics context
     */
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // Step 1: Draw sunset gradient background
        drawSunsetGradient(g2d);
        
        // Step 2: Draw cloud layers with parallax
        if (animationEnabled) {
            drawAnimatedClouds(g2d);
        } else {
            drawStaticClouds(g2d);
        }
        
        // Step 3: Apply warm color overlay
        drawWarmOverlay(g2d);
    }
    
    /**
     * Draws the 3-color sunset gradient background.
     * Uses two gradient paints for smooth color transition.
     */
    private void drawSunsetGradient(Graphics2D g2d) {
        int horizonY = GAME_HEIGHT / 2;
        
        // Top half: Dark purple to warm orange
        GradientPaint topGradient = new GradientPaint(
            0, 0, GRADIENT_TOP,
            0, horizonY, GRADIENT_HORIZON
        );
        g2d.setPaint(topGradient);
        g2d.fillRect(0, 0, GAME_WIDTH, horizonY);
        
        // Bottom half: Warm orange to peachy bottom
        GradientPaint bottomGradient = new GradientPaint(
            0, horizonY, GRADIENT_HORIZON,
            0, GAME_HEIGHT, GRADIENT_BOTTOM
        );
        g2d.setPaint(bottomGradient);
        g2d.fillRect(0, horizonY, GAME_WIDTH, GAME_HEIGHT - horizonY);
    }
    
    /**
     * Draws clouds with animated scrolling effect.
     * Each cloud moves independently and wraps around seamlessly.
     */
    private void drawAnimatedClouds(Graphics2D g2d) {
        // Draw big clouds (back layer, slower movement)
        if (bigCloud != null) {
            for (int i = 0; i < bigCloudsXPos.length; i++) {
                int xPos = (int) bigCloudsXPos[i];
                g2d.drawImage(bigCloud, xPos, bigCloudsYPos[i], 
                            BIG_CLOUDS_WIDTH, BIG_CLOUDS_HEIGHT, null);
            }
        }
        
        // Draw small clouds (front layer, visible movement)
        if (smallCloud != null) {
            for (int i = 0; i < smallCloudsXPos.length; i++) {
                int xPos = (int) smallCloudsXPos[i];
                g2d.drawImage(smallCloud, xPos, smallCloudsYPos[i], 
                            SMALL_CLOUD_1_WIDTH, SMALL_CLOUD_1_HEIGHT, null);
            }
        }
    }
    
    /**
     * Draws clouds at fixed positions (no animation).
     * Used when parallax is disabled for performance.
     */
    private void drawStaticClouds(Graphics2D g2d) {
        // Draw big clouds at their current positions
        if (bigCloud != null) {
            for (int i = 0; i < bigCloudsXPos.length; i++) {
                int xPos = (int) bigCloudsXPos[i];
                g2d.drawImage(bigCloud, xPos, bigCloudsYPos[i], 
                            BIG_CLOUDS_WIDTH, BIG_CLOUDS_HEIGHT, null);
            }
        }
        
        // Draw small clouds at their current positions
        if (smallCloud != null) {
            for (int i = 0; i < smallCloudsXPos.length; i++) {
                int xPos = (int) smallCloudsXPos[i];
                g2d.drawImage(smallCloud, xPos, smallCloudsYPos[i], 
                            SMALL_CLOUD_1_WIDTH, SMALL_CLOUD_1_HEIGHT, null);
            }
        }
    }
    
    /**
     * Applies a semi-transparent warm color overlay.
     * Enhances the sunset atmosphere and tints clouds.
     */
    private void drawWarmOverlay(Graphics2D g2d) {
        Composite prevComposite = g2d.getComposite();
        
        // Apply warm tint with SRC_ATOP (only affects existing pixels)
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, WARM_OVERLAY_ALPHA));
        g2d.setColor(WARM_OVERLAY_COLOR);
        g2d.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        
        // Restore original composite
        g2d.setComposite(prevComposite);
    }
    
    /**
     * Sets whether animation is enabled.
     * @param enabled true to enable scrolling, false for static background
     */
    public void setAnimationEnabled(boolean enabled) {
        this.animationEnabled = enabled;
    }
    
    /**
     * Resets cloud positions to their initial state.
     * Useful when transitioning between states.
     */
    public void reset() {
        initializeCloudPositions();
    }
}
