package utilz;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static utilz.Constants.PATHS.ROOT;

public class LoadSave {

    public static final String ASSETS = "/assets/";
    public static final String UI = ASSETS + "UI/";


    public static final String BUTTONS = UI + "Buttons/ButtonsAtlas.png";
    public static final String ICMONS = ASSETS + "ICMONS/" ;

    public static BufferedImage GetSpriteAtlas(String path){
        BufferedImage img = null;
        InputStream is = LoadSave.class.getResourceAsStream(path);
        if (is == null) {
            System.err.println("Impossible de trouver le fichier : " + path);
            return null;
        }

        try {
            img = ImageIO.read(is);
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture de l'image : " + path);
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return img;
    }

    public static BufferedImage GetICMonSprite(String file){
        BufferedImage sprite = GetSpriteAtlas(ICMONS + file);
        if (sprite == null) {
            System.err.println("Impossible de charger l'image : " + ICMONS + file);
            sprite = GetSpriteAtlas(ICMONS + "722.png");
            if (sprite == null) {
                System.err.println("Image de secours introuvable : " + ICMONS + "772.png");
            }
        }
        return sprite;

    }
}
