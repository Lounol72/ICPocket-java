package states.PlayerStates;

import java.awt.Graphics;

import services.AnimationService;
import services.InputService;
import services.PhysicsService;

/**
 * État d'attaque du joueur
 * 
 * COMPORTEMENT:
 * - Le joueur attaque (non-interruptible)
 * - Transition vers IDLE/RUN/JUMP selon l'état physique après l'attaque
 * - Priorité absolue - ne peut pas être interrompu
 * 
 * @author Lounol72
 * @version 2.0 - État Attack du Player
 */
public class AttackState implements PlayerState {
    
    @Override
    public PlayerState update(InputService inputService, PhysicsService physicsService, AnimationService animationService) {
        // L'attaque est non-interruptible
        // Vérifier si l'animation d'attaque est terminée
        if (!animationService.isAttacking()) {
            // L'attaque est terminée - déterminer l'état suivant
            if (physicsService.isInAir()) {
                return new JumpState();
            } else if (inputService.isMovingHorizontally()) {
                return new RunState();
            } else {
                return new IdleState();
            }
        }
        
        // Mettre à jour l'animation d'attaque
        animationService.setAnimation(true, physicsService.isInAir(), physicsService.isMoving());
        animationService.updateAnimationTick(physicsService.isInAir(), true);
        
        return this; // Rester dans l'état Attack
    }
    
    @Override
    public void render(Graphics g, AnimationService animationService, float hitboxX, float hitboxY, int xLvlOffset, int yLvlOffset) {
        animationService.render(g, hitboxX, hitboxY, xLvlOffset, yLvlOffset);
    }
    
    @Override
    public void onEnter(InputService inputService, PhysicsService physicsService, AnimationService animationService) {
        System.out.println("Entering ATTACK state - non-interruptible");
        animationService.setPlayerAction(12); // ATTACK animation
    }
    
    @Override
    public void onExit(InputService inputService, PhysicsService physicsService, AnimationService animationService) {
        System.out.println("Exiting ATTACK state");
    }
    
    @Override
    public String getStateName() {
        return "ATTACK";
    }
}
