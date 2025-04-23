package ui;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Button {

    protected int x,y,width,height;
    protected Rectangle bounds;
    protected int index;
    protected BufferedImage[] img;
    protected boolean isMouseOver, isMousePressed;

    public Button(int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        img = new BufferedImage[3];
        createBounds();
    }

    private void createBounds() {
        bounds = new Rectangle(x,y,width,height);
    }

    public void resetBools(){
        isMouseOver = false;
        isMousePressed = false;
    }

    // Getters and Setters
    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds( Rectangle bounds ) {
        this.bounds = bounds;
    }

    public int getY() {
        return y;
    }

    public void setY( int y ) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX( int x ) {
        this.x = x;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth( int width ) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight( int height ) {
        this.height = height;
    }

    public boolean isMousePressed() {return isMousePressed;}

    public void setMousePressed( boolean mousePressed ) {isMousePressed = mousePressed;}

    public boolean isMouseOver() {return isMouseOver;}

    public void setMouseOver( boolean mouseOver ) {isMouseOver = mouseOver;}
}
