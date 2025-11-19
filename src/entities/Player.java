package entities;

// Java standard library imports
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import config.PlayerConfig;

import static entities.PlayerStateEnum.DASH;
import static entities.PlayerStateEnum.IDLE;
import static entities.PlayerStateEnum.JUMP;
import static entities.PlayerStateEnum.RUN;
import physics.ForceType;
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
import static utilz.Constants.PLAYER.HITBOX.HITBOX_HEIGHT;
import static utilz.Constants.PLAYER.HITBOX.HITBOX_WIDTH;

import static utilz.Constants.PLAYER.JUMP_SPEED_MAX;
import static utilz.Constants.PLAYER.MAX_FALL_SPEED;
import static utilz.Constants.PLAYER.MAX_RISE_SPEED;
import static utilz.Constants.PLAYER.MAX_SPEED_X;
import static utilz.Constants.PLAYER.SPRITE.ATTACK_SPRITE_WIDTH;
import static utilz.Constants.PLAYER.SPRITE.ATTACK_SPRITE_WIDTH_DEFAULT;
import static utilz.Constants.PLAYER.SPRITE.ATTACK_X_DRAW_OFFSET;
import static utilz.Constants.PLAYER.SPRITE.ATTACK_Y_DRAW_OFFSET;
import static utilz.Constants.PLAYER.SPRITE.SPRITE_HEIGHT_DEFAULT;
import static utilz.Constants.PLAYER.SPRITE.SPRITE_WIDTH_DEFAULT;
import static utilz.Constants.PLAYER.SPRITE.X_DRAW_OFFSET;
import static utilz.Constants.PLAYER.SPRITE.Y_DRAW_OFFSET;
import utilz.HelpMethods;
import static utilz.HelpMethods.CanMoveHereAABB;
import static utilz.HelpMethods.GetEntityXPosNextToWallAABB;
import static utilz.HelpMethods.GetEntityYPosUnderRoofOrAboveFloorAABB;
import static utilz.HelpMethods.GetSpriteAmount;
import static utilz.HelpMethods.IsEntityOnFloor;
import static utilz.HelpMethods.IsEntityOnFloorAABB;
import static utilz.HelpMethods.mapAndClamp;
import utilz.LoadSave;
import utilz.PhysicsDebugger;

/**
 * Classe représentant le joueur avec système de physique avancé
 * 
 * ARCHITECTURE:
 * - Système de physique basé sur des forces (Vector2D, ForceType)
 * - Gestion des plateformes one-way avec drop-through
 * - Animations synchronisées avec l'état physique
 * - Détection de collision AABB optimisée
 * 
 * ORDRE D'EXÉCUTION CRITIQUE (updatePhysics):
 * 1. Inputs (one-way drop-through, jump)
 * 2. Forces (gravité, mouvement)
 * 3. Application des forces
 * 4. Résistances et limitations
 * 5. Collisions (peut modifier inAir)
 * 6. Mouvement final
 * 7. Synchronisation des états
 * 
 * @author Lounol72
 * @version 2.0 - Système one-way platform corrigé
 */
public class Player extends Entity {

    // ================================
    // ÉTAT DU JOUEUR
    // ================================
    private int playerAction = IDLE.ordinal();
    private int direction = 1;
    private boolean moving = false;
    private boolean inAir = false;

    // ================================
    // INPUTS
    // ================================
    private boolean left, up, right, down, jump, attack;

    // ================================
    // DASH
    // ================================
    private boolean isDashing = false;
    private int dashDurationFrames = 12; // durée du dash en frames
    private int dashTimer = 0;
    private int dashCooldownFrames = 30; // cooldown après dash
    private int dashCooldownTimer = 0;

    // ================================
    // SYSTÈME DE SAUT
    // ================================
    private int coyoteTimeCounter = COYOTE_TIME_FRAMES;
    private boolean jumpReleased = true;
    private boolean isJumping = false;

    // ================================
    // SYSTÈME DROP-THROUGH ONE-WAY
    // ================================
    private int dropThroughGraceFrames = 0;
    private static final int DROP_THROUGH_GRACE_FRAMES = 10; // Frames d'immunité après drop-through

    // ================================
    // NIVEAU ET COLLISIONS
    // ================================
    private int[][] levelData;
    private levels.Level currentLevel;

    // ================================
    // ANIMATIONS
    // ================================
    private AnimationManager animManager; // Gestionnaire unique pour toutes les animations

    // === GESTION DES SPRITES D'ATTAQUE ===
    private boolean isUsingAttackSprites = false; // Flag pour savoir si on utilise les sprites d'attaque
    private boolean isAttacking = false; // Flag pour savoir si on est en train d'attaquer

    // ================================
    // CONSTRUCTEUR
    // ================================

    /**
     * Constructeur du joueur
     * 
     * INITIALISATION:
     * - Charge les animations du joueur
     * - Initialise la hitbox avec les dimensions définies dans Constants
     * - Détermine l'état initial (air/sol) basé sur la position
     * 
     * @param x      Position X initiale du joueur
     * @param y      Position Y initiale du joueur
     * @param width  Largeur du sprite du joueur
     * @param height Hauteur du sprite du joueur
     * @param level  Niveau contenant les données de collision
     */
    public Player(float x, float y, int width, int height, levels.Level level) {
        super(x, y, width, height);
        if (!PlayerConfig.validateConfig()) {
            PlayerConfig.getConfigDescription();
            throw new IllegalStateException("Invalid Player configuration");
        }
        loadAnimations();
        initHitbox(x, y, HITBOX_WIDTH, HITBOX_HEIGHT);
        this.currentLevel = level;
        this.levelData = level.getLevelData();

        // DÉTERMINATION DE L'ÉTAT INITIAL
        // Vérifier si le joueur commence en l'air ou au sol
        inAir = !IsEntityOnFloorAABB(hitbox, level, physicsBody.getVelocity());
    }

    // ================================
    // MÉTHODES PRINCIPALES (UPDATE/RENDER)
    // ================================

    /**
     * Met à jour le joueur (appelé chaque frame)
     */
    public void update() {
        updatePhysics();
        updateAnimationTick();
        setAnimation();
    }

    /**
     * Rend le joueur à l'écran avec gestion de la direction et des animations
     * 
     * RENDU OPTIMISÉ:
     * - Calcul automatique de la direction basée sur la vélocité
     * - Gestion du flip horizontal pour les sprites
     * - Synchronisation avec l'état physique du joueur
     * 
     * @param g          Graphics context pour le rendu
     * @param xLvlOffset Offset horizontal du niveau (caméra)
     * @param yLvlOffset Offset vertical du niveau (caméra)
     */
    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        // === DÉTERMINATION DES OFFSETS SELON L'ÉTAT ===
        float currentXOffset, currentYOffset;

        if (isUsingAttackSprites) {
            // Utiliser les offsets pour les sprites d'attaque (80x48)
            currentXOffset = ATTACK_X_DRAW_OFFSET;
            currentYOffset = ATTACK_Y_DRAW_OFFSET;
        } else {
            // Utiliser les offsets pour les sprites normaux (48x48)
            currentXOffset = X_DRAW_OFFSET;
            currentYOffset = Y_DRAW_OFFSET;
        }

        // Calcul de la position de rendu avec offset de caméra
        int drawX = (int) (hitbox.x - currentXOffset) - xLvlOffset;
        int drawY = (int) (hitbox.y - currentYOffset) - yLvlOffset;

        // Mise à jour de la direction basée sur la vélocité horizontale
        updateDirection();

        // Gestion du flip horizontal pour les sprites
        // IMPORTANT: Toujours utiliser les dimensions originales pour l'affichage
        int drawWidth = (int) (isUsingAttackSprites ? ATTACK_SPRITE_WIDTH * 1.6 * direction : width * direction);

        int correctedX = (direction == -1) ? drawX + (isUsingAttackSprites ? ATTACK_SPRITE_WIDTH : width) : drawX;

        // Rendu du sprite avec animation
        // Utiliser l'index approprié selon l'état
        int animationIndex = isUsingAttackSprites ? 13 : playerAction; // Ligne 13 pour l'attaque

        g.drawImage(
                animManager.getFrame(animationIndex, true),
                correctedX, drawY,
                drawWidth, height,
                null);

        // DEBUG: Décommenter pour afficher l'état du joueur
        // if(inAir) System.out.println("inAir");

        // DEBUG: Décommenter pour afficher la hitbox
        // drawHitbox(g, xLvlOffset, yLvlOffset);
    }

    // ================================
    // MÉTHODES DE PHYSIQUE
    // ================================

    /**
     * Met à jour la physique du joueur avec le système de vecteurs
     * 
     * ORDRE D'EXÉCUTION CRITIQUE (ne pas modifier sans comprendre les dépendances)
     * :
     * 
     * 1. handleOneWayTiles() - Gère le drop-through AVANT les autres calculs
     * 2. handleJumpInput() - Gère les inputs de saut
     * 3. applyGravity() + applyMovementInput() - Applique les forces
     * 4. applyForces() + add(acceleration) - Calcule la nouvelle vélocité
     * 5. applyResistances() - Applique friction/air resistance
     * 6. limitAndNormalizeVelocity() - Limite la vélocité
     * 7. handleCollisions() - Gère les collisions (peut modifier inAir via
     * resetInAir())
     * 8. applyMovement() - Applique le mouvement final à la hitbox
     * 9. cleanupAndSync() - Nettoie les forces expirées
     * 10. updateStates() - Met à jour inAir (DÉPEND de handleCollisions())
     * 
     * IMPORTANT: updateStates() doit être appelé APRÈS handleCollisions() car
     * handleCollisions() peut appeler resetInAir() qui change l'état du joueur.
     */
    private void updatePhysics() {
        // === PHASE 1: GESTION DES INPUTS ===
        handleOneWayTiles(); // Drop-through one-way platforms
        moving = false;
        handleJumpInput(); // Jump inputs avec coyote time

        // === PHASE 2: CALCUL DES FORCES ===
        applyGravity(); // Gravité avec modificateurs Hollow Knight
        applyMovementInput(); // Forces d'input horizontal

        // === PHASE 3: APPLICATION DES FORCES ===
        physicsBody.applyForces();
        physicsBody.getVelocity().add(physicsBody.getAcceleration());

        // === PHASE 4: RÉSISTANCES ET LIMITATIONS ===
        applyResistances(); // Friction sol / résistance air
        limitAndNormalizeVelocity(); // Limites de vitesse Hollow Knight

        // === PHASE 5: COLLISIONS (PEUT MODIFIER inAir) ===
        handleCollisions(); // Collisions solides + one-way (appelle resetInAir())

        // === PHASE 6: MOUVEMENT FINAL ===
        applyMovement(); // Applique le mouvement à la hitbox

        // === PHASE 7: NETTOYAGE ET SYNCHRONISATION ===
        cleanupAndSync(); // Nettoie les forces expirées
        updateStates(); // Met à jour inAir (DÉPEND de handleCollisions)
        // Mettre à jour les timers de dash (si actif ou en cooldown)
        updateDashTimers();
    }

    /**
     * Lance un dash si possible (cooldown, pas en l'air, pas d'attaque en cours)
     */
    public void startDash() {
        // Allow dash while jumping/moving: only block if already dashing, attacking, or
        // in cooldown
        if (isDashing || isAttacking || dashCooldownTimer > 0)
            return;

        // Prefer input direction if available (arrow keys or A/D)
        if (right)
            direction = 1;
        else if (left)
            direction = -1;

        isDashing = true;
        dashTimer = dashDurationFrames;

        // Force immediate horizontal velocity for the dash
        physicsBody.getVelocity().x = utilz.Constants.PLAYER.DASH_SPEED * direction;

        // Force dash animation
        playerAction = DASH.ordinal();
        animManager.reset();

        // Debug
        //System.out.println("Dash started: speed=" + dashSpeed + " dir=" + direction + " inAir=" + inAir);
    }

    /**
     * Termine le dash en cours et démarre le cooldown
     */
    private void finishDash() {
        if (!isDashing)
            return;
        isDashing = false;
        dashCooldownTimer = dashCooldownFrames;

        // Rétablir une vitesse raisonnable (clamp à MAX_SPEED_X)
        float vx = physicsBody.getVelocity().x;
        if (Math.abs(vx) > MAX_SPEED_X) {
            physicsBody.getVelocity().x = Math.signum(vx) * MAX_SPEED_X;
        }
    }

    /**
     * Met à jour timers liés au dash
     */
    private void updateDashTimers() {
        if (isDashing) {
            dashTimer--;
            if (dashTimer <= 0) {
                finishDash();
            }
        } else if (dashCooldownTimer > 0) {
            dashCooldownTimer--;
        }
    }

    /**
     * Gère le drop-through des plateformes one-way
     * 
     * SYSTÈME DROP-THROUGH AMÉLIORÉ:
     * - Détection AABB pour vérifier si le joueur est SUR une plateforme (pas
     * dedans)
     * - Impulsion vers le bas pour garantir le passage à travers
     * - Grace frames pour éviter la re-collision immédiate
     * 
     * IMPORTANT: Pendant l'attaque, le joueur ne peut pas faire de drop-through
     * 
     * TIMING: Appelé en PHASE 1 de updatePhysics() AVANT les autres calculs
     */
    private void handleOneWayTiles() {
        // === BLOCAGE DU DROP-THROUGH PENDANT L'ATTAQUE ===
        if (isAttacking) {
            // Pendant l'attaque, on ne peut pas faire de drop-through
            // Mais on continue à décrémenter les grace frames pour éviter les bugs
            if (dropThroughGraceFrames > 0) {
                dropThroughGraceFrames--;
            }
            return; // Sortir immédiatement pour empêcher tout drop-through
        }

        // === DROP-THROUGH NORMAL (seulement si pas en train d'attaquer) ===
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
                float dropThroughForce = 2.0f; // Force suffisante pour traverser
                physicsBody.getVelocity().y = dropThroughForce;

                // 3. Activer les grace frames pour éviter la re-collision
                dropThroughGraceFrames = DROP_THROUGH_GRACE_FRAMES;
            }
        }
    }

    /**
     * Applique la gravité avec modificateurs Hollow Knight
     * 
     * SYSTÈME DE GRAVITÉ AVANCÉ:
     * - Gravité de base constante
     * - Fast fall: gravité augmentée si touche down pressée
     * - Apex bonus: gravité réduite au sommet du saut (floatiness)
     * - Application continue pendant que le joueur est en l'air
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
     * AMÉLIORATION : Gestion plus efficace des forces d'input
     * 
     * IMPORTANT: Pendant l'attaque, le joueur ne peut pas bouger horizontalement
     */
    private void applyMovementInput() {
        // === BLOCAGE DU MOUVEMENT PENDANT L'ATTAQUE ===
        if (isAttacking) {
            // Supprimer toutes les forces d'input pendant l'attaque
            physicsBody.removeForcesOfType(ForceType.INPUT);
            return; // Sortir immédiatement pour empêcher tout mouvement
        }
        // === BLOCAGE DU MOUVEMENT PENDANT LE DASH ===
        if (isDashing) {
            // Pendant le dash, on force la vitesse horizontale et on ignore les inputs
            physicsBody.removeForcesOfType(ForceType.INPUT);
            physicsBody.getVelocity().x = utilz.Constants.PLAYER.DASH_SPEED * direction;
            return;
        }

        // === MOUVEMENT NORMAL (seulement si pas en train d'attaquer) ===
        if (left && !right) {
            applyHorizontalForce(-ACCELERATION);
        } else if (right && !left) {
            applyHorizontalForce(ACCELERATION);
        } else {
            // AMÉLIORATION : Nettoyer les forces d'input seulement quand nécessaire
            physicsBody.removeForcesOfType(ForceType.INPUT);
        }
    }

    /**
     * Applique une force horizontale avec apex control
     * AMÉLIORATION : Utilise replaceForceOfType pour éviter l'accumulation
     */
    private void applyHorizontalForce(float baseAcceleration) {
        float acceleration = baseAcceleration;

        // Apex control - meilleur contrôle au sommet du saut
        if (inAir && Math.abs(physicsBody.getVelocity().y) < APEX_THRESHOLD) {
            acceleration *= APEX_ACCEL_MULT;
        }

        // AMÉLIORATION : Utilise replaceForceOfType pour éviter l'accumulation
        physicsBody.replaceForceOfType(new Vector2D(acceleration, 0), ForceType.INPUT);
    }

    /**
     * Applique les résistances (friction/air)
     */
    private void applyResistances() {
        // Ne pas appliquer de résistances horizontales pendant un dash
        if (isDashing)
            return;

        if (!inAir) {
            physicsBody.applyResistance(GROUND_FRICTION, 1.0f);
        } else {
            // Résistance uniquement sur l'axe X pour ne pas affecter le saut
            physicsBody.getVelocity().x *= AIR_RESISTANCE;
        }
    }

    /**
     * Limite la vélocité selon le style Hollow Knight
     * - Axes X et Y indépendants (pas de normalisation diagonale)
     * - Gravité STRICTEMENT limitée
     * - Montée et descente avec limites différentes
     */
    private void limitAndNormalizeVelocity() {
        Vector2D velocity = physicsBody.getVelocity();

        // Sauvegarder pour debug
        float originalVelX = velocity.x;
        float originalVelY = velocity.y;

        // === LIMITATION HORIZONTALE ===
        // Pendant le dash, ne pas clampper la vitesse X pour laisser le dash complet
        if (!isDashing) {
            velocity.x = Math.max(-MAX_SPEED_X, Math.min(MAX_SPEED_X, velocity.x));
        }

        // === LIMITATION VERTICALE (stricte, différenciée) ===
        // Montée: limite plus basse pour saut réactif
        // Descente: limite plus haute pour chute naturelle
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
     * Ordre: 1. Collisions solides, 2. Plateformes one-way
     */
    private void handleCollisions() {
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
                handleOneWayPlatformCollisions();
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
     * 
     * TIMING CRITIQUE: Cette méthode est appelée dans handleCollisions() APRÈS
     * les collisions solides. Elle peut modifier inAir via resetInAir().
     * 
     * LOGIQUE ONE-WAY:
     * - Montée (velocity.y < 0) + pas de down → ignorer (passer à travers)
     * - Descente (velocity.y >= 0) → vérifier collision et atterrir si nécessaire
     * - Down pressé → toujours ignorer (drop-through)
     * - Grace frames actives → ignorer (évite re-collision après drop-through)
     * 
     * CORRECTION VÉLOCITÉ ÉLEVÉE:
     * - Vérifie le chemin du mouvement, pas seulement la position finale
     * - Utilise un système de raycast pour détecter les collisions pendant le
     * mouvement
     * 
     * @return true si une collision one-way a été gérée
     */
    private boolean handleOneWayPlatformCollisions() {
        Vector2D velocity = physicsBody.getVelocity();

        // GRACE FRAMES: Ignorer les collisions one-way pendant le drop-through
        if (dropThroughGraceFrames > 0) {
            return false; // En drop-through → ignorer toutes les collisions one-way
        }

        // RÈGLE ONE-WAY: Ignorer si on monte ACTIVEMENT sans appuyer sur down
        if (velocity.y < 0 && !down && isJumping) {
            return false; // Montée ACTIVE → passer à travers
        }

        // CORRECTION: Vérifier le chemin du mouvement pour les vélocités élevées
        Rectangle2D.Float blockingPlatform = findOneWayPlatformInPath(velocity);

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
     * 
     * CORRECTION CRITIQUE: Cette méthode résout le problème de passage à travers
     * les plateformes one-way avec des vélocités élevées en vérifiant le chemin
     * complet du mouvement, pas seulement la position finale.
     * 
     * @param velocity Vélocité du joueur
     * @return Plateforme one-way qui bloque le chemin, ou null si aucune
     */
    private Rectangle2D.Float findOneWayPlatformInPath(Vector2D velocity) {
        // Si pas de mouvement vertical, pas de collision possible
        if (velocity.y == 0) {
            return null;
        }

        // Pour les vélocités élevées, vérifier le chemin par étapes
        float stepSize = Math.max(1.0f, Math.abs(velocity.y) / 10.0f); // Diviser en 10 étapes max
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
    private void applyMovement() {
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
    private void updateStates() {
        // Reset isJumping dès qu'on commence à tomber (velocity positive)
        if (isJumping && physicsBody.getVelocity().y >= 0) {
            isJumping = false;
            physicsBody.removeForcesOfType(ForceType.JUMP);
        }

        // Mettre à jour l'état inAir
        updateAirState();

        // Déterminer si le joueur bouge
        moving = Math.abs(physicsBody.getVelocity().x) > 0.1f || Math.abs(physicsBody.getVelocity().y) > 0.5f;
    }

    // ================================
    // MÉTHODES DE SAUT
    // ================================

    /**
     * Gère l'input de saut avec coyote time et variable jump height
     * 
     * IMPORTANT: Pendant l'attaque, le joueur ne peut pas sauter
     */
    private void handleJumpInput() {
        // === BLOCAGE DU SAUT PENDANT L'ATTAQUE ===
        if (isAttacking) {
            // Pendant l'attaque, on ne peut pas sauter
            // Mais on continue à gérer le relâchement pour éviter les bugs
            if (!jump) {
                jumpReleased = true;
            }
            return; // Sortir immédiatement pour empêcher tout saut
        }

        // === SAUT NORMAL (seulement si pas en train d'attaquer) ===
        if (jump && jumpReleased) {
            jumpReleased = false;
            if (!isJumping) {
                jump();
            }
        } else if (!jump) {
            jumpReleased = true;
            handleJumpRelease();
        }

        // Empêcher le saut continu si on touche le plafond
        if (isJumping && checkCeilingCollision()) {
            isJumping = false;
            physicsBody.getVelocity().y = FALL_SPEED_AFTER_COLLISION;
        }

        updateCoyoteTime();
    }

    /**
     * Gère le relâchement du bouton de saut (variable jump height)
     */
    private void handleJumpRelease() {
        if (isJumping && physicsBody.getVelocity().y < 0) {
            physicsBody.getVelocity().y *= utilz.Constants.PLAYER.JUMP_CUT_MULTIPLIER;
            isJumping = false;
            physicsBody.removeForcesOfType(ForceType.JUMP);
        }
    }

    /**
     * Effectue un saut avec système de validation avancé
     * 
     * CONDITIONS DE SAUT:
     * 1. Pas déjà en train de sauter (évite les sauts infinis)
     * 2. Soit au sol, soit avec coyote time disponible
     * 3. Pas de collision avec le plafond
     * 
     * SYSTÈME DE FORCES:
     * - Force de saut temporaire avec durée limitée
     * - Gestion du coyote time pour les sauts de précision
     * - Validation des collisions avant le saut
     */
    private void jump() {
        // VALIDATION DES CONDITIONS DE SAUT
        boolean canJump = !isJumping &&
                (!inAir || coyoteTimeCounter > 0) &&
                !checkCeilingCollision();

        if (!canJump) {
            return; // Conditions non remplies → pas de saut
        }

        // INITIALISATION DU SAUT
        isJumping = true;
        inAir = true;

        // Debug: Logger les paramètres du saut
        PhysicsDebugger.logJump(utilz.Constants.PLAYER.JUMP_FORCE, physicsBody.getVelocity().y);

        // APPLICATION DE LA FORCE DE SAUT (lecture dynamique depuis Constants)
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
                hitbox.x,
                hitbox.y,
                hitbox.width,
                1);

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

    // ================================
    // MÉTHODES D'ANIMATION
    // ================================

    /**
     * Met à jour le tick d'animation avec support des animations d'attaque
     */
    private void updateAnimationTick() {
        if (inAir) {
            handleAirAnimation();
        } else {
            // Utiliser l'index approprié selon l'état
            int animationIndex = isUsingAttackSprites ? 13 : playerAction; // Ligne 13 pour l'attaque

            animManager.updateFrame(animationIndex, true, isAttack());
        }
    }

    /**
     * Gère l'animation en l'air
     */
    private void handleAirAnimation() {
        int spriteCount = GetSpriteAmount(playerAction);

        int airIndex = mapAndClamp(
                physicsBody.getVelocity().y,
                JUMP_SPEED_MAX,
                -JUMP_SPEED_MAX,
                0,
                spriteCount - 1);

        animManager.setAniIndex(airIndex);
    }

    /**
     * Définit l'animation actuelle avec support des animations d'attaque
     * 
     * PRIORITÉ DES ANIMATIONS:
     * 1. ATTACK (priorité absolue, non-interruptible)
     * 2. JUMP (en l'air)
     * 3. RUN (mouvement horizontal)
     * 4. IDLE (par défaut)
     */
    private void setAnimation() {
        int startAni = playerAction;

        // === PRIORITÉ 1: ANIMATION D'ATTAQUE (NON-INTERRUPTIBLE) ===
        if (attack && !isAttacking) {
            // Commencer une nouvelle attaque
            startAttack();
        }
        // === GESTION DE LA FIN D'ANIMATION D'ATTAQUE ===
        else if (isAttacking && isAttackAnimationFinished()) {
            // L'animation d'attaque est terminée
            finishAttack();
        }
        // === PRIORITÉ 2: AUTRES ANIMATIONS (seulement si pas en train d'attaquer) ===
        else if (!isAttacking) {
            if (isDashing) {
                playerAction = DASH.ordinal();
            } else if (inAir) {
                playerAction = JUMP.ordinal();
            } else if (moving) {
                playerAction = RUN.ordinal();
            } else {
                playerAction = IDLE.ordinal();
            }
        }
        // === PENDANT L'ATTAQUE: GARDER L'ANIMATION D'ATTAQUE ===
        else if (isAttacking) {
            // Ne pas changer d'animation pendant l'attaque
            playerAction = PlayerStateEnum.ATTACK.ordinal();
        }

        // Reset de l'animation si changement d'état (mais pas pendant l'attaque)
        if (startAni != playerAction && !isAttacking) {
            animManager.reset();
        }
    }

    /**
     * Met à jour la direction du joueur
     */
    private void updateDirection() {
        if (physicsBody.getVelocity().x < 0) {
            direction = -1;
        } else if (physicsBody.getVelocity().x > 0) {
            direction = 1;
        }
    }

    /**
     * Commence une nouvelle attaque
     * 
     * Cette méthode initialise tous les flags nécessaires pour une attaque
     * et active les sprites d'attaque.
     */
    private void startAttack() {
        isAttacking = true;
        isUsingAttackSprites = true;
        playerAction = PlayerStateEnum.ATTACK.ordinal();

        // Reset de l'animation pour recommencer l'attaque
        animManager.reset();

        // Debug: Logger le début de l'attaque
        System.out.println("Starting attack animation - non-interruptible");
    }

    /**
     * Termine l'attaque en cours
     * 
     * Cette méthode remet tous les flags à leur état normal
     * et permet de reprendre les autres animations.
     */
    private void finishAttack() {
        isAttacking = false;
        isUsingAttackSprites = false;

        // Debug: Logger la fin de l'attaque
        System.out.println("Attack animation finished - returning to normal animations");
    }

    /**
     * Vérifie si l'animation d'attaque est terminée
     * 
     * Cette méthode permet de gérer automatiquement la fin des animations d'attaque
     * et de revenir aux sprites normaux.
     * 
     * @return true si l'animation d'attaque est terminée
     */
    private boolean isAttackAnimationFinished() {
        if (!isAttacking) {
            return false;
        }

        // Vérifier si l'animation d'attaque a atteint sa dernière frame
        int currentFrame = animManager.getAniIndex();
        int totalFrames = 10; // Nombre total de frames d'attaque

        // L'animation est terminée si on a atteint la dernière frame
        boolean animationFinished = currentFrame >= totalFrames - 1;

        if (animationFinished) {
            System.out.println("Attack animation completed at frame " + currentFrame + "/" + totalFrames);
        }

        return animationFinished;
    }

    /**
     * Met à jour l'état inAir - SOURCE UNIQUE DE VÉRITÉ pour la détection du sol
     * 
     * TIMING CRITIQUE: Cette méthode est appelée APRÈS handleCollisions() dans
     * updatePhysics().
     * Elle ne doit PAS surcharger inAir si handleCollisions() vient de le changer
     * via resetInAir().
     * 
     * LOGIQUE DE DÉTECTION:
     * - Utilise IsEntityOnFloorAABB() qui gère correctement les plateformes one-way
     * - Vérifie les transitions: air→sol et sol→air
     * - Ne change inAir que si l'état a vraiment changé
     * 
     * CORRECTION MAJEURE: Cette méthode gère maintenant correctement le cas où
     * le joueur est immobile (velocity.y == 0) sur une plateforme one-way.
     */
    private void updateAirState() {
        // Vérifier si le joueur est réellement sur le sol
        // CORRECTION: IsEntityOnFloorAABB() gère maintenant velocity.y == 0
        boolean isOnFloor = IsEntityOnFloorAABB(hitbox, currentLevel, physicsBody.getVelocity());

        // TRANSITION: Sol → Air (le joueur quitte le sol)
        if (!inAir && !isOnFloor) {
            inAir = true;
        }
        // TRANSITION: Air → Sol (le joueur atterrit)
        else if (inAir && isOnFloor) {
            inAir = false;
        }
        // CAS STABLE: Pas de changement d'état
        // (inAir == isOnFloor) → pas de modification nécessaire
    }

    // ================================
    // MÉTHODES DE CHARGEMENT
    // ================================

    /**
     * Charge les animations du joueur avec un seul gestionnaire
     * 
     * SYSTÈME UNIFIÉ:
     * - Lignes 0-12: Animations normales (48x48)
     * - Ligne 13: Animation d'attaque (80x48)
     * 
     * ORGANISATION:
     * - Un seul AnimationManager pour toutes les animations
     * - Basculement géré par les indices et les offsets
     */
    private void loadAnimations() {
        BufferedImage imgNormal = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
        BufferedImage imgAttack = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATTACK_ATLAS);

        // === CRÉATION D'UN SEUL TABLEAU D'ANIMATIONS ===
        BufferedImage[][] allAnimations = new BufferedImage[14][10]; // 13 animations normales + 1 attaque

        // === CHARGEMENT DES ANIMATIONS NORMALES (lignes 0-12) ===
        for (int j = 0; j < 13; j++) {
            for (int i = 0; i < allAnimations[j].length; i++) {
                allAnimations[j][i] = imgNormal.getSubimage(
                        i * SPRITE_WIDTH_DEFAULT,
                        j * SPRITE_HEIGHT_DEFAULT,
                        SPRITE_WIDTH_DEFAULT,
                        SPRITE_HEIGHT_DEFAULT);
            }
        }

        // === CHARGEMENT DE L'ANIMATION D'ATTAQUE (ligne 13) ===
        for (int i = 0; i < allAnimations[13].length; i++) {
            allAnimations[13][i] = imgAttack.getSubimage(
                    i * ATTACK_SPRITE_WIDTH_DEFAULT,
                    0,
                    ATTACK_SPRITE_WIDTH_DEFAULT,
                    SPRITE_HEIGHT_DEFAULT);
        }

        // === CRÉATION DU GESTIONNAIRE UNIQUE ===
        animManager = new AnimationManager(allAnimations);

        // Debug: Logger le chargement
        System.out.println("Animations loaded: Unified system with 14 animation sets");
    }

    /**
     * Charge les données de niveau (ancien système)
     * 
     * @deprecated Utiliser loadLevel(Level) pour le nouveau système AABB
     *             Cette méthode est conservée pour la compatibilité mais ne doit
     *             plus être utilisée.
     *             Le nouveau système AABB offre de meilleures performances et une
     *             détection plus précise.
     */
    @Deprecated
    public void loadLvlData(int[][] lvlData) {
        this.levelData = lvlData;
        if (!IsEntityOnFloor(hitbox, lvlData, physicsBody.getVelocity())) {
            inAir = true;
        }
    }

    /**
     * Charge le niveau avec les rectangles de collision AABB
     * 
     * SYSTÈME AABB OPTIMISÉ:
     * - Utilise des rectangles de collision pré-calculés
     * - Détection plus précise des plateformes one-way
     * - Meilleures performances que le système tile-based
     * 
     * @param level Niveau contenant les rectangles de collision AABB
     */
    public void loadLevel(levels.Level level) {
        this.currentLevel = level;
        this.levelData = level.getLevelData();

        // DÉTERMINATION DE L'ÉTAT INITIAL avec le système AABB
        if (!IsEntityOnFloorAABB(hitbox, level, physicsBody.getVelocity())) {
            inAir = true;
        }
    }

    // ================================
    // MÉTHODES D'INPUT (GETTERS/SETTERS)
    // ================================

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
    }

    // ================================
    // GETTERS/SETTERS POUR LES INPUTS
    // ================================

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

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public boolean isAttack() {
        return attack;
    }

    public void setAttack(boolean attack) {
        this.attack = attack;
    }

    /**
     * Vérifie si le joueur peut commencer une nouvelle attaque
     * 
     * @return true si le joueur peut attaquer (pas déjà en train d'attaquer)
     */
    public boolean canAttack() {
        return !isAttacking;
    }

    /**
     * Vérifie si le joueur est en train d'attaquer
     * 
     * @return true si le joueur est en train d'attaquer
     */
    public boolean isCurrentlyAttacking() {
        return isAttacking;
    }
}