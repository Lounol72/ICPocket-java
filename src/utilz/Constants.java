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
        

        public static final int ANI_SPEED = 25;
        public static final float PLAYER_SPEED_RUN = SCALE;
        public static final float JUMP_SPEED_MAX = -5f * SCALE;
        public static final float FALL_SPEED_AFTER_COLLISION = 0.5f * SCALE;
        public static final float GRAVITY = 0.02f * SCALE;

        public static class HITBOX{
            private static final int HITBOX_WIDTH_DEFAULT = 19;
            private static final int HITBOX_HEIGHT_DEFAULT = 32;
            public static final int HITBOX_WIDTH =(int) (HITBOX_WIDTH_DEFAULT * SCALE);
            public static final int HITBOX_HEIGHT =(int) (HITBOX_HEIGHT_DEFAULT * SCALE);
        }

        public static final float MAX_AIR_SPEED = 3f * SCALE;
        
        // Nouvelles constantes pour le système de vecteurs
        public static final float ACCELERATION = 0.4f * SCALE;      // Force d'accélération
        public static final float MAX_SPEED_X = 3f * SCALE;
        public static final float MAX_SPEED_Y = 10f * SCALE;
        public static final float AIR_RESISTANCE = 0.98f;           // 2% de ralentissement/frame en l'air
        public static final float GROUND_FRICTION = 0.88f;         // 12% de ralentissement/frame au sol
        public static final float MASS = 0.8f; // Masse du joueur pour les calculs de force
        
        // Jump mechanics
        public static final float JUMP_FORCE = -8f * SCALE;
        public static final float JUMP_CUT_MULTIPLIER = 0.5f;      // Réduction vélocité si relâché
        public static final float APEX_THRESHOLD = 1.5f * SCALE;   // Seuil pour apex bonus
        public static final float APEX_GRAVITY_MULT = 0.5f;        // Gravité réduite à l'apex
        public static final float APEX_ACCEL_MULT = 1.5f;          // Accélération horizontale à l'apex
        public static final float FAST_FALL_MULT = 2.0f;           // Multiplicateur chute rapide
        public static final int COYOTE_TIME_FRAMES = 6;            // Frames de coyote time
        public static final float MAX_FALL_SPEED = 8f * SCALE;     // Vitesse chute max
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
            public static final float ONE_WAY_TOLERANCE = 5f * SCALE;
        }
    }
    

    public static void SetLanguage (String name){
        language = name;
    }
}