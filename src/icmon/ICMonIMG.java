package icmon;

import java.awt.*;
import java.awt.image.BufferedImage;

import static utilz.Constants.SCALE;
import static utilz.LoadSave.GetICMonSprite;

public class ICMonIMG {

    private int x,y,width,height;
    private BufferedImage sprite; /**< image du ICmon. */
    private Rectangle InitialHpBar;
    private Rectangle HpBar;

    public ICMonIMG(int x,int y, int width, int height, String name){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.sprite = GetICMonSprite(name + ".png");
        initBounds();
    }

    private void initBounds() {
        int HEIGHT = 7;
        InitialHpBar = new Rectangle(x,y - HEIGHT, width, (int)(HEIGHT * SCALE));
        HpBar = new Rectangle(x,y - HEIGHT, width, (int)(HEIGHT * SCALE));
    }

    public void draw( Graphics g){
        g.drawImage(sprite,x,y,width,height,null);
        g.setColor(Color.RED);
        g.fillRect(InitialHpBar.x,InitialHpBar.y,InitialHpBar.width,InitialHpBar.height);
        g.setColor(Color.GREEN);
        g.fillRect(HpBar.x,HpBar.y,HpBar.width,HpBar.height);
    }

    public void update(){
        HpBar.width= (HpBar.width > 0)? HpBar.width -1 :InitialHpBar.width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight( int height ) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth( int width ) {
        this.width = width;
    }

    public int getX() {
        return x;
    }

    public void setX( int x ) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY( int y ) {
        this.y = y;
    }

    public Rectangle getInitialHpBar() {
        return InitialHpBar;
    }

    public void setInitialHpBar( Rectangle initialHpBar ) {
        InitialHpBar = initialHpBar;
    }

    public Rectangle getHpBar() {
        return HpBar;
    }

    public void setHpBar( Rectangle hpBar ) {
        HpBar = hpBar;
    }

    public BufferedImage getSprite() {
        return sprite;
    }

    public void setSprite( BufferedImage sprite ) {
        this.sprite = sprite;
    }
}
