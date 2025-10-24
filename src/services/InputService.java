package services;

/**
 * Service responsable de la gestion des inputs du joueur
 * 
 * RESPONSABILITÉS:
 * - Gestion centralisée des inputs
 * - Validation des inputs
 * - Gestion des états d'input
 * - Interface avec le système d'input du jeu
 * 
 * @author Lounol72
 * @version 2.0 - Service extrait du Player
 */
public class InputService {
    
    // === INPUTS DE MOUVEMENT ===
    private boolean left = false;
    private boolean right = false;
    private boolean up = false;
    private boolean down = false;
    
    // === INPUTS D'ACTION ===
    private boolean jump = false;
    private boolean attack = false;
    
    // === ÉTAT DES INPUTS ===
    private boolean jumpReleased = true;
    
    public InputService() {
        // Initialisation des inputs
    }
    
    /**
     * Met à jour l'état des inputs
     * Cette méthode est appelée par le système d'input du jeu
     */
    public void updateInputs() {
        // Gestion du relâchement du bouton de saut
        if (!jump) {
            jumpReleased = true;
        }
    }
    
    /**
     * Remet tous les booléens d'input à false
     * 
     * UTILISATION:
     * - Appelé lors du changement de niveau
     * - Évite les inputs "collés" entre les transitions
     */
    public void resetDirBooleans() {
        left = false;
        right = false;
        up = false;
        down = false;
        jump = false;
        attack = false;
    }
    
    /**
     * Vérifie si le joueur peut sauter
     * 
     * @return true si le joueur peut sauter (bouton pressé et relâché)
     */
    public boolean canJump() {
        return jump && jumpReleased;
    }
    
    /**
     * Marque le bouton de saut comme pressé
     */
    public void pressJump() {
        jump = true;
        jumpReleased = false;
    }
    
    /**
     * Vérifie si le joueur bouge horizontalement
     * 
     * @return true si le joueur bouge à gauche ou à droite
     */
    public boolean isMovingHorizontally() {
        return left || right;
    }
    
    /**
     * Vérifie si le joueur bouge vers la gauche
     * 
     * @return true si le joueur bouge à gauche
     */
    public boolean isMovingLeft() {
        return left && !right;
    }
    
    /**
     * Vérifie si le joueur bouge vers la droite
     * 
     * @return true si le joueur bouge à droite
     */
    public boolean isMovingRight() {
        return right && !left;
    }
    
    /**
     * Vérifie si le joueur veut faire un drop-through
     * 
     * @return true si le joueur appuie sur down
     */
    public boolean wantsDropThrough() {
        return down;
    }
    
    /**
     * Vérifie si le joueur veut attaquer
     * 
     * @return true si le joueur appuie sur attack
     */
    public boolean wantsAttack() {
        return attack;
    }
    
    // === GETTERS/SETTERS POUR LES INPUTS ===
    
    public boolean isLeft() { 
        return left; 
    }
    
    public void setLeft(boolean left) { 
        this.left = left; 
    }

    public boolean isUp() { 
        return up; 
    }
    
    public void setUp(boolean up) { 
        this.up = up; 
    }

    public boolean isRight() { 
        return right; 
    }
    
    public void setRight(boolean right) { 
        this.right = right; 
    }

    public boolean isDown() { 
        return down; 
    }
    
    public void setDown(boolean down) { 
        this.down = down; 
    }

    public boolean isJump() { 
        return jump; 
    }
    
    public void setJump(boolean jump) { 
        this.jump = jump; 
    }

    public boolean isAttack() {
        return attack;
    }

    public void setAttack(boolean attack) {
        this.attack = attack;
    }
    
    public boolean isJumpReleased() {
        return jumpReleased;
    }
    
    public void setJumpReleased(boolean jumpReleased) {
        this.jumpReleased = jumpReleased;
    }
    
    // === MÉTHODES DE CONVERSION POUR COMPATIBILITÉ ===
    
    /**
     * Retourne un tableau des inputs de mouvement pour compatibilité
     * 
     * @return [left, up, right, down, jump, attack]
     */
    public boolean[] getMovementInputs() {
        return new boolean[]{left, up, right, down, jump, attack};
    }
    
    /**
     * Définit les inputs de mouvement depuis un tableau
     * 
     * @param inputs [left, up, right, down, jump, attack]
     */
    public void setMovementInputs(boolean[] inputs) {
        if (inputs.length >= 6) {
            left = inputs[0];
            up = inputs[1];
            right = inputs[2];
            down = inputs[3];
            jump = inputs[4];
            attack = inputs[5];
        }
    }
}

