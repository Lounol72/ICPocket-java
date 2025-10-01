package utilz;

import icmon.ICMon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import com.google.gson.*;
import duel.Team;
import icmon.Move;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class LoadSave {

    public static final String ASSETS = "/assets/";
    public static final String UI = ASSETS + "UI/";


    public static final String BUTTONS = UI + "Buttons/ButtonsAtlas.png";
    public static final String ICMONS = ASSETS + "ICMONS/" ;

    //public static final String PLAYER_ATLAS = ASSETS + "Hooded Character-Sheet.png";
    public static final String PLAYER_ATLAS = ASSETS + "PlayerSheet.png";
    public static final String LEVEL_ONE_DATA = ASSETS + "Levels/LevelOne.png";
    public static final String LEVEL_ATLAS = ASSETS + "tileset/GrassTileSet.png";

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

    private static final String SAVE_DIRECTORY = "saves/";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // Méthode pour sauvegarder une équipe
    public static void saveTeamData(Team team, String fileName) {
        try {
            // Créer le répertoire de sauvegarde s'il n'existe pas
            File saveDir = new File(SAVE_DIRECTORY);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }

            SaveData saveData = new SaveData();
            List<ICMonSaveData> icmonSaves = new ArrayList<>();

            // Convertir chaque ICMon en ICMonSaveData
            for (ICMon icmon : team.getTeam()) {
                if (icmon != null) {
                    ICMonSaveData icmonSave = convertICMonToSaveData(icmon);
                    icmonSaves.add(icmonSave);
                }
            }

            saveData.setIcmons(icmonSaves);

            // Écrire dans le fichier JSON
            String json = gson.toJson(saveData);
            Files.write(Paths.get(SAVE_DIRECTORY + fileName), json.getBytes());

        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }

    // Méthode pour convertir un ICMon en données de sauvegarde
    private static ICMonSaveData convertICMonToSaveData( ICMon icmon) {
        ICMonSaveData saveData = new ICMonSaveData();

        saveData.setId(icmon.getId());
        saveData.setLvl(icmon.getLvl());
        saveData.setNature(icmon.getNature());
        saveData.setNb_move(icmon.getNb_move());

        // Sauvegarder les IDs des moves
        List<Integer> moveIds = new ArrayList<>();
        for (Move move : icmon.getMoveList()) {
            if (move != null && move.getPower() != -1) {
                moveIds.add(move.getId());
            }
        }
        saveData.setMoveIds(moveIds);

        // Sauvegarder les IVs
        saveData.setIv(icmon.getIv());

        // Sauvegarder les types
        String[] types = new String[2];
        types[0] = icmon.getType()[0].name();
        types[1] = icmon.getType()[1].name();
        saveData.setTypes(types);

        return saveData;
    }

    // Méthode pour charger les données sauvegardées
    public static SaveData loadTeamData(String fileName) {
        try {
            String json = Files.readString(Paths.get(SAVE_DIRECTORY + fileName));
            return gson.fromJson(json, SaveData.class);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement : " + e.getMessage());
            return null;
        }
    }
    
    public static int[][] GetLevelData(){
        BufferedImage img = GetSpriteAtlas(LEVEL_ONE_DATA);
        int [][] levelData = new int[img.getHeight()][img.getWidth()];

        for(int j = 0; j<img.getHeight(); j++)
            for(int i = 0; i<img.getWidth(); i++){
                Color tileColor = new Color(img.getRGB(i,j));
                int value = tileColor.getRed();
                if (value >= 48)
                    value = 0;
                levelData[j][i] = value;
            }


        return levelData;
    }

}
