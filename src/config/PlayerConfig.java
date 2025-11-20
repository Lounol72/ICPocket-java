package config;

/**
 * Configuration externalisée pour le joueur
 * 
 * Cette classe centralise toutes les constantes et paramètres du joueur
 * pour faciliter la maintenance et la personnalisation.
 * 
 * @author Lounol72
 * @version 2.0 - Configuration externalisée
 */
public class PlayerConfig {
    
    // ================================
    // CONSTANTES DE PHYSIQUE
    // ================================
    
    // === GRAVITÉ ET MOUVEMENT ===
    public static final float GRAVITY = 0.04f;
    public static final float GRAVITY_MULTIPLIER_BASE = 1.0f;
    public static final float FAST_FALL_MULT = 2.0f;
    public static final float APEX_GRAVITY_MULT = 0.5f;
    public static final float APEX_THRESHOLD = 0.5f;
    
    // === ACCÉLÉRATION ET VITESSE ===
    public static final float ACCELERATION = 0.5f;
    public static final float MAX_SPEED_X = 5.0f;
    public static final float MAX_RISE_SPEED = 6.0f;
    public static final float MAX_FALL_SPEED = 8.0f;
    public static final float APEX_ACCEL_MULT = 1.2f;
    
    // === RÉSISTANCES ===
    public static final float GROUND_FRICTION = 0.8f;
    public static final float AIR_RESISTANCE = 0.95f;
    
    // === SAUT ===
    public static final float JUMP_FORCE = 15.0f;
    public static final int JUMP_MAX_TIME = 20;
    public static final float JUMP_SPEED_MAX = 6.0f;
    public static final float JUMP_CUT_MULTIPLIER = 0.5f;
    public static final int COYOTE_TIME_FRAMES = 5;
    public static final float FALL_SPEED_AFTER_COLLISION = 0.5f;
    
    // ================================
    // CONSTANTES DE HITBOX
    // ================================
    
    public static final float HITBOX_WIDTH = 20.0f;
    public static final float HITBOX_HEIGHT = 27.0f;
    
    // ================================
    // CONSTANTES D'ANIMATION
    // ================================
    
    // === SPRITES NORMaux ===
    public static final int SPRITE_WIDTH_DEFAULT = 48;
    public static final int SPRITE_HEIGHT_DEFAULT = 48;
    public static final float X_DRAW_OFFSET = 21.0f;
    public static final float Y_DRAW_OFFSET = 4.0f;
    
    // === SPRITES D'ATTAQUE ===
    public static final int ATTACK_SPRITE_WIDTH = 80;
    public static final int ATTACK_SPRITE_WIDTH_DEFAULT = 80;
    public static final float ATTACK_X_DRAW_OFFSET = 26.0f;
    public static final float ATTACK_Y_DRAW_OFFSET = 4.0f;
    
    // === VITESSE D'ANIMATION ===
    public static final int ANI_SPEED = 25;
    
    // ================================
    // CONSTANTES DE PLATEFORME ONE-WAY
    // ================================
    
    public static final int DROP_THROUGH_GRACE_FRAMES = 10;
    public static final float DROP_THROUGH_FORCE = 2.0f;
    
    // ================================
    // CONSTANTES DE MASSE
    // ================================
    
    public static final float MASS = 1.0f;
    
    // ================================
    // MÉTHODES DE CONFIGURATION
    // ================================
    
    /**
     * @test
     * Valide la configuration du joueur afin de détecter les erreurs potentielles avant l'exécution
     * 
     * @return true si la configuration est valide
     */
    public static boolean validateConfig() {
        // Vérifier les valeurs critiques
        if (GRAVITY <= 0) {
            System.err.println("GRAVITY must be positive");
            return false;
        }
        
        if (MAX_SPEED_X <= 0) {
            System.err.println("MAX_SPEED_X must be positive");
            return false;
        }
        
        if (JUMP_FORCE <= 0) {
            System.err.println("JUMP_FORCE must be positive");
            return false;
        }
        
        if (COYOTE_TIME_FRAMES < 0) {
            System.err.println("COYOTE_TIME_FRAMES must be non-negative");
            return false;
        }
        
        return true;
    }
    
    /**
     * Affiche la configuration actuelle
     */
    public static void printConfig() {
        System.out.println("=== PLAYER CONFIGURATION ===");
        System.out.println("Gravity: " + GRAVITY);
        System.out.println("Max Speed X: " + MAX_SPEED_X);
        System.out.println("Jump Force: " + JUMP_FORCE);
        System.out.println("Coyote Time: " + COYOTE_TIME_FRAMES + " frames");
        System.out.println("Hitbox: " + HITBOX_WIDTH + "x" + HITBOX_HEIGHT);
        System.out.println("Animation Speed: " + ANI_SPEED);
        System.out.println("=============================");
    }
    
    /**
     * Retourne une description de la configuration
     * 
     * @return Description de la configuration
     */
    public static String getConfigDescription() {
        return String.format(
            "Player Configuration: Gravity=%.2f, MaxSpeed=%.1f, JumpForce=%.1f, CoyoteTime=%d",
            GRAVITY, MAX_SPEED_X, JUMP_FORCE, COYOTE_TIME_FRAMES
        );
    }
}
