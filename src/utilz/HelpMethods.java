package utilz;

// Java standard library imports
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import ui.settings.DefaultValues;
import static utilz.Constants.WORLD.TILES_SIZE;
import static utilz.Constants.language;
/**
 * Cette classe contient des méthodes utilitaires pour le jeu
 * @implNote Cette classe est utilisée pour gérer les données du jeu
 */
public class HelpMethods {

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
     * Convertit un code de langue en Locale
     * @param langCode Code de langue (ex: "en", "fr", "de")
     * @return Locale correspondant
     */
    private static Locale getLocaleFromLanguageCode(String langCode) {
        // Utiliser forLanguageTag pour les codes standards (en, fr, etc.)
        return Locale.forLanguageTag(langCode);
    }
    
    /**
     * Retourne une phrase à partir de son nom
     * @param name Nom de la phrase
     * @return Phrase
     */
    public static String GetPhrase(String name){
        try {
            // Utilisation du chemin complet correspondant à la structure de vos fichiers
            bundle = ResourceBundle.getBundle("data.langue", getLocaleFromLanguageCode(language), new ResourceControl());
            return bundle.getString(name);


        } catch (MissingResourceException e) {
            System.err.printf("Erreur lors de la récupération de la phrase : {%s} - {%s}%n", name, e.getMessage());
            // Tentative de fallback vers l'anglais si la langue demandée n'existe pas
            try {
                bundle = ResourceBundle.getBundle("res.data.langue", Locale.ENGLISH);
                return bundle.getString(name);
            } catch (MissingResourceException e2) {
                return "Error: " + name;
            }finally{
                System.exit(3);
            }
        }
    }
    /**
     * Classe interne pour gérer le chargement des ressources depuis le dossier res
     */
    private static class ResourceControl extends ResourceBundle.Control {
        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format,
                ClassLoader loader, boolean reload) throws IllegalAccessException,
                InstantiationException, IOException {
            if (format.equals("java.properties")) {
                String bundleName = toBundleName(baseName, locale);
                String resourceName = toResourceName(bundleName, "properties");
                
                
                try (java.io.InputStream stream = loader.getResourceAsStream("res/" + resourceName)) {
                    if (stream != null) {
                        try (java.io.InputStreamReader reader = new java.io.InputStreamReader(stream, "UTF-8")) {
                            return new java.util.PropertyResourceBundle(reader);
                        }
                    }
                }
            }
            return super.newBundle(baseName, locale, format, loader, reload);
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
        } catch (IOException | JsonSyntaxException e) {
            System.err.println("Erreur lors du chargement du niveau depuis le JSON: " + e.getMessage());
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
        } catch (JsonSyntaxException | IOException e) {
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
     * =============================== Sauvegarde / Chargement config ========================
     */
    
    /**
     * Classe interne pour stocker la configuration du joueur
     * Utilisée pour la sérialisation/désérialisation JSON
     */
    private static class PlayerConfigData {
        // Paramètres Gameplay
        float acceleration;
        float maxSpeedX;
        float jumpForce;
        float gravity;
        float dashSpeed;
        
        // Paramètres Advanced Physics
        float airResistance;
        float groundFriction;
        float fastFallMultiplier;
        int coyoteTimeFrames;
        float apexGravityMultiplier;
        float apexAccelerationMultiplier;
        
        String language;
        
        // Constructeur par défaut requis pour Gson
        PlayerConfigData() {}
        
        // Constructeur avec toutes les valeurs
        PlayerConfigData(float acceleration, float maxSpeedX, float jumpForce, float gravity, float dashSpeed,
                        float airResistance, float groundFriction, float fastFallMultiplier, int coyoteTimeFrames,
                        float apexGravityMultiplier, float apexAccelerationMultiplier, String language) {
            this.acceleration = acceleration;
            this.maxSpeedX = maxSpeedX;
            this.jumpForce = jumpForce;
            this.gravity = gravity;
            this.dashSpeed = dashSpeed;
            this.airResistance = airResistance;
            this.groundFriction = groundFriction;
            this.fastFallMultiplier = fastFallMultiplier;
            this.coyoteTimeFrames = coyoteTimeFrames;
            this.apexGravityMultiplier = apexGravityMultiplier;
            this.apexAccelerationMultiplier = apexAccelerationMultiplier;
            this.language = language;
        }

    }
    
    /**
     * Chemin vers le fichier de sauvegarde de configuration
     */
    private static final String SAVE_CONFIG_PATH = "res/data/save_config.json";

    /**
     * Sauvegarde les paramètres de la configuration des constantes du joueur dans un fichier JSON
     * Sauvegarde aussi la langue sélectionnée
     * @return true si la sauvegarde a réussi, false sinon
     */
    public static boolean save_config(){
        try {
            // Créer un objet de configuration avec les valeurs actuelles
            PlayerConfigData config = new PlayerConfigData(
                Constants.PLAYER.ACCELERATION,
                Constants.PLAYER.MAX_SPEED_X,
                Constants.PLAYER.JUMP_FORCE,
                Constants.PLAYER.GRAVITY,
                Constants.PLAYER.DASH_SPEED,
                Constants.PLAYER.AIR_RESISTANCE,
                Constants.PLAYER.GROUND_FRICTION,
                Constants.PLAYER.FAST_FALL_MULT,
                Constants.PLAYER.COYOTE_TIME_FRAMES,
                Constants.PLAYER.APEX_GRAVITY_MULT,
                Constants.PLAYER.APEX_ACCEL_MULT,
                Constants.language
            );
            
            // Sérialiser en JSON avec formatage
            Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
            String json = prettyGson.toJson(config);
            
            // Créer le répertoire parent s'il n'existe pas
            File file = new File(SAVE_CONFIG_PATH);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            // Écrire dans le fichier
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(json);
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde de la configuration: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Sauvegarde toutes les données du jeu (config, partie, etc.)
     * @return true si toutes les sauvegardes ont réussi, false sinon
     */
    public static boolean save_all(){        
        // Affichage des données sauvegardées
        System.out.println("Données sauvegardées :");
        System.out.println("  Acceleration = " + Constants.PLAYER.ACCELERATION);
        System.out.println("  Max Speed X  = " + Constants.PLAYER.MAX_SPEED_X);
        System.out.println("  Jump Force   = " + Constants.PLAYER.JUMP_FORCE);
        System.out.println("  Gravity      = " + Constants.PLAYER.GRAVITY);
        System.out.println("  Dash Speed   = " + Constants.PLAYER.DASH_SPEED);
        System.out.println("  Air Resistance = " + Constants.PLAYER.AIR_RESISTANCE);
        System.out.println("  Ground Friction = " + Constants.PLAYER.GROUND_FRICTION);
        System.out.println("  Fast Fall Multiplier = " + Constants.PLAYER.FAST_FALL_MULT);
        System.out.println("  Coyote Time Frames = " + Constants.PLAYER.COYOTE_TIME_FRAMES);
        System.out.println("  Apex Gravity Multiplier = " + Constants.PLAYER.APEX_GRAVITY_MULT);
        System.out.println("  Apex Acceleration Multiplier = " + Constants.PLAYER.APEX_ACCEL_MULT);
        System.out.println("  Langue       = " + Constants.language);
        
        System.out.println("Save done");
        return save_config();
    }
    
    /**
     * Détecte s'il y a un fichier save_config.json
     * @return true si le fichier existe, false sinon
     */
    public static boolean detect_save(){
        File file = new File(SAVE_CONFIG_PATH);
        return file.exists() && file.isFile();
    }

    /**
     * Charge la configuration depuis save_config.json et remplace la configuration par défaut
     * Charge aussi la langue sauvegardée
     * @return true si le chargement a réussi, false sinon
     */
    public static boolean charger_config(){
        // Vérifier si le fichier existe
        if (!detect_save()) {
            return false;
        }
        
        try {
            // Lire et parser le fichier JSON
            try (FileReader reader = new FileReader(SAVE_CONFIG_PATH)) {
                PlayerConfigData config = gson.fromJson(reader, PlayerConfigData.class);
                
                // Vérifier que la configuration a été chargée correctement
                if (config == null) {
                    System.err.println("Erreur: Impossible de parser la configuration depuis le fichier JSON");
                    return false;
                }
                
                // Valider et appliquer les valeurs chargées aux constantes du joueur
                // Utiliser les valeurs par défaut si les valeurs chargées sont invalides
                Constants.PLAYER.ACCELERATION = validateFloat(config.acceleration, 0f, 5f, DefaultValues.getDefaultAcceleration());
                Constants.PLAYER.MAX_SPEED_X = validateFloat(config.maxSpeedX, 0.1f, 50f, DefaultValues.getDefaultMaxSpeedX());
                Constants.PLAYER.JUMP_FORCE = validateFloat(config.jumpForce, -100f, 100f, DefaultValues.getDefaultJumpForce());
                Constants.PLAYER.GRAVITY = validateFloat(config.gravity, -5f, 50f, DefaultValues.getDefaultGravity());
                Constants.PLAYER.DASH_SPEED = validateFloat(config.dashSpeed, 0f, 500f, DefaultValues.getDefaultDashSpeed());
                
                // Paramètres Advanced Physics
                // validateFloat retourne la valeur par défaut si invalide, donc compatible avec anciennes sauvegardes
                Constants.PLAYER.AIR_RESISTANCE = validateFloat(config.airResistance, 0.5f, 1.0f, DefaultValues.getDefaultAirResistance());
                Constants.PLAYER.GROUND_FRICTION = validateFloat(config.groundFriction, 0.5f, 1.0f, DefaultValues.getDefaultGroundFriction());
                Constants.PLAYER.FAST_FALL_MULT = validateFloat(config.fastFallMultiplier, 1.0f, 2.0f, DefaultValues.getDefaultFastFallMultiplier());
                int validatedCoyote = (int) validateFloat(config.coyoteTimeFrames, 0f, 10f, DefaultValues.getDefaultCoyoteTimeFrames());
                Constants.PLAYER.COYOTE_TIME_FRAMES = validatedCoyote;
                Constants.PLAYER.APEX_GRAVITY_MULT = validateFloat(config.apexGravityMultiplier, 0.1f, 1.0f, DefaultValues.getDefaultApexGravityMultiplier());
                Constants.PLAYER.APEX_ACCEL_MULT = validateFloat(config.apexAccelerationMultiplier, 1.0f, 3.0f, DefaultValues.getDefaultApexAccelerationMultiplier());
                
                // Appliquer la langue sauvegardée si elle existe
                if (config.language != null && !config.language.isEmpty()) {
                    Constants.SetLanguage(config.language);
                }
                
                return true;
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la configuration: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
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
        for( int action : new int[]{13} ) {
            ACTION_SPRITE_COUNT.put(action, 10);
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
     * Vérifie si une position est solide en tenant compte des plateformes one-way (version avec Vector2D)
     * @param x Position X
     * @param y Position Y  
     * @param lvlData Données du niveau
     * @param hitbox Hitbox de l'entité (pour les plateformes one-way)
     * @param velocity Vélocité de l'entité (pour les plateformes one-way)
     * @param isDownPressed Si la touche down est pressée
     * @return true si la position est solide
     */
    public static boolean IsSolid (float x, float y, int[][] lvlData, Rectangle2D.Float hitbox, physics.Vector2D velocity, boolean isDownPressed) {
        int tileId = GetLvlDataValue(x, y, lvlData);
        
        // Si c'est une plateforme one-way, vérifier si on peut passer à travers
        if (isOneWayPlatform(tileId)) {
            return !canPassThroughOneWay(hitbox, tileId, velocity.y, isDownPressed);
        }
        
        // Comportement normal pour les autres tiles
        return (tileId != 21);
    }

    // ========== NOUVELLES MÉTHODES AABB ==========
    
    /**
     * Vérifie les collisions AABB avec les rectangles de collision du niveau
     * @param hitbox Hitbox de l'entité
     * @param level Niveau contenant les rectangles de collision
     * @return true s'il y a collision avec un rectangle solide
     */
    public static boolean checkAABBCollision(Rectangle2D.Float hitbox, levels.Level level) {
        for (Rectangle2D.Float collisionRect : level.getSolidCollisions()) {
            if (hitbox.intersects(collisionRect)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Vérifie les collisions AABB avec les plateformes one-way
     * 
     * CORRECTION CRITIQUE: Cette méthode doit vérifier les plateformes one-way
     * même quand velocity.y == 0 (joueur immobile) pour éviter le flottement.
     * 
     * @param hitbox Hitbox de l'entité
     * @param level Niveau contenant les rectangles de collision
     * @param velocity Vélocité de l'entité
     * @param isDownPressed Si la touche down est pressée
     * @return true s'il y a collision avec une plateforme one-way
     */
    public static boolean checkOneWayPlatformCollision(Rectangle2D.Float hitbox, levels.Level level, physics.Vector2D velocity, boolean isDownPressed) {
        for (Rectangle2D.Float platformRect : level.getOneWayPlatformCollisions()) {
            if (hitbox.intersects(platformRect)) {
                // CORRECTION: Vérifier les plateformes one-way dans TOUS les cas sauf montée
                // - velocity.y > 0 : le joueur tombe → vérifier
                // - velocity.y == 0 : le joueur est immobile → vérifier AUSSI (clé du fix!)
                // - velocity.y < 0 : le joueur monte → ne pas vérifier
                if (velocity.y >= 0 && // Entité tombe OU immobile
                    hitbox.y + hitbox.height <= platformRect.y + Constants.WORLD.ONE_WAY_PLATFORMS.ONE_WAY_TOLERANCE && // Entité au-dessus de la plateforme
                    !isDownPressed) { // Touche down non pressée
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Vérifie si une plateforme one-way doit bloquer le joueur
     * 
     * Règles:
     * - Montée (velocity.y < 0): Jamais bloquer, toujours passer
     * - Descente (velocity.y >= 0): Bloquer SI le bas de la hitbox est au-dessus de la plateforme
     * - Down pressé: Jamais bloquer, forcer le passage
     * 
     * @param hitbox Hitbox du joueur
     * @param platformRect Rectangle de la plateforme
     * @param velocity Vélocité du joueur
     * @param isDownPressed Si la touche down est pressée
     * @return true si la plateforme doit bloquer le joueur
     */
    public static boolean shouldOneWayPlatformBlock(
        Rectangle2D.Float hitbox, 
        Rectangle2D.Float platformRect, 
        physics.Vector2D velocity, 
        boolean isDownPressed
    ) {
        // Si touche down pressée, toujours laisser passer
        if (isDownPressed) {
            return false;
        }
        
        // Si le joueur monte, toujours laisser passer
        if (velocity.y < 0) {
            return false;
        }
        
        // Le joueur descend (velocity.y >= 0)
        // Bloquer SEULEMENT si le bas du joueur est au-dessus de la plateforme
        float playerBottom = hitbox.y + hitbox.height;
        float platformTop = platformRect.y;
        
        // Utiliser la tolérance pour éviter les accrochages
        return playerBottom <= platformTop + Constants.WORLD.ONE_WAY_PLATFORMS.ONE_WAY_TOLERANCE;
    }
    
    /**
     * Obtient la première plateforme one-way qui bloque le joueur
     * 
     * @param hitbox Hitbox du joueur après mouvement
     * @param level Niveau contenant les plateformes
     * @param velocity Vélocité du joueur
     * @param isDownPressed Si la touche down est pressée
     * @return Rectangle de la plateforme qui bloque, ou null si aucune
     */
    public static Rectangle2D.Float getBlockingOneWayPlatform(
        Rectangle2D.Float hitbox,
        levels.Level level,
        physics.Vector2D velocity,
        boolean isDownPressed
    ) {
        for (Rectangle2D.Float platformRect : level.getOneWayPlatformCollisions()) {
            if (hitbox.intersects(platformRect)) {
                if (shouldOneWayPlatformBlock(hitbox, platformRect, velocity, isDownPressed)) {
                    return platformRect;
                }
            }
        }
        return null;
    }
    
    /**
     * Vérifie si l'entité peut se déplacer à une position donnée avec AABB
     * @param x Position X
     * @param y Position Y
     * @param width Largeur
     * @param height Hauteur
     * @param level Niveau contenant les rectangles de collision
     * @param velocity Vélocité de l'entité
     * @param isDownPressed Si la touche down est pressée
     * @return true si le mouvement est possible
     */
    public static boolean CanMoveHereAABB(float x, float y, float width, float height, levels.Level level, physics.Vector2D velocity, boolean isDownPressed) {
        // Vérifier les limites du niveau
        int levelWidth = level.getLevelData()[0].length * TILES_SIZE;
        int levelHeight = level.getLevelData().length * TILES_SIZE;
        
        if (x < 0 || x >= levelWidth || y < 0 || y >= levelHeight) {
            return false;
        }
        
        // Créer une hitbox temporaire pour la position testée
        Rectangle2D.Float testHitbox = new Rectangle2D.Float(x, y, width, height);
        
        // Vérifier les collisions solides
        if (checkAABBCollision(testHitbox, level)) {
            return false;
        }
        
        // Vérifier les plateformes one-way
        if (checkOneWayPlatformCollision(testHitbox, level, velocity, isDownPressed)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Vérifie si l'entité est sur le sol avec AABB
     * 
     * CORRECTION MAJEURE: Cette méthode gère maintenant correctement les cas où
     * le joueur est immobile (velocity.y == 0) sur une plateforme one-way.
     * 
     * @param hitbox Hitbox de l'entité
     * @param level Niveau contenant les rectangles de collision
     * @param velocity Vélocité de l'entité
     * @return true si l'entité est sur le sol
     */
    public static boolean IsEntityOnFloorAABB(Rectangle2D.Float hitbox, levels.Level level, physics.Vector2D velocity) {
        // Créer une hitbox légèrement en dessous pour détecter le sol
        // Cette hitbox de 1 pixel de hauteur est placée juste sous les pieds du joueur
        Rectangle2D.Float groundCheckHitbox = new Rectangle2D.Float(
            hitbox.x, 
            hitbox.y + hitbox.height + 1, 
            hitbox.width, 
            1
        );
        
        // Vérifier d'abord les collisions avec les blocs solides (murs, sol normal)
        if (checkAABBCollision(groundCheckHitbox, level)) {
            return true;
        }
        
        // Vérifier les plateformes one-way
        // CORRECTION CRITIQUE: Vérifier les plateformes one-way dans TOUS les cas sauf montée
        // - velocity.y > 0 : le joueur tombe → vérifier
        // - velocity.y == 0 : le joueur est immobile → vérifier AUSSI (c'est la clé du fix!)
        // - velocity.y < 0 : le joueur monte → ne pas vérifier
        if (velocity.y >= 0) {
            return checkOneWayPlatformCollision(groundCheckHitbox, level, velocity, false);
        }
        
        // Si velocity.y < 0, le joueur monte (saut) → pas sur le sol
        return false;
    }
    
    /**
     * Trouve la position X de l'entité à côté d'un mur avec AABB
     * @param hitbox Hitbox de l'entité
     * @param xSpeed Vitesse horizontale de l'entité
     * @param level Niveau contenant les rectangles de collision
     * @return Position X de l'entité à côté d'un mur
     */
    public static float GetEntityXPosNextToWallAABB(Rectangle2D.Float hitbox, float xSpeed, levels.Level level) {
        if (xSpeed > 0) {
            // Déplacement vers la droite - trouver le mur le plus proche à droite
            float closestWallX = Float.MAX_VALUE;
            
            for (Rectangle2D.Float collisionRect : level.getSolidCollisions()) {
                if (collisionRect.y < hitbox.y + hitbox.height && 
                    collisionRect.y + collisionRect.height > hitbox.y &&
                    collisionRect.x > hitbox.x + hitbox.width) {
                    closestWallX = Math.min(closestWallX, collisionRect.x);
                }
            }
            
            if (closestWallX != Float.MAX_VALUE) {
                return closestWallX - hitbox.width - 1;
            }
        } else {
            // Déplacement vers la gauche - trouver le mur le plus proche à gauche
            float closestWallX = Float.MIN_VALUE;
            
            for (Rectangle2D.Float collisionRect : level.getSolidCollisions()) {
                if (collisionRect.y < hitbox.y + hitbox.height && 
                    collisionRect.y + collisionRect.height > hitbox.y &&
                    collisionRect.x + collisionRect.width < hitbox.x) {
                    closestWallX = Math.max(closestWallX, collisionRect.x + collisionRect.width);
                }
            }
            
            if (closestWallX != Float.MIN_VALUE) {
                return closestWallX + 1;
            }
        }
        
        return hitbox.x; // Pas de collision trouvée
    }
    
    /**
     * Trouve la position Y de l'entité sous le toit ou au-dessus du sol avec AABB
     * @param hitbox Hitbox de l'entité
     * @param airSpeed Vitesse verticale de l'entité
     * @param level Niveau contenant les rectangles de collision
     * @return Position Y de l'entité sous le toit ou au-dessus du sol
     */
    public static float GetEntityYPosUnderRoofOrAboveFloorAABB(Rectangle2D.Float hitbox, float airSpeed, levels.Level level) {
        if (airSpeed > 0) {
            // Chute - trouver le sol le plus proche en dessous
            float closestFloorY = Float.MAX_VALUE;
            
            for (Rectangle2D.Float collisionRect : level.getSolidCollisions()) {
                if (collisionRect.x < hitbox.x + hitbox.width && 
                    collisionRect.x + collisionRect.width > hitbox.x &&
                    collisionRect.y > hitbox.y + hitbox.height) {
                    closestFloorY = Math.min(closestFloorY, collisionRect.y);
                }
            }
            
            if (closestFloorY != Float.MAX_VALUE) {
                return closestFloorY - hitbox.height - 1;
            }
        } else {
            // Saut - trouver le plafond le plus proche au-dessus
            float closestCeilingY = Float.MIN_VALUE;
            
            for (Rectangle2D.Float collisionRect : level.getSolidCollisions()) {
                if (collisionRect.x < hitbox.x + hitbox.width && 
                    collisionRect.x + collisionRect.width > hitbox.x &&
                    collisionRect.y + collisionRect.height < hitbox.y) {
                    closestCeilingY = Math.max(closestCeilingY, collisionRect.y + collisionRect.height);
                }
            }
            
            if (closestCeilingY != Float.MIN_VALUE) {
                return closestCeilingY + 1;
            }
        }
        
        return hitbox.y; // Pas de collision trouvée
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
     * Vérifie si le joueur peut se déplacer ici en tenant compte des plateformes one-way (version avec Vector2D)
     * @param x Position X
     * @param y Position Y
     * @param width Largeur
     * @param height Hauteur
     * @param lvlData Données du niveau
     * @param hitbox Hitbox de l'entité
     * @param velocity Vélocité de l'entité
     * @param isDownPressed Si la touche down est pressée
     * @return true si le mouvement est possible
     */
    public static boolean CanMoveHere(float x, float y, float width, float height, int[][] lvlData, Rectangle2D.Float hitbox, physics.Vector2D velocity, boolean isDownPressed) {
        if (x < 0 || x >= lvlData[0].length * TILES_SIZE || y < 0 || y >= lvlData.length * TILES_SIZE)
            return false;

        // Vérifier les coins avec la logique one-way
        boolean bottomLeft = !IsSolid(x, y + height, lvlData, hitbox, velocity, isDownPressed);
        boolean bottomRight = !IsSolid(x + width, y + height, lvlData, hitbox, velocity, isDownPressed);
        boolean topLeft = !IsSolid(x, y, lvlData, hitbox, velocity, isDownPressed);
        boolean topRight = !IsSolid(x + width, y, lvlData, hitbox, velocity, isDownPressed);
        
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
     * Vérifie si l'entité est sur le sol en tenant compte des plateformes one-way (version avec Vector2D)
     * @param hitbox Hitbox de l'entité
     * @param lvlData Données du niveau
     * @param velocity Vélocité de l'entité
     * @return true si l'entité est sur le sol
     */
    public static boolean IsEntityOnFloor(Rectangle2D.Float hitbox, int[][] lvlData, physics.Vector2D velocity) {
        // Vérifier les pixels en dessous avec la logique one-way
        boolean leftPixel = IsSolid(hitbox.x, hitbox.y + hitbox.height + 1, lvlData, hitbox, velocity, false);
        boolean rightPixel = IsSolid(hitbox.x + hitbox.width, hitbox.y + hitbox.height + 1, lvlData, hitbox, velocity, false);
        
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
        for(float i = hitbox.x; i < hitbox.x + hitbox.width; i++)
            for(float j = hitbox.y; j < hitbox.y + hitbox.height; j++)
                if(isOneWayPlatform(GetLvlDataValue(i, j, lvlData)))
                    return true;
        return false;
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

    /**
     * Valide une valeur float et retourne la valeur par défaut si invalide
     * @param value Valeur à valider
     * @param min Valeur minimale acceptée
     * @param max Valeur maximale acceptée
     * @param defaultValue Valeur par défaut si invalide
     * @return Valeur validée ou valeur par défaut
     */
    private static float validateFloat(float value, float min, float max, float defaultValue) {
        // Vérifier si la valeur est NaN ou Infinity
        if (Float.isNaN(value) || Float.isInfinite(value)) {
            System.err.println("Valeur invalide détectée (NaN ou Infinity), utilisation de la valeur par défaut: " + defaultValue);
            return defaultValue;
        }
        
        // Vérifier si la valeur est dans la plage valide
        if (value < min || value > max) {
            System.err.println("Valeur hors limites [" + min + ", " + max + "]: " + value + ", utilisation de la valeur par défaut: " + defaultValue);
            return defaultValue;
        }
        
        return value;
    }

}