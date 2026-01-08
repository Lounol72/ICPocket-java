package ui.settings;

import static utilz.Constants.SCALE;

/**
 * Stockage centralisé des valeurs par défaut des paramètres.
 * Facilite le reset et la comparaison avec les valeurs actuelles.
 */
public class DefaultValues {
    
    // === PARAMÈTRES GAMEPLAY ===
    
    public static float getDefaultAcceleration() {
        return 0.2f * SCALE;
    }
    
    public static float getDefaultMaxSpeedX() {
        return 2f * SCALE;
    }
    
    public static float getDefaultJumpForce() {
        return -4f * SCALE;
    }
    
    public static float getDefaultGravity() {
        return 0.15f * SCALE;
    }
    
    public static float getDefaultDashSpeed() {
        return 24f * SCALE;
    }
    
    // === PARAMÈTRES PHYSICS AVANCÉS ===
    
    public static float getDefaultAirResistance() {
        return 0.98f;
    }
    
    public static float getDefaultGroundFriction() {
        return 0.88f;
    }
    
    public static float getDefaultFastFallMultiplier() {
        return 1.25f;
    }
    
    public static int getDefaultCoyoteTimeFrames() {
        return 3;
    }
    
    public static float getDefaultApexGravityMultiplier() {
        return 0.3f;
    }
    
    public static float getDefaultApexAccelerationMultiplier() {
        return 1.5f;
    }
    
    // === PARAMÈTRES PERFORMANCE ===
    
    public static int getDefaultFPS() {
        return utilz.Constants.PERFORMANCE.DEFAULT_FPS;
    }
    
    // === PARAMÈTRES DEBUG ===
    
    public static boolean getDefaultDebugPhysics() {
        return false;
    }
    
    public static boolean getDefaultRenderFpsUps() {
        return true;
    }
}

