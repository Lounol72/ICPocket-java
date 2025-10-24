package ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import states.GameState;
import static utilz.Constants.UI.BUTTONS.DEFAULT_HEIGHT;
import static utilz.Constants.UI.BUTTONS.DEFAULT_WIDTH;
import utilz.LoadSave;

public class MenuButtons extends Button{
    private int rowIndex = 0;
    private String text, baseText;
    private GameState action;

    public MenuButtons(int x, int y, int width, int height, int rowIndex, String text, GameState action ) {
        super(x, y, width, height);
        loadImages();
        this.rowIndex = rowIndex;
        this.text = text;
        this.baseText = text;
        this.action = action;
    }
    
    private void loadImages() {
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.BUTTONS);
        for (int  i = 0;  i < img.length ;  i++)
            img[i] = temp.getSubimage( (rowIndex * DEFAULT_WIDTH), i  * DEFAULT_HEIGHT, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public void update() {
        index = 0;
        if(isMouseOver)
            index = 1;
        if(isMousePressed)
            index = 2;
    }

    public void draw(Graphics g) {
        g.drawImage(img[index], x, y, width, height, null);
        g.drawString(text, x + width / 2 - g.getFontMetrics().stringWidth(text) / 2, y + height / 2 + g.getFontMetrics().getHeight() / 2);
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
    public void action() {
        // prendre le label du bouton et faire une action en fonction de ce label
        GameState.setState(action);
    }

    public String getBaseText() {
        return baseText;
    }

}
