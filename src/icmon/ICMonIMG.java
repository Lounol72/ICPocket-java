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
        InitialHpBar = new Rectangle(x,y, width, (int)(5 * SCALE));
        HpBar = new Rectangle(x,y, width, (int)(5 * SCALE));
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
}
