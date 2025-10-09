package utilz;

import physics.Vector2D;

/**
 * Classe utilitaire pour le debug de la physique
 * Permet d'activer/désactiver les logs de physique via DEBUG_PHYSICS
 * Format des logs optimisé pour l'analyse en temps réel
 */
public class PhysicsDebugger {
    private static final String PREFIX = "[PHYSICS] ";
    
    /**
     * Log la vélocité actuelle avec contexte
     * @param context Contexte du log (ex: "JUMP", "FALL", "COLLISION")
     * @param velocity Vecteur vélocité à logger
     */
    public static void logVelocity(String context, Vector2D velocity) {
        if (!Constants.PLAYER.DEBUG_PHYSICS) return;
        System.out.printf("%s%s - Velocity: X=%.3f, Y=%.3f, Mag=%.3f%n", 
            PREFIX, context, velocity.x, velocity.y, velocity.magnitude());
    }
    
    /**
     * Log quand une vélocité est limitée sur un axe
     * @param axis Axe concerné ("X" ou "Y")
     * @param before Valeur avant limitation
     * @param after Valeur après limitation
     * @param limit Limite appliquée
     */
    public static void logVelocityLimited(String axis, float before, float after, float limit) {
        if (!Constants.PLAYER.DEBUG_PHYSICS) return;
        System.out.printf("%s%s velocity LIMITED: %.3f -> %.3f (limit: %.3f)%n", 
            PREFIX, axis, before, after, limit);
    }
    
    /**
     * Log l'application d'une force
     * @param forceType Type de force appliquée
     * @param x Composante X de la force
     * @param y Composante Y de la force
     */
    public static void logForceApplied(String forceType, float x, float y) {
        if (!Constants.PLAYER.DEBUG_PHYSICS) return;
        System.out.printf("%sForce %s applied: (%.3f, %.3f)%n", PREFIX, forceType, x, y);
    }
    
    /**
     * Log un saut avec détails
     * @param jumpForce Force de saut appliquée
     * @param currentVelocityY Vélocité Y actuelle avant saut
     */
    public static void logJump(float jumpForce, float currentVelocityY) {
        if (!Constants.PLAYER.DEBUG_PHYSICS) return;
        System.out.printf("%sJUMP! Force: %.3f, Current Y velocity: %.3f%n", 
            PREFIX, jumpForce, currentVelocityY);
    }
    
    /**
     * Log une collision
     * @param axis Type de collision ("FLOOR", "CEILING", "WALL")
     * @param velocity Vélocité au moment de la collision
     */
    public static void logCollision(String axis, float velocity) {
        if (!Constants.PLAYER.DEBUG_PHYSICS) return;
        System.out.printf("%sCOLLISION on %s axis (velocity: %.3f)%n", PREFIX, axis, velocity);
    }
    
    /**
     * Log l'état général de la physique
     * @param context Contexte du log
     * @param inAir Si le joueur est en l'air
     * @param isJumping Si le joueur saute
     * @param velocity Vélocité actuelle
     */
    public static void logPhysicsState(String context, boolean inAir, boolean isJumping, Vector2D velocity) {
        if (!Constants.PLAYER.DEBUG_PHYSICS) return;
        System.out.printf("%s%s - InAir: %s, Jumping: %s, Vel: (%.3f, %.3f)%n", 
            PREFIX, context, inAir, isJumping, velocity.x, velocity.y);
    }
    
    /**
     * Log une interaction avec plateforme one-way
     * @param action Action effectuée ("BLOCK", "PASS THROUGH", "DROP THROUGH")
     * @param velocity Vélocité au moment de l'interaction
     */
    public static void logOneWayPlatform(String action, float velocity) {
        if (!Constants.PLAYER.DEBUG_PHYSICS) return;
        System.out.printf("%sONE-WAY %s (velocity: %.3f)%n", PREFIX, action, velocity);
    }
}
