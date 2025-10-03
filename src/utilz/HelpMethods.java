package utilz;

import com.google.gson.*;
import duel.Team;
import game.Game;
import icmon.ICMon;
import icmon.Move;

import java.awt.geom.Rectangle2D;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.*;

import static utilz.Constants.PATHS.DATA_FILE;
import static utilz.Constants.SCALE;
import static utilz.Constants.WORLD.*;
import static utilz.Constants.language;
/**
 * Cette classe contient des méthodes utilitaires pour le jeu
 * @implNote Cette classe est utilisée pour gérer les données du jeu
 */
public class HelpMethods {

    
    private static final Random rnd = new Random();

    // Cache des données pour éviter des lectures répétées
    private static Map<String, JsonObject> jsonCache = new HashMap<>();
    private static Gson gson = new Gson();

    private static ResourceBundle bundle;

    /**
     * Charge un fichier JSON en cache s'il n'y est pas déjà
     * @param filePath Chemin vers le fichier JSON
     * @return JsonObject Données du fichier JSON
     * @throws IOException Erreur lors de la lecture du fichier JSON
     */
    static JsonObject getJsonData( String filePath ) throws IOException {
        if (!jsonCache.containsKey(filePath)) {
            try (Reader reader = new FileReader(filePath)) {
                JsonObject data = JsonParser.parseReader(reader).getAsJsonObject();
                jsonCache.put(filePath, data);
            }
        }
        return jsonCache.get(filePath);
    }
    /**
     * Génère une team à partir de son id
     * @param id Id de la team
     * @return Team
     */
    public static Team generateTeamFromId(int id){
        try {
            JsonObject jsonData = getJsonData(DATA_FILE);

            JsonObject teamData = jsonData.getAsJsonArray("teams")
                    .get(id - 1)
                    .getAsJsonObject();

            int nbPokes = teamData.get("nb_Poke").getAsInt();
            String name = teamData.get("name").getAsString();


            ICMon[] icmons = new ICMon[nbPokes];
            for(int i = 0; i < nbPokes; i++) {
                int monId = teamData.getAsJsonArray("ids").get(i).getAsInt();
                icmons[i] = new ICMon(monId);
                if (icmons[i] == null) {
                    throw new IllegalStateException("Échec de la création de l'ICMon #" + monId);
                }
            }

            return new Team(id, icmons, nbPokes, name);
        } catch (Exception e) {
            System.err.println("Erreur détaillée lors de la génération de la team #" + id + ": ");
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Génère un ICMon à partir de son id
     * @param id Id du ICMon
     * @return ICMon
     */
    public static ICMon generateICMonFromId(int id) {
        try {
            JsonObject jsonData = getJsonData(DATA_FILE);
            JsonObject icmonData = jsonData.getAsJsonArray("icmons")
                    .get(id - 1)
                    .getAsJsonObject();

            // Configuration de l'ICMon
            String name = icmonData.get("name").getAsString();

            t_Type[] types = new t_Type[2];
            String type1 = icmonData.getAsJsonArray("types").get(0).getAsString();
            String type2 = icmonData.getAsJsonArray("types").get(1).getAsString();
            types[0] = t_Type.valueOf(type1);
            types[1] = type2.equals("NONE") ? t_Type.noType : t_Type.valueOf(type2);

            int[] baseStats = extractStats(icmonData.getAsJsonObject("stats"));

            ICMon icmon = new ICMon(id, name, types, baseStats);

            // Ajout des moves initiaux si spécifiés
            JsonArray movesArray = icmonData.getAsJsonArray("initialMoves");
            if (movesArray != null) {
                for (int i = 0; i < movesArray.size(); i++) {
                    icmon.setNewMove(generateMoveFromId(movesArray.get(i).getAsInt()));
                }
            }

            return icmon;
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération de l'ICMon #" + id + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Retourne les stats d'un ICMon
     * @param stats Stats de l'ICMon
     * @return Stats d'un ICMon
     */
    private static int[] extractStats(JsonObject stats) {
        int[] baseStats = new int[6];
        baseStats[0] = stats.get("hp").getAsInt();
        baseStats[1] = stats.get("attack").getAsInt();
        baseStats[2] = stats.get("defense").getAsInt();
        baseStats[3] = stats.get("spAttack").getAsInt();
        baseStats[4] = stats.get("spDefense").getAsInt();
        baseStats[5] = stats.get("speed").getAsInt();
        return baseStats;
    }
    /**
     * Retourne le buffer de moves pour un ICMon
     * @param pokeId Id du ICMon
     * @param pokeLevel Niveau du ICMon
     * @return Buffer de moves
     */
    public static Move[] getLearningBuffer(int pokeId, int pokeLevel) {
        try {
            JsonObject jsonData = getJsonData(DATA_FILE);
            JsonObject poolData = jsonData.getAsJsonArray("movepool")
                    .get(pokeId - 1)
                    .getAsJsonObject();

            Move[] buffer = new Move[20];
            int bufferIndex = 0;

            // Récupérer le tableau de moves
            JsonArray moves = poolData.getAsJsonArray("moves");

            // Parcourir tous les moves disponibles
            for (JsonElement moveElement : moves) {
                JsonObject moveData = moveElement.getAsJsonObject();
                int moveLvl = moveData.get("lvl").getAsInt();

                // Si le niveau correspond exactement, ajouter le move au buffer
                if (moveLvl == pokeLevel) {
                    int idMove = moveData.get("idMove").getAsInt();
                    buffer[bufferIndex] = generateMoveFromId(idMove);
                    bufferIndex++;

                    // Éviter le dépassement du buffer
                    if (bufferIndex >= buffer.length) {
                        break;
                    }
                }
            }

            return buffer;
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du buffer de moves pour l'ICMon #" + pokeId + ": " + e.getMessage());
            return null;
        }
    }


    /**
     * Génère un Move à partir de son id
     * @param id Id du Move
     * @return Move
     */
    public static Move generateMoveFromId(int id) {
        try {
            JsonObject jsonData = getJsonData(DATA_FILE);
            JsonObject moveData = jsonData.getAsJsonArray("moves")
                    .get(id - 1)
                    .getAsJsonObject();

            return new Move(
                id,
                moveData.get("name").getAsString(),
                moveData.get("power").getAsInt(),
                t_Type.valueOf(moveData.get("type").getAsString()),
                t_Categ.valueOf(moveData.get("categ").getAsString()),
                moveData.get("accuracy").getAsInt(),
                moveData.get("PP").getAsInt(),
                moveData.get("PP").getAsInt(), // current_pp = max_pp au début
                moveData.get("priority").getAsInt(),
                moveData.get("target").getAsInt(),
                moveData.get("ind_secEffect").getAsInt(),
                moveData.get("probability").getAsInt(),
                moveData.get("value_effect").getAsInt(),
                moveData.get("effect_modifier").getAsInt()
            );
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du Move #" + id + ": " + e.getMessage());
            return null;
        }
    }
    /**
     * Retourne une phrase à partir de son nom
     * @param name Nom de la phrase
     * @return Phrase
     */
    public static String GetPhrase(String name){
        try {
            // Utilisation du chemin complet correspondant à la structure de vos fichiers
            bundle = ResourceBundle.getBundle("data.langue", new Locale(language), new ResourceControl());
            return bundle.getString(name);


        } catch (MissingResourceException e) {
            System.err.printf("Erreur lors de la récupération de la phrase : {%s} - {%s}%n", name, e.getMessage());
            // Tentative de fallback vers l'anglais si la langue demandée n'existe pas
            try {
                bundle = ResourceBundle.getBundle("res.data.langue", new Locale("en"));
                return bundle.getString(name);
            } catch (MissingResourceException e2) {
                return "Error: " + name;
            }finally{
                System.exit(3);
            }
        }
    }
    // Classe pour gérer le chargement des ressources depuis le dossier res
    private static class ResourceControl extends ResourceBundle.Control {
        public URL getResource( String baseName, String locale) {
            String bundleName = toBundleName(baseName, Locale.of(locale));
            String resourceName = toResourceName(bundleName, "properties");
            return ClassLoader.getSystemResource("res/" + resourceName);
        }
    }


    /**
     * Charge les données de niveau depuis un fichier JSON Tiled
     * @param filePath Chemin vers le fichier JSON du niveau
     * @param layerName Nom de la layer à charger (ex: "out")
     * @return int[][] Données du niveau sous forme de tableau 2D
     */
    public static int[][] loadLevelDataFromJson(String filePath, String layerName) {
        try {
            JsonObject jsonData = getJsonData(filePath);
            JsonArray layers = jsonData.getAsJsonArray("layers");
            
            // Rechercher la layer avec le nom spécifié
            JsonObject targetLayer = null;
            for (JsonElement layerElement : layers) {
                JsonObject layer = layerElement.getAsJsonObject();
                if (layerName.equals(layer.get("name").getAsString())) {
                    targetLayer = layer;
                    break;
                }
            }
            
            if (targetLayer == null) {
                System.err.println("Layer '" + layerName + "' non trouvée dans le fichier JSON");
                return null;
            }
            
            // Récupérer les dimensions et les données
            int width = targetLayer.get("width").getAsInt();
            int height = targetLayer.get("height").getAsInt();
            JsonArray dataArray = targetLayer.getAsJsonArray("data");
            
            // Convertir les données JSON en tableau 2D
            int[][] levelData = new int[height][width];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int index = y * width + x;
                    if(dataArray.get(index).getAsInt() == 0)
                        levelData[y][x] = 21;
                    else
                        levelData[y][x] = dataArray.get(index).getAsInt() - 1;
                }
            }
            
            return levelData;
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du niveau depuis le JSON: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Réinitialiser le cache (utile pour les tests ou si les fichiers sont modifiés)
     */
    public static void clearCache() {
        jsonCache.clear();
    }

    /**
     * Méthode générique pour charger un élément à partir d'un JSON
     * @param filePath Chemin du fichier JSON
     * @param arrayName Nom du tableau dans le JSON
     * @param id ID de l'élément
     * @param classOfT Classe à instancier
     */
    public static <T> T loadResource(String filePath, String arrayName, int id, Class<T> classOfT) {
        try {
            JsonObject jsonData = getJsonData(filePath);
            JsonObject elementData = jsonData.getAsJsonArray(arrayName)
                    .get(id - 1)
                    .getAsJsonObject();

            return gson.fromJson(elementData, classOfT);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de la ressource: " + e.getMessage());
            return null;
        }
    }

    /**
     * Permet d'enregistrer des adaptateurs de type personnalisés pour le parsing JSON
     */
    public static void registerTypeAdapter(Object typeAdapter, Type typeOfT) {
        gson = new GsonBuilder()
            .registerTypeAdapter(typeOfT, typeAdapter)
            .create();
    }
    /**
     * Map des nombres de sprites pour chaque action
     */
    private static final Map<Integer, Integer> ACTION_SPRITE_COUNT = new HashMap<>();
    /**
     * Initialise le nombre de sprites pour chaque action
     */
    static {
        for ( int action : new int[]{0, 8, 9, 10, 12} ) {
            ACTION_SPRITE_COUNT.put(action, 10);
        }
        for ( int action : new int[]{1, 2} ) {
            ACTION_SPRITE_COUNT.put(action, 8);
        }
        ACTION_SPRITE_COUNT.put(3, 9);
        for ( int action : new int[]{4, 5, 6} ) {
            ACTION_SPRITE_COUNT.put(action, 6);
        }
        for ( int action : new int[]{7, 11} ) {
            ACTION_SPRITE_COUNT.put(action, 4);
        }
    }
    /**
     * Retourne le nombre de sprites pour une action donnée
     * @param action Action
     * @return Nombre de sprites
     */
    public static int GetSpriteAmount( int action ) {
        return ACTION_SPRITE_COUNT.getOrDefault(action, 0);
    }
    /**
     * Retourne la valeur de la tile à la position donnée
     * @param x Position X de la tile
     * @param y Position Y de la tile
     * @param lvlData Données du niveau
     * @return Valeur de la tile
     */
    private static int GetLvlDataValue(float x, float y, int[][] lvlData) {
        float xIndex = x / TILES_SIZE;
        float yIndex = y / TILES_SIZE;

        return lvlData[(int) yIndex][(int) xIndex];
    }



    /**
     * Vérifie si l'entité est sur le sol
     * @param x Position X de l'entité
     * @param y Position Y de l'entité
     * @param lvlData Données du niveau
     * @return true si l'entité est sur le sol
     */
    private static boolean IsSolid(float x, float y, int[][] lvlData) {
        return (GetLvlDataValue(x, y, lvlData) != 21);
    }
    /**
     * Vérifie si l'entité est sur le sol
     * @param hitbox Hitbox de l'entité
     * @param lvlData Données du niveau
     * @return true si l'entité est sur le sol
     */
    public static boolean IsEntityOnFloor(Rectangle2D.Float hitbox, int[][] lvlData) {
        // Check the pixel below bottomleft and bottomright
        // System.out.println(IsSolid(hitbox.x, hitbox.y + hitbox.height + 1, lvlData) + " : left pixel");
        // System.out.println(IsSolid(hitbox.x + hitbox.width, hitbox.y + hitbox.height + 1, lvlData) + " : right pixel");
        if (!IsSolid(hitbox.x, hitbox.y + hitbox.height + 1, lvlData))
            if (!IsSolid(hitbox.x + hitbox.width, hitbox.y + hitbox.height + 1, lvlData))
                return false;

        return true;

    }
    /**
     * Retourne la position X de l'entité à côté d'un mur
     * @param hitbox Hitbox de l'entité
     * @param xSpeed Vitesse horizontale de l'entité
     * @return Position X de l'entité à côté d'un mur
     */
    public static float GetEntityXPosNextToWall(Rectangle2D.Float hitbox, float xSpeed) {
        int currentTile = (int) (hitbox.x / TILES_SIZE);
        if (xSpeed > 0) {
            // Right
            int tileXPos = currentTile * TILES_SIZE;
            int xOffset = (int) (TILES_SIZE - hitbox.width);
            return tileXPos + xOffset - 1;
        } else
            // Left
            return currentTile * TILES_SIZE;
    }
    /**
     * Retourne la position Y de l'entité sous le toit ou au-dessus du sol
     * @param hitbox Hitbox de l'entité
     * @param airSpeed Vitesse verticale de l'entité
     * @return Position Y de l'entité sous le toit ou au-dessus du sol
     */
    public static float GetEntityYPosUnderRoofOrAboveFloor(Rectangle2D.Float hitbox, float airSpeed) {
        int currentTile = (int) (hitbox.y / TILES_SIZE);
        // System.out.println((hitbox.height / TILES_SIZE) + " : currentTile");
        if (airSpeed > 0) {
            // Falling - touching floor
            int tileYPos = currentTile * TILES_SIZE;
            int yOffset = (int) (TILES_DEFAULT_SIZE - hitbox.height );
            return tileYPos - yOffset + TILES_DEFAULT_SIZE - 1;
        } else
            // Jumping
            //System.out.println(currentTile * TILES_SIZE + " : currentTile");
            return currentTile * TILES_SIZE;

    }

    /**
     * Vérifie si une tile est une plateforme one-way
     * @param tileId ID de la tile à vérifier
     * @return true si c'est une plateforme one-way
     */
    public static boolean isOneWayPlatform(int tileId) {
        for (int oneWayId : Constants.WORLD.ONE_WAY_PLATFORMS.ONE_WAY_TILE_IDS) {
            if (tileId == oneWayId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie si le joueur peut passer à travers une plateforme one-way
     * @param hitbox Hitbox du joueur
     * @param tileId ID de la tile
     * @param airSpeed Vitesse verticale du joueur
     * @param isDownPressed Si la touche down est pressée
     * @return true si le joueur peut passer à travers
     */
    public static boolean canPassThroughOneWay(Rectangle2D.Float hitbox, int tileId, float airSpeed, boolean isDownPressed) {
        if (!isOneWayPlatform(tileId))
            return false;
        

        if (airSpeed < 0) {
            return true;
        }
            
        // Le joueur peut passer à travers si :
        // 1. Il tombe (airSpeed > 0) OU
        // 2. Il est suffisamment au-dessous de la plateforme OU
        // 3. Il appuie sur la touche down (nouvelle fonctionnalité)
        return ((hitbox.y - Constants.WORLD.ONE_WAY_PLATFORMS.ONE_WAY_TOLERANCE) >
            ((int)(hitbox.y / TILES_SIZE) + 1) * TILES_SIZE) ||
            isDownPressed;
    }

    /**
     * Vérifie si une position est solide en tenant compte des plateformes one-way
     * @param x Position X
     * @param y Position Y  
     * @param lvlData Données du niveau
     * @param hitbox Hitbox de l'entité (pour les plateformes one-way)
     * @param airSpeed Vitesse verticale (pour les plateformes one-way)
     * @return true si la position est solide
     */
    public static boolean IsSolid (float x, float y, int[][] lvlData, Rectangle2D.Float hitbox, float airSpeed, boolean isDownPressed) {
        int tileId = GetLvlDataValue(x, y, lvlData);
        
        // Si c'est une plateforme one-way, vérifier si on peut passer à travers
        if (isOneWayPlatform(tileId)) {
            return !canPassThroughOneWay(hitbox, tileId, airSpeed, isDownPressed);
        }
        
        // Comportement normal pour les autres tiles
        return (tileId != 21);
    }

    /**
     * Vérifie si le joueur peut se déplacer ici en tenant compte des plateformes one-way
     * @param x Position X
     * @param y Position Y
     * @param width Largeur
     * @param height Hauteur
     * @param lvlData Données du niveau
     * @param hitbox Hitbox de l'entité
     * @param airSpeed Vitesse verticale
     * @return true si le mouvement est possible
     */
    public static boolean CanMoveHere(float x, float y, float width, float height, int[][] lvlData, Rectangle2D.Float hitbox, float airSpeed, boolean isDownPressed) {
        if (x < 0 || x >= lvlData[0].length * TILES_SIZE || y < 0 || y >= lvlData.length * TILES_SIZE)
            return false;

        // Vérifier les coins avec la logique one-way
        boolean bottomLeft = !IsSolid(x, y + height, lvlData, hitbox, airSpeed, isDownPressed);
        boolean bottomRight = !IsSolid(x + width, y + height, lvlData, hitbox, airSpeed, isDownPressed);
        boolean topLeft = !IsSolid(x, y, lvlData, hitbox, airSpeed, isDownPressed);
        boolean topRight = !IsSolid(x + width, y, lvlData, hitbox, airSpeed, isDownPressed);
        
        return bottomLeft && bottomRight && topLeft && topRight;
    }

    /**
     * Vérifie si l'entité est sur le sol en tenant compte des plateformes one-way
     * @param hitbox Hitbox de l'entité
     * @param lvlData Données du niveau
     * @param airSpeed Vitesse verticale
     * @return true si l'entité est sur le sol
     */
    public static boolean IsEntityOnFloor(Rectangle2D.Float hitbox, int[][] lvlData, float airSpeed) {
        // Vérifier les pixels en dessous avec la logique one-way
        boolean leftPixel = IsSolid(hitbox.x, hitbox.y + hitbox.height + 1, lvlData, hitbox, airSpeed, false);
        boolean rightPixel = IsSolid (hitbox.x + hitbox.width, hitbox.y + hitbox.height + 1, lvlData, hitbox, airSpeed, false);
        
        return leftPixel || rightPixel;
    }
    /**
     * Vérifie si l'entité est dans une tile one-way
     * @param hitbox Hitbox de l'entité
     * @param lvlData Données du niveau
     * @return true si l'entité est dans une tile one-way
     */
    public static boolean IsInOneWayTile(Rectangle2D.Float hitbox, int[][] lvlData) {

        // Si un des pixels de l'hitbox est dans une tile one-way return true
        // donc regarder chaque coins de l'hitbox
        return isOneWayPlatform(GetLvlDataValue(hitbox.x, hitbox.y, lvlData));
    }
    /**
     * Map et clamps une valeur entre deux valeurs
     * @param value Valeur à mapper
     * @param inMin Valeur minimale
     * @param inMax Valeur maximale
     * @param outMin Valeur minimale de sortie
     * @param outMax Valeur maximale de sortie
     * @return Valeur mapée et clampée
     */
    public static int mapAndClamp(float value, float inMin, float inMax, int outMin, int outMax) {
        float t = (value - inMin) / (inMax - inMin);

        // Clamp entre 0 et 1
        t = clamp(t, 0, 1);

    
        t = 1 - (1 - t) * (1 - t); // EaseOutQuad 

        return (int)(outMin + t * (outMax - outMin));
    }


    /**
     * Clamp une valeur entre deux valeurs
     * @param v Valeur à clamer
     * @param min Valeur minimale
     * @param max Valeur maximale
     * @return Valeur clampée
     */
    public static float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }

}