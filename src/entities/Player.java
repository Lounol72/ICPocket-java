package entities;

import utilz.LoadSave;


import static entities.PlayerState.*;

import static utilz.Constants.PLAYER.*;
import static utilz.Constants.SCALE;
import static utilz.HelpMethods.*;

import java.awt.*;
import java.awt.image.BufferedImage;



public class Player extends Entity {
    private BufferedImage[][] animations;
    private int aniTick, aniIndex, aniSpeed = 25;
    private int playerAction = IDLE.ordinal();
    private boolean moving = false, attacking = false;
    private boolean left, up, right, down, jump;
    private float playerSpeed = SCALE;
    private int[][] levelData;
    private float xDrawOffset = 22 * SCALE, yDrawOffset = 20 * SCALE;
    float xSpeed = 0;

    // Jumping & Falling
    private float airSpeed = 0f;
    private final float gravity = 0.05f * SCALE;
    private final float jumpSpeed = -5f * SCALE;
    private float fallSpeedAfterCollision = 0.5f * SCALE;
    private boolean inAir = false;
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
        // Position de base du sprite (sans retournement)
        int drawX = (int) (hitbox.x - xDrawOffset) - xLvlOffset;
        int drawY = (int) (hitbox.y - yDrawOffset) - yLvlOffset;

        // Détermination de la direction
        if (xSpeed < 0) {
            direction = -1; // gauche
        } else if (xSpeed > 0) {
            direction = 1; // droite
        }

        // Largeur dessinée (positive = normal, négative = miroir)
        int drawWidth = direction * width;

        // Correction de l’offset X si on dessine en miroir
        int correctedX = (direction == -1) ? drawX + width : drawX;

        // Dessin du sprite
        g.drawImage(
                animations[playerAction][aniIndex],
                correctedX, drawY,
                drawWidth, height,
                null
        );
        drawHitbox(g, xLvlOffset, yLvlOffset);
    }

    private void updateAnimationTick() {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;

            if (inAir) {
                int spriteCount = GetSpriteAmount(playerAction);

                // On mappe la vitesse verticale (airSpeed) sur un index de sprite
                int airIndex = mapAndClamp(
                        airSpeed,        // vitesse actuelle
                        jumpSpeed,       // vitesse max vers le haut
                        -jumpSpeed,      // vitesse max vers le bas
                        0,               // premier sprite
                        spriteCount      // dernier sprite
                );

                aniIndex = airIndex;

            } else {
                // Animation normale au sol
                aniIndex++;
                if (aniIndex >= GetSpriteAmount(playerAction)) {
                    aniIndex = 0;
                    attacking = false;
                }
            }
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
            resetAniTick();
    }

    private void resetAniTick() {
        aniTick = 0;
        aniIndex = 0;
    }

    private void updatePos() {
        moving = false;
        if(jump)
            jump();
        if (!inAir)
            if((!left && !right )|| (left && right))
                return;

        xSpeed = 0;

        if (left)
            xSpeed -= playerSpeed;
        if (right)
            xSpeed += playerSpeed;
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
                if (airSpeed + gravity > MAX_AIR_SPEED)
                    airSpeed = MAX_AIR_SPEED;
                else
                    airSpeed += gravity;
                
                updateXPos(xSpeed);
            }else{
                hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox,airSpeed);
                if(airSpeed > 0)
                    resetInAir();
                else
                    airSpeed = fallSpeedAfterCollision;
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
        airSpeed = jumpSpeed;

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

        animations = new BufferedImage[13][10];
        for (int j = 0; j < animations.length; j++)
            for (int i = 0; i < animations[j].length; i++)
                animations[j][i] = img.getSubimage(i * 48, j * 48, 48, 48);

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