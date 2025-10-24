package states.PlayerStates;

import java.awt.Graphics;

import services.AnimationService;
import services.InputService;
import services.PhysicsService;

/**
 * État d'inactivité du joueur
 * 
 * COMPORTEMENT:
 * - Le joueur est immobile
 * - Transition vers RUN si mouvement horizontal
 * - Transition vers JUMP si saut
 * - Transition vers ATTACK si attaque
 * 
 * @author Lounol72
 * @version 2.0 - État Idle du Player
 */
public class IdleState implements PlayerState {
    
    @Override
    public PlayerState update(InputService inputService, PhysicsService physicsService, AnimationService animationService) {
        // Vérifier les transitions possibles
        if (inputService.wantsAttack() && animationService.canAttack()) {
            return new AttackState();
        }
        
        if (inputService.canJump() && !physicsService.isInAir()) {
            return new JumpState();
        }
        
        if (inputService.isMovingHorizontally()) {
            return new RunState();
        }
        
        if (physicsService.isInAir()) {
            return new JumpState();
        }
        
        // Mettre à jour l'animation
        animationService.setAnimation(false, false, false);
        animationService.updateAnimationTick(false, false);
        
        return this; // Rester dans l'état Idle
    }
    
    @Override
    public void render(Graphics g, AnimationService animationService, float hitboxX, float hitboxY, int xLvlOffset, int yLvlOffset) {
        animationService.render(g, hitboxX, hitboxY, xLvlOffset, yLvlOffset);
    }
    
    @Override
    public void onEnter(InputService inputService, PhysicsService physicsService, AnimationService animationService) {
        System.out.println("Entering IDLE state");
        animationService.setPlayerAction(0); // IDLE animation
    }
    
    @Override
    public void onExit(InputService inputService, PhysicsService physicsService, AnimationService animationService) {
        System.out.println("Exiting IDLE state");
    }
    
    @Override
    public String getStateName() {
        return "IDLE";
    }
}
