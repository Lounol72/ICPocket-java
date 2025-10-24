package states.PlayerStates;

import java.awt.Graphics;

import services.AnimationService;
import services.InputService;
import services.PhysicsService;

/**
 * État de saut du joueur
 * 
 * COMPORTEMENT:
 * - Le joueur est en l'air
 * - Transition vers IDLE si atterrissage
 * - Transition vers RUN si mouvement horizontal en l'air
 * - Transition vers ATTACK si attaque (non-interruptible)
 * 
 * @author Lounol72
 * @version 2.0 - État Jump du Player
 */
public class JumpState implements PlayerState {
    
    @Override
    public PlayerState update(InputService inputService, PhysicsService physicsService, AnimationService animationService) {
        // Vérifier les transitions possibles
        if (inputService.wantsAttack() && animationService.canAttack()) {
            return new AttackState();
        }
        
        if (!physicsService.isInAir()) {
            // Atterrissage - déterminer l'état suivant
            if (inputService.isMovingHorizontally()) {
                return new RunState();
            } else {
                return new IdleState();
            }
        }
        
        // Mettre à jour l'animation
        animationService.setAnimation(false, true, false);
        animationService.updateAnimationTick(true, false);
        
        return this; // Rester dans l'état Jump
    }
    
    @Override
    public void render(Graphics g, AnimationService animationService, float hitboxX, float hitboxY, int xLvlOffset, int yLvlOffset) {
        animationService.render(g, hitboxX, hitboxY, xLvlOffset, yLvlOffset);
    }
    
    @Override
    public void onEnter(InputService inputService, PhysicsService physicsService, AnimationService animationService) {
        System.out.println("Entering JUMP state");
        animationService.setPlayerAction(5); // JUMP animation
    }
    
    @Override
    public void onExit(InputService inputService, PhysicsService physicsService, AnimationService animationService) {
        System.out.println("Exiting JUMP state");
    }
    
    @Override
    public String getStateName() {
        return "JUMP";
    }
}
