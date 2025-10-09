package physics;

/**
 * Enum représentant les types de forces possibles
 * AMÉLIORATION : Ajout de types de forces plus spécifiques
 */
public enum ForceType {
    GRAVITY,        // Force de gravité
    INPUT,          // Force d'input du joueur
    JUMP,           // Force de saut (temporaire)
    DASH,           // Force de dash
    KNOCKBACK,      // Force de recul
    FRICTION,       // Force de friction
    AIR_RESISTANCE, // Résistance de l'air
    WIND,           // Force de vent
    CUSTOM          // Force personnalisée
}
