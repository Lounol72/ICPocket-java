package main;

// Game imports
import game.Game;

/**
 * Classe principale du jeu ICPocket.
 * Cette classe initialise et lance le jeu.
 * 
 * @author Lounol72
 * @version 1.0.0
 * @since 1.0.0
 */
public class Main {
    
    /**
     * Instance du jeu principal.
     */
    private static Game game;
    
    /**
     * Point d'entrée principal du programme.
     * Initialise et lance le jeu ICPocket.
     * 
     * @param args Arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        Init();
    }

    /**
     * Initialise le jeu en créant une nouvelle instance de Game.
     * Cette méthode est appelée au démarrage du programme.
     */
    private static void Init() {
        game = new Game();
    }
}