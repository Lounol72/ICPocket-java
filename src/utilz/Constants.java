package utilz;


/**
 * This class contains constants and utility methods used throughout the application.
 * It provides nested classes organizing these constants
 * into logical groupings such as battle mechanics, type charts, stats, and more.
 */
public class Constants {
    public final static float SCALE = 1.0f;                              // Facteur d'échelle global
    public static String language  = "en";

    public static class PLAYER{
        // === PHYSICS DEBUG ===
        public static final boolean DEBUG_PHYSICS = false; // Activer logs physique
        
        // === VITESSES LIMITES (Hollow Knight style) ===
        // Hollow Knight: ~5-6 unités/s horizontal, ~15-20 unités/s vertical
        public static final float MAX_SPEED_X = 3f * SCALE;        // Vitesse horizontale max
        public static final float MAX_FALL_SPEED = 8f * SCALE;     // AUGMENTÉ: Chute plus rapide type HK
        public static final float MAX_RISE_SPEED = 10f * SCALE;    // AUGMENTÉE: Cohérente avec JUMP_FORCE plus forte
        
        // === GRAVITÉ (toujours strictement limitée) ===
        public static final float GRAVITY = 0.15f * SCALE;         // RÉDUITE: Gravité plus douce pour saut plus haut
        public static final float GRAVITY_MULTIPLIER_BASE = 1.0f;  // Multiplicateur par défaut
        
        // === MASSE ET FORCES ===
        public static final float MASS = 0.8f;                     // Masse légère = réactif (Hollow Knight)
        public static final float ACCELERATION = 0.4f * SCALE;     // Accélération horizontale
        
        // === SAUT (Hollow Knight: saut court et réactif) ===
        public static final float JUMP_FORCE = -12f * SCALE;      // Force de saut
        public static final float JUMP_CUT_MULTIPLIER = 0.5f;      // Coupure saut si relâché
        
        // === APEX (contrôle au sommet du saut) ===
        public static final float APEX_THRESHOLD = 2.5f * SCALE;   // AUGMENTÉ: Seuil plus large pour apex control
        public static final float APEX_GRAVITY_MULT = 0.3f;        // RÉDUITE: Plus de floatiness au sommet
        public static final float APEX_ACCEL_MULT = 1.5f;          // Contrôle renforcé à l'apex
        
        // === RÉSISTANCES ===
        public static final float AIR_RESISTANCE = 0.98f;          // Résistance air (2%)
        public static final float GROUND_FRICTION = 0.88f;         // Friction sol (12%)
        
        // === CHUTE RAPIDE ===
        public static final float FAST_FALL_MULT = 2.0f;           // Fast fall (appui bas)
        
        // === COYOTE TIME ===
        public static final int COYOTE_TIME_FRAMES = 6;            // Frames de grâce après quitter sol
        
        // === ANIMATIONS ===
        public static final int ANI_SPEED = 25;
        public static final float PLAYER_SPEED_RUN = SCALE;
        public static final float JUMP_SPEED_MAX = -5f * SCALE;
        public static final float FALL_SPEED_AFTER_COLLISION = 0.5f * SCALE;
        public static final float MAX_AIR_SPEED = 3f * SCALE;
        public static final float MAX_SPEED_Y = 10f * SCALE;

        public static class HITBOX{
            private static final int HITBOX_WIDTH_DEFAULT = 19;
            private static final int HITBOX_HEIGHT_DEFAULT = 32;
            public static final int HITBOX_WIDTH =(int) (HITBOX_WIDTH_DEFAULT * SCALE);
            public static final int HITBOX_HEIGHT =(int) (HITBOX_HEIGHT_DEFAULT * SCALE);
        }
    }

    public static class UI{
        public static class BUTTONS{
            public static final int DEFAULT_WIDTH = 192;
            public static final int DEFAULT_HEIGHT = 31;
            public static final int WIDTH = (int) (DEFAULT_WIDTH * SCALE);
            public static final int HEIGHT = (int) (DEFAULT_HEIGHT * SCALE);
        }
    }
    public static class PATHS{
        public static final String ROOT = "res/";

        public static final String DATA = ROOT + "data/";
        public static final String DATA_FILE = DATA + "data.json";
        public static final String LANGUAGE_FILE = DATA + "language.json";


    }
    public static class WORLD{
        public final static int TILES_DEFAULT_SIZE = 32;                           // Taille de base d'une tuile
        public final static int TILES_IN_WIDTH = 26;                                // Nombre de tuiles en largeur
        public final static int TILES_IN_HEIGHT = 14;                               // Nombre de tuiles en hauteur
        public final static int TILES_SIZE = (int) (TILES_DEFAULT_SIZE * SCALE);  // Taille d'une tuile après mise à l'échelle
        public final static int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;           // Largeur totale du jeu
        public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;         // Hauteur totale du jeu
        public static class ONE_WAY_PLATFORMS{
            public static final int[] ONE_WAY_TILE_IDS = {6, 13, 19, 20}; 
            public static final float ONE_WAY_TOLERANCE = 3f * SCALE;     // RÉDUITE: Moins d'accrochage
            public static final float ONE_WAY_PASS_THROUGH_TOLERANCE = 8f * SCALE; // NOUVEAU: Tolérance pour down
        }
    }
    
    public static class DEBUG{
        public static final boolean RENDER_FPS_UPS = true;  // Afficher FPS/UPS à l'écran
    }
    

    public static void SetLanguage (String name){
        language = name;
    }
}