package utilz;

import icmon.Move;

import static utilz.t_Categ.physical;
import static utilz.t_Type.noType;

/**
 * This class contains constants and utility methods used throughout the application.
 * It provides nested classes organizing these constants
 * into logical groupings such as battle mechanics, type charts, stats, and more.
 */
public class Constants {
    public final static float SCALE = 2.0f;                              // Facteur d'échelle global
    public static String language  = "en";

    public static class UI{
        public static class BUTTONS{
            public static final int DEFAULT_WIDTH = 192;
            public static final int DEFAULT_HEIGHT = 31;
            public static final int WIDTH = (int) ((int) (DEFAULT_WIDTH * SCALE) * 1f);
            public static final int HEIGHT = (int) ((int) (DEFAULT_HEIGHT * SCALE) * 1f);
        }
    }
    public static class PATHS{
        public static final String ROOT = "res/";

        public static final String DATA = ROOT + "data/";
        public static final String DATA_FILE = DATA + "data.json";
        public static final String LANGUAGE_FILE = DATA + "language.json";


    }
    public static class WORLD{
        public final static int TILES_DEFAULT_SIZE = 16;                     // Taille de base d'une tuile
        public final static int TILES_IN_WIDTH = 52;                         // Nombre de tuiles en largeur
        public final static int TILES_IN_HEIGHT = 28;                        // Nombre de tuiles en hauteur
        public final static int TILES_SIZE = (int) (TILES_DEFAULT_SIZE * (SCALE));  // Taille d'une tuile après mise à l'échelle
        public final static int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;    // Largeur totale du jeu
        public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;  // Hauteur totale du jeu
        public static class PLAYER{
            public static final int IDLE = 0;
            public static final int KNEEL = 1;
            public static final int RUN = 2;
            public static final int JUMP = 3;
            public static final int FALL = 4;

            public final static int PLAYER_DEFAULT_WIDTH = 32;
            public final static int PLAYER_DEFAULT_HEIGHT  = 32;
            public final static int PLAYER_WIDTH = (int) (PLAYER_DEFAULT_WIDTH  * SCALE);
            public final static int PLAYER_HEIGHT = (int) (PLAYER_DEFAULT_HEIGHT  * SCALE);
        }
    }
    public static class ICMONS{
        public static final int TYPE_NUMBER = 9;
        public static final int IS_ABSCENT  =-1;
        public static final int NUMBER_OF_MOVES = 49;
        public static final int NEUTRAL_STAT_CHANGE = 6;

        public static class Combat{
            public static final int STRUGGLE_MOVE_INDEX = -10;
            public static final int CONFUSED_MOVE_INDEX = -20;

            public static final Move STRUGGLE_MOVE = createStruggleMove();
            public static final Move CONFUSED_MOVE = createConfusedMove();

            private static Move createStruggleMove(){
                return  new Move(-1,"lutte",50,noType,physical,200,1,1,0,1,2,100,25,0);
            }
            private static Move createConfusedMove(){
                return new Move (-2,"Confus",40,noType,physical,200,1,1,0,1,-1,0,0,0);
            }
        }



        public static boolean criticalHitFlag = false;
        public static double moveEffectivenessFlag = 1.0;
        public static boolean secondaryEffectHappenedFlag = false;

        public static final double LEVEL_MULTIPLIER = 0.4;
        public static final int DAMAGE_DIVISOR = 50;
        public static final int RANDOM_MIN = 85;

        public static class STATS{
            public static final int PV  =  0;
            public static final int ATT =  1;
            public static final int DEF =  2;
            public static final int SPA =  3;
            public static final int SPD =  4;
            public static final int SPE =  5;
        }

        public static class TypeChart {
            private static final double[][] CHART ={
                    /*defender*/
                    /*offender*//*noType  feu     plante   eau     electrique  malware  data    net		waifu*/
                    /*notype*/  {1.,     1.,     	1.,     1.,     1.,         1.,      1.,     1.,		1.	},
                    /*feu*/     {1.,     0.5,    	2.,     0.5,    1.,         2,       0.5,    2.,		2.	},
                    /*plante*/  {1.,     0.5,    	0.5,    2.,     1.,         0.5,     2.,     1.,		1.	},
                    /*eau*/     {1.,     2.,     	0.5,    0.5,    1.,         1.,      2.,     1.,		1.	},
                    /*electrique*/ {1.,  1.,     	1.,     1.,     0.5,        2.,      2.,     2.,		0.5	},
                    /*malware*/ {1.,     0.5,    	2.,     1.,     0.5,        2.,      2.,     2.,		1.	},
                    /*data*/    {1.,     2.,     	0.5,    0.5,    0.5,        0.5,     0.5,     2.,		1.	},
                    /*net*/     {1.,	 1,    		1,    	1,    	2,        	0.5,     0.5,    0.5,		0.5	},
                    /*waifu*/	{1.,	0.5,		1.,		1.,		1.,			1.,		1.,		2.,			2.	}
            };
            public static double getEffectiveness(int attackerType, int defenderType) {
                return CHART[attackerType][defenderType];
            }
        }

        public static class Nature {
            private static final String[] NAMES = {
                    "Hardi", "Solo", "Rigide", "Mauvais", "Brave", "Assuré", "Docile", "Malin", "Lâche", "Relax",
                    "Modeste", "Doux", "Pudique", "Foufou", "Discret", "Calme", "Gentil", "Prudent", "Bizarre", "Malpoli",
                    "Timide", "Pressé", "Jovial", "Naïf", "Sérieux"
            };

            private static final float[][] COEFFS = {
                    {1, 1, 1, 1, 1, 1},       {1.1f, 0.9f, 1, 1, 1, 1}, {1.1f, 1, 0.9f, 1, 1, 1}, {1.1f, 1, 1, 0.9f, 1, 1}, {1.1f, 1, 1, 1, 0.9f, 1},
                    {0.9f, 1.1f, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1},       {1, 1.1f, 0.9f, 1, 1, 1}, {1, 1.1f, 1, 0.9f, 1, 1}, {1, 1.1f, 1, 1, 0.9f, 1},
                    {0.9f, 1, 1.1f, 1, 1, 1}, {1, 0.9f, 1.1f, 1, 1, 1}, {1, 1, 1, 1, 1, 1},       {1, 1, 1.1f, 0.9f, 1, 1}, {1, 1, 1.1f, 1, 0.9f, 1},
                    {0.9f, 1, 1, 1.1f, 1, 1}, {1, 0.9f, 1, 1.1f, 1, 1}, {1, 1, 0.9f, 1.1f, 1, 1}, {1, 1, 1, 1, 1, 1},       {1, 1, 1, 1.1f, 0.9f, 1},
                    {0.9f, 1, 1, 1, 1.1f, 1}, {1, 0.9f, 1, 1, 1.1f, 1}, {1, 1, 0.9f, 1, 1.1f, 1}, {1, 1, 1, 0.9f, 1.1f, 1}, {1, 1, 1, 1, 1, 1}
            };

            public static String getNatureName(int index) {
                return NAMES[index];
            }

            public static float[] getNatureCoefficients(int index) {
                if (index < 0 || index >=COEFFS.length)
                    throw new IllegalArgumentException("Index hors limites");
                return COEFFS[index];
            }

            public static int getNatureCount() {
                return NAMES.length;
            }
        }
        public static class StatVariations{
            private static final double[] VARIATIONS = {0.25,2./7,1./3,2./5,0.5,2./3,1,1.5,2,2.5,3,3.5,4};

            public static double getStatVariation(int index) {
                if (index < 0 || index >= VARIATIONS.length)
                    throw new IllegalArgumentException("Index hors limites");
                return VARIATIONS[index];
            }
        }

        public static class MULTIPLIERS{
            public static final double TRAINER_BONUS = 1.5f;
            public static final double EXP_BOOST = 1.5f;
            public static final int BASE_EXP_MULTIPLIER = 3;
            public static final int EXP_DIVISOR = 7;
        }
    }

    public static void SetLanguage (String name){
        language = name;
    }
}
