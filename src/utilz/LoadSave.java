package utilz;

// Java standard library imports
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

public class LoadSave {

    public static final String ASSETS = "/assets/";
    public static final String UI = ASSETS + "UI/";
    public static final String LEVEL_FOLDER = ASSETS + "Levels/";
    public static final String LEVEL_ONE_FOLDER = LEVEL_FOLDER + "LevelOne/";

    
    public static final String BUTTONS = UI + "Buttons/ButtonsAtlas.png";
    public static final String ICMONS = ASSETS + "ICMONS/" ;

    //public static final String PLAYER_ATLAS = ASSETS + "Hooded Character-Sheet.png";
    public static final String PLAYER_ATLAS = ASSETS + "PlayerSheet.png";
    public static final String PLAYER_ATTACK_ATLAS = ASSETS + "PlayerSheetAttacks.png";
    
    public static final String LEVEL_ONE_DATA = LEVEL_ONE_FOLDER + "LevelOne.png";
    public static final String LEVEL_ATLAS = ASSETS + "tileset/GrassTileSet.png";

    public static final String MENU_BACKGROUND = UI + "menu_background.jpg";
    public static final String WORLD_BACKGROUND = LEVEL_ONE_FOLDER + "BG Image.png";


    // Clouds

    public static final String BIG_CLOUDS = LEVEL_ONE_FOLDER + "Big Clouds.png";
    public static final String SMALL_CLOUD_1 = LEVEL_ONE_FOLDER +"Small Cloud 1.png";
    /**
     * Méthode pour récupérer un sprite d'un atlas
     * @param path chemin du sprite
     * @return BufferedImage le sprite
     */
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

    /**
     * 
     * Méthode pour récupérer les données du niveau depuis le fichier JSON
     * Charge spécifiquement la layer "out" du fichier LevelOne.json
     * @return int[][] les données du niveau
     */
    public static int[][] GetLevelData(){
        // Utilisation de la nouvelle fonction de chargement JSON
        String levelJsonPath = "res/assets/Levels/levelsData/Level_1.json";
        int[][] levelData = HelpMethods.loadLevelDataFromJson(levelJsonPath, "out");
        
        if (levelData == null) {
            System.err.println("Échec du chargement du niveau depuis le JSON, utilisation de la méthode de fallback");
            // Méthode de fallback vers l'ancien système si le JSON échoue
            return getLevelDataFromImage();
        }
        
        return levelData;
    }
    public static int[][] GetLevelData(String levelJsonPath){
        // Utilisation de la nouvelle fonction de chargement JSON
        int[][] levelData = HelpMethods.loadLevelDataFromJson(levelJsonPath, "out");
        
        if (levelData == null) {
            System.err.println("Échec du chargement du niveau depuis le JSON, utilisation de la méthode de fallback");
            // Méthode de fallback vers l'ancien système si le JSON échoue
            return getLevelDataFromImage();
        }
        
        return levelData;
    }

    /**
     * Récupère toutes les données de niveau depuis tous les fichiers JSON du dossier levelsData
     * Parcourt chaque fichier JSON dans le dossier et utilise GetLevelData pour charger les données
     * @return int[][][] tableau contenant toutes les données de niveau (un tableau 2D par niveau)
     */
    public static int[][][] GetAllLevelData(){
        // Chemin vers le dossier contenant les fichiers JSON des niveaux
        String levelsDataFolderPath = "res/assets/Levels/levelsData/";
        File levelsDataFolder = new File(levelsDataFolderPath);
        
        // Liste pour stocker les données de chaque niveau
        List<int[][]> allLevelsData = new ArrayList<>();
        
        // Vérifier que le dossier existe
        if (!levelsDataFolder.exists() || !levelsDataFolder.isDirectory()) {
            System.err.println("Le dossier levelsData n'existe pas : " + levelsDataFolderPath);
            return new int[0][][];
        }
        
        // Lister tous les fichiers dans le dossier
        File[] files = levelsDataFolder.listFiles();
        if (files == null) {
            System.err.println("Impossible de lister les fichiers du dossier : " + levelsDataFolderPath);
            return new int[0][][];
        }

        // Trie le tableau de fichiers par ordre alphabétique pour garantir l'ordre des niveaux
        Arrays.sort(files, (f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
        
        // Parcourir chaque fichier et charger les données si c'est un fichier JSON
        for (File file : files) {
            // Vérifier que c'est un fichier et qu'il a l'extension .json
            if (file.isFile() && file.getName().toLowerCase().endsWith(".json")) {
                // Construire le chemin complet du fichier
                String levelJsonPath = levelsDataFolderPath + file.getName();
                
                // Charger les données du niveau en utilisant GetLevelData
                int[][] levelData = GetLevelData(levelJsonPath);
                
                // Ajouter les données à la liste si le chargement a réussi
                if (levelData != null)
                    allLevelsData.add(levelData);
                else 
                    System.err.println("Échec du chargement du niveau : " + file.getName());
                
            }
        }
        
        // Convertir la liste en tableau 3D
        int[][][] result = new int[allLevelsData.size()][][];
        for (int i = 0; i < allLevelsData.size(); i++) {
            result[i] = allLevelsData.get(i);
        }
        
        return result;
    }
    
    /**
     * Méthode de fallback pour charger les données du niveau depuis l'image
     * @return int[][] les données du niveau
     */
    private static int[][] getLevelDataFromImage(){
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


    

    /**
     * Méthode pour appliquer un easing à une valeur
     * @param x Valeur à appliquer l'easing
     * 
     * Cette methode est utilisee pour faire des animations plus fluides
     * Elle applique un regresse ou un acceleration en fonction de la valeur de x 
     * 0.5 est la valeur jusqu'a laquelle on applique l'acceleration
     * Cette methode est mappee entre 0 et 1
     * @return Valeur avec l'easing appliqué
     */
    private static float EaseInOutCubic(float x) {
        if (x < 0.5f) {
            return 4f * x * x * x;
        }
        float f = -2f * x + 2f;
        return 1f - (f * f * f) / 2f;
    }
    
    /**
     * Méthode pour appliquer un easing à une valeur
     * @param value valeur actuelle du mouvement
     * @param min Valeur minimale
     * @param max Valeur maximale
     * @return Valeur avec l'easing appliqué
     */
    public static float EaseInOutCubicMove(float value, float min, float max) {
        float x = HelpMethods.clamp(value / max, 0f, 1f);
        float easedValue = EaseInOutCubic(x);
        return easedValue * (max - min) + min;
    }
    

}