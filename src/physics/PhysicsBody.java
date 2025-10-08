package physics;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant un corps physique avec système de forces
 * Gère la vélocité, l'accélération et l'application des forces
 */
public class PhysicsBody {
    private final Vector2D position;      // Position du corps
    private final Vector2D velocity;      // Vélocité actuelle
    private final Vector2D acceleration;  // Accélération actuelle
    private float mass;             // Masse du corps (pour les calculs de force)
    private final List<Force> forces;    // Liste des forces actives
    
    /**
     * Constructeur par défaut
     * @param x Position X initiale
     * @param y Position Y initiale
     * @param mass Masse du corps
     */
    public PhysicsBody(float x, float y, float mass) {
        this.position = new Vector2D(x, y);
        this.velocity = new Vector2D();
        this.acceleration = new Vector2D();
        this.mass = mass;
        this.forces = new ArrayList<>();
    }
    
    /**
     * Constructeur avec position vectorielle
     * @param position Position initiale
     * @param mass Masse du corps
     */
    public PhysicsBody(Vector2D position, float mass) {
        this.position = position.copy();
        this.velocity = new Vector2D();
        this.acceleration = new Vector2D();
        this.mass = mass;
        this.forces = new ArrayList<>();
    }
    
    /**
     * Ajoute une force au corps
     * @param force Force à ajouter
     */
    public void addForce(Force force) {
        forces.add(force);
    }
    
    /**
     * Ajoute une force vectorielle directe
     * @param forceVector Vecteur de force
     * @param type Type de force
     */
    public void addForce(Vector2D forceVector, ForceType type) {
        addForce(new Force(forceVector, type));
    }
    
    /**
     * Ajoute une force temporaire
     * @param forceVector Vecteur de force
     * @param type Type de force
     * @param duration Durée en frames
     */
    public void addForce(Vector2D forceVector, ForceType type, float duration) {
        addForce(new Force(forceVector, type, duration));
    }
    
    /**
     * Supprime toutes les forces d'un type donné
     * @param type Type de force à supprimer
     */
    public void removeForcesOfType(ForceType type) {
        forces.removeIf(force -> force.getType() == type);
    }
    
    /**
     * Supprime toutes les forces
     */
    public void clearForces() {
        forces.clear();
    }
    
    /**
     * Applique toutes les forces actives et calcule l'accélération
     */
    public void applyForces() {
        // Réinitialiser l'accélération
        acceleration.zero();
        
        // Appliquer toutes les forces actives
        for (Force force : forces) {
            if (force.isActive()) {
                // F = ma, donc a = F/m
                Vector2D forceAcceleration = Vector2D.multiply(force.getForce(), 1.0f / mass);
                acceleration.add(forceAcceleration);
            }
        }
    }
    
    /**
     * Met à jour la physique du corps
     * Applique les forces, met à jour la vélocité et la position
     */
    public void updatePhysics() {
        // Appliquer les forces
        applyForces();
        
        // Mettre à jour la vélocité avec l'accélération
        velocity.add(acceleration);
        
        // Mettre à jour la position avec la vélocité
        position.add(velocity);
        
        // Mettre à jour et nettoyer les forces
        forces.removeIf(force -> !force.update());
    }
    
    /**
     * Limite la vélocité à une valeur maximale
     * @param maxVelocity Vélocité maximale
     */
    public void limitVelocity(float maxVelocity) {
        velocity.limit(maxVelocity);
    }
    
    /**
     * Limite la vélocité sur chaque axe
     * @param maxVelocityX Vélocité maximale en X
     * @param maxVelocityY Vélocité maximale en Y
     */
    public void limitVelocity(float maxVelocityX, float maxVelocityY) {
        velocity.x = Math.max(-maxVelocityX, Math.min(maxVelocityX, velocity.x));
        velocity.y = Math.max(-maxVelocityY, Math.min(maxVelocityY, velocity.y));
    }
    
    /**
     * Applique une résistance (friction) à la vélocité
     * @param resistance Facteur de résistance (0.0 à 1.0)
     */
    public void applyResistance(float resistance) {
        velocity.multiply(resistance);
    }
    
    /**
     * Applique une résistance différente sur chaque axe
     * @param resistanceX Résistance en X
     * @param resistanceY Résistance en Y
     */
    public void applyResistance(float resistanceX, float resistanceY) {
        velocity.x *= resistanceX;
        velocity.y *= resistanceY;
    }
    
    /**
     * Définit la vélocité à zéro
     */
    public void stop() {
        velocity.zero();
    }
    
    /**
     * Définit la vélocité sur un axe à zéro
     * @param stopX Arrêter le mouvement en X
     * @param stopY Arrêter le mouvement en Y
     */
    public void stop(boolean stopX, boolean stopY) {
        if (stopX) velocity.x = 0;
        if (stopY) velocity.y = 0;
    }
    
    // Getters et setters
    public Vector2D getPosition() { return position; }
    public Vector2D getVelocity() { return velocity; }
    public Vector2D getAcceleration() { return acceleration; }
    public float getMass() { return mass; }
    public List<Force> getForces() { return forces; }
    
    public void setPosition(float x, float y) {
        position.set(x, y);
    }
    
    public void setPosition(Vector2D pos) {
        position.set(pos);
    }
    
    public void setVelocity(float x, float y) {
        velocity.set(x, y);
    }
    
    public void setVelocity(Vector2D vel) {
        velocity.set(vel);
    }
    
    public void setMass(float mass) {
        this.mass = mass;
    }
    
    /**
     * Vérifie si le corps a une force active d'un type donné
     * @param type Type de force à vérifier
     * @return true si une force de ce type est active
     */
    public boolean hasForceOfType(ForceType type) {
        return forces.stream().anyMatch(force -> force.getType() == type && force.isActive());
    }
    
    /**
     * Obtient la force totale d'un type donné
     * @param type Type de force
     * @return Vecteur de force totale
     */
    public Vector2D getTotalForceOfType(ForceType type) {
        Vector2D totalForce = new Vector2D();
        for (Force force : forces) {
            if (force.getType() == type && force.isActive()) {
                totalForce.add(force.getForce());
            }
        }
        return totalForce;
    }
    
    @Override
    public String toString() {
        return "PhysicsBody{pos=" + position + ", vel=" + velocity + ", acc=" + acceleration + ", mass=" + mass + "}";
    }
}
