package physics;

/**
 * Classe représentant une force appliquée à un objet physique
 */
public class Force {
    private final Vector2D force;
    private final ForceType type;
    private final float duration;      // Durée de la force en frames (-1 = permanente)
    private float currentTime;   // Temps écoulé depuis l'application
    private boolean active;     // Si la force est active
    
    /**
     * Constructeur pour une force permanente
     * @param force Vecteur de la force
     * @param type Type de force
     */
    public Force(Vector2D force, ForceType type) {
        this.force = force.copy();
        this.type = type;
        this.duration = -1; // Permanente
        this.currentTime = 0;
        this.active = true;
    }
    
    /**
     * Constructeur pour une force temporaire
     * @param force Vecteur de la force
     * @param type Type de force
     * @param duration Durée en frames
     */
    public Force(Vector2D force, ForceType type, float duration) {
        this.force = force.copy();
        this.type = type;
        this.duration = duration;
        this.currentTime = 0;
        this.active = true;
    }
    
    /**
     * Met à jour la force (gère le temps)
     * @return true si la force est toujours active
     */
    public boolean update() {
        if (!active) return false;
        
        if (duration > 0) {
            currentTime++;
            if (currentTime >= duration) {
                active = false;
                return false;
            }
        }
        return true;
    }
    
    /**
     * Applique un facteur de réduction à la force (pour les forces qui diminuent avec le temps)
     * @param factor Facteur de réduction (0.0 à 1.0)
     */
    public void applyReduction(float factor) {
        if (active) {
            force.multiply(factor);
        }
    }
    
    // Getters
    public Vector2D getForce() { return force; }
    public ForceType getType() { return type; }
    public float getDuration() { return duration; }
    public float getCurrentTime() { return currentTime; }
    public boolean isActive() { return active; }
    
    /**
     * Désactive la force
     */
    public void deactivate() {
        active = false;
    }
    
    /**
     * Crée une copie de cette force
     * @return Nouvelle copie de la force
     */
    public Force copy() {
        Force newForce = new Force(force, type, duration);
        newForce.currentTime = this.currentTime;
        newForce.active = this.active;
        return newForce;
    }
    
    @Override
    public String toString() {
        return "Force{" + force + ", type=" + type + ", duration=" + duration + ", active=" + active + "}";
    }
}
