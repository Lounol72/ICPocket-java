package physics;

/**
 * Enum représentant les types de forces possibles
 */
public enum ForceType {
    GRAVITY,        // Force de gravité
    INPUT,          // Force d'input du joueur
    DASH,           // Force de dash
    KNOCKBACK,      // Force de recul
    FRICTION,       // Force de friction
    AIR_RESISTANCE, // Résistance de l'air
    CUSTOM          // Force personnalisée
}
