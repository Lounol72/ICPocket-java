package icmon;

import java.awt.*;
import java.awt.image.BufferedImage;

import static utilz.Constants.SCALE;
import static utilz.LoadSave.*;

public class ICMonIMG {

    private int x,y,width,height;
    private BufferedImage sprite; /**< image du ICmon. */
    private BufferedImage HpBarSprite;
    private Rectangle InitialHpBar;
    private Rectangle HpBar;
    private Rectangle InitialExpBar;
    private Rectangle Expbar;
    private String name;

    private final int HEALTHBAR_DEFAULT_HEIGHT = 64;
    private final int HEALTHBAR_DEFAULT_WIDTH = 192;
    private final int HEALTHBAR_HEIGHT = (int) (HEALTHBAR_DEFAULT_HEIGHT * SCALE);
    private final int HEALTHBAR_WIDTH = (int) ( HEALTHBAR_DEFAULT_WIDTH * SCALE);

    private final int EXPBAR_DEFAULT_HEIGHT = 64;
    private final int EXPBAR_DEFAULT_WIDTH = 192;
    private final int EXPBAR_HEIGHT = (int) (EXPBAR_DEFAULT_HEIGHT * SCALE);
    private final int EXPBAR_WIDTH = (int) ( EXPBAR_DEFAULT_WIDTH * SCALE);


    private ICMon icmon;

    public ICMonIMG(int x, int y, int width, int height, ICMon icmon){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.name = icmon.getName();
        this.icmon = icmon;  // Ajouter cette ligne
        this.sprite = GetICMonSprite(this.name + ".png");
        this.HpBarSprite = GetSpriteAtlas(UI +"HealthBars/healthbar_bottom_right.png");
        initBounds();
    }


    private void initBounds() {
        int HEIGHT_HP = (int) (10 * SCALE);
        int WIDTH_HP = (int) (104 * SCALE);
        int HEIGHT_EXP = (int) (5 * SCALE);
        int WIDTH_EXP = (int) (140 * SCALE);
        InitialHpBar = new Rectangle(x,y - HEIGHT_HP * 4, WIDTH_HP, (int)(HEIGHT_HP * SCALE));
        HpBar = new Rectangle(x,y - HEIGHT_HP * 4, WIDTH_HP, (int)(HEIGHT_HP * SCALE));
        InitialExpBar = new Rectangle(x,y - HEIGHT_EXP * 3, WIDTH_EXP, (int)(HEIGHT_EXP * SCALE));
        Expbar = new Rectangle(x,y - HEIGHT_EXP * 3, WIDTH_EXP, (int)(HEIGHT_EXP * SCALE));

    }

    public void draw( Graphics g){
        g.drawImage(sprite,x,y,width,height,null);
        g.setColor(Color.GRAY);
        g.fillRect(InitialHpBar.x,InitialHpBar.y,InitialHpBar.width,InitialHpBar.height);
        g.setColor(Color.GREEN);
        g.fillRect(HpBar.x,HpBar.y,HpBar.width,HpBar.height);
        g.drawImage(HpBarSprite,InitialHpBar.x - 20,InitialHpBar.y- 26,HEALTHBAR_WIDTH,HEALTHBAR_HEIGHT,null);
        g.setColor(Color.black);
        g.drawString(name,x+120,y-((HEALTHBAR_HEIGHT /4)*3));
        g.drawString("Lvl "+icmon.getLvl(),x+120,y-((HEALTHBAR_HEIGHT /4)*2));
        g.setColor(Color.gray);
        g.fillRect(InitialExpBar.x,InitialExpBar.y,InitialExpBar.width,InitialExpBar.height);
        g.setColor(Color.blue);
        g.fillRect(Expbar.x,Expbar.y,Expbar.width,Expbar.height);
    }

    public void update(){
        // Calcul du pourcentage de HP restant
        double hpPercentage = (double) this.icmon.getCurrent_pv() / this.icmon.getInitial_pv();
        // Mettre à jour la largeur de la barre de HP en fonction du pourcentage
        HpBar.width = (int)(InitialHpBar.width * hpPercentage);

        // Calcul du pourcentage d'EXP vers le prochain niveau
        long currentLevelExp = this.icmon.expCurve(this.icmon.getLvl());
        long nextLevelExp = this.icmon.expCurve(this.icmon.getLvl() + 1);
        double expPercentage = (double)(this.icmon.getExp() - currentLevelExp) / (nextLevelExp - currentLevelExp);
        // Mettre à jour la largeur de la barre d'EXP en fonction du pourcentage
        Expbar.width = (int)(InitialExpBar.width * expPercentage);

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
