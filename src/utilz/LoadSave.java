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
        try {
            img = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return img;
    }

    public static BufferedImage GetICMonSprite(String file){
        return GetSpriteAtlas(ICMONS + file);
    }
}
