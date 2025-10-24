package states.PlayerStates;

import java.awt.Graphics;

import services.AnimationService;
import services.InputService;
import services.PhysicsService;

/**
 * Interface pour les états du joueur utilisant le pattern State
 * 
 * RESPONSABILITÉS:
 * - Définir le comportement spécifique à chaque état
 * - Gérer les transitions entre états
 * - Coordonner les services selon l'état
 * 
 * @author Lounol72
 * @version 2.0 - Pattern State pour le Player
 */
public interface PlayerState {
    
    /**
     * Met à jour l'état du joueur
     * 
     * @param inputService Service de gestion des inputs
     * @param physicsService Service de physique
     * @param animationService Service d'animation
     * @return Le nouvel état du joueur (peut être le même ou un autre)
     */
    PlayerState update(InputService inputService, PhysicsService physicsService, AnimationService animationService);
    
    /**
     * Rend l'état du joueur
     * 
     * @param g Graphics context
     * @param animationService Service d'animation
     * @param hitboxX Position X de la hitbox
     * @param hitboxY Position Y de la hitbox
     * @param xLvlOffset Offset horizontal du niveau
     * @param yLvlOffset Offset vertical du niveau
     */
    void render(Graphics g, AnimationService animationService, float hitboxX, float hitboxY, int xLvlOffset, int yLvlOffset);
    
    /**
     * Gère l'entrée dans cet état
     * 
     * @param inputService Service de gestion des inputs
     * @param physicsService Service de physique
     * @param animationService Service d'animation
     */
    void onEnter(InputService inputService, PhysicsService physicsService, AnimationService animationService);
    
    /**
     * Gère la sortie de cet état
     * 
     * @param inputService Service de gestion des inputs
     * @param physicsService Service de physique
     * @param animationService Service d'animation
     */
    void onExit(InputService inputService, PhysicsService physicsService, AnimationService animationService);
    
    /**
     * Retourne le nom de l'état pour le debug
     * 
     * @return Nom de l'état
     */
    String getStateName();
}

