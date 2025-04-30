package entities;

import utilz.LoadSave;


import static entities.PlayerState.*;

import static utilz.Constants.SCALE;
import static utilz.HelpMethods.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;


public class Player extends Entity {
	private BufferedImage[][] animations;
	private int aniTick, aniIndex, aniSpeed = 25;
	private int playerAction = IDLE.ordinal();
	private boolean moving = false, attacking = false;
	private boolean left, up, right, down, jump;
	private float playerSpeed = SCALE;
	private int[][] levelData;
	private float xDrawOffset = 21 * SCALE, yDrawOffset = 4 * SCALE;

	// Jumping & Falling
	private float airSpeed = 0f;
	private final float gravity = 0.05f * SCALE;
	private final float jumpSpeed = -2.5f * SCALE;
	private float fallSpeedAfterCollision = 0.5f * SCALE;
	private boolean inAir = false;


	public Player(float x, float y, int width, int height) {
		super(x, y, width, height);
		loadAnimations();
		initHitbox(x, y, (int)(20 * SCALE), (int)(27 * SCALE));

	}

	public void update() {
		updatePos();
		updateAnimationTick();
		setAnimation();
	}

	public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
		g.drawImage(animations[playerAction][aniIndex], (int) (hitbox.x - xDrawOffset) - xLvlOffset, (int) (hitbox.y - yDrawOffset) - yLvlOffset, width, height, null);
//		drawHitbox(g);
	}

	private void updateAnimationTick() {
		aniTick++;
		if (aniTick >= aniSpeed) {
			aniTick = 0;
			aniIndex++;
			if (aniIndex >= GetSpriteAmount(playerAction)) {
				aniIndex = 0;
				attacking = false;
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
			if (airSpeed < 0)
				playerAction = JUMP.ordinal();
			else
				playerAction = FALL.ordinal();
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

		if (jump)
			jump();
		if (!inAir)
			if ((!left && !right) || (left && right))
				return;

		float xSpeed = 0;

		if (left)
			xSpeed -= playerSpeed;
		if (right)
			xSpeed += playerSpeed;

		if (!inAir)
			if (!IsEntityOnFloor(hitbox, levelData))
				inAir = true;

		if (inAir) {
			if (CanMoveHere(new Rectangle2D.Float(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height), levelData)) {
				hitbox.y += airSpeed;
				airSpeed += gravity;
				updateXPos(xSpeed);
			} else {
				hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
				if (airSpeed > 0)
					resetInAir();
				else
					airSpeed = fallSpeedAfterCollision;
				updateXPos(xSpeed);
			}

		} else
			updateXPos(xSpeed);
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
		if (CanMoveHere(new Rectangle2D.Float(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height), levelData)) {
			hitbox.x += xSpeed;
		} else {
			hitbox.x = GetEntityXPosNextToWall(hitbox, xSpeed);
		}

	}

	private void loadAnimations() {

		BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);

		animations = new BufferedImage[4][8];
		for (int j = 0; j < animations.length; j++)
			for (int i = 0; i < animations[j].length; i++)
				animations[j][i] = img.getSubimage(i * 32, j * 32, 32, 32);

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
