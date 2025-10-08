package physics;

/**
 * Classe représentant un vecteur 2D pour les calculs physiques
 * Utilisée pour gérer les vitesses, accélérations et forces
 */
public class Vector2D {
    public float x;
    public float y;
    
    /**
     * Constructeur par défaut - vecteur nul
     */
    public Vector2D() {
        this.x = 0;
        this.y = 0;
    }
    
    /**
     * Constructeur avec composantes
     * @param x Composante X
     * @param y Composante Y
     */
    public Vector2D(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Constructeur de copie
     * @param other Vecteur à copier
     */
    public Vector2D(Vector2D other) {
        this.x = other.x;
        this.y = other.y;
    }
    
    // Getters et setters
    public float getX() { return x; }
    public float getY() { return y; }
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    
    /**
     * Définit les composantes du vecteur
     * @param x Composante X
     * @param y Composante Y
     */
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Copie les composantes d'un autre vecteur
     * @param other Vecteur source
     */
    public void set(Vector2D other) {
        this.x = other.x;
        this.y = other.y;
    }
    
    /**
     * Remet le vecteur à zéro
     */
    public void zero() {
        this.x = 0;
        this.y = 0;
    }
    
    /**
     * Additionne un vecteur à ce vecteur
     * @param other Vecteur à additionner
     * @return Ce vecteur modifié
     */
    public Vector2D add(Vector2D other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }
    
    /**
     * Additionne deux vecteurs et retourne le résultat
     * @param a Premier vecteur
     * @param b Deuxième vecteur
     * @return Nouveau vecteur résultant
     */
    public static Vector2D add(Vector2D a, Vector2D b) {
        return new Vector2D(a.x + b.x, a.y + b.y);
    }
    
    /**
     * Soustrait un vecteur de ce vecteur
     * @param other Vecteur à soustraire
     * @return Ce vecteur modifié
     */
    public Vector2D subtract(Vector2D other) {
        this.x -= other.x;
        this.y -= other.y;
        return this;
    }
    
    /**
     * Soustrait deux vecteurs et retourne le résultat
     * @param a Premier vecteur
     * @param b Deuxième vecteur
     * @return Nouveau vecteur résultant
     */
    public static Vector2D subtract(Vector2D a, Vector2D b) {
        return new Vector2D(a.x - b.x, a.y - b.y);
    }
    
    /**
     * Multiplie ce vecteur par un scalaire
     * @param scalar Scalaire
     * @return Ce vecteur modifié
     */
    public Vector2D multiply(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }
    
    /**
     * Multiplie un vecteur par un scalaire et retourne le résultat
     * @param vector Vecteur à multiplier
     * @param scalar Scalaire
     * @return Nouveau vecteur résultant
     */
    public static Vector2D multiply(Vector2D vector, float scalar) {
        return new Vector2D(vector.x * scalar, vector.y * scalar);
    }
    
    /**
     * Calcule la magnitude (longueur) du vecteur
     * @return Magnitude du vecteur
     */
    public float magnitude() {
        return (float) Math.sqrt(x * x + y * y);
    }
    
    /**
     * Calcule la magnitude au carré (plus rapide pour les comparaisons)
     * @return Magnitude au carré
     */
    public float magnitudeSquared() {
        return x * x + y * y;
    }
    
    /**
     * Normalise le vecteur (le rend unitaire)
     * @return Ce vecteur modifié
     */
    public Vector2D normalize() {
        float mag = magnitude();
        if (mag > 0) {
            this.x /= mag;
            this.y /= mag;
        }
        return this;
    }
    
    /**
     * Limite la magnitude du vecteur à une valeur maximale
     * @param maxMagnitude Magnitude maximale
     * @return Ce vecteur modifié
     */
    public Vector2D limit(float maxMagnitude) {
        float mag = magnitude();
        if (mag > maxMagnitude) {
            normalize();
            multiply(maxMagnitude);
        }
        return this;
    }
    
    /**
     * Crée une copie de ce vecteur
     * @return Nouvelle copie du vecteur
     */
    public Vector2D copy() {
        return new Vector2D(this);
    }
    
    /**
     * Calcule le produit scalaire avec un autre vecteur
     * @param other Autre vecteur
     * @return Produit scalaire
     */
    public float dot(Vector2D other) {
        return this.x * other.x + this.y * other.y;
    }
    
    /**
     * Calcule la distance avec un autre vecteur
     * @param other Autre vecteur
     * @return Distance entre les vecteurs
     */
    public float distance(Vector2D other) {
        float dx = this.x - other.x;
        float dy = this.y - other.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
    
    @Override
    public String toString() {
        return "Vector2D(" + x + ", " + y + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vector2D vector2D = (Vector2D) obj;
        return Float.compare(vector2D.x, x) == 0 && Float.compare(vector2D.y, y) == 0;
    }
    
    @Override
    public int hashCode() {
        return Float.hashCode(x) * 31 + Float.hashCode(y);
    }
}