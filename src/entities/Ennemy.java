package entities;

import java.awt.Graphics;

public abstract class Ennemy extends Entity {

    /* ================================
    * ÉTAT DE L'ENNEMY
    * ================================
    * 
    * - État initial : Idle
    * - État de déplacement : Moving
    * - État d'attaque : Attacking
    * - État de mort : Dead
    */
    private int ennemyState = EnnemyStateEnum.IDLE.ordinal();
    private int direction = 1;
    private boolean moving = false;
    private boolean attacking = false;
    private boolean dead = false;

    public Ennemy(float x, float y, int width, int height) {
        super(x, y, width, height);
    }

    public void update() {
    }

    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
    }

    public boolean isDead() {
        return dead;
    }
    public boolean isAttacking() {
        return attacking;
    }
    public boolean isMoving() {
        return moving;
    }
    public void setDead(boolean dead) {
        this.dead = dead;
    }
    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }
    public void setMoving(boolean moving) {
        this.moving = moving;
    }
    public void setEnnemyState(int ennemyState) {
        this.ennemyState = ennemyState;
    }


}
