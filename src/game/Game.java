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
import states.Settings;
import states.Splash;
import states.Start;
import states.World;
import utilz.LoadSave;

public class Game implements Runnable{

    // Composants principaux du jeu
    private final GamePanel gamePanel;      // Panneau où le jeu est rendu
    private final GameWindow gameWindow;    // Fenêtre qui contient le panneau
    private Thread gameLoopThread;          // Thread dédié à la boucle de jeu

    // Paramètres de performance
    private final int FPS_SET = 120;        // Images par seconde cible
    private final int UPS_SET = 200;        // Mises à jour par seconde cible


    private Menu menu;
    private World world;
    private Start start;
    private Settings settings;
    private LevelSelect levelSelect;
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
        // Charger toutes les données de niveau en premier
        LoadSave.GetAllLevelData();

        // Initialiser tous les états du jeu
        this.menu = new Menu(this);
        this.world = new World(this);
        this.settings = new Settings(this);
        this.splash = new Splash(this);
        this.start = new Start(this);
        this.levelSelect = new LevelSelect(this);
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
     * Implémente la boucle principale du jeu avec synchronisation FPS/UPS.
     * Utilise un temps delta pour maintenir une cadence régulière.
     * Affiche les FPS et UPS dans la console pour le suivi des performances.
     */
    @Override
    public void run() {
        // Calcul du temps entre chaque frame et update
        double timePerFrame = 1000000000.0 / FPS_SET;  // Nanoseconds par frame
        double timePerUpdate = 1000000000.0 / UPS_SET; // Nanoseconds par update

        long previousTime = System.nanoTime();
        int frames = 0;      // Compteur de frames rendues
        int updates = 0;     // Compteur de mises à jour effectuées
        long lastCheck = System.currentTimeMillis();

        double deltaU = 0;   // Accumulation du temps pour les updates
        double deltaF = 0;   // Accumulation du temps pour les frames

        // Boucle infinie du jeu
        while (true) {
            long currentTime = System.nanoTime();

            // Calcul des deltas
            deltaU += (currentTime - previousTime) / timePerUpdate;
            deltaF += (currentTime - previousTime) / timePerFrame;
            previousTime = currentTime;

            // Mise à jour de la logique si nécessaire
            if (deltaU >= 1) {
                update();
                updates++;
                deltaU--;
            }

            // Rendu à l'écran si nécessaire
            if (deltaF >= 1) {
                gamePanel.repaint();
                frames++;
                deltaF--;
            }

            // Affichage des FPS et UPS chaque seconde
            if (System.currentTimeMillis() - lastCheck >= 1000) {
                lastCheck = System.currentTimeMillis();
                currentFPS = frames;
                currentUPS = updates;
                frames = 0;
                updates = 0;
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

    public void startTransition(states.GameState target, Color color) {
        fader.start(target, 400, 400, color);
    }

    public void windowFocusLost() {
		if (GameState.currentState == GameState.WORLD)
			world.getPlayer().resetDirBooleans();
	}
}