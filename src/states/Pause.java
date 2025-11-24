package states;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import game.Game;
import static utilz.Constants.WORLD.GAME_HEIGHT;
import static utilz.Constants.WORLD.GAME_WIDTH;
import static utilz.HelpMethods.GetPhrase;

/**
 * État de pause du jeu.
 * 
 * FONCTIONNALITÉS:
 * - Affiche le niveau WORLD en arrière-plan (sans mise à jour)
 * - Applique un effet de flou (blur) sur l'écran
 * - Affiche un overlay semi-transparent
 * - Affiche le texte "PAUSE"
 * - Peut être activé/désactivé avec ESC
 * 
 * ARCHITECTURE:
 * - Ne met PAS à jour le monde (World.update() n'est pas appelé)
 * - Dessine le monde en arrière-plan via World.draw()
 * - Applique le blur et l'overlay par-dessus
 * 
 * @author Lounol72
 * @version 1.0
 */
public class Pause extends State implements StateMethods {

    // === EFFET DE BLUR ===
    private static final int BLUR_RADIUS = 5; // Rayon du flou (plus élevé = plus flou)
    private static final float OVERLAY_ALPHA = 0.5f; // Opacité de l'overlay (0.0 = transparent, 1.0 = opaque)
    
    // === COULEURS ===
    private static final Color OVERLAY_COLOR = new Color(0, 0, 0, (int)(OVERLAY_ALPHA * 255)); // Noir semi-transparent
    
    // === TEXTE ===
    private String pauseText;
    private Font pauseFont;

    /**
     * Constructeur de l'état de pause
     * @param game Instance du jeu
     */
    public Pause(Game game) {
        super(game);
        pauseFont = new Font("Arial", Font.BOLD, 48);
        UpdateStrings();
    }

    /**
     * Dessine l'état de pause.
     * 
     * ORDRE DE RENDU:
     * 1. Dessine le monde dans une image temporaire
     * 2. Applique l'effet de blur sur cette image
     * 3. Dessine l'image floutée à l'écran
     * 4. Dessine l'overlay semi-transparent
     * 5. Dessine le texte "PAUSE"
     * 
     * @param g Contexte graphique pour le dessin
     */
    @Override
    public void draw(Graphics g) {
        // === ÉTAPE 1: CRÉER UNE IMAGE TEMPORAIRE POUR CAPTURER LE MONDE ===
        BufferedImage worldImage = new BufferedImage(GAME_WIDTH, GAME_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D worldGraphics = worldImage.createGraphics();
        
        // Dessiner le monde dans l'image (sans mise à jour)
        World world = game.getWorld();
        if (world != null) {
            world.draw(worldGraphics);
        }
        worldGraphics.dispose();

        // === ÉTAPE 2: APPLIQUER LE BLUR ===
        BufferedImage blurredImage = applyBlur(worldImage, BLUR_RADIUS);
        
        // === ÉTAPE 3: DESSINER L'IMAGE FLOUTÉE ===
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(blurredImage, 0, 0, null);

        // === ÉTAPE 4: DESSINER L'OVERLAY SEMI-TRANSPARENT ===
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, OVERLAY_ALPHA));
        g2d.setColor(OVERLAY_COLOR);
        g2d.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); // Réinitialiser

        // === ÉTAPE 5: DESSINER LE TEXTE "PAUSE" ===
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setFont(pauseFont);
        g2d.setColor(Color.WHITE);
        
        // Centrer le texte
        int textWidth = g2d.getFontMetrics().stringWidth(pauseText);
        int textX = (GAME_WIDTH - textWidth) / 2;
        int textY = GAME_HEIGHT / 2;
        
        // Dessiner avec ombre pour meilleure lisibilité
        g2d.setColor(Color.BLACK);
        g2d.drawString(pauseText, textX + 2, textY + 2);
        g2d.setColor(Color.WHITE);
        g2d.drawString(pauseText, textX, textY);
    }

    /**
     * Applique un effet de flou gaussien à une image.
     * 
     * ALGORITHME OPTIMISÉ:
     * - Utilise un box blur en deux passes (horizontal puis vertical)
     * - Beaucoup plus rapide que le blur 2D complet (O(n²) au lieu de O(n⁴))
     * - Plus le rayon est élevé, plus le flou est prononcé
     * 
     * @param source Image source à flouter
     * @param radius Rayon du flou (en pixels)
     * @return Image floutée
     */
    private BufferedImage applyBlur(BufferedImage source, int radius) {
        if (source == null) {
            return null;
        }

        int width = source.getWidth();
        int height = source.getHeight();
        
        // === PASSE 1: BLUR HORIZONTAL ===
        BufferedImage horizontalBlur = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = 0, g = 0, b = 0, a = 0;
                int count = 0;

                // Moyenne horizontale des pixels dans le rayon
                for (int dx = -radius; dx <= radius; dx++) {
                    int px = x + dx;
                    if (px >= 0 && px < width) {
                        int rgb = source.getRGB(px, y);
                        r += (rgb >> 16) & 0xFF;
                        g += (rgb >> 8) & 0xFF;
                        b += rgb & 0xFF;
                        a += (rgb >> 24) & 0xFF;
                        count++;
                    }
                }

                if (count > 0) {
                    r /= count;
                    g /= count;
                    b /= count;
                    a /= count;
                    int rgb = (a << 24) | (r << 16) | (g << 8) | b;
                    horizontalBlur.setRGB(x, y, rgb);
                }
            }
        }

        // === PASSE 2: BLUR VERTICAL ===
        BufferedImage blurred = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = 0, g = 0, b = 0, a = 0;
                int count = 0;

                // Moyenne verticale des pixels dans le rayon
                for (int dy = -radius; dy <= radius; dy++) {
                    int py = y + dy;
                    if (py >= 0 && py < height) {
                        int rgb = horizontalBlur.getRGB(x, py);
                        r += (rgb >> 16) & 0xFF;
                        g += (rgb >> 8) & 0xFF;
                        b += rgb & 0xFF;
                        a += (rgb >> 24) & 0xFF;
                        count++;
                    }
                }

                if (count > 0) {
                    r /= count;
                    g /= count;
                    b /= count;
                    a /= count;
                    int rgb = (a << 24) | (r << 16) | (g << 8) | b;
                    blurred.setRGB(x, y, rgb);
                }
            }
        }

        return blurred;
    }

    /**
     * Ne fait rien en pause (le monde n'est pas mis à jour)
     */
    @Override
    public void update() {
        // Rien à mettre à jour en pause
        // Le monde reste figé
    }

    /**
     * Gère les entrées clavier en pause.
     * ESC pour reprendre le jeu.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE -> {
                // Reprendre le jeu
                GameState.setState(GameState.WORLD);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Rien à faire
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Rien à faire
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Rien à faire
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Rien à faire
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Rien à faire
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Rien à faire
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Rien à faire
    }

    /**
     * Met à jour les chaînes de caractères traduites
     */
    @Override
    public void UpdateStrings() {
        pauseText = GetPhrase("pause");
    }
}

