package states.PlayerStates;

import java.awt.Graphics;

import services.AnimationService;
import services.InputService;
import services.PhysicsService;

/**
 * État de course du joueur
 * 
 * COMPORTEMENT:
 * - Le joueur se déplace horizontalement
 * - Transition vers IDLE si arrêt
 * - Transition vers JUMP si saut
 * - Transition vers ATTACK si attaque
 * 
 * @author Lounol72
 * @version 2.0 - État Run du Player
 */
public class RunState implements PlayerState {
    
    @Override
    public PlayerState update(InputService inputService, PhysicsService physicsService, AnimationService animationService) {
        // Vérifier les transitions possibles
        if (inputService.wantsAttack() && animationService.canAttack()) {
            return new AttackState();
        }
        
        if (inputService.canJump() && !physicsService.isInAir()) {
            return new JumpState();
        }
        
        if (physicsService.isInAir()) {
            return new JumpState();
        }
        
        if (!inputService.isMovingHorizontally()) {
            return new IdleState();
        }
        
        // Mettre à jour l'animation
        animationService.setAnimation(false, false, true);
        animationService.updateAnimationTick(false, false);
        
        return this; // Rester dans l'état Run
    }
    
    @Override
    public void render(Graphics g, AnimationService animationService, float hitboxX, float hitboxY, int xLvlOffset, int yLvlOffset) {
        animationService.render(g, hitboxX, hitboxY, xLvlOffset, yLvlOffset);
    }
    
    @Override
    public void onEnter(InputService inputService, PhysicsService physicsService, AnimationService animationService) {
        System.out.println("Entering RUN state");
        animationService.setPlayerAction(2); // RUN animation
    }
    
    @Override
    public void onExit(InputService inputService, PhysicsService physicsService, AnimationService animationService) {
        System.out.println("Exiting RUN state");
    }
    
    @Override
    public String getStateName() {
        return "RUN";
    }
}
