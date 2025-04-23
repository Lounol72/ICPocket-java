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

import java.awt.*;

import states.Battle;
import states.Menu;


import static states.GameState.currentState;

public class Game implements Runnable{
    // Composants principaux du jeu
    private final GamePanel gamePanel;      // Panneau où le jeu est rendu
    private final GameWindow gameWindow;    // Fenêtre qui contient le panneau
    private Thread gameLoopThread;          // Thread dédié à la boucle de jeu

    // Paramètres de performance
    private final int FPS_SET = 120;        // Images par seconde cible
    private final int UPS_SET = 200;        // Mises à jour par seconde cible



    // Constantes liées à la taille de la fenêtre

    public final static int GAME_WIDTH = 1280;    // Largeur totale du jeu
    public final static int GAME_HEIGHT = 920;  // Hauteur totale du jeu

    private Menu menu;
    private Battle battle;


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
    private void initClasses() {
        this.menu = new Menu(this);
        this.battle = new Battle(this);
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
            case MENU -> {
                menu.update();
            }
            case BATTLE -> {
                battle.update();
            }
            case WORLD -> {
            }
            case SETTINGS -> {
            }
            case TEAM -> {
            }
            case INFOS -> {
            }
            default -> {throw new IllegalStateException("État de jeu non géré"); }
        }
    }

    /**
     * Dessine l'état actuel du jeu selon le GameState.
     * Délègue le rendu à l'état approprié.
     *
     * @param g Contexte graphique utilisé pour le dessin
     */
    public void render(Graphics g) {
        switch(currentState){
            case MENU -> {
                menu.draw(g);
            }
            case BATTLE -> {
                battle.draw(g);
            }
            case WORLD -> {
            }
            case SETTINGS -> {
            }
            case TEAM -> {
            }
            case INFOS -> {
            }
            default -> {throw new IllegalStateException("État de jeu non géré"); }
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
                frames = 0;
                updates = 0;
            }
        }
    }

    public Menu getMenu() {
        return menu;
    }
    public Battle getBattle(){
        return battle;
    }

    public void UpdateEveryStrings() {
        System.out.println("Every Strings Updated");
    }
}