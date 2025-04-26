package utilz;

import com.google.gson.*;
import duel.Team;
import icmon.ICMon;
import icmon.Move;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.*;

import static utilz.Constants.PATHS.DATA_FILE;
import static utilz.Constants.language;

public class HelpMethods {

    
    private static final Random rnd = new Random();

    // Cache des données pour éviter des lectures répétées
    private static Map<String, JsonObject> jsonCache = new HashMap<>();
    private static Gson gson = new Gson();

    private static ResourceBundle bundle;

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