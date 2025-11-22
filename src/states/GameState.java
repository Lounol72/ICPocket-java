package states;

/**
 * Enumération représentant tous les états possibles du jeu.
 * Gère la transition entre les différents écrans (menu, jeu, paramètres, etc.)
 */
public enum GameState {
    // FIRST_LAUNCH, // État réservé pour usage futur
    SPLASH,         // Écran de démarrage/splash
    START,          // Écran de sélection de langue
    MENU,           // Menu principal
    LEVEL_SELECT,   // Écran de sélection de niveau
    WORLD,          // État de jeu (niveau en cours)
    SETTINGS,       // Écran des paramètres
    INFOS,          // Écran d'informations (réservé)
    QUIT;           // État de sortie

    /** État actuel du jeu */
    public static GameState currentState = GameState.SPLASH;
    
    /** État précédent du jeu (pour navigation retour) */
    private static GameState lastState = GameState.SPLASH;

    /**
     * Change l'état actuel du jeu
     * @param state Nouvel état à activer
     */
    public static void setState(GameState state) {
        lastState = currentState;
        currentState = state;
    }

    /**
     * Retourne l'état précédent du jeu
     * @param state Paramètre non utilisé (conservé pour compatibilité)
     * @return L'état précédent
     */
    public GameState getLastState(GameState state) {
        return lastState;
    }
}
