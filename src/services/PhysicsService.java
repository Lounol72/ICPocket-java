package services;

import java.awt.geom.Rectangle2D;

import levels.Level;
import physics.ForceType;
import physics.PhysicsBody;
import physics.Vector2D;
import static utilz.Constants.PLAYER.ACCELERATION;
import static utilz.Constants.PLAYER.AIR_RESISTANCE;
import static utilz.Constants.PLAYER.APEX_ACCEL_MULT;
import static utilz.Constants.PLAYER.APEX_GRAVITY_MULT;
import static utilz.Constants.PLAYER.APEX_THRESHOLD;
import static utilz.Constants.PLAYER.COYOTE_TIME_FRAMES;
import static utilz.Constants.PLAYER.FALL_SPEED_AFTER_COLLISION;
import static utilz.Constants.PLAYER.FAST_FALL_MULT;
import static utilz.Constants.PLAYER.GRAVITY;
import static utilz.Constants.PLAYER.GRAVITY_MULTIPLIER_BASE;
import static utilz.Constants.PLAYER.GROUND_FRICTION;
import static utilz.Constants.PLAYER.MAX_FALL_SPEED;
import static utilz.Constants.PLAYER.MAX_RISE_SPEED;
import static utilz.Constants.PLAYER.MAX_SPEED_X;
import utilz.HelpMethods;
import static utilz.HelpMethods.CanMoveHereAABB;
import static utilz.HelpMethods.GetEntityXPosNextToWallAABB;
import static utilz.HelpMethods.GetEntityYPosUnderRoofOrAboveFloorAABB;
import static utilz.HelpMethods.IsEntityOnFloorAABB;
import utilz.PhysicsDebugger;

/**
 * Service responsable de la gestion de la physique du joueur
 * 
 * RESPONSABILITÉS:
 * - Application de la gravité avec modificateurs
 * - Gestion des forces d'input
 * - Application des résistances (friction/air)
 * - Limitation de la vélocité
 * - Gestion des collisions
 * - Gestion des plateformes one-way
 * 
 * @author Lounol72
 * @version 2.0 - Service extrait du Player
 */
public class PhysicsService {

    private final PhysicsBody physicsBody;
    private final Level currentLevel;

    // === ÉTAT DU JOUEUR ===
    private boolean inAir = false;
    private boolean isJumping = false;
    private int coyoteTimeCounter = COYOTE_TIME_FRAMES;
    private int dropThroughGraceFrames = 0;
    private static final int DROP_THROUGH_GRACE_FRAMES = 10;

    // === INPUTS ===
    private boolean left, right, down, jump;

    public PhysicsService(PhysicsBody physicsBody, Level level) {
        this.physicsBody = physicsBody;
        this.currentLevel = level;
    }

    /**
     * Met à jour la physique du joueur avec le système de vecteurs
     * 
     * ORDRE D'EXÉCUTION CRITIQUE:
     * 1. handleOneWayTiles() - Gère le drop-through AVANT les autres calculs
     * 2. handleJumpInput() - Gère les inputs de saut
     * 3. applyGravity() + applyMovementInput() - Applique les forces
     * 4. applyForces() + add(acceleration) - Calcule la nouvelle vélocité
     * 5. applyResistances() - Applique friction/air resistance
     * 6. limitAndNormalizeVelocity() - Limite la vélocité
     * 7. handleCollisions() - Gère les collisions (peut modifier inAir)
     * 8. applyMovement() - Applique le mouvement final à la hitbox
     * 9. cleanupAndSync() - Nettoie les forces expirées
     * 10. updateStates() - Met à jour inAir (DÉPEND de handleCollisions())
     */
    public void updatePhysics(Rectangle2D.Float hitbox) {
        // === PHASE 1: GESTION DES INPUTS ===
        handleOneWayTiles(hitbox);
        handleJumpInput();

        // === PHASE 2: CALCUL DES FORCES ===
        applyGravity();
        applyMovementInput();

        // === PHASE 3: APPLICATION DES FORCES ===
        physicsBody.applyForces();
        physicsBody.getVelocity().add(physicsBody.getAcceleration());

        // === PHASE 4: RÉSISTANCES ET LIMITATIONS ===
        applyResistances();
        limitAndNormalizeVelocity();

        // === PHASE 5: COLLISIONS (PEUT MODIFIER inAir) ===
        handleCollisions(hitbox);

        // === PHASE 6: MOUVEMENT FINAL ===
        applyMovement(hitbox);

        // === PHASE 7: NETTOYAGE ET SYNCHRONISATION ===
        cleanupAndSync();
        updateStates(hitbox);
    }

    /**
     * Gère le drop-through des plateformes one-way
     */
    private void handleOneWayTiles(Rectangle2D.Float hitbox) {
        // Décrémenter les grace frames si actives
        if (dropThroughGraceFrames > 0) {
            dropThroughGraceFrames--;
        }

        // Vérifier si le joueur est debout sur une plateforme one-way
        if (down && !inAir && dropThroughGraceFrames == 0) {
            // Créer une hitbox de test légèrement en dessous pour détecter la plateforme
            Rectangle2D.Float groundCheckHitbox = new Rectangle2D.Float(
                    hitbox.x,
                    hitbox.y + hitbox.height + 1,
                    hitbox.width,
                    1);

            // Vérifier s'il y a une plateforme one-way sous les pieds
            if (HelpMethods.checkOneWayPlatformCollision(groundCheckHitbox, currentLevel, physicsBody.getVelocity(),
                    false)) {
                // DROP-THROUGH DÉTECTÉ → Forcer le passage à travers
                PhysicsDebugger.logPhysicsState("DROP THROUGH ONE-WAY", inAir, isJumping, physicsBody.getVelocity());

                // 1. Marquer comme en l'air
                inAir = true;

                // 2. Ajouter une petite impulsion vers le bas pour garantir le passage
                float dropThroughForce = 2.0f;
                physicsBody.getVelocity().y = dropThroughForce;

                // 3. Activer les grace frames pour éviter la re-collision
                dropThroughGraceFrames = DROP_THROUGH_GRACE_FRAMES;
            }
        }
    }

    /**
     * Applique la gravité avec modificateurs Hollow Knight
     */
    private void applyGravity() {
        if (inAir) {
            float gravityMultiplier = GRAVITY_MULTIPLIER_BASE;

            // FAST FALL: Gravité augmentée si appui sur down
            if (down) {
                gravityMultiplier *= FAST_FALL_MULT;
            }

            // APEX BONUS: Gravité réduite au sommet du saut (effet floatiness)
            if (Math.abs(physicsBody.getVelocity().y) < APEX_THRESHOLD) {
                gravityMultiplier *= APEX_GRAVITY_MULT;
            }

            // Application de la force de gravité
            Vector2D gravityForce = new Vector2D(0, GRAVITY * gravityMultiplier);
            physicsBody.replaceForceOfType(gravityForce, ForceType.GRAVITY);

            // Debug: Logger la force appliquée
            PhysicsDebugger.logForceApplied("GRAVITY", 0, GRAVITY * gravityMultiplier);
        } else {
            // Le joueur est au sol → supprimer la gravité
            physicsBody.removeForcesOfType(ForceType.GRAVITY);
        }
    }

    /**
     * Applique les forces d'input du joueur
     */
    private void applyMovementInput() {
        if (left && !right) {
            applyHorizontalForce(-ACCELERATION);
        } else if (right && !left) {
            applyHorizontalForce(ACCELERATION);
        } else {
            // Nettoyer les forces d'input seulement quand nécessaire
            physicsBody.removeForcesOfType(ForceType.INPUT);
        }
    }

    /**
     * Applique une force horizontale avec apex control
     */
    private void applyHorizontalForce(float baseAcceleration) {
        float acceleration = baseAcceleration;

        // Apex control - meilleur contrôle au sommet du saut
        if (inAir && Math.abs(physicsBody.getVelocity().y) < APEX_THRESHOLD) {
            acceleration *= APEX_ACCEL_MULT;
        }

        // Utilise replaceForceOfType pour éviter l'accumulation
        physicsBody.replaceForceOfType(new Vector2D(acceleration, 0), ForceType.INPUT);
    }

    /**
     * Applique les résistances (friction/air)
     */
    private void applyResistances() {
        if (!inAir) {
            physicsBody.applyResistance(GROUND_FRICTION, 1.0f);
        } else {
            // Résistance uniquement sur l'axe X pour ne pas affecter le saut
            physicsBody.getVelocity().x *= AIR_RESISTANCE;
        }
    }

    /**
     * Limite la vélocité selon le style Hollow Knight
     */
    private void limitAndNormalizeVelocity() {
        Vector2D velocity = physicsBody.getVelocity();

        // Sauvegarder pour debug
        float originalVelX = velocity.x;
        float originalVelY = velocity.y;

        // === LIMITATION HORIZONTALE ===
        velocity.x = Math.max(-MAX_SPEED_X, Math.min(MAX_SPEED_X, velocity.x));

        // === LIMITATION VERTICALE (stricte, différenciée) ===
        if (velocity.y < 0) {
            // Monte
            velocity.y = Math.max(-MAX_RISE_SPEED, velocity.y);
        } else {
            // Tombe - GRAVITÉ STRICTEMENT LIMITÉE
            velocity.y = Math.min(MAX_FALL_SPEED, velocity.y);
        }

        // Debug: Logger si limitation appliquée
        if (Math.abs(originalVelX - velocity.x) > 0.001f) {
            PhysicsDebugger.logVelocityLimited("X", originalVelX, velocity.x, MAX_SPEED_X);
        }
        if (Math.abs(originalVelY - velocity.y) > 0.001f) {
            float limit = velocity.y < 0 ? MAX_RISE_SPEED : MAX_FALL_SPEED;
            PhysicsDebugger.logVelocityLimited("Y", originalVelY, velocity.y, limit);
        }
    }

    /**
     * Gère les collisions avec le système AABB
     */
    private void handleCollisions(Rectangle2D.Float hitbox) {
        Vector2D velocity = physicsBody.getVelocity();

        // === COLLISIONS HORIZONTALES (murs) ===
        if (velocity.x != 0) {
            if (!CanMoveHereAABB(hitbox.x + velocity.x, hitbox.y, hitbox.width, hitbox.height, currentLevel, velocity,
                    down)) {
                physicsBody.getVelocity().x = 0;
                PhysicsDebugger.logCollision("WALL", velocity.x);
            }
        }

        // === COLLISIONS VERTICALES ===
        if (velocity.y != 0) {
            // 1. Vérifier collisions solides (murs, plafonds, sol)
            if (!CanMoveHereAABB(hitbox.x, hitbox.y + velocity.y, hitbox.width, hitbox.height, currentLevel, velocity,
                    down)) {
                handleVerticalCollision(velocity.y);
            }
            // 2. Vérifier plateformes one-way (seulement si pas déjà en collision solide)
            else if (velocity.y >= 0) {
                // Seulement pour la descente
                handleOneWayPlatformCollisions(hitbox);
            }
        }
    }

    /**
     * Gère une collision verticale
     */
    private void handleVerticalCollision(float collisionVelocityY) {
        PhysicsDebugger.logCollision(collisionVelocityY > 0 ? "FLOOR" : "CEILING", collisionVelocityY);

        physicsBody.getVelocity().y = 0;

        if (collisionVelocityY > 0) {
            // Toucher le sol en tombant
            resetInAir();
        } else {
            // Toucher le plafond en sautant
            physicsBody.getVelocity().y = FALL_SPEED_AFTER_COLLISION;

            isJumping = false;
            physicsBody.removeForcesOfType(ForceType.JUMP);
        }
    }

    /**
     * Gère les collisions avec les plateformes one-way
     */
    private boolean handleOneWayPlatformCollisions(Rectangle2D.Float hitbox) {
        Vector2D velocity = physicsBody.getVelocity();

        // GRACE FRAMES: Ignorer les collisions one-way pendant le drop-through
        if (dropThroughGraceFrames > 0) {
            return false;
        }

        // RÈGLE ONE-WAY: Ignorer si on monte ACTIVEMENT sans appuyer sur down
        if (velocity.y < 0 && !down && isJumping) {
            return false;
        }

        // Vérifier le chemin du mouvement pour les vélocités élevées
        Rectangle2D.Float blockingPlatform = findOneWayPlatformInPath(hitbox, velocity);

        if (blockingPlatform != null) {
            // COLLISION DÉTECTÉE → Atterrir sur la plateforme
            PhysicsDebugger.logCollision("ONE-WAY PLATFORM (HIGH VELOCITY)", velocity.y);

            // Positionner le joueur juste au-dessus de la plateforme
            float targetY = blockingPlatform.y - hitbox.height;
            hitbox.y = targetY;

            // Arrêter le mouvement vertical et marquer comme au sol
            physicsBody.getVelocity().y = 0;
            resetInAir();

            return true;
        }

        return false;
    }

    /**
     * Trouve une plateforme one-way dans le chemin du mouvement
     */
    private Rectangle2D.Float findOneWayPlatformInPath(Rectangle2D.Float hitbox, Vector2D velocity) {
        if (velocity.y == 0) {
            return null;
        }

        // Pour les vélocités élevées, vérifier le chemin par étapes
        float stepSize = Math.max(1.0f, Math.abs(velocity.y) / 10.0f);
        float totalDistance = Math.abs(velocity.y);
        int steps = Math.max(1, (int) (totalDistance / stepSize));

        for (int i = 1; i <= steps; i++) {
            float progress = (float) i / steps;
            float currentX = hitbox.x + velocity.x * progress;
            float currentY = hitbox.y + velocity.y * progress;

            // Créer une hitbox de test pour cette position intermédiaire
            Rectangle2D.Float testHitbox = new Rectangle2D.Float(
                    currentX,
                    currentY,
                    hitbox.width,
                    hitbox.height);

            // Vérifier s'il y a une plateforme one-way qui bloque à cette position
            Rectangle2D.Float blockingPlatform = HelpMethods.getBlockingOneWayPlatform(
                    testHitbox,
                    currentLevel,
                    velocity,
                    down);

            if (blockingPlatform != null) {
                return blockingPlatform;
            }
        }

        return null;
    }

    /**
     * Applique le mouvement à la hitbox
     */
    private void applyMovement(Rectangle2D.Float hitbox) {
        float velocityXBeforeCollision = physicsBody.getVelocity().x;
        float velocityYBeforeCollision = physicsBody.getVelocity().y;

        hitbox.x += physicsBody.getVelocity().x;
        hitbox.y += physicsBody.getVelocity().y;

        // Ajuster la position en cas de collision
        if (velocityXBeforeCollision != 0 && physicsBody.getVelocity().x == 0) {
            hitbox.x = GetEntityXPosNextToWallAABB(hitbox, velocityXBeforeCollision, currentLevel);
        }

        if (velocityYBeforeCollision != 0 && physicsBody.getVelocity().y == 0) {
            hitbox.y = GetEntityYPosUnderRoofOrAboveFloorAABB(hitbox, velocityYBeforeCollision, currentLevel);
        }
    }

    /**
     * Nettoie et synchronise les états
     */
    private void cleanupAndSync() {
        updatePhysicsFromHitbox();
        physicsBody.getForces().removeIf(force -> !force.update());
    }

    /**
     * Met à jour les états du joueur
     */
    private void updateStates(Rectangle2D.Float hitbox) {
        // Reset isJumping dès qu'on commence à tomber (velocity positive)
        if (isJumping && physicsBody.getVelocity().y >= 0) {
            isJumping = false;
            physicsBody.removeForcesOfType(ForceType.JUMP);
        }

        // Mettre à jour l'état inAir
        updateAirState(hitbox);
    }

    /**
     * Gère l'input de saut avec coyote time et variable jump height
     */
    private void handleJumpInput() {
        if (jump && !isJumping) {
            if (!isJumping) {
                performJump();
            }
        }

        // Empêcher le saut continu si on touche le plafond
        if (isJumping && checkCeilingCollision()) {
            isJumping = false;
            physicsBody.getVelocity().y = FALL_SPEED_AFTER_COLLISION;
        }

        updateCoyoteTime();
    }

    /**
     * Effectue un saut avec système de validation avancé
     */
    private void performJump() {
        // VALIDATION DES CONDITIONS DE SAUT
        boolean canJump = !isJumping &&
                (!inAir || coyoteTimeCounter > 0) &&
                !checkCeilingCollision();

        if (!canJump) {
            return;
        }

        // INITIALISATION DU SAUT
        isJumping = true;
        inAir = true;

        // Debug: Logger les paramètres du saut (valeurs lues dynamiquement)
        PhysicsDebugger.logJump(utilz.Constants.PLAYER.JUMP_FORCE, physicsBody.getVelocity().y);

        // APPLICATION DE LA FORCE DE SAUT
        physicsBody.addForce(
                new Vector2D(0, utilz.Constants.PLAYER.JUMP_FORCE),
                ForceType.JUMP,
                utilz.Constants.PLAYER.JUMP_MAX_TIME);
    }

    /**
     * Vérifie s'il y a une collision avec le plafond
     */
    private boolean checkCeilingCollision() {
        Rectangle2D.Float ceilingCheckHitbox = new Rectangle2D.Float(
                physicsBody.getPosition().x,
                physicsBody.getPosition().y,
                physicsBody.getPosition().x + 48, // Largeur par défaut
                physicsBody.getPosition().y + 1);

        for (Rectangle2D.Float collisionRect : currentLevel.getSolidCollisions()) {
            if (ceilingCheckHitbox.intersects(collisionRect)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Met à jour le coyote time
     */
    private void updateCoyoteTime() {
        if (!inAir) {
            coyoteTimeCounter = COYOTE_TIME_FRAMES;
        } else if (coyoteTimeCounter > 0) {
            coyoteTimeCounter--;
        }
    }

    /**
     * Remet le joueur au sol
     */
    private void resetInAir() {
        inAir = false;
        isJumping = false;
        coyoteTimeCounter = COYOTE_TIME_FRAMES;
        physicsBody.removeForcesOfType(ForceType.JUMP);
        physicsBody.stop(false, true);
    }

    /**
     * Met à jour l'état inAir - SOURCE UNIQUE DE VÉRITÉ pour la détection du sol
     */
    private void updateAirState(Rectangle2D.Float hitbox) {
        // Vérifier si le joueur est réellement sur le sol
        boolean isOnFloor = IsEntityOnFloorAABB(hitbox, currentLevel, physicsBody.getVelocity());

        // TRANSITION: Sol → Air (le joueur quitte le sol)
        if (!inAir && !isOnFloor) {
            inAir = true;
        }
        // TRANSITION: Air → Sol (le joueur atterrit)
        else if (inAir && isOnFloor) {
            inAir = false;
        }
    }

    /**
     * Met à jour la position du PhysicsBody en fonction de la hitbox
     */
    private void updatePhysicsFromHitbox() {
        // Cette méthode sera implémentée selon les besoins
    }

    // === GETTERS/SETTERS ===

    public boolean isInAir() {
        return inAir;
    }

    public boolean isJumping() {
        return isJumping;
    }

    public void setInputs(boolean left, boolean right, boolean down, boolean jump) {
        this.left = left;
        this.right = right;
        this.down = down;
        this.jump = jump;
    }

    public Vector2D getVelocity() {
        return physicsBody.getVelocity();
    }

    public boolean isMoving() {
        return Math.abs(physicsBody.getVelocity().x) > 0.1f || Math.abs(physicsBody.getVelocity().y) > 0.5f;
    }
}
