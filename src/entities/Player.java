package entities;

import java.awt.Graphics;
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
import static utilz.Constants.PLAYER.GROUND_FRICTION;
import static utilz.Constants.PLAYER.HITBOX.HITBOX_HEIGHT;
import static utilz.Constants.PLAYER.HITBOX.HITBOX_WIDTH;
import static utilz.Constants.PLAYER.JUMP_CUT_MULTIPLIER;
import static utilz.Constants.PLAYER.JUMP_FORCE;
import static utilz.Constants.PLAYER.JUMP_SPEED_MAX;
import static utilz.Constants.PLAYER.MAX_FALL_SPEED;
import static utilz.Constants.PLAYER.MAX_SPEED_X;
import static utilz.Constants.SCALE;
import static utilz.HelpMethods.CanMoveHere;
import static utilz.HelpMethods.GetEntityXPosNextToWall;
import static utilz.HelpMethods.GetEntityYPosUnderRoofOrAboveFloor;
import static utilz.HelpMethods.GetSpriteAmount;
import static utilz.HelpMethods.IsEntityOnFloor;
import static utilz.HelpMethods.IsInOneWayTile;
import static utilz.HelpMethods.mapAndClamp;
import utilz.LoadSave;



public class Player extends Entity {

    int playerAction = IDLE.ordinal();

    private AnimationManager animManager;
    // Map des états disponibles
    //private EnumMap<PlayerStateEnum, PlayerState> states;

    // Etat courant (commenté pour compatibilité future)
    // private PlayerState currentState;
    // private PlayerStateEnum currentStateEnum;

    // Bouleens de controle
    private boolean moving = false, attacking = false;
    private boolean left, up, right, down, jump;
    private boolean inAir = false;
    private int[][] levelData;
    
    // Jump state
    private int coyoteTimeCounter = COYOTE_TIME_FRAMES;      // Timer coyote time
    private boolean jumpReleased = true;    // Si le bouton jump a été relâché
    private boolean isJumping = false;      // Si on est en train de sauter

    private final float xDrawOffset = 22 * SCALE, yDrawOffset = 20 * SCALE;

    // Direction of the player
    private int direction = 1;

    public Player(float x, float y, int width, int height) {
        super(x, y, width, height);
        loadAnimations();
        initHitbox(x , y,  HITBOX_WIDTH,HITBOX_HEIGHT);

    }

    public void update() {
        updatePhysics();
        updateAnimationTick();
        setAnimation();
    }

    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        int drawX = (int) (hitbox.x - xDrawOffset) - xLvlOffset;
        int drawY = (int) (hitbox.y - yDrawOffset) - yLvlOffset;
    
        // Utiliser la vélocité du PhysicsBody pour déterminer la direction
        if (physicsBody.getVelocity().x < 0) direction = -1;
        else if (physicsBody.getVelocity().x > 0) direction = 1;
    
        int drawWidth = direction * width;
        int correctedX = (direction == -1) ? drawX + width : drawX;
    
        g.drawImage(
            animManager.getFrame(playerAction, true),
            correctedX, drawY,
            drawWidth, height,
            null
        );
        drawHitbox(g, xLvlOffset, yLvlOffset);
    }
    

    private void updateAnimationTick() {
        if (inAir) {
            // Jump : cas spécial (ne passe pas par l'AnimationManager)
            int spriteCount = GetSpriteAmount(playerAction);
    
            int airIndex = mapAndClamp(
                    physicsBody.getVelocity().y,
                    JUMP_SPEED_MAX,       // vitesse max vers le haut
                    -JUMP_SPEED_MAX,      // vitesse max vers le bas
                    0,
                    spriteCount
            );
    
            animManager.setAniIndex(airIndex);
    
        } else {
            // Animations normales
            animManager.updateFrame(playerAction, true, attacking);
        }
    }




    private void setAnimation() {
        int startAni = playerAction;

        if (moving)
            playerAction = RUN.ordinal();
        else
            playerAction = IDLE.ordinal();

        if (inAir) {
            playerAction = JUMP.ordinal();
        }

        if (startAni != playerAction)
            animManager.reset();
    }
    

    /**
     * Met à jour la physique du joueur avec le système de vecteurs
     */
    private void updatePhysics() {
        // Vérifier si le joueur est dans une tile one-way
        if (IsInOneWayTile(hitbox, levelData) && down) {
            inAir = true;
        }
        
        moving = false;
        
        // Gérer le saut avec coyote time
        handleJumpInput();
        
        // 1. Nettoyer les forces INPUT de la frame précédente
        physicsBody.removeForcesOfType(ForceType.INPUT);
        
        // 2. Ajouter nouvelles forces
        applyGravity();
        applyMovementInput();
        
        // 3. Calculer accélération à partir des forces
        physicsBody.applyForces();
        
        // 4. Appliquer accélération à vélocité
        physicsBody.getVelocity().add(physicsBody.getAcceleration());
        
        // 5. Appliquer résistance (friction/air)
        if (!inAir) {
            physicsBody.applyResistance(GROUND_FRICTION, 1.0f);
        } else {
            // Résistance uniquement sur l'axe X pour ne pas affecter le saut
            physicsBody.getVelocity().x *= AIR_RESISTANCE;
        }
        
        // 6. Limiter vélocité
        physicsBody.limitVelocity(MAX_SPEED_X, MAX_FALL_SPEED);
        
        // Sauvegarder vélocité avant collision
        float velocityXBeforeCollision = physicsBody.getVelocity().x;
        float velocityYBeforeCollision = physicsBody.getVelocity().y;
        
        // 7. Gérer les collisions (modifie la vélocité)
        handleCollisions();
        
        // 8. Appliquer le mouvement à la hitbox
        hitbox.x += physicsBody.getVelocity().x;
        hitbox.y += physicsBody.getVelocity().y;
        
        // Si collision X détectée, ajuster position
        if (velocityXBeforeCollision != 0 && physicsBody.getVelocity().x == 0) {
            hitbox.x = GetEntityXPosNextToWall(hitbox, velocityXBeforeCollision);
        }
        
        // Si collision Y détectée, ajuster position
        if (velocityYBeforeCollision != 0 && physicsBody.getVelocity().y == 0) {
            hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, velocityYBeforeCollision);
        }
        
        // 9. Synchroniser PhysicsBody avec hitbox
        updatePhysicsFromHitbox();
        
        // 10. Nettoyer les forces
        physicsBody.getForces().removeIf(force -> !force.update());

        // Reset isJumping après la première frame de saut
        if (isJumping && physicsBody.getVelocity().y >= 0) {
            isJumping = false;
        }

        // Mettre à jour l'état inAir
        updateAirState();
        
        // Déterminer si le joueur bouge
        moving = Math.abs(physicsBody.getVelocity().x) > 0.1f || Math.abs(physicsBody.getVelocity().y) > 0.1f;
    }
    
    /**
     * Applique la force de gravité avec modificateurs (apex bonus, fast fall)
     */
    private void applyGravity() {
        if (inAir) {
            float gravityMultiplier = 0.2f * SCALE;

            // Fast fall - gravité augmentée si on appuie sur bas
            if (down) {
                gravityMultiplier *= FAST_FALL_MULT;
            }

            // Apex bonus - gravité réduite au sommet du saut
            if (Math.abs(physicsBody.getVelocity().y) < APEX_THRESHOLD) {
                gravityMultiplier *= APEX_GRAVITY_MULT;
            }

            // Ne pas appliquer la gravité si on vient de sauter (première frame)
            if (!isJumping || physicsBody.getVelocity().y >= 0) {
                physicsBody.addForce(new Vector2D(0, GRAVITY * gravityMultiplier), ForceType.GRAVITY);
            }
        }
    }
    
    /**
     * Applique les forces d'input du joueur avec apex control
     */
    private void applyMovementInput() {
        // Ne pas utiliser de force pour la décélération - la friction s'en charge
        if (left && !right) {
            float acceleration = ACCELERATION;
            // Apex control - meilleur contrôle au sommet du saut
            if (inAir && Math.abs(physicsBody.getVelocity().y) < APEX_THRESHOLD) {
                acceleration *= APEX_ACCEL_MULT;
            }
            physicsBody.addForce(new Vector2D(-acceleration, physicsBody.getVelocity().y), ForceType.INPUT);
        } else if (right && !left) {
            float acceleration = ACCELERATION;
            // Apex control - meilleur contrôle au sommet du saut
            if (inAir && Math.abs(physicsBody.getVelocity().y) < APEX_THRESHOLD) {
                acceleration *= APEX_ACCEL_MULT;
            }
            physicsBody.addForce(new Vector2D(acceleration, physicsBody.getVelocity().y), ForceType.INPUT);
        }
        // La décélération sera gérée par la friction
    }
    
    /**
     * Gère les collisions avec le système de vecteurs
     */
    private void handleCollisions() {
        Vector2D velocity = physicsBody.getVelocity();
        
        // Collision horizontale
        if (velocity.x != 0) {
            if (!CanMoveHere(hitbox.x + velocity.x, hitbox.y, hitbox.width, hitbox.height, levelData, hitbox, velocity, down)) {
                // Collision détectée - mettre vélocité à 0
                physicsBody.getVelocity().x = 0;
            }
        }
        
        // Collision verticale
        if (velocity.y != 0) {
            // Ne pas gérer les collisions verticales pendant le saut initial
            if (!isJumping || velocity.y > 0) {
                if (!CanMoveHere(hitbox.x, hitbox.y + velocity.y, hitbox.width, hitbox.height, levelData, hitbox, velocity, down)) {
                    // Collision détectée
                    float oldVelocityY = velocity.y;
                    physicsBody.getVelocity().y = 0;
                    
                    // Si on touche le sol en tombant
                    if (oldVelocityY > 0) {
                        resetInAir();
                    } else {
                        // Si on touche le plafond en sautant
                        physicsBody.getVelocity().y = FALL_SPEED_AFTER_COLLISION;
                    }
                }
            }
        }
    }
    
    /**
     * Met à jour l'état inAir
     */
    private void updateAirState() {
        if (!inAir) {
            if (!IsEntityOnFloor(hitbox, levelData, physicsBody.getVelocity())) {
                inAir = true;
            }
        }
    }

    private void jump() {
        // Permettre le saut si on a du coyote time OU si on est au sol
        if ((coyoteTimeCounter <= 0 && inAir) || isJumping)
            return;
        isJumping = true;
        inAir = true;
        // Appliquer directement la vélocité de saut (JUMP_FORCE est négatif pour aller vers le haut)
        physicsBody.getVelocity().y = JUMP_FORCE;
    }

    private void resetInAir() {
        inAir = false;
        isJumping = false;
        coyoteTimeCounter = COYOTE_TIME_FRAMES; // Reset coyote time
        // Arrêter le mouvement vertical
        physicsBody.stop(false, true);
    }
    
    /**
     * Gère l'input de saut avec coyote time et variable jump height
     */
    private void handleJumpInput() {
        // Mettre à jour l'état du bouton jump
        if (jump && jumpReleased) {
            // Bouton pressé pour la première fois
            jumpReleased = false;
            // Permettre le saut si on a du coyote time OU si on est au sol
            if ((coyoteTimeCounter > 0 || !inAir) && !isJumping) {
                jump();
            }
        } else if (!jump) {
            // Bouton relâché
            jumpReleased = true;
            // Variable jump height - réduire vélocité si on relâche
            if (isJumping && physicsBody.getVelocity().y < 0) {
                physicsBody.getVelocity().y *= JUMP_CUT_MULTIPLIER;
                isJumping = false;
            }
        }
        
        // Mettre à jour le coyote time
        updateCoyoteTime();
    }
    
    /**
     * Met à jour le coyote time
     */
    private void updateCoyoteTime() {
        if (!inAir) {
            // Sur le sol - reset coyote time
            coyoteTimeCounter = COYOTE_TIME_FRAMES;
        } else {
            // En l'air - décrémenter le timer
            if (coyoteTimeCounter > 0) {
                coyoteTimeCounter--;
            }
        }
    }

    private void loadAnimations() {

        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);

        BufferedImage [][] animations = new BufferedImage[13][10];
        for (int j = 0; j < animations.length; j++)
            for (int i = 0; i < animations[j].length; i++)
                animations[j][i] = img.getSubimage(i * 48, j * 48, 48, 48);
        
        animManager = new AnimationManager(animations);
    }

    public void loadLvlData(int[][] lvlData) {
        this.levelData = lvlData;
        if (!IsEntityOnFloor(hitbox, lvlData, physicsBody.getVelocity()))
            inAir = true;
    }

    public void resetDirBooleans() {
        left = false;
        right = false;
        up = false;
        down = false;
        jump = false;
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }

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
}