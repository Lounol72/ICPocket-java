package icmon;

import utilz.t_Categ;
import utilz.t_Type;

import java.util.Random;

import static utilz.Constants.ICMONS.TYPE_NUMBER;

public class Move {
    private int id;                 /**< id de l'attaque. */
    private String name;            /**< nom de l'attaque. */
    private int power;              /**< puissance de l'attaque. */
    private t_Type type;            /**< type de l'attaque. */
    private t_Categ categ;          /**< catégorie de l'attaque. */
    private int accuracy;           /**< précision de l'attaque. */
    private int current_pp;         /**< points de pouvoir actuel de l'attaque. */
    private int max_pp;             /**< pp de l'attaque. */
    private int priority_lvl;       /**< priorité de l'attaque. */
    private int target;             /**< 0: ennemi, 1: soi. */
    private int ind_secEffect;      /**< 0: pas d'effet secondaire, 1: effet secondaire. */
    private int probability;        /**< probabilité de l'effet secondaire. */
    private int value_effect;       /**< Puissance de l'effet. */
    private int effect_modifier;    /**< Modificateur de l'effet. */

    public Move() {
        // DEBUG
        generateRndMove();
    }
    public Move(int id, String name, int power, t_Type type, t_Categ categ, int accuracy, int pp,int max_pp ,int priority_lvl, int target, int ind_secEffect, int probability, int value_effect, int effect_modifier) {
        this.id = id;
        this.name = name;
        this.power = power;
        this.type = type;
        this.categ = categ;
        this.accuracy = accuracy;
        this.current_pp = pp;
        this.max_pp = max_pp;
        this.priority_lvl = priority_lvl;
        this.target = target;
        this.ind_secEffect = ind_secEffect;
        this.probability = probability;
        this.value_effect = value_effect;
        this.effect_modifier = effect_modifier;
    }

    private void generateRndMove() {
        Random rnd = new Random();

        name = "Move test";
        power = rnd.nextInt(0,120) + 30;
        categ = (rnd.nextInt(0,2) == 0) ? t_Categ.physical : t_Categ.special;
        type = t_Type.fromValue(rnd.nextInt(TYPE_NUMBER-1) + 1);
        accuracy = rnd.nextInt(0,71) + 30;
        max_pp = rnd.nextInt(0,26) + 5;
        priority_lvl = rnd.nextInt(0,8);
    }

    private void generateMove(int line){
        // Move à partir d'un fichier

    }

    public void InfoDisplay() {
        String separator = "=".repeat(40);
        System.out.println(separator);
        System.out.println("         MOVE INFORMATION         ");
        System.out.println(separator);
        
        // Informations de base
        System.out.printf("%-15s: %s%n", "Name", name);
        System.out.printf("%-15s: %d%n", "Power", power);
        System.out.printf("%-15s: %s%n", "Type", type);
        
        // Statistiques
        System.out.printf("%-15s: %d%n", "Accuracy", accuracy);
        System.out.printf("%-15s: %d%n", "Max PP", max_pp);
        System.out.printf("%-15s: %d%n", "Priority", priority_lvl);
        
        // Effets et cibles
        System.out.printf("%-15s: %s%n", "Target", target);
        System.out.printf("%-15s: %s%n", "Sec Effect", ind_secEffect);
        System.out.printf("%-15s: %d%%%n", "Probability", probability);
        System.out.printf("%-15s: %d%n", "Value Effect", value_effect);
        System.out.printf("%-15s: %s%n", "Effect Modifier", effect_modifier);
        
        System.out.println(separator);
    }

    public boolean useMove(){
        if (current_pp >0){
            current_pp--;
            return true;
        }
        return false;
    }

    public boolean doesHit(){
        return Math.random()  * 100< probability;
    }

    public boolean applySecondaryEffect(int secondaryEffectChance){
        return Math.random() * 100 < secondaryEffectChance;
    }
    public void resetMaxPP(){
        current_pp = max_pp;
    }

    // Getters and Setters

    public int getPower() {return power;}
    public void setPower( int power ) {this.power = power;}
    public String getName() {return name;}
    public void setName( String name ) {this.name = name;}
    public int getId() {return id;}
    public void setId( int id ) {this.id = id;}
    public t_Categ getCateg() {return categ;}
    public void setCateg( t_Categ categ ) {this.categ = categ;}
    public int getAccuracy() {return accuracy;}
    public void setAccuracy( int accuracy ) {this.accuracy = accuracy;}
    public int getCurrent_pp() {return current_pp;}
    public void setCurrent_pp( int current_pp ) {this.current_pp = current_pp;}
    public int getEffect_modifier() {return effect_modifier;}
    public void setEffect_modifier( int effect_modifier ) {this.effect_modifier = effect_modifier;}
    public int getInd_secEffect() {return ind_secEffect;}
    public void setInd_secEffect( int ind_secEffect ) {this.ind_secEffect = ind_secEffect;}
    public int getMax_pp() {return max_pp;}
    public void setMax_pp( int max_pp ) {this.max_pp = max_pp;}
    public int getPriority_lvl() {return priority_lvl;}
    public void setPriority_lvl( int priority_lvl ) {this.priority_lvl = priority_lvl;}
    public int getProbability() {return probability;}
    public void setProbability( int probability ) {this.probability = probability;}
    public int getTarget() {return target;}
    public void setTarget( int target ) {this.target = target;}
    public t_Type getType() {return type;}
    public void setType( t_Type type ) {this.type = type;}
    public int getValue_effect() {return value_effect;}
    public void setValue_effect( int value_effect ) {this.value_effect = value_effect;}
}