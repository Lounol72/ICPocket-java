package utilz;

import com.google.gson.*;
import icmon.ICMon;
import icmon.Move;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static utilz.Constants.PATHS.DATA_FILE;
import static utilz.Constants.PATHS.LANGUAGE_FILE;
import static utilz.Constants.language;

public class HelpMethods {

    
    private static final Random rnd = new Random();

    // Cache des données pour éviter des lectures répétées
    private static Map<String, JsonObject> jsonCache = new HashMap<>();
    private static Gson gson = new Gson();

    /**
     * Charge un fichier JSON en cache s'il n'y est pas déjà
     */
    private static JsonObject getJsonData(String filePath) throws IOException {
        if (!jsonCache.containsKey(filePath)) {
            try (Reader reader = new FileReader(filePath)) {
                JsonObject data = JsonParser.parseReader(reader).getAsJsonObject();
                jsonCache.put(filePath, data);
            }
        }
        return jsonCache.get(filePath);
    }

    /**
     * Génère un ICMon à partir de son id
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
     * Extrait les stats depuis un objet JSON
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
     * Génère un Move à partir de son id
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

    public static String GetPhrase(String name){
        try{
            JsonObject jsonData = getJsonData(LANGUAGE_FILE);
            JsonObject languageData = jsonData.getAsJsonObject("languages").get(language).getAsJsonObject();
            return languageData.get(name).getAsString();
        } catch (Exception e){
            System.err.println("Erreur lors de la récupération de la phrase :" + name + ">>" + e.getMessage());
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

}