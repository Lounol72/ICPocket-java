package ui;

import states.GameState;
import utilz.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static utilz.Constants.SetLanguage;
import static utilz.Constants.UI.BUTTONS.*;
import static utilz.LoadSave.GetSpriteAtlas;

public class MenuButtons extends Button{

    private int rowIndex  = 0;
    private String language;
    private String text;
    private int xText, yText;
    private boolean textPositionCalculated = false;
    public MenuButtons( int x, int y, int width, int height, int rowIndex, String language, String text ) {
        super(x, y, width, height);
        this.rowIndex = rowIndex;
        this.language = language;
        this.text = text;
        this.xText = 0;
        loadImages();
    }

    private void loadImages() {
        BufferedImage temp = GetSpriteAtlas(LoadSave.BUTTONS);
        for (int  i = 0;  i < img.length ;  i++)
            img[i] = temp.getSubimage( (rowIndex * DEFAULT_WIDTH), i  * DEFAULT_HEIGHT, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public void update(){
        index = 0;
        if(isMouseOver)
            index = 1;
        if(isMousePressed)
            index = 2;
    }

    public void draw( Graphics g){
        g.drawImage(img[index],x,y,width,height, null);
        // Calculer la position du texte seulement la première fois
        if (!textPositionCalculated) {
            FontMetrics fm = g.getFontMetrics();
            xText = x + (width - fm.stringWidth(text)) / 2;
            yText = y + ((height - fm.getHeight()) / 2) + fm.getAscent();
            textPositionCalculated = true;
        }
        // Utiliser les valeurs stockées
        g.drawString(text, xText, yText);
    }

    public void ApplyLanguage(){
        SetLanguage(this.language);
    }

    public void action() {
        ApplyLanguage();
        GameState.setState(GameState.BATTLE);
    }
}
