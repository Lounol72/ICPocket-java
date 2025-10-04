package entities;

import utilz.LoadSave;


import static entities.PlayerStateEnum.*;
import entities.AnimationManager;
import static utilz.Constants.PLAYER.*;
import static utilz.Constants.PLAYER.HITBOX.HITBOX_HEIGHT;
import static utilz.Constants.PLAYER.HITBOX.HITBOX_WIDTH;
import static utilz.Constants.SCALE;
import static utilz.HelpMethods.*;

import java.awt.*;
import java.awt.image.BufferedImage;



public class Player extends Entity {

    int playerAction = IDLE.ordinal();

    private AnimationManager animManager;
    // Map des états disponibles
    //private EnumMap<PlayerStateEnum, PlayerState> states;

    // Etat courant
    private PlayerState currentState;
    private PlayerStateEnum currentStateEnum;

    // Bouleens de controle
    private boolean moving = false, attacking = false;
    private boolean left, up, right, down, jump;
    private boolean inAir = false;
    // Speed of the player
    public static final float PLAYER_SPEED_RUN = SCALE;
    private int[][] levelData;

    private float xDrawOffset = 22 * SCALE, yDrawOffset = 20 * SCALE;

    // Speed of the player
    float xSpeed = 0;

    // Jumping & Falling
    private float airSpeed = 0f;
    // Direction of the player
    private int direction = 1;

    public Player(float x, float y, int width, int height) {
        super(x, y, width, height);
        loadAnimations();
        initHitbox(x , y,  HITBOX_WIDTH,HITBOX_HEIGHT);

    }

    public void update() {
        updatePos();
        updateAnimationTick();
        setAnimation();
    }

    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        int drawX = (int) (hitbox.x - xDrawOffset) - xLvlOffset;
        int drawY = (int) (hitbox.y - yDrawOffset) - yLvlOffset;
    
        if (xSpeed < 0) direction = -1;
        else if (xSpeed > 0) direction = 1;
    
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
            // Jump : cas spécial (ne passe pas par l’AnimationManager)
            int spriteCount = GetSpriteAmount(playerAction);
    
            int airIndex = mapAndClamp(
                    airSpeed,
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
    

    private void updatePos() {
        // Regarder si le joueur est dans une tile one way si oui inAir = true 
        if (IsInOneWayTile(hitbox, levelData) && down) {
            inAir = true;
        }
        moving = false;
        if(jump)
            jump();
        if (!inAir)
            if((!left && !right )|| (left && right) )
                return;

        xSpeed = 0;

        if (left)
            xSpeed -= PLAYER_SPEED_RUN;
        if (right)
            xSpeed += PLAYER_SPEED_RUN;
        

        if (!inAir) {
            // Utiliser la nouvelle méthode avec support one-way
            if ( !IsEntityOnFloor (hitbox, levelData, airSpeed) ) {
                inAir = true;
            }
        }
        if (inAir){
            // Utiliser la nouvelle méthode avec support one-way
            if (CanMoveHere (hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, levelData, hitbox, airSpeed, down)){
                hitbox.y += airSpeed;
                if (airSpeed + GRAVITY > MAX_AIR_SPEED)
                    airSpeed = MAX_AIR_SPEED;
                else
                    airSpeed += GRAVITY;
                
                updateXPos(xSpeed);
            }else{
                hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox,airSpeed);
                if(airSpeed > 0)
                    resetInAir();
                else
                    airSpeed = FALL_SPEED_AFTER_COLLISION;
                updateXPos(xSpeed);
            }
        }else{
            updateXPos(xSpeed);
        }
        moving = true;
    }

    private void jump() {
        if (inAir)
            return;
        inAir = true;
        airSpeed = JUMP_SPEED_MAX;

    }

    private void resetInAir() {
        inAir = false;
        airSpeed = 0;

    }

    private void updateXPos(float xSpeed) {
        if (CanMoveHere (hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, levelData, hitbox, airSpeed, down)) {
            hitbox.x += xSpeed;
        } else {
            hitbox.x = GetEntityXPosNextToWall(hitbox, xSpeed);
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
        if (!IsEntityOnFloor(hitbox, lvlData))
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