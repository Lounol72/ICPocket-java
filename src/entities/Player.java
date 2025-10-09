package entities;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

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
import static utilz.Constants.PLAYER.JUMP_CUT_MULTIPLIER;
import static utilz.Constants.PLAYER.JUMP_FORCE;
import static utilz.Constants.PLAYER.JUMP_SPEED_MAX;
import static utilz.Constants.PLAYER.MAX_FALL_SPEED;
import static utilz.Constants.PLAYER.MAX_RISE_SPEED;
import static utilz.Constants.PLAYER.MAX_SPEED_X;
import static utilz.Constants.SCALE;
import utilz.HelpMethods;
import static utilz.HelpMethods.CanMoveHereAABB;
import static utilz.HelpMethods.GetEntityXPosNextToWallAABB;
import static utilz.HelpMethods.GetEntityYPosUnderRoofOrAboveFloorAABB;
import static utilz.HelpMethods.GetSpriteAmount;
import static utilz.HelpMethods.IsEntityOnFloor;
import static utilz.HelpMethods.IsEntityOnFloorAABB;
import static utilz.HelpMethods.IsInOneWayTile;
import static utilz.HelpMethods.mapAndClamp;
import utilz.LoadSave;
import utilz.PhysicsDebugger;

/**
 * Classe représentant le joueur avec système de physique avancé
 * Gère les mouvements, sauts, collisions et animations
 */
public class Player extends Entity {

    // ================================
    // CONSTANTES DE RENDU
    // ================================
    private final float xDrawOffset = 22 * SCALE;
    private final float yDrawOffset = 20 * SCALE;

    // ================================
    // VARIABLES D'ÉTAT DU JOUEUR
    // ================================
    private int playerAction = IDLE.ordinal();
    private int direction = 1;
    private boolean moving = false;
    private boolean attacking = false;
    private boolean inAir = false;

    // ================================
    // VARIABLES D'INPUT
    // ================================
    private boolean left, up, right, down, jump;

    // ================================
    // VARIABLES DE SAUT
    // ================================
    private int coyoteTimeCounter = COYOTE_TIME_FRAMES;
    private boolean jumpReleased = true;
    private boolean isJumping = false;

    // ================================
    // VARIABLES DE NIVEAU
    // ================================
    private int[][] levelData;
    private levels.Level currentLevel;

    // ================================
    // VARIABLES D'ANIMATION
    // ================================
    private AnimationManager animManager;

    // ================================
    // CONSTRUCTEUR
    // ================================
    
    /**
     * Constructeur du joueur
     * @param x Position X initiale
     * @param y Position Y initiale
     * @param width Largeur du joueur
     * @param height Hauteur du joueur
     * @param level Niveau actuel
     */
    public Player(float x, float y, int width, int height, levels.Level level) {
        super(x, y, width, height);
        loadAnimations();
        initHitbox(x, y, HITBOX_WIDTH, HITBOX_HEIGHT);
        this.currentLevel = level;
        this.levelData = level.getLevelData();
        
        // Vérifier si le joueur commence en l'air
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
     * Rend le joueur à l'écran
     * @param g Graphics context
     * @param xLvlOffset Offset X du niveau
     * @param yLvlOffset Offset Y du niveau
     */
    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        int drawX = (int) (hitbox.x - xDrawOffset) - xLvlOffset;
        int drawY = (int) (hitbox.y - yDrawOffset) - yLvlOffset;
    
        // Déterminer la direction basée sur la vélocité
        updateDirection();
    
        int drawWidth = direction * width;
        int correctedX = (direction == -1) ? drawX + width : drawX;
    
        g.drawImage(
            animManager.getFrame(playerAction, true),
            correctedX, drawY,
            drawWidth, height,
            null
        );
        if (inAir) {
            // Afficher l'index de l'animation en l'air
            System.out.println("Index: " + animManager.getAniIndex());
        }
        //drawHitbox(g, xLvlOffset, yLvlOffset);
    }

    // ================================
    // MÉTHODES DE PHYSIQUE
    // ================================

    /**
     * Met à jour la physique du joueur avec le système de vecteurs
     */
    private void updatePhysics() {
        // Vérifier les tiles one-way
        handleOneWayTiles();
        
        moving = false;
        
        // Gérer les inputs de saut
        handleJumpInput();
        
        // Appliquer les forces
        applyGravity();
        applyMovementInput();
        
        // Calculer et appliquer l'accélération
        physicsBody.applyForces();
        physicsBody.getVelocity().add(physicsBody.getAcceleration());
        
        // Appliquer les résistances
        applyResistances();
        
        // Limiter et normaliser la vélocité
        limitAndNormalizeVelocity();
        
        // Gérer les collisions
        handleCollisions();
        
        // Appliquer le mouvement
        applyMovement();
        
        // Nettoyer et synchroniser
        cleanupAndSync();
        
        // Mettre à jour les états
        updateStates();
    }

    /**
     * Gère les tiles one-way (passage vers le bas)
     * Permet de traverser une plateforme en appuyant sur down
     */
    private void handleOneWayTiles() {
        if (IsInOneWayTile(hitbox, levelData) && down && !inAir) {
            // Le joueur appuie sur down sur une plateforme one-way
            // Créer une "grâce period" pour éviter re-collision immédiate
            inAir = true;
            PhysicsDebugger.logPhysicsState("DROP THROUGH ONE-WAY", inAir, isJumping, physicsBody.getVelocity());
        }
    }

    /**
     * Applique la gravité avec modificateurs Hollow Knight
     */
    private void applyGravity() {
        if (inAir) {
            float gravityMultiplier = GRAVITY_MULTIPLIER_BASE;
            
            // Fast fall - gravité augmentée si appui bas
            if (down) {
                gravityMultiplier *= FAST_FALL_MULT;
            }
            
            // Apex bonus - gravité réduite au sommet (floatiness)
            if (Math.abs(physicsBody.getVelocity().y) < APEX_THRESHOLD) {
                gravityMultiplier *= APEX_GRAVITY_MULT;
            }
            
            // Ne pas appliquer gravité première frame de saut
            if (!isJumping || physicsBody.getVelocity().y >= 0) {
                Vector2D gravityForce = new Vector2D(0, GRAVITY * gravityMultiplier);
                physicsBody.replaceForceOfType(gravityForce, ForceType.GRAVITY);
                
                // Debug
                PhysicsDebugger.logForceApplied("GRAVITY", 0, GRAVITY * gravityMultiplier);
            }
        } else {
            physicsBody.removeForcesOfType(ForceType.GRAVITY);
        }
    }

    /**
     * Applique les forces d'input du joueur
     * AMÉLIORATION : Gestion plus efficace des forces d'input
     */
    private void applyMovementInput() {
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
        
        // === LIMITATION HORIZONTALE (style Hollow Knight) ===
        velocity.x = Math.max(-MAX_SPEED_X, Math.min(MAX_SPEED_X, velocity.x));
        
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
            if (!CanMoveHereAABB(hitbox.x + velocity.x, hitbox.y, hitbox.width, hitbox.height, currentLevel, velocity, down)) {
                physicsBody.getVelocity().x = 0;
                PhysicsDebugger.logCollision("WALL", velocity.x);
            }
        }
        
        // === COLLISIONS VERTICALES ===
        if (velocity.y != 0) {
            // 1. Vérifier collisions solides (murs, plafonds, sol)
            if (!CanMoveHereAABB(hitbox.x, hitbox.y + velocity.y, hitbox.width, hitbox.height, currentLevel, velocity, down)) {
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
        }
    }
    
    /**
     * Gère les collisions avec les plateformes one-way
     * Séparé des collisions solides pour clarté
     * 
     * @return true si une collision one-way a été gérée
     */
    private boolean handleOneWayPlatformCollisions() {
        Vector2D velocity = physicsBody.getVelocity();
        
        // Seulement vérifier si on descend ou si on est sur une plateforme
        if (velocity.y < 0 && !down) {
            // Montée sans down → ignorer les one-way
            return false;
        }
        
        // Créer hitbox de test avec la nouvelle position
        Rectangle2D.Float testHitbox = new Rectangle2D.Float(
            hitbox.x + velocity.x,
            hitbox.y + velocity.y,
            hitbox.width,
            hitbox.height
        );
        
        Rectangle2D.Float blockingPlatform = HelpMethods.getBlockingOneWayPlatform(
            testHitbox, 
            currentLevel, 
            velocity, 
            down
        );
        
        if (blockingPlatform != null) {
            // Collision détectée avec plateforme one-way
            PhysicsDebugger.logCollision("ONE-WAY PLATFORM", velocity.y);
            
            // Ajuster position pour être juste au-dessus de la plateforme
            float targetY = blockingPlatform.y - hitbox.height;
            hitbox.y = targetY;
            
            // Arrêter mouvement vertical et réinitialiser état air
            physicsBody.getVelocity().y = 0;
            resetInAir();
            
            return true;
        }
        
        return false;
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
        // Reset isJumping après la première frame de saut ou si on touche le sol
        if (isJumping && (physicsBody.getVelocity().y >= 0 || !inAir)) {
            isJumping = false;
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
     */
    private void handleJumpInput() {
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
            physicsBody.getVelocity().y *= JUMP_CUT_MULTIPLIER;
            isJumping = false;
        }
    }

    /**
     * Effectue un saut (style Hollow Knight: court et réactif)
     */
    private void jump() {
        if (isJumping || (inAir && coyoteTimeCounter <= 0) || checkCeilingCollision()) {
            return;
        }
        
        isJumping = true;
        inAir = true;
        
        // Debug
        PhysicsDebugger.logJump(JUMP_FORCE, physicsBody.getVelocity().y);
        
        // Force de saut temporaire
        physicsBody.addForce(
            new Vector2D(0, JUMP_FORCE), 
            ForceType.JUMP, 
            1.0f
        );
    }

    /**
     * Vérifie s'il y a une collision avec le plafond
     */
    private boolean checkCeilingCollision() {
        Rectangle2D.Float ceilingCheckHitbox = new Rectangle2D.Float(
            hitbox.x, 
            hitbox.y,
            hitbox.width, 
            1
        );
        
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
        physicsBody.stop(false, true);
    }

    // ================================
    // MÉTHODES D'ANIMATION
    // ================================

    /**
     * Met à jour le tick d'animation
     */
    private void updateAnimationTick() {
        if (inAir) {
            handleAirAnimation();
        } else {
            animManager.updateFrame(playerAction, true, attacking);
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
            spriteCount - 1
        );
        
        animManager.setAniIndex(airIndex);
    }

    /**
     * Définit l'animation actuelle
     */
    private void setAnimation() {
        int startAni = playerAction;

        if (moving) {
            playerAction = RUN.ordinal();
        } else {
            playerAction = IDLE.ordinal();
        }

        if (inAir) {
            playerAction = JUMP.ordinal();
        }

        if (startAni != playerAction) {
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
     * Met à jour l'état inAir
     */
    private void updateAirState() {
        if (!inAir && !IsEntityOnFloorAABB(hitbox, currentLevel, physicsBody.getVelocity())) {
            inAir = true;
        }
    }

    // ================================
    // MÉTHODES DE CHARGEMENT
    // ================================

    /**
     * Charge les animations du joueur
     */
    private void loadAnimations() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
        BufferedImage[][] animations = new BufferedImage[13][10];
        
        for (int j = 0; j < animations.length; j++) {
            for (int i = 0; i < animations[j].length; i++) {
                animations[j][i] = img.getSubimage(i * 48, j * 48, 48, 48);
            }
        }
        
        animManager = new AnimationManager(animations);
    }

    /**
     * Charge les données de niveau (ancien système)
     * @deprecated Utiliser loadLevel(Level) pour le nouveau système AABB
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
     */
    public void loadLevel(levels.Level level) {
        this.currentLevel = level;
        this.levelData = level.getLevelData();
        if (!IsEntityOnFloorAABB(hitbox, level, physicsBody.getVelocity())) {
            inAir = true;
        }
    }

    // ================================
    // MÉTHODES D'INPUT (GETTERS/SETTERS)
    // ================================

    /**
     * Remet tous les booléens de direction à false
     */
    public void resetDirBooleans() {
        left = false;
        right = false;
        up = false;
        down = false;
        jump = false;
    }

    // Getters et setters pour les inputs
    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }

    public boolean isLeft() { return left; }
    public void setLeft(boolean left) { this.left = left; }

    public boolean isUp() { return up; }
    public void setUp(boolean up) { this.up = up; }

    public boolean isRight() { return right; }
    public void setRight(boolean right) { this.right = right; }

    public boolean isDown() { return down; }
    public void setDown(boolean down) { this.down = down; }

    public void setJump(boolean jump) { this.jump = jump; }
}