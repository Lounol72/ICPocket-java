/**
 * La classe Game représente le cœur du jeu, gérant la boucle principale et
 * l'ensemble des composants du jeu.
 *
 * <p><b>Fonctionnalités principales :</b></p>
 * <ul>
 *   <li>Initialisation des composants graphiques et des états du jeu</li>
 *   <li>Gestion de la boucle principale avec synchronisation FPS/UPS</li>
 *   <li>Coordination des différents états du jeu (menu, jeu, options)</li>
 *   <li>Gestion de l'échelle et des dimensions du jeu</li>
 * </ul>
 *
 * <p><b>Constantes importantes :</b></p>
 * <ul>
 *   <li>SCALE : Facteur d'échelle appliqué aux éléments graphiques</li>
 *   <li>TILES_SIZE : Taille des tuiles du jeu après mise à l'échelle</li>
 *   <li>GAME_WIDTH/HEIGHT : Dimensions de la fenêtre de jeu</li>
 *   <li>PLAYER_WIDTH/HEIGHT : Dimensions du joueur après mise à l'échelle</li>
 * </ul>
 *
 * <p><b>Utilisation :</b> Instancier un objet Game pour démarrer le jeu.</p>
 *
 * @author Lounol72
 * @version 1.0
 * @since 16/04/2025
 */
package game;

// Java standard library imports
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import states.GameState;
import static states.GameState.currentState;
import states.LevelSelect;
import states.Menu;
import states.Pause;
import states.Settings;
import states.Splash;
import states.Start;
import states.World;

public class Game implements Runnable{

    // Composants principaux du jeu
    private final GamePanel gamePanel;      // Panneau où le jeu est rendu
    private final GameWindow gameWindow;    // Fenêtre qui contient le panneau
    private Thread gameLoopThread;          // Thread dédié à la boucle de jeu

    // Paramètres de performance (utilisent les constantes de Constants.PERFORMANCE)
    private static final int TARGET_UPS = utilz.Constants.PERFORMANCE.TARGET_UPS; // UPS fixe


    private Menu menu;
    private World world;
    private Start start;
    private Settings settings;
    private LevelSelect levelSelect;
    private Pause pause;
    private final ScreenFader fader = new ScreenFader();
    private Splash splash;
    
    // FPS/UPS tracking
    private int currentFPS = 0;
    private int currentUPS = 0;


    /**
     * Constructeur qui initialise et démarre le jeu.
     * Crée les composants nécessaires et lance la boucle de jeu.
     */
    public Game() {
        initClasses();
        gamePanel = new GamePanel(this);
        gameWindow = new GameWindow(gamePanel);
        gamePanel.setFocusable(true);
        gamePanel.requestFocus();

        startGameLoop();
    }

    /**
     * Initialise les différents états du jeu.
     */
    /**
     * Initialise tous les états du jeu.
     * L'ordre d'initialisation est important car certains états peuvent dépendre d'autres.
     */
    private void initClasses() {
        // Initialiser tous les états du jeu
        this.menu = new Menu(this);
        this.world = new World(this);
        this.settings = new Settings(this);
        this.splash = new Splash(this);
        this.start = new Start(this);
        this.levelSelect = new LevelSelect(this);
        this.pause = new Pause(this);
    }

    /**
     * Démarre la boucle de jeu dans un thread séparé.
     */
    private void startGameLoop() {
        gameLoopThread = new Thread(this);
        gameLoopThread.start();
    }

    /**
     * Met à jour l'état du jeu en fonction du GameState actuel.
     * Délègue la mise à jour à l'état approprié.
     */
    private void update() {
        switch(currentState){
            case SPLASH -> {
                splash.update();
            }
            case MENU -> {
                menu.update();
            }
            case LEVEL_SELECT -> {
                levelSelect.update();
            }
            case WORLD -> {
                world.update();
            }
            case PAUSE -> {
                pause.update();
                // Ne pas mettre à jour le monde en pause
            }
            case START -> {
                start.update();
            }
            case SETTINGS -> {
                settings.update();
            }

            case INFOS -> {
            }
            case QUIT -> {
                System.exit(0);
            }
            default -> {throw new IllegalStateException("État de jeu non géré"); }
        }
        // update fader last
        fader.update(this);
    }

    /**
     * Dessine l'état actuel du jeu selon le GameState.
     * Délègue le rendu à l'état approprié.
     *
     * @param g Contexte graphique utilisé pour le dessin
     */
    public void render(Graphics g) {
        switch(currentState){
            case SPLASH ->{
                splash.draw(g);
            }
            case START ->{
                start.draw(g);
            }
            case MENU -> {
                menu.draw(g);
            }
            case LEVEL_SELECT -> {
                levelSelect.draw(g);
            }
            case WORLD -> {
                world.draw(g);
            }
            case PAUSE -> {
                // Pause dessine le monde en arrière-plan avec blur
                pause.draw(g);
            }
            case SETTINGS -> {
                settings.draw(g);
            }

            case INFOS -> {
            }
            case QUIT -> {
                // Nothing to render for quit state
            }
            default -> {throw new IllegalStateException("État de jeu non géré"); }
        }
        // draw fader overlay on top
        fader.draw((Graphics2D) g, this);
        
        // Draw FPS/UPS overlay if enabled
        if (utilz.Constants.DEBUG.RENDER_FPS_UPS) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            g2d.drawString("FPS: " + currentFPS + " | UPS: " + currentUPS, 10, 20);
        }
    }

    /**
     * Implémente la boucle principale du jeu avec découplage complet FPS/UPS.
     * 
     * ARCHITECTURE:
     * - UPS (Logic): Fixed Time Step strict à 200 UPS avec accumulateur
     * - FPS (Render): Limité mais découplé, configurable depuis les settings
     * - Utilise System.nanoTime() pour une précision maximale
     * - Thread.sleep() pour limiter le FPS et économiser les ressources CPU
     * 
     * FIXED TIME STEP:
     * L'UPS utilise un accumulateur qui garantit exactement 200 updates/seconde
     * même si le rendu est plus lent ou plus rapide. Cela assure une physique
     * stable et reproductible.
     */
    @Override
    public void run() {
        // === CONSTANTES DE TEMPS ===
        // Temps par update en nanosecondes (Fixed Time Step pour UPS)
        final double NANOS_PER_UPDATE = 1_000_000_000.0 / TARGET_UPS;
        
        // Variables pour le tracking FPS/UPS
        int frames = 0;              // Compteur de frames rendues
        int updates = 0;             // Compteur de mises à jour effectuées
        long lastFpsCheck = System.currentTimeMillis();
        
        // === FIXED TIME STEP - ACCUMULATEUR ===
        // L'accumulateur garantit que la logique tourne exactement à TARGET_UPS
        double accumulator = 0.0;     // Accumulateur pour les updates (Fixed Time Step)
        
        // === TIMING ===
        long previousTime = System.nanoTime(); // Temps précédent en nanosecondes
        
        // === BOUCLE PRINCIPALE ===
        while (true) {
            long currentTime = System.nanoTime();
            long elapsedNanos = currentTime - previousTime;
            previousTime = currentTime;
            
            // === PHASE 1: LOGIC (UPS) - FIXED TIME STEP ===
            // Ajouter le temps écoulé à l'accumulateur
            accumulator += elapsedNanos;
            
            // Exécuter les updates nécessaires pour maintenir TARGET_UPS
            // L'accumulateur garantit qu'on fait exactement le bon nombre d'updates
            while (accumulator >= NANOS_PER_UPDATE) {
                update();            // Mise à jour de la logique du jeu
                updates++;
                accumulator -= NANOS_PER_UPDATE; // Retirer un "tick" de l'accumulateur
            }
            
            // === PHASE 2: RENDERING (FPS) - DÉCOUPLÉ ===
            // Le rendu est complètement indépendant de la logique
            // On peut rendre à n'importe quelle fréquence (limitée par TARGET_FPS)
            gamePanel.repaint();
            frames++;
            
            // === PHASE 3: LIMITATION FPS ET ÉCONOMIE CPU ===
            // Calculer le temps disponible avant le prochain frame
            int targetFPS = utilz.Constants.PERFORMANCE.TARGET_FPS;
            long nanosPerFrame = 1_000_000_000L / targetFPS;
            long frameTime = System.nanoTime() - currentTime;
            long sleepTime = (nanosPerFrame - frameTime) / 1_000_000; // Convertir en millisecondes
            
            // Dormir seulement si on a le temps (évite les valeurs négatives)
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break; // Sortir de la boucle si interrompu
                }
            }
            // Si sleepTime <= 0, on est en retard et on continue sans dormir
            
            // === PHASE 4: TRACKING FPS/UPS (affichage) ===
            // Mettre à jour les compteurs chaque seconde
            long currentMillis = System.currentTimeMillis();
            if (currentMillis - lastFpsCheck >= 1000) {
                currentFPS = frames;
                currentUPS = updates;
                frames = 0;
                updates = 0;
                lastFpsCheck = currentMillis;
            }
        }
    }

    public Menu getMenu() {
        return menu;
    }

    /**
     * Met à jour toutes les chaînes de caractères traduites dans tous les états.
     * Appelé lors d'un changement de langue.
     */
    public void UpdateEveryStrings() {
        System.out.println("Every Strings Updated");
        menu.UpdateStrings();
        settings.UpdateStrings();
        splash.UpdateStrings();
        // Vérifier que levelSelect est initialisé avant de mettre à jour ses strings
        if (levelSelect != null) {
            levelSelect.UpdateStrings();
        }
    }

    public World getWorld() {return world;
    }

    public Settings getSettings() {
        return settings;
    }

    public Start getStart() {
        return start;
    }

    public Splash getSplash() {
        return splash;
    }

    /**
     * Retourne l'instance du state de sélection de niveau.
     * 
     * @return L'instance de LevelSelect
     */
    public LevelSelect getLevelSelect() {
        return levelSelect;
    }

    public Pause getPause() {
        return pause;
    }

    public void startTransition(states.GameState target, Color color) {
        fader.start(target, 400, 400, color);
    }

    public void windowFocusLost() {
		if (GameState.currentState == GameState.WORLD)
			world.getPlayer().resetDirBooleans();
	}
}