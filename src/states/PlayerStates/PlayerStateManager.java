package states.PlayerStates;

import services.AnimationService;
import services.InputService;
import services.PhysicsService;

/**
 * Gestionnaire des états du joueur utilisant le pattern State
 * 
 * RESPONSABILITÉS:
 * - Gestion des transitions entre états
 * - Coordination des services selon l'état actuel
 * - Gestion du cycle de vie des états
 * 
 * @author Lounol72
 * @version 2.0 - Gestionnaire d'états pour le Player
 */
public class PlayerStateManager {
    
    private PlayerState currentState;
    private PlayerState previousState;
    
    // === SERVICES ===
    private final InputService inputService;
    private final PhysicsService physicsService;
    private final AnimationService animationService;
    
    public PlayerStateManager(InputService inputService, PhysicsService physicsService, AnimationService animationService) {
        this.inputService = inputService;
        this.physicsService = physicsService;
        this.animationService = animationService;
        
        // État initial
        this.currentState = new IdleState();
        this.previousState = null;
        
        // Initialiser l'état
        currentState.onEnter(inputService, physicsService, animationService);
    }
    
    /**
     * Met à jour l'état actuel du joueur
     */
    public void update() {
        // Sauvegarder l'état précédent
        previousState = currentState;
        
        // Mettre à jour l'état actuel
        PlayerState newState = currentState.update(inputService, physicsService, animationService);
        
        // Vérifier s'il y a eu un changement d'état
        if (newState != currentState) {
            // Sortir de l'ancien état
            currentState.onExit(inputService, physicsService, animationService);
            
            // Changer d'état
            currentState = newState;
            
            // Entrer dans le nouvel état
            currentState.onEnter(inputService, physicsService, animationService);
            
            System.out.println("State transition: " + previousState.getStateName() + " -> " + currentState.getStateName());
        }
    }
    
    /**
     * Rend l'état actuel du joueur
     */
    public void render(java.awt.Graphics g, float hitboxX, float hitboxY, int xLvlOffset, int yLvlOffset) {
        currentState.render(g, animationService, hitboxX, hitboxY, xLvlOffset, yLvlOffset);
    }
    
    /**
     * Force un changement d'état (pour les cas spéciaux)
     * 
     * @param newState Nouvel état
     */
    public void forceState(PlayerState newState) {
        if (newState != currentState) {
            // Sortir de l'état actuel
            currentState.onExit(inputService, physicsService, animationService);
            
            // Changer d'état
            previousState = currentState;
            currentState = newState;
            
            // Entrer dans le nouvel état
            currentState.onEnter(inputService, physicsService, animationService);
            
            System.out.println("Forced state transition: " + previousState.getStateName() + " -> " + currentState.getStateName());
        }
    }
    
    /**
     * Retourne à l'état précédent
     */
    public void revertToPreviousState() {
        if (previousState != null) {
            forceState(previousState);
        }
    }
    
    // === GETTERS ===
    
    public PlayerState getCurrentState() {
        return currentState;
    }
    
    public PlayerState getPreviousState() {
        return previousState;
    }
    
    public String getCurrentStateName() {
        return currentState.getStateName();
    }
    
    public String getPreviousStateName() {
        return previousState != null ? previousState.getStateName() : "NONE";
    }
    
    /**
     * Vérifie si l'état actuel est un état spécifique
     * 
     * @param stateClass Classe de l'état à vérifier
     * @return true si l'état actuel est du type spécifié
     */
    public boolean isInState(Class<? extends PlayerState> stateClass) {
        return stateClass.isInstance(currentState);
    }
    
    /**
     * Vérifie si l'état précédent était un état spécifique
     * 
     * @param stateClass Classe de l'état à vérifier
     * @return true si l'état précédent était du type spécifié
     */
    public boolean wasInState(Class<? extends PlayerState> stateClass) {
        return previousState != null && stateClass.isInstance(previousState);
    }
}
