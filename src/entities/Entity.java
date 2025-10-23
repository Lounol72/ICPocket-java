package entities;

// Java standard library imports
import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import physics.PhysicsBody;
import static utilz.Constants.PLAYER.MASS;

public abstract class Entity {

    protected float x, y;
    protected int width, height;
    protected Rectangle2D.Float hitbox;
    protected PhysicsBody physicsBody;

    public Entity(float x, float y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.physicsBody = new PhysicsBody(x, y, MASS);

    }

    protected void drawHitbox(Graphics g, int xLvlOffset, int yLvlOffset) {
        // For debugging the hitbox
        g.setColor(Color.BLUE);
        g.drawRect((int) hitbox.x - xLvlOffset, (int) hitbox.y - yLvlOffset, (int) hitbox.width, (int) hitbox.height);

    }

    protected void initHitbox(float x, float y, float width, float height) {
        hitbox = new Rectangle2D.Float(x, y, width, height);
        // Synchroniser la position du PhysicsBody avec la hitbox
        physicsBody.setPosition(x, y);
    }

    /**
     * Met à jour la hitbox en fonction de la position du PhysicsBody
     */
    protected void updateHitboxFromPhysics() {
        hitbox.x = physicsBody.getPosition().x;
        hitbox.y = physicsBody.getPosition().y;
        // Synchroniser aussi les variables x, y pour compatibilité
        this.x = physicsBody.getPosition().x;
        this.y = physicsBody.getPosition().y;
    }

    /**
     * Met à jour la position du PhysicsBody en fonction de la hitbox
     */
    protected void updatePhysicsFromHitbox() {
        physicsBody.setPosition(hitbox.x, hitbox.y);
        this.x = hitbox.x;
        this.y = hitbox.y;
    }

    public Rectangle2D.Float getHitbox() {
        return hitbox;
    }
    
    public PhysicsBody getPhysicsBody() {
        return physicsBody;
    }

}
