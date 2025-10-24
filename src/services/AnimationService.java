package services;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import entities.AnimationManager;
import entities.PlayerStateEnum;
import static utilz.Constants.PLAYER.SPRITE.ATTACK_SPRITE_WIDTH;
import static utilz.Constants.PLAYER.SPRITE.ATTACK_SPRITE_WIDTH_DEFAULT;
import static utilz.Constants.PLAYER.SPRITE.ATTACK_X_DRAW_OFFSET;
import static utilz.Constants.PLAYER.SPRITE.ATTACK_Y_DRAW_OFFSET;
import static utilz.Constants.PLAYER.SPRITE.SPRITE_HEIGHT_DEFAULT;
import static utilz.Constants.PLAYER.SPRITE.SPRITE_WIDTH_DEFAULT;
import static utilz.Constants.PLAYER.SPRITE.X_DRAW_OFFSET;
import static utilz.Constants.PLAYER.SPRITE.Y_DRAW_OFFSET;
import utilz.LoadSave;

/**
 * Service responsable de la gestion des animations du joueur
 * 
 * RESPONSABILITÉS:
 * - Chargement et gestion des sprites
 * - Gestion des animations normales et d'attaque
 * - Calcul des offsets de rendu
 * - Synchronisation avec l'état physique
 * - Gestion du flip horizontal
 * 
 * @author Lounol72
 * @version 2.0 - Service extrait du Player
 */
public class AnimationService {
    
    private AnimationManager animManager;
    
    // === GESTION DES SPRITES D'ATTAQUE ===
    private boolean isUsingAttackSprites = false;
    private boolean isAttacking = false;
    
    // === ÉTAT DU JOUEUR ===
    private int playerAction = PlayerStateEnum.IDLE.ordinal();
    private int direction = 1;
    private boolean inAir = false;
    
    public AnimationService() {
        loadAnimations();
    }
    
    /**
     * Met à jour le tick d'animation avec support des animations d'attaque
     */
    public void updateAnimationTick(boolean inAir, boolean isAttacking) {
        this.inAir = inAir;
        this.isAttacking = isAttacking;
        
        if (inAir) {
            handleAirAnimation();
        } else {
            // Utiliser l'index approprié selon l'état
            int animationIndex = isUsingAttackSprites ? 13 : playerAction; // Ligne 13 pour l'attaque
            
            animManager.updateFrame(animationIndex, true, isAttacking);
        }
    }
    
    /**
     * Gère l'animation en l'air
     */
    private void handleAirAnimation() {
        // Utiliser un index fixe pour l'animation en l'air
        // À améliorer avec l'accès à la vélocité du joueur
        int airIndex = 0;
        
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
    public void setAnimation(boolean attack, boolean inAir, boolean moving) {
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
            if (inAir) {
                playerAction = PlayerStateEnum.JUMP.ordinal();
            } else if (moving) {
                playerAction = PlayerStateEnum.RUN.ordinal();
            } else {
                playerAction = PlayerStateEnum.IDLE.ordinal();
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
    public void updateDirection(float velocityX) {
        if (velocityX < 0) {
            direction = -1;
        } else if (velocityX > 0) {
            direction = 1;
        }
    }
    
    /**
     * Commence une nouvelle attaque
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
     */
    private void finishAttack() {
        isAttacking = false;
        isUsingAttackSprites = false;
        
        // Debug: Logger la fin de l'attaque
        System.out.println("Attack animation finished - returning to normal animations");
    }
    
    /**
     * Vérifie si l'animation d'attaque est terminée
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
     * Rend le joueur à l'écran avec gestion de la direction et des animations
     */
    public void render(Graphics g, float hitboxX, float hitboxY, int xLvlOffset, int yLvlOffset) {
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
        int drawX = (int) (hitboxX - currentXOffset) - xLvlOffset;
        int drawY = (int) (hitboxY - currentYOffset) - yLvlOffset;
    
        // Gestion du flip horizontal pour les sprites
        int drawWidth = (int) (isUsingAttackSprites ? ATTACK_SPRITE_WIDTH * 1.6 * direction : 48 * direction);
        
        int correctedX = (direction == -1) ? drawX + (isUsingAttackSprites ? ATTACK_SPRITE_WIDTH : 48) : drawX;
    
        // Rendu du sprite avec animation
        // Utiliser l'index approprié selon l'état
        int animationIndex = isUsingAttackSprites ? 13 : playerAction; // Ligne 13 pour l'attaque
        
        g.drawImage(
            animManager.getFrame(animationIndex, true),
            correctedX, drawY,
            drawWidth, 48,
            null
        );
    }
    
    /**
     * Charge les animations du joueur avec un seul gestionnaire
     * 
     * SYSTÈME UNIFIÉ:
     * - Lignes 0-12: Animations normales (48x48)
     * - Ligne 13: Animation d'attaque (80x48)
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
                    SPRITE_HEIGHT_DEFAULT
                );
            }
        }
        
        // === CHARGEMENT DE L'ANIMATION D'ATTAQUE (ligne 13) ===
        for (int i = 0; i < allAnimations[13].length; i++) {
            allAnimations[13][i] = imgAttack.getSubimage(
                i * ATTACK_SPRITE_WIDTH_DEFAULT, 
                0, 
                ATTACK_SPRITE_WIDTH_DEFAULT, 
                SPRITE_HEIGHT_DEFAULT
            );
        }
        
        // === CRÉATION DU GESTIONNAIRE UNIQUE ===
        animManager = new AnimationManager(allAnimations);
        
        // Debug: Logger le chargement
        System.out.println("Animations loaded: Unified system with 14 animation sets");
    }
    
    // === GETTERS/SETTERS ===
    
    public boolean isAttacking() {
        return isAttacking;
    }
    
    public boolean canAttack() {
        return !isAttacking;
    }
    
    public int getPlayerAction() {
        return playerAction;
    }
    
    public void setPlayerAction(int playerAction) {
        this.playerAction = playerAction;
    }
    
    public int getDirection() {
        return direction;
    }
    
    public void setDirection(int direction) {
        this.direction = direction;
    }
    
    public boolean isUsingAttackSprites() {
        return isUsingAttackSprites;
    }
}
