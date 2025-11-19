package utilz;

/**
 * This class contains constants and utility methods used throughout the
 * application.
 * It provides nested classes organizing these constants
 * into logical groupings such as battle mechanics, type charts, stats, and
 * more.
 */
public class Constants {
    public final static float SCALE = 2.0f; // Facteur d'échelle global
    public static String language = "en";

    public static class PLAYER {
        // === PHYSICS DEBUG ===
        public static boolean DEBUG_PHYSICS = false; // Activer logs physique

        // === VITESSES LIMITES ===
        // ~5-6 unités/s horizontal, ~15-20 unités/s vertical
        public static float MAX_SPEED_X = 2f * SCALE; // Vitesse horizontale max
        public static float MAX_FALL_SPEED = 6f * SCALE; // Chute plus rapide type HK
        public static float MAX_RISE_SPEED = 8f * SCALE; // Cohérente avec JUMP_FORCE plus forte

        // === GRAVITÉ (toujours strictement limitée) ===
        public static float GRAVITY = 0.15f * SCALE; // Gravité plus douce pour saut plus haut
        public static float GRAVITY_MULTIPLIER_BASE = 1.0f; // Multiplicateur par défaut

        // === MASSE ET FORCES ===
        public static float MASS = 0.8f; // Masse légère = réactif
        public static float ACCELERATION = 0.2f * SCALE; // Accélération horizontale

        // === SAUT (Hollow Knight: saut court et réactif) ===
        public static float JUMP_FORCE = -4f * SCALE; // Force de saut
        public static float JUMP_CUT_MULTIPLIER = 0.5f; // Coupure saut si relâché

        // === APEX (contrôle au sommet du saut) ===
        public static float APEX_THRESHOLD = 2.5f * SCALE; // Seuil plus large pour apex control
        public static float APEX_GRAVITY_MULT = 0.3f; // Plus de floatiness au sommet
        public static float APEX_ACCEL_MULT = 1.5f; // Contrôle renforcé à l'apex

        // === RÉSISTANCES ===
        public static float AIR_RESISTANCE = 0.98f; // Résistance air (2%)
        public static float GROUND_FRICTION = 0.88f; // Friction sol (12%)

        // === CHUTE RAPIDE ===
        public static float FAST_FALL_MULT = 1.25f; // Fast fall (appui bas)

        // === COYOTE TIME ===
        public static int COYOTE_TIME_FRAMES = 3; // Frames de grâce après quitter sol

        // === JUMP RELEASE ===
        public static float JUMP_MAX_TIME = 0.8f * SCALE; // Temps max de saut

        // === ANIMATIONS ===
        public static int ANI_SPEED = 25;
        public static int ATTACK_ANI_SPEED = 10;
        public static float PLAYER_SPEED_RUN = SCALE;
        public static float JUMP_SPEED_MAX = -5f * SCALE;
        public static float FALL_SPEED_AFTER_COLLISION = 0.5f * SCALE;
        public static float MAX_AIR_SPEED = 3f * SCALE;
        public static float MAX_SPEED_Y = 10f * SCALE;

        // DASH
        public static float DASH_SPEED = 24f * SCALE; // vitesse appliquée pendant le dash

        public static class HITBOX {
            private static final int HITBOX_WIDTH_DEFAULT = 19;
            private static final int HITBOX_HEIGHT_DEFAULT = 32;
            public static final int HITBOX_WIDTH = (int) (HITBOX_WIDTH_DEFAULT * SCALE);
            public static final int HITBOX_HEIGHT = (int) (HITBOX_HEIGHT_DEFAULT * SCALE);
        }

        public static class SPRITE {
            // ================================
            // DIMENSIONS DES SPRITES
            // ================================
            public static final int SPRITE_WIDTH_DEFAULT = 48;
            public static final int SPRITE_HEIGHT_DEFAULT = 48;
            public static final int ATTACK_SPRITE_WIDTH_DEFAULT = 80;
            public static final int NORMAL_SPRITE_WIDTH = (int) (SPRITE_WIDTH_DEFAULT * SCALE);
            public static final int NORMAL_SPRITE_HEIGHT = (int) (SPRITE_HEIGHT_DEFAULT * SCALE);
            public static final int ATTACK_SPRITE_WIDTH = (int) (ATTACK_SPRITE_WIDTH_DEFAULT * SCALE);
            public static final int ATTACK_SPRITE_HEIGHT = (int) (SPRITE_HEIGHT_DEFAULT * SCALE);

            // ================================
            // CONSTANTES DE RENDU
            // ================================
            public static final float X_DRAW_OFFSET = 22 * SCALE;
            public static final float Y_DRAW_OFFSET = 20 * SCALE;

            // === OFFSETS POUR ANIMATIONS D'ATTAQUE (80x48) ===
            // Offset horizontal ajusté pour les sprites plus larges (80px vs 48px)
            // Différence de largeur: 80 - 48 = 32px, donc offset supplémentaire de 16px
            public static final float ATTACK_X_DRAW_OFFSET = (22 + 16) * SCALE; // 38 * SCALE
            public static final float ATTACK_Y_DRAW_OFFSET = 20 * SCALE; // Même offset vertical
        }
    }

    public static class UI {
        public static class BUTTONS {
            public static final int DEFAULT_WIDTH = 192;
            public static final int DEFAULT_HEIGHT = 31;
            public static final int WIDTH = (int) (DEFAULT_WIDTH * SCALE);
            public static final int HEIGHT = (int) (DEFAULT_HEIGHT * SCALE);
        }
    }

    public static class PATHS {
        public static final String ROOT = "res/";

        public static final String DATA = ROOT + "data/";
        public static final String DATA_FILE = DATA + "data.json";
        public static final String LANGUAGE_FILE = DATA + "language.json";

    }

    public static class WORLD {
        public final static int TILES_DEFAULT_SIZE = 32; // Taille de base d'une tuile
        public final static int TILES_IN_WIDTH = 26; // Nombre de tuiles en largeur
        public final static int TILES_IN_HEIGHT = 14; // Nombre de tuiles en hauteur
        public final static int TILES_SIZE = (int) (TILES_DEFAULT_SIZE * SCALE); // Taille d'une tuile après mise à
                                                                                 // l'échelle
        public final static int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH; // Largeur totale du jeu
        public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT; // Hauteur totale du jeu

        public static class ONE_WAY_PLATFORMS {
            public static final int[] ONE_WAY_TILE_IDS = { 6, 13, 19, 20 };
            public static final float ONE_WAY_TOLERANCE = 3f * SCALE; // Moins d'accrochage
            public static final float ONE_WAY_PASS_THROUGH_TOLERANCE = 8f * SCALE; // Tolérance pour down
        }
    }

    public static class DEBUG {
        public static final boolean RENDER_FPS_UPS = true; // Afficher FPS/UPS à l'écran
    }

    public static void SetLanguage(String name) {
        language = name;
    }
}